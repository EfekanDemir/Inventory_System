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
                if (context instanceof LoginActivity) {
                    Toast.makeText(context, "Kullanıcı adı veya şifre hatalı.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Oturum süresi doldu. Lütfen tekrar giriş yapın.", Toast.LENGTH_LONG).show();
                    logout(context);
                }
                break;
            case 403:
                Toast.makeText(context, "Bu işlem için yetkiniz yok (403).", Toast.LENGTH_SHORT).show();
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
        SharedPreferences prefs = context.getSharedPreferences("envanter_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
