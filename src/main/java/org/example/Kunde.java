package org.example;

public class Kunde extends Benutzer {

    private String kundenId;

    public Kunde() {
    }

    public Kunde(String name, String email, String passwort) {
        super(name, email, passwort);
    }

    public String getKundenId() {
        return kundenId;
    }

    public void setKundenId(String kundenId) {
        this.kundenId = kundenId;
    }
}
