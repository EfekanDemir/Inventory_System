package com.envanter.android.repository;

import android.content.Context;

import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.ItemDTO;
import com.envanter.android.model.ItemRequest;

import java.util.List;

import retrofit2.Callback;

public class InventoryRepository {

    private final ApiService apiService;

    public InventoryRepository(Context context) {
        // ApiClient.getClient(context) sayesinde her istege token otomatik ekleniyor.
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getItems(Callback<GenericResponse<List<ItemDTO>>> callback) {
        // null, null gondererek tum urunleri filtrelemeden istiyoruz.
        apiService.getItems(null, null).enqueue(callback);
    }

    public void createItem(ItemRequest request, Callback<GenericResponse<ItemDTO>> callback) {
        apiService.createItem(request).enqueue(callback);
    }

    public void updateItem(Long id, ItemRequest request, Callback<GenericResponse<ItemDTO>> callback) {
        apiService.updateItem(id, request).enqueue(callback); 
    }

    public void deleteItem(Long id, Callback<GenericResponse<Void>> callback) {
        apiService.deleteItem(id).enqueue(callback);
    }
}
