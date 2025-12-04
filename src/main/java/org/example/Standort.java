package org.example;

import java.util.ArrayList;
import java.util.List;

public class Standort {

    private int standortId;
    private String name;
    private String adresse;
    private final List<Ladestation> ladestationen = new ArrayList<>();

    public int getStandortId() {
        return standortId;
    }

    public void setStandortId(int standortId) {
        this.standortId = standortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<Ladestation> getLadestationen() {
        return ladestationen;
    }


}
