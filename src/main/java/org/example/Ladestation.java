package org.example;

import java.time.LocalDateTime;

public class Ladestation {

    private int ladestationId;
    private Lademodus lademodus;
    private Betriebszustand betriebszustand;

    // --- NEW: Reservation fields ---
    private Kunde reserviertVon;
    private LocalDateTime reserviertVonZeit;
    private LocalDateTime reserviertBisZeit;

    public Ladestation() {
    }

    public Ladestation(int ladestationId, Lademodus lademodus, Betriebszustand betriebszustand) {
        this.ladestationId = ladestationId;
        this.lademodus = lademodus;
        this.betriebszustand = betriebszustand;
    }

    public int getLadestationId() {
        return ladestationId;
    }

    public void setLadestationId(int ladestationId) {
        this.ladestationId = ladestationId;
    }

    public Lademodus getLademodus() {
        return lademodus;
    }

    public void setLademodus(Lademodus lademodus) {
        this.lademodus = lademodus;
    }

    public Betriebszustand getBetriebszustand() {
        return betriebszustand;
    }

    public void setBetriebszustand(Betriebszustand betriebszustand) {
        this.betriebszustand = betriebszustand;
    }

    public boolean isDc() {
        return lademodus == Lademodus.DC;
    }

    public boolean isAc() {
        return lademodus == Lademodus.AC;
    }

    // ------------------------------------------------------
    // NEW: reservation logic
    // ------------------------------------------------------
    public boolean reservieren(Kunde kunde, LocalDateTime start, LocalDateTime end) {

        // If already reserved for that period → reject
        if (reserviertVon != null && reserviertBisZeit != null) {
            boolean overlaps =
                    start.isBefore(reserviertBisZeit) &&
                            end.isAfter(reserviertVonZeit);

            if (overlaps) {
                return false;
            }
        }

        // Save reservation
        this.reserviertVon = kunde;
        this.reserviertVonZeit = start;
        this.reserviertBisZeit = end;

        return true;
    }

    // Optional helper method
    public boolean isReserviert() {
        return reserviertVon != null;
    }
}
