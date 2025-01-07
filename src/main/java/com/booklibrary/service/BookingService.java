package com.booklibrary.service;

import com.booklibrary.dto.BookingDTO;
import com.booklibrary.dto.BookingResponse;
import com.booklibrary.dto.AvailabilityResponse;
import com.booklibrary.exception.BookingConflictException;
import com.booklibrary.exception.InvalidBookingException;
import com.booklibrary.model.Book;
import com.booklibrary.model.Booking;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.repository.BookingRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookRepository bookRepository;

    public BookingService(BookingRepository bookingRepository, BookRepository bookRepository) {
        this.bookingRepository = bookingRepository;
        this.bookRepository = bookRepository;
    }

    public BookingResponse createBooking(BookingDTO bookingDTO) {
        // Validate end date
        if (bookingDTO.getEndDate().isBefore(LocalDate.now())) {
            throw new InvalidBookingException("End date cannot be in the past");
        }

        Book book = bookRepository.findById(bookingDTO.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        LocalDate startDate = LocalDate.now();
        List<Booking> overlappingBookings = bookingRepository
                .findOverlappingBookings(bookingDTO.getBookId(), startDate, bookingDTO.getEndDate());

        if (!overlappingBookings.isEmpty()) {
            Booking conflict = overlappingBookings.get(0);
            throw new BookingConflictException(
                    String.format("Book is already booked from %s to %s",
                            conflict.getStartDate(),
                            conflict.getEndDate())
            );
        }

        Booking booking = new Booking();
        booking.setBook(book);
        booking.setStartDate(startDate);
        booking.setEndDate(bookingDTO.getEndDate());
        booking.setUserDetails(bookingDTO.getUserDetails());

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingResponse(savedBooking);
    }

    public AvailabilityResponse checkAvailability(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found");
        }

        LocalDate currentDate = LocalDate.now();
        List<Booking> futureBookings = bookingRepository.findFutureBookings(bookId, currentDate);

        if (futureBookings.isEmpty()) {
            return new AvailabilityResponse(true, currentDate);
        }

        // Find next available date after the last booking
        LocalDate nextAvailable = futureBookings.get(futureBookings.size() - 1).getEndDate().plusDays(1);
        return new AvailabilityResponse(false, nextAvailable);
    }
}
