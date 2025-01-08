package com.booklibrary.service;

import com.booklibrary.dto.BookingDTO;
import com.booklibrary.dto.BookingResponse;
import com.booklibrary.dto.AvailabilityResponse;
import com.booklibrary.dto.DateRange;
import com.booklibrary.exception.BookingConflictException;
import com.booklibrary.exception.InvalidBookingException;
import com.booklibrary.model.Book;
import com.booklibrary.model.Booking;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.repository.BookingRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        LocalDate currentDate = LocalDate.now();
        if (bookingDTO.getEndDate().isBefore(currentDate)) {
            throw new InvalidBookingException("End date cannot be in the past");
        }

        Book book = bookRepository.findById(bookingDTO.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Get all bookings for a related book
        List<Booking> existingBookings = bookingRepository.findByBookIdOrderByStartDateAsc(bookingDTO.getBookId());

        // Check for overlaps
        Booking conflictingBooking = null;
        for (Booking existing : existingBookings) {
            if (isOverlapping(currentDate, bookingDTO.getEndDate(),
                    existing.getStartDate(), existing.getEndDate())) {
                conflictingBooking = existing;
                break;
            }
        }

        if (conflictingBooking != null) {
            throw new BookingConflictException(
                    String.format("Book is already booked from %s to %s",
                            conflictingBooking.getStartDate(),
                            conflictingBooking.getEndDate())
            );
        }



        Booking booking = new Booking();
        booking.setBook(book);
        booking.setStartDate(currentDate);
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
        List<Booking> allBookings = bookingRepository.findByBookIdOrderByStartDateAsc(bookId);

        if (allBookings.isEmpty()) {
            // If no bookings exist, book is available from today
            return new AvailabilityResponse(true,
                    List.of(new DateRange(currentDate, currentDate.plusYears(1))));
        }

        // Check if current date is within any booking period
        boolean isAvailable = true;
        for (Booking booking : allBookings) {
            if (isDateInRange(currentDate, booking.getStartDate(), booking.getEndDate())) {
                isAvailable = false;
                break;
            }
        }

        // Find all available date ranges
        List<DateRange> availableDateRanges = findAvailableDateRanges(allBookings, currentDate);

        return new AvailabilityResponse(isAvailable, availableDateRanges);
    }

    // Helper method to check if a date falls within a range
    private boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isEqual(endDate) || date.isBefore(endDate));
    }
    private boolean isOverlapping(LocalDate start1, LocalDate end1,
                                  LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private List<DateRange> findAvailableDateRanges(List<Booking> bookings, LocalDate currentDate) {
        List<DateRange> availableRanges = new ArrayList<>();
        LocalDate lastEndDate = currentDate;

        for (Booking booking : bookings) {
            // If there's a gap between last end date and next booking's start date
            // if there are 2 bookings-> 01.10 - 01.15 , 01.20 - 01.25
            //lastEndDate = currentDay= 01.08
            if (lastEndDate.isBefore(booking.getStartDate())) {
                // if( 01.08 < 01.10 - then there is a gap. add the gap range -> [01.08 - 01.09 (01.10 - 1) ]
                availableRanges.add(new DateRange(lastEndDate, booking.getStartDate().minusDays(1)));
            }
            // lastEndDate = 01.16 (01.15 + 1)
            lastEndDate = booking.getEndDate().plusDays(1);
        }

        // Add final range after last booking
        if (!lastEndDate.isAfter(currentDate)) {
            // If lastEndDate is not after the currentDate -> lastEndDate = 2023.01.08, currentDate = 2023.01.08
            // Meaning no bookings overlap or extend past today.
            // Add the range from currentDate to one year ahead -> [2023.01.08 to 2024.01.07]
            availableRanges.add(new DateRange(currentDate, currentDate.plusYears(1)));

        } else if (lastEndDate.isBefore(currentDate.plusYears(1))) {
            // lastEndDate = 2023.01.16, currentDate = 2023.01.08
            // Add the range from lastEndDate to one year ahead -> [2023.01.16 to 2024.01.07]
            availableRanges.add(new DateRange(lastEndDate, currentDate.plusYears(1)));
        }

        return availableRanges;
    }
}



