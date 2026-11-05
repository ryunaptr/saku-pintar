from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database import Base

class Siswa(Base):
    __tablename__ = "siswa"

    id         = Column(Integer, primary_key=True, index=True)
    nama       = Column(String(100), nullable=False)
    kelas      = Column(String(20), nullable=False)   # "Kelas 7/8/9"
    foto_url   = Column(String(255), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    parameter      = relationship("ParameterInstruksional", back_populates="siswa", uselist=False)
    materi_adaptif = relationship("MateriAdaptif", back_populates="siswa")
    sesi_latihan   = relationship("SesiLatihan", back_populates="siswa")
    gamifikasi     = relationship("Gamifikasi", back_populates="siswa", uselist=False)


class ParameterInstruksional(Base):
    __tablename__ = "parameter_instruksional"

    id          = Column(Integer, primary_key=True, index=True)
    siswa_id    = Column(Integer, ForeignKey("siswa.id"), unique=True, nullable=False)

    # KI-01 s.d. KI-07
    ki_01_tingkat_atensi         = Column(String(50), nullable=False)
    ki_02_kemampuan_kebahasaan   = Column(String(50), nullable=False)
    ki_03_kapasitas_memori       = Column(String(50), nullable=False)
    ki_04_preferensi_persepsi    = Column(String(50), nullable=False)
    ki_05_metakognitif           = Column(String(50), nullable=False)
    ki_06_jenjang_kelas          = Column(String(20), nullable=False)
    ki_07_tingkatan_tunagrahita  = Column(String(20), nullable=False)

    updated_at = Column(DateTime(timezone=True), server_default=func.now())

    siswa = relationship("Siswa", back_populates="parameter")