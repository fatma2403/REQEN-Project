package org.example;

import org.example.Kunde;
import org.example.Kundenkonto;
import org.example.PaymentMethod;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        demoCustomerAccount();
        demoTopUpBalance();

        // Gemeinsame System-Standortliste, die von mehreren Demos benutzt wird
        List<Standort> standorte = new ArrayList<>();

        demoManageLocations(standorte);               // füllt standorte mit City Center, Mall Parking, Highway etc.
        demoPricingManagement();                      // arbeitet intern, braucht die Liste nicht
        demoReservationChargingAndBilling(standorte); // ergänzt City Center + Station 11
        demoOperatorDashboard(standorte);             // nutzt die vorhandenen Standorte
        demoCustomerDataManagementEdgeCase();         // zeigt Betreiber edge case wenn er das Guthaben einer email sehen will, die es nicht gibt
        demoViewLocationsEdgeCaseUnknownLocation();
        demoViewInvoicesNoInvoices();
    }

    // =====================================================================
    // Customer Account
    // =====================================================================
    private static void demoCustomerAccount() {
        System.out.println("=== Demo: Customer Account ===");

        Kundenverwaltung kundenverwaltung = new Kundenverwaltung();
        boolean loggedIn = false;

        System.out.println("\n-- Scenario: Register or log in --");

        Kunde customer = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        customer.setKundenId(null);
        System.out.println("Neuer Kunde ohne Account:");
        System.out.println("  Name:  " + customer.getName());
        System.out.println("  Email: " + customer.getEmail());

        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;

        System.out.println("Kunde wurde registriert.");
        System.out.println("Zugewiesene Kunden-ID: " + customer.getKundenId());
        System.out.println("Eingeloggt: " + loggedIn);

        System.out.println("\n-- Scenario: Receive a customer ID --");

        customer.setKundenId("CUST-1023");
        kundenverwaltung.kundendatenAktualisieren(customer);

        System.out.println("Konto wurde aktiviert, neue Kunden-ID gesetzt:");
        System.out.println("  Kunden-ID: " + customer.getKundenId());
        System.out.println("  Email:     " + customer.getEmail());

        System.out.println("\n-- Scenario: View personal data --");

        Kunde shownData = kundenverwaltung
                .findeKundeNachId(customer.getKundenId())
                .orElse(null);

        if (shownData != null) {
            System.out.println("Persönliche Daten anzeigen für Email 'martin.keller@testmail.com':");
            System.out.println("  Name:  " + shownData.getName());
            System.out.println("  Email: " + shownData.getEmail());
            System.out.println("  ID:    " + shownData.getKundenId());
        } else {
            System.out.println("Kunde mit ID " + customer.getKundenId() + " wurde nicht gefunden!");
        }

        System.out.println("\n-- Scenario: Edit personal data --");

        String neueEmail = "martin.keller.new@testmail.com";
        System.out.println("Kunde ändert seine Email auf: " + neueEmail);

        customer.setEmail(neueEmail);
        kundenverwaltung.kundendatenAktualisieren(customer);

        Kunde aktualisierterKunde = kundenverwaltung
                .findeKundeNachId(customer.getKundenId())
                .orElseThrow(() -> new IllegalStateException("Kunde nicht gefunden"));

        System.out.println("Aktualisierte Kundendaten:");
        System.out.println("  Name:  " + aktualisierterKunde.getName());
        System.out.println("  Email: " + aktualisierterKunde.getEmail());
        System.out.println("  ID:    " + aktualisierterKunde.getKundenId());

        // -----------------------------------------
// Scenario: Invalid email during registration (edge case)
// -----------------------------------------
        System.out.println("\n-- Scenario: Invalid email during registration (edge case) --");

        String invalidEmail = "martin.kellertestmail.com"; // missing @
        System.out.println("Kunde versucht sich mit ungültiger Email zu registrieren: " + invalidEmail);

        Kunde invalidCustomer = new Kunde("Martin Keller", invalidEmail, "Secure456!");
        invalidCustomer.setKundenId(null);

        boolean registrationRejected = false;
        String errorMessage = null;

        try {
            // this should fail because email is invalid
            kundenverwaltung.kundeRegistrieren(invalidCustomer);

            // if we get here, it was NOT rejected -> mark as failure in demo output
            registrationRejected = false;
        } catch (IllegalArgumentException ex) {
            registrationRejected = true;
            errorMessage = ex.getMessage();
        }

// Output like the feature
        System.out.println("System lehnt Registrierung ab? " + registrationRejected);

        String expectedMessage = "The entered email is missing an @";
        System.out.println("Fehlermeldung: " + errorMessage + " (erwartet: " + expectedMessage + ")");

// Optional small safety check for the demo
        if (!registrationRejected || errorMessage == null || !errorMessage.equals(expectedMessage)) {
            System.out.println("⚠ Demo-Hinweis: Erwartetes Verhalten wurde NICHT exakt getroffen.");
        } else {
            System.out.println("Edge case erfolgreich abgefangen.");
        }

        System.out.println("\n=== Ende Demo: Customer Account ===");
    }

    // =====================================================================
    // Top up balance
    // =====================================================================
    private static void demoTopUpBalance() {
        System.out.println();
        System.out.println("=== Demo: Top up balance ===");

        System.out.println("\n-- Scenario: Use the prepaid system --");

        Kunde customer = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        Kundenkonto account = new Kundenkonto(1, 20.0);
        double startBalance = account.aktuellesGuthaben();

        System.out.println("Kunde: " + customer.getName() + " (" + customer.getEmail() + ")");
        System.out.println("Startguthaben: " + startBalance + " EUR");

        System.out.println("Kunde wählt die Prepaid-Auflade-Option.");

        double topUpAmount = 15.0;
        PaymentMethod currentPaymentMethod = PaymentMethod.CREDIT_CARD;
        System.out.println("Kunde gibt Betrag ein: " + topUpAmount + " EUR");
        System.out.println("Zahlungsmethode für diese Aufladung: " + currentPaymentMethod);

        if (topUpAmount > 0) {
            account.guthabenAufladen(topUpAmount, currentPaymentMethod);
            System.out.println("Zahlung bestätigt.");
        } else {
            System.out.println("Ungültiger Betrag, Aufladung nicht durchgeführt.");
        }

        double newBalance = account.aktuellesGuthaben();
        System.out.println("Neues Guthaben: " + newBalance + " EUR (erwartet: 35.0 EUR)");

        System.out.println("\n-- Scenario: Set preferred payment method --");

        PaymentMethod preferredPaymentMethod = PaymentMethod.PAYPAL;
        System.out.println("Kunde wählt bevorzugte Zahlungsmethode: " + preferredPaymentMethod);

        System.out.println("System speichert " + preferredPaymentMethod + " als Standard-Zahlungsmethode.");

        double futureTopUp = 10.0;
        System.out.println("Zukünftige Aufladung: " + futureTopUp + " EUR, verwendet automatisch: " + preferredPaymentMethod);

        account.guthabenAufladen(futureTopUp, preferredPaymentMethod);
        double finalBalance = account.aktuellesGuthaben();

        System.out.println("Guthaben nach weiterer Aufladung: " + finalBalance + " EUR");
        System.out.println("Bevorzugte Zahlungsmethode bleibt: " + preferredPaymentMethod);

        System.out.println("\n=== Ende Demo: Top up balance ===");
    }

    // =====================================================================
    // Manage locations – verwendet gemeinsame standorte-Liste
    // =====================================================================
    private static void demoManageLocations(List<Standort> standorte) {
        System.out.println();
        System.out.println("=== Demo: Manage locations ===");

        System.out.println("\n-- Scenario: Change operating status of a charging station --");

        Standort cityCenter = new Standort();
        cityCenter.setStandortId(1);
        cityCenter.setName("City Center");
        cityCenter.setAdresse("Hauptstraße 1, 1010 Wien");

        Ladestation station101 =
                new Ladestation(101, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI);
        cityCenter.getLadestationen().add(station101);

        standorte.add(cityCenter);

        System.out.println("Standort: " + cityCenter.getName() + " (ID " + cityCenter.getStandortId() + ")");
        System.out.println("  Ladestation " + station101.getLadestationId()
                + " Modus=" + station101.getLademodus()
                + " Status=" + station101.getBetriebszustand());

        System.out.println("Betreiber setzt Station 101 auf Status AUSSER_BETRIEB …");
        station101.setBetriebszustand(Betriebszustand.AUSSER_BETRIEB);

        System.out.println("Neuer Status der Station 101: " + station101.getBetriebszustand());
        boolean verfuegbar = station101.getBetriebszustand() == Betriebszustand.IN_BETRIEB_FREI;
        System.out.println("Verfügbar für neue Ladevorgänge? " + verfuegbar);

        System.out.println("\n-- Scenario: Edit location data --");

        Standort mall = new Standort();
        mall.setStandortId(2);
        mall.setName("Mall Parking");
        mall.setAdresse("Einkaufspark 5, 4020 Linz");
        standorte.add(mall);

        System.out.println("Alter Standort-Name: " + mall.getName());
        System.out.println("Alte Adresse:        " + mall.getAdresse());

        mall.setName("Mall Parking Nord");
        mall.setAdresse("Einkaufspark Nord 7, 4020 Linz");

        System.out.println("Aktualisierter Standort-Name: " + mall.getName());
        System.out.println("Aktualisierte Adresse:        " + mall.getAdresse());

        System.out.println("\n-- Scenario: Remove a location --");

        Standort oldLocation = new Standort();
        oldLocation.setStandortId(3);
        oldLocation.setName("Old Station");
        oldLocation.setAdresse("Industriestraße 9, 3100 St. Pölten");
        standorte.add(oldLocation);

        System.out.println("Aktive Standorte vor Entfernen:");
        for (Standort s : standorte) {
            System.out.println("  ID " + s.getStandortId() + " – " + s.getName());
        }

        standorte.removeIf(s -> s.getStandortId() == 3);

        System.out.println("Aktive Standorte nach Entfernen von ID 3:");
        for (Standort s : standorte) {
            System.out.println("  ID " + s.getStandortId() + " – " + s.getName());
        }

        System.out.println("\n-- Scenario: Assign a charging station to a location --");

        Ladestation unassignedStation =
                new Ladestation(201, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);

        Standort highway = new Standort();
        highway.setStandortId(4);
        highway.setName("Highway Station");
        highway.setAdresse("Autobahn A1, Rastplatz West");
        standorte.add(highway);

        System.out.println("Unzugeordnete Station: " + unassignedStation.getLadestationId()
                + " Modus=" + unassignedStation.getLademodus()
                + " Status=" + unassignedStation.getBetriebszustand());
        System.out.println("Zielstandort: " + highway.getName() + " (ID " + highway.getStandortId() + ")");

        highway.getLadestationen().add(unassignedStation);

        System.out.println("Ladestationen am Standort '" + highway.getName() + "':");
        for (Ladestation ls : highway.getLadestationen()) {
            System.out.println("  Station " + ls.getLadestationId()
                    + " Modus=" + ls.getLademodus()
                    + " Status=" + ls.getBetriebszustand());
        }

        System.out.println("\n=== Ende Demo: Manage locations ===");
    }

    // =====================================================================
    // Manage pricing (arbeitet für sich)
    // =====================================================================
    private static void demoPricingManagement() {
        System.out.println("=== Demo: Manage pricing ===");

        Standort cityCenter = new Standort();
        cityCenter.setStandortId(1);
        cityCenter.setName("City Center");
        cityCenter.setAdresse("Hauptstraße 1, 1010 Wien");

        Preisverwaltung preisverwaltung = new Preisverwaltung();

        System.out.println("\n-- Scenario: Set prices per location (AC) --");

        LocalDateTime gueltigAbAc = LocalDateTime.parse("2025-11-01T00:00");
        Preisregel acRegel_035 = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.AC,
                0.35,
                0.05,
                gueltigAbAc
        );

        System.out.println("Betreiber definiert AC-Preise für Standort 'City Center':");
        System.out.println("  Preisregel (AC): 0.35 €/kWh, 0.05 €/Minute, gültig ab " + gueltigAbAc);
        System.out.println("  Gespeicherte Regel:");
        System.out.println("    Modus:       " + acRegel_035.getLademodus());
        System.out.println("    Preis/kWh:   " + acRegel_035.getPreisProKWh());
        System.out.println("    Preis/Min:   " + acRegel_035.getPreisProMinute());
        System.out.println("    Gültig ab:   " + acRegel_035.getGueltigAb());

        System.out.println("\n-- Scenario: Adjust AC and DC price parameters --");

        LocalDateTime gueltigAbUpdated = LocalDateTime.parse("2025-11-15T00:00");

        Preisregel acRegel_040 = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.AC,
                0.40,
                0.06,
                gueltigAbUpdated
        );

        Preisregel dcRegel_050 = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.DC,
                0.50,
                0.12,
                gueltigAbUpdated
        );

        System.out.println("Betreiber passt AC- und DC-Preise an (gültig ab " + gueltigAbUpdated + "):");
        System.out.println("  Neue AC-Regel: 0.40 €/kWh, 0.06 €/Minute");
        System.out.println("  Neue DC-Regel: 0.50 €/kWh, 0.12 €/Minute");

        List<Preisregel> cityRules = preisverwaltung.preisregelnFuerStandort(cityCenter);
        System.out.println("Aktuelle Preisregeln für 'City Center':");
        for (Preisregel r : cityRules) {
            System.out.printf(
                    "  Modus: %s | Preis/kWh: %.2f | Preis/Min: %.2f | gültig ab: %s%n",
                    r.getLademodus(), r.getPreisProKWh(), r.getPreisProMinute(), r.getGueltigAb()
            );
        }

        System.out.println("\n-- Scenario: Update pricing rule (ID 101) --");

        acRegel_040.setPreisregelId(101);

        System.out.println("Bestehende Regel 101 (AC) vor Update:");
        System.out.printf("  ID: %d | Preis/kWh: %.2f | Preis/Min: %.2f | gültig ab: %s%n",
                acRegel_040.getPreisregelId(),
                acRegel_040.getPreisProKWh(),
                acRegel_040.getPreisProMinute(),
                acRegel_040.getGueltigAb()
        );

        LocalDateTime gueltigAbFuture = LocalDateTime.parse("2025-12-01T00:00");
        Preisregel acRegel_038 = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.AC,
                0.38,
                0.05,
                gueltigAbFuture
        );
        acRegel_038.setPreisregelId(101);

        System.out.println("Update von Regel 101:");
        System.out.printf("  Neue Werte ab %s -> Preis/kWh: %.2f, Preis/Min: %.2f%n",
                gueltigAbFuture, acRegel_038.getPreisProKWh(), acRegel_038.getPreisProMinute());

        LocalDateTime futureSessionTime = LocalDateTime.parse("2025-12-10T10:00");
        Preisregel gueltigeFutureRegel = preisverwaltung
                .findePreisregel(cityCenter, Lademodus.AC, futureSessionTime)
                .orElseThrow();

        System.out.println("Gültige AC-Regel für eine Ladung am " + futureSessionTime + ":");
        System.out.printf("  ID: %d | Preis/kWh: %.2f | Preis/Min: %.2f | gültig ab: %s%n",
                gueltigeFutureRegel.getPreisregelId(),
                gueltigeFutureRegel.getPreisProKWh(),
                gueltigeFutureRegel.getPreisProMinute(),
                gueltigeFutureRegel.getGueltigAb()
        );

        System.out.println("\n-- Scenario: Validate price during charging (Martin Keller, DC) --");

        LocalDateTime gueltigAbDc = LocalDateTime.parse("2025-11-01T00:00");
        Preisregel dcRegel_045 = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.DC,
                0.45,
                0.10,
                gueltigAbDc
        );

        System.out.println("DC-Preisregel für 'City Center':");
        System.out.printf("  Preis/kWh: %.2f | Preis/Min: %.2f | gültig ab: %s%n",
                dcRegel_045.getPreisProKWh(),
                dcRegel_045.getPreisProMinute(),
                dcRegel_045.getGueltigAb()
        );

        double energieKWh = 24.0;
        int dauerMin = 30;
        LocalDateTime ladeEnde = LocalDateTime.parse("2025-11-20T10:30");

        Preisregel gueltigeDcRegel = preisverwaltung
                .findePreisregel(cityCenter, Lademodus.DC, ladeEnde)
                .orElseThrow();

        double preis = gueltigeDcRegel.berechnePreis(energieKWh, dauerMin);

        System.out.println("Ladevorgang DC (24.0 kWh, 30 Minuten) am 2025-11-20T10:30:");
        System.out.printf("  Verwendete Regel: Preis/kWh = %.2f, Preis/Min = %.2f%n",
                gueltigeDcRegel.getPreisProKWh(), gueltigeDcRegel.getPreisProMinute());
        System.out.printf("  Berechneter Gesamtpreis: %.2f EUR (erwartet: 13.80 EUR)%n", preis);

        System.out.println("\n=== Ende Demo: Manage pricing ===");
    }

    // =====================================================================
    // Reservation, Charging & Billing – fügt City Center + Station 11 ein
    // =====================================================================
    private static void demoReservationChargingAndBilling(List<Standort> standorte) {
        System.out.println("=== Demo: Reservation, Charging & Billing ===");

        Kunde martin = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        martin.setKundenId("CUST-1023");

        Standort cityCenter = standorte.stream()
                .filter(s -> s.getStandortId() == 1)
                .findFirst()
                .orElseGet(() -> {
                    Standort s = new Standort();
                    s.setStandortId(1);
                    s.setName("City Center");
                    s.setAdresse("Hauptstraße 1, 1010 Wien");
                    standorte.add(s);
                    return s;
                });

        Ladestation dcStation = new Ladestation(
                11,
                Lademodus.DC,
                Betriebszustand.IN_BETRIEB_FREI
        );
        cityCenter.getLadestationen().add(dcStation);

        System.out.println("\n-- Setup --");
        System.out.println("Kunde: " + martin.getName() + " (" + martin.getEmail() + "), ID: " + martin.getKundenId());
        System.out.println("Standort: " + cityCenter.getName() + " (" + cityCenter.getAdresse() + ")");
        System.out.println("Ladestation: ID " + dcStation.getLadestationId()
                + ", Modus " + dcStation.getLademodus()
                + ", Zustand " + dcStation.getBetriebszustand());

        Ladevorgangsverwaltung verwaltung =
                new Ladevorgangsverwaltung(List.of(dcStation));

        LocalDateTime reservStart = LocalDateTime.of(2025, 11, 20, 9, 50);
        LocalDateTime reservEnd   = LocalDateTime.of(2025, 11, 20, 10, 30);

        boolean reservationOk = verwaltung.reserviereLadestation(
                11,
                martin,
                reservStart,
                reservEnd
        );

        System.out.println("\n-- Reservation --");
        System.out.println("Reservierungszeit: " + reservStart + " bis " + reservEnd);
        System.out.println("Reservierung erfolgreich? " + reservationOk);
        System.out.println("Station reserviert?      " + dcStation.isReserviert());

        Ladevorgang vorgang = new Ladevorgang();
        vorgang.setVorgangsId(5001);
        vorgang.setKunde(martin);
        vorgang.setLadestation(dcStation);

        LocalDateTime ladeStart = LocalDateTime.parse("2025-11-20T10:00");
        LocalDateTime ladeEnde  = LocalDateTime.parse("2025-11-20T10:30");
        vorgang.setStart(ladeStart);
        vorgang.setEnde(ladeEnde);
        vorgang.setGeladeneMengeKWh(24.0);

        long dauerMinuten = Duration.between(ladeStart, ladeEnde).toMinutes();

        System.out.println("\n-- Charging Session --");
        System.out.println("Vorgangs-ID: " + vorgang.getVorgangsId());
        System.out.println("Start:       " + ladeStart);
        System.out.println("Ende:        " + ladeEnde);
        System.out.println("Dauer:       " + dauerMinuten + " Minuten");
        System.out.println("Energiemenge:" + vorgang.getGeladeneMengeKWh() + " kWh");

        Preisverwaltung preisverwaltung = new Preisverwaltung();
        LocalDateTime gueltigAb = LocalDateTime.parse("2025-11-01T00:00");

        Preisregel dcRegel = preisverwaltung.preisregelAnlegen(
                cityCenter,
                Lademodus.DC,
                0.45,
                0.10,
                gueltigAb
        );

        System.out.println("\n-- Pricing --");
        System.out.println("Preisregel: Standort " + cityCenter.getName()
                + ", Modus " + dcRegel.getLademodus());
        System.out.println("  Preis pro kWh:    " + dcRegel.getPreisProKWh() + " EUR");
        System.out.println("  Preis pro Minute: " + dcRegel.getPreisProMinute() + " EUR");
        System.out.println("  Gültig ab:        " + dcRegel.getGueltigAb());

        List<Preisregel> alleRegeln = preisverwaltung.preisregelnFuerStandort(cityCenter);

        Preisregel regelZurAbrechnung = null;
        for (Preisregel regel : alleRegeln) {
            boolean modusPasst   = regel.getLademodus() == Lademodus.DC;
            boolean datumGueltig = !regel.getGueltigAb().isAfter(ladeEnde);

            if (modusPasst && datumGueltig) {
                if (regelZurAbrechnung == null
                        || regel.getGueltigAb().isAfter(regelZurAbrechnung.getGueltigAb())) {
                    regelZurAbrechnung = regel;
                }
            }
        }

        if (regelZurAbrechnung == null) {
            System.out.println("Keine passende Preisregel gefunden – Abrechnung nicht möglich!");
            return;
        }

        double betrag = regelZurAbrechnung.berechnePreis(
                vorgang.getGeladeneMengeKWh(),
                (int) dauerMinuten
        );

        System.out.println("\n-- Price Calculation --");
        System.out.println("Benutzte Preisregel gültig ab: " + regelZurAbrechnung.getGueltigAb());
        System.out.println("Berechneter Betrag: " + String.format("%.2f EUR", betrag)
                + " (erwartet: 13.80 EUR)");

        Rechnungsverwaltung rechnungsverwaltung = new Rechnungsverwaltung();
        Rechnung rechnung = rechnungsverwaltung.rechnungErstellen(
                martin,
                vorgang,
                regelZurAbrechnung,
                betrag
        );
        rechnung.setRechnungsNr(1001);

        System.out.println("\n-- Invoice --");
        System.out.println("Rechnungs-Nr.:  " + rechnung.getRechnungsNr());
        System.out.println("Kunde:          " + rechnung.getKunde().getName()
                + " (" + rechnung.getKunde().getKundenId() + ")");
        System.out.println("Ladevorgang-ID: " + rechnung.getLadevorgang().getVorgangsId());
        System.out.println("Datum:          " + rechnung.getRechnungsDatum());
        System.out.println("Gesamtbetrag:   " + String.format("%.2f EUR", rechnung.getGesamtBetrag()));

        System.out.println("\n=== Ende Demo: Reservation, Charging & Billing ===");
    }

    // =====================================================================
    // Operator Dashboard – verwendet gemeinsame standorte-Liste
    // =====================================================================
    private static void demoOperatorDashboard(List<Standort> standorte) {

        System.out.println("=== Demo: Operator Dashboard ===");

        Betreiber betreiber = new Betreiber("Admin Operator", "operator@testsystem.com", "AdminPass123");
        betreiber.setBetreiberId("BET-01");

        System.out.println("Betreiber '" + betreiber.getName()
                + "' (ID: " + betreiber.getBetreiberId() + ") ist eingeloggt.\n");

        DashboardController dashboard = new DashboardController();

        System.out.println("-- Scenario: Monitor locations in real time --");

        List<Standort> uebersicht = dashboard.statusUebersichtAnzeigen(standorte);

        for (Standort s : uebersicht) {
            System.out.println("Standort: " + s.getName() + " (" + s.getAdresse() + ")");
            for (Ladestation ls : s.getLadestationen()) {
                System.out.println("  Station " + ls.getLadestationId()
                        + " | Modus: " + ls.getLademodus()
                        + " | Status: " + ls.getBetriebszustand());
            }
        }

        System.out.println("\n-- Scenario: Customize dashboard --");

        String layout = "Standard";
        System.out.println("Aktuelles Dashboard-Layout: " + layout);

        layout = "KPI_COMPACT";
        System.out.println("Neues Layout gespeichert: " + layout);

        System.out.println("\n-- Scenario: Fault handling --");

        Ladestation fehlerStation = standorte.stream()
                .flatMap(s -> s.getLadestationen().stream())
                .findFirst()
                .orElse(null);

        if (fehlerStation == null) {
            System.out.println("WARNUNG: Keine Ladestationen im System vorhanden. Fehler-Szenario wird übersprungen.");
            System.out.println("=== Ende Demo: Operator Dashboard ===");
            return;
        }

        fehlerStation.setBetriebszustand(Betriebszustand.AUSSER_BETRIEB);

        List<Ladestation> alleStationen = standorte.stream()
                .flatMap(s -> s.getLadestationen().stream())
                .toList();

        List<Ladestation> fehlerListe = dashboard.fehlerMeldungenAnzeigen(alleStationen);

        System.out.println("Fehlerhafte Ladestationen:");

        for (Ladestation ls : fehlerListe) {
            if (ls.getBetriebszustand() == Betriebszustand.AUSSER_BETRIEB) {

                String standortName = standorte.stream()
                        .filter(s -> s.getLadestationen().contains(ls))
                        .map(Standort::getName)
                        .findFirst()
                        .orElse("?");

                System.out.println("  ⚠ Station " + ls.getLadestationId()
                        + " am Standort '" + standortName + "' ist AUSSER_BETRIEB.");
            }
        }

        System.out.println("\n=== Ende Demo: Operator Dashboard ===");
    }

    private static void demoCustomerDataManagementEdgeCase() {
        System.out.println();
        System.out.println("=== Demo: Manage customer data (edge case) ===");

        System.out.println("\n-- Scenario: Operator opens balance for an unknown customer email --");

        // Setup: multiple customers exist
        List<Kunde> kundenListe = new ArrayList<>();
        kundenListe.add(new Kunde("Martin Keller", "martin.keller@testmail.com", "pw1"));
        kundenListe.add(new Kunde("Laura Fischer", "laura.fischer@testmail.com", "pw2"));
        kundenListe.add(new Kunde("Jonas Weber", "jonas.weber@testmail.com", "pw3"));

        String emailToSearch = "unknown@testmail.com";

        System.out.println("Operator öffnet Kontostand-Info für Email: " + emailToSearch);

        Kunde found = kundenListe.stream()
                .filter(k -> k.getEmail() != null && k.getEmail().equals(emailToSearch))
                .findFirst()
                .orElse(null);

        if (found == null) {
            String errorMessage = "Customer with email " + emailToSearch + " was not found";
            System.out.println("System lehnt Anfrage ab.");
            System.out.println("Fehlermeldung: " + errorMessage);
        } else {
            // (Not expected in this edge case)
            Kundenkonto konto = new Kundenkonto();
            konto.setGuthaben(75.50);
            System.out.println("Kunde gefunden: " + found.getName());
            System.out.println("Kontostand: " + konto.aktuellesGuthaben());
        }

        System.out.println("\n=== Ende Demo: Manage customer data (edge case) ===");
    }

    private static void demoViewLocationsEdgeCaseUnknownLocation() {
        System.out.println("\n=== Demo: View charging locations (Edge Case) ===");

        Kunde kunde = new Kunde("Martin Keller", "martin.keller@testmail.com", "secret");
        kunde.setKundenId("CUST-1023");

        List<Standort> standorte = new ArrayList<>();

        Standort cityCenter = new Standort();
        cityCenter.setStandortId(1);
        cityCenter.setName("City Center");
        cityCenter.setAdresse("Hauptstraße 1, 1010 Wien");
        cityCenter.getLadestationen().add(new Ladestation(1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));
        cityCenter.getLadestationen().add(new Ladestation(2, Lademodus.DC, Betriebszustand.IN_BETRIEB_BESETZT));

        standorte.add(cityCenter);

        System.out.println("-- Scenario: Customer opens details for an unknown location --");
        String requested = "Unbekannter Standort";

        Standort found = standorte.stream()
                .filter(s -> requested.equals(s.getName()))
                .findFirst()
                .orElse(null);

        if (found == null) {
            System.out.println("ERROR: Standort " + requested + " wurde nicht gefunden");
        } else {
            System.out.println("Geöffneter Standort: " + found.getName());
        }

        System.out.println("=== End Demo: View charging locations (Edge Case) ===");
    }

    // =====================================================================
// Demo: View invoices – Edge Case (keine Rechnungen vorhanden)
// =====================================================================
    private static void demoViewInvoicesNoInvoices() {

        System.out.println();
        System.out.println("=== Demo: View invoices (Edge Case – keine Rechnungen vorhanden) ===");

        // ------------------------------------------------------------
        // Setup: eingeloggter Kunde
        // ------------------------------------------------------------
        Kunde kunde = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        kunde.setKundenId("CUST-1023");

        System.out.println("\n-- Kunde angemeldet --");
        System.out.println("Name:       " + kunde.getName());
        System.out.println("E-Mail:     " + kunde.getEmail());
        System.out.println("Kunden-ID:  " + kunde.getKundenId());

        // ------------------------------------------------------------
        // Keine Rechnungen im System vorhanden
        // ------------------------------------------------------------
        List<Rechnung> rechnungen = new ArrayList<>();

        System.out.println("\n-- Systemstatus --");
        System.out.println("Für diesen Kunden sind aktuell keine Rechnungen gespeichert.");

        // ------------------------------------------------------------
        // Kunde öffnet Rechnungsübersicht
        // ------------------------------------------------------------
        System.out.println("\n-- Aktion --");
        System.out.println("Kunde öffnet die Rechnungsübersicht …");

        if (rechnungen.isEmpty()) {
            System.out.println("\n-- Ergebnis --");
            System.out.println("⚠ Keine Rechnungen gefunden für Kunden-ID " + kunde.getKundenId());
            System.out.println("Bitte führen Sie zuerst einen Ladevorgang durch, um Rechnungen zu erzeugen.");
        } else {
            System.out.println("\n-- Rechnungen --");
            for (Rechnung r : rechnungen) {
                System.out.println("Rechnung " + r.getRechnungsNr()
                        + " | Datum: " + r.getRechnungsDatum()
                        + " | Betrag: " + String.format("%.2f EUR", r.getGesamtBetrag()));
            }
        }

        System.out.println("\n=== Ende Demo: View invoices (Edge Case) ===");
    }


}




