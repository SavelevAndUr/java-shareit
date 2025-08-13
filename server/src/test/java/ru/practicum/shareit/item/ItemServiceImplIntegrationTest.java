package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
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
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Owner").email("owner@email.com").build());
        booker = userRepository.save(User.builder().name("Booker").email("booker@email.com").build());

        item1 = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .owner(owner)
                .build());
        item2 = itemRepository.save(Item.builder()
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .owner(owner)
                .build());

        LocalDateTime now = LocalDateTime.now();
        bookingRepository.save(Booking.builder()
                .start(now.minusDays(2)).end(now.minusDays(1))
                .item(item1).booker(booker).status(BookingStatus.APPROVED).build());
        bookingRepository.save(Booking.builder()
                .start(now.plusDays(1)).end(now.plusDays(2))
                .item(item1).booker(booker).status(BookingStatus.APPROVED).build());

        commentRepository.save(Comment.builder()
                .text("Хорошая дрель")
                .item(item1)
                .author(booker)
                .created(now)
                .build());
    }

    @Test
    void getAllItemsByOwner_shouldReturnItemsWithLastAndNextAndComments() {
        List<ItemDto> items = itemService.getAllItemsByOwner(owner.getId());

        assertEquals(2, items.size());

        ItemDto drill = items.stream()
                .filter(i -> "Дрель".equals(i.getName()))
                .findFirst()
                .orElseThrow();

        BookingShortDto lastBooking = drill.getLastBooking();
        BookingShortDto nextBooking = drill.getNextBooking();
        List<CommentDto> comments = drill.getComments();

        assertNotNull(lastBooking);
        assertNotNull(nextBooking);
        assertEquals(1, comments.size());
        assertEquals("Хорошая дрель", comments.get(0).getText());

        ItemDto screwdriver = items.stream()
                .filter(i -> "Отвертка".equals(i.getName()))
                .findFirst()
                .orElseThrow();
        assertNull(screwdriver.getLastBooking());
        assertNull(screwdriver.getNextBooking());
        assertTrue(screwdriver.getComments().isEmpty());
    }
}
