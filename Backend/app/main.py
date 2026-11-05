from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from app.database import engine, Base, SessionLocal
from app.models import (
    Guru, Siswa, ParameterInstruksional,
    Materi, MateriChunk, MateriAdaptif,
    SesiLatihan, Gamifikasi
)
from app.routers import auth, siswa, materi, progres, pipeline
from app.utils.auth import hash_password
import os

app = FastAPI(
    title="Saku Pintar API",
    description="Backend untuk aplikasi pembelajaran Aritmetika Sosial tunagrahita",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Serve folder uploads agar foto bisa diakses via URL
os.makedirs("uploads/foto", exist_ok=True)
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

# Daftarkan semua router
app.include_router(auth.router)
app.include_router(siswa.router)
app.include_router(materi.router)
app.include_router(progres.router)
app.include_router(pipeline.router)

@app.on_event("startup")
def startup():
    Base.metadata.create_all(bind=engine)
    print("✓ Semua tabel berhasil dibuat/diverifikasi")

    db = SessionLocal()
    try:
        guru_ada = db.query(Guru).filter(Guru.username == "guru").first()
        if not guru_ada:
            guru_baru = Guru(
                username="guru",
                password_hash=hash_password("123456"),
                nama="Guru SMP Hikmah Teladan"
            )
            db.add(guru_baru)
            db.commit()
            print("✓ Akun guru default berhasil dibuat (guru / 123456)")
        else:
            print("✓ Akun guru sudah ada")
    finally:
        db.close()

@app.get("/")
def root():
    return {"status": "ok", "pesan": "Saku Pintar API berjalan"}

@app.get("/health")
def health():
    return {"status": "sehat"}