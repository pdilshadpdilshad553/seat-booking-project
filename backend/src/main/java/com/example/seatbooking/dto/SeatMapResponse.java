package com.example.seatbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SeatMapResponse(Long showId, String movieTitle, String screenName,
        LocalDateTime showTime, double price,
        long availableCount, long bookedCount, long blockedCount,
        List<SeatResponse> seats) {
	
}
