import requests
import time

BASE_URL = "http://localhost:8000"
SISWA_IDS = [1, 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]
KONDISI_IDS = ["A", "B", "C", "D", "E", "F"]

total = len(SISWA_IDS) * len(KONDISI_IDS)
selesai = 0
gagal = []

for siswa_id in SISWA_IDS:
    for kondisi_id in KONDISI_IDS:
        selesai += 1
        print(f"[{selesai}/{total}] Generate siswa {siswa_id} kondisi {kondisi_id}...")
        try:
            resp = requests.get(
                f"{BASE_URL}/pipeline/konten/{siswa_id}/{kondisi_id}",
                timeout=180
            )
            status = resp.json().get('status', 'error')
            print(f"  → {status}")
        except Exception as e:
            print(f"  → GAGAL: {e}")
            gagal.append(f"siswa {siswa_id} kondisi {kondisi_id}")
        time.sleep(1)

print("\n=== SELESAI ===")
print(f"Berhasil: {total - len(gagal)}/{total}")
if gagal:
    print("Yang gagal:")
    for g in gagal:
        print(f"  - {g}")
