package com.ticketbooking.trainticketbooking.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BookingRequest {

    @Min(value = 1, message = "Number of seats must be at least 1")
    @Max(value = 7, message = "Cannot book more than 7 seats at once")
    private Integer numberOfSeats;

}
