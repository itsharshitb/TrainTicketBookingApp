package com.ticketbooking.trainticketbooking.repository;

import com.ticketbooking.trainticketbooking.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findBySeatRowAndIsBookedFalseOrderBySeatNumber(Integer seatRow);

    @Query("""
    SELECT s
    FROM Seat s
    WHERE s.seatRow = :seatRow AND s.isBooked = false
    ORDER BY s.seatNumber
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying(clearAutomatically = true)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    List<Seat> findUnbookedSeatsInRow(@Param("seatRow") Integer seatRow);

    @Query("""
    SELECT DISTINCT s.seatRow
    FROM Seat s
    WHERE s.isBooked = false
    GROUP BY s.seatRow
    HAVING COUNT(s) >= ?1
""")
    List<Integer> findRowsWithEnoughSeats(Integer requiredSeats);

    @Query("""
        SELECT s
        FROM Seat s
        WHERE s.isBooked = false
        ORDER BY s.seatRow, s.seatNumber
        """)
    List<Seat> findNearbyAvailableSeats();

    List<Seat> findByBookingReference(String bookingReference);

}
