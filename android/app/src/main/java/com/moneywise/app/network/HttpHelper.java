package com.moneywise.app.network;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper komunikasi jaringan HTTP menggunakan background thread (ExecutorService)
 * dan mengembalikan respon ke UI Thread secara aman melalui Handler Looper.
 */
public class HttpHelper {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int TIMEOUT_MILLIS = 10000;

    public interface HttpCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }

    /**
     * Mengirim HTTP POST dengan Body JSON di background thread.
     */
    public static void postJson(final String targetUrl, final String jsonBody, final HttpCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(targetUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(TIMEOUT_MILLIS);
                    conn.setReadTimeout(TIMEOUT_MILLIS);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Tulis JSON body
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(input, 0, input.length);
                        os.flush();
                    }

                    int responseCode = conn.getResponseCode();
                    InputStream is = (responseCode >= 200 && responseCode < 400)
                            ? conn.getInputStream()
                            : conn.getErrorStream();

                    StringBuilder sb = new StringBuilder();
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    }

                    final String resultString = sb.toString();

                    if (responseCode >= 200 && responseCode < 300) {
                        dispatchSuccess(callback, resultString);
                    } else {
                        dispatchError(callback, "HTTP " + responseCode + ": " + (resultString.isEmpty() ? conn.getResponseMessage() : resultString));
                    }

                } catch (Exception e) {
                    dispatchError(callback, "Gagal koneksi ke server: " + e.getLocalizedMessage());
                } finally {
                    if (reader != null) {
                        try { reader.close(); } catch (Exception ignored) {}
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
    }

    /**
     * Mengirim HTTP GET di background thread.
     */
    public static void get(final String targetUrl, final HttpCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(targetUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(TIMEOUT_MILLIS);
                    conn.setReadTimeout(TIMEOUT_MILLIS);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    InputStream is = (responseCode >= 200 && responseCode < 400)
                            ? conn.getInputStream()
                            : conn.getErrorStream();

                    StringBuilder sb = new StringBuilder();
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    }

                    final String resultString = sb.toString();

                    if (responseCode >= 200 && responseCode < 300) {
                        dispatchSuccess(callback, resultString);
                    } else {
                        dispatchError(callback, "HTTP " + responseCode + ": " + (resultString.isEmpty() ? conn.getResponseMessage() : resultString));
                    }

                } catch (Exception e) {
                    dispatchError(callback, "Gagal koneksi ke server: " + e.getLocalizedMessage());
                } finally {
                    if (reader != null) {
                        try { reader.close(); } catch (Exception ignored) {}
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
    }

    private static void dispatchSuccess(final HttpCallback callback, final String response) {
        if (callback == null) return;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response);
            }
        });
    }

    private static void dispatchError(final HttpCallback callback, final String errorMessage) {
        if (callback == null) return;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(errorMessage);
            }
        });
    }
}

