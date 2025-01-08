package com.booklibrary.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private boolean available;
    private List<DateRange> availableDateRanges;
}