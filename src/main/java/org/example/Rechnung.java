package org.example;

import java.time.Instant;
import java.time.LocalDate;
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

    public void setNummer(String number) {
    }

    public double getPreisProKwh() {
        return 0;
    }

    public double getPreisProMinute() {
        return 0;
    }

    public void setKundenEmail(String mail) {
    }

    public void setDatum(LocalDate parse) {
    }

    public Instant getDatum() {
        return null;
    }

    public String getKundenEmail() {
        return "";
    }

    public void setBetrag(double calculatedCost) {
    }

    public String getNummer() {
        return "";
    }

    public double getBetrag() {
        return 0;
    }

    public void setPreisProKwh(double pricePerKwh) {
    }

    public void setPreisProMinute(double pricePerMinute) {
    }

    public void setStart(LocalDateTime parse) {
    }

    public void setEnde(LocalDateTime parse) {
    }

    public void setEnergieKwh(double v) {
    }

    public double getEnergieKwh() {
        return 0;
    }

    public LocalDateTime getStart() {
        return null;
    }

    public LocalDateTime getEnde() {
        return null;
    }
}
