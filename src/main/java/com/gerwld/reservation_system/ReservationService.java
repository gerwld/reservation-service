package com.gerwld.reservation_system;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {


    private static Map<Long, Reservation>  reservationMap;
    private static AtomicLong idCounter; // long для многопоточной среды


    public ReservationService(ReservationRepository reservationRepository) {
        this.repo = reservationRepository;
        reservationMap = new HashMap<Long, Reservation>();
        idCounter = new AtomicLong();
    }

    private final ReservationRepository repo;


    public List<Reservation> findAllReservations() {
        List<ReservationEntity> allEntities = repo.findAll();

        return allEntities.stream()
                .map(it ->
                    new Reservation(
                            it.getId(),
                            it.getUserId(),
                            it.getRoomId(),
                            it.getStartDate(),
                            it.getEndDate(),
                            it.getStatus()
                )).toList();

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



    public static Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id: " + id);
        }
        if(reservationToUpdate == null) {
            throw new IllegalArgumentException("Wrong request, reservationToUpdate is missing");
        }

        var reservation = reservationMap.get(id);
        if(reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation due to rule, that status can be only changed  from PENDING to *. Current status:" + reservation.status());
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

    public Reservation approveReservation(Long id) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id: " + id);
        }

        var reservation = reservationMap.get(id);
        if(reservation.status().equals(ReservationStatus.APPROVED)) {
            return reservation;
        }

        if(reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation due to rule, that status can be only changed  from PENDING to *. Current status:" + reservation.status());
        }
        var isConflict = isReservationConflict(reservation);
        if(isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }

        var approvedReservation = new Reservation(
                id,
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED
        );

        reservationMap.put(id, approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflict(
            Reservation reservation
    ) {
        for(Reservation existingReservation: reservationMap.values()) {
            // if current reservation
            if(reservation.id().equals(existingReservation.id())) {
                continue;
            }
            // if different rooms
            if(!reservation.roomId().equals(existingReservation.roomId())) {
                continue;
            }
            // if not approved
            if(!existingReservation.status().equals(ReservationStatus.APPROVED)) {
                continue;
            }

            if(reservation.startDate().isBefore(existingReservation.endDate())
            && existingReservation.startDate().isBefore(reservation.endDate())) {
                return true;
            }
        }
        return false;
    }
}