package com.envanter.android.api;

import com.envanter.android.model.LoginRequest;
import com.envanter.android.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
