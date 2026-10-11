package com.example.seatbooking.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookingRequest(
        @NotNull(message = "showId is required") Long showId,
        @NotBlank(message = "name is required") String customerName,
        @Pattern(regexp = "\\d{10}", message = "phone must be 10 digits") String phone,
        @NotEmpty(message = "select at least one seat") List<Long> seatIds) {
	
}
