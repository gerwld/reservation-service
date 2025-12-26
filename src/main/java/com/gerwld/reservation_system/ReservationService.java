package com.gerwld.reservation_system;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    public Reservation getReservationById(
            Long id
    ) {
        return new Reservation(
                id,
                100L,
                40L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                ReservatiomStatus.APPROVED
        );
    }

    public List<Reservation> findAllReservations() {
        return List.of(
                new Reservation(
                        0L,
                        100L,
                        40L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(5),
                        ReservatiomStatus.APPROVED
                ),
                new Reservation(
                        1L,
                        100L,
                        40L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(5),
                        ReservatiomStatus.APPROVED
                )
        );
    }
}