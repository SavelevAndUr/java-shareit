package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void createBooking_shouldCreateNewBooking() {
        long userId = 1L;
        BookingDto request = new BookingDto();
        request.setItemId(1L);

        BookingDto expectedBooking = new BookingDto();
        expectedBooking.setId(1L);

        when(bookingService.createBooking(userId, request)).thenReturn(expectedBooking);

        var response = bookingController.createBooking(userId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedBooking, response.getBody());
        verify(bookingService).createBooking(userId, request);
    }

    @Test
    void approveBooking_shouldApprove() {
        long ownerId = 1L;
        long bookingId = 10L;
        boolean approved = true;

        BookingDto expected = new BookingDto();
        expected.setId(bookingId);
        expected.setStatus("APPROVED");

        when(bookingService.approveBooking(ownerId, bookingId, approved)).thenReturn(expected);

        var response = bookingController.approveBooking(ownerId, bookingId, approved);

        assertEquals(expected, response.getBody());
        verify(bookingService).approveBooking(ownerId, bookingId, approved);
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        long userId = 1L;
        long bookingId = 2L;

        BookingDto expected = new BookingDto();
        expected.setId(bookingId);

        when(bookingService.getBookingById(bookingId, userId)).thenReturn(expected);

        var response = bookingController.getBookingById(userId, bookingId);

        assertEquals(expected, response.getBody());
        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    void getAllBookingsByBooker_shouldReturnList() {
        long userId = 1L;

        BookingDto b1 = new BookingDto();
        b1.setId(1L);
        BookingDto b2 = new BookingDto();
        b2.setId(2L);

        when(bookingService.getBookingsByUser(userId, "ALL", 0, 10)).thenReturn(List.of(b1, b2));

        var response = bookingController.getAllBookingsByBooker(userId, "ALL", 0, 10);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(bookingService).getBookingsByUser(userId, "ALL", 0, 10);
    }

    @Test
    void getAllBookingsByOwner_shouldReturnList() {
        long ownerId = 1L;

        BookingDto b1 = new BookingDto();
        b1.setId(1L);

        when(bookingService.getBookingsByOwner(ownerId, "ALL", 0, 10)).thenReturn(List.of(b1));

        var response = bookingController.getAllBookingsByOwner(ownerId, "ALL", 0, 10);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(bookingService).getBookingsByOwner(ownerId, "ALL", 0, 10);
    }
}
