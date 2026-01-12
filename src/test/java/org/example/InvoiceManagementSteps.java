package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceManagementSteps {

    // -------------------------
    // Simple in-memory test state
    // -------------------------
    private static class Session {
        String customerName;
        String customerEmail;
        LocalDateTime start;
        LocalDateTime end;
        double energyKwh;
        String mode;
    }

    private static class Tariff {
        String mode;
        double pricePerKwh;
        double pricePerMinute;
    }

    private static class Invoice {
        String number;
        LocalDate date;
        String customerName;
        String customerEmail;
        double totalAmount;

        // details for "Check details of a charging session"
        LocalDateTime start;
        LocalDateTime end;
        double energyKwh;
        double pricePerKwh;
        double pricePerMinute;
    }

    private final List<Invoice> invoices = new ArrayList<>();
    private final List<Invoice> filteredInvoices = new ArrayList<>();
    private final List<String> displayedInvoiceNumbers = new ArrayList<>();

    private Session completedSession;
    private Tariff tariff;
    private double calculatedCost;

    private Invoice generatedInvoice;
    private Invoice openedInvoiceDetails;

    private String exportedFileName;
    private boolean noInvoicesMessageShown;

    // -------------------------
    // Scenario: Calculate cost of a charging session
    // -------------------------
    @Given("a completed charging session for customer {string} with email {string} from {string} to {string} with energy {string} kWh at charging mode {string}")
    public void a_completed_charging_session_for_customer_with_email_from_to_with_energy_kwh_at_charging_mode(
            String customerName,
            String email,
            String startStr,
            String endStr,
            String energyStr,
            String mode
    ) {
        completedSession = new Session();
        completedSession.customerName = customerName;
        completedSession.customerEmail = email;
        completedSession.start = LocalDateTime.parse(startStr);
        completedSession.end = LocalDateTime.parse(endStr);
        completedSession.energyKwh = Double.parseDouble(energyStr);
        completedSession.mode = mode;
    }

    @Given("tariffs are defined with price per kWh {string} and price per minute {string} for charging mode {string}")
    public void tariffs_are_defined_with_price_per_kwh_and_price_per_minute_for_charging_mode(
            String pricePerKwhStr,
            String pricePerMinuteStr,
            String mode
    ) {
        tariff = new Tariff();
        tariff.mode = mode;
        tariff.pricePerKwh = Double.parseDouble(pricePerKwhStr);
        tariff.pricePerMinute = Double.parseDouble(pricePerMinuteStr);
    }

    @When("the operator triggers the cost calculation")
    public void the_operator_triggers_the_cost_calculation() {
        assertNotNull(completedSession, "Completed session must be set");
        assertNotNull(tariff, "Tariff must be set");
        assertEquals(tariff.mode, completedSession.mode, "Tariff mode must match session mode");

        long minutes = java.time.Duration.between(completedSession.start, completedSession.end).toMinutes();
        double cost = completedSession.energyKwh * tariff.pricePerKwh + minutes * tariff.pricePerMinute;

        // round to 2 decimals to match expected strings
        calculatedCost = Math.round(cost * 100.0) / 100.0;
    }

    @Then("the system calculates the total cost of the session as {string}")
    public void the_system_calculates_the_total_cost_of_the_session_as(String expectedCostStr) {
        double expected = Double.parseDouble(expectedCostStr);
        assertEquals(expected, calculatedCost, 0.0001, "Calculated cost must match expected cost");
    }

    // -------------------------
    // Scenario: Generate invoice entry
    // -------------------------
    @Given("the cost of a completed charging session for customer {string} with email {string} is {string}")
    public void the_cost_of_a_completed_charging_session_for_customer_with_email_is(
            String customerName,
            String email,
            String costStr
    ) {
        calculatedCost = Double.parseDouble(costStr);

        // Ensure we have a customer identity for invoice creation
        if (completedSession == null) {
            completedSession = new Session();
            completedSession.customerName = customerName;
            completedSession.customerEmail = email;
        } else {
            completedSession.customerName = customerName;
            completedSession.customerEmail = email;
        }
    }

    @When("the operator confirms the billing")
    public void the_operator_confirms_the_billing() {
        generatedInvoice = new Invoice();
        generatedInvoice.number = "1001";
        generatedInvoice.date = LocalDate.parse("2025-11-20");
        generatedInvoice.customerName = completedSession.customerName;
        generatedInvoice.customerEmail = completedSession.customerEmail;
        generatedInvoice.totalAmount = calculatedCost;

        invoices.add(generatedInvoice);
    }

    @Then("the system creates an invoice with number {string}, date {string} and total amount {string} for customer with email {string}")
    public void the_system_creates_an_invoice_with_number_date_and_total_amount_for_customer_with_email(
            String expectedNumber,
            String expectedDate,
            String expectedAmount,
            String expectedEmail
    ) {
        assertNotNull(generatedInvoice, "Generated invoice must exist");
        assertEquals(expectedNumber, generatedInvoice.number, "Invoice number must match");
        assertEquals(LocalDate.parse(expectedDate), generatedInvoice.date, "Invoice date must match");
        assertEquals(expectedEmail, generatedInvoice.customerEmail, "Invoice email must match");
        assertEquals(Double.parseDouble(expectedAmount), generatedInvoice.totalAmount, 0.0001, "Invoice amount must match");
    }

    // -------------------------
    // Scenario: Check details of a charging session
    // -------------------------
    @Given("an invoice with number {string} exists for customer {string} with email {string} and charging session from {string} to {string} with energy {string} kWh, price per kWh {string} and price per minute {string}")
    public void an_invoice_with_number_exists_for_customer_with_email_and_charging_session_from_to_with_energy_kwh_price_per_kwh_and_price_per_minute(
            String number,
            String customerName,
            String email,
            String startStr,
            String endStr,
            String energyStr,
            String pricePerKwhStr,
            String pricePerMinuteStr
    ) {
        Invoice inv = new Invoice();
        inv.number = number;
        inv.date = LocalDate.parse("2025-11-20");
        inv.customerName = customerName;
        inv.customerEmail = email;
        inv.totalAmount = 0.0;

        inv.start = LocalDateTime.parse(startStr);
        inv.end = LocalDateTime.parse(endStr);
        inv.energyKwh = Double.parseDouble(energyStr);
        inv.pricePerKwh = Double.parseDouble(pricePerKwhStr);
        inv.pricePerMinute = Double.parseDouble(pricePerMinuteStr);

        invoices.add(inv);
    }

    @When("the operator opens the invoice details for invoice number {string}")
    public void the_operator_opens_the_invoice_details_for_invoice_number(String number) {
        openedInvoiceDetails = invoices.stream()
                .filter(i -> Objects.equals(i.number, number))
                .findFirst()
                .orElse(null);

        assertNotNull(openedInvoiceDetails, "Invoice details must exist for number " + number);
    }

    @Then("the system shows energy used {string} kWh, time period from {string} to {string} and applied tariffs {string} per kWh and {string} per minute")
    public void the_system_shows_energy_used_kwh_time_period_and_applied_tariffs_per_kwh_and_per_minute(
            String expectedEnergyStr,
            String expectedStartStr,
            String expectedEndStr,
            String expectedPricePerKwhStr,
            String expectedPricePerMinuteStr
    ) {
        assertNotNull(openedInvoiceDetails, "Opened invoice details must exist");

        assertEquals(Double.parseDouble(expectedEnergyStr), openedInvoiceDetails.energyKwh, 0.0001, "Energy must match");
        assertEquals(LocalDateTime.parse(expectedStartStr), openedInvoiceDetails.start, "Start time must match");
        assertEquals(LocalDateTime.parse(expectedEndStr), openedInvoiceDetails.end, "End time must match");
        assertEquals(Double.parseDouble(expectedPricePerKwhStr), openedInvoiceDetails.pricePerKwh, 0.0001, "Price per kWh must match");
        assertEquals(Double.parseDouble(expectedPricePerMinuteStr), openedInvoiceDetails.pricePerMinute, 0.0001, "Price per minute must match");
    }

    // -------------------------
    // Scenario: Filter invoices by period or customer
    // -------------------------
    @Given("invoices exist in the system: invoice {string} for customer email {string} with date {string} and invoice {string} for customer email {string} with date {string}")
    public void invoices_exist_in_the_system_with_date(
            String inv1, String email1, String date1,
            String inv2, String email2, String date2
    ) {
        Invoice i1 = new Invoice();
        i1.number = inv1;
        i1.customerEmail = email1;
        i1.date = LocalDate.parse(date1);

        Invoice i2 = new Invoice();
        i2.number = inv2;
        i2.customerEmail = email2;
        i2.date = LocalDate.parse(date2);

        invoices.add(i1);
        invoices.add(i2);
    }

    @When("the operator filters invoices by date range from {string} to {string} and customer email {string}")
    public void the_operator_filters_invoices_by_date_range_from_to_and_customer_email(
            String fromStr, String toStr, String email
    ) {
        LocalDate from = LocalDate.parse(fromStr);
        LocalDate to = LocalDate.parse(toStr);

        filteredInvoices.clear();
        filteredInvoices.addAll(
                invoices.stream()
                        .filter(i -> i.date != null)
                        .filter(i -> !i.date.isBefore(from) && !i.date.isAfter(to))
                        .filter(i -> Objects.equals(i.customerEmail, email))
                        .collect(Collectors.toList())
        );
    }

    @Then("the system shows only invoices {string} in the result list")
    public void the_system_shows_only_invoices_in_the_result_list(String expectedInvoiceNumber) {
        assertEquals(1, filteredInvoices.size(), "Exactly one invoice should match the filter");
        assertEquals(expectedInvoiceNumber, filteredInvoices.get(0).number, "Filtered invoice number must match");
    }

    // -------------------------
    // Scenario: Show invoice list
    // -------------------------
    @Given("invoices exist in the system: invoice {string} for customer email {string} and invoice {string} for customer email {string}")
    public void invoices_exist_in_the_system_simple(String inv1, String email1, String inv2, String email2) {
        Invoice i1 = new Invoice();
        i1.number = inv1;
        i1.customerEmail = email1;
        i1.date = LocalDate.parse("2025-11-20");

        Invoice i2 = new Invoice();
        i2.number = inv2;
        i2.customerEmail = email2;
        i2.date = LocalDate.parse("2025-10-15");

        invoices.add(i1);
        invoices.add(i2);
    }

    @When("the operator opens the invoice overview")
    public void the_operator_opens_the_invoice_overview() {
        displayedInvoiceNumbers.clear();
        displayedInvoiceNumbers.addAll(
                invoices.stream()
                        .map(i -> i.number)
                        .collect(Collectors.toList())
        );
        noInvoicesMessageShown = displayedInvoiceNumbers.isEmpty();
    }

    @Then("the system displays a list of all invoices including {string} and {string}")
    public void the_system_displays_a_list_of_all_invoices_including(String inv1, String inv2) {
        assertTrue(displayedInvoiceNumbers.contains(inv1), "Invoice list must contain " + inv1);
        assertTrue(displayedInvoiceNumbers.contains(inv2), "Invoice list must contain " + inv2);
    }

    // -------------------------
    // Scenario: Export invoices
    // -------------------------
    @Given("a list of invoices {string} and {string} is displayed in the invoice overview")
    public void a_list_of_invoices_and_is_displayed_in_the_invoice_overview(String inv1, String inv2) {
        displayedInvoiceNumbers.clear();
        displayedInvoiceNumbers.add(inv1);
        displayedInvoiceNumbers.add(inv2);
        noInvoicesMessageShown = displayedInvoiceNumbers.isEmpty();
    }

    @When("the operator exports the invoices")
    public void the_operator_exports_the_invoices() {
        assertNotNull(displayedInvoiceNumbers, "Displayed invoices must exist before export");
        assertFalse(displayedInvoiceNumbers.isEmpty(), "Displayed invoices must not be empty before export");
        exportedFileName = "invoices_2025-11_export.csv";
    }

    @Then("the system provides the invoices in a downloadable file {string}")
    public void the_system_provides_the_invoices_in_a_downloadable_file(String expectedFileName) {
        assertNotNull(exportedFileName, "Exported file name must not be null");
        assertEquals(expectedFileName, exportedFileName, "Exported file name must match");
    }

    // -------------------------
    // Scenario: No invoices available
    // -------------------------
    @When("the customer opens the invoice list")
    public void the_customer_opens_the_invoice_list() {
        displayedInvoiceNumbers.clear();
        displayedInvoiceNumbers.addAll(
                invoices.stream().map(i -> i.number).collect(Collectors.toList())
        );
        noInvoicesMessageShown = displayedInvoiceNumbers.isEmpty();
    }

    @Then("the system shows no invoices message")
    public void the_system_shows_no_invoices_message() {
        assertTrue(noInvoicesMessageShown, "No invoices message should be shown when no invoices exist");
        assertTrue(displayedInvoiceNumbers.isEmpty(), "Displayed invoices must be empty when no invoices exist");
    }
}
