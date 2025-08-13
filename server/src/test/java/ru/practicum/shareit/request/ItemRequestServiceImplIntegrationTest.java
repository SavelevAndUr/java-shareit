package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getAllRequestsByUser_shouldReturnRequestsWithItems() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        ItemRequest req1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужна дрель")
                .requester(user)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        ItemRequest req2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужен молоток")
                .requester(user)
                .created(LocalDateTime.now())
                .build());

        itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(user)
                .requestId(req1.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .requestId(req2.getId())
                .build());

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUser(user.getId());

        assertEquals(2, result.size());

        // последний по created
        ItemRequestDto latest = result.get(0);
        assertEquals(req2.getId(), latest.getId());
        assertEquals("Нужен молоток", latest.getDescription());
        assertEquals(1, latest.getItems().size());
        ItemDto latestItem = latest.getItems().get(0);
        assertEquals("Молоток", latestItem.getName());

        // более ранний
        ItemRequestDto earlier = result.get(1);
        assertEquals(req1.getId(), earlier.getId());
        assertEquals("Нужна дрель", earlier.getDescription());
        assertEquals(1, earlier.getItems().size());
        ItemDto earlierItem = earlier.getItems().get(0);
        assertEquals("Дрель", earlierItem.getName());
    }

    @Test
    void getAllRequestsByUser_whenNoRequests_shouldReturnEmpty() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUser(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequestsByUser_whenUserNotFound_shouldThrow() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequestsByUser(999L));
    }
}
