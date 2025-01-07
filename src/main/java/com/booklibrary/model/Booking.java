package com.booklibrary.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "booking")
@Data
public class Booking {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long bookingId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;


    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String userDetails;
}
