package org.example;

import java.time.LocalDateTime;
import java.util.*;

public class Preisverwaltung {

    private final Map<Standort, List<Preisregel>> preisregelnProStandort = new HashMap<>();

    public Preisregel preisregelAnlegen(Standort standort,
                                        Lademodus lademodus,
                                        double preisProKWh,
                                        double preisProMinute,
                                        LocalDateTime gueltigAb) {

        Preisregel regel = new Preisregel();
        regel.setLademodus(lademodus);
        regel.setPreisProKWh(preisProKWh);
        regel.setPreisProMinute(preisProMinute);
        regel.setGueltigAb(gueltigAb);

        preisregelnProStandort
                .computeIfAbsent(standort, s -> new ArrayList<>())
                .add(regel);

        return regel;
    }

    public List<Preisregel> preisregelnFuerStandort(Standort standort) {
        return new ArrayList<>(preisregelnProStandort.getOrDefault(standort, List.of()));
    }

    /**
     * Liefert die aktuell gültige Preisregel für einen Vorgang.
     * (Sehr einfache Logik: erste passende Regel mit Datum <= Startzeit)
     */
    public Optional<Preisregel> findePreisregel(Standort standort,
                                                Lademodus modus,
                                                LocalDateTime zeitpunkt) {
        return preisregelnProStandort
                .getOrDefault(standort, List.of())
                .stream()
                .filter(r -> r.getLademodus() == modus)
                .filter(r -> !r.getGueltigAb().isAfter(zeitpunkt))
                .max(Comparator.comparing(Preisregel::getGueltigAb));
    }
}
