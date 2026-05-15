package com.envanter.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.envanter.android.adapter.MovementAdapter;
import com.envanter.android.api.ApiClient;
import com.envanter.android.api.ApiService;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.StockMovementDTO;
import com.envanter.android.util.ApiErrorHandler;
import com.envanter.android.repository.MovementRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockMovementsActivity extends AppCompatActivity {

    private RecyclerView rvMovements;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private MovementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_movements);

        rvMovements = findViewById(R.id.rvMovements);
        swipeRefresh = findViewById(R.id.swipeRefreshMovements);
        progressBar = findViewById(R.id.pbMovements);

        rvMovements.setLayoutManager(new LinearLayoutManager(this));
        rvMovements.setHasFixedSize(true);
        rvMovements.setItemViewCacheSize(20);
        adapter = new MovementAdapter();
        rvMovements.setAdapter(adapter);

        MovementRepository repository = new MovementRepository(this);

        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        if (btnClearHistory != null) {
            btnClearHistory.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                    .setTitle("Geçmişi Temizle")
                    .setMessage("Tüm stok hareket geçmişi silinecek. Emin misiniz?")
                    .setPositiveButton("Temizle", (d, w) -> clearHistory(repository))
                    .setNegativeButton("İptal", null)
                    .show()
            );
        }

        swipeRefresh.setOnRefreshListener(() -> fetchMovements(repository));

        fetchMovements(repository);
    }

    private void clearHistory(MovementRepository repository) {
        repository.clearMovements(new retrofit2.Callback<GenericResponse<String>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<GenericResponse<String>> call,
                                   @NonNull retrofit2.Response<GenericResponse<String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StockMovementsActivity.this, "Geçmiş temizlendi", Toast.LENGTH_SHORT).show();
                    fetchMovements(repository);
                } else {
                    ApiErrorHandler.handleError(StockMovementsActivity.this, response.code());
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<GenericResponse<String>> call, @NonNull Throwable t) {
                Toast.makeText(StockMovementsActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovements(MovementRepository repository) {
        progressBar.setVisibility(View.VISIBLE);
        repository.getMovements(new retrofit2.Callback<com.envanter.android.api.GenericResponse<java.util.List<com.envanter.android.model.StockMovementDTO>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse<List<StockMovementDTO>>> call, @NonNull Response<GenericResponse<List<StockMovementDTO>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<StockMovementDTO> data = response.body().getData();
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(StockMovementsActivity.this, "Henüz stok hareketi bulunmuyor.", Toast.LENGTH_SHORT).show();
                    }
                    adapter.setMovements(data);
                } else {
                    // Beklenen 2 argument saglandi
                    ApiErrorHandler.handleError(StockMovementsActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse<List<StockMovementDTO>>> call, @NonNull Throwable t) {
                Log.e("STOCK_MOVE_ERROR", "Bağlantı hatası: ", t);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                // handleFailure bulunamadigi icin yerel uyari verildi
                Toast.makeText(StockMovementsActivity.this, "Bağlantı hatası: Sunucuya ulaşılamıyor.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
