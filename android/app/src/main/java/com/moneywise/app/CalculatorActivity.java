package com.moneywise.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * MONEYWISE - Kalkulator Pengeluaran Bulanan Mahasiswa
 * Menampung 5 input, memvalidasi form, melakukan kalkulasi finansial,
 * dan meneruskan hasil ke ResultActivity.
 */
public class CalculatorActivity extends AppCompatActivity {

    // 5 EditText input finansial mahasiswa
    private EditText etMonthlyBudget;
    private EditText etFood;
    private EditText etTransport;
    private EditText etInternet;
    private EditText etCollege;

    private Button btnCalculate;
    private Button btnBackToDashboard;

    // Kunci Intent Extras
    public static final String EXTRA_BUDGET      = "EXTRA_BUDGET";
    public static final String EXTRA_FOOD        = "EXTRA_FOOD";
    public static final String EXTRA_TRANSPORT   = "EXTRA_TRANSPORT";
    public static final String EXTRA_INTERNET    = "EXTRA_INTERNET";
    public static final String EXTRA_COLLEGE     = "EXTRA_COLLEGE";
    public static final String EXTRA_TOTAL       = "EXTRA_TOTAL";
    public static final String EXTRA_REMAINING   = "EXTRA_REMAINING";
    public static final String EXTRA_PERCENTAGE  = "EXTRA_PERCENTAGE";
    public static final String EXTRA_STATUS      = "EXTRA_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etMonthlyBudget    = findViewById(R.id.etMonthlyBudget);
        etFood             = findViewById(R.id.etFood);
        etTransport        = findViewById(R.id.etTransport);
        etInternet         = findViewById(R.id.etInternet);
        etCollege          = findViewById(R.id.etCollege);

        btnCalculate       = findViewById(R.id.btnCalculate);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
    }

    private void setupListeners() {
        // Tombol Hitung Pengeluaran dengan setOnClickListener()
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndProceed();
            }
        });

        // Tombol Kembali ke Dashboard
        btnBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Memvalidasi input dan menghitung total pengeluaran, sisa saldo, dan persentase
     */
    private void calculateAndProceed() {
        String strBudget    = etMonthlyBudget.getText().toString().trim();
        String strFood      = etFood.getText().toString().trim();
        String strTransport = etTransport.getText().toString().trim();
        String strInternet  = etInternet.getText().toString().trim();
        String strCollege   = etCollege.getText().toString().trim();

        // 1. Validasi Input Kosong
        if (strBudget.isEmpty() || strFood.isEmpty() || strTransport.isEmpty() ||
            strInternet.isEmpty() || strCollege.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi! Jangan biarkan kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget, food, transport, internet, college;

        // 2. Validasi Format Numerik
        try {
            budget    = Double.parseDouble(strBudget);
            food      = Double.parseDouble(strFood);
            transport = Double.parseDouble(strTransport);
            internet  = Double.parseDouble(strInternet);
            college   = Double.parseDouble(strCollege);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid! Masukkan hanya digit angka.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Validasi Uang Bulanan Harus Lebih Besar dari Nol
        if (budget <= 0) {
            Toast.makeText(this, "Uang bulanan harus lebih besar dari Rp 0!", Toast.LENGTH_SHORT).show();
            etMonthlyBudget.requestFocus();
            return;
        }

        // 4. Validasi Nilai Negatif
        if (food < 0 || transport < 0 || internet < 0 || college < 0) {
            Toast.makeText(this, "Nilai pengeluaran tidak boleh bernilai negatif!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Kalkulasi Finansial
        double totalPengeluaran = food + transport + internet + college;
        double sisaSaldo        = budget - totalPengeluaran;
        double persentase       = (totalPengeluaran / budget) * 100.0;

        // Tentukan Kategori Status Finansial Mahasiswa
        String status;
        if (persentase <= 70.0) {
            status = "Aman (Hemat)";
        } else if (persentase <= 100.0) {
            status = "Waspada (Mendekati Batas)";
        } else {
            status = "Defisit (Overbudget)";
        }

        // Berikan feedback instan jika overbudget
        if (sisaSaldo < 0) {
            Toast.makeText(this, "Peringatan: Pengeluaran melebihi uang bulanan Anda!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Perhitungan berhasil diselesaikan!", Toast.LENGTH_SHORT).show();
        }

        // Kirim hasil ke ResultActivity melalui Intent
        Intent intent = new Intent(CalculatorActivity.this, ResultActivity.class);
        intent.putExtra(EXTRA_BUDGET, budget);
        intent.putExtra(EXTRA_FOOD, food);
        intent.putExtra(EXTRA_TRANSPORT, transport);
        intent.putExtra(EXTRA_INTERNET, internet);
        intent.putExtra(EXTRA_COLLEGE, college);
        intent.putExtra(EXTRA_TOTAL, totalPengeluaran);
        intent.putExtra(EXTRA_REMAINING, sisaSaldo);
        intent.putExtra(EXTRA_PERCENTAGE, persentase);
        intent.putExtra(EXTRA_STATUS, status);

        startActivity(intent);
    }
}

