package org.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LadestationReservationTest {

    @Test
    void firstReservationShouldSucceed() {
        // arrange
        Ladestation station = new Ladestation(1, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI);
        Kunde kunde = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 11, 0);

        // act
        boolean result = station.reservieren(kunde, start, end);

        // assert
        assertTrue(result, "First reservation should succeed");
        assertTrue(station.isReserviert(), "Station should be marked as reserved");
    }

    @Test
    void overlappingReservationShouldFail() {
        // arrange
        Ladestation station = new Ladestation(1, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI);
        Kunde kunde1 = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        Kunde kunde2 = new Kunde("Laura Fischer", "laura.fischer@testmail.com", "Secure456!");

        LocalDateTime firstStart = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime firstEnd   = LocalDateTime.of(2025, 1, 1, 11, 0);

        // first reservation -> should succeed
        boolean first = station.reservieren(kunde1, firstStart, firstEnd);
        assertTrue(first);

        // second reservation with overlap
        LocalDateTime secondStart = LocalDateTime.of(2025, 1, 1, 10, 30);
        LocalDateTime secondEnd   = LocalDateTime.of(2025, 1, 1, 11, 30);

        // act
        boolean second = station.reservieren(kunde2, secondStart, secondEnd);

        // assert
        assertFalse(second, "Overlapping reservation should fail");
    }

    @Test
    void nonOverlappingReservationShouldSucceed() {
        // arrange
        Ladestation station = new Ladestation(1, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI);
        Kunde kunde1 = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        Kunde kunde2 = new Kunde("Jonas Weber", "jonas.weber@testmail.com", "Secure456!");

        LocalDateTime firstStart = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime firstEnd   = LocalDateTime.of(2025, 1, 1, 11, 0);

        boolean first = station.reservieren(kunde1, firstStart, firstEnd);
        assertTrue(first);

        // second slot without overlap (directly after first)
        LocalDateTime secondStart = LocalDateTime.of(2025, 1, 1, 11, 0);
        LocalDateTime secondEnd   = LocalDateTime.of(2025, 1, 1, 12, 0);

        // act
        boolean second = station.reservieren(kunde2, secondStart, secondEnd);

        // assert
        assertTrue(second, "Non-overlapping reservation should succeed");
    }
}
