package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagePaymentsSteps {

    private static class InvoiceEntry {
        String number;
        String date;
        String totalAmount;
        String customerEmail;

        InvoiceEntry(String number, String date, String totalAmount, String customerEmail) {
            this.number = number;
            this.date = date;
            this.totalAmount = totalAmount;
            this.customerEmail = customerEmail;
        }
    }

    private static class ChargingSessionEntry {
        String sessionId;
        String start;
        String end;
        String energyKWh;
        String location;

        ChargingSessionEntry(String sessionId, String start, String end, String energyKWh, String location) {
            this.sessionId = sessionId;
            this.start = start;
            this.end = end;
            this.energyKWh = energyKWh;
            this.location = location;
        }
    }

    private String customerName;
    private String customerEmail;
    private String customerId;

    private final List<InvoiceEntry> invoiceList = new ArrayList<>();
    private boolean invoiceOverviewOpened;

    private final List<ChargingSessionEntry> chargingSessions = new ArrayList<>();
    private boolean chargingHistoryOpened;

    // Neu für Edge/Error-Cases
    private boolean invoiceAccessDenied;
    private boolean invoiceEmptyStateShown;
    private String invoiceErrorMessage;

    @Given("a logged-in customer with name {string}, email {string} and customer ID {string}")
    public void a_logged_in_customer_with_name_email_and_customer_id(String name, String email, String custId) {
        this.customerName = name;
        this.customerEmail = email;
        this.customerId = custId;

        // Sanity-Checks (nicht mehr hart auf CUST-1023, damit Error-Case "UNKNOWN" möglich ist)
        assertNotNull(name);
        assertFalse(name.trim().isEmpty(), "Customer name must not be empty");

        assertNotNull(email);
        assertFalse(email.trim().isEmpty(), "Customer email must not be empty");

        assertNotNull(custId);
        assertFalse(custId.trim().isEmpty(), "Customer ID must not be empty");
    }

    @Given("past invoices exist for this customer: invoice {string} dated {string} with total amount {string} and invoice {string} dated {string} with total amount {string}")
    public void past_invoices_exist_for_this_customer_invoice_dated_with_total_amount_and_invoice_dated_with_total_amount(
            String inv1Number,
            String inv1Date,
            String inv1Amount,
            String inv2Number,
            String inv2Date,
            String inv2Amount
    ) {
        invoiceList.clear();
        invoiceList.add(new InvoiceEntry(inv1Number, inv1Date, inv1Amount, customerEmail));
        invoiceList.add(new InvoiceEntry(inv2Number, inv2Date, inv2Amount, customerEmail));

        // (Optional) leichte Plausibilität statt harter Feature-Fixierung
        assertNotNull(inv1Number);
        assertNotNull(inv2Number);
        assertNotEquals(inv1Number, inv2Number, "Invoice numbers should be different");
    }

    // ✅ Neu: Edge Case "keine Rechnungen"
    @Given("no past invoices exist for this customer")
    public void no_past_invoices_exist_for_this_customer() {
        invoiceList.clear();
    }

    @When("the customer opens the invoice overview")
    public void the_customer_opens_the_invoice_overview() {
        invoiceOverviewOpened = true;

        // Reset pro Öffnen
        invoiceAccessDenied = false;
        invoiceEmptyStateShown = false;
        invoiceErrorMessage = null;

        // Error-Case: unbekannte/ungültige Customer ID
        if ("UNKNOWN".equalsIgnoreCase(customerId)) {
            invoiceAccessDenied = true;
            invoiceErrorMessage = "INVOICE_ACCESS_ERROR";
            return;
        }

        // Edge-Case: keine Rechnungen vorhanden
        if (invoiceList.isEmpty()) {
            invoiceEmptyStateShown = true;
        }
    }

    @Then("the system shows a list of invoices {string} and {string} for customer ID {string}")
    public void the_system_shows_a_list_of_invoices_and_for_customer_id(
            String expectedInv1,
            String expectedInv2,
            String expectedCustomerId
    ) {
        assertTrue(invoiceOverviewOpened, "Invoice overview should be opened");
        assertFalse(invoiceAccessDenied, "Invoice overview should not be denied for valid customer");
        assertEquals(expectedCustomerId, this.customerId, "Customer ID should match the logged-in customer");
        assertFalse(invoiceList.isEmpty(), "Invoice list should not be empty");

        boolean foundInv1 = invoiceList.stream().anyMatch(inv -> inv.number.equals(expectedInv1));
        boolean foundInv2 = invoiceList.stream().anyMatch(inv -> inv.number.equals(expectedInv2));

        assertTrue(foundInv1, "Invoice " + expectedInv1 + " should be in the list");
        assertTrue(foundInv2, "Invoice " + expectedInv2 + " should be in the list");
    }

    // ✅ Neu: Then-Step für „keine Rechnungen“
    @Then("the system shows no invoices for customer ID {string}")
    public void the_system_shows_no_invoices_for_customer_id(String expectedCustomerId) {
        assertTrue(invoiceOverviewOpened, "Invoice overview should be opened");
        assertFalse(invoiceAccessDenied, "Access must not be denied in empty-state scenario");
        assertEquals(expectedCustomerId, this.customerId, "Customer ID should match");
        assertTrue(invoiceList.isEmpty(), "Invoice list should be empty");
        assertTrue(invoiceEmptyStateShown, "Empty state should be shown");
    }

    // ✅ Neu: Empty Message
    @Then("the system shows an invoice empty message")
    public void the_system_shows_an_invoice_empty_message() {
        assertTrue(invoiceOverviewOpened, "Invoice overview should be opened");
        assertTrue(invoiceEmptyStateShown, "Invoice empty message should be shown");
        assertNull(invoiceErrorMessage, "No error message expected when empty state is shown");
    }

    // ✅ Neu: Zugriff/Fehlerfall
    @Then("the system shows an invoice access error message")
    public void the_system_shows_an_invoice_access_error_message() {
        assertTrue(invoiceOverviewOpened, "Invoice overview should be opened");
        assertTrue(invoiceAccessDenied, "Access should be denied for unknown customer");
        assertEquals("INVOICE_ACCESS_ERROR", invoiceErrorMessage, "Expected invoice access error message");
    }

    @Given("past charging sessions exist for this customer: session {string} from {string} to {string} with energy {string} kWh at location {string} and session {string} from {string} to {string} with energy {string} kWh at location {string}")
    public void past_charging_sessions_exist_for_this_customer_session_from_to_with_energy_k_wh_at_location_and_session_from_to_with_energy_k_wh_at_location(
            String session1Id,
            String session1Start,
            String session1End,
            String session1Energy,
            String session1Location,
            String session2Id,
            String session2Start,
            String session2End,
            String session2Energy,
            String session2Location
    ) {
        chargingSessions.clear();
        chargingSessions.add(new ChargingSessionEntry(session1Id, session1Start, session1End, session1Energy, session1Location));
        chargingSessions.add(new ChargingSessionEntry(session2Id, session2Start, session2End, session2Energy, session2Location));

        // Sanity-Checks: nur plausibel
        assertNotEquals(session1Id, session2Id, "Session IDs should be different");
    }

    @When("the customer opens the charging history")
    public void the_customer_opens_the_charging_history() {
        chargingHistoryOpened = true;
    }

    @Then("the system shows a list of charging sessions with main details including session {string} with start {string}, end {string}, energy {string} kWh and location {string} and session {string} with start {string}, end {string}, energy {string} kWh and location {string}")
    public void the_system_shows_a_list_of_charging_sessions_with_main_details_including_session_with_start_end_energy_k_wh_and_location_and_session_with_start_end_energy_k_wh_and_location(
            String session1Id,
            String session1Start,
            String session1End,
            String session1Energy,
            String session1Location,
            String session2Id,
            String session2Start,
            String session2End,
            String session2Energy,
            String session2Location
    ) {
        assertTrue(chargingHistoryOpened, "Charging history should be opened");

        ChargingSessionEntry s1 = chargingSessions.stream().filter(s -> s.sessionId.equals(session1Id)).findFirst().orElse(null);
        assertNotNull(s1, "Session " + session1Id + " should exist in history");
        assertEquals(session1Start, s1.start);
        assertEquals(session1End, s1.end);
        assertEquals(session1Energy, s1.energyKWh);
        assertEquals(session1Location, s1.location);

        ChargingSessionEntry s2 = chargingSessions.stream().filter(s -> s.sessionId.equals(session2Id)).findFirst().orElse(null);
        assertNotNull(s2, "Session " + session2Id + " should exist in history");
        assertEquals(session2Start, s2.start);
        assertEquals(session2End, s2.end);
        assertEquals(session2Energy, s2.energyKWh);
        assertEquals(session2Location, s2.location);
    }
}

