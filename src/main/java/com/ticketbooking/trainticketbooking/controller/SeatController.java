package com.ticketbooking.trainticketbooking.controller;

import com.ticketbooking.trainticketbooking.model.BookingRequest;
import com.ticketbooking.trainticketbooking.model.BookingResponse;
import com.ticketbooking.trainticketbooking.model.Seat;
import com.ticketbooking.trainticketbooking.service.SeatService;
import com.ticketbooking.trainticketbooking.service.SeatServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@Slf4j
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatServiceImpl seatServiceImpl) {
        this.seatService = seatServiceImpl;
    }

    //To Book seats
    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookSeats(@Valid @RequestBody BookingRequest request) {
        log.info("Received booking request for {} seats", request.getNumberOfSeats());
        BookingResponse response = seatService.bookSeats(request.getNumberOfSeats());
        return ResponseEntity.ok(response);
    }

    //To check seat with ref no.
    @GetMapping("/status/{reference}")
    public ResponseEntity<List<Seat>> getSeatStatusByReference(@PathVariable String reference) {
        List<Seat> seats = seatService.getSeatsByBookingReference(reference);
        if (seats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(seats);
    }

    // To get seat statuses
    @GetMapping("/status")
    public ResponseEntity<List<Seat>> getAllSeatStatuses() {
        List<Seat> seatStatuses = seatService.getAllSeats();
        return ResponseEntity.ok(seatStatuses);
    }

    // To reset all seats
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetSeats() {
        seatService.resetAllSeats();  // Call the service method to reset seats
        return new ResponseEntity<>("All seats have been reset and made available for booking.", HttpStatus.OK);
    }
}
