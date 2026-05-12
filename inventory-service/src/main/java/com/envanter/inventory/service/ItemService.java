package com.envanter.inventory.service;

import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;

import java.util.List;

/**
 * ItemService arayuzu.
 */
public interface ItemService {

    ItemDTO createItem(ItemRequest request);

    ItemDTO getItemById(Long id);

    List<ItemDTO> getAllItems();

    ItemDTO updateItem(Long id, ItemRequest request);

    void deleteItem(Long id);
}
