package com.envanter.android.api;

import com.envanter.android.model.CategoryDTO;
import com.envanter.android.model.ItemDTO;
import com.envanter.android.model.ItemRequest;
import com.envanter.android.model.LoginRequest;
import com.envanter.android.model.RegisterRequest;
import com.envanter.android.model.StockMovementDTO;
import com.envanter.android.model.StockMovementRequest;
import com.envanter.android.model.StockReportDTO;
import com.envanter.android.model.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/users/register")
    Call<GenericResponse<UserDTO>> register(@Body RegisterRequest request);

    @POST("/api/users/login")
    Call<GenericResponse<UserDTO>> login(@Body LoginRequest request);

    @GET("/api/inventory/items")
    Call<GenericResponse<List<ItemDTO>>> getItems(@Query("categoryId") Long categoryId, 
                                                  @Query("search") String search);

    @GET("/api/inventory/items/low-stock")
    Call<GenericResponse<List<ItemDTO>>> getLowStockItems();

    @GET("/api/inventory/items/report")
    Call<GenericResponse<StockReportDTO>> getStockReport();

    @POST("/api/inventory/items")
    Call<GenericResponse<ItemDTO>> createItem(@Body ItemRequest request);

    @PUT("/api/inventory/items/{id}")
    Call<GenericResponse<ItemDTO>> updateItem(@Path("id") Long id, @Body ItemRequest request);

    @DELETE("/api/inventory/items/{id}")
    Call<GenericResponse<Void>> deleteItem(@Path("id") Long id);

    @GET("/api/inventory/categories")
    Call<GenericResponse<List<CategoryDTO>>> getCategories();

    @GET("/api/inventory/movements")
    Call<GenericResponse<List<StockMovementDTO>>> getMovements();

    @GET("/api/inventory/movements/item/{itemId}")
    Call<GenericResponse<List<StockMovementDTO>>> getItemMovements(@Path("itemId") Long itemId);

    @POST("/api/inventory/movements")
    Call<GenericResponse<StockMovementDTO>> createMovement(@Body StockMovementRequest request);

    @POST("/api/inventory/movements/clear")
    Call<GenericResponse<String>> clearMovements();
}
