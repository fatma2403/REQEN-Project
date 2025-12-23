package org.example;

import java.util.*;
import java.util.function.Predicate;

public class Kundenverwaltung {

    private final Map<String, Kunde> kundenNachId = new HashMap<>();

    /**
     * Registriert einen neuen Kunden im System.
     */
    public void kundeRegistrieren(Kunde kunde) {
        if (kunde == null) {
            throw new IllegalArgumentException("kunde darf nicht null sein");
        }
        String email = kunde.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("The entered email is missing an @");
        }

        if (kunde.getKundenId() == null) {
            // zur Not eine einfache ID vergeben, wenn noch keine da ist
            kunde.setKundenId(UUID.randomUUID().toString());
        }
        kundenNachId.put(kunde.getKundenId(), kunde);
    }

    /**
     * Liefert einen Kunden zur ID, falls vorhanden.
     */
    public Optional<Kunde> findeKundeNachId(String kundenId) {
        return Optional.ofNullable(kundenNachId.get(kundenId));
    }

    /**
     * Aktualisiert die Kundendaten (Überschreibt den Eintrag zur gleichen ID).
     */
    public void kundendatenAktualisieren(Kunde kunde) {
        if (kunde == null || kunde.getKundenId() == null) {
            throw new IllegalArgumentException("kunde oder kundenId ist null");
        }
        kundenNachId.put(kunde.getKundenId(), kunde);
    }

    /**
     * Gibt alle Kunden zurück.
     */
    public List<Kunde> alleKunden() {
        return new ArrayList<>(kundenNachId.values());
    }

    /**
     * Filtert Kunden nach einem beliebigen Kriterium (für Listen-/Filter-Stories).
     */
    public List<Kunde> kundenFiltern(Predicate<Kunde> kriterium) {
        return kundenNachId.values().stream()
                .filter(kriterium)
                .toList();
    }
}
