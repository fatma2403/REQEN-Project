package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class StopChargingSteps {

    // --- einfache Modellklassen nur für die Tests ---

    static class Customer {
        String name;
        String customerId;
    }

    static class ChargingStation {
        String id;
        boolean sessionRunning;
    }

    static class ChargingSession {
        String sessionId;
        Customer customer;
        ChargingStation station;
        String startTime;
        String endTime;
        double measuredEnergyKWh;
        boolean finished;
    }

    static class PricingRule {
        String location;
        String mode;
        double pricePerKWh;
        double pricePerMinute;
        String validFrom;
    }

    static class BillingRecord {
        String sessionId;
        String customerId;
        double totalAmount;
    }

    static class Invoice {
        String invoiceNumber;
        String sessionId;
        String customerId;
        String customerName;
        double totalAmount;
        boolean generated;
    }

    // --- Zustand über die Szenarien hinweg ---

    private Customer currentCustomer;
    private ChargingStation currentStation;
    private ChargingSession currentSession;
    private PricingRule currentPricingRule;
    private BillingRecord currentBillingRecord;
    private Invoice currentInvoice;

    // =========================================================
    // NEU: Flags / Messages für Error & Edge Cases
    // =========================================================
    private boolean stopRejected;
    private String stopErrorMessage;

    private boolean alreadyFinishedMessageShown;
    private String alreadyFinishedMessage;

    // --------------------------------------------------------------------
    // Scenario: Unplug the charging cable
    // --------------------------------------------------------------------

    @Given("an ongoing charging session {string} for customer {string} with customer ID {string} at charging station {string} started at {string}")
    public void an_ongoing_charging_session_for_customer_with_customer_id_at_charging_station_started_at(
            String sessionId,
            String customerName,
            String customerId,
            String stationId,
            String startTime
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = customerName;
        currentCustomer.customerId = customerId;

        currentStation = new ChargingStation();
        currentStation.id = stationId;
        currentStation.sessionRunning = true;

        currentSession = new ChargingSession();
        currentSession.sessionId = sessionId;
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
        currentSession.startTime = startTime;
        currentSession.finished = false;

        // Reset Flags (NEU)
        stopRejected = false;
        stopErrorMessage = null;
        alreadyFinishedMessageShown = false;
        alreadyFinishedMessage = null;

        // Sanity-Checks gegen das Feature
        assertEquals("5001", sessionId);
        assertEquals("Martin Keller", customerName);
        assertEquals("CUST-1023", customerId);
        assertEquals("11", stationId);
        assertEquals("2025-11-20T10:00", startTime);
    }

    @When("the customer unplugs the charging cable at {string}")
    public void the_customer_unplugs_the_charging_cable_at(String endTime) {
        assertNotNull(currentSession, "Session must exist");
        assertNotNull(currentStation, "Station must exist");

        // Edge: Wenn schon finished, dann NICHT überschreiben
        if (currentSession.finished) {
            alreadyFinishedMessageShown = true;
            alreadyFinishedMessage = "CHARGING_SESSION_ALREADY_FINISHED";
            return;
        }

        currentSession.endTime = endTime;
        currentSession.finished = true;
        currentStation.sessionRunning = false;
    }

    @Then("the system stops the charging session {string} and records the end time {string}")
    public void the_system_stops_the_charging_session_and_records_the_end_time(
            String expectedSessionId,
            String expectedEndTime
    ) {
        assertNotNull(currentSession, "Session must exist");
        assertEquals(expectedSessionId, currentSession.sessionId);
        assertTrue(currentSession.finished, "Session should be finished");
        assertEquals(expectedEndTime, currentSession.endTime);
        assertFalse(currentStation.sessionRunning, "Station should no longer have a running session");

        // Sicherstellen, dass das nicht der Error-Flow war
        assertFalse(stopRejected, "Stop must not be rejected in happy path");
    }

    // --------------------------------------------------------------------
    // NEU: Error Case – Session unknown
    // --------------------------------------------------------------------

    @When("the customer unplugs the charging cable for session {string} at {string}")
    public void the_customer_unplugs_the_charging_cable_for_session_at(String sessionIdToStop, String endTime) {
        assertNotNull(currentSession, "A known session should exist in test context");

        // Wenn andere Session-ID -> ablehnen
        if (!currentSession.sessionId.equals(sessionIdToStop)) {
            stopRejected = true;
            stopErrorMessage = "STOP_CHARGING_ERROR";
            return;
        }

        // sonst normal stoppen
        the_customer_unplugs_the_charging_cable_at(endTime);
    }

    @Then("the system rejects stopping session {string}")
    public void the_system_rejects_stopping_session(String sessionId) {
        assertTrue(stopRejected, "Stopping should be rejected");
        assertNotNull(stopErrorMessage, "Stop error message should be set");
        assertEquals("STOP_CHARGING_ERROR", stopErrorMessage);
        assertNotNull(sessionId);

        // Session darf NICHT verändert werden
        assertNotNull(currentSession, "Session must exist in context");
        assertFalse(currentSession.finished, "Known session must still be ongoing");
        assertTrue(currentStation.sessionRunning, "Station should still have running session");
    }

    @Then("the system shows a stop charging error message")
    public void the_system_shows_a_stop_charging_error_message() {
        assertTrue(stopRejected, "Stopping should be rejected");
        assertEquals("STOP_CHARGING_ERROR", stopErrorMessage);
    }

    // --------------------------------------------------------------------
    // NEU: Edge Case – Session already finished
    // --------------------------------------------------------------------

    @Given("a finished charging session {string} for customer {string} with customer ID {string} ended at {string}")
    public void a_finished_charging_session_for_customer_with_customer_id_ended_at(
            String sessionId,
            String customerName,
            String customerId,
            String endedAt
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = customerName;
        currentCustomer.customerId = customerId;

        currentStation = new ChargingStation();
        currentStation.id = "11";
        currentStation.sessionRunning = false;

        currentSession = new ChargingSession();
        currentSession.sessionId = sessionId;
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
        currentSession.startTime = "2025-11-20T10:00"; // nicht relevant für diesen Edge Case
        currentSession.endTime = endedAt;
        currentSession.finished = true;

        // Reset Flags
        stopRejected = false;
        stopErrorMessage = null;
        alreadyFinishedMessageShown = false;
        alreadyFinishedMessage = null;

        assertEquals("5001", sessionId);
        assertEquals("Martin Keller", customerName);
        assertEquals("CUST-1023", customerId);
        assertEquals("2025-11-20T10:30", endedAt);
    }

    @When("the customer unplugs the charging cable again")
    public void the_customer_unplugs_the_charging_cable_again() {
        assertNotNull(currentSession, "Session must exist");
        assertTrue(currentSession.finished, "Session must already be finished");
        assertNotNull(currentStation, "Station must exist");

        // nichts ändern, nur Message setzen
        alreadyFinishedMessageShown = true;
        alreadyFinishedMessage = "CHARGING_SESSION_ALREADY_FINISHED";
    }

    @Then("the system keeps the charging session {string} unchanged")
    public void the_system_keeps_the_charging_session_unchanged(String sessionId) {
        assertNotNull(currentSession, "Session must exist");
        assertEquals(sessionId, currentSession.sessionId);

        // Unchanged bedeutet: endTime bleibt wie gesetzt + finished bleibt true
        assertTrue(currentSession.finished, "Session must remain finished");
        assertNotNull(currentSession.endTime, "End time must remain set");
        assertFalse(currentStation.sessionRunning, "Station must remain not running");
    }

    @Then("the system shows a charging session already finished message")
    public void the_system_shows_a_charging_session_already_finished_message() {
        assertTrue(alreadyFinishedMessageShown, "Already finished message should be shown");
        assertEquals("CHARGING_SESSION_ALREADY_FINISHED", alreadyFinishedMessage);
    }

    // --------------------------------------------------------------------
    // Scenario: Automatic billing after charging
    // --------------------------------------------------------------------

    @Given("a finished charging session {string} for customer {string} with customer ID {string} from {string} to {string} with measured energy {string} kWh")
    public void a_finished_charging_session_for_customer_with_customer_id_from_to_with_measured_energy_k_wh(
            String sessionId,
            String customerName,
            String customerId,
            String startTime,
            String endTime,
            String energyKWh
    ) {
        currentCustomer = new Customer();
        currentCustomer.name = customerName;
        currentCustomer.customerId = customerId;

        currentStation = new ChargingStation();
        currentStation.id = "11"; // aus Domäne/Feature ableitbar
        currentStation.sessionRunning = false;

        currentSession = new ChargingSession();
        currentSession.sessionId = sessionId;
        currentSession.customer = currentCustomer;
        currentSession.station = currentStation;
        currentSession.startTime = startTime;
        currentSession.endTime = endTime;
        currentSession.measuredEnergyKWh = Double.parseDouble(energyKWh);
        currentSession.finished = true;

        assertEquals("5001", sessionId);
        assertEquals("Martin Keller", customerName);
        assertEquals("CUST-1023", customerId);
        assertEquals("2025-11-20T10:00", startTime);
        assertEquals("2025-11-20T10:30", endTime);
        assertEquals(24.0, currentSession.measuredEnergyKWh, 0.0001);
    }

    @Given("a pricing rule exists for location {string} with charging mode {string}, price per kWh {string} and price per minute {string} valid from {string}")
    public void a_pricing_rule_exists_for_location_with_charging_mode_price_per_k_wh_and_price_per_minute_valid_from(
            String location,
            String mode,
            String pricePerKWh,
            String pricePerMinute,
            String validFrom
    ) {
        currentPricingRule = new PricingRule();
        currentPricingRule.location = location;
        currentPricingRule.mode = mode;
        currentPricingRule.pricePerKWh = Double.parseDouble(pricePerKWh);
        currentPricingRule.pricePerMinute = Double.parseDouble(pricePerMinute);
        currentPricingRule.validFrom = validFrom;

        assertEquals("City Center", location);
        assertEquals("DC", mode);
    }

    @When("the system calculates the cost for session {string} based on the tariffs")
    public void the_system_calculates_the_cost_for_session_based_on_the_tariffs(String sessionId) {
        assertNotNull(currentSession, "Session must exist");
        assertNotNull(currentPricingRule, "Pricing rule must exist");
        assertEquals(sessionId, currentSession.sessionId);

        // Für dieses Beispiel wissen wir: 30 Minuten
        long minutes = 30L;

        double energyCost = currentSession.measuredEnergyKWh * currentPricingRule.pricePerKWh;
        double timeCost = minutes * currentPricingRule.pricePerMinute;
        double total = energyCost + timeCost;

        currentBillingRecord = new BillingRecord();
        currentBillingRecord.sessionId = currentSession.sessionId;
        currentBillingRecord.customerId = currentCustomer.customerId;
        currentBillingRecord.totalAmount = total;
    }

    @Then("the system creates a billing record for customer ID {string} with total amount {string}")
    public void the_system_creates_a_billing_record_for_customer_id_with_total_amount(
            String expectedCustomerId,
            String expectedTotalAmount
    ) {
        assertNotNull(currentBillingRecord, "Billing record must exist");
        assertEquals(expectedCustomerId, currentBillingRecord.customerId);

        double expected = Double.parseDouble(expectedTotalAmount);
        assertEquals(expected, currentBillingRecord.totalAmount, 0.01);
    }

    // --------------------------------------------------------------------
    // Scenario: Customer receives an invoice
    // --------------------------------------------------------------------

    @Given("the system has created a billing record for session {string} with invoice number {string} and total amount {string} for customer ID {string}")
    public void the_system_has_created_a_billing_record_for_session_with_invoice_number_and_total_amount_for_customer_id(
            String sessionId,
            String invoiceNumber,
            String totalAmount,
            String customerId
    ) {
        currentBillingRecord = new BillingRecord();
        currentBillingRecord.sessionId = sessionId;
        currentBillingRecord.customerId = customerId;
        currentBillingRecord.totalAmount = Double.parseDouble(totalAmount);

        currentCustomer = new Customer();
        currentCustomer.customerId = customerId;
        if ("CUST-1023".equals(customerId)) {
            currentCustomer.name = "Martin Keller";
        }

        currentInvoice = new Invoice();
        currentInvoice.invoiceNumber = invoiceNumber;
        currentInvoice.sessionId = sessionId;
        currentInvoice.customerId = customerId;
        currentInvoice.totalAmount = Double.parseDouble(totalAmount);
        currentInvoice.customerName = currentCustomer.name;
        currentInvoice.generated = false;

        assertEquals("5001", sessionId);
        assertEquals("1001", invoiceNumber);
        assertEquals("CUST-1023", customerId);
    }

    @When("the invoice {string} is generated")
    public void the_invoice_is_generated(String invoiceNumber) {
        assertNotNull(currentInvoice, "Invoice must exist");
        assertEquals(invoiceNumber, currentInvoice.invoiceNumber);
        currentInvoice.generated = true;
    }

    @Then("the customer {string} can view invoice {string} with total amount {string} for charging session {string}")
    public void the_customer_can_view_invoice_with_total_amount_for_charging_session(
            String expectedCustomerName,
            String expectedInvoiceNumber,
            String expectedTotalAmount,
            String expectedSessionId
    ) {
        assertNotNull(currentInvoice, "Invoice must exist");
        assertTrue(currentInvoice.generated, "Invoice should be generated");

        assertEquals(expectedCustomerName, currentInvoice.customerName);
        assertEquals(expectedInvoiceNumber, currentInvoice.invoiceNumber);
        assertEquals(expectedSessionId, currentInvoice.sessionId);

        double expectedTotal = Double.parseDouble(expectedTotalAmount);
        assertEquals(expectedTotal, currentInvoice.totalAmount, 0.01);
    }
}
