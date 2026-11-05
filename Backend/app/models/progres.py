from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, JSON, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database import Base

class SesiLatihan(Base):
    __tablename__ = "sesi_latihan"

    id            = Column(Integer, primary_key=True, index=True)
    siswa_id      = Column(Integer, ForeignKey("siswa.id"), nullable=False)
    kondisi_id    = Column(String(5), nullable=False)
    label_kondisi = Column(String(100), nullable=False)
    jumlah_benar  = Column(Integer, default=0)   # dari 5
    skor          = Column(Integer, default=0)    # dari 100
    soal_json     = Column(JSON, nullable=True)   # soal yang digenerate LLM
    created_at    = Column(DateTime(timezone=True), server_default=func.now())

    siswa = relationship("Siswa", back_populates="sesi_latihan")


class Gamifikasi(Base):
    __tablename__ = "gamifikasi"

    id          = Column(Integer, primary_key=True, index=True)
    siswa_id    = Column(Integer, ForeignKey("siswa.id"), unique=True, nullable=False)
    total_poin  = Column(Integer, default=0)

    # Badge per kompetensi — True jika sudah diperoleh
    badge_a = Column(Boolean, default=False)
    badge_b = Column(Boolean, default=False)
    badge_c = Column(Boolean, default=False)
    badge_d = Column(Boolean, default=False)
    badge_e = Column(Boolean, default=False)
    badge_f = Column(Boolean, default=False)

    updated_at = Column(DateTime(timezone=True), server_default=func.now())

    siswa = relationship("Siswa", back_populates="gamifikasi")