package com.example.seatbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.seatbooking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	
}