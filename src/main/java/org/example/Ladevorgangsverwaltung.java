package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ladevorgangsverwaltung {

    private final List<Ladevorgang> ladevorgaenge = new ArrayList<>();

    public Ladevorgang ladevorgangStarten(Kunde kunde,
                                          Ladestation ladestation,
                                          LocalDateTime startzeit) {
        Ladevorgang vorgang = new Ladevorgang();
        vorgang.setKunde(kunde);
        vorgang.setLadestation(ladestation);
        vorgang.setStart(startzeit);
        ladevorgaenge.add(vorgang);
        return vorgang;
    }

    public void ladevorgangBeenden(Ladevorgang vorgang,
                                   LocalDateTime endzeit,
                                   double geladeneMengeKWh) {
        vorgang.setEnde(endzeit);
        vorgang.setGeladeneMengeKWh(geladeneMengeKWh);
    }

    public List<Ladevorgang> ladehistorieFuerKunde(Kunde kunde) {
        return ladevorgaenge.stream()
                .filter(v -> v.getKunde() == kunde)
                .collect(Collectors.toList());
    }

    public List<Ladevorgang> alleLadevorgaenge() {
        return new ArrayList<>(ladevorgaenge);
    }
}
