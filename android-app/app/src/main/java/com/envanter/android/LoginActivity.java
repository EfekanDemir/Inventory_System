package com.envanter.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.LoginRequest;
import com.envanter.android.model.UserDTO;
import com.envanter.android.util.ApiErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

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
        // Login sirasinda context göndererek ApiClient'i baslatiyoruz
        ApiClient.getClient(this).create(ApiService.class).login(request).enqueue(new Callback<GenericResponse<UserDTO>>() {
            @Override
            public void onResponse(Call<GenericResponse<UserDTO>> call, Response<GenericResponse<UserDTO>> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    saveToken(response.body().getData().getToken());
                    
                    // KRITIK: Login basarili olunca eski (tokensiz) client'i temizle
                    ApiClient.clearClient();
                    
                    Toast.makeText(LoginActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    ApiErrorHandler.handleError(LoginActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<UserDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Bağlantı Hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("envanter_prefs", MODE_PRIVATE);
        prefs.edit().putString("jwt_token", token).apply();
    }
}
