package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto saveItem(long userId, ItemDto itemDto);

    void deleteItem(long userId, long id);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemByItemId(long id);

    Collection<ItemDto> getAllItemByUserId(long id);

    Collection<ItemDto> findItemByName(String name);
}
