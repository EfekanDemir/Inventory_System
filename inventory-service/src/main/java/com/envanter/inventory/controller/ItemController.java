package com.envanter.inventory.controller;

import com.envanter.common.generic.GenericResponseWrapper;
import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.dto.StockReportDTO;
import com.envanter.inventory.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Envanter kalemi (Item) yönetim controller'ı.
 *
 * <p>DIP: ItemService arayüzüne bağımlı — implementasyon detayını bilmez.
 * Constructor Injection zorunlu — @Autowired field injection yasak.</p>
 *
 * Endpointler:
 * - GET    /api/inventory/items                    → tüm itemlar (opsiyonel ?search=, ?categoryId=)
 * - GET    /api/inventory/items/{id}               → tek item
 * - GET    /api/inventory/items/low-stock          → düşük stok uyarısı
 * - GET    /api/inventory/items/report             → stok özet raporu
 * - POST   /api/inventory/items                    → yeni item
 * - PUT    /api/inventory/items/{id}               → güncelle
 * - DELETE /api/inventory/items/{id}               → soft-delete (DISCONTINUED)
 * - GET    /api/inventory/health                   → sağlık kontrolü
 */
@RestController
@RequestMapping("/api/inventory")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    /** DIP: Somut impl değil, arayüz inject edildi. */
    private final ItemService itemService;

    /** Constructor Injection — Spring tek constructor varsa @Autowired gerekmez. */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // -------------------------------------------------------------------------
    // Endpoints
    // -------------------------------------------------------------------------

    /**
     * Tüm itemları listeler.
     * Opsiyonel filtreler: ?search=anahtar  veya  ?categoryId=1
     */
    @GetMapping("/items")
    public ResponseEntity<GenericResponseWrapper<List<ItemDTO>>> getAllItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {

        List<ItemDTO> items;
        if (search != null && !search.isBlank()) {
            items = itemService.searchItems(search);
        } else if (categoryId != null) {
            items = itemService.getItemsByCategory(categoryId);
        } else {
            items = itemService.getAllItems();
        }
        return ResponseEntity.ok(GenericResponseWrapper.success(items));
    }

    @GetMapping("/items/{id:\\d+}")
    public ResponseEntity<GenericResponseWrapper<ItemDTO>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(GenericResponseWrapper.success(itemService.getItemById(id)));
    }

    /**
     * quantity <= minStockLevel olan itemları listeler.
     * GET /api/inventory/items/low-stock
     */
    @GetMapping("/items/low-stock")
    public ResponseEntity<GenericResponseWrapper<List<ItemDTO>>> getLowStockItems() {
        List<ItemDTO> lowStock = itemService.getLowStockItems();
        return ResponseEntity.ok(GenericResponseWrapper.success(lowStock,
                lowStock.size() + " ürün minimum stok seviyesinin altında."));
    }

    /**
     * Stok özet raporu (toplam değer, düşük stok sayısı vb.)
     * GET /api/inventory/items/report
     */
    @GetMapping("/items/report")
    public ResponseEntity<GenericResponseWrapper<StockReportDTO>> getStockReport() {
        return ResponseEntity.ok(GenericResponseWrapper.success(itemService.getStockReport()));
    }

    @PostMapping("/items")
    public ResponseEntity<GenericResponseWrapper<ItemDTO>> createItem(@RequestBody ItemRequest request) {
        log.info("Yeni item isteği: code={}", request.getItemCode());
        ItemDTO created = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponseWrapper.success(created));
    }

    @PutMapping("/items/{id:\\d+}")
    public ResponseEntity<GenericResponseWrapper<ItemDTO>> updateItem(@PathVariable Long id,
                                              @RequestBody ItemRequest request) {
        log.info("Item güncelleme isteği: id={}", id);
        return ResponseEntity.ok(GenericResponseWrapper.success(itemService.updateItem(id, request)));
    }

    /**
     * Soft-delete: item'ı silmez, status=DISCONTINUED yapar.
     */
    @DeleteMapping("/items/{id:\\d+}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.info("Item soft-delete isteği: id={}", id);
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("inventory-service UP");
    }
}
