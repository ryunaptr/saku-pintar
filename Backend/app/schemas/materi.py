from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class MateriResponse(BaseModel):
    id:            int
    nama_file:     str
    deskripsi:     Optional[str]
    tanggal_upload: datetime
    class Config:
        from_attributes = True

class MateriChunkResponse(BaseModel):
    id:         int
    urutan:     int
    kondisi_id: Optional[str]
    isi_chunk:  str
    konteks:    str
    class Config:
        from_attributes = True

class MateriDetailResponse(MateriResponse):
    chunks: List[MateriChunkResponse] = []
    class Config:
        from_attributes = True