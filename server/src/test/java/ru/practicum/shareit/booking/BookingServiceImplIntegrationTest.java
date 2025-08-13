package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
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
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        booker = userRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        item = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .build());
    }

    @Test
    void getBookingsByUser_stateAll_shouldReturnAllInDescOrder() {
        Booking past = createBooking(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.APPROVED);
        Booking current = createBooking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), BookingStatus.APPROVED);
        Booking future = createBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "ALL", 0, 10);

        assertEquals(3, result.size());
        assertEquals(future.getId(), result.get(0).getId());
        assertEquals(current.getId(), result.get(1).getId());
        assertEquals(past.getId(), result.get(2).getId());
    }

    @Test
    void getBookingsByUser_stateCurrent_shouldReturnOnlyCurrent() {
        createBooking(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.APPROVED);
        Booking current = createBooking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), BookingStatus.APPROVED);
        createBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "CURRENT", 0, 10);

        assertEquals(1, result.size());
        assertEquals(current.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByUser_whenUserNotFound_shouldThrow() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByUser(999L, "ALL", 0, 10));
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(status)
                .build();
        return bookingRepository.save(booking);
    }
}
