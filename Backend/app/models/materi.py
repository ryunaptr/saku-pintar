from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text, JSON
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database import Base

class Materi(Base):
    __tablename__ = "materi"

    id             = Column(Integer, primary_key=True, index=True)
    nama_file      = Column(String(255), nullable=False)
    deskripsi      = Column(String(255), nullable=True)
    file_path      = Column(String(500), nullable=False)
    tanggal_upload = Column(DateTime(timezone=True), server_default=func.now())

    chunks = relationship(
        "MateriChunk",
        back_populates="materi",
        cascade="all, delete-orphan"
    )


class MateriChunk(Base):
    __tablename__ = "materi_chunk"

    id         = Column(Integer, primary_key=True, index=True)
    materi_id  = Column(Integer, ForeignKey("materi.id"), nullable=False)
    urutan     = Column(Integer, nullable=False)
    kondisi_id = Column(String(10), nullable=True)   # A-F, dideteksi otomatis Claude
    isi_chunk  = Column(Text, nullable=False)
    konteks    = Column(Text, nullable=False)

    materi = relationship("Materi", back_populates="chunks")


class MateriAdaptif(Base):
    __tablename__ = "materi_adaptif"

    id            = Column(Integer, primary_key=True, index=True)
    siswa_id      = Column(Integer, ForeignKey("siswa.id"), nullable=False)
    kondisi_id    = Column(String(5), nullable=False)
    konten_json   = Column(JSON, nullable=False)
    iterasi       = Column(Integer, default=1)
    skor_evaluasi = Column(JSON, nullable=True)
    created_at    = Column(DateTime(timezone=True), server_default=func.now())

    siswa = relationship("Siswa", back_populates="materi_adaptif")