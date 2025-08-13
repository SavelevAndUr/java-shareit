package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(requestService.createItemRequest(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUser(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getAllRequestsByUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(requestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }
}