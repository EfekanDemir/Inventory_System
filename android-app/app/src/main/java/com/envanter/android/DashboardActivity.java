package com.envanter.android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.envanter.mobile.model.ItemStock;
import com.envanter.mobile.view.StockLevelBarChartView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private StockLevelBarChartView stockBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        stockBarChart = findViewById(R.id.stockBarChart);

        // Demo (Mock) Veri Ekliyoruz
        List<ItemStock> mockItems = new ArrayList<>();
        mockItems.add(new ItemStock("Laptop", 50, 20));  // Yeşil
        mockItems.add(new ItemStock("Mouse", 25, 20));   // Turuncu
        mockItems.add(new ItemStock("Klavye", 5, 10));   // Kırmızı (Kritik)
        mockItems.add(new ItemStock("Monitör", 12, 10)); // Turuncu
        mockItems.add(new ItemStock("Kablo", 100, 50));  // Yeşil

        stockBarChart.setItems(mockItems);

        // --- Pie Chart (Kategori Dagilimi) Verisi ---
        categoryPieChart = findViewById(R.id.categoryPieChart);

        float[] percentages = {40.5f, 25.0f, 15.0f, 10.0f, 7.5f, 2.0f};
        String[] categories = {"Elektronik", "Sarf Malzemesi", "Mobilya", "Yazılım", "Temizlik", "Diğer"};
        
        categoryPieChart.setData(percentages, categories);
    }
}
