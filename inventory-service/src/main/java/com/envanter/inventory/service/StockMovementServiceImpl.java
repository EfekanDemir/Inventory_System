package com.envanter.inventory.service;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.repository.ItemRepository;
import com.envanter.inventory.repository.StockMovementRepository;

import java.util.List;

/**
 * StockMovementService implementasyonu.
 *
 * TDD RED ASAMA -- tum metodlar UnsupportedOperationException firlatir.
 * Implementasyon bir sonraki commit'te (GREEN) eklenecektir.
 *
 * SOLID: Constructor Injection -- Strategy pattern ile stok dogrulama.
 */
public class StockMovementServiceImpl implements StockMovementService {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationClient notificationClient;

    public StockMovementServiceImpl(ItemRepository itemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    NotificationClient notificationClient) {
        this.itemRepository = itemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.notificationClient = notificationClient;
    }

    @Override
    public StockMovementDTO createMovement(StockMovementRequest request) {
        throw new UnsupportedOperationException("createMovement() RED asama -- henuz implemente edilmedi");
    }

    @Override
    public List<StockMovementDTO> getMovementsByItem(Long itemId) {
        throw new UnsupportedOperationException("getMovementsByItem() RED asama");
    }
}
