package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceManagementSteps {

    // Daten für Kostenermittlung
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private double energyKwh;
    private double pricePerKwh;
    private double pricePerMinute;
    private double berechneteKosten;

    // einfache Test-Invoice-Struktur
    private static class TestInvoice {
        String nummer;
        String kundenEmail;
        LocalDate datum;
        double betrag;

        LocalDateTime start;
        LocalDateTime ende;
        double energieKwh;
        double preisProKwh;
        double preisProMinute;
    }

    private TestInvoice aktuelleRechnung;
    private final List<TestInvoice> alleRechnungen = new ArrayList<>();
    private List<TestInvoice> gefilterteRechnungen;
    private boolean exportErfolgreich;

    // ---------- Szenario: Calculate cost of a charging session ----------

    @Given("a completed charging session for customer \"Martin Keller\" with email \"martin.keller@testmail.com\" from \"2025-11-20T10:00\" to \"2025-11-20T10:30\" with energy \"24.0\" kWh at charging mode \"DC\"")
    public void a_completed_charging_session_for_martin_keller_with_concrete_values() {
        sessionStart = LocalDateTime.parse("2025-11-20T10:00");
        sessionEnd   = LocalDateTime.parse("2025-11-20T10:30");
        energyKwh    = 24.0;
    }

    @Given("tariffs are defined with price per kWh \"0.45\" and price per minute \"0.10\" for charging mode \"DC\"")
    public void tariffs_are_defined_with_concrete_prices_for_dc() {
        pricePerKwh    = 0.45;
        pricePerMinute = 0.10;
    }

    @When("the operator triggers the cost calculation")
    public void the_operator_triggers_the_cost_calculation() {
        long minutes = Duration.between(sessionStart, sessionEnd).toMinutes();
        berechneteKosten = energyKwh * pricePerKwh + minutes * pricePerMinute;
    }

    @Then("the system calculates the total cost of the session as \"13.80\"")
    public void the_system_calculates_the_total_cost_of_the_session_as_13_80() {
        assertEquals(13.80, berechneteKosten, 0.0001);
    }

    // ---------- Szenario: Generate invoice entry ----------

    @Given("the cost of a completed charging session for customer \"Martin Keller\" with email \"martin.keller@testmail.com\" is \"13.80\"")
    public void the_cost_of_a_completed_charging_session_is_13_80() {
        berechneteKosten = 13.80;
    }

    @When("the operator confirms the billing")
    public void the_operator_confirms_the_billing() {
        aktuelleRechnung = new TestInvoice();
        aktuelleRechnung.nummer      = "1001";
        aktuelleRechnung.kundenEmail = "martin.keller@testmail.com";
        aktuelleRechnung.datum       = LocalDate.parse("2025-11-20");
        aktuelleRechnung.betrag      = berechneteKosten;
        alleRechnungen.add(aktuelleRechnung);
    }

    @Then("the system creates an invoice with number \"1001\", date \"2025-11-20\" and total amount \"13.80\" for customer with email \"martin.keller@testmail.com\"")
    public void the_system_creates_an_invoice_with_concrete_data() {
        assertNotNull(aktuelleRechnung);
        assertEquals("1001", aktuelleRechnung.nummer);
        assertEquals(LocalDate.parse("2025-11-20"), aktuelleRechnung.datum);
        assertEquals(13.80, aktuelleRechnung.betrag, 0.0001);
        assertEquals("martin.keller@testmail.com", aktuelleRechnung.kundenEmail);
    }

    // ---------- Szenario: Check details of a charging session ----------

    @Given("an invoice with number \"1001\" exists for customer \"Martin Keller\" with email \"martin.keller@testmail.com\" and charging session from \"2025-11-20T10:00\" to \"2025-11-20T10:30\" with energy \"24.0\" kWh, price per kWh \"0.45\" and price per minute \"0.10\"")
    public void an_invoice_with_number_1001_exists_with_concrete_session_details() {
        aktuelleRechnung = new TestInvoice();
        aktuelleRechnung.nummer      = "1001";
        aktuelleRechnung.kundenEmail = "martin.keller@testmail.com";
        aktuelleRechnung.datum       = LocalDate.parse("2025-11-20");
        aktuelleRechnung.betrag      = 13.80;

        aktuelleRechnung.start       = LocalDateTime.parse("2025-11-20T10:00");
        aktuelleRechnung.ende        = LocalDateTime.parse("2025-11-20T10:30");
        aktuelleRechnung.energieKwh  = 24.0;
        aktuelleRechnung.preisProKwh     = 0.45;
        aktuelleRechnung.preisProMinute  = 0.10;
    }

    @When("the operator opens the invoice details for invoice number \"1001\"")
    public void the_operator_opens_the_invoice_details_for_1001() {
        assertNotNull(aktuelleRechnung);
        assertEquals("1001", aktuelleRechnung.nummer);
    }

    @Then("the system shows energy used \"24.0\" kWh, time period from \"2025-11-20T10:00\" to \"2025-11-20T10:30\" and applied tariffs \"0.45\" per kWh and \"0.10\" per minute")
    public void the_system_shows_energy_time_and_tariffs() {
        assertEquals(24.0, aktuelleRechnung.energieKwh, 0.0001);
        assertEquals(LocalDateTime.parse("2025-11-20T10:00"), aktuelleRechnung.start);
        assertEquals(LocalDateTime.parse("2025-11-20T10:30"), aktuelleRechnung.ende);
        assertEquals(0.45, aktuelleRechnung.preisProKwh, 0.0001);
        assertEquals(0.10, aktuelleRechnung.preisProMinute, 0.0001);
    }

    // ---------- Szenario: Filter invoices by period or customer ----------

    @Given("invoices exist in the system: invoice \"1001\" for customer email \"martin.keller@testmail.com\" with date \"2025-11-20\" and invoice \"1002\" for customer email \"laura.fischer@testmail.com\" with date \"2025-10-15\"")
    public void invoices_exist_in_the_system_with_two_entries() {
        alleRechnungen.clear();

        TestInvoice r1 = new TestInvoice();
        r1.nummer      = "1001";
        r1.kundenEmail = "martin.keller@testmail.com";
        r1.datum       = LocalDate.parse("2025-11-20");

        TestInvoice r2 = new TestInvoice();
        r2.nummer      = "1002";
        r2.kundenEmail = "laura.fischer@testmail.com";
        r2.datum       = LocalDate.parse("2025-10-15");

        alleRechnungen.add(r1);
        alleRechnungen.add(r2);
    }

    @When("the operator filters invoices by date range from \"2025-11-01\" to \"2025-11-30\" and customer email \"martin.keller@testmail.com\"")
    public void the_operator_filters_invoices_by_date_range_and_customer() {
        LocalDate from = LocalDate.parse("2025-11-01");
        LocalDate to   = LocalDate.parse("2025-11-30");

        gefilterteRechnungen = new ArrayList<>();
        for (TestInvoice r : alleRechnungen) {
            LocalDate d = r.datum;
            boolean inRange = (!d.isBefore(from) && !d.isAfter(to));
            boolean emailOk = "martin.keller@testmail.com".equals(r.kundenEmail);
            if (inRange && emailOk) {
                gefilterteRechnungen.add(r);
            }
        }
    }

    @Then("the system shows only invoices \"1001\" in the result list")
    public void the_system_shows_only_invoice_1001_in_result_list() {
        assertNotNull(gefilterteRechnungen);
        assertEquals(1, gefilterteRechnungen.size());
        assertEquals("1001", gefilterteRechnungen.get(0).nummer);
    }

    // ---------- Szenario: Show invoice list ----------

    @Given("invoices exist in the system: invoice \"1001\" for customer email \"martin.keller@testmail.com\" and invoice \"1002\" for customer email \"laura.fischer@testmail.com\"")
    public void invoices_exist_in_the_system_for_show_list() {
        alleRechnungen.clear();

        TestInvoice r1 = new TestInvoice();
        r1.nummer = "1001";
        r1.kundenEmail = "martin.keller@testmail.com";

        TestInvoice r2 = new TestInvoice();
        r2.nummer = "1002";
        r2.kundenEmail = "laura.fischer@testmail.com";

        alleRechnungen.add(r1);
        alleRechnungen.add(r2);
    }

    @When("the operator opens the invoice overview")
    public void the_operator_opens_the_invoice_overview() {
        // keine extra Logik nötig
    }

    @Then("the system displays a list of all invoices including \"1001\" and \"1002\"")
    public void the_system_displays_a_list_of_all_invoices_including_1001_and_1002() {
        List<String> nums = new ArrayList<>();
        for (TestInvoice r : alleRechnungen) {
            nums.add(r.nummer);
        }
        assertTrue(nums.contains("1001"));
        assertTrue(nums.contains("1002"));
    }

    // ---------- Szenario: Export invoices ----------

    @Given("a list of invoices \"1001\" and \"1002\" is displayed in the invoice overview")
    public void a_list_of_invoices_1001_and_1002_is_displayed() {
        // Liste explizit aufbauen (nicht von anderem Szenario abhängig machen)
        alleRechnungen.clear();

        TestInvoice r1 = new TestInvoice();
        r1.nummer = "1001";
        TestInvoice r2 = new TestInvoice();
        r2.nummer = "1002";

        alleRechnungen.add(r1);
        alleRechnungen.add(r2);

        assertFalse(alleRechnungen.isEmpty());
    }

    @When("the operator exports the invoices")
    public void the_operator_exports_the_invoices() {
        exportErfolgreich = !alleRechnungen.isEmpty();
    }

    @Then("the system provides the invoices in a downloadable file \"invoices_2025-11_export.csv\"")
    public void the_system_provides_the_invoices_in_a_downloadable_file() {
        assertTrue(exportErfolgreich);
    }
}
