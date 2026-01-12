package org.example;

import java.util.ArrayList;
import java.util.List;

public class NoMatchingLocationsDemo {
    public static void main(String[] args) {
        StandortService standortService = new StandortService();

        List<Standort> locations = new ArrayList<>();

        Standort s1 = new Standort();
        s1.setStandortId(1);
        s1.setName("City Center");
        s1.setAdresse("Main St 1");
        s1.getLadestationen().add(new Ladestation(1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));

        Standort s2 = new Standort();
        s2.setStandortId(2);
        s2.setName("Mall Parking");
        s2.setAdresse("Mall Rd 5");
        s2.getLadestationen().add(new Ladestation(2, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));

        locations.add(s1);
        locations.add(s2);

        List<Standort> dcLocations = standortService.standorteMitDcFiltern(locations);

        if (dcLocations.isEmpty()) {
            System.out.println("NO_MATCHING_LOCATIONS");
        } else {
            System.out.println("DC locations found: " + dcLocations.size());
        }
    }
}

