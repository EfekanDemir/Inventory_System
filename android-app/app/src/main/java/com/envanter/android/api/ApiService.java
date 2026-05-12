package com.envanter.android.api;

import com.envanter.android.model.ItemDTO;
import com.envanter.android.model.ItemRequest;
import com.envanter.android.model.LoginRequest;
import com.envanter.android.model.RegisterRequest;
import com.envanter.android.model.StockMovementDTO;
import com.envanter.android.model.StockMovementRequest;
import com.envanter.android.model.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/users/register")
    Call<GenericResponse<UserDTO>> register(@Body RegisterRequest request);

    @POST("/api/users/login")
    Call<GenericResponse<UserDTO>> login(@Body LoginRequest request);

    @GET("/api/inventory/items")
    Call<GenericResponse<List<ItemDTO>>> getItems(@Header("Authorization") String token);

    @POST("/api/inventory/items")
    Call<GenericResponse<ItemDTO>> createItem(@Header("Authorization") String token, @Body ItemRequest request);

    @POST("/api/inventory/movements")
    Call<GenericResponse<StockMovementDTO>> createMovement(@Header("Authorization") String token, @Body StockMovementRequest request);
}
