package com.envanter.android.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Emülatör localhost port 8000
    private static final String BASE_URL = "http://10.0.2.2:8000";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            
            // HTTP Loglarini gormek icin Interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient yapilandirmasi (Timeout + JWT Interceptor)
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            
                            // SharedPreferences'ten JWT token'i al
                            SharedPreferences prefs = context.getSharedPreferences("envanter_prefs", Context.MODE_PRIVATE);
                            String token = prefs.getString("jwt_token", null);

                            Request.Builder requestBuilder = original.newBuilder();
                            
                            // Eger token varsa ve header onceden eklenmemisse Authorization Bearer ekle
                            if (token != null && !token.isEmpty()) {
                                if (original.header("Authorization") == null) {
                                    requestBuilder.header("Authorization", "Bearer " + token);
                                }
                            }

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            // Retrofit Instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
