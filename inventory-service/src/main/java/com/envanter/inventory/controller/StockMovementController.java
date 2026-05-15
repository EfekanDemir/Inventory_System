package com.envanter.inventory.controller;

import com.envanter.common.generic.GenericResponseWrapper;
import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;

import com.envanter.inventory.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import java.util.List;

/**
 * Stok hareketi (StockMovement) controller'ı.
 *
 * <p>DIP: StockMovementService arayüzüne bağımlı.
 * Constructor Injection zorunlu — @Autowired field injection yasak.</p>
 *
 * Endpointler:
 * - POST /api/inventory/movements                   → yeni stok hareketi
 * - GET  /api/inventory/movements                   → tüm hareketler (?type=IN|OUT, ?from=, ?to=)
 * - GET  /api/inventory/movements/item/{itemId}     → item'a göre hareketler
 */
@RestController
@RequestMapping("/api/inventory")
public class StockMovementController {

    private static final Logger log = LoggerFactory.getLogger(StockMovementController.class);

    /** DIP: Somut impl değil, arayüz inject edildi. */
    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    // -------------------------------------------------------------------------
    // Endpoints
    // -------------------------------------------------------------------------

    @PostMapping("/movements")
    public ResponseEntity<GenericResponseWrapper<StockMovementDTO>> createMovement(
            @RequestBody StockMovementRequest request) {
        log.info("Stok hareketi isteği: itemId={}, type={}, qty={}",
                request.getItemId(), request.getMovementType(), request.getQuantity());
        StockMovementDTO created = stockMovementService.createMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponseWrapper.success(created));
    }

    /**
     * Tüm hareketleri listeler.
     * Opsiyonel filtreler:
     *   ?type=IN  veya  ?type=OUT
     *   ?from=2025-01-01T00:00:00  &  ?to=2025-12-31T23:59:59
     */
    @GetMapping("/movements")
    public ResponseEntity<GenericResponseWrapper<List<StockMovementDTO>>> getMovements() {
        log.info("Tüm stok hareketleri listeleniyor...");
        List<StockMovementDTO> movements = stockMovementService.getAllMovements();
        return ResponseEntity.ok(GenericResponseWrapper.success(movements));
    }

    @GetMapping("/movements/item/{itemId}")
    public ResponseEntity<GenericResponseWrapper<List<StockMovementDTO>>> getMovementsByItem(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(GenericResponseWrapper.success(
                stockMovementService.getMovementsByItem(itemId)));
    }

    @PostMapping("/movements/clear")
    public ResponseEntity<GenericResponseWrapper<String>> clearAllHistory() {
        log.warn("Kullanici tarafindan tüm stok geçmişi temizleme isteği alindi.");
        stockMovementService.clearAllHistory();
        return ResponseEntity.ok(GenericResponseWrapper.success("Tüm stok hareket geçmişi ve loglar başarıyla temizlendi."));
    }
}
