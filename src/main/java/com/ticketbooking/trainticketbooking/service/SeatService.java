package com.ticketbooking.trainticketbooking.service;

import com.ticketbooking.trainticketbooking.exception.BookingException;
import com.ticketbooking.trainticketbooking.exception.InvalidBookingReferenceException;
import com.ticketbooking.trainticketbooking.model.BookingResponse;
import com.ticketbooking.trainticketbooking.model.Seat;

import java.util.List;

public interface SeatService {
    public BookingResponse bookSeats(Integer numberOfSeats) throws BookingException;
    public BookingResponse bookSeatsInRow(Integer rowNumber, Integer numberOfSeats) throws BookingException;
    public BookingResponse bookNearbySeats(Integer numberOfSeats) throws BookingException;
    public List<Seat> getSeatsByBookingReference(String bookingReference) throws InvalidBookingReferenceException;
    public List<Seat> getAllSeats();
    public String generateBookingReference();
    public void resetAllSeats();
}
