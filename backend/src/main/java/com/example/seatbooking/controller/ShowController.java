package com.example.seatbooking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.seatbooking.dto.SeatMapResponse;
import com.example.seatbooking.dto.ShowResponse;
import com.example.seatbooking.service.ShowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public List<ShowResponse> getShows() {
        return showService.getAllShows();
    }

    @GetMapping("/{id}/seats")
    public SeatMapResponse getSeats(@PathVariable Long id) {
        return showService.getSeatMap(id);
    }
}
