from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from app.database import get_db
from app.models.guru import Guru
from app.schemas.auth import TokenResponse
from app.utils.auth import verify_password, create_access_token

router = APIRouter(prefix="/auth", tags=["Autentikasi"])

@router.post("/login", response_model=TokenResponse)
def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):
    """
    Login guru. Menerima form data (username + password).
    Kompatibel dengan Swagger UI dan Retrofit Android.
    """
    guru = db.query(Guru).filter(Guru.username == form_data.username).first()

    if not guru or not verify_password(form_data.password, guru.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Username atau password salah"
        )

    token = create_access_token(data={"sub": guru.username})
    return TokenResponse(
        access_token=token,
        nama_guru=guru.nama
    )