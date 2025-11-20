package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Rechnungsverwaltung {

    private final List<Rechnung> rechnungen = new ArrayList<>();

    public Rechnung rechnungErstellen(Kunde kunde,
                                      Ladevorgang vorgang,
                                      Preisregel preisregel,
                                      double betrag) {
        Rechnung rechnung = new Rechnung();
        rechnung.setKunde(kunde);
        rechnung.setLadevorgang(vorgang);
        rechnung.setRechnungsDatum(LocalDateTime.now());
        rechnung.setGesamtBetrag(betrag);
        rechnungen.add(rechnung);
        return rechnung;
    }

    public List<Rechnung> rechnungenNachZeitraum(LocalDateTime von, LocalDateTime bis) {
        return rechnungen.stream()
                .filter(r -> !r.getRechnungsDatum().isBefore(von)
                        && !r.getRechnungsDatum().isAfter(bis))
                .collect(Collectors.toList());
    }

    public List<Rechnung> rechnungenNachKunde(Kunde kunde) {
        return rechnungen.stream()
                .filter(r -> r.getKunde() == kunde)
                .collect(Collectors.toList());
    }

    public List<Rechnung> alleRechnungen() {
        return new ArrayList<>(rechnungen);
    }
}
