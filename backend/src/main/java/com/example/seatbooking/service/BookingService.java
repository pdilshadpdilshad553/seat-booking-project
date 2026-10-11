package com.example.seatbooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.seatbooking.dto.BookingRequest;
import com.example.seatbooking.dto.BookingResponse;
import com.example.seatbooking.entity.Booking;
import com.example.seatbooking.entity.Seat;
import com.example.seatbooking.entity.SeatStatus;
import com.example.seatbooking.entity.Show;
import com.example.seatbooking.exception.ResourceNotFoundException;
import com.example.seatbooking.exception.SeatUnavailableException;
import com.example.seatbooking.repository.BookingRepository;
import com.example.seatbooking.repository.SeatRepository;
import com.example.seatbooking.repository.ShowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowRepository showRepo;
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;

    @Transactional   // all steps succeed together, or nothing is saved
    public BookingResponse book(BookingRequest req) {
        Show show = showRepo.findById(req.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + req.showId()));

        List<Seat> seats = seatRepo.findByIdInForUpdate(req.seatIds());
        if (seats.size() != req.seatIds().size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        for (Seat s : seats) {
            if (!s.getShow().getId().equals(show.getId())) {
                throw new IllegalArgumentException("Seat " + label(s) + " does not belong to this show");
            }
            if (s.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatUnavailableException("Seat " + label(s) + " is not available");
            }
        }

        Booking booking = new Booking();
        booking.setShow(show);
        booking.setCustomerName(req.customerName());
        booking.setPhone(req.phone());
        booking.setTotalAmount(show.getPrice() * seats.size());
        booking.setBookedAt(LocalDateTime.now());
        bookingRepo.save(booking);

        for (Seat s : seats) {
            s.setStatus(SeatStatus.BOOKED);
            s.setBooking(booking);
        }
        seatRepo.saveAll(seats);

        return toResponse(booking, seats);
    }

    public BookingResponse getBooking(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        List<Seat> seats = seatRepo.findAll().stream()
                .filter(s -> s.getBooking() != null && s.getBooking().getId().equals(id))
                .toList();
        return toResponse(booking, seats);
    }

    private BookingResponse toResponse(Booking b, List<Seat> seats) {
        return new BookingResponse(b.getId(), b.getShow().getMovie().getTitle(),
                b.getShow().getScreen().getName(), b.getShow().getShowTime(),
                b.getCustomerName(), seats.stream().map(this::label).toList(),
                b.getTotalAmount(), b.getBookedAt());
    }

    private String label(Seat s) { return s.getSeatRow() + s.getSeatNumber(); }   // A1, B7...
}
