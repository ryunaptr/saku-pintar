import json
import fitz
import anthropic
from app.config import settings

client = anthropic.Anthropic(api_key=settings.ANTHROPIC_API_KEY)

DESKRIPSI_KONDISI = {
    "A": (
        "Mengenal Jenis-Jenis Uang: materi tentang jenis uang kertas vs uang koin/logam, "
        "bahan pembuat uang, ciri fisik uang (bentuk, warna, gambar), contoh pecahan uang "
        "Rupiah, pengenalan Rupiah sebagai mata uang Indonesia, metode 3D keaslian uang."
    ),
    "B": (
        "Mengenal Nilai Nominal Uang: materi tentang angka/nilai yang tertulis pada uang, "
        "pengertian nominal, urutan pecahan dari terkecil ke terbesar, cara membaca nilai "
        "nominal dalam kata-kata (seribu, dua ribu, dst), membandingkan besar kecil nilai."
    ),
    "C": (
        "Menghitung Uang: materi tentang OPERASI MATEMATIKA pada uang — menjumlahkan "
        "beberapa lembar dan keping uang, perkalian jumlah lembar dengan nilai pecahan, "
        "menghitung TOTAL belanja, RUMUS KEMBALIAN (uang dibayar DIKURANGI harga barang), "
        "memeriksa kecukupan uang untuk membeli barang."
    ),
    "D": (
        "Mengetahui Kesetaraan Nilai Uang: materi tentang tukar menukar pecahan uang, "
        "kombinasi pecahan berbeda yang nilainya SAMA (setara), tabel kesetaraan "
        "(misalnya Rp5.000 = 5 x Rp1.000), membentuk jumlah tertentu dari pecahan kecil."
    ),
    "E": (
        "Mengidentifikasi Harga Barang: materi tentang MEMBACA LABEL HARGA pada barang, "
        "simbol Rp/IDR pada label, daftar harga barang di kantin atau koperasi sekolah, "
        "cara membaca harga dalam kata-kata, mengenali apakah uang cukup untuk satu barang."
    ),
    "F": (
        "Membandingkan Harga Barang: materi tentang MEMBANDINGKAN dua atau lebih harga "
        "secara berdampingan, menentukan mana yang lebih murah atau lebih mahal, mencari "
        "barang PALING MURAH atau PALING MAHAL dari beberapa pilihan, memilih barang "
        "berdasarkan hasil perbandingan harga, strategi belanja hemat."
    ),
}

def ekstrak_teks_pdf(file_path: str) -> str:
    doc = fitz.open(file_path)
    teks = ""
    for halaman in doc:
        teks += halaman.get_text()
    doc.close()
    return teks.strip()

def pecah_menjadi_chunks(teks: str, ukuran_chunk: int = 150) -> list[str]:
    """Chunk lebih kecil agar setiap bagian lebih fokus ke satu topik."""
    kata_list = teks.split()
    chunks, chunk_kini, jumlah = [], [], 0
    for kata in kata_list:
        chunk_kini.append(kata)
        jumlah += 1
        if jumlah >= ukuran_chunk:
            chunks.append(" ".join(chunk_kini))
            chunk_kini, jumlah = [], 0
    if chunk_kini:
        chunks.append(" ".join(chunk_kini))
    return chunks

def analisis_dan_kontekstualisasi_chunk(
    chunk: str,
    urutan: int,
    total: int
) -> dict:
    deskripsi_str = "\n".join([f"   {k} = {v}" for k, v in DESKRIPSI_KONDISI.items()])

    system_prompt = f"""Kamu menganalisis potongan teks dari buku materi Aritmetika Sosial untuk siswa tunagrahita SMP.

Pilih SATU kompetensi yang PALING SESUAI dengan isi potongan teks ini:
{deskripsi_str}

Aturan:
- WAJIB pilih salah satu dari A, B, C, D, E, atau F
- Tidak ada pilihan lain
- Jika konten tampak umum, pilih kompetensi yang paling mendekati topiknya

Buat juga kalimat konteks singkat (1-2 kalimat) yang menjelaskan isi chunk ini dalam konteks materi tunagrahita.

Kembalikan HANYA JSON:
{{"kondisi_id": "A|B|C|D|E|F", "konteks": "kalimat konteks di sini"}}"""

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=300,
        system=[
            {
                "type": "text",
                "text": system_prompt,
                "cache_control": {"type": "ephemeral"}
            }
        ],
        messages=[
            {
                "role": "user",
                "content": f"Potongan teks (bagian {urutan} dari {total}):\n{chunk[:500]}"
            }
        ]
    )

    teks = response.content[0].text.strip()
    teks = teks.replace("```json", "").replace("```", "").strip()
    return json.loads(teks)

def proses_contextual_retrieval(file_path: str) -> list[dict]:
    teks = ekstrak_teks_pdf(file_path)
    if not teks:
        return []

    chunks = pecah_menjadi_chunks(teks, ukuran_chunk=150)
    total  = len(chunks)
    hasil  = []

    for i, chunk in enumerate(chunks):
        print(f"  → Memproses chunk {i+1}/{total}...")
        try:
            analisis = analisis_dan_kontekstualisasi_chunk(chunk, i + 1, total)
            kondisi  = analisis.get("kondisi_id", "A")

            # Pastikan nilai kondisi selalu valid
            if kondisi not in DESKRIPSI_KONDISI:
                kondisi = "A"

            hasil.append({
                "urutan"    : i + 1,
                "kondisi_id": kondisi,
                "isi_chunk" : chunk,
                "konteks"   : analisis.get("konteks", "")
            })
        except Exception as e:
            print(f"  ⚠ Chunk {i+1} gagal diproses ({e}), dilewati.")
            # Tetap simpan chunk dengan kondisi default agar tidak ada data yang hilang
            hasil.append({
                "urutan"    : i + 1,
                "kondisi_id": "A",
                "isi_chunk" : chunk,
                "konteks"   : "Konteks tidak tersedia karena gagal diproses."
            })
    

    return hasil