package org.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LadevorgangsverwaltungReservationTest {

    /**
     * Erster Reservierungsversuch über die Service-Schicht sollte funktionieren.
     */
    @Test
    void firstReservationViaServiceShouldSucceed() {
        // arrange
        Ladestation station = new Ladestation(
                1,
                Lademodus.DC,
                Betriebszustand.IN_BETRIEB_FREI
        );

        Ladevorgangsverwaltung verwaltung = new Ladevorgangsverwaltung(
                Arrays.asList(station)
        );

        Kunde kunde = new Kunde(
                "Martin Keller",
                "martin.keller@testmail.com",
                "Secure456!"
        );

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 11, 0);

        // act
        boolean result = verwaltung.reserviereLadestation(
                1,
                kunde,
                start,
                end
        );

        // assert
        assertTrue(result, "First reservation via service should succeed");
        assertTrue(station.isReserviert(), "Station should be marked as reserved");
    }

    /**
     * Überlappende Reservierung über die Service-Schicht sollte fehlschlagen.
     */
    @Test
    void overlappingReservationViaServiceShouldFail() {
        // arrange
        Ladestation station = new Ladestation(
                2,
                Lademodus.DC,
                Betriebszustand.IN_BETRIEB_FREI
        );

        Ladevorgangsverwaltung verwaltung = new Ladevorgangsverwaltung(
                Arrays.asList(station)
        );

        Kunde kunde1 = new Kunde(
                "Martin Keller",
                "martin.keller@testmail.com",
                "Secure456!"
        );
        Kunde kunde2 = new Kunde(
                "Laura Fischer",
                "laura.fischer@testmail.com",
                "Secure456!"
        );

        // erste Reservierung: 10:00–11:00
        LocalDateTime firstStart = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime firstEnd   = LocalDateTime.of(2025, 1, 1, 11, 0);
        boolean first = verwaltung.reserviereLadestation(
                2,
                kunde1,
                firstStart,
                firstEnd
        );
        assertTrue(first, "First reservation should succeed");

        // zweite (überlappende) Reservierung: 10:30–11:30
        LocalDateTime secondStart = LocalDateTime.of(2025, 1, 1, 10, 30);
        LocalDateTime secondEnd   = LocalDateTime.of(2025, 1, 1, 11, 30);

        // act
        boolean second = verwaltung.reserviereLadestation(
                2,
                kunde2,
                secondStart,
                secondEnd
        );

        // assert
        assertFalse(second, "Overlapping reservation via service should fail");
    }
}
