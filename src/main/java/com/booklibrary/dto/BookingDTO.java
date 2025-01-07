package com.booklibrary.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDTO {

    private Long bookingId;

    @NotNull(message = "Book IDis required")
    private Long bookId;

    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in future")
    private LocalDate endDate;

    private String userDetails;
}

