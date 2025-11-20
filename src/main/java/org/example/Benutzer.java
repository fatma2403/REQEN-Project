package org.example;

public class Benutzer {

    private String name;
    private String email;
    private String passwort;

    public Benutzer() {
    }

    public Benutzer(String name, String email, String passwort) {
        this.name = name;
        this.email = email;
        this.passwort = passwort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }
}
