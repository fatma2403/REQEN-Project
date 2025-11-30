package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class StartChargingSteps {


    static class Customer {
        String name;
        String email;
        String customerId;
        boolean identified;
        boolean validated;
    }

    static class ChargingStation {
        String id;
        String location;
        String mode;           // aktueller Modus
        String status;         // IN_BETRIEB_FREI usw.
        boolean configured;
        String configuredMode;
        boolean deliveringEnergy;
    }

    static class ChargingSession {
        String sessionId;
        Customer customer;
        ChargingStation station;
        String startTime;
        String endTime;
        double energyKWh;
    }



    private Customer currentCustomer;
    private ChargingStation currentStation;
    private ChargingSession currentSession;

    private String selectedChargingMode;

    private double recordedEnergyKWh;
    private String lastNotificationSessionId;
    private double lastNotificationEnergyKWh;

 
    @Given("a charging station with id {string} at location {string} in mode {string} and status {string} is ready for use")
    public void a_charging_station_with_id_at_location_in_mode_and_status_is_ready_for_use(
            String stationId,
            String location,
            String mode,
            String status
    ) {
        currentStation = new ChargingStation();
        currentStation.id = stationId;
        currentStation.location = location;
        currentStation.mode = mode;
        currentStation.status = status;
        currentStation.configured = false;
        currentStation.deliveringEnergy = false;

        // Sanity-Checks gegen das Feature
        assertEquals("11", stationId);
        assertEquals("City Center", location);
        assertEquals("DC", mode);
        assertEquals("IN_BETRIEB_FREI", status);
    }

    @Given("a customer with name {string}, email {string} and customer ID {string} is identified at the station")
    public void a_customer_with_name_email_and_customer_id_is_identified_at_the_station(
            String name,
            String email,
            String customerId
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = name;
        currentCustomer.email = email;
        currentCustomer.customerId = customerId;
        currentCustomer.identified = true;
        currentCustomer.validated = false;

        assertEquals("Martin Keller", name);
        assertEquals("martin.keller@testmail.com", email);
        assertEquals("CUST-1023", customerId);
    }

    @When("the customer enters customer ID {string} at the charging station")
    public void the_customer_enters_customer_id_at_the_charging_station(String enteredId) {
        assertNotNull(currentCustomer, "Customer should be identified");
        assertTrue(currentCustomer.identified, "Customer should be identified");

        // Im echten System würde man hier prüfen, ob die ID existiert
        assertEquals(currentCustomer.customerId, enteredId);
        currentCustomer.validated = true;
    }

    @Then("the system validates that customer ID {string} exists")
    public void the_system_validates_that_customer_id_exists(String expectedId) {
        assertNotNull(currentCustomer, "Customer should exist");
        assertTrue(currentCustomer.validated, "Customer should be validated");
        assertEquals(expectedId, currentCustomer.customerId);
    }

    @Then("the system links the new charging session to the customer account with customer ID {string}")
    public void the_system_links_the_new_charging_session_to_the_customer_account_with_customer_id(String expectedCustomerId) {
        assertNotNull(currentCustomer, "Customer should exist");
        assertTrue(currentCustomer.validated, "Customer should be validated");
        assertEquals(expectedCustomerId, currentCustomer.customerId);

        currentSession = new ChargingSession();
        currentSession.sessionId = "NEW_SESSION";
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
    }


    @Given("a validated customer {string} with customer ID {string} is at charging station with id {string} that supports charging modes {string} and {string}")
    public void a_validated_customer_with_customer_id_is_at_charging_station_with_id_that_supports_charging_modes_and(
            String name,
            String customerId,
            String stationId,
            String mode1,
            String mode2
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = name;
        currentCustomer.customerId = customerId;
        currentCustomer.validated = true;

        currentStation = new ChargingStation();
        currentStation.id = stationId;
        currentStation.location = "City Center";
        currentStation.status = "IN_BETRIEB_FREI";
        // Initialer Modus egal, wird gleich konfiguriert
        currentStation.mode = mode1;
        currentStation.configured = false;
        currentStation.deliveringEnergy = false;

        // Nur Sanity-Checks
        assertEquals("Martin Keller", name);
        assertEquals("CUST-1023", customerId);
        assertEquals("11", stationId);
        assertEquals("AC", mode1);
        assertEquals("DC", mode2);
    }

    @When("the customer selects charging mode {string}")
    public void the_customer_selects_charging_mode(String mode) {
        assertNotNull(currentCustomer, "Customer should exist");
        assertTrue(currentCustomer.validated, "Customer should be validated");
        assertNotNull(currentStation, "Station should exist");

        // In einem echten System würden wir prüfen, ob der Modus unterstützt wird
        selectedChargingMode = mode;
    }

    @Then("the system configures charging station {string} with charging mode {string}")
    public void the_system_configures_charging_station_with_charging_mode(String expectedStationId, String expectedMode) {
        assertNotNull(currentStation, "Station should exist");
        assertNotNull(selectedChargingMode, "A charging mode should have been selected");

        // Station konfigurieren
        currentStation.configured = true;
        currentStation.configuredMode = selectedChargingMode;
        currentStation.mode = selectedChargingMode;

        // Jetzt echte Asserts – hier war vorher dein 0 statt 11 Problem
        assertEquals(expectedStationId, currentStation.id);
        assertEquals(expectedMode, currentStation.configuredMode);
    }

   
    @Given("a validated customer {string} with customer ID {string} is at charging station with id {string} configured with charging mode {string}")
    public void a_validated_customer_with_customer_id_is_at_charging_station_with_id_configured_with_charging_mode(
            String name,
            String customerId,
            String stationId,
            String configuredMode
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = name;
        currentCustomer.customerId = customerId;
        currentCustomer.validated = true;

        currentStation = new ChargingStation();
        currentStation.id = stationId;
        currentStation.location = "City Center";
        currentStation.status = "IN_BETRIEB_FREI";
        currentStation.mode = configuredMode;
        currentStation.configured = true;
        currentStation.configuredMode = configuredMode;
        currentStation.deliveringEnergy = false;

        assertEquals("Martin Keller", name);
        assertEquals("CUST-1023", customerId);
        assertEquals("11", stationId);
        assertEquals("DC", configuredMode);
    }

    @When("the customer starts the charging session at {string}")
    public void the_customer_starts_the_charging_session_at(String startTime) {
        assertNotNull(currentCustomer, "Customer should exist");
        assertNotNull(currentStation, "Station should exist");
        assertTrue(currentCustomer.validated, "Customer should be validated");
        assertTrue(currentStation.configured, "Station should be configured");

        currentSession = new ChargingSession();
        currentSession.sessionId = "SESSION-START";
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
        currentSession.startTime = startTime;
        currentSession.energyKWh = 0.0;

        currentStation.deliveringEnergy = true;
    }

    @Then("the system begins delivering energy at charging station {string}")
    public void the_system_begins_delivering_energy_at_charging_station(String expectedStationId) {
        assertNotNull(currentStation, "Station should exist");
        assertTrue(currentStation.deliveringEnergy, "Station should be delivering energy");
        assertEquals(expectedStationId, currentStation.id);
    }

    @Then("the system records the start time of the charging session as {string}")
    public void the_system_records_the_start_time_of_the_charging_session_as(String expectedStartTime) {
        assertNotNull(currentSession, "Session should exist");
        assertEquals(expectedStartTime, currentSession.startTime);
    }

  

    @Given("an ongoing charging session {string} for customer {string} at charging station {string} started at {string} with recorded energy {string} kWh")
    public void an_ongoing_charging_session_for_customer_at_charging_station_started_at_with_recorded_energy_k_wh(
            String sessionId,
            String customerName,
            String stationId,
            String startTime,
            String energyKWh
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = customerName;
        currentCustomer.customerId = "CUST-1023";
        currentCustomer.validated = true;

        currentStation = new ChargingStation();
        currentStation.id = stationId;
        currentStation.location = "City Center";
        currentStation.mode = "DC";
        currentStation.status = "IN_BETRIEB_FREI";
        currentStation.configured = true;
        currentStation.configuredMode = "DC";
        currentStation.deliveringEnergy = true;

        currentSession = new ChargingSession();
        currentSession.sessionId = sessionId;
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
        currentSession.startTime = startTime;
        currentSession.energyKWh = Double.parseDouble(energyKWh);

        recordedEnergyKWh = currentSession.energyKWh;

        // Sanity-Checks
        assertEquals("5001", sessionId);
        assertEquals("Martin Keller", customerName);
        assertEquals("11", stationId);
        assertEquals("2025-11-20T10:00", startTime);
        assertEquals(10.0, recordedEnergyKWh, 0.0001);
    }

    @When("the charging progress changes and the recorded energy for session {string} is updated to {string} kWh")
    public void the_charging_progress_changes_and_the_recorded_energy_for_session_is_updated_to_k_wh(
            String sessionId,
            String newEnergyKWh
    ) {
        assertNotNull(currentSession, "Session should exist");
        assertEquals(sessionId, currentSession.sessionId);

        double newEnergy = Double.parseDouble(newEnergyKWh);
        currentSession.energyKWh = newEnergy;
        recordedEnergyKWh = newEnergy;

        lastNotificationSessionId = sessionId;
        lastNotificationEnergyKWh = newEnergy;
    }

    @Then("the system updates the charging session data to energy {string} kWh")
    public void the_system_updates_the_charging_session_data_to_energy_k_wh(String expectedEnergyKWh) {
        assertNotNull(currentSession, "Session should exist");
        double expected = Double.parseDouble(expectedEnergyKWh);
        assertEquals(expected, currentSession.energyKWh, 0.0001);
    }

    @Then("the updated charging session status for session {string} is available to be shown to the customer")
    public void the_updated_charging_session_status_for_session_is_available_to_be_shown_to_the_customer(String sessionId) {
        assertEquals(sessionId, lastNotificationSessionId, "Notification should refer to the correct session");
        assertEquals(currentSession.energyKWh, lastNotificationEnergyKWh, 0.0001);
    }
}

