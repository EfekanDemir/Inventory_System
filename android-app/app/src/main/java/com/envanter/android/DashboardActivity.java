package com.envanter.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.envanter.android.util.ApiErrorHandler;
import com.envanter.mobile.model.ItemStock;
import com.envanter.mobile.view.CategoryPieChartView;
import com.envanter.mobile.view.StockLevelBarChartView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private StockLevelBarChartView stockBarChart;
    private CategoryPieChartView categoryPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // --- Navigation (Yonlendirme Butonlari) ---
        Button btnEnvanterList = findViewById(R.id.btnEnvanterList);
        Button btnLogout = findViewById(R.id.btnLogout);

        if (btnEnvanterList != null) {
            btnEnvanterList.setOnClickListener(v -> {
                startActivity(new Intent(DashboardActivity.this, EnvanterListActivity.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> ApiErrorHandler.logout(this));
        }

        // --- Bar Chart ---
        stockBarChart = findViewById(R.id.stockBarChart);
        if (stockBarChart != null) {
            List<ItemStock> mockItems = new ArrayList<>();
            mockItems.add(new ItemStock("Laptop", 50, 20));
            mockItems.add(new ItemStock("Mouse", 25, 20));
            mockItems.add(new ItemStock("Klavye", 5, 10));
            mockItems.add(new ItemStock("Monitör", 12, 10));
            mockItems.add(new ItemStock("Kablo", 100, 50));
            stockBarChart.setItems(mockItems);
        }

        // --- Pie Chart ---
        categoryPieChart = findViewById(R.id.categoryPieChart);
        if (categoryPieChart != null) {
            float[] percentages = {40.5f, 25.0f, 15.0f, 10.0f, 7.5f, 2.0f};
            String[] categories = {"Elektronik", "Sarf Malzemesi", "Mobilya", "Yazılım", "Temizlik", "Diğer"};
            categoryPieChart.setData(percentages, categories);
        }
    }
}
