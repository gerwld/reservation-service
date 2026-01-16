package com.gerwld.reservation_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
            ) {
        log.info("Called getReservationById, id: "+id);

        try {
           Reservation reservation = reservationService.getReservationById(id);
            return ResponseEntity.status(HttpStatus.OK).body(reservation);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("Called getAllReservations");
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.findAllReservations());
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(
          @RequestBody Reservation reservationToCreate
    ) {
        log.info("Called createReservation");
       return ResponseEntity.status(HttpStatus.CREATED)
               .header("test-header", "123")
               .body(reservationService.createReservation(reservationToCreate));
        // return ReservationService.createReservation(reservationToCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservationToUpdate
    ) {
        log.info("Called updateReservation");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ReservationService.updateReservation(id, reservationToUpdate));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
           @PathVariable Long id
    ) {
        log.info("Called approveReservation, id: " + id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReservation(
            @PathVariable("id") Long id
    ) {
        log.info("Called deleteReservation");
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok().build();
        }
         catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
         }
    }

}
