package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocationManagementSteps {

    private Standort standort;
    private Ladestation ladestation;

    // Für mehrere Standorte / Entfernen / Zuweisen
    private List<Standort> aktiveStandorte = new ArrayList<>();
    private Ladestation unassignedStation;

    // ------------------------------------------------------------
    // Scenario: Change operating status of a charging station
    // ------------------------------------------------------------

    @Given("a location with id {string}, name {string} and address {string} exists with a charging station with id {string}, mode {string} and status {string}")
    public void a_location_with_id_name_and_address_exists_with_a_charging_station_with_id_mode_and_status(
            String standortIdStr,
            String name,
            String adresse,
            String stationIdStr,
            String modeStr,
            String statusStr
    ) {
        int standortId = Integer.parseInt(standortIdStr);
        int stationId = Integer.parseInt(stationIdStr);

        standort = new Standort();
        standort.setStandortId(standortId);
        standort.setName(name);
        standort.setAdresse(adresse);

        Lademodus modus = Lademodus.valueOf(modeStr);
        Betriebszustand status = Betriebszustand.valueOf(statusStr);

        ladestation = new Ladestation(stationId, modus, status);
        standort.getLadestationen().add(ladestation);

        // auch in aktive Standorte aufnehmen
        aktiveStandorte.add(standort);

        // sanity checks
        assertEquals(standortId, standort.getStandortId());
        assertEquals(name, standort.getName());
        assertEquals(adresse, standort.getAdresse());
        assertEquals(stationId, ladestation.getLadestationId());
        assertEquals(modus, ladestation.getLademodus());
        assertEquals(status, ladestation.getBetriebszustand());
    }

    @When("the operator sets the charging station with id {string} to status {string}")
    public void the_operator_sets_the_charging_station_with_id_to_status(
            String stationIdStr,
            String newStatusStr
    ) {
        assertNotNull(ladestation, "Charging station must exist");

        int expectedId = Integer.parseInt(stationIdStr);
        assertEquals(expectedId, ladestation.getLadestationId(),
                "Wrong charging station id in scenario");

        Betriebszustand newStatus = Betriebszustand.valueOf(newStatusStr);
        ladestation.setBetriebszustand(newStatus);
    }

    @Then("the system marks the charging station with id {string} as {string}")
    public void the_system_marks_the_charging_station_with_id_as(
            String stationIdStr,
            String expectedStatusStr
    ) {
        assertNotNull(ladestation, "Charging station must exist");

        int expectedId = Integer.parseInt(stationIdStr);
        assertEquals(expectedId, ladestation.getLadestationId(),
                "Wrong charging station id");

        Betriebszustand expectedStatus = Betriebszustand.valueOf(expectedStatusStr);
        assertEquals(expectedStatus, ladestation.getBetriebszustand(),
                "Station must have the expected status");
    }

    @Then("charging stations with status {string} are not available for new charging sessions")
    public void charging_stations_with_status_are_not_available_for_new_charging_sessions(String statusStr) {
        assertNotNull(ladestation, "Charging station must exist");

        Betriebszustand status = Betriebszustand.valueOf(statusStr);
        assertEquals(status, ladestation.getBetriebszustand(),
                "Station must have the given status from the step");

        // ganz einfache Geschäftslogik:
        // nur IN_BETRIEB_FREI ist verfügbar
        boolean verfuegbar = ladestation.getBetriebszustand() == Betriebszustand.IN_BETRIEB_FREI;
        assertFalse(verfuegbar,
                "Stations with status " + status + " must not be available for new charging sessions");
    }

    // ------------------------------------------------------------
    // Scenario: Edit location data
    // ------------------------------------------------------------

    @Given("a location with id {string}, name {string} and address {string} exists")
    public void a_location_with_id_name_and_address_exists(
            String standortIdStr,
            String name,
            String adresse
    ) {
        int standortId = Integer.parseInt(standortIdStr);

        standort = new Standort();
        standort.setStandortId(standortId);
        standort.setName(name);
        standort.setAdresse(adresse);

        // wir tun so, als wäre dieser Standort in der Systemliste
        aktiveStandorte.add(standort);

        assertEquals(standortId, standort.getStandortId());
        assertEquals(name, standort.getName());
        assertEquals(adresse, standort.getAdresse());
    }

    @When("the operator changes the location name to {string} and the address to {string} and saves the changes")
    public void the_operator_changes_the_location_name_to_and_the_address_to_and_saves_the_changes(
            String newName,
            String newAdresse
    ) {
        assertNotNull(standort, "Location must exist before editing");

        standort.setName(newName);
        standort.setAdresse(newAdresse);
        // Speichern wird hier einfach durch das Setzen simuliert.
    }

    @Then("the system stores the updated location data for location id {string} with name {string} and address {string}")
    public void the_system_stores_the_updated_location_data_for_location_id_with_name_and_address(
            String expectedIdStr,
            String expectedName,
            String expectedAdresse
    ) {
        assertNotNull(standort, "Location must exist for assertion");

        int expectedId = Integer.parseInt(expectedIdStr);
        assertEquals(expectedId, standort.getStandortId(), "Location id mismatch");
        assertEquals(expectedName, standort.getName(), "Updated name not stored");
        assertEquals(expectedAdresse, standort.getAdresse(), "Updated address not stored");
    }

    // ------------------------------------------------------------
    // Scenario: Remove a location
    // ------------------------------------------------------------

    @Given("a location with id {string}, name {string} and address {string} exists with charging stations")
    public void a_location_with_id_name_and_address_exists_with_charging_stations(
            String standortIdStr,
            String name,
            String adresse
    ) {
        int standortId = Integer.parseInt(standortIdStr);

        standort = new Standort();
        standort.setStandortId(standortId);
        standort.setName(name);
        standort.setAdresse(adresse);

        // Eine Dummy-Ladestation, um "mit charging stations" zu erfüllen
        Ladestation dummy = new Ladestation(99, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);
        standort.getLadestationen().add(dummy);

        aktiveStandorte.add(standort);

        assertFalse(standort.getLadestationen().isEmpty(), "Location should have at least one charger");
    }

    @When("the operator removes the location with id {string}")
    public void the_operator_removes_the_location_with_id(String standortIdStr) {
        int idToRemove = Integer.parseInt(standortIdStr);
        aktiveStandorte.removeIf(s -> s.getStandortId() == idToRemove);
    }

    @Then("the system no longer shows the location with id {string} in the list of active locations")
    public void the_system_no_longer_shows_the_location_with_id_in_the_list_of_active_locations(String standortIdStr) {
        int id = Integer.parseInt(standortIdStr);

        boolean exists = aktiveStandorte.stream()
                .anyMatch(s -> s.getStandortId() == id);

        assertFalse(exists,
                "Location with id " + id + " should not be in active locations anymore");
    }

    // ------------------------------------------------------------
    // Scenario: Assign a charging station to a location
    // ------------------------------------------------------------

    @Given("an unassigned charging station with id {string}, mode {string} and status {string} exists")
    public void an_unassigned_charging_station_with_id_mode_and_status_exists(
            String stationIdStr,
            String modeStr,
            String statusStr
    ) {
        int stationId = Integer.parseInt(stationIdStr);
        Lademodus modus = Lademodus.valueOf(modeStr);
        Betriebszustand status = Betriebszustand.valueOf(statusStr);

        unassignedStation = new Ladestation(stationId, modus, status);

        assertEquals(stationId, unassignedStation.getLadestationId());
        assertEquals(modus, unassignedStation.getLademodus());
        assertEquals(status, unassignedStation.getBetriebszustand());
    }

    @Given("a location with id {string}, name {string} and address {string} exists for assignment")
    public void a_location_with_id_name_and_address_exists_for_assignment(
            String standortIdStr,
            String name,
            String adresse
    ) {
        int standortId = Integer.parseInt(standortIdStr);

        Standort loc = new Standort();
        loc.setStandortId(standortId);
        loc.setName(name);
        loc.setAdresse(adresse);

        aktiveStandorte.add(loc);
    }

    @When("the operator assigns the charging station with id {string} to the location with id {string}")
    public void the_operator_assigns_the_charging_station_with_id_to_the_location_with_id(
            String stationIdStr,
            String standortIdStr
    ) {
        assertNotNull(unassignedStation, "Unassigned station must exist");

        int expectedStationId = Integer.parseInt(stationIdStr);
        int targetStandortId = Integer.parseInt(standortIdStr);

        assertEquals(expectedStationId, unassignedStation.getLadestationId(),
                "Unexpected station id for assignment");

        Standort ziel = aktiveStandorte.stream()
                .filter(s -> s.getStandortId() == targetStandortId)
                .findFirst()
                .orElse(null);

        assertNotNull(ziel, "Target location must exist");

        ziel.getLadestationen().add(unassignedStation);

        // auch als "aktuelle" Referenz setzen
        standort = ziel;
        ladestation = unassignedStation;
    }

    @Then("the system links the charging station with id {string} to the location with id {string}")
    public void the_system_links_the_charging_station_with_id_to_the_location_with_id(
            String stationIdStr,
            String standortIdStr
    ) {
        int expectedStationId = Integer.parseInt(stationIdStr);
        int expectedStandortId = Integer.parseInt(standortIdStr);

        Standort ziel = aktiveStandorte.stream()
                .filter(s -> s.getStandortId() == expectedStandortId)
                .findFirst()
                .orElse(null);

        assertNotNull(ziel, "Target location must exist for verification");

        Ladestation found = ziel.getLadestationen().stream()
                .filter(ls -> ls.getLadestationId() == expectedStationId)
                .findFirst()
                .orElse(null);

        assertNotNull(found,
                "Charging station with id " + expectedStationId + " must be linked to location " + expectedStandortId);
    }
}
