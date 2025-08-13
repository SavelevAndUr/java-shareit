package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;


@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(USER_ID_HEADER) Long bookerId,
            @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(
            @RequestHeader(USER_ID_HEADER) Long bookerId) {
        return bookingClient.getAllBookingsByBooker(bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return bookingClient.getAllBookingsByOwner(ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }
}