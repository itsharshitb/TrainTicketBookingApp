package com.ticketbooking.trainticketbooking.service;

import com.ticketbooking.trainticketbooking.exception.BookingException;
import com.ticketbooking.trainticketbooking.exception.InvalidBookingReferenceException;
import com.ticketbooking.trainticketbooking.model.BookingResponse;
import com.ticketbooking.trainticketbooking.model.Seat;
import com.ticketbooking.trainticketbooking.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BookingResponse bookSeats(Integer numberOfSeats) throws BookingException {
        log.info("Attempting to book {} seats", numberOfSeats);

        if (numberOfSeats > 7) {
            log.error("Cannot book more than 7 seats at once");
            throw new BookingException("Cannot book more than 7 seats at once");
        }

        List<Integer> rowsWithEnoughSeats = seatRepository.findRowsWithEnoughSeats(numberOfSeats);

        if (!rowsWithEnoughSeats.isEmpty()) {
            log.info("Found available seats in row {}", rowsWithEnoughSeats.get(0));
            // Ensure all changes are flushed to the database before proceeding
            seatRepository.flush();
            return bookSeatsInRow(rowsWithEnoughSeats.get(0), numberOfSeats);
        }

        log.info("No single row available, attempting to book nearby seats");
        return bookNearbySeats(numberOfSeats);
    }

    @Override
    public BookingResponse bookSeatsInRow(Integer seatRow, Integer numberOfSeats) throws BookingException {
        List<Seat> availableSeats = new ArrayList<>(seatRepository.findUnbookedSeatsInRow(seatRow));

        log.info("Available seats in row {}: {}", seatRow, availableSeats);
        log.info("length before check: {}", availableSeats.size());

        if (availableSeats.stream().count() < numberOfSeats) {
            throw new BookingException("Not enough seats available in row " + seatRow);
        }

        String bookingReference = generateBookingReference();
        List<Seat> seatsToBook = new ArrayList<>();

        // Book the required number of seats
        for (int i = 0; i < numberOfSeats; i++) {
            Seat seat = availableSeats.get(i);
            seat.setBooked(true);
            seat.setBookingReference(bookingReference);
            seatsToBook.add(seat);
            log.info("Booking seat: {}", seat);
        }

        // Save all booked seats in one go
        seatRepository.saveAll(seatsToBook);

        BookingResponse response = new BookingResponse();
        response.setBookingReference(bookingReference);
        response.setAllocatedSeats(seatsToBook.stream().map(Seat::getSeatNumber).collect(Collectors.toList()));
        response.setMessage("Seats booked successfully in row " + seatRow);
        return response;
    }

    @Override
    public BookingResponse bookNearbySeats(Integer numberOfSeats) throws BookingException {
        List<Seat> nearbySeats = seatRepository.findNearbyAvailableSeats();

        // Filter for only unbooked seats and check if we have enough
        if (nearbySeats.size() < numberOfSeats) {
            throw new BookingException("Not enough seats available for nearby booking");
        }

        // Collect only the required number of seats
        List<Seat> allocatedSeats = nearbySeats.stream()
                .limit(numberOfSeats)
                .collect(Collectors.toList());

        return finalizeBooking(allocatedSeats);
    }



    private BookingResponse finalizeBooking(List<Seat> seatsToBook) {
        String bookingReference = generateBookingReference();
        List<Integer> allocatedSeatNumbers = new ArrayList<>();

        // Logging before updating the seats
        log.info("Before saving, seats to book: {}", seatsToBook);

        for (Seat seat : seatsToBook) {
            seat.setBooked(true);
            seat.setBookingReference(bookingReference);
            allocatedSeatNumbers.add(seat.getSeatNumber());
        }

        seatRepository.saveAll(seatsToBook); // Save the updated seats

        // Logging after updating the seats
        log.info("After saving, booked seats: {}", allocatedSeatNumbers);

        BookingResponse response = new BookingResponse();
        response.setBookingReference(bookingReference);
        response.setAllocatedSeats(allocatedSeatNumbers);
        response.setMessage("Seats booked successfully");

        return response;
    }



    @Override
    public List<Seat> getSeatsByBookingReference(String bookingReference) throws InvalidBookingReferenceException {
        List<Seat> seats = seatRepository.findByBookingReference(bookingReference);

        if (seats.isEmpty()) {
            throw new InvalidBookingReferenceException("Invalid booking reference: " + bookingReference);
        }

        return seats;
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();  // Fetches all seats with their status
    }

    @Override
    public String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public void resetAllSeats() {
        List<Seat> allSeats = seatRepository.findAll();  // Fetch all seats

        // Set all seats as unbooked
        for (Seat seat : allSeats) {
            seat.setBooked(false);
            seat.setBookingReference(null); // Optional: Clear booking reference
        }

        seatRepository.saveAll(allSeats);  // Save all updated seats
        log.info("All seats have been reset and made available for booking.");
    }
}
