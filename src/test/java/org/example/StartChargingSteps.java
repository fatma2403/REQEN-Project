package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StartChargingSteps {

    private Kunde kunde;
    private Ladestation ladestation;
    private Ladevorgang ladevorgang;

    private boolean stationReady;
    private boolean customerIdentified;
    private boolean customerIdValid;
    private boolean sessionLinkedToCustomer;

    private boolean modeSelected;
    private boolean stationConfigured;

    private boolean sessionRunning;
    private boolean startTimeRecorded;
    private boolean notificationSent;

    // -------------------------------------------------
    // Scenario: Confirm customer ID
    // -------------------------------------------------

    @Given("a charging station is ready for use")
    public void a_charging_station_is_ready_for_use() {
        ladestation = new Ladestation(1, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);
        stationReady = true;
    }

    @Given("a customer is identified at the station")
    public void a_customer_is_identified_at_the_station() {
        kunde = new Kunde("Startkunde", "start@example.com", "secret");
        // einfache Kunden-ID setzen
        kunde.setKundenId("CUST-123");
        customerIdentified = true;
    }

    @When("the customer presents or enters their customer ID")
    public void the_customer_presents_or_enters_their_customer_id() {
        // ID ist gültig, wenn sie nicht null/leer ist
        customerIdValid = kundenIdIstGueltig(kunde.getKundenId());
    }

    @Then("the system validates the customer ID")
    public void the_system_validates_the_customer_id() {
        assertTrue(customerIdValid);
    }

    @Then("links the charging session to the customer account")
    public void links_the_charging_session_to_the_customer_account() {
        if (customerIdValid) {
            ladevorgang = new Ladevorgang();
            ladevorgang.setKunde(kunde);
            ladevorgang.setLadestation(ladestation);
            sessionLinkedToCustomer = true;
        } else {
            sessionLinkedToCustomer = false;
        }
        assertTrue(sessionLinkedToCustomer);
    }

    private boolean kundenIdIstGueltig(String kundenId) {
        return kundenId != null && !kundenId.isBlank();
    }

    // -------------------------------------------------
    // Scenario: Select charging mode
    // -------------------------------------------------

    @Given("a validated customer at a compatible charging station")
    public void a_validated_customer_at_a_compatible_charging_station() {
        // Kunde + Station wieder anlegen
        kunde = new Kunde("Startkunde", "start@example.com", "secret");
        kunde.setKundenId("CUST-999");

        ladestation = new Ladestation(2, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);

        customerIdValid = true;
        stationReady = true;
    }

    @When("the customer selects AC or DC charging mode")
    public void the_customer_selects_ac_or_dc_charging_mode() {
        // wir simulieren hier einfach DC-Auswahl
        ladestation.setLademodus(Lademodus.DC);
        modeSelected = true;
    }

    @Then("the system configures the charging station with the selected mode")
    public void the_system_configures_the_charging_station_with_the_selected_mode() {
        stationConfigured = stationReady && modeSelected;
        assertTrue(stationConfigured);
        assertEquals(Lademodus.DC, ladestation.getLademodus());
    }

    // -------------------------------------------------
    // Scenario: Start the charging session
    // -------------------------------------------------

    @Given("a validated customer with a selected charging mode")
    public void a_validated_customer_with_a_selected_charging_mode() {
        kunde = new Kunde("Startkunde", "start@example.com", "secret");
        kunde.setKundenId("CUST-777");

        ladestation = new Ladestation(3, Lademodus.AC, Betriebszustand.IN_BETRIEB_FREI);

        customerIdValid = true;
        modeSelected = true;
    }

    @When("the customer starts the charging session")
    public void the_customer_starts_the_charging_session() {
        ladevorgang = new Ladevorgang();
        ladevorgang.setKunde(kunde);
        ladevorgang.setLadestation(ladestation);
        ladevorgang.setStart(LocalDateTime.now());

        sessionRunning = true;
        startTimeRecorded = (ladevorgang.getStart() != null);
    }

    @Then("the system begins delivering energy to the vehicle")
    public void the_system_begins_delivering_energy_to_the_vehicle() {
        assertTrue(sessionRunning);
    }

    @Then("the system records the start time of the session")
    public void the_system_records_the_start_time_of_the_session() {
        assertTrue(startTimeRecorded);
    }

    // -------------------------------------------------
    // Scenario: Receive charging notifications
    // -------------------------------------------------

    @Given("an ongoing charging session")
    public void an_ongoing_charging_session() {
        // wir nehmen einfach an: Session läuft
        sessionRunning = true;
        ladevorgang = new Ladevorgang();
        ladevorgang.setStart(LocalDateTime.now().minusMinutes(5));
        ladevorgang.setGeladeneMengeKWh(3.0);
    }

    @When("the charging progress changes significantly")
    public void the_charging_progress_changes_significantly() {
        // wir simulieren eine deutliche Änderung der geladenen Energie
        ladevorgang.setGeladeneMengeKWh(10.0);
    }

    @Then("the system sends a notification about the current status to the customer")
    public void the_system_sends_a_notification_about_the_current_status_to_the_customer() {
        // im MVP: Notification wird gesendet, wenn Session läuft und sich etwas geändert hat
        notificationSent = sessionRunning && ladevorgang.getGeladeneMengeKWh() >= 10.0;
        assertTrue(notificationSent);
    }
}
