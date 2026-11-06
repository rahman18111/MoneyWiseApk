# MoneyWise - Student Monthly Expense Calculator 💰📊

MoneyWise adalah aplikasi Android kalkulator pengeluaran bulanan mahasiswa yang terintegrasi dengan backend PHP API dan database MySQL (XAMPP).

---

## 📱 Fitur Utama
- **Dashboard Ringkas**: Navigasi cepat ke kalkulator, riwayat data, dan tips hemat keuangan mahasiswa.
- **Kalkulator Pengeluaran**: Input anggaran bulanan serta alokasi makan, transportasi, kuota internet, dan kebutuhan kuliah.
- **Analisis & Status Keuangan**: Perhitungan total pengeluaran, sisa saldo, persentase pengeluaran, serta indikator status keuangan (Aman / Waspada / Bahaya).
- **Simpan ke Database MySQL**: Menyimpan riwayat kalkulasi langsung ke server backend XAMPP secara real-time via REST API.
- **Riwayat Pengeluaran**: Menampilkan daftar riwayat perhitungan pengeluaran sebelumnya yang tersimpan di database.

---

## 🛠️ Struktur Project

```text
MoneyWiseApk/
├── android/            # Source code aplikasi Android (Java / Android Studio)
│   ├── app/
│   │   ├── src/main/java/com/moneywise/app/
│   │   │   ├── CalculatorActivity.java
│   │   │   ├── HistoryActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── ResultActivity.java
│   │   │   ├── model/ExpenseRecord.java
│   │   │   ├── network/ApiConfig.java & HttpHelper.java
│   │   │   └── util/CurrencyHelper.java
│   │   └── src/main/res/
│   └── build.gradle
├── api/                # Backend REST API (PHP)
│   ├── db_connect.php
│   ├── get_history.php
│   ├── save_expense.php
│   └── test_api.php
└── database/           # Skema Database MySQL
    └── db_moneywise.sql
```

---

## 🚀 Panduan Instalasi & Penggunaan

### 1. Backend & Database (XAMPP)
1. Nyalakan service **Apache** dan **MySQL** di XAMPP Control Panel.
2. Buka `phpMyAdmin` (`http://localhost/phpmyadmin`).
3. Buat database baru bernama `db_moneywise` (atau import langsung file `database/db_moneywise.sql`).
4. Pastikan folder project berada di `C:\xampp\htdocs\MoneyWiseApk`.

### 2. Konfigurasi API Android
1. Buka file `android/app/src/main/java/com/moneywise/app/network/ApiConfig.java`.
2. Sesuaikan IP server / host:
   - Jika menggunakan **Android Emulator**: gunakan `10.0.2.2` (contoh: `http://10.0.2.2/MoneyWiseApk/api/`).
   - Jika menggunakan **HP Fisik (Real Device)**: gunakan alamat IP lokal laptop/PC Anda (contoh: `http://192.168.x.x/MoneyWiseApk/api/`).

### 3. Menjalankan Aplikasi Android
1. Buka folder `android/` di **Android Studio**.
2. Sinkronkan Gradle (Sync Project with Gradle Files).
3. Jalankan aplikasi di Emulator atau perangkat Android Anda.
