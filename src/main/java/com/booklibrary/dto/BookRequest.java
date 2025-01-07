package com.booklibrary.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Author is required")
    private String author;

    @NotNull
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private List<String> tags;
}