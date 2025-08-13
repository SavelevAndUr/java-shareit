package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void createItemRequest_shouldCreate() {
        long userId = 1L;
        ItemRequestDto in = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
        ItemRequestDto out = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.createItemRequest(userId, in)).thenReturn(out);

        ResponseEntity<ItemRequestDto> response = itemRequestController.createItemRequest(userId, in);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(out, response.getBody());
        verify(itemRequestService).createItemRequest(userId, in);
    }

    @Test
    void getAllRequestsByUser_shouldReturnList() {
        long userId = 1L;
        ItemRequestDto r = ItemRequestDto.builder().id(1L).build();

        when(itemRequestService.getAllRequestsByUser(userId)).thenReturn(List.of(r));

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequestsByUser(userId);

        assertEquals(1, response.getBody().size());
        verify(itemRequestService).getAllRequestsByUser(userId);
    }

    @Test
    void getAllRequestsByUser_whenNoRequests_shouldReturnEmptyList() {
        long userId = 1L;
        when(itemRequestService.getAllRequestsByUser(userId)).thenReturn(List.of());

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequestsByUser(userId);

        assertTrue(response.getBody().isEmpty());
        verify(itemRequestService).getAllRequestsByUser(userId);
    }

    @Test
    void getAllRequests_shouldReturnPaged() {
        long userId = 1L;

        when(itemRequestService.getAllRequests(userId, 0, 10)).thenReturn(List.of());

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequests(userId, 0, 10);

        assertNotNull(response.getBody());
        verify(itemRequestService).getAllRequests(userId, 0, 10);
    }

    @Test
    void getRequestById_shouldReturnOne() {
        long userId = 1L;
        long requestId = 2L;
        ItemRequestDto r = ItemRequestDto.builder().id(requestId).build();

        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(r);

        ResponseEntity<ItemRequestDto> response = itemRequestController.getRequestById(userId, requestId);

        assertEquals(r, response.getBody());
        verify(itemRequestService).getRequestById(userId, requestId);
    }

    @Test
    void getRequestById_whenNotFound_shouldThrow() {
        long userId = 1L;
        long requestId = 999L;

        when(itemRequestService.getRequestById(userId, requestId))
                .thenThrow(new NotFoundException("Request not found"));

        assertThrows(NotFoundException.class,
                () -> itemRequestController.getRequestById(userId, requestId));
        verify(itemRequestService).getRequestById(userId, requestId);
    }
}
