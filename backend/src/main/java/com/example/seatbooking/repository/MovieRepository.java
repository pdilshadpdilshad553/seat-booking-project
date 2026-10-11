package com.example.seatbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.seatbooking.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	
}