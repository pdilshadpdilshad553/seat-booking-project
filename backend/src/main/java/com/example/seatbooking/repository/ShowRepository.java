package com.example.seatbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.seatbooking.entity.Show;

public interface ShowRepository extends JpaRepository<Show, Long> {}
