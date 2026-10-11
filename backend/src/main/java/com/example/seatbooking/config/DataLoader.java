package com.example.seatbooking.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.seatbooking.entity.Movie;
import com.example.seatbooking.entity.Screen;
import com.example.seatbooking.entity.Seat;
import com.example.seatbooking.entity.SeatStatus;
import com.example.seatbooking.entity.Show;
import com.example.seatbooking.repository.MovieRepository;
import com.example.seatbooking.repository.ScreenRepository;
import com.example.seatbooking.repository.SeatRepository;
import com.example.seatbooking.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MovieRepository movieRepo;
    private final ScreenRepository screenRepo;
    private final ShowRepository showRepo;
    private final SeatRepository seatRepo;

    @Override
    public void run(String... args) {
        if (movieRepo.count() > 0) return;   // already loaded

        Movie m1 = movie("Leo", "Tamil", 164);
        Movie m2 = movie("Pushpa 2", "Telugu", 200);

        Screen s1 = screen("Screen 1");
        Screen s2 = screen("Screen 2");
        Screen s3 = screen("Screen 3");

        LocalDateTime today = LocalDate.now().atStartOfDay();
        createShow(m1, s1, today.plusHours(18), 200);
        createShow(m2, s2, today.plusHours(19), 250);
        createShow(m1, s3, today.plusHours(21), 180);
    }

    private Movie movie(String title, String lang, int mins) {
        Movie m = new Movie();
        m.setTitle(title); m.setLanguage(lang); m.setDurationMins(mins);
        return movieRepo.save(m);
    }

    private Screen screen(String name) {
        Screen s = new Screen();
        s.setName(name);
        return screenRepo.save(s);
    }

    private void createShow(Movie movie, Screen screen, LocalDateTime time, double price) {
        Show show = new Show();
        show.setMovie(movie); show.setScreen(screen);
        show.setShowTime(time); show.setPrice(price);
        showRepo.save(show);

        List<Seat> seats = new ArrayList<>();
        for (char row = 'A'; row <= 'H'; row++) {
            for (int num = 1; num <= 10; num++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setSeatRow(String.valueOf(row));
                seat.setSeatNumber(num);
                seat.setStatus(SeatStatus.AVAILABLE);
                if (row == 'A' && num <= 2) seat.setStatus(SeatStatus.BOOKED);   // demo
                if (row == 'H' && num >= 9) seat.setStatus(SeatStatus.BLOCKED);  // demo
                seats.add(seat);
            }
        }
        seatRepo.saveAll(seats);
    }
}
