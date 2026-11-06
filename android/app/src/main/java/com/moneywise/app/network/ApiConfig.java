package com.moneywise.app.network;

/**
 * Konfigurasi URL Endpoints API Backend PHP XAMPP
 */
public class ApiConfig {

    /**
     * CATATAN PENTING KONEKSI JARINGAN:
     * 1. Jika menggunakan ANDROID EMULATOR (AVD):
     *    Gunakan IP default loopback host: "http://10.0.2.2/MoneyWiseApk/api/"
     *
     * 2. Jika menggunakan HP FISIK (REAL DEVICE) via WiFi:
     *    Ganti 10.0.2.2 dengan IP LAN laptop/komputer Anda (cek via cmd: ipconfig).
     *    Contoh: "http://192.168.1.15/MoneyWiseApk/api/"
     */
    public static final String BASE_URL = "http://10.0.2.2/MoneyWiseApk/api/";

    // Endpoint simpan data pengeluaran
    public static final String URL_SAVE_EXPENSE = BASE_URL + "save_expense.php";

    // Endpoint ambil data riwayat pengeluaran
    public static final String URL_GET_HISTORY = BASE_URL + "get_history.php";
}

