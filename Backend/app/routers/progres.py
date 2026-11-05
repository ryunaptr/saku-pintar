from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from app.database import get_db
from app.models.siswa import Siswa
from app.models.progres import SesiLatihan, Gamifikasi
from app.schemas.progres import (
    SesiLatihanCreate, SesiLatihanResponse, GamifikasiResponse
)
from app.utils.auth import get_current_guru

router = APIRouter(prefix="/progres", tags=["Progres"])

POIN_PER_BENAR      = 10
MIN_BENAR_BADGE     = 4   # 4/5 benar = dapat badge

# ── Endpoint tanpa auth (diakses mode siswa) ──────────────────────────

@router.post("/latihan", response_model=SesiLatihanResponse)
def simpan_sesi_latihan(data: SesiLatihanCreate, db: Session = Depends(get_db)):
    """
    Simpan hasil sesi latihan soal.
    Otomatis update poin dan badge di tabel gamifikasi.
    """
    sesi = SesiLatihan(
        siswa_id      = data.siswa_id,
        kondisi_id    = data.kondisi_id,
        label_kondisi = data.label_kondisi,
        jumlah_benar  = data.jumlah_benar,
        skor          = data.skor,
        soal_json     = data.soal_json
    )
    db.add(sesi)

    # Update gamifikasi
    gamifikasi = db.query(Gamifikasi).filter(
        Gamifikasi.siswa_id == data.siswa_id
    ).first()
    if not gamifikasi:
        gamifikasi = Gamifikasi(siswa_id=data.siswa_id)
        db.add(gamifikasi)

    gamifikasi.total_poin += data.jumlah_benar * POIN_PER_BENAR

    if data.jumlah_benar >= MIN_BENAR_BADGE:
        badge_field = f"badge_{data.kondisi_id.lower()}"
        if hasattr(gamifikasi, badge_field):
            setattr(gamifikasi, badge_field, True)

    db.commit()
    db.refresh(sesi)
    return sesi

@router.get("/{siswa_id}/gamifikasi", response_model=GamifikasiResponse)
def get_gamifikasi(siswa_id: int, db: Session = Depends(get_db)):
    """Poin dan badge siswa — untuk layar Pencapaianku."""
    gami = db.query(Gamifikasi).filter(Gamifikasi.siswa_id == siswa_id).first()
    if not gami:
        return GamifikasiResponse(
            total_poin=0,
            badge_a=False, badge_b=False, badge_c=False,
            badge_d=False, badge_e=False, badge_f=False
        )
    return gami

# ── Endpoint dengan auth (diakses mode guru) ──────────────────────────

@router.get("/{siswa_id}/latihan", response_model=List[SesiLatihanResponse])
def get_riwayat_latihan(
    siswa_id: int,
    db: Session = Depends(get_db)
):
    return db.query(SesiLatihan).filter(
        SesiLatihan.siswa_id == siswa_id
    ).order_by(SesiLatihan.created_at.desc()).all()

@router.get("/{siswa_id}/ringkasan")
def get_ringkasan(
    siswa_id: int,
    db: Session = Depends(get_db),
    _: None = Depends(get_current_guru)
):
    """Ringkasan progres siswa — untuk layar Pantau Progres guru."""
    siswa = db.query(Siswa).filter(Siswa.id == siswa_id).first()
    if not siswa:
        raise HTTPException(status_code=404, detail="Siswa tidak ditemukan")

    gami = db.query(Gamifikasi).filter(Gamifikasi.siswa_id == siswa_id).first()
    sesi = db.query(SesiLatihan).filter(SesiLatihan.siswa_id == siswa_id).all()

    kompetensi_dikuasai = 0
    if gami:
        badges = [gami.badge_a, gami.badge_b, gami.badge_c,
                  gami.badge_d, gami.badge_e, gami.badge_f]
        kompetensi_dikuasai = sum(1 for b in badges if b)

    return {
        "siswa_id"           : siswa_id,
        "nama"               : siswa.nama,
        "kelas"              : siswa.kelas,
        "total_poin"         : gami.total_poin if gami else 0,
        "kompetensi_dikuasai": kompetensi_dikuasai,
        "total_badge"        : kompetensi_dikuasai,
        "jumlah_sesi"        : len(sesi)
    }