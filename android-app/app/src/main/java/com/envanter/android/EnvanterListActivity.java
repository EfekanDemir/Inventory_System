package com.envanter.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.envanter.android.adapter.EnvanterAdapter;
import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.ItemDTO;
import com.envanter.android.util.ApiErrorHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnvanterListActivity extends AppCompatActivity {

    private RecyclerView rvEnvanterList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private EnvanterAdapter adapter;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envanter_list);

        rvEnvanterList = findViewById(R.id.rvEnvanterList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);

        rvEnvanterList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EnvanterAdapter();
        rvEnvanterList.setAdapter(adapter);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("envanter_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchInventoryItems(false); // asagidan cekiste progress bar degil swipe'in kendi bar'i donsun
        });

        // Ilk acilista yukle
        fetchInventoryItems(true);
    }

    private void fetchInventoryItems(boolean showProgress) {
        if (showProgress) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Call<GenericResponse<List<ItemDTO>>> call = apiService.getItems("Bearer " + token);
        call.enqueue(new Callback<GenericResponse<List<ItemDTO>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<ItemDTO>>> call, Response<GenericResponse<List<ItemDTO>>> response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                });

                if (response.isSuccessful() && response.body() != null) {
                    List<ItemDTO> items = response.body().getData();
                    runOnUiThread(() -> adapter.setItems(items));
                } else {
                    runOnUiThread(() -> ApiErrorHandler.handleError(EnvanterListActivity.this, response.code()));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<ItemDTO>>> call, Throwable t) {
                Log.e("API_ERROR", "Fetch failed", t);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(EnvanterListActivity.this, "Ağ hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
