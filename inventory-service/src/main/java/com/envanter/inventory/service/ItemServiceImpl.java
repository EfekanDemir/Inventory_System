package com.envanter.inventory.service;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.repository.ItemRepository;
import com.envanter.inventory.repository.StockMovementRepository;

import java.util.List;

/**
 * ItemService implementasyonu.
 *
 * TDD RED ASAMA -- tum metodlar UnsupportedOperationException firlatir.
 * Implementasyon bir sonraki commit'te (GREEN) eklenecektir.
 *
 * SOLID: Constructor Injection -- @Autowired field injection yasak.
 */
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationClient notificationClient;

    public ItemServiceImpl(ItemRepository itemRepository,
                           StockMovementRepository stockMovementRepository,
                           NotificationClient notificationClient) {
        this.itemRepository = itemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.notificationClient = notificationClient;
    }

    @Override
    public ItemDTO createItem(ItemRequest request) {
        throw new UnsupportedOperationException("createItem() RED asama -- henuz implemente edilmedi");
    }

    @Override
    public ItemDTO getItemById(Long id) {
        throw new UnsupportedOperationException("getItemById() RED asama");
    }

    @Override
    public List<ItemDTO> getAllItems() {
        throw new UnsupportedOperationException("getAllItems() RED asama");
    }

    @Override
    public ItemDTO updateItem(Long id, ItemRequest request) {
        throw new UnsupportedOperationException("updateItem() RED asama");
    }

    @Override
    public void deleteItem(Long id) {
        throw new UnsupportedOperationException("deleteItem() RED asama");
    }
}
