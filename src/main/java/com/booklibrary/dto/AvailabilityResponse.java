package com.booklibrary.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private boolean available;
    private LocalDate nextAvailableDate;
}
