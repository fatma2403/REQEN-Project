package org.example;

public class Ladestation {

    private int ladestationId;
    private Lademodus lademodus;
    private Betriebszustand betriebszustand;

    public Ladestation() {
    }

    public Ladestation(int ladestationId, Lademodus lademodus, Betriebszustand betriebszustand) {
        this.ladestationId = ladestationId;
        this.lademodus = lademodus;
        this.betriebszustand = betriebszustand;
    }

    public int getLadestationId() {
        return ladestationId;
    }

    public void setLadestationId(int ladestationId) {
        this.ladestationId = ladestationId;
    }

    public Lademodus getLademodus() {
        return lademodus;
    }

    public void setLademodus(Lademodus lademodus) {
        this.lademodus = lademodus;
    }

    public Betriebszustand getBetriebszustand() {
        return betriebszustand;
    }

    public void setBetriebszustand(Betriebszustand betriebszustand) {
        this.betriebszustand = betriebszustand;
    }

    public boolean isDc() {
        return lademodus == Lademodus.DC;
    }

    public boolean isAc() {
        return lademodus == Lademodus.AC;
    }

    public int getId() {
        return 0;
    }

    public void setId(int i) {
    }
}
