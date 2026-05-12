package com.envanter.unit;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.exception.ValidationException;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
import com.envanter.inventory.model.MovementType;
import com.envanter.inventory.repository.ItemRepository;
import com.envanter.inventory.repository.StockMovementRepository;
import com.envanter.inventory.service.StockMovementServiceImpl;
import com.envanter.user.exception.ConflictException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TDD -- RED Asama
 *
 * StockMovementServiceImpl henuz implemente edilmedigi icin tum testler BASARISIZ olacak.
 * createMovement() metodu UnsupportedOperationException firlatir.
 *
 * Commit 5: StockMovement stok hareketi testleri (RED)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StockMovementService - Stok Hareketi Testleri")
class StockMovementServiceTest {

    // -- Mock'lar --------------------------------------------------------------

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private NotificationClient notificationClient;

    // -- Test altindaki sinif -------------------------------------------------

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    // =========================================================================
    // STOK CIKISI (OUT) -- YETERLI STOK
    // =========================================================================

    /**
     * Senaryo 1 -- Yeterli stokta OUT hareketi basarili
     * Item.quantity=100, istek OUT quantity=30 -> stok 70'e duser
     * RED: UnsupportedOperationException nedeniyle BASARISIZ olacak.
     */
    @Test
    @DisplayName("Yeterli stokta OUT hareketi -- StockMovementDTO doner")
    void createMovement_StockOut_WithSufficientStock_ReturnsDTO() {
        // -- Given --
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setItemCode("ITM-001");
        existingItem.setName("Laptop HP ProBook");
        existingItem.setQuantity(100);
        existingItem.setMinStockLevel(10);
        existingItem.setStatus(ItemStatus.ACTIVE);

        StockMovementRequest request = new StockMovementRequest(1L, MovementType.OUT, 30);
        request.setReason("Personele tahsis");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        // Hareket kaydediliyor
        com.envanter.inventory.model.StockMovement savedMovement =
                new com.envanter.inventory.model.StockMovement();
        savedMovement.setId(1L);
        savedMovement.setItemId(1L);
        savedMovement.setMovementType(MovementType.OUT);
        savedMovement.setQuantity(30);
        when(stockMovementRepository.save(any())).thenReturn(savedMovement);

        // Item guncelleniyor
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        // -- When --
        StockMovementDTO result = stockMovementService.createMovement(request);

        // -- Then --
        assertNotNull(result, "createMovement sonucu null olmamali");
        assertThat(result.getId()).isNotNull();
        assertEquals(MovementType.OUT, result.getMovementType());
        assertEquals(30, result.getQuantity());

        // Stok guncellendi mi?
        verify(itemRepository, times(1)).save(any(Item.class));
        // Bildirim gonderilmedi (stok min. seviyenin ustunde)
        verify(notificationClient, never()).sendLowStockAlert(anyLong(), anyInt(), anyInt());
    }

    // =========================================================================
    // STOK CIKISI (OUT) -- YETERSIZ STOK
    // =========================================================================

    /**
     * Senaryo 2 -- Yetersiz stokta OUT hareketi -> ConflictException
     * Item.quantity=10, istek OUT quantity=50 -> HATA
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Yetersiz stokta OUT hareketi -- ConflictException firlatir")
    void createMovement_StockOut_WithInsufficientStock_ThrowsConflictException() {
        // -- Given --
        Item existingItem = new Item();
        existingItem.setId(2L);
        existingItem.setItemCode("ITM-002");
        existingItem.setQuantity(10);       // Mevcut stok: 10
        existingItem.setMinStockLevel(5);
        existingItem.setStatus(ItemStatus.ACTIVE);

        // Cikis istegi: 50 -- stoktan fazla!
        StockMovementRequest request = new StockMovementRequest(2L, MovementType.OUT, 50);

        when(itemRepository.findById(2L)).thenReturn(Optional.of(existingItem));

        // -- When & Then --
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> stockMovementService.createMovement(request),
                "Yetersiz stok icin ConflictException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();

        // Stok guncellenmemeli, hareket kaydedilmemeli
        verify(itemRepository, never()).save(any(Item.class));
        verify(stockMovementRepository, never()).save(any());
    }

    // =========================================================================
    // STOK GIRISI (IN) -- HER ZAMAN BASARILI
    // =========================================================================

    /**
     * Senaryo 3 -- IN hareketi her zaman basarili olmali
     * Stok miktarinden bagimsiz, IN hareketi hep kabul edilir.
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("IN hareketi -- her zaman StockMovementDTO doner")
    void createMovement_StockIn_AlwaysSucceeds() {
        // -- Given --
        Item existingItem = new Item();
        existingItem.setId(3L);
        existingItem.setItemCode("ITM-003");
        existingItem.setQuantity(5);         // Dusuk stok -- ama IN hareketi kabul edilmeli
        existingItem.setMinStockLevel(20);
        existingItem.setStatus(ItemStatus.ACTIVE);

        StockMovementRequest request = new StockMovementRequest(3L, MovementType.IN, 50);
        request.setReason("Yeni satin alma");

        when(itemRepository.findById(3L)).thenReturn(Optional.of(existingItem));

        com.envanter.inventory.model.StockMovement savedMovement =
                new com.envanter.inventory.model.StockMovement();
        savedMovement.setId(1L);
        savedMovement.setItemId(3L);
        savedMovement.setMovementType(MovementType.IN);
        savedMovement.setQuantity(50);
        when(stockMovementRepository.save(any())).thenReturn(savedMovement);
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        // -- When --
        StockMovementDTO result = stockMovementService.createMovement(request);

        // -- Then --
        assertNotNull(result, "IN hareketi sonucu null olmamali");
        assertEquals(MovementType.IN, result.getMovementType());
        assertEquals(50, result.getQuantity());

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(stockMovementRepository, times(1)).save(any());
    }

    // =========================================================================
    // EDGE CASE'LER
    // =========================================================================

    /**
     * Senaryo 4 -- Null itemId -> ValidationException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Null itemId ile createMovement -- ValidationException firlatir")
    void createMovement_WithNullItemId_ThrowsValidationException() {
        // -- Given --
        StockMovementRequest request = new StockMovementRequest(null, MovementType.OUT, 10);

        // -- When & Then --
        assertThrows(
                ValidationException.class,
                () -> stockMovementService.createMovement(request),
                "Null itemId icin ValidationException firlatilmali"
        );

        verify(itemRepository, never()).findById(any());
    }

    /**
     * Senaryo 5 -- Sifir miktar -> ValidationException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Sifir quantity ile createMovement -- ValidationException firlatir")
    void createMovement_WithZeroQuantity_ThrowsValidationException() {
        // -- Given --
        StockMovementRequest request = new StockMovementRequest(1L, MovementType.OUT, 0);

        // -- When & Then --
        assertThrows(
                ValidationException.class,
                () -> stockMovementService.createMovement(request),
                "Sifir miktar icin ValidationException firlatilmali"
        );

        verify(itemRepository, never()).findById(any());
    }

    /**
     * Senaryo 6 -- Null movementType -> ValidationException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Null movementType ile createMovement -- ValidationException firlatir")
    void createMovement_WithNullMovementType_ThrowsValidationException() {
        // -- Given --
        StockMovementRequest request = new StockMovementRequest(1L, null, 20);

        // -- When & Then --
        assertThrows(
                ValidationException.class,
                () -> stockMovementService.createMovement(request),
                "Null movementType icin ValidationException firlatilmali"
        );

        verify(itemRepository, never()).findById(any());
    }
}
