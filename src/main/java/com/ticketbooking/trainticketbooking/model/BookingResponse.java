package com.ticketbooking.trainticketbooking.model;

import lombok.Data;

import java.util.List;

@Data
public class BookingResponse {

    private String bookingReference;
    private List<Integer> allocatedSeats;
    private String message;

}
