package com.envanter.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.envanter.android.adapter.EnvanterAdapter;
import com.envanter.android.api.GenericResponse;
import com.envanter.android.model.ItemDTO;
import com.envanter.android.model.ItemRequest;
import com.envanter.android.repository.InventoryRepository;
import com.envanter.android.util.ApiErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnvanterListActivity extends AppCompatActivity {

    private RecyclerView rvEnvanterList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddItem;
    private EnvanterAdapter adapter;
    private InventoryRepository repository;
    private TextInputEditText etBarcodeField; 

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() != null && etBarcodeField != null) {
                    etBarcodeField.setText(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envanter_list);

        rvEnvanterList = findViewById(R.id.rvEnvanterList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        fabAddItem = findViewById(R.id.fabAddItem);

        rvEnvanterList.setLayoutManager(new LinearLayoutManager(this));
        rvEnvanterList.setHasFixedSize(true);
        rvEnvanterList.setItemViewCacheSize(20);
        adapter = new EnvanterAdapter();
        rvEnvanterList.setAdapter(adapter);

        repository = new InventoryRepository(this);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchInventoryItems(false));

        fabAddItem.setOnClickListener(v -> showItemDialog(null));

        adapter.setOnItemClickListener(new EnvanterAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(ItemDTO item) {
                showItemDialog(item);
            }

            @Override
            public void onDeleteClick(ItemDTO item) {
                showDeleteConfirmation(item);
            }

            @Override
            public void onMovementClick(ItemDTO item) {
                showMovementDialog(item);
            }
        });

        fetchInventoryItems(true);
    }

    private void showItemDialog(ItemDTO itemToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_item_form, null);
        builder.setView(dialogView);

        TextInputEditText etItemCode = dialogView.findViewById(R.id.etItemCode);
        TextInputEditText etItemName = dialogView.findViewById(R.id.etItemName);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        TextInputEditText etMinStock = dialogView.findViewById(R.id.etMinStock);
        TextInputEditText etBarcode = dialogView.findViewById(R.id.etBarcode);
        TextInputEditText etUnitPrice = dialogView.findViewById(R.id.etUnitPrice);
        ImageButton btnScan = dialogView.findViewById(R.id.btnScanBarcode);

        etBarcodeField = etBarcode; 

        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
            options.setPrompt("Barkodu hizalayın");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            barcodeLauncher.launch(options);
        });

        boolean isEdit = itemToEdit != null;
        builder.setTitle(isEdit ? "Ürünü Düzenle" : "Yeni Ürün Ekle");

        if (isEdit) {
            etItemCode.setText(itemToEdit.getItemCode());
            etItemName.setText(itemToEdit.getName());
            etQuantity.setText(String.valueOf(itemToEdit.getQuantity()));
            etMinStock.setText(String.valueOf(itemToEdit.getMinStockLevel()));
            etBarcode.setText(itemToEdit.getBarcode());
            if (itemToEdit.getUnitPrice() != null) {
                etUnitPrice.setText(itemToEdit.getUnitPrice().toString());
            }
            etItemCode.setEnabled(false); 
        }

        builder.setPositiveButton(isEdit ? "Güncelle" : "Kaydet", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String code = etItemCode.getText().toString().trim();
            String name = etItemName.getText().toString().trim();
            String barcodeStr = etBarcode.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            String minStr = etMinStock.getText().toString().trim();
            String priceStr = etUnitPrice.getText().toString().trim();

            if (code.isEmpty() || name.isEmpty() || qtyStr.isEmpty() || minStr.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int qty = Integer.parseInt(qtyStr);
                int min = Integer.parseInt(minStr);

                ItemRequest request = new ItemRequest(code, name, qty, min, 1L);
                request.setBarcode(barcodeStr);
                if (!priceStr.isEmpty()) {
                    request.setUnitPrice(new java.math.BigDecimal(priceStr));
                }

                if (isEdit) {
                    updateItem(itemToEdit.getId(), request);
                } else {
                    createItem(request);
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçersiz sayı formatı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createItem(ItemRequest request) {
        repository.createItem(request, new Callback<GenericResponse<ItemDTO>>() {
            @Override
            public void onResponse(Call<GenericResponse<ItemDTO>> call, Response<GenericResponse<ItemDTO>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EnvanterListActivity.this, "Ürün eklendi", Toast.LENGTH_SHORT).show();
                    fetchInventoryItems(true);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<ItemDTO>> call, Throwable t) {
                Toast.makeText(EnvanterListActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItem(Long id, ItemRequest request) {
        repository.updateItem(id, request, new Callback<GenericResponse<ItemDTO>>() {
            @Override
            public void onResponse(Call<GenericResponse<ItemDTO>> call, Response<GenericResponse<ItemDTO>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EnvanterListActivity.this, "Ürün güncellendi", Toast.LENGTH_SHORT).show();
                    fetchInventoryItems(true);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<ItemDTO>> call, Throwable t) {
                Toast.makeText(EnvanterListActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(ItemDTO item) {
        new AlertDialog.Builder(this)
                .setTitle("Ürünü Sil")
                .setMessage(item.getName() + " silinecek. Emin misiniz?")
                .setPositiveButton("Sil", (dialog, which) -> deleteItem(item.getId()))
                .setNegativeButton("İptal", null)
                .show();
    }

    private void deleteItem(Long id) {
        repository.deleteItem(id, new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EnvanterListActivity.this, "Ürün silindi", Toast.LENGTH_SHORT).show();
                    fetchInventoryItems(true);
                } else {
                    Log.e("DeleteItem", "Silme başarısız, HTTP: " + response.code());
                    ApiErrorHandler.handleError(EnvanterListActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                Log.e("DeleteItem", "Ağ hatası: " + t.getMessage(), t);
                Toast.makeText(EnvanterListActivity.this, "Ağ hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchInventoryItems(boolean showProgress) {
        if (showProgress) {
            progressBar.setVisibility(View.VISIBLE);
        }

        repository.getItems(new Callback<GenericResponse<List<ItemDTO>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<ItemDTO>>> call, Response<GenericResponse<List<ItemDTO>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body().getData());
                } else {
                    ApiErrorHandler.handleError(EnvanterListActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<ItemDTO>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(EnvanterListActivity.this, "Ağ hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMovementDialog(ItemDTO item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_stock_movement, null);
        builder.setView(dialogView);

        android.widget.RadioGroup rgMovementType = dialogView.findViewById(R.id.rgMovementType);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        TextInputEditText etAssignedTo = dialogView.findViewById(R.id.etAssignedTo);
        TextInputEditText etReturnDate = dialogView.findViewById(R.id.etReturnDate);
        TextInputEditText etReason = dialogView.findViewById(R.id.etReason);

        // Takvim (DatePicker) islemi
        etReturnDate.setFocusable(false);
        etReturnDate.setClickable(true);
        etReturnDate.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(this, (view, y, m, d) -> {
                // Ay 0-indexli oldugu icin +1 ekliyoruz
                String selectedDate = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                etReturnDate.setText(selectedDate);
            }, year, month, day);
            datePicker.show();
        });

        builder.setPositiveButton("Kaydet", null);
        builder.setNegativeButton("İptal", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String qtyStr = etQuantity.getText().toString().trim();
            String reason = etReason.getText().toString().trim();
            String assignedTo = etAssignedTo.getText().toString().trim();
            String returnDateStr = etReturnDate.getText().toString().trim();
            if (!returnDateStr.isEmpty() && !returnDateStr.contains("T")) {
                // Backend LocalDateTime beklediği için 'T00:00:00' ekliyoruz
                returnDateStr += "T00:00:00";
            }

            if (qtyStr.isEmpty()) {
                Toast.makeText(this, "Miktar girilmesi zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgMovementType.getCheckedRadioButtonId();
            String movementType = checkedId == R.id.rbTypeIn ? "IN" : "OUT";

            try {
                int quantity = Integer.parseInt(qtyStr);

                com.envanter.android.model.StockMovementRequest request = new com.envanter.android.model.StockMovementRequest(
                        item.getId(),
                        movementType,
                        quantity,
                        reason.isEmpty() ? null : reason
                );

                if (!assignedTo.isEmpty()) {
                    request.setAssignedTo(assignedTo);
                }
                
                if (!returnDateStr.isEmpty()) {
                    request.setReturnDate(returnDateStr);
                }

                com.envanter.android.repository.MovementRepository movementRepository = new com.envanter.android.repository.MovementRepository(this);
                movementRepository.createMovement(request, new Callback<GenericResponse<com.envanter.android.model.StockMovementDTO>>() {
                    @Override
                    public void onResponse(Call<GenericResponse<com.envanter.android.model.StockMovementDTO>> call, Response<GenericResponse<com.envanter.android.model.StockMovementDTO>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EnvanterListActivity.this, "Stok hareketi başarıyla eklendi", Toast.LENGTH_SHORT).show();
                            fetchInventoryItems(true); // Stok değişmiş olabilir, listeyi yenile
                            dialog.dismiss();
                        } else {
                            ApiErrorHandler.handleError(EnvanterListActivity.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse<com.envanter.android.model.StockMovementDTO>> call, Throwable t) {
                        Toast.makeText(EnvanterListActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçersiz miktar formatı", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
