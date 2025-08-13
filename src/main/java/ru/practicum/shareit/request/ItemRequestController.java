package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("создание запроса создания вещи");
        return itemRequestService.saveItemRequest(userId, itemRequestDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto updateItemRequest(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                                            @Positive(message = "неверное значение") @PathVariable("id") long id,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("обновление запроса создания вещи");
        return itemRequestService.updateItemRequest(userId, id, itemRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findIdItemRequestById(@Positive(message = "неверное значение") @PathVariable("id") long id) {
        log.info("вывод запроса создания вещи id " + id);
        return itemRequestService.findIdItemRequestById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findItemRequestByDescription(@RequestParam String item) {
        log.info("вывод запроса по названию вещи");
        return itemRequestService.findItemRequestByDescription(item);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAll() {
        log.info("вывод всех запросов создания вещи");
        return itemRequestService.findAll();
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    void delete(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                @Positive(message = "неверное значение") @PathVariable("itemId") long id) {
        log.info("удаление запроса создания вещи");
        itemRequestService.delete(userId, id);
    }
}
