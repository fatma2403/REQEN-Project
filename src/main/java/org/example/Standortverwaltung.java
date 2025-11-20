package org.example;

import java.util.ArrayList;
import java.util.List;

public class Standortverwaltung {

    private final List<Standort> standorte = new ArrayList<>();

    public Standort standortAnlegen(String name, String adresse) {
        Standort standort = new Standort();
        standort.setName(name);
        standort.setAdresse(adresse);
        standorte.add(standort);
        return standort;
    }

    public void standortEntfernen(Standort standort) {
        standorte.remove(standort);
    }

    public List<Standort> standortUebersichtAnzeigen() {
        return new ArrayList<>(standorte);
    }

    public void ladestationZuStandortZuweisen(Standort standort, Ladestation ladestation) {
        standort.getLadestationen().add(ladestation);
    }

    public List<Standort> alleStandorte() {
        return new ArrayList<>(standorte);
    }
}
