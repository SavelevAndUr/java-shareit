package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader(USER_ID_HEADER) Long bookerId,
            @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.createBooking(bookerId, bookingDto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByBooker(
            @RequestHeader(USER_ID_HEADER) Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(bookerId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(bookingService.getBookingsByOwner(ownerId, state, from, size));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(ownerId, bookingId, approved));
    }
}