package com.moneywise.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * MONEYWISE - Dashboard Mahasiswa
 * Halaman utama aplikasi yang menyajikan ringkasan dan akses cepat ke kalkulator & riwayat
 */
public class MainActivity extends AppCompatActivity {

    private Button btnMainCalculator;
    private Button btnMainHistory;
    private LinearLayout cardCalculator;
    private LinearLayout cardHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnMainCalculator = findViewById(R.id.btnMainCalculator);
        btnMainHistory    = findViewById(R.id.btnMainHistory);
        cardCalculator    = findViewById(R.id.cardCalculator);
        cardHistory       = findViewById(R.id.cardHistory);
    }

    private void setupListeners() {
        // Navigasi ke Kalkulator Pengeluaran
        View.OnClickListener openCalculatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        };
        btnMainCalculator.setOnClickListener(openCalculatorListener);
        cardCalculator.setOnClickListener(openCalculatorListener);

        // Navigasi ke Riwayat Pengeluaran
        View.OnClickListener openHistoryListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        };
        btnMainHistory.setOnClickListener(openHistoryListener);
        cardHistory.setOnClickListener(openHistoryListener);
    }
}

