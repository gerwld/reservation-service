package com.gerwld.reservation_system;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {
    private static Map<Long, Reservation>  reservationMap;
    private static AtomicLong idCounter; // long для многопоточной среды
    public ReservationService() {
        reservationMap = new HashMap<Long, Reservation>();
        idCounter = new AtomicLong();
    }

    public static Reservation createReservation(Reservation reservationToCreate) {
        if(reservationToCreate.id() != null) {
            throw new IllegalArgumentException("id should be empty");
        }
        if(reservationToCreate.status() != null) {
            throw new IllegalArgumentException("status should be empty");
        }

        var newReservation = new Reservation(
                idCounter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);
        return reservationMap.get(newReservation.id());
    }

    public void deleteReservation(
            Long id
    ) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id: " + id);
        }
        reservationMap.remove(id);
    }

    public Reservation getReservationById(
            Long id
    ) {
       if(!reservationMap.containsKey(id)) {
           throw new NoSuchElementException("Not found reservation by id: " + id);
       }
       return reservationMap.get(id);
    }

    public List<Reservation> findAllReservations() {
        return reservationMap.values().stream().toList();
    }

    public static Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id: " + id);
        }
        if(reservationToUpdate == null) {
            throw new IllegalArgumentException("Wrong request, reservationToUpdate body parameter is missing");
        }

        var reservation = reservationMap.get(id);
        if(reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation due to status can be only change from PENDING to *. Current status:" + reservation.status());
        }
        var updatedReservation = new Reservation(
                id,
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(id, updatedReservation);
        return updatedReservation;
    }
}