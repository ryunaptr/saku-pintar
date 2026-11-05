from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, status
from sqlalchemy.orm import Session
from typing import Any
from app.database import get_db
from app.models.siswa import Siswa, ParameterInstruksional
from app.models.materi import Materi, MateriChunk, MateriAdaptif
from app.utils.auth import get_current_guru
from app.services.profiling_pipeline import ekstrak_parameter_dari_pdf
from app.services.adaptasi_pipeline import self_refine_materi, self_refine_soal
from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Form, status

router = APIRouter(prefix="/pipeline", tags=["Pipeline AI"])


# ── Pipeline Profiling ────────────────────────────────────────────────

@router.post(
    "/profiling",
    dependencies=[Depends(get_current_guru)]
)
async def profiling_asesmen(file: UploadFile = File(...), kelas: str = Form(default="")):
    """
    Kirim PDF laporan psikologis → Claude ekstrak 7 parameter instruksional.
    Digunakan saat guru menambahkan siswa baru.
    """
    if not file.filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=400, detail="File harus berformat PDF")

    pdf_bytes = await file.read()
    try:
        parameter = ekstrak_parameter_dari_pdf(pdf_bytes, kelas=kelas)
        return {"status": "ok", "parameter": parameter}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Gagal memproses dokumen: {str(e)}"
        )


# ── Pipeline Adaptasi Konten ──────────────────────────────────────────

@router.get("/konten/{siswa_id}/{kondisi_id}")
def get_konten_adaptif(
    siswa_id: int,
    kondisi_id: str,
    db: Session = Depends(get_db)
):
    """
    Ambil materi adaptif untuk siswa tertentu dan kondisi tertentu.
    Jika belum ada → generate via Self-Refine LLM dan simpan.
    """
    # Cek cache database
    materi_cache = db.query(MateriAdaptif).filter(
        MateriAdaptif.siswa_id   == siswa_id,
        MateriAdaptif.kondisi_id == kondisi_id
    ).first()

    if materi_cache:
        return {
            "status"  : "cache",
            "halaman" : materi_cache.konten_json.get("halaman", []),
            "iterasi" : materi_cache.iterasi
        }

    # Ambil data siswa dan parameter
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa or not siswa.parameter:
        raise HTTPException(status_code=404, detail="Siswa atau parameter tidak ditemukan")

    # Baru — filter berdasarkan kondisi_id di chunk itu sendiri
    chunks = db.query(MateriChunk).filter(
        MateriChunk.kondisi_id == kondisi_id
    ).order_by(MateriChunk.urutan).all()

    if not chunks:
        raise HTTPException(
            status_code=404,
            detail=f"Belum ada chunk untuk Kondisi {kondisi_id}. "
                    f"Pastikan dokumen materi sudah diupload dan terproses."
        )

    # Siapkan parameter sebagai dict
    param = siswa.parameter
    parameter_dict = {
        "ki_01_tingkat_atensi"        : param.ki_01_tingkat_atensi,
        "ki_02_kemampuan_kebahasaan"  : param.ki_02_kemampuan_kebahasaan,
        "ki_03_kapasitas_memori"      : param.ki_03_kapasitas_memori,
        "ki_04_preferensi_persepsi"   : param.ki_04_preferensi_persepsi,
        "ki_05_metakognitif"          : param.ki_05_metakognitif,
        "ki_06_jenjang_kelas"         : param.ki_06_jenjang_kelas,
        "ki_07_tingkatan_tunagrahita" : param.ki_07_tingkatan_tunagrahita
    }

    chunks_dict = [
        {"urutan": c.urutan, "isi_chunk": c.isi_chunk, "konteks": c.konteks}
        for c in chunks
    ]

    try:
        # Jalankan Self-Refine
        hasil = self_refine_materi(chunks_dict, parameter_dict, kondisi_id=kondisi_id, max_iterasi=3)

        # Simpan ke database (cache)
        db.add(MateriAdaptif(
            siswa_id         = siswa_id,
            kondisi_id       = kondisi_id,
            konten_json      = hasil["konten"],
            iterasi          = hasil["iterasi"],
            skor_evaluasi    = hasil["skor_evaluasi"]
        ))
        db.commit()

        return {
            "status"  : "generated",
            "halaman" : hasil["konten"].get("halaman", []),
            "iterasi" : hasil["iterasi"]
        }

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Gagal generate materi adaptif: {str(e)}"
        )


# ── Quiz Generator ────────────────────────────────────────────────────

@router.post("/soal/{siswa_id}/{kondisi_id}")
def generate_soal_latihan(
    siswa_id: int,
    kondisi_id: str,
    db: Session = Depends(get_db)
):
    """
    Generate 5 soal latihan baru untuk siswa dan kondisi tertentu.
    Selalu generate fresh agar soal tidak berulang.
    """
    # Validasi siswa dan parameter
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa or not siswa.parameter:
        raise HTTPException(
            status_code=404,
            detail="Siswa atau parameter tidak ditemukan"
        )

    # Ambil chunks berdasarkan kondisi_id (bukan dari materi.kompetensi)
    chunks = db.query(MateriChunk).filter(
        MateriChunk.kondisi_id == kondisi_id
    ).order_by(MateriChunk.urutan).limit(5).all()

    if not chunks:
        raise HTTPException(
            status_code=404,
            detail=f"Belum ada chunk untuk Kondisi {kondisi_id}. "
                   f"Pastikan dokumen materi sudah diupload dan terproses."
        )

    param = siswa.parameter
    parameter_dict = {
        "ki_01_tingkat_atensi"        : param.ki_01_tingkat_atensi,
        "ki_02_kemampuan_kebahasaan"  : param.ki_02_kemampuan_kebahasaan,
        "ki_03_kapasitas_memori"      : param.ki_03_kapasitas_memori,
        "ki_04_preferensi_persepsi"   : param.ki_04_preferensi_persepsi,
        "ki_05_metakognitif"          : param.ki_05_metakognitif,
        "ki_06_jenjang_kelas"         : param.ki_06_jenjang_kelas,
        "ki_07_tingkatan_tunagrahita" : param.ki_07_tingkatan_tunagrahita
    }

    chunks_dict = [
        {"urutan": c.urutan, "isi_chunk": c.isi_chunk, "konteks": c.konteks}
        for c in chunks
    ]

    try:
        hasil = self_refine_soal(chunks_dict, parameter_dict, kondisi_id=kondisi_id, max_iterasi=3)
        return {"status": "ok", "soal": hasil["soal"], "iterasi": hasil["iterasi"]}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Gagal generate soal: {str(e)}"
        )