package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("создание запроса бронирования");
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto updateBooking(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                                    @Positive(message = "неверное значение") @PathVariable("id") long id,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("обновление запроса бронирования");
        return bookingService.updateBooking(userId, id, bookingDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void delBooking(@Positive(message = "неверное значение") @RequestHeader("X-Sharer-User-Id") long userId,
                           @Positive(message = "неверное значение") @PathVariable("itemId") long itemId) {
        log.info("удаление запроса бронирования");
        bookingService.delBooking(userId, itemId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@Positive(message = "неверное значение") @PathVariable("id") long id) {
        log.info("поиск запроса бронирования id" + id);
        return bookingService.geyBookingById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDto> findAllBooking() {
        log.info("вывод списка запросов бронирования");
        return bookingService.findAllBooking();
    }

}
