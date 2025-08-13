package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void addItem_shouldReturnCreatedItem() {
        long userId = 1L;
        ItemDto request = new ItemDto();
        request.setName("Дрель");
        request.setDescription("Описание");
        request.setAvailable(true);

        ItemDto expected = new ItemDto();
        expected.setId(1L);
        expected.setName("Дрель");

        when(itemService.addItem(userId, request)).thenReturn(expected);

        ResponseEntity<ItemDto> response = itemController.addItem(userId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
        verify(itemService).addItem(userId, request);
    }

    @Test
    void updateItem_shouldReturnUpdated() {
        long userId = 1L;
        long itemId = 2L;
        ItemDto patch = new ItemDto();
        patch.setName("Updated");

        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setName("Updated");

        when(itemService.updateItem(userId, itemId, patch)).thenReturn(expected);

        ResponseEntity<ItemDto> response = itemController.updateItem(userId, itemId, patch);

        assertEquals(expected, response.getBody());
        verify(itemService).updateItem(userId, itemId, patch);
    }

    @Test
    void getItemById_shouldReturnItem() {
        long itemId = 3L;
        ItemDto expected = new ItemDto();
        expected.setId(itemId);

        when(itemService.getItemById(itemId)).thenReturn(expected);

        ResponseEntity<ItemDto> response = itemController.getItemById(itemId);

        assertEquals(expected, response.getBody());
        verify(itemService).getItemById(itemId);
    }

    @Test
    void getAllItemsByOwner_shouldReturnList() {
        long ownerId = 1L;
        ItemDto i1 = new ItemDto();
        i1.setId(1L);

        when(itemService.getAllItemsByOwner(ownerId)).thenReturn(List.of(i1));

        ResponseEntity<List<ItemDto>> response = itemController.getAllItemsByOwner(ownerId);

        assertEquals(1, response.getBody().size());
        verify(itemService).getAllItemsByOwner(ownerId);
    }

    @Test
    void searchItems_shouldReturnList() {
        String text = "дрель";
        ItemDto i1 = new ItemDto();
        i1.setId(1L);

        when(itemService.searchItems(text)).thenReturn(List.of(i1));

        ResponseEntity<List<ItemDto>> response = itemController.searchItems(text);

        assertEquals(1, response.getBody().size());
        verify(itemService).searchItems(text);
    }

    @Test
    void addComment_shouldReturnCreatedComment() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto request = new CommentDto();
        request.setText("Класс!");

        CommentDto expected = new CommentDto();
        expected.setId(10L);
        expected.setText("Класс!");

        when(itemService.addComment(userId, itemId, request)).thenReturn(expected);

        ResponseEntity<CommentDto> response = itemController.addComment(userId, itemId, request);

        assertEquals(expected, response.getBody());
        verify(itemService).addComment(userId, itemId, request);
    }
}
