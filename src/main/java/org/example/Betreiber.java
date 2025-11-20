package org.example;

public class Betreiber extends Benutzer {

    private String betreiberId;

    public Betreiber() {
    }

    public Betreiber(String name, String email, String passwort) {
        super(name, email, passwort);
    }

    public String getBetreiberId() {
        return betreiberId;
    }

    public void setBetreiberId(String betreiberId) {
        this.betreiberId = betreiberId;
    }
}
