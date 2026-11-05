from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Form, status
from sqlalchemy.orm import Session
from typing import List
import os, shutil
from datetime import datetime, timezone, timedelta
from app.database import get_db
from app.models.materi import Materi, MateriChunk, MateriAdaptif
from app.schemas.materi import MateriResponse, MateriDetailResponse
from app.utils.auth import get_current_guru
from app.services.knowledge_base_pipeline import proses_contextual_retrieval

router = APIRouter(prefix="/materi", tags=["Materi"])

UPLOAD_DIR = "uploads/materi"
os.makedirs(UPLOAD_DIR, exist_ok=True)
WIB = timezone(timedelta(hours=7))

@router.get("/", response_model=List[MateriResponse])
def get_all_materi(
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    return db.query(Materi).all()

@router.get("/{materi_id}", response_model=MateriDetailResponse)
def get_materi_detail(
    materi_id: int,
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    materi = db.query(Materi).filter(Materi.id == materi_id).first()
    if not materi:
        raise HTTPException(status_code=404, detail="Materi tidak ditemukan")
    return materi

@router.post("/upload", response_model=MateriResponse, status_code=status.HTTP_201_CREATED)
async def upload_materi(
    file: UploadFile = File(...),
    deskripsi: str = Form(default="Buku Materi Aritmetika Sosial"),
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    """
    Upload satu dokumen materi lengkap (semua kondisi A-F dalam satu PDF).
    Claude akan mengidentifikasi otomatis kondisi setiap chunk via Contextual Retrieval.
    """
    if not file.filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=400, detail="File harus berformat PDF")

    # Hapus dokumen lama jika ada
    materi_lama = db.query(Materi).first()
    if materi_lama:
        if os.path.exists(materi_lama.file_path):
            os.remove(materi_lama.file_path)
        db.delete(materi_lama)
        db.query(MateriAdaptif).delete()
        db.commit()

    # Simpan file PDF
    timestamp = datetime.now(WIB).strftime("%Y%m%d_%H%M%S")
    file_path = os.path.join(UPLOAD_DIR, f"materi_{timestamp}.pdf")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # Simpan record materi
    materi_baru = Materi(
        nama_file  = file.filename,
        deskripsi  = deskripsi,
        file_path  = file_path
    )
    db.add(materi_baru)
    db.commit()
    db.refresh(materi_baru)

    # ── Pipeline Knowledge Base ──────────────────────────────────────
    print(f"\n▶ Memulai Contextual Retrieval untuk: {file.filename}")

    hasil_chunks = proses_contextual_retrieval(file_path)

    if not hasil_chunks:
        raise HTTPException(
            status_code=422,
            detail="Gagal mengekstrak teks dari PDF. "
                   "Pastikan PDF berisi teks yang bisa dibaca, bukan hasil scan gambar."
        )

    kondisi_count = {}
    for chunk_data in hasil_chunks:
        kid = chunk_data.get("kondisi_id", "UMUM")
        kondisi_count[kid] = kondisi_count.get(kid, 0) + 1
        db.add(MateriChunk(
            materi_id  = materi_baru.id,
            urutan     = chunk_data["urutan"],
            kondisi_id = kid,
            isi_chunk  = chunk_data["isi_chunk"],
            konteks    = chunk_data["konteks"]
        ))

    db.commit()
    print(f"✓ {len(hasil_chunks)} chunk tersimpan. Distribusi kondisi: {kondisi_count}\n")
    # ────────────────────────────────────────────────────────────────

    return materi_baru

@router.get("/kondisi/summary")
def get_kondisi_summary(
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    """
    Ringkasan status per kondisi — dipakai layar Daftar Materi Android.
    Guru melihat kondisi mana yang sudah tersedia, tanpa tahu detail chunk.
    """
    SEMUA_KONDISI = {
        "A": "Mengenal Jenis Uang",
        "B": "Mengenal Nilai Nominal Uang",
        "C": "Menghitung Uang",
        "D": "Mengetahui Kesetaraan Nilai Uang",
        "E": "Mengidentifikasi Harga Barang",
        "F": "Membandingkan Harga Barang"
    }

    materi = db.query(Materi).first()
    if not materi:
        return {
            "dokumen": None,
            "kondisi": {
                k: {"label": v, "tersedia": False}
                for k, v in SEMUA_KONDISI.items()
            }
        }

    kondisi_status = {}
    for kode, label in SEMUA_KONDISI.items():
        jumlah = db.query(MateriChunk).filter(
            MateriChunk.materi_id  == materi.id,
            MateriChunk.kondisi_id == kode
        ).count()
        kondisi_status[kode] = {
            "label"   : label,
            "tersedia": jumlah > 0
        }

    return {
        "dokumen": {
            "nama_file"     : materi.nama_file,
            "tanggal_upload": materi.tanggal_upload
        },
        "kondisi": kondisi_status
    }


@router.get("/kondisi/{kondisi_id}/preview")
def get_kondisi_preview(
    kondisi_id: str,
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    """
    Preview isi materi per kondisi — ditampilkan saat guru tap salah satu kondisi.
    Disajikan sebagai teks terbaca, bukan format chunk mentah.
    """
    materi = db.query(Materi).first()
    if not materi:
        raise HTTPException(status_code=404, detail="Belum ada dokumen yang diunggah")

    chunks = db.query(MateriChunk).filter(
        MateriChunk.materi_id  == materi.id,
        MateriChunk.kondisi_id == kondisi_id
    ).order_by(MateriChunk.urutan).all()

    if not chunks:
        raise HTTPException(
            status_code=404,
            detail=f"Kondisi {kondisi_id} tidak terdeteksi dalam dokumen"
        )

    # Gabungkan isi materi menjadi teks terbaca — guru tidak melihat "chunk"
    isi_gabungan = "\n\n".join([c.isi_chunk for c in chunks])

    LABEL = {
        "A": "Mengenal Jenis Uang",
        "B": "Mengenal Nilai Nominal Uang",
        "C": "Menghitung Uang",
        "D": "Mengetahui Kesetaraan Nilai Uang",
        "E": "Mengidentifikasi Harga Barang",
        "F": "Membandingkan Harga Barang"
    }

    return {
        "kondisi_id"    : kondisi_id,
        "label"         : LABEL.get(kondisi_id, kondisi_id),
        "nama_dokumen"  : materi.nama_file,
        "isi"           : isi_gabungan
    }

@router.delete("/{materi_id}", status_code=status.HTTP_204_NO_CONTENT,
               dependencies=[Depends(get_current_guru)])
def delete_materi(materi_id: int, db: Session = Depends(get_db)):
    materi = db.query(Materi).filter(Materi.id == materi_id).first()
    if not materi:
        raise HTTPException(status_code=404, detail="Materi tidak ditemukan")
    if os.path.exists(materi.file_path):
        os.remove(materi.file_path)
    db.delete(materi)
    db.commit()