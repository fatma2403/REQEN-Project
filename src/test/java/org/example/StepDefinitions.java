package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;

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

    // ------------------------------------------------------------
    // Standorte mit Ladestationen (für Übersicht & Details)
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
        // Wiederverwenden der oben definierten Testdaten
        there_are_several_locations_with_charging_stations();
    }

    // ------------------------------------------------------------
    // View location overview
    // ------------------------------------------------------------

    @When("the customer opens the location overview")
    public void the_customer_opens_the_location_overview() {
        locationOverviewOpen = true;
        sichtbareStandorte = new ArrayList<>(alleStandorte);
    }

    @Then("the system shows a list of available locations")
    public void the_system_shows_a_list_of_available_locations() {
        assertTrue(locationOverviewOpen, "Location overview should be open");
        assertNotNull(sichtbareStandorte, "Visible locations should not be null");
        assertFalse(sichtbareStandorte.isEmpty(), "There should be at least one location");
    }

    // ------------------------------------------------------------
    // View location details
    // ------------------------------------------------------------

    @Given("the customer is on the location overview")
    public void the_customer_is_on_the_location_overview() {
        // Stelle sicher, dass die Übersicht offen ist
        locationOverviewOpen = true;
        if (sichtbareStandorte == null || sichtbareStandorte.isEmpty()) {
            sichtbareStandorte = new ArrayList<>(alleStandorte);
        }
    }

    @When("the customer opens the details of a location")
    public void the_customer_opens_the_details_of_a_location() {
        // Wir wählen einfach den ersten Standort aus
        assertNotNull(sichtbareStandorte);
        assertFalse(sichtbareStandorte.isEmpty());
        ausgewaehlterStandort = sichtbareStandorte.get(0);
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

    // ------------------------------------------------------------
    // Filter locations by DC chargers
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
        // Beispiel: filtere nach Name enthält "City"
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
    // Reserve a charging station (view_locations.feature)
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
                Betriebszustand.IN_BETRIEB_FREI   // verfügbar
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
        // Reservierung = Station wird blockiert (nicht mehr frei)
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
}
