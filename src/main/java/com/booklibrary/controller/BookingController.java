package com.booklibrary.controller;

import com.booklibrary.dto.BookingDTO;
import com.booklibrary.dto.BookingResponse;
import com.booklibrary.dto.AvailabilityResponse;
import com.booklibrary.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }

    @GetMapping("/availability/{bookId}")
    public ResponseEntity<AvailabilityResponse> checkAvailability(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookingService.checkAvailability(bookId));
    }
}
