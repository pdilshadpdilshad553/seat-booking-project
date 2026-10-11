package com.example.seatbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(Long bookingId, String movieTitle, String screenName,
                              LocalDateTime showTime, String customerName,
                              List<String> seats, double totalAmount, LocalDateTime bookedAt) {
	
}
