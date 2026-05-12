package com.envanter.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.LoginRequest;
import com.envanter.android.model.UserDTO;
import com.envanter.android.util.ApiErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-login (Oturum hatirlama) mekanizmasi
        SharedPreferences prefs = getSharedPreferences("envanter_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token != null && !token.isEmpty()) {
            goToDashboard();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(username, password);
        Call<GenericResponse<UserDTO>> call = apiService.login(request);

        call.enqueue(new Callback<GenericResponse<UserDTO>>() {
            @Override
            public void onResponse(Call<GenericResponse<UserDTO>> call, Response<GenericResponse<UserDTO>> response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                });

                if (response.isSuccessful() && response.body() != null) {
                    // Token basariyla alindi, SharedPreferences'e kaydet
                    saveToken(response.body().getData().getToken());
                    Toast.makeText(LoginActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                    goToDashboard();
                } else {
                    // Merkezi Hata Yonetimi
                    runOnUiThread(() -> ApiErrorHandler.handleError(LoginActivity.this, response.code()));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<UserDTO>> call, Throwable t) {
                Log.e("API_ERROR", "Login failed", t);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Ağ Hatası: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("envanter_prefs", MODE_PRIVATE);
        prefs.edit().putString("jwt_token", token).apply();
    }

    private void goToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
