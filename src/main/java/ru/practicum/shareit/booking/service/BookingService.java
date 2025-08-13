package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto saveBooking(long userId, BookingDto bookingDto);

    BookingDto updateBooking(long userId, long itemId, BookingDto bookingDto);


    void delBooking(long userId, long itemId);


    BookingDto geyBookingById(long id);


    Collection<BookingDto> findAllBooking();
}
