package com.gerwld.reservation_system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;


    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(
            @PathVariable("id") Long id
            ) {
        System.out.println("call: getReservationById");
        return reservationService.getReservationById(id);
    }

    @GetMapping("/api/get")
    public List<Reservation> getAllReservations() {
        System.out.println("call: getAllReservations");
        return reservationService.findAllReservations();
    }

}
