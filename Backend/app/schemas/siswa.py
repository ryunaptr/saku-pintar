from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class ParameterInstruksionalBase(BaseModel):
    ki_01_tingkat_atensi:        str
    ki_02_kemampuan_kebahasaan:  str
    ki_03_kapasitas_memori:      str
    ki_04_preferensi_persepsi:   str
    ki_05_metakognitif:          str
    ki_06_jenjang_kelas:         str
    ki_07_tingkatan_tunagrahita: str

class ParameterInstruksionalResponse(ParameterInstruksionalBase):
    id: int
    siswa_id: int
    class Config:
        from_attributes = True

class SiswaCreate(BaseModel):
    nama:     str
    kelas:    str
    foto_url: Optional[str] = None

class SiswaResponse(BaseModel):
    id:         int
    nama:       str
    kelas:      str
    foto_url:   Optional[str]
    created_at: datetime
    parameter:  Optional[ParameterInstruksionalResponse] = None
    class Config:
        from_attributes = True