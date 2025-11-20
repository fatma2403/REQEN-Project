package org.example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StandortService {

    /**
     * Liefert alle Standorte, die mindestens eine DC-Ladestation haben.
     */
    public List<Standort> standorteMitDcFiltern(List<Standort> alleStandorte) {
        return alleStandorte.stream()
                .filter(standort ->
                        standort.getLadestationen().stream()
                                .anyMatch(Ladestation::isDc)   // nutzt isDc() aus Ladestation
                )
                .collect(Collectors.toList());
    }

    /**
     * Sucht einen Standort nach ID.
     */
    public Optional<Standort> findeStandortNachId(List<Standort> alle, int id) {
        return alle.stream()
                .filter(s -> s.getStandortId() == id)
                .findFirst();
    }
}
