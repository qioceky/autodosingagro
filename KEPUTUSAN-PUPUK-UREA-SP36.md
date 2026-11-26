# Auto Dosing — Bahan keputusan: tambah Urea & SP-36 ke Kalkulator?

Disiapkan 2026-11-26 17:32 WIB (proactivity research). Belum ada kode diubah.

## Pertanyaan yang sudah terkirim (jangan tanya ulang)

v1.12.0 rilis 2026-11-26 17:26 WIB (CI 45800751745, APK 8.576.199 byte,
SHA-256 5e18a025…ea). Di pesan rilis yang sama asisten menanyakan konflik:
dokumen SISTEM_APLIKASI_PERTANIAN menulis 6 pupuk kalkulator
(NPK, KCl, UREA, SP36, KNO3, MKP) vs ketetapan user 2026-11-19 hanya 4
(NPK, KNO3, MKP, KCl). Sementara dipertahankan 4. Per 17:32 WIB belum ada
jawaban user (has_newer: false sesudah pesan receipt).

## Temuan baru yang membantu keputusan (belum disampaikan ke user)

Jadwal pupuk per tanaman di aplikasi SENDIRI sudah memakai Urea & SP-36,
tapi Kalkulator tidak bisa menghitung keduanya:

- `data/Data.kt` Padi: "Urea 225 kg/ha, SP-36 100 kg/ha, KCl 100 kg/ha"
  (Dasar Urea 75 + SP-36 100 + KCl 50; Susulan 1 Urea 75 + KCl 50;
  Susulan 2 Urea 75).
- Jagung: Urea 350 / SP-36 150 / KCl 100 (Dasar Urea 100 + SP-36 150 +
  KCl 100; Susulan 1 & 2 Urea 125).
- Kedelai: Urea 50 / SP-36 100 / KCl 100, semua dasar.
- Bawang Merah: SP-36 200 dasar; Tomat: SP-36 dasar (deskripsi).

Akibatnya sekarang: layar Jadwal Pupuk merekomendasikan dosis Urea/SP-36,
tapi kalau user membuka Kalkulator Dosis, dua pupuk itu tidak ada di
pilihan nomor 4 — dosis per tanaman/petak untuk keduanya tidak bisa
dihitung di aplikasi. Menambah Urea & SP-36 menyelaraskan Kalkulator
dengan jadwalnya sendiri, bukan sekadar menyamakan dokumen.

## Bentuk perubahan bila user setuju (satu file data + tanpa ubah UI)

`PupukDB.semua` di `data/Data.kt:320-327` saat ini persis 4:

- NPK 150 kg/ha umum, bisaKocor, faktorKocor 0,67
- KNO3 50, bisaKocor, faktor 0,8
- MKP 25, bisaKocor, faktor 1,0
- KCl 50, bisaKocor, faktor 0,7

Kalkulator (`Screens.kt` DosisScreen) sudah generik: chip dibuat dari
`PupukDB.semua.chunked(2)`, dosis diambil dari jadwal tanaman bila ada
(`dariJadwal`) atau `dosisUmumKgPerHa`, faktor kocor per pupuk, dan
peringatan "tidak larut air — jangan dikocor" sudah ada bila
`bisaKocor == false` (Screens.kt:365). Jadi menambah 2 entri PupukDB
cukup; tidak perlu ubah layout. Dosis dari jadwal (Padi/Jagung/dst)
otomatis terpakai karena pencocokan nama sudah case-insensitive.

## Yang masih butuh ketetapan user / modul ATPH (jangan karang)

1. **UREA**: dosis umum per aplikasi (kg/ha) + faktor kocor.
   Urea larut air tapi mudah menguap (aplikasi sendiri sudah memperingatkan
   ini di panduan Tabur Kemarau). Pilihan: (a) bisaKocor dengan faktor
   tertentu, atau (b) tabur saja. Angka faktor TIDAK boleh dikarang —
   minta dari user/modul.
2. **SP-36 (SP36)**: pupuk fosfat sukar larut → usulan `bisaKocor = false`
   (peringatan tabur otomatis muncul), dosis umum per aplikasi perlu
   ketetapan. Nama tampilan: dokumen menulis "SP36", jadwal menulis
   "SP-36" — samakan satu nama agar pencocokan jadwal ketemu
   (pencocokan saat ini exact-ignore-case, "SP36" ≠ "SP-36").

## Rekomendasi penyampaian

- JANGAN kirim pertanyaan kedua sekarang — widget keputusan baru
  terkirim 17:26 WIB hari ini dan belum dijawab.
- Bila user menjawab "tambah": bangun dari bahan ini, tanyakan hanya
  dua angka di atas (faktor Urea + dosis umum), rilis seperti biasa
  (CI conclusion + artefak + apksigner).
- Bila user menjawab "tetap 4": catat keputusan, tutup konflik, tidak
  ada perubahan kode. Jadwal tetap menampilkan Urea/SP-36 sebagai
  rekomendasi (itu data tanaman, bukan pilihan kalkulator).
