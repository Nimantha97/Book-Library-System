package com.booklibrary.dto;

import com.booklibrary.model.Booking;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingResponse {
    private Long bookingId;
    private Long bookId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userDetails;

    public BookingResponse(Booking booking) {
        this.bookingId = booking.getBookingId();
        this.bookId = booking.getBook().getId();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
        this.userDetails = booking.getUserDetails();
    }
}

