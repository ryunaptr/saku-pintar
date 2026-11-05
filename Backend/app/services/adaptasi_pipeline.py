import json


def parse_json_claude(teks: str) -> dict:
    teks = teks.strip()
    if "```json" in teks:
        teks = teks.split("```json", 1)[1]
        if "```" in teks:
            teks = teks.rsplit("```", 1)[0]
    elif teks.startswith("```"):
        teks = teks.split("```", 1)[1]
        if "```" in teks:
            teks = teks.rsplit("```", 1)[0]
    teks = teks.strip()
    try:
        return json.loads(teks)
    except json.JSONDecodeError:
        pass
    start = teks.find('{')
    end   = teks.rfind('}')
    if start != -1 and end != -1 and end > start:
        try:
            return json.loads(teks[start:end + 1])
        except json.JSONDecodeError:
            pass
    raise ValueError(f"Gagal parse JSON dari Claude. Preview: {teks[:300]}")


import anthropic
from app.config import settings

client = anthropic.Anthropic(api_key=settings.ANTHROPIC_API_KEY)


#Tujuan Pembelajaran Per Kondisi
TUJUAN_PER_KONDISI = {
    "A": {
        "nama": "Mengenal Jenis-Jenis Uang",
        "tujuan": """
- Siswa dapat membedakan uang kertas dan uang koin berdasarkan bentuk fisiknya
- Siswa dapat menyebutkan ciri-ciri uang kertas (berbentuk lembaran, terbuat dari kertas khusus, tipis)
- Siswa dapat menyebutkan ciri-ciri uang koin (berbentuk bulat, terbuat dari logam, keras)
- Siswa dapat mengelompokkan contoh-contoh uang ke dalam jenis kertas atau koin
""",
        "dilarang": """
- JANGAN bahas nilai nominal atau warna uang (itu materi Kondisi B)
- JANGAN bahas cara menghitung atau membayar (itu materi Kondisi C)
- JANGAN tanya "berapa nilainya" atau "mana yang lebih besar nilainya"
"""
    },
    "B": {
        "nama": "Mengenal Nilai Nominal Uang",
        "tujuan": """
- Siswa dapat membaca dan menyebutkan nilai nominal uang dengan benar (contoh: Rp50.000 = Lima Puluh Ribu Rupiah)
- Siswa dapat mengenali warna khas setiap nominal uang kertas (merah=100rb, biru=50rb, hijau=20rb, ungu=10rb, cokelat=5rb)
- Siswa dapat mengurutkan pecahan uang dari yang terkecil ke terbesar
- Siswa dapat menyebutkan pecahan uang yang ada di Indonesia
- Format penulisan warna dan nominal WAJIB menggunakan tabel teks seperti ini:
  Rp100.000 = Seratus Ribu Rupiah → warna MERAH
  Rp50.000 = Lima Puluh Ribu Rupiah → warna BIRU
  (dst)
""",
        "dilarang": """
- JANGAN bahas cara menghitung kembalian (itu materi Kondisi C)
- JANGAN tanya "mana yang lebih mahal" dalam konteks harga barang (itu materi Kondisi F)
- JANGAN bahas kesetaraan kombinasi uang (itu materi Kondisi D)
- JANGAN tulis "100rb", "50rb", atau singkatan. Tulis nominal lengkap: Rp100.000
- Aktivitas cukup: mencocokkan nominal dengan bacaannya, menyebutkan warna, mengurutkan
"""
    },
    "C": {
        "nama": "Menghitung Uang",
        "tujuan": """
- Siswa dapat menjumlahkan nominal uang dari beberapa uang yang dimiliki
- Siswa dapat menghitung kembalian dari transaksi jual beli sederhana
- Siswa dapat menentukan apakah uang yang dimiliki cukup untuk membayar suatu barang
- Siswa dapat melakukan simulasi transaksi membeli barang di kantin/koperasi sekolah (pastikan memiliki oeparasi penjumlahan dan pengurangan)
""",
        "dilarang": """
- JANGAN bahas perbandingan harga dua barang berbeda (itu materi Kondisi F)
- JANGAN bahas kesetaraan kombinasi uang tanpa konteks belanja (itu materi Kondisi D)
"""
    },
    "D": {
        "nama": "Kesetaraan Nilai Uang",
        "tujuan": """
- Siswa dapat menunjukkan kombinasi uang yang nilainya sama (contoh: Rp10.000 = Rp5.000 + Rp5.000)
- Siswa dapat menukar satu pecahan uang dengan kombinasi pecahan lain yang nilainya setara
- Siswa dapat memilih kombinasi uang yang tepat untuk membayar suatu nominal
- Siswa memahami bahwa satu nominal bisa direpresentasikan dengan berbagai kombinasi pecahan
""",
        "dilarang": """
- JANGAN bahas harga barang atau transaksi belanja (itu materi Kondisi C/E)
- JANGAN tanya "mana yang lebih murah" (itu materi Kondisi F)
- Fokus pada KOMBINASI pecahan, bukan belanja
- JANGAN buat opsi yang memiliki dua atau lebih nominal sama persis (contoh SALAH: opsi = ["Rp5.000", "Rp5.000", "Rp2.000"])
- Jika kombinasi melibatkan dua uang sama, tulis sebagai teks:
  (contoh BENAR: opsi = ["Rp5.000 + Rp5.000", "Rp2.000 + Rp2.000 + Rp2.000 + Rp2.000 + Rp2.000 + Rp2.000", "Rp1.000 + Rp1.000 + ..."])
  Atau sederhanakan: ["2 lembar Rp5.000", "5 lembar Rp2.000", "10 lembar Rp1.000"]
"""
    },
    "E": {
        "nama": "Mengidentifikasi Harga Barang",
        "tujuan": """
- Siswa mampu membaca dan memahami label harga pada sebuah barang
- Siswa mampu menyebutkan harga barang yang tertera pada label harga
- Siswa mampu menentukan apakah suatu barang termasuk murah, sedang, atau mahal berdasarkan harga
""",
        "dilarang": """
- JANGAN bandingkan harga dua barang untuk menentukan mana yang lebih murah (itu materi Kondisi F)
- JANGAN tanya "mana yang lebih murah dari dua pilihan ini" — fokus pada satu barang dan satu harga
"""
    },
    "F": {
        "nama": "Membandingkan Harga Barang",
        "tujuan": """
- Siswa dapat membandingkan harga dua barang atau lebih dan menentukan mana yang lebih murah/mahal
- Siswa dapat memilih barang dengan harga paling murah dari beberapa pilihan
- Siswa dapat mengurutkan barang dari yang paling murah ke paling mahal
- Siswa dapat mengambil keputusan pembelian berdasarkan perbandingan harga dan sesuai dengan uang yang dimiliki
""",
        "dilarang": """
- JANGAN bahas cara menghitung kembalian (itu materi Kondisi C)
- JANGAN bahas kesetaraan uang (itu materi Kondisi D)
- Fokus pada PERBANDINGAN harga, bukan menghitung
"""
    }
}


#Generator Agent

def generate_draft_materi(
    chunks: list[dict],
    parameter: dict,
    kondisi_id: str,
    umpan_balik: str = None
) -> dict:
    materi_konteks = "\n\n".join([
        f"[Chunk {c['urutan']}]\nKonteks: {c['konteks']}\nIsi: {c['isi_chunk']}"
        for c in chunks
    ])

    info_kondisi     = TUJUAN_PER_KONDISI.get(kondisi_id, {})
    nama_kondisi     = info_kondisi.get("nama", f"Kondisi {kondisi_id}")
    tujuan_detail    = info_kondisi.get("tujuan", "")
    larangan_kondisi = info_kondisi.get("dilarang", "")

    blok_statis = f"""<role>
Kamu adalah asisten pembelajaran khusus siswa tunagrahita.
Tugasmu: adaptasi materi Aritmetika Sosial menjadi 4 halaman sesuai profil kognitif siswa.
</role>

<batasan_kognitif>
Seluruh materi WAJIB berada dalam kategori Lower Order Thinking Skills (LOTS)
menurut Taksonomi Bloom. JANGAN PERNAH membuat aktivitas yang menuntut siswa
menganalisis hubungan sebab-akibat kompleks, mengevaluasi/membandingkan lebih
dari 2 opsi sekaligus secara mandiri, atau mencipta solusi baru tanpa arahan
(kategori Higher Order Thinking Skills) — batasan ini berlaku untuk SEMUA
siswa tanpa terkecuali, tidak peduli nilai parameter lain.
Di dalam LOTS, tingkat yang diperbolehkan ditentukan oleh nilai KI-07
(Tingkatan Tunagrahita) pada <student_profile> di bawah:

- JIKA KI-07 = "Sedang" atau "Berat": seluruh aktivitas WAJIB berhenti di
  tingkat Memahami (Understand) — menjelaskan ulang, memberi contoh, atau
  membandingkan 2 hal secara langsung. JANGAN minta siswa melakukan operasi
  hitung atau prosedur bertahap apa pun, sekalipun terlihat sederhana.

- JIKA KI-07 = "Ringan": aktivitas BOLEH naik ke tingkat Menerapkan (Apply),
  TERBATAS PADA satu jenis operasi hitung tunggal (contoh: hanya
  penjumlahan, seperti menjumlahkan seluruh nominal uang yang dimiliki
  dalam satu instruksi). JANGAN gabungkan lebih dari satu jenis operasi
  dalam satu soal (misal: penjumlahan digabung pengurangan, atau
  penjumlahan digabung perbandingan).

Jika ragu di antara dua tingkat, WAJIB pilih tingkat yang lebih rendah.
</batasan_kognitif>

<kondisi>
Kondisi yang sedang diajarkan: {kondisi_id} — {nama_kondisi}

TUJUAN PEMBELAJARAN KONDISI INI:
{tujuan_detail}

LARANGAN KHUSUS KONDISI INI (JANGAN dilanggar):
{larangan_kondisi}
</kondisi>

<materi_referensi>
{materi_konteks}
</materi_referensi>

<instructions>
Hasilkan HANYA JSON 4 halaman materi adaptif dengan format TEPAT seperti ini:
{{
  "halaman": [
    {{
      "tipePage": "Penjelasan",
      "judul": "...",
      "isi": "• poin pertama\\n• poin kedua\\n• poin ketiga",
      "emoji": "..."
    }},
    {{
      "tipePage": "Contoh",
      "judul": "...",
      "isi": "Satu kalimat cerita singkat.\\n• fakta atau langkah pertama\\n• fakta atau langkah kedua",
      "emoji": "..."
    }},
    {{
      "tipePage": "Aktivitas 1",
      "judul": "...",
      "isi": "pertanyaan singkat untuk siswa",
      "emoji": "...",
      "opsi": ["pilihan A", "pilihan B", "pilihan C"],
      "jawabanBenar": 0
    }},
    {{
      "tipePage": "Aktivitas 2",
      "judul": "...",
      "isi": "pertanyaan singkat untuk siswa",
      "emoji": "...",
      "opsi": ["pilihan A", "pilihan B", "pilihan C"],
      "jawabanBenar": 1
    }}
  ]
}}

FORMAT ISI HALAMAN 1 — PENJELASAN (WAJIB ikuti format ini):
- Tulis 3 sampai 4 poin bullet saja. Tidak lebih.
- Setiap poin diawali tanda "•" lalu spasi lalu teks.
- Pisahkan setiap poin dengan "\\n" (bukan baris baru literal).
- Setiap poin MAKSIMAL 10 kata. Satu poin = satu fakta sederhana.
- JANGAN tulis paragraf. JANGAN tulis kalimat panjang.

FORMAT ISI HALAMAN 2 — CONTOH (WAJIB ikuti format ini):
- Baris pertama: satu kalimat cerita pendek (maksimal 15 kata), TANPA tanda "•"
- Baris berikutnya: 2 sampai 3 poin bullet diawali "•"
- Pisahkan setiap baris dengan "\\n"
- Nama tokoh: gunakan Doni, Rina, Bima, Tara, Eko, atau Citra

FORMAT ISI HALAMAN 3 & 4 — AKTIVITAS:
- Tulis SATU pertanyaan singkat saja (maksimal 15 kata)
- Pertanyaan HARUS menguji tujuan pembelajaran kondisi ini
- Field "opsi" WAJIB ada (2 atau 3 pilihan)
- Field "jawabanBenar" WAJIB ada (index 0, 1, atau 2)
- ATURAN JUMLAH OPSI:
  * Gunakan 3 opsi jika tersedia 3 pilihan yang BENAR-BENAR berbeda secara konsep
  * Gunakan 2 opsi jika hanya ada 2 kategori yang bermakna berbeda
  * JANGAN paksa buat 3 opsi jika opsi ketiga hanya sinonim dari opsi lain
  * Contoh kondisi A: cukup ["Uang kertas", "Uang koin"] — tidak perlu opsi ketiga

ATURAN WAJIB:
- Gunakan bahasa sesuai KI-02 kemampuan kebahasaan siswa
- Contoh HARUS dari konteks sekolah: kantin, koperasi, uang saku
- Gunakan HANYA nominal uang Rupiah yang nyata: Rp100, Rp200, Rp500, Rp1.000, Rp2.000, Rp5.000, Rp10.000, Rp20.000, Rp50.000, Rp100.000
- JANGAN gunakan nominal yang tidak ada seperti Rp3.000, Rp4.000, Rp7.000, Rp15.000 dll
- Setiap opsi jawaban HARUS berbeda secara konsep dan makna
- Opsi jawaban HARUS benar-benar berbeda satu sama lain
- Sesuaikan kompleksitas dengan KI-07 tingkatan tunagrahita

LARANGAN KERAS UMUM:
- JANGAN tulis [Gambar: ...], [Image: ...], atau deskripsi gambar apapun
- JANGAN tulis instruksi navigasi ("ketuk tombol", "scroll ke bawah", "klik ✅")
- JANGAN gunakan simbol perkalian (×) — gunakan penjumlahan berulang
- JANGAN tulis kunci jawaban di field "isi" Aktivitas
- JANGAN tulis paragraf panjang di halaman Penjelasan dan Contoh
- JANGAN tulis lebih dari 4 bullet point di halaman Penjelasan
</instructions>"""

    blok_dinamis = f"""<student_profile>
KI-01 Tingkat Atensi: {parameter['ki_01_tingkat_atensi']}
KI-02 Kemampuan Kebahasaan: {parameter['ki_02_kemampuan_kebahasaan']}
KI-03 Kapasitas Memori: {parameter['ki_03_kapasitas_memori']}
KI-04 Preferensi Persepsi: {parameter['ki_04_preferensi_persepsi']}
KI-05 Metakognitif: {parameter['ki_05_metakognitif']}
KI-06 Jenjang Kelas: {parameter['ki_06_jenjang_kelas']}
KI-07 Tingkatan Tunagrahita: {parameter['ki_07_tingkatan_tunagrahita']}
</student_profile>"""

    pesan_user = "Hasilkan materi adaptif dalam format JSON berdasarkan materi referensi dan profil siswa di atas.\nPENTING: Gunakan \\n untuk baris baru di dalam string JSON, bukan baris baru literal."
    if umpan_balik:
        pesan_user = (
            f"<perbaikan>\nPerbaiki masalah berikut dari iterasi sebelumnya:\n"
            f"{umpan_balik}\n</perbaikan>\n\n"
            + pesan_user
        )

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=4000,
        system=[
            {
                "type": "text",
                "text": blok_statis,
                "cache_control": {"type": "ephemeral"}
            },
            {
                "type": "text",
                "text": blok_dinamis
            }
        ],
        messages=[
            {
                "role": "user",
                "content": pesan_user
            }
        ]
    )

    teks = response.content[0].text.strip()
    teks = teks.replace("```json", "").replace("```", "").strip()
    return parse_json_claude(teks)

#Evaluator Agent
def evaluasi_draft(draft: dict, parameter: dict, kondisi_id: str) -> dict:

    info_kondisi  = TUJUAN_PER_KONDISI.get(kondisi_id, {})
    nama_kondisi  = info_kondisi.get("nama", f"Kondisi {kondisi_id}")
    tujuan_detail = info_kondisi.get("tujuan", "")

    blok_statis = f"""<role>
Kamu adalah evaluator kualitas materi pembelajaran tunagrahita.
Evaluasi berdasarkan rubrik 5 dimensi kognitif.
</role>

<batasan_kognitif>
Materi WAJIB berada dalam kategori Lower Order Thinking Skills (LOTS) menurut
Taksonomi Bloom. Materi yang menuntut analisis sebab-akibat kompleks,
evaluasi/perbandingan lebih dari 2 opsi mandiri, atau kreasi solusi baru
tanpa arahan (Higher Order Thinking Skills) TIDAK LULUS — beri skor
Accessibility = 1, berapa pun nilai KI-07-nya.

Periksa juga kesesuaian dengan nilai KI-07 pada <student_profile>:
- JIKA KI-07 = "Sedang" atau "Berat": materi HARUS berhenti di tingkat
  Memahami. Jika materi meminta siswa melakukan operasi hitung atau
  prosedur bertahap apa pun, beri skor Accessibility = 1 dan sebutkan
  pelanggaran ini secara spesifik pada umpan_balik.
- JIKA KI-07 = "Ringan": materi BOLEH mengandung satu jenis operasi hitung
  tunggal. Jika mengandung lebih dari satu jenis operasi dalam satu soal,
  beri skor Accessibility = 2 dan catat pelanggaran ini secara spesifik
  pada umpan_balik.
</batasan_kognitif>

<kondisi>
Kondisi yang dievaluasi: {kondisi_id} — {nama_kondisi}
Tujuan pembelajaran yang harus dicapai:
{tujuan_detail}
</kondisi>

<evaluation_criteria>
Berikan skor 1-5 untuk setiap dimensi:
1. Clarity       (KI-02): Kosakata & panjang kalimat sesuai kemampuan kebahasaan
2. Simplicity    (KI-01,02): Format bullet pada Penjelasan — beri skor 1 jika masih paragraf
3. Repetition    (KI-03): Frekuensi pengulangan poin kunci sesuai kapasitas memori
4. Contextuality (KI-06): Relevansi contoh dengan kehidupan sehari-hari
5. Accessibility (KI-04,05,07): Kesesuaian dengan profil kognitif dan tujuan pembelajaran kondisi ini.
   Beri skor 1 jika materi membahas topik di luar tujuan pembelajaran kondisi ini.
   Untuk pelanggaran batasan kognitif, ikuti ketentuan skor pada <batasan_kognitif> di atas.

Kembalikan HANYA JSON:
{{
  "skor": {{
    "clarity": N, "simplicity": N, "repetition": N,
    "contextuality": N, "accessibility": N
  }},
  "rata_rata": N.N,
  "sudah_sesuai": true/false,
  "umpan_balik": "Instruksi perbaikan spesifik. Sebutkan jika ada topik yang keluar dari tujuan kondisi ini."
}}

sudah_sesuai = true jika semua skor >= 4
</evaluation_criteria>"""

    blok_dinamis = f"""<student_profile>
KI-01 Tingkat Atensi: {parameter['ki_01_tingkat_atensi']}
KI-02 Kemampuan Kebahasaan: {parameter['ki_02_kemampuan_kebahasaan']}
KI-03 Kapasitas Memori: {parameter['ki_03_kapasitas_memori']}
KI-04 Preferensi Persepsi: {parameter['ki_04_preferensi_persepsi']}
KI-05 Metakognitif: {parameter['ki_05_metakognitif']}
KI-07 Tingkatan Tunagrahita: {parameter['ki_07_tingkatan_tunagrahita']}
</student_profile>"""

    draft_str = json.dumps(draft, ensure_ascii=False)

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=4000,
        system=[
            {
                "type": "text",
                "text": blok_statis,
                "cache_control": {"type": "ephemeral"}
            },
            {
                "type": "text",
                "text": blok_dinamis
            }
        ],
        messages=[
            {
                "role": "user",
                "content": f"<draft>\n{draft_str}\n</draft>\n\nEvaluasi draft ini."
            }
        ]
    )

    teks = response.content[0].text.strip()
    teks = teks.replace("```json", "").replace("```", "").strip()
    return parse_json_claude(teks)

#Self-Refine Loop

def self_refine_materi(chunks: list[dict], parameter: dict, kondisi_id: str, max_iterasi: int = 3) -> dict:
    draft_terbaik  = None
    skor_terbaik   = 0.0
    umpan_balik    = None
    draft_terakhir = None 

    for iterasi in range(1, max_iterasi + 1):
        draft = generate_draft_materi(chunks, parameter, kondisi_id, umpan_balik)
        draft_terakhir = draft

        valid, pesan_error = validasi_struktur_materi(draft)
        if not valid:
            umpan_balik = pesan_error
            continue

        evaluasi = evaluasi_draft(draft, parameter, kondisi_id)
        rata_rata = evaluasi.get("rata_rata", 0.0)

        if rata_rata > skor_terbaik:
            skor_terbaik = rata_rata
            draft_terbaik = {
                "konten"        : draft,
                "skor_evaluasi" : evaluasi["skor"],
                "rata_rata"     : rata_rata,
                "iterasi"       : iterasi
            }

        if evaluasi.get("sudah_sesuai", False):
            break

        umpan_balik = evaluasi.get("umpan_balik", "")

    # Fallback: kalau tidak ada satupun draft yang lolos validasi struktur, tetap kembalikan percobaan terakhir alih-alih None, supaya endpoint tidak crash
    if draft_terbaik is None:
        return {
            "konten"        : draft_terakhir,
            "skor_evaluasi" : None,
            "rata_rata"     : 0.0,
            "iterasi"       : max_iterasi
        }

    return draft_terbaik


#Quiz Generator

def generate_soal(chunks: list[dict], parameter: dict, kondisi_id: str, umpan_balik: str = None) -> list[dict]:
    materi_konteks = "\n\n".join([
        f"[Chunk {c['urutan']}]\nKonteks: {c['konteks']}\nIsi: {c['isi_chunk']}"
        for c in chunks[:4]
    ])

    info_kondisi     = TUJUAN_PER_KONDISI.get(kondisi_id, {})
    nama_kondisi     = info_kondisi.get("nama", f"Kondisi {kondisi_id}")
    tujuan_detail    = info_kondisi.get("tujuan", "")
    larangan_kondisi = info_kondisi.get("dilarang", "")

    blok_statis = f"""<role>
Kamu adalah pembuat soal latihan untuk siswa tunagrahita.
Buat 5 soal pilihan ganda yang menguji tujuan pembelajaran kondisi ini.
</role>

<batasan_kognitif>
Seluruh soal WAJIB berada dalam kategori Lower Order Thinking Skills (LOTS)
menurut Taksonomi Bloom, level yang sama seperti materi yang diajarkan.
JIKA KI-07 = "Sedang" atau "Berat": soal WAJIB level Memahami saja (mengenali,
menyebutkan, membandingkan langsung) — JANGAN buat soal yang menuntut operasi
hitung.
JIKA KI-07 = "Ringan": soal BOLEH menuntut satu jenis operasi hitung tunggal.
Jika ragu, pilih level yang lebih rendah.
</batasan_kognitif>

<kondisi>
Kondisi yang diuji: {kondisi_id} — {nama_kondisi}
Tujuan pembelajaran yang harus diuji oleh soal:
{tujuan_detail}

Larangan khusus kondisi ini:
{larangan_kondisi}
</kondisi>

<materi_referensi>
{materi_konteks}
</materi_referensi>

Kembalikan HANYA JSON:
{{
  "soal": [
    {{
      "pertanyaan": "...",
      "pilihan": ["opsi A", "opsi B", "opsi C"],
      "jawabanBenar": 0
    }}
  ]
}}

ATURAN WAJIB:
1. Setiap soal menguji TEPAT SATU tujuan dari kondisi ini
2. Setiap soal memiliki TEPAT SATU jawaban benar
3. Ketiga opsi berbeda secara logis/matematis
4. Soal SELF-CONTAINED — semua info ada di teks soal
5. Gunakan HANYA nominal nyata: Rp100, Rp200, Rp500, Rp1.000, Rp2.000, Rp5.000, Rp10.000, Rp20.000, Rp50.000, Rp100.000
6. JANGAN gunakan nominal tidak nyata seperti Rp3.000, Rp4.000, Rp7.000, dll
7. Setiap opsi HARUS berbeda secara konsep dan makna
8. Jika hanya ada 2 kategori berbeda, gunakan 2 opsi saja
9. Gunakan bahasa sederhana sesuai kemampuan kebahasaan siswa
10. Pertanyaan singkat, maksimal 20 kata
11. Konteks: kantin, koperasi, atau uang saku sekolah"""

    blok_dinamis = f"""<student_profile>
KI-02 Kemampuan Kebahasaan: {parameter['ki_02_kemampuan_kebahasaan']}
KI-01 Tingkat Atensi: {parameter['ki_01_tingkat_atensi']}
KI-06 Jenjang Kelas: {parameter['ki_06_jenjang_kelas']}
KI-07 Tingkatan Tunagrahita: {parameter['ki_07_tingkatan_tunagrahita']}
</student_profile>"""

    pesan_user = f"Buat 5 soal pilihan ganda untuk kondisi {kondisi_id} berdasarkan materi referensi dan profil siswa di atas."
    if umpan_balik:
        pesan_user = (
            f"<perbaikan>\nPerbaiki masalah berikut dari iterasi sebelumnya:\n"
            f"{umpan_balik}\n</perbaikan>\n\n"
            + pesan_user
        )

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=4000,
        system=[
            {
                "type": "text",
                "text": blok_statis,
                "cache_control": {"type": "ephemeral"}
            },
            {
                "type": "text",
                "text": blok_dinamis
            }
        ],
        messages=[
            {
                "role": "user",
                "content": pesan_user
            }
        ]
    )

    teks = response.content[0].text.strip()
    teks = teks.replace("```json", "").replace("```", "").strip()
    return parse_json_claude(teks).get("soal", [])

def validasi_struktur_materi(draft: dict) -> tuple[bool, str]:
    halaman = draft.get("halaman", [])
    tipe_diharapkan = ["Penjelasan", "Contoh", "Aktivitas 1", "Aktivitas 2"]
    if len(halaman) != 4:
        return False, f"Draft menghasilkan {len(halaman)} halaman, HARUS TEPAT 4 halaman dengan urutan: {tipe_diharapkan}."
    tipe_aktual = [h.get("tipePage") for h in halaman]
    if tipe_aktual != tipe_diharapkan:
        return False, f"Urutan/isi tipePage salah (dapat {tipe_aktual}). HARUS TEPAT: {tipe_diharapkan}, masing-masing satu kali, tanpa duplikat."
    return True, ""

def validasi_struktur_soal(soal_list: list) -> tuple[bool, str]:
    if not isinstance(soal_list, list) or len(soal_list) != 5:
        return False, f"Menghasilkan {len(soal_list) if isinstance(soal_list, list) else 0} soal, HARUS TEPAT 5 soal."
    for i, s in enumerate(soal_list):
        if not all(k in s for k in ["pertanyaan", "pilihan", "jawabanBenar"]):
            return False, f"Soal ke-{i+1} kehilangan field wajib (pertanyaan/pilihan/jawabanBenar)."
        if not isinstance(s["pilihan"], list) or len(s["pilihan"]) < 2:
            return False, f"Soal ke-{i+1} field 'pilihan' harus list berisi minimal 2 opsi."
        if not isinstance(s["jawabanBenar"], int) or not (0 <= s["jawabanBenar"] < len(s["pilihan"])):
            return False, f"Soal ke-{i+1} field 'jawabanBenar' harus angka index yang valid sesuai jumlah 'pilihan'."
    return True, ""

def evaluasi_soal(soal_list: list[dict], parameter: dict, kondisi_id: str) -> dict:
    info_kondisi  = TUJUAN_PER_KONDISI.get(kondisi_id, {})
    nama_kondisi  = info_kondisi.get("nama", f"Kondisi {kondisi_id}")
    tujuan_detail = info_kondisi.get("tujuan", "")

    blok_statis = f"""<role>
Kamu adalah evaluator kualitas soal pembelajaran tunagrahita.
Evaluasi berdasarkan rubrik 5 dimensi kognitif.
</role>

<batasan_kognitif>
Soal WAJIB berada dalam kategori Lower Order Thinking Skills (LOTS) menurut
Taksonomi Bloom. Soal yang menuntut analisis sebab-akibat kompleks,
evaluasi/perbandingan lebih dari 2 opsi mandiri, atau kreasi solusi baru
tanpa arahan (Higher Order Thinking Skills) TIDAK LULUS — beri skor
accessibility = 1, berapa pun nilai KI-07-nya.

- JIKA KI-07 = "Sedang" atau "Berat": soal HARUS berhenti di tingkat Memahami.
  Jika soal menuntut operasi hitung, beri skor accessibility = 1 dan sebutkan
  pelanggaran ini secara spesifik pada umpan_balik.
- JIKA KI-07 = "Ringan": soal BOLEH mengandung satu jenis operasi hitung tunggal.
  Jika lebih dari satu jenis operasi, beri skor accessibility = 2.
</batasan_kognitif>

<kondisi>
Kondisi yang diuji: {kondisi_id} — {nama_kondisi}
Tujuan pembelajaran yang harus diuji:
{tujuan_detail}
</kondisi>

<evaluation_criteria>
Berikan skor 1-5 untuk setiap dimensi:
1. Clarity       (KI-02): Kosakata & panjang kalimat soal sesuai kemampuan kebahasaan siswa
2. Simplicity    (KI-01,02): Pertanyaan singkat dan langsung — beri skor 1 jika pertanyaan bertele-tele atau berupa kalimat majemuk rumit
3. Repetition    (KI-03): Soal bersifat self-contained — beri skor 1 jika soal menuntut siswa mengingat informasi dari luar teks soal itu sendiri
4. Contextuality (KI-06): Relevansi konteks soal (kantin, koperasi, uang saku) dengan kehidupan sehari-hari
5. Accessibility (07): Kesesuaian dengan profil kognitif dan tujuan pembelajaran kondisi ini.
   Beri skor 1 jika ada soal yang membahas topik di luar tujuan pembelajaran kondisi ini.
   Untuk pelanggaran batasan kognitif, ikuti ketentuan skor pada <batasan_kognitif> di atas.

Kembalikan HANYA JSON:
{{
  "skor": {{
    "clarity": N, "simplicity": N, "repetition": N,
    "contextuality": N, "accessibility": N
  }},
  "rata_rata": N.N,
  "sudah_sesuai": true/false,
  "umpan_balik": "Instruksi perbaikan spesifik."
}}

sudah_sesuai = true jika semua skor >= 4
</evaluation_criteria>"""

    blok_dinamis = f"""<student_profile>
KI-01 Tingkat Atensi: {parameter['ki_01_tingkat_atensi']}
KI-02 Kemampuan Kebahasaan: {parameter['ki_02_kemampuan_kebahasaan']}
KI-03 Kapasitas Memori: {parameter['ki_03_kapasitas_memori']}
KI-06 Jenjang Kelas: {parameter['ki_06_jenjang_kelas']}
KI-07 Tingkatan Tunagrahita: {parameter['ki_07_tingkatan_tunagrahita']}
</student_profile>"""

    soal_str = json.dumps({"soal": soal_list}, ensure_ascii=False)

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=2000,
        system=[
            {"type": "text", "text": blok_statis, "cache_control": {"type": "ephemeral"}},
            {"type": "text", "text": blok_dinamis}
        ],
        messages=[
            {"role": "user", "content": f"<draft_soal>\n{soal_str}\n</draft_soal>\n\nEvaluasi draft soal ini."}
        ]
    )

    teks = response.content[0].text.strip()
    teks = teks.replace("```json", "").replace("```", "").strip()
    return parse_json_claude(teks)

def self_refine_soal(chunks: list[dict], parameter: dict, kondisi_id: str, max_iterasi: int = 3) -> dict:
    """
    Versi ringan: validasi struktur saja (Python murni, instan),
    TANPA evaluasi Evaluator Agent — menjaga kecepatan tetap dekat ke
    desain asli (generate cepat tiap diakses siswa). Batasan Bloom/KI-07
    tetap terjaga lewat <batasan_kognitif> di dalam generate_soal itu
    sendiri, bukan lewat verifikasi tambahan.
    """
    soal_terakhir = None
    umpan_balik = None

    for iterasi in range(1, max_iterasi + 1):
        soal_list = generate_soal(chunks, parameter, kondisi_id, umpan_balik)
        soal_terakhir = soal_list

        valid, pesan_error = validasi_struktur_soal(soal_list)
        if valid:
            return {"soal": soal_list, "iterasi": iterasi}

        umpan_balik = pesan_error

    return {"soal": soal_terakhir, "iterasi": max_iterasi}