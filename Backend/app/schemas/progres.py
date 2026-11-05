from pydantic import BaseModel
from typing import Optional, Any
from datetime import datetime

class SesiLatihanCreate(BaseModel):
    siswa_id:      int
    kondisi_id:    str
    label_kondisi: str
    jumlah_benar:  int
    skor:          int
    soal_json:     Optional[Any] = None

class SesiLatihanResponse(BaseModel):
    id:            int
    kondisi_id:    str
    label_kondisi: str
    jumlah_benar:  int
    skor:          int
    created_at:    datetime
    class Config:
        from_attributes = True

class GamifikasiResponse(BaseModel):
    total_poin: int
    badge_a:    bool
    badge_b:    bool
    badge_c:    bool
    badge_d:    bool
    badge_e:    bool
    badge_f:    bool
    class Config:
        from_attributes = True