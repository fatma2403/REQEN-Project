package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KundenkontoService {

    private final Map<String, Kundenkonto> kontenNachKundenId = new HashMap<>();

    /**
     * Legt für einen Kunden ein Konto an, falls noch nicht vorhanden.
     */
    public Kundenkonto kontoFuerKundenSicherstellen(Kunde kunde) {
        if (kunde.getKundenId() == null) {
            throw new IllegalArgumentException("Kunde hat noch keine KundenId");
        }
        return kontenNachKundenId.computeIfAbsent(kunde.getKundenId(), id -> new Kundenkonto());
    }

    public Optional<Kundenkonto> kontoZuKundenId(String kundenId) {
        return Optional.ofNullable(kontenNachKundenId.get(kundenId));
    }

    public double aktuellesGuthaben(String kundenId) {
        return kontoZuKundenId(kundenId)
                .map(Kundenkonto::aktuellesGuthaben)
                .orElse(0.0);
    }

    public void guthabenAufladen(String kundenId, double betrag) {
        Kundenkonto konto = kontenNachKundenId
                .computeIfAbsent(kundenId, id -> new Kundenkonto());
        konto.guthabenAufladen(betrag);
    }
}
