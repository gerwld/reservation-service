package com.gerwld.reservation_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/api/reservations/{id}")
    public Reservation getReservationById(
            @PathVariable("id") Long id
            ) {
        log.info("Called getReservationById, id: "+id);
        return reservationService.getReservationById(id);
    }

    @GetMapping("/api/reservations")
    public List<Reservation> getAllReservations() {
        log.info("Called getAllReservations");
        return reservationService.findAllReservations();
    }

    @PostMapping("/api/reservations")
    public Reservation createReservation(
          @RequestBody Reservation reservationToCreate
    ) {
        log.info("Called createReservation");
        return ReservationService.createReservation(reservationToCreate);

    }
}
