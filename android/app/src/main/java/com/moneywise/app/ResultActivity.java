package com.moneywise.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moneywise.app.network.ApiConfig;
import com.moneywise.app.network.HttpHelper;
import com.moneywise.app.util.CurrencyHelper;

import org.json.JSONObject;

/**
 * MONEYWISE - Halaman Hasil Perhitungan Pengeluaran
 * Menampilkan ringkasan status keuangan mahasiswa dan menyediakan fitur simpan ke MySQL via PHP API
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvStatusBadge;
    private TextView tvStatusDescription;
    private TextView tvMonthlyBudget;
    private TextView tvTotalExpense;
    private TextView tvRemainingBalance;
    private TextView tvPercentage;

    private TextView tvDetailFood;
    private TextView tvDetailTransport;
    private TextView tvDetailInternet;
    private TextView tvDetailCollege;

    private Button btnSaveToDb;
    private Button btnResultViewHistory;
    private Button btnRecalculate;
    private ProgressBar pbSaving;

    // Nilai-nilai pengeluaran
    private double budget;
    private double food;
    private double transport;
    private double internet;
    private double college;
    private double totalExpense;
    private double remainingBalance;
    private double percentage;
    private String status;

    private boolean isAlreadySaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
        extractIntentData();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        tvStatusBadge        = findViewById(R.id.tvStatusBadge);
        tvStatusDescription  = findViewById(R.id.tvStatusDescription);
        tvMonthlyBudget      = findViewById(R.id.tvMonthlyBudget);
        tvTotalExpense       = findViewById(R.id.tvTotalExpense);
        tvRemainingBalance   = findViewById(R.id.tvRemainingBalance);
        tvPercentage         = findViewById(R.id.tvPercentage);

        tvDetailFood         = findViewById(R.id.tvDetailFood);
        tvDetailTransport    = findViewById(R.id.tvDetailTransport);
        tvDetailInternet     = findViewById(R.id.tvDetailInternet);
        tvDetailCollege      = findViewById(R.id.tvDetailCollege);

        btnSaveToDb          = findViewById(R.id.btnSaveToDb);
        btnResultViewHistory = findViewById(R.id.btnResultViewHistory);
        btnRecalculate       = findViewById(R.id.btnRecalculate);
        pbSaving             = findViewById(R.id.pbSaving);
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            budget           = intent.getDoubleExtra(CalculatorActivity.EXTRA_BUDGET, 0.0);
            food             = intent.getDoubleExtra(CalculatorActivity.EXTRA_FOOD, 0.0);
            transport        = intent.getDoubleExtra(CalculatorActivity.EXTRA_TRANSPORT, 0.0);
            internet         = intent.getDoubleExtra(CalculatorActivity.EXTRA_INTERNET, 0.0);
            college          = intent.getDoubleExtra(CalculatorActivity.EXTRA_COLLEGE, 0.0);
            totalExpense     = intent.getDoubleExtra(CalculatorActivity.EXTRA_TOTAL, 0.0);
            remainingBalance = intent.getDoubleExtra(CalculatorActivity.EXTRA_REMAINING, 0.0);
            percentage       = intent.getDoubleExtra(CalculatorActivity.EXTRA_PERCENTAGE, 0.0);
            status           = intent.getStringExtra(CalculatorActivity.EXTRA_STATUS);
            if (status == null) status = "Aman (Hemat)";
        }
    }

    private void displayResults() {
        tvMonthlyBudget.setText(CurrencyHelper.formatRupiah(budget));
        tvTotalExpense.setText(CurrencyHelper.formatRupiah(totalExpense));
        tvRemainingBalance.setText(CurrencyHelper.formatRupiah(remainingBalance));
        tvPercentage.setText(CurrencyHelper.formatPercentage(percentage));

        tvDetailFood.setText(CurrencyHelper.formatRupiah(food));
        tvDetailTransport.setText(CurrencyHelper.formatRupiah(transport));
        tvDetailInternet.setText(CurrencyHelper.formatRupiah(internet));
        tvDetailCollege.setText(CurrencyHelper.formatRupiah(college));

        // Format Badge Status & Warna
        if (status.contains("Aman")) {
            tvStatusBadge.setText("Status: Aman (Hemat)");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_safe);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.safe_badge_text));
            tvRemainingBalance.setTextColor(ContextCompat.getColor(this, R.color.safe_badge_text));
            tvStatusDescription.setText("Luar biasa! Pengeluaran terkendali dengan baik di bawah 70%.");
        } else if (status.contains("Waspada")) {
            tvStatusBadge.setText("Status: Waspada (Mendekati Batas)");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.warning_badge_text));
            tvRemainingBalance.setTextColor(ContextCompat.getColor(this, R.color.warning_badge_text));
            tvStatusDescription.setText("Perhatian! Pengeluaran telah mencapai 70% - 100% dari anggaran saku Anda.");
        } else {
            tvStatusBadge.setText("Status: Defisit (Overbudget)");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_danger);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.danger_badge_text));
            tvRemainingBalance.setTextColor(ContextCompat.getColor(this, R.color.danger_badge_text));
            tvStatusDescription.setText("Bahaya! Pengeluaran melebihi uang bulanan Anda. Segera hemat pengeluaran sekunder.");
        }
    }

    private void setupListeners() {
        // Tombol Simpan ke Database MySQL via PHP API
        btnSaveToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlreadySaved) {
                    Toast.makeText(ResultActivity.this, "Data ini sudah tersimpan di database!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveExpenseToDatabase();
            }
        });

        // Tombol Lihat Riwayat
        btnResultViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Tombol Hitung Ulang
        btnRecalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Mengirim payload JSON ke API PHP save_expense.php di background thread
     */
    private void saveExpenseToDatabase() {
        pbSaving.setVisibility(View.VISIBLE);
        btnSaveToDb.setEnabled(false);

        try {
            JSONObject payload = new JSONObject();
            payload.put("uang_bulanan", budget);
            payload.put("makan", food);
            payload.put("transportasi", transport);
            payload.put("kuota", internet);
            payload.put("kuliah", college);
            payload.put("total_pengeluaran", totalExpense);
            payload.put("sisa_saldo", remainingBalance);
            payload.put("persentase", percentage);
            payload.put("status", status);

            HttpHelper.postJson(ApiConfig.URL_SAVE_EXPENSE, payload.toString(), new HttpHelper.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    pbSaving.setVisibility(View.GONE);
                    try {
                        JSONObject resObj = new JSONObject(response);
                        String msg = resObj.optString("message", "Data pengeluaran berhasil disimpan ke MySQL!");
                        Toast.makeText(ResultActivity.this, msg, Toast.LENGTH_LONG).show();
                        isAlreadySaved = true;
                        btnSaveToDb.setText("Tersimpan di Database ✓");
                        btnSaveToDb.setBackgroundColor(ContextCompat.getColor(ResultActivity.this, R.color.text_hint));
                    } catch (Exception e) {
                        Toast.makeText(ResultActivity.this, "Data tersimpan!", Toast.LENGTH_SHORT).show();
                        isAlreadySaved = true;
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    pbSaving.setVisibility(View.GONE);
                    btnSaveToDb.setEnabled(true);
                    Toast.makeText(ResultActivity.this, "Gagal menyimpan: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            pbSaving.setVisibility(View.GONE);
            btnSaveToDb.setEnabled(true);
            Toast.makeText(this, "Kesalahan payload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

