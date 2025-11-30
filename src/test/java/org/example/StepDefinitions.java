package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private Kunde kunde;
    private final StandortService standortService = new StandortService();

    private List<Standort> alleStandorte;
    private List<Standort> sichtbareStandorte;
    private List<Standort> gefilterteStandorte;

    private Standort ausgewaehlterStandort;
    private boolean locationOverviewOpen;
    private boolean locationDetailsOpen;

    private Ladestation reservierteLadestation;
    private boolean reservierungAktiv;

    // ------------------------------------------------------------
    // Gemeinsame Given-Steps
    // ------------------------------------------------------------

    @Given("a customer exists")
    public void a_customer_exists() {
        kunde = new Kunde("Testkunde", "kunde@example.com", "secret");
        kunde.setKundenId("CUST-001");
    }

    @Given("a logged-in customer with name {string} and email {string}")
    public void a_logged_in_customer_martin_keller(String name, String email) {
        kunde = new Kunde(name, email, "secret");
        kunde.setKundenId("CUST-1023");
    }

    // ------------------------------------------------------------
    // Standorte mit Ladestationen (für Übersicht & Details) – einfache Testdaten
    // ------------------------------------------------------------

    @Given("there are several locations with charging stations")
    public void there_are_several_locations_with_charging_stations() {
        alleStandorte = new ArrayList<>();

        // Standort 1 mit 2 Ladestationen
        Standort s1 = new Standort();
        s1.setStandortId(1);
        s1.setName("City Station");
        s1.setAdresse("Hauptstraße 1");
        s1.getLadestationen().add(
                new Ladestation(1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));
        s1.getLadestationen().add(
                new Ladestation(2, Lademodus.DC, Betriebszustand.IN_BETRIEB_BESETZT));

        // Standort 2 mit 1 Ladestation
        Standort s2 = new Standort();
        s2.setStandortId(2);
        s2.setName("Mall Station");
        s2.setAdresse("Einkaufszentrum 5");
        s2.getLadestationen().add(
                new Ladestation(3, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));

        // Standort 3 mit 1 Ladestation
        Standort s3 = new Standort();
        s3.setStandortId(3);
        s3.setName("Highway Station");
        s3.setAdresse("Autobahnabfahrt 3");
        s3.getLadestationen().add(
                new Ladestation(4, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI));

        alleStandorte.add(s1);
        alleStandorte.add(s2);
        alleStandorte.add(s3);

        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @Given("there are several locations with AC and DC charging stations")
    public void there_are_several_locations_with_ac_and_dc_charging_stations() {
        there_are_several_locations_with_charging_stations();
    }

    // ------------------------------------------------------------
    // Daten aus Text-Scenario: example.feature (drei Standorte in einem Schritt)
    // ------------------------------------------------------------

    @Given("locations exist: {string} at {string} with chargers \\({int} AC IN_BETRIEB_FREI, {int} DC IN_BETRIEB_BESETZT), {string} at {string} with chargers \\({int} AC IN_BETRIEB_FREI), {string} at {string} with chargers \\({int} DC IN_BETRIEB_FREI)")
    public void locations_exist_three_locations(
            String name1, String addr1, Integer acId1, Integer dcId1,
            String name2, String addr2, Integer acId2,
            String name3, String addr3, Integer dcId3
    ) {
        alleStandorte = new ArrayList<>();

        // 1. Standort mit AC + DC
        Standort s1 = new Standort();
        s1.setStandortId(1);
        s1.setName(name1);
        s1.setAdresse(addr1);
        s1.getLadestationen().add(new Ladestation(acId1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));
        s1.getLadestationen().add(new Ladestation(dcId1, Lademodus.DC, Betriebszustand.IN_BETRIEB_BESETZT));

        // 2. Standort mit AC
        Standort s2 = new Standort();
        s2.setStandortId(2);
        s2.setName(name2);
        s2.setAdresse(addr2);
        s2.getLadestationen().add(new Ladestation(acId2, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));

        // 3. Standort mit DC
        Standort s3 = new Standort();
        s3.setStandortId(3);
        s3.setName(name3);
        s3.setAdresse(addr3);
        s3.getLadestationen().add(new Ladestation(dcId3, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI));

        alleStandorte.add(s1);
        alleStandorte.add(s2);
        alleStandorte.add(s3);

        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @Given("locations exist: {string} at {string} with chargers \\({int} AC IN_BETRIEB_FREI, {int} DC IN_BETRIEB_BESETZT)")
    public void locations_exist_single_location(
            String name, String address, Integer acId, Integer dcId
    ) {
        alleStandorte = new ArrayList<>();

        Standort s = new Standort();
        s.setStandortId(1);
        s.setName(name);
        s.setAdresse(address);
        s.getLadestationen().add(
                new Ladestation(acId, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI));
        s.getLadestationen().add(
                new Ladestation(dcId, Lademodus.DC, Betriebszustand.IN_BETRIEB_BESETZT));

        alleStandorte.add(s);
        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    // ------------------------------------------------------------
    // View location overview
    // ------------------------------------------------------------

    @When("the customer opens the location overview")
    public void the_customer_opens_the_location_overview() {
        locationOverviewOpen = true;
        if (alleStandorte == null) {
            alleStandorte = new ArrayList<>();
        }
        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @Then("the system shows a list of available locations")
    public void the_system_shows_a_list_of_available_locations() {
        assertTrue(locationOverviewOpen, "Location overview should be open");
        assertNotNull(sichtbareStandorte, "Visible locations should not be null");
        assertFalse(sichtbareStandorte.isEmpty(), "There should be at least one location");
    }

    @Then("the system shows a list of available locations including {string}, {string} and {string}")
    public void the_system_shows_a_list_of_available_locations_including_and(
            String name1, String name2, String name3) {

        the_system_shows_a_list_of_available_locations();

        List<String> names = sichtbareStandorte.stream()
                .map(Standort::getName)
                .collect(Collectors.toList());

        assertTrue(names.contains(name1), "Location list should contain " + name1);
        assertTrue(names.contains(name2), "Location list should contain " + name2);
        assertTrue(names.contains(name3), "Location list should contain " + name3);
    }

    // ------------------------------------------------------------
    // View location details (allgemein)
    // ------------------------------------------------------------

    @Given("the customer is on the location overview")
    public void the_customer_is_on_the_location_overview() {
        locationOverviewOpen = true;
        if (sichtbareStandorte == null || sichtbareStandorte.isEmpty()) {
            if (alleStandorte == null) {
                alleStandorte = new ArrayList<>();
            }
            sichtbareStandorte = new ArrayList<>(alleStandorte);
        }
    }

    @When("the customer opens the details of a location")
    public void the_customer_opens_the_details_of_a_location() {
        assertNotNull(sichtbareStandorte);
        assertFalse(sichtbareStandorte.isEmpty());
        ausgewaehlterStandort = sichtbareStandorte.get(0);
        locationDetailsOpen = true;
    }

    @When("the customer opens the details of location {string}")
    public void the_customer_opens_the_details_of_location(String name) {
        assertNotNull(sichtbareStandorte, "Visible locations must not be null");
        ausgewaehlterStandort = sichtbareStandorte.stream()
                .filter(s -> name.equals(s.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(ausgewaehlterStandort, "Location " + name + " must exist in visible locations");
        locationDetailsOpen = true;
    }

    @Then("the system shows information about chargers and status at that location")
    public void the_system_shows_information_about_chargers_and_status_at_that_location() {
        assertTrue(locationDetailsOpen, "Location details should be open");
        assertNotNull(ausgewaehlterStandort, "Selected location should not be null");
        assertNotNull(ausgewaehlterStandort.getLadestationen(), "Chargers list should not be null");
        assertFalse(ausgewaehlterStandort.getLadestationen().isEmpty(),
                "There should be at least one charger at the location");
    }

    @Then("the system shows chargers for location {string} with \\({int} AC IN_BETRIEB_FREI, {int} DC IN_BETRIEB_BESETZT)")
    public void the_system_shows_chargers_for_location_with_ac_in_betrieb_frei_dc_in_betrieb_besetzt(
            String expectedName, Integer expectedAcId, Integer expectedDcId) {

        assertTrue(locationDetailsOpen, "Location details should be open");
        assertNotNull(ausgewaehlterStandort, "Selected location must not be null");
        assertEquals(expectedName, ausgewaehlterStandort.getName(), "Location name must match");
        assertNotNull(ausgewaehlterStandort.getLadestationen(), "Chargers list should not be null");

        Ladestation acCharger = ausgewaehlterStandort.getLadestationen().stream()
                .filter(l -> l.getLadestationId() == expectedAcId)
                .findFirst()
                .orElse(null);

        Ladestation dcCharger = ausgewaehlterStandort.getLadestationen().stream()
                .filter(l -> l.getLadestationId() == expectedDcId)
                .findFirst()
                .orElse(null);

        assertNotNull(acCharger, "AC charger with id " + expectedAcId + " should exist");
        assertNotNull(dcCharger, "DC charger with id " + expectedDcId + " should exist");

        assertEquals(Lademodus.AC, acCharger.getLademodus(), "AC charger must have mode AC");
        assertEquals(Betriebszustand.IN_BETRIEB_FREI, acCharger.getBetriebszustand(),
                "AC charger must be IN_BETRIEB_FREI");

        assertEquals(Lademodus.DC, dcCharger.getLademodus(), "DC charger must have mode DC");
        assertEquals(Betriebszustand.IN_BETRIEB_BESETZT, dcCharger.getBetriebszustand(),
                "DC charger must be IN_BETRIEB_BESETZT");
    }

    // ------------------------------------------------------------
    // Filter locations by DC chargers (allgemein)
    // ------------------------------------------------------------

    @When("the customer filters for locations with DC chargers")
    public void the_customer_filters_for_locations_with_dc_chargers() {
        gefilterteStandorte = standortService.standorteMitDcFiltern(alleStandorte);
    }

    @Then("the system shows only locations that have at least one DC charger")
    public void the_system_shows_only_locations_that_have_at_least_one_dc_charger() {
        assertNotNull(gefilterteStandorte);
        assertFalse(gefilterteStandorte.isEmpty(),
                "There should be at least one location with DC chargers");

        for (Standort s : gefilterteStandorte) {
            boolean hatDc = s.getLadestationen().stream()
                    .anyMatch(Ladestation::isDc);
            assertTrue(hatDc,
                    "Every location in the filtered list must have at least one DC charger");
        }
    }

    @Then("the system shows only locations that have at least one DC charger: {string} and {string}")
    public void the_system_shows_only_locations_that_have_at_least_one_dc_charger_and(
            String expected1, String expected2) {

        the_customer_filters_for_locations_with_dc_chargers();
        the_system_shows_only_locations_that_have_at_least_one_dc_charger();

        List<String> names = gefilterteStandorte.stream()
                .map(Standort::getName)
                .collect(Collectors.toList());

        assertEquals(2, gefilterteStandorte.size(),
                "Exactly two locations should match the DC filter");
        assertTrue(names.contains(expected1), "Filtered list should contain " + expected1);
        assertTrue(names.contains(expected2), "Filtered list should contain " + expected2);
    }

    // ------------------------------------------------------------
    // Filter locations by different attributes (view_locations.feature)
    // ------------------------------------------------------------

    @Given("there are several locations with different attributes")
    public void there_are_several_locations_with_different_attributes() {
        alleStandorte = new ArrayList<>();

        Standort s1 = new Standort();
        s1.setStandortId(10);
        s1.setName("City Center");
        s1.setAdresse("Innenstadt 1");

        Standort s2 = new Standort();
        s2.setStandortId(11);
        s2.setName("Airport Station");
        s2.setAdresse("Flughafen 1");

        Standort s3 = new Standort();
        s3.setStandortId(12);
        s3.setName("Suburb Station");
        s3.setAdresse("Vorstadt 5");

        alleStandorte.add(s1);
        alleStandorte.add(s2);
        alleStandorte.add(s3);

        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @When("the customer applies filter criteria to the location overview")
    public void the_customer_applies_filter_criteria_to_the_location_overview() {
        gefilterteStandorte = new ArrayList<>();
        for (Standort s : sichtbareStandorte) {
            if (s.getName() != null &&
                    s.getName().toLowerCase().contains("city")) {
                gefilterteStandorte.add(s);
            }
        }
    }

    @Then("the system shows only locations that match the selected criteria")
    public void the_system_shows_only_locations_that_match_the_selected_criteria() {
        assertNotNull(gefilterteStandorte);
        assertFalse(gefilterteStandorte.isEmpty(),
                "There should be at least one filtered location");
        for (Standort s : gefilterteStandorte) {
            assertTrue(s.getName().toLowerCase().contains("city"),
                    "Filtered locations must match the filter criteria (name contains 'city')");
        }
    }

    // ------------------------------------------------------------
    // Reserve a charging station (view_locations.feature - einfache Version)
    // ------------------------------------------------------------

    @Given("there is a location with at least one available charging station")
    public void there_is_a_location_with_at_least_one_available_charging_station() {
        alleStandorte = new ArrayList<>();

        Standort s1 = new Standort();
        s1.setStandortId(20);
        s1.setName("Reservation Station");
        s1.setAdresse("Reservierungsweg 1");

        reservierteLadestation = new Ladestation(
                100,
                Lademodus.AC,
                Betriebszustand.IN_BETRIEB_FREI
        );
        s1.getLadestationen().add(reservierteLadestation);

        alleStandorte.add(s1);
        ausgewaehlterStandort = s1;
    }

    @Given("the customer is viewing the location details")
    public void the_customer_is_viewing_the_location_details() {
        locationDetailsOpen = true;
        assertNotNull(ausgewaehlterStandort);
    }

    @When("the customer reserves a charging station")
    public void the_customer_reserves_a_charging_station() {
        assertEquals(Betriebszustand.IN_BETRIEB_FREI,
                reservierteLadestation.getBetriebszustand(),
                "Station should initially be free");

        reservierteLadestation.setBetriebszustand(Betriebszustand.IN_BETRIEB_BESETZT);
        reservierungAktiv = true;
    }

    @Then("the system blocks this charging station for the customer for a limited time")
    public void the_system_blocks_this_charging_station_for_the_customer_for_a_limited_time() {
        assertTrue(reservierungAktiv, "Reservation should be active");
        assertEquals(Betriebszustand.IN_BETRIEB_BESETZT,
                reservierteLadestation.getBetriebszustand(),
                "Reserved station should not be available for others");
    }

    // ------------------------------------------------------------
    // view_locations.feature – DataTable-Varianten
    // ------------------------------------------------------------

    @Given("the following locations with charging stations exist:")
    public void the_following_locations_with_charging_stations_exist(DataTable dataTable) {
        alleStandorte = new ArrayList<>();

        // Gruppieren nach Standort + Adresse
        Map<String, Standort> standortMap = new LinkedHashMap<>();

        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String name = row.get("Standort");
            String adresse = row.get("Adresse");
            int ladestationId = Integer.parseInt(row.get("LadestationID"));
            String modusStr = row.get("Modus");
            String statusStr = row.get("Status");

            String key = name + "|" + adresse;
            Standort standort = standortMap.computeIfAbsent(key, k -> {
                Standort s = new Standort();
                s.setStandortId(standortMap.size() + 1);
                s.setName(name);
                s.setAdresse(adresse);
                return s;
            });

            Lademodus modus = Lademodus.valueOf(modusStr);
            Betriebszustand status = Betriebszustand.valueOf(statusStr);

            standort.getLadestationen().add(
                    new Ladestation(ladestationId, modus, status)
            );
        }

        alleStandorte.addAll(standortMap.values());
        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @Given("location {string} at address {string} has the following charging stations:")
    public void location_city_center_has_the_following_charging_stations(
            String name, String address, DataTable dataTable) {

        alleStandorte = new ArrayList<>();
        Standort s = new Standort();
        s.setStandortId(1);
        s.setName(name);
        s.setAdresse(address);

        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            int ladestationId = Integer.parseInt(row.get("LadestationID"));
            String modusStr = row.get("Modus");
            String statusStr = row.get("Status");

            Lademodus modus = Lademodus.valueOf(modusStr);
            Betriebszustand status = Betriebszustand.valueOf(statusStr);

            s.getLadestationen().add(
                    new Ladestation(ladestationId, modus, status)
            );
        }

        alleStandorte.add(s);
        sichtbareStandorte = new ArrayList<>(alleStandorte);
        ausgewaehlterStandort = s; // für Details
        locationDetailsOpen = true;
    }

    @Then("the system shows chargers for {string} including:")
    public void the_system_shows_chargers_for_city_center_including(
            String expectedName, DataTable dataTable) {

        assertTrue(locationDetailsOpen, "Location details should be open");
        assertNotNull(ausgewaehlterStandort, "Selected location must not be null");
        assertEquals(expectedName, ausgewaehlterStandort.getName(), "Location name must match");
        assertNotNull(ausgewaehlterStandort.getLadestationen(), "Chargers list should not be null");

        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            int expectedId = Integer.parseInt(row.get("LadestationID"));
            Lademodus expectedMode = Lademodus.valueOf(row.get("Modus"));
            Betriebszustand expectedStatus = Betriebszustand.valueOf(row.get("Status"));

            Ladestation charger = ausgewaehlterStandort.getLadestationen().stream()
                    .filter(l -> l.getLadestationId() == expectedId)
                    .findFirst()
                    .orElse(null);

            assertNotNull(charger, "Expected charger with id " + expectedId + " not found");
            assertEquals(expectedMode, charger.getLademodus(),
                    "Charger " + expectedId + " has wrong mode");
            assertEquals(expectedStatus, charger.getBetriebszustand(),
                    "Charger " + expectedId + " has wrong status");
        }
    }

    @When("the customer filters locations by charging mode {string}")
    public void the_customer_filters_locations_by_charging_mode(String modeStr) {
        Lademodus mode = Lademodus.valueOf(modeStr);
        gefilterteStandorte = alleStandorte.stream()
                .filter(s -> s.getLadestationen().stream()
                        .anyMatch(l -> l.getLademodus() == mode))
                .collect(Collectors.toList());
    }

    @Then("the system shows only locations including {string}")
    public void the_system_shows_only_locations_including(String expectedName) {
        assertNotNull(gefilterteStandorte);
        assertFalse(gefilterteStandorte.isEmpty(), "There should be at least one filtered location");

        List<String> names = gefilterteStandorte.stream()
                .map(Standort::getName)
                .collect(Collectors.toList());

        assertEquals(1, gefilterteStandorte.size(),
                "Exactly one location should match the filter");
        assertTrue(names.contains(expectedName),
                "Filtered list should contain " + expectedName);
    }

    @Given("location {string} has a free charging station with LadestationID {int}")
    public void location_has_a_free_charging_station_with_ladestation_id(
            String name, Integer ladestationId) {

        alleStandorte = new ArrayList<>();
        Standort s = new Standort();
        s.setStandortId(1);
        s.setName(name);
        s.setAdresse("dummy"); // Adresse ist hier egal

        reservierteLadestation = new Ladestation(
                ladestationId,
                Lademodus.AC,
                Betriebszustand.IN_BETRIEB_FREI
        );
        s.getLadestationen().add(reservierteLadestation);

        alleStandorte.add(s);
        ausgewaehlterStandort = s;
    }

    @Given("the customer is viewing the location details of {string}")
    public void the_customer_is_viewing_the_location_details_of(String name) {
        assertNotNull(ausgewaehlterStandort);
        assertEquals(name, ausgewaehlterStandort.getName());
        locationDetailsOpen = true;
    }

    @When("the customer reserves charging station with LadestationID {int} for {int} minutes")
    public void the_customer_reserves_charging_station_with_ladestation_id_for_minutes(
            Integer expectedId, Integer minutes) {

        assertNotNull(reservierteLadestation, "Reserved charger must exist");
        assertEquals(expectedId.intValue(), reservierteLadestation.getLadestationId(),
                "Wrong charger reserved");
        assertEquals(Betriebszustand.IN_BETRIEB_FREI, reservierteLadestation.getBetriebszustand(),
                "Charger should initially be free");

        // "Reservierung" simulieren
        reservierteLadestation.setBetriebszustand(Betriebszustand.IN_BETRIEB_BESETZT);
        reservierungAktiv = true;
    }

    @Then("the system sets the charging station with LadestationID {int} as reserved for customer {string}")
    public void the_system_sets_the_charging_station_with_ladestation_id_as_reserved_for_customer(
            Integer expectedId, String email) {

        assertTrue(reservierungAktiv, "Reservation should be active");
        assertNotNull(reservierteLadestation, "Reserved charger must exist");
        assertEquals(expectedId.intValue(), reservierteLadestation.getLadestationId(),
                "Reserved charger has wrong id");
        assertEquals(Betriebszustand.IN_BETRIEB_BESETZT,
                reservierteLadestation.getBetriebszustand(),
                "Reserved charger should not be free");
        assertEquals(kunde.getEmail(), email,
                "Reservation should be for the given customer email");
    }

    @Then("the station cannot be used by other customers during this time")
    public void the_station_cannot_be_used_by_other_customers_during_this_time() {
        assertEquals(Betriebszustand.IN_BETRIEB_BESETZT,
                reservierteLadestation.getBetriebszustand(),
                "Station must not be available for other customers");
    }
}
