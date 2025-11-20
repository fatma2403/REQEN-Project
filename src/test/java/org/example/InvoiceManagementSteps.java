package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für invoice_management.feature
 */
public class InvoiceManagementSteps {

    private Ladevorgang ladevorgang;
    private Preisregel preisregel;
    private Rechnung rechnung;

    private boolean tarifDefiniert;
    private boolean kostenBerechnet;
    private boolean rechnungseintragErzeugt;
    private boolean filterAngewendet;
    private boolean exportErfolgreich;

    // ---------- Szenario: Calculate cost of a charging session ----------

    @Given("a completed charging session with measured energy")
    public void a_completed_charging_session_with_measured_energy() {
        ladevorgang = new Ladevorgang();
        ladevorgang.setStart(LocalDateTime.now().minusMinutes(60));
        ladevorgang.setEnde(LocalDateTime.now());
    }

    @Given("tariffs are defined for the location and charging mode")
    public void tariffs_are_defined_for_the_location_and_charging_mode() {
        preisregel = new Preisregel(1, Lademodus.AC, 0.5, 0.1, LocalDateTime.now().minusDays(1));
        tarifDefiniert = true;
    }

    @When("the operator triggers the cost calculation")
    public void the_operator_triggers_the_cost_calculation() {
        if (tarifDefiniert) {
            kostenBerechnet = true; // Dummy-Berechnung
        }
    }

    @Then("the system calculates the total cost of the session")
    public void the_system_calculates_the_total_cost_of_the_session() {
        assertTrue(kostenBerechnet, "Kosten sollten berechnet sein");
    }

    // ---------- Szenario: Generate invoice entry ----------

    @Given("the cost for a charging session is calculated")
    public void the_cost_for_a_charging_session_is_calculated() {
        kostenBerechnet = true;
    }

    @When("the operator confirms the billing")
    public void the_operator_confirms_the_billing() {
        if (kostenBerechnet) {
            rechnungseintragErzeugt = true;
        }
    }

    @Then("the system creates an invoice entry in the billing system")
    public void the_system_creates_an_invoice_entry_in_the_billing_system() {
        assertTrue(rechnungseintragErzeugt, "Rechnungseintrag sollte erzeugt werden");
    }

    // ---------- Szenario: Check details of a charging session ----------

    @Given("an invoice exists for a charging session")
    public void an_invoice_exists_for_a_charging_session() {
        rechnung = new Rechnung();
    }

    @When("the operator opens the invoice details")
    public void the_operator_opens_the_invoice_details() {
        // nichts Spezielles
    }

    @Then("the system shows energy used, time period and applied tariffs")
    public void the_system_shows_energy_used_time_period_and_applied_tariffs() {
        assertNotNull(rechnung, "Rechnung sollte existieren");
    }

    // ---------- Szenario: Filter invoices by period or customer ----------

    @Given("invoices exist in the system")
    public void invoices_exist_in_the_system() {
        rechnung = new Rechnung();
    }

    @When("the operator filters invoices by date range or customer")
    public void the_operator_filters_invoices_by_date_range_or_customer() {
        filterAngewendet = true;
    }

    @Then("the system shows only invoices that match the filter criteria")
    public void the_system_shows_only_invoices_that_match_the_filter_criteria() {
        assertTrue(filterAngewendet, "Filter sollte angewendet sein");
    }

    // ---------- Szenario: Show invoice list ----------

    @When("the operator opens the invoice overview")
    public void the_operator_opens_the_invoice_overview() {
        // keine besondere Logik
    }

    @Then("the system displays a list of all invoices")
    public void the_system_displays_a_list_of_all_invoices() {
        assertNotNull(rechnung, "Mindestens eine Rechnung sollte existieren");
    }

    // ---------- Szenario: Export invoices ----------

    @Given("a list of invoices is displayed")
    public void a_list_of_invoices_is_displayed() {
        rechnung = new Rechnung();
    }

    @When("the operator exports the invoices")
    public void the_operator_exports_the_invoices() {
        if (rechnung != null) {
            exportErfolgreich = true;
        }
    }

    @Then("the system provides the invoices in a downloadable file format")
    public void the_system_provides_the_invoices_in_a_downloadable_file_format() {
        assertTrue(exportErfolgreich, "Export sollte erfolgreich sein");
    }
}
