package org.example;

import java.time.LocalDateTime;

public class Rechnung {

    private int rechnungsNr;
    private LocalDateTime rechnungsDatum;
    private double gesamtBetrag;

    private Kunde kunde;
    private Ladevorgang ladevorgang;

    public int getRechnungsNr() {
        return rechnungsNr;
    }

    public void setRechnungsNr(int rechnungsNr) {
        this.rechnungsNr = rechnungsNr;
    }

    public LocalDateTime getRechnungsDatum() {
        return rechnungsDatum;
    }

    public void setRechnungsDatum(LocalDateTime rechnungsDatum) {
        this.rechnungsDatum = rechnungsDatum;
    }

    public double getGesamtBetrag() {
        return gesamtBetrag;
    }

    public void setGesamtBetrag(double gesamtBetrag) {
        this.gesamtBetrag = gesamtBetrag;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Ladevorgang getLadevorgang() {
        return ladevorgang;
    }

    public void setLadevorgang(Ladevorgang ladevorgang) {
        this.ladevorgang = ladevorgang;
    }
}
