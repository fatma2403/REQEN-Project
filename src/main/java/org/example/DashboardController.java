package org.example;

import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    /**
     * Gibt einfach die Standorte zurück, die überwacht werden – für die
     * Dashboard-Stories kannst du hier später mehr Logik einbauen.
     */
    public List<Standort> statusUebersichtAnzeigen(List<Standort> standorte) {
        return new ArrayList<>(standorte);
    }

    /**
     * Platzhalter für Fehlermeldungen je Ladestation.
     */
    public List<Ladestation> fehlerMeldungenAnzeigen(List<Ladestation> alleStationen) {
        // sehr einfache Version: gib einfach alle zurück, in der Praxis würdest du
        // z.B. nach Betriebszustand oder Fehlerflag filtern
        return new ArrayList<>(alleStationen);
    }
}
