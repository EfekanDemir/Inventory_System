package com.envanter.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.envanter.android.LoginActivity;

public class ApiErrorHandler {

    public static void handleError(Context context, int statusCode) {
        switch (statusCode) {
            case 401:
                Toast.makeText(context, "Oturum süresi doldu. Lütfen tekrar giriş yapın.", Toast.LENGTH_LONG).show();
                logout(context);
                break;
            case 404:
                Toast.makeText(context, "Kayıt bulunamadı (404).", Toast.LENGTH_SHORT).show();
                break;
            case 409:
                Toast.makeText(context, "Yetersiz stok veya çakışan işlem (409).", Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(context, "Sunucu hatası. Lütfen daha sonra tekrar deneyin.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "Bilinmeyen bir hata oluştu: " + statusCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static void logout(Context context) {
        // Token'i temizle
        SharedPreferences prefs = context.getSharedPreferences("envanter_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();

        // Login ekranina yonlendir ve backstack'i temizle
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
