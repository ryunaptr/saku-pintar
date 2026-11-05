from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, status
from fastapi.staticfiles import StaticFiles
from sqlalchemy.orm import Session
from typing import List
import os, shutil
from datetime import datetime, timezone, timedelta
from app.database import get_db
from app.models.siswa import Siswa, ParameterInstruksional
from app.models.progres import Gamifikasi
from app.schemas.siswa import (
    SiswaCreate, SiswaResponse,
    ParameterInstruksionalBase,
    ParameterInstruksionalResponse
)
from app.utils.auth import get_current_guru

router = APIRouter(prefix="/siswa", tags=["Siswa"])

FOTO_DIR = "uploads/foto"
os.makedirs(FOTO_DIR, exist_ok=True)
WIB = timezone(timedelta(hours=7))

@router.get("/", response_model=List[SiswaResponse])
def get_all_siswa(db: Session = Depends(get_db)):
    return db.query(Siswa).order_by(Siswa.nama).all()

@router.get("/{siswa_id}", response_model=SiswaResponse)
def get_siswa(siswa_id: int, db: Session = Depends(get_db)):
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa:
        raise HTTPException(status_code=404, detail="Siswa tidak ditemukan")
    return siswa

@router.post(
    "/upload-foto",
    dependencies=[Depends(get_current_guru)]
)
async def upload_foto_siswa(file: UploadFile = File(...)):
    """
    Upload foto profil siswa. Kembalikan URL yang bisa diakses langsung.
    """
    ekstensi = os.path.splitext(file.filename or "foto.jpg")[1].lower()
    if ekstensi not in [".jpg", ".jpeg", ".png", ".webp"]:
        raise HTTPException(status_code=400, detail="Format foto harus JPG, PNG, atau WEBP")

    timestamp = datetime.now(WIB).strftime("%Y%m%d_%H%M%S_%f")
    nama_file = f"foto_{timestamp}{ekstensi}"
    file_path = os.path.join(FOTO_DIR, nama_file)

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    foto_url = f"http://172.20.10.2:8000/uploads/foto/{nama_file}"
    return {"foto_url": foto_url}

@router.post(
    "/",
    response_model=SiswaResponse,
    status_code=status.HTTP_201_CREATED,
    dependencies=[Depends(get_current_guru)]
)
def create_siswa(data: SiswaCreate, db: Session = Depends(get_db)):
    siswa_baru = Siswa(
        nama     = data.nama,
        kelas    = data.kelas,
        foto_url = data.foto_url
    )
    db.add(siswa_baru)
    db.flush()
    db.add(Gamifikasi(siswa_id=siswa_baru.id))
    db.commit()
    db.refresh(siswa_baru)
    return siswa_baru

@router.put(
    "/{siswa_id}",
    response_model=SiswaResponse,
    dependencies=[Depends(get_current_guru)]
)
def update_siswa(siswa_id: int, data: SiswaCreate, db: Session = Depends(get_db)):
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa:
        raise HTTPException(status_code=404, detail="Siswa tidak ditemukan")
    siswa.nama     = data.nama
    siswa.kelas    = data.kelas
    siswa.foto_url = data.foto_url
    db.commit()
    db.refresh(siswa)
    return siswa

@router.put(
    "/{siswa_id}/parameter",
    response_model=ParameterInstruksionalResponse,
    dependencies=[Depends(get_current_guru)]
)
def simpan_parameter(
    siswa_id: int,
    data: ParameterInstruksionalBase,
    db: Session = Depends(get_db)
):
    param = db.query(ParameterInstruksional).filter(
        ParameterInstruksional.siswa_id == siswa_id
    ).first()

    if param:
        param.ki_01_tingkat_atensi        = data.ki_01_tingkat_atensi
        param.ki_02_kemampuan_kebahasaan  = data.ki_02_kemampuan_kebahasaan
        param.ki_03_kapasitas_memori      = data.ki_03_kapasitas_memori
        param.ki_04_preferensi_persepsi   = data.ki_04_preferensi_persepsi
        param.ki_05_metakognitif          = data.ki_05_metakognitif
        param.ki_06_jenjang_kelas         = data.ki_06_jenjang_kelas
        param.ki_07_tingkatan_tunagrahita = data.ki_07_tingkatan_tunagrahita
    else:
        param = ParameterInstruksional(
            siswa_id                     = siswa_id,
            ki_01_tingkat_atensi         = data.ki_01_tingkat_atensi,
            ki_02_kemampuan_kebahasaan   = data.ki_02_kemampuan_kebahasaan,
            ki_03_kapasitas_memori       = data.ki_03_kapasitas_memori,
            ki_04_preferensi_persepsi    = data.ki_04_preferensi_persepsi,
            ki_05_metakognitif           = data.ki_05_metakognitif,
            ki_06_jenjang_kelas          = data.ki_06_jenjang_kelas,
            ki_07_tingkatan_tunagrahita  = data.ki_07_tingkatan_tunagrahita
        )
        db.add(param)

    db.commit()
    db.refresh(param)
    return param

@router.get("/{siswa_id}/materi-status")
def get_materi_status(siswa_id: int, db: Session = Depends(get_db)):
    from sqlalchemy import text
    hasil = db.execute(
        text("SELECT kondisi_id FROM materi_adaptif WHERE siswa_id = :id"),
        {"id": siswa_id}
    ).fetchall()
    kondisi_tersedia = [row[0] for row in hasil]
    return {"kondisi_tersedia": kondisi_tersedia}

@router.delete(
    "/{siswa_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    dependencies=[Depends(get_current_guru)]
)
def delete_siswa(siswa_id: int, db: Session = Depends(get_db)):
    from sqlalchemy import text
    
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa:
        raise HTTPException(status_code=404, detail="Siswa tidak ditemukan")
    
    # Hapus semua relasi dulu dengan urutan yang benar
    db.execute(text("DELETE FROM materi_adaptif WHERE siswa_id = :id"), {"id": siswa_id})
    db.execute(text("DELETE FROM sesi_latihan WHERE siswa_id = :id"),   {"id": siswa_id})
    db.execute(text("DELETE FROM gamifikasi WHERE siswa_id = :id"),     {"id": siswa_id})
    db.execute(text("DELETE FROM parameter_instruksional WHERE siswa_id = :id"), {"id": siswa_id})
    
    db.delete(siswa)
    db.commit()