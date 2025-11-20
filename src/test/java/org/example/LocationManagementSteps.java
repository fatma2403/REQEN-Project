package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocationManagementSteps {

    private final Standortverwaltung standortverwaltung = new Standortverwaltung();

    private Standort standort;
    private Ladestation ladestation;
    private List<Standort> aktiveStandorte;

    // --------------------------------------------
    // Scenario: Change operating status of a charging station
    // --------------------------------------------

    @Given("a location with at least one charging station exists")
    public void a_location_with_at_least_one_charging_station_exists() {
        standort = standortverwaltung.standortAnlegen("Standort A", "Beispielstraße 1");
        ladestation = new Ladestation(1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);
        standortverwaltung.ladestationZuStandortZuweisen(standort, ladestation);
    }

    @When("the operator sets a charging station to inactive")
    public void the_operator_sets_a_charging_station_to_inactive() {
        ladestation.setBetriebszustand(Betriebszustand.AUSSER_BETRIEB);
    }

    @Then("the system marks this station as inactive")
    public void the_system_marks_this_station_as_inactive() {
        assertEquals(Betriebszustand.AUSSER_BETRIEB, ladestation.getBetriebszustand());
    }

    @Then("inactive stations are not available for new charging sessions")
    public void inactive_stations_are_not_available_for_new_charging_sessions() {
        assertNotEquals(Betriebszustand.IN_BETRIEB_FREI, ladestation.getBetriebszustand());
    }

    // --------------------------------------------
    // Scenario: Edit location data
    // --------------------------------------------

    private String alterName;
    private String alteAdresse;

    @Given("a location exists with stored address and name")
    public void a_location_exists_with_stored_address_and_name() {
        standort = standortverwaltung.standortAnlegen("Alter Standort", "Alte Straße 5");
        alterName = standort.getName();
        alteAdresse = standort.getAdresse();
    }

    @When("the operator changes the location data and saves the changes")
    public void the_operator_changes_the_location_data_and_saves_the_changes() {
        standort.setName("Neuer Standort");
        standort.setAdresse("Neue Straße 10");
        // explizit speichern brauchen wir nicht, Objekt liegt in der Liste
    }

    @Then("the system stores the updated location data")
    public void the_system_stores_the_updated_location_data() {
        assertNotEquals(alterName, standort.getName());
        assertNotEquals(alteAdresse, standort.getAdresse());
        assertEquals("Neuer Standort", standort.getName());
        assertEquals("Neue Straße 10", standort.getAdresse());
    }

    // --------------------------------------------
    // Scenario: Remove a location
    // --------------------------------------------

    @Given("a location exists with charging stations")
    public void a_location_exists_with_charging_stations() {
        standort = standortverwaltung.standortAnlegen("Zu löschender Standort", "Löschweg 3");
        ladestation = new Ladestation(2, Lademodus.DC, Betriebszustand.IN_BETRIEB_FREI);
        standortverwaltung.ladestationZuStandortZuweisen(standort, ladestation);
    }

    @When("the operator removes this location")
    public void the_operator_removes_this_location() {
        standortverwaltung.standortEntfernen(standort);
    }

    @Then("the system no longer shows this location in the list of active locations")
    public void the_system_no_longer_shows_this_location_in_the_list_of_active_locations() {
        aktiveStandorte = standortverwaltung.standortUebersichtAnzeigen();
        assertFalse(aktiveStandorte.contains(standort));
    }

    // --------------------------------------------
    // Scenario: Assign a charging station to a location
    // --------------------------------------------

    private Ladestation unassignedStation;

    @Given("an unassigned charging station exists")
    public void an_unassigned_charging_station_exists() {
        unassignedStation = new Ladestation(3, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);
    }

    @Given("a location exists")
    public void a_location_exists() {
        standort = standortverwaltung.standortAnlegen("Zielstandort", "Zielstraße 7");
    }

    @When("the operator assigns the charging station to the location")
    public void the_operator_assigns_the_charging_station_to_the_location() {
        standortverwaltung.ladestationZuStandortZuweisen(standort, unassignedStation);
    }

    @Then("the system links the charging station to that location")
    public void the_system_links_the_charging_station_to_that_location() {
        assertTrue(standort.getLadestationen().contains(unassignedStation));
    }
}
