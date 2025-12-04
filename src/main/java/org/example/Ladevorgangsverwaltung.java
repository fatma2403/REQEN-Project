package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ladevorgangsverwaltung {

    private List<Ladestation> ladestationen = new ArrayList<>();

    public Ladevorgangsverwaltung() {
    }

    public Ladevorgangsverwaltung(List<Ladestation> ladestationen) {
        this.ladestationen = ladestationen;
    }

    public List<Ladestation> getLadestationen() {
        return ladestationen;
    }

    public void setLadestationen(List<Ladestation> ladestationen) {
        this.ladestationen = ladestationen;
    }

    // ------------------------------------------------------------
    // 1. Hilfsmethode: Ladestation anhand ID finden
    // ------------------------------------------------------------
    public Ladestation findeLadestationNachId(int stationId) {
        if (ladestationen == null) {
            return null;
        }
        for (Ladestation station : ladestationen) {
            if (station.getLadestationId() == stationId) {
                return station;
            }
        }
        return null;
    }

    // ------------------------------------------------------------
    // 2. Neue Funktion: Reservierung über die Service-Schicht
    // ------------------------------------------------------------
    /**
     * Reserviert eine Ladestation für einen Kunden in einem bestimmten Zeitfenster.
     *
     * @param stationId ID der Ladestation
     * @param kunde     Kunde, der reservieren möchte
     * @param start     Startzeit der Reservierung
     * @param end       Endzeit der Reservierung
     * @return true, wenn Reservierung erfolgreich war, sonst false
     */
    public boolean reserviereLadestation(int stationId,
                                         Kunde kunde,
                                         LocalDateTime start,
                                         LocalDateTime end) {

        Ladestation station = findeLadestationNachId(stationId);
        if (station == null) {
            return false; // Station existiert nicht
        }

        return station.reservieren(kunde, start, end);
    }

}
