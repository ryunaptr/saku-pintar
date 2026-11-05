import json
import base64
import anthropic
from app.config import settings

client = anthropic.Anthropic(api_key=settings.ANTHROPIC_API_KEY)

SYSTEM_PROMPT_PROFILING = """Kamu adalah psikolog pendidikan yang mengekstrak parameter instruksional dari laporan psikologis.
Baca dokumen dengan teliti dan ekstrak data SPESIFIK dari laporan — jangan gunakan nilai default.
Setiap siswa memiliki profil berbeda. Kembalikan HANYA JSON tanpa penjelasan apapun."""

USER_PROMPT_PROFILING = """Baca seluruh laporan psikologis ini dengan teliti, lalu ekstrak 7 parameter instruksional berdasarkan data yang BENAR-BENAR ADA di dokumen.

Kembalikan HANYA JSON dengan format ini:
{
  "ki_01_tingkat_atensi": "<Sangat Pendek|Pendek|Sedang>",
  "ki_02_kemampuan_kebahasaan": "<Kata Sangat Dasar|Kalimat Sederhana|Kalimat Kompleks>",
  "ki_03_kapasitas_memori": "<Pengulangan Intensif|Pengulangan Sedang|Tanpa Pengulangan Khusus>",
  "ki_04_preferensi_persepsi": "<Visual Dominan|Seimbang|Teks Dominan>",
  "ki_05_metakognitif": "<Konfirmasi Setiap Langkah|Konfirmasi Akhir Segmen|Tanpa Konfirmasi Khusus>",
  "ki_06_jenjang_kelas": "<Kelas 7|Kelas 8|Kelas 9>",
  "ki_07_tingkatan_tunagrahita": "<Ringan|Sedang|Berat>"
}

=== PANDUAN EKSTRAKSI PER PARAMETER ===

KI-01 — Tingkat Atensi (pilih berdasarkan bukti di dokumen):
- "Sangat Pendek" → laporan menyebut: atensi/konsentrasi sangat mudah teralih, tidak bisa fokus,
  perlu stimulasi terus-menerus, skor atensi/memori di level 5½ atau lebih rendah pada tabel WISC,
  ATAU siswa tingkatan Sedang/Berat dengan deskripsi atensi buruk
- "Pendek" → laporan menyebut: mudah teralih tapi masih bisa difokuskan kembali, perlu pengulangan
  sering, skor atensi di level 6½–8½ pada tabel WISC,
  ATAU siswa tingkatan Ringan dengan deskripsi atensi kurang — gunakan ini sebagai default untuk Ringan
  kecuali ada bukti eksplisit atensi sangat buruk
- "Sedang" → laporan menyebut: daya konsentrasi cukup baik, mampu fokus dengan arahan minimal,
  skor atensi di level 9½ ke atas

PANDUAN KI-01: Korelasikan dengan KI-07.
Siswa Ringan (IQ 55-70) → default "Pendek" kecuali dokumen eksplisit menyebut atensi sangat buruk
Siswa Sedang (IQ 40-54) → default "Sangat Pendek" kecuali dokumen eksplisit menyebut atensi cukup baik

KI-02 — Kemampuan Kebahasaan (UTAMAKAN skor VIQ numerik):
- "Kata Sangat Dasar" → VIQ < 50 (WAJIB pilih ini jika VIQ tertulis di bawah 50),
  ATAU laporan menyebut: kosakata sangat terbatas, komunikasi satu kata,
  sulit memahami instruksi verbal apapun
- "Kalimat Sederhana" → VIQ 50–75, ATAU laporan menyebut: mampu memahami kalimat
  pendek dan sederhana, kosakata terbatas tapi fungsional, perlu bahasa konkret dan bertahap
- "Kalimat Kompleks" → VIQ > 75 DAN laporan EKSPLISIT menyebut mampu mengikuti
  instruksi multi-langkah tanpa kesulitan berarti

PERINGATAN KI-02: Jika dokumen mencantumkan VIQ ≤45 atau VIQ <50 → WAJIB "Kata Sangat Dasar".
Semua siswa dalam dokumen ini adalah siswa berkebutuhan khusus di SLB.
VIQ > 70 TIDAK otomatis berarti "Kalimat Kompleks".
"Kalimat Kompleks" hanya dipilih jika laporan EKSPLISIT menyatakan kemampuan bahasa kompleks.

KI-03 — Kapasitas Memori (pilih berdasarkan deskripsi memori/pengulangan):
- "Pengulangan Intensif" → laporan menyebut: sangat sulit mengingat informasi baru,
  perlu pengulangan berkali-kali, memori jangka pendek sangat lemah,
  ATAU laporan menyebut "membutuhkan penegasan atas hal-hal yang sama secara berulang",
  ATAU siswa tingkatan Sedang/Berat
- "Pengulangan Sedang" → laporan menyebut: perlu pengulangan konsisten, butuh beberapa
  kali penyampaian untuk mengingat, ATAU siswa tingkatan Ringan dengan memori cukup
- "Tanpa Pengulangan Khusus" → laporan menyebut: memori relatif baik, dapat mengingat
  informasi dengan satu-dua kali penyampaian

PANDUAN KI-03: Korelasikan dengan KI-07.
Siswa Sedang (IQ 40-54) → default "Pengulangan Intensif"
Siswa Ringan (IQ 55-70) → "Pengulangan Intensif" jika dokumen menyebut perlu penegasan
berulang, "Pengulangan Sedang" jika tidak disebutkan

KI-04 — Preferensi Persepsi (pilih berdasarkan rekomendasi psikolog):
- "Visual Dominan" → laporan merekomendasikan: alat peraga, gambar, demonstrasi langsung, benda
  konkret, atau menyebut lebih mudah memahami lewat visual
- "Seimbang" → laporan menyebut: dapat memproses informasi visual maupun verbal dengan cukup baik
- "Teks Dominan" → laporan menyebut: lebih mudah memahami penjelasan verbal/tertulis dibanding visual

KI-05 — Metakognitif (pilih berdasarkan kebutuhan scaffolding DAN tingkatan tunagrahita):
- "Konfirmasi Setiap Langkah" → laporan menyebut: perlu pendampingan intensif di setiap tahap,
  tidak bisa bekerja mandiri sama sekali, perlu instruksi satu per satu,
  ATAU siswa tingkatan Sedang/Berat
- "Konfirmasi Akhir Segmen" → laporan menyebut: perlu pengecekan berkala, bisa mengerjakan
  beberapa langkah mandiri tapi perlu konfirmasi,
  ATAU siswa tingkatan Ringan dengan deskripsi kemandirian terbatas
- "Tanpa Konfirmasi Khusus" → laporan menyebut: cukup mandiri, hanya perlu arahan awal

PANDUAN KI-05: Korelasikan dengan KI-07.
Siswa Ringan (IQ 55-70) → default "Konfirmasi Akhir Segmen"
Siswa Sedang (IQ 40-54) → default "Konfirmasi Setiap Langkah"
Siswa Berat (IQ < 40)   → selalu "Konfirmasi Setiap Langkah"

KI-06 — Jenjang Kelas:
- PRIORITAS UTAMA: Jika DATA KELAS AKTUAL SISWA tersedia di bawah, gunakan nilai tersebut langsung.
  Jangan inferensikan dari usia — data kelas aktual selalu lebih akurat dari dokumen historis.
- FALLBACK (jika tidak ada data kelas aktual): inferensikan dari usia saat pemeriksaan:
  * 12 tahun → Kelas 7
  * 13 tahun 0–5 bulan → Kelas 7
  * 13 tahun 6 bulan ke atas → Kelas 8
  * 14 tahun → Kelas 8
  * 15 tahun → Kelas 9

PERINGATAN KI-06: Dokumen psikotes bisa dibuat 1–2 tahun sebelum siswa masuk sekolah ini.
Usia di dokumen tidak mencerminkan jenjang kelas saat ini. Prioritaskan data kelas aktual.

KI-07 — Tingkatan Tunagrahita (WAJIB dari skor IQ numerik):
- Cari skor: FIQ, Full Scale IQ, IQ Total, atau PIQ
- Jika dokumen menyebut "di bawah X" atau "< X", gunakan X-1 sebagai estimasi
  CONTOH: "FIQ di bawah 43" → estimasi 42 → Sedang
- Jika VIQ = 0, ABAIKAN VIQ — gunakan PIQ atau FIQ saja (VIQ=0 berarti tidak terukur, bukan IQ nol)
- Klasifikasi KETAT berdasarkan skor numerik (mengacu ICD-10, WHO):
  * Ringan = IQ 50–69
  * Sedang = IQ 35–49
  * Berat   = IQ 20–34
  * Sangat Berat = IQ < 20
- Jika dokumen menyebut "Borderline" dengan skor IQ 67–70, klasifikasikan sebagai Ringan
- JANGAN gunakan istilah deskriptif sebagai dasar klasifikasi
- JANGAN default ke Ringan jika tidak yakin — baca ulang dokumen sampai menemukan skor numerik
- Urutan prioritas skor: FIQ > PIQ > rata-rata VIQ+PIQ (jika VIQ ≠ 0)

=== PERINGATAN ===
Hasil ekstraksi HARUS mencerminkan data spesifik dari dokumen ini.
Jika dua siswa berbeda memiliki profil berbeda di dokumen, hasil JSON mereka HARUS berbeda.
Dilarang menggunakan nilai yang sama untuk semua parameter tanpa dasar dari dokumen."""


def ekstrak_parameter_dari_pdf(pdf_bytes: bytes, kelas: str = "") -> dict:
    pdf_base64 = base64.standard_b64encode(pdf_bytes).decode("utf-8")

    konteks_kelas = f"\n\nDATA KELAS AKTUAL SISWA: {kelas}" if kelas else ""
    prompt_final  = USER_PROMPT_PROFILING + konteks_kelas

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=1000,
        system=[
            {
                "type": "text",
                "text": SYSTEM_PROMPT_PROFILING,
                "cache_control": {"type": "ephemeral"}
            },
            {
                "type": "text",
                "text": prompt_final,
                "cache_control": {"type": "ephemeral"}
            }
        ],
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "document",
                        "source": {
                            "type": "base64",
                            "media_type": "application/pdf",
                            "data": pdf_base64
                        }
                    },
                    {
                        "type": "text",
                        "text": "Ekstrak parameter instruksional dari dokumen psikologis ini."
                    }
                ]
            }
        ]
    )

    teks = response.content[0].text.strip()
    print(f"=== RESPONSE CLAUDE ===")
    print(f"Teks: '{teks}'")
    print(f"Stop reason: {response.stop_reason}")
    print(f"Cache: input={response.usage.cache_creation_input_tokens} creation, {response.usage.cache_read_input_tokens} read")
    print(f"======================")
    teks = teks.replace("```json", "").replace("```", "").strip()
    return json.loads(teks)