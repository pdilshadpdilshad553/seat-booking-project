package com.example.seatbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "seats")
@Getter @Setter @NoArgsConstructor
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "show_id")
    private Show show;

    @Column(name = "seat_row")          // "row" is a reserved word, so we avoid it
    private String seatRow;             // A to H
    private int seatNumber;             // 1 to 10

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne @JoinColumn(name = "booking_id")
    private Booking booking;            // null until booked
}
