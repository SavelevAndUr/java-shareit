package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorIsNull;
import ru.practicum.shareit.exception.NotDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ValidateItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ValidateItemController validateItemController;

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        if (userRepository.findById(userId) == null) {
            throw new ErrorIsNull("нет такого пользователя");
        }
        validateItemController.validateItemDto(itemDto);
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(userId);
        item = itemRepository.save(item);
        log.info("пердана вещь для создания");
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(long userId, long id) {
        if (getItemByItemId(id) == null) {
            throw new NotDataException("нет вещи с таким id " + id);
        }
        if (getItemByItemId(id).getOwner() != userId) {
            throw new ValidationException("вы не являетесь владельцем вещи");
        }
        itemRepository.delete(id);
        log.info("удаление вещи, передан id " + id);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        if (getItemByItemId(itemId) == null) {
            throw new NotDataException("нет вещи с таким id " + itemId);
        }
        if (getItemByItemId(itemId).getOwner() != userId) {
            throw new ErrorIsNull("вы не являетесь владельцем вещи");
        }
        Item item = ItemMapper.mapToItem(itemDto);
        item.setId(itemId);
        log.info("обновление вещи, передан id " + itemId);
        itemRepository.update(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto getItemByItemId(long id) {
        log.info("передан запрос вещи по id " + id);
        return ItemMapper.mapToItemDto(itemRepository.getByItemId(id));
    }

    @Override
    public Collection<ItemDto> getAllItemByUserId(long id) {
        log.info("передан запрос списка вещей пользователя id " + id);
        return itemRepository.getAllByUserId(id).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findItemByName(String name) {

        log.info("передан запрос поиска вещей по названию " + name.toLowerCase());
        return itemRepository.findItemByName(name.toLowerCase()).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
