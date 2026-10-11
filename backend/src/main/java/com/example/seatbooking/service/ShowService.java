package com.example.seatbooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.seatbooking.dto.SeatMapResponse;
import com.example.seatbooking.dto.SeatResponse;
import com.example.seatbooking.dto.ShowResponse;
import com.example.seatbooking.entity.Seat;
import com.example.seatbooking.entity.SeatStatus;
import com.example.seatbooking.entity.Show;
import com.example.seatbooking.exception.ResourceNotFoundException;
import com.example.seatbooking.repository.SeatRepository;
import com.example.seatbooking.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepo;
    private final SeatRepository seatRepo;

    public List<ShowResponse> getAllShows() {
        return showRepo.findAll().stream()
                .map(s -> new ShowResponse(s.getId(), s.getMovie().getTitle(),
                        s.getScreen().getName(), s.getShowTime(), s.getPrice()))
                .toList();
    }

    public SeatMapResponse getSeatMap(Long showId) {
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));

        List<Seat> seats = seatRepo.findByShowIdOrderBySeatRowAscSeatNumberAsc(showId);

        List<SeatResponse> seatList = seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatRow(), s.getSeatNumber(), s.getStatus().name()))
                .toList();

        long available = seats.stream().filter(s -> s.getStatus() == SeatStatus.AVAILABLE).count();
        long booked    = seats.stream().filter(s -> s.getStatus() == SeatStatus.BOOKED).count();
        long blocked   = seats.stream().filter(s -> s.getStatus() == SeatStatus.BLOCKED).count();

        return new SeatMapResponse(show.getId(), show.getMovie().getTitle(), show.getScreen().getName(),
                show.getShowTime(), show.getPrice(), available, booked, blocked, seatList);
    }
}