package com.envanter.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static final String API_KEY = "envanter-api-key-2026";
    private static volatile Retrofit retrofit = null;

    /**
     * Singleton Retrofit döndürür.
     * Token her istekte SharedPreferences'tan taze okunur — client yeniden yaratılmaz.
     * OkHttpClient + Retrofit sadece bir kez oluşturulur → bağlantı havuzu (connection pool)
     * tekrar kullanılır, GC baskısı düşer, UI donması önlenir.
     */
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null && context != null) {
                    retrofit = buildRetrofit(context.getApplicationContext());
                }
            }
        }
        return retrofit;
    }

    private static Retrofit buildRetrofit(Context appContext) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC); // BODY → BASIC: daha az log, daha az String alloc

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("apikey", API_KEY);

                    // Token her istekte taze okunur — client singleton kalsa bile güncel token gider
                    SharedPreferences prefs = appContext.getSharedPreferences("envanter_prefs", Context.MODE_PRIVATE);
                    String token = prefs.getString("jwt_token", null);

                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }

                    return chain.proceed(requestBuilder.build());
                })
                .addInterceptor(loggingInterceptor) // Logging en sona — header'lar dahil loglanır
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Login/logout sonrası çağrılır — bir sonraki getClient() taze client oluşturur.
     */
    public static void clearClient() {
        retrofit = null;
    }
}
