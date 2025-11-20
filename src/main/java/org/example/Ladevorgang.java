package org.example;

import java.time.LocalDateTime;

public class Ladevorgang {

    private int vorgangsId;
    private LocalDateTime start;
    private LocalDateTime ende;
    private double geladeneMengeKWh;

    private Kunde kunde;
    private Ladestation ladestation;

    public int getVorgangsId() {
        return vorgangsId;
    }

    public void setVorgangsId(int vorgangsId) {
        this.vorgangsId = vorgangsId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnde() {
        return ende;
    }

    public void setEnde(LocalDateTime ende) {
        this.ende = ende;
    }

    public double getGeladeneMengeKWh() {
        return geladeneMengeKWh;
    }

    public void setGeladeneMengeKWh(double geladeneMengeKWh) {
        this.geladeneMengeKWh = geladeneMengeKWh;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Ladestation getLadestation() {
        return ladestation;
    }

    public void setLadestation(Ladestation ladestation) {
        this.ladestation = ladestation;
    }
}
