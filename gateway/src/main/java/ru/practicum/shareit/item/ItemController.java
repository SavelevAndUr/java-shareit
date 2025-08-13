package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}