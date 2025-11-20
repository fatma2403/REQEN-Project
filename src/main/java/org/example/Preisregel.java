package org.example;

import java.time.LocalDateTime;

public class Preisregel {

    private int preisregelId;
    private Lademodus lademodus;
    private double preisProKWh;
    private double preisProMinute;
    private LocalDateTime gueltigAb;

    public Preisregel(int i, Lademodus lademodus, double v, double v1, LocalDateTime localDateTime) {
    }

    public Preisregel() {

    }

    public int getPreisregelId() {
        return preisregelId;
    }

    public void setPreisregelId(int preisregelId) {
        this.preisregelId = preisregelId;
    }

    public Lademodus getLademodus() {
        return lademodus;
    }

    public void setLademodus(Lademodus lademodus) {
        this.lademodus = lademodus;
    }

    public double getPreisProKWh() {
        return preisProKWh;
    }

    public void setPreisProKWh(double preisProKWh) {
        this.preisProKWh = preisProKWh;
    }

    public double getPreisProMinute() {
        return preisProMinute;
    }

    public void setPreisProMinute(double preisProMinute) {
        this.preisProMinute = preisProMinute;
    }

    public LocalDateTime getGueltigAb() {
        return gueltigAb;
    }

    public void setGueltigAb(LocalDateTime gueltigAb) {
        this.gueltigAb = gueltigAb;
    }

    public double berechnePreis(double energieKWh, int dauerMinuten) {
        return energieKWh * preisProKWh + dauerMinuten * preisProMinute;
    }
}
