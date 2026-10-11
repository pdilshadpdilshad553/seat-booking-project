package com.example.seatbooking.dto;

import java.time.LocalDateTime;

public record ShowResponse(Long id, String movieTitle, String screenName,
        LocalDateTime showTime, double price) {
	
}