package com.envanter.android.repository;

import android.content.Context;

import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.StockMovementDTO;

import java.util.List;

import retrofit2.Callback;

public class MovementRepository {

    private final ApiService apiService;

    public MovementRepository(Context context) {
        // ApiClient.getClient(context) kullanarak token'ın otomatik eklenmesini sağlıyoruz
        apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getMovements(Callback<GenericResponse<List<StockMovementDTO>>> callback) {
        // Parametre olarak token göndermeye gerek kalmadı
        apiService.getMovements().enqueue(callback);
    }

    public void getItemMovements(Long itemId, Callback<GenericResponse<List<StockMovementDTO>>> callback) {
        apiService.getItemMovements(itemId).enqueue(callback);
    }

    public void createMovement(com.envanter.android.model.StockMovementRequest request, Callback<GenericResponse<StockMovementDTO>> callback) {
        apiService.createMovement(request).enqueue(callback);
    }

    public void clearMovements(Callback<GenericResponse<String>> callback) {
        apiService.clearMovements().enqueue(callback);
    }
}
