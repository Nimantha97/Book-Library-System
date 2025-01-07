package com.booklibrary.repository;

import com.booklibrary.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.book.id = :bookId " +
            "AND ((b.startDate BETWEEN :startDate AND :endDate) " +
            "OR (b.endDate BETWEEN :startDate AND :endDate) " +
            "OR (:startDate BETWEEN b.startDate AND b.endDate))")
    List<Booking> findOverlappingBookings(Long bookId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.book.id = :bookId " +
            "AND b.startDate >= :currentDate ORDER BY b.startDate ASC")
    List<Booking> findFutureBookings(Long bookId, LocalDate currentDate);
}