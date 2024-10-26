package com.ticketbooking.trainticketbooking.config;

import com.ticketbooking.trainticketbooking.model.Seat;
import com.ticketbooking.trainticketbooking.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeatInitializationConfig {

    @Bean
    CommandLineRunner initializeSeats(SeatRepository seatRepository) {
        return args -> {
            // Check if seats are already initialized
            if (seatRepository.count() == 0) {
                List<Seat> seats = new ArrayList<>();

                // Initialize 11 rows with 7 seats each
                for (int row = 1; row <= 12; row++) {
                    int seatsInRow = (row == 12) ? 3 : 7; // Last row has 3 seats
                    int seatNumberOffset = (row - 1) * 7; // Calculate the offset for continuous numbering

                    for (int seatNum = 1; seatNum <= seatsInRow; seatNum++) {
                        Seat seat = new Seat();
                        seat.setSeatRow(row);
                        seat.setSeatNumber(seatNumberOffset + seatNum); // Continuous seat numbering
                        seat.setBooked(false);
                        seats.add(seat);
                    }
                }

                // Save all seats
                seatRepository.saveAll(seats);
                System.out.println("Initialized " + seats.size() + " seats");
            }
        };
    }
}
