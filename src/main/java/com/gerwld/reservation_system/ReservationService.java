package com.gerwld.reservation_system;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
                reservationToCreate.roomId(),
                reservationToCreate.userId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservatiomStatus.PENDING
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
}