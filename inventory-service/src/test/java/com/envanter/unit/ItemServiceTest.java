package com.envanter.unit;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.exception.ValidationException;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
import com.envanter.inventory.repository.ItemRepository;
import com.envanter.inventory.repository.StockMovementRepository;
import com.envanter.inventory.service.ItemServiceImpl;
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
 * ItemServiceImpl henuz implemente edilmedigi icin tum testler BASARISIZ olacak.
 * createItem() metodu UnsupportedOperationException firlatir.
 *
 * Commit 5: InventoryService item ekleme testleri (RED)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService - Envanter Kalem Testleri")
class ItemServiceTest {

    // -- Mock'lar --------------------------------------------------------------

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private NotificationClient notificationClient;

    // -- Test altindaki sinif -------------------------------------------------

    @InjectMocks
    private ItemServiceImpl itemService;

    // =========================================================================
    // BASARILI SENARYOLAR
    // =========================================================================

    /**
     * Senaryo 1 -- Gecerli istek ile item olusturma
     * RED: UnsupportedOperationException nedeniyle BASARISIZ olacak.
     */
    @Test
    @DisplayName("Gecerli ItemRequest ile createItem -- ItemDTO doner")
    void addItem_WithValidRequest_ReturnsItemDTO() {
        // -- Given --
        ItemRequest request = new ItemRequest(
                "ITM-001",
                "Laptop HP ProBook",
                1L,
                50,
                10
        );

        // itemCode sistemde yok
        when(itemRepository.existsByItemCode("ITM-001")).thenReturn(false);

        // Save sonucu
        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setItemCode("ITM-001");
        savedItem.setName("Laptop HP ProBook");
        savedItem.setCategoryId(1L);
        savedItem.setQuantity(50);
        savedItem.setMinStockLevel(10);
        savedItem.setStatus(ItemStatus.ACTIVE);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        // -- When --
        ItemDTO result = itemService.createItem(request);

        // -- Then --
        assertNotNull(result, "createItem sonucu null olmamali");
        assertEquals("ITM-001", result.getItemCode(), "ItemCode eslesmiyor");
        assertEquals("Laptop HP ProBook", result.getName());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(50);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(notificationClient, times(1)).sendInventoryAddedNotification(anyLong(), anyString());
    }

    // =========================================================================
    // HATA SENARYOLARI (RED)
    // =========================================================================

    /**
     * Senaryo 2 -- Mevcut itemCode ile tekrar ekleme -> ConflictException
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Mevcut itemCode ile createItem -- ConflictException firlatir")
    void addItem_WithDuplicateItemCode_ThrowsConflictException() {
        // -- Given --
        ItemRequest request = new ItemRequest(
                "ITM-001",
                "Baska Urun",
                1L,
                20,
                5
        );

        // itemCode zaten sistemde var
        when(itemRepository.existsByItemCode("ITM-001")).thenReturn(true);

        // -- When & Then --
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> itemService.createItem(request),
                "Mevcut itemCode icin ConflictException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();

        // Kayit yapilmamali
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * Senaryo 3 -- Null itemCode ile ekleme -> ValidationException
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Null itemCode ile createItem -- ValidationException firlatir")
    void addItem_WithNullItemCode_ThrowsValidationException() {
        // -- Given -- (itemCode null)
        ItemRequest request = new ItemRequest(
                null,
                "Klavye",
                1L,
                100,
                15
        );

        // -- When & Then --
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(request),
                "Null itemCode icin ValidationException firlatilmali"
        );

        assertThat(exception.getErrors()).isNotEmpty();

        verify(itemRepository, never()).existsByItemCode(any());
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * Senaryo 4 -- Negatif miktar ile ekleme -> ValidationException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Negatif quantity ile createItem -- ValidationException firlatir")
    void addItem_WithNegativeQuantity_ThrowsValidationException() {
        // -- Given --
        ItemRequest request = new ItemRequest("ITM-002", "Mouse", 1L, -5, 10);

        // -- When & Then --
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(request),
                "Negatif miktar icin ValidationException firlatilmali"
        );

        assertThat(exception.getErrors()).isNotEmpty();
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * Senaryo 5 -- Var olmayan categoryId -> ResourceNotFoundException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Gecersiz categoryId ile createItem -- ResourceNotFoundException firlatir")
    void addItem_WithInvalidCategoryId_ThrowsNotFoundException() {
        // -- Given --
        ItemRequest request = new ItemRequest("ITM-003", "Tablet", 999L, 5, 2);

        when(itemRepository.existsByItemCode("ITM-003")).thenReturn(false);
        // categoryId=999 veritabaninda yok (CategoryRepository mock'u burada eklenmez,
        // ItemServiceImpl icinde categoryId kontrolu yapacak)

        // -- When & Then --
        assertThrows(
                com.envanter.user.exception.ResourceNotFoundException.class,
                () -> itemService.createItem(request),
                "Gecersiz categoryId icin ResourceNotFoundException firlatilmali"
        );

        verify(itemRepository, never()).save(any(Item.class));
    }
}
