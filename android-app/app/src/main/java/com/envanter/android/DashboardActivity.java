package com.envanter.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.CategoryDTO;
import com.envanter.android.model.ItemDTO;
import com.envanter.android.model.StockReportDTO;
import com.envanter.android.util.ApiErrorHandler;
import com.envanter.mobile.model.ItemStock;
import com.envanter.mobile.view.StockLevelBarChartView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private StockLevelBarChartView stockBarChart;
    private TextView tvTotalItems, tvLowStockCount, tvCategoryCount;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnEnvanterList = findViewById(R.id.btnEnvanterList);
        Button btnMovements = findViewById(R.id.btnMovements);
        Button btnLogout = findViewById(R.id.btnLogout);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvCategoryCount = findViewById(R.id.tvCategoryCount);
        stockBarChart = findViewById(R.id.stockBarChart);

        if (btnEnvanterList != null)
            btnEnvanterList.setOnClickListener(v ->
                startActivity(new Intent(this, EnvanterListActivity.class)));
        if (btnMovements != null)
            btnMovements.setOnClickListener(v ->
                startActivity(new Intent(this, StockMovementsActivity.class)));
        if (btnLogout != null)
            btnLogout.setOnClickListener(v -> ApiErrorHandler.logout(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);

        loadStockReport();
        loadCategories();
        loadItemsForChart();
    }

    private void loadStockReport() {
        apiService.getStockReport().enqueue(new Callback<GenericResponse<StockReportDTO>>() {
            @Override
            public void onResponse(Call<GenericResponse<StockReportDTO>> call, Response<GenericResponse<StockReportDTO>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    StockReportDTO report = response.body().getData();
                    runOnUiThread(() -> {
                        if (tvTotalItems != null) tvTotalItems.setText(String.valueOf(report.getActiveItems()));
                        if (tvLowStockCount != null) tvLowStockCount.setText(String.valueOf(report.getLowStockItems()));
                    });
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<StockReportDTO>> call, Throwable t) {}
        });
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<GenericResponse<List<CategoryDTO>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<CategoryDTO>>> call, Response<GenericResponse<List<CategoryDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int count = response.body().getData().size();
                    runOnUiThread(() -> {
                        if (tvCategoryCount != null) tvCategoryCount.setText(String.valueOf(count));
                    });
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<List<CategoryDTO>>> call, Throwable t) {}
        });
    }

    private void loadItemsForChart() {
        apiService.getItems(null, null).enqueue(new Callback<GenericResponse<List<ItemDTO>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<ItemDTO>>> call, Response<GenericResponse<List<ItemDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ItemDTO> items = response.body().getData();
                    List<ItemStock> chartItems = new ArrayList<>();
                    for (ItemDTO item : items) {
                        chartItems.add(new ItemStock(item.getName(), item.getQuantity(), item.getMinStockLevel()));
                    }
                    runOnUiThread(() -> {
                        if (stockBarChart != null) stockBarChart.setItems(chartItems);
                    });
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<List<ItemDTO>>> call, Throwable t) {}
        });
    }
}
