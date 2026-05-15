package com.envanter.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.envanter.android.LoginActivity;

public class ApiErrorHandler {

    public static void handleError(Context context, int statusCode) {
        handleError(context, statusCode, null);
    }

    public static void handleError(Context context, int statusCode, String customMessage) {
        String baseMessage;
        switch (statusCode) {
            case 401:
                if (context instanceof LoginActivity) {
                    baseMessage = "Kullanıcı adı veya şifre hatalı.";
                } else {
                    baseMessage = "Oturum süresi doldu. Lütfen tekrar giriş yapın.";
                    logout(context);
                }
                break;
            case 403:
                baseMessage = "Bu işlem için yetkiniz yok (403).";
                break;
            case 404:
                baseMessage = "Kayıt bulunamadı (404).";
                break;
            case 500:
                baseMessage = "Sunucu hatası.";
                break;
            default:
                baseMessage = "Hata kodu: " + statusCode;
                break;
        }

        String finalMessage = (customMessage != null) ? customMessage + ": " + baseMessage : baseMessage;
        Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
    }

    public static void handleFailure(Context context, Throwable t) {
        Log.e("ApiErrorHandler", "Network failure", t);
        Toast.makeText(context, "Bağlantı hatası: Sunucuya ulaşılamıyor.", Toast.LENGTH_LONG).show();
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("envanter_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
