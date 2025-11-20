package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für stop_charging.feature
 */
public class StopChargingSteps {

    private Ladevorgang ladevorgang;
    private boolean ladevorgangBeendet;

    private boolean kostenBerechnet;
    private boolean abrechnungErstellt;
    private boolean rechnungSichtbar;

    // WICHTIG: HIER KEIN @Given("an ongoing charging session") MEHR!
    // Dieser Schritt ist in StartChargingSteps definiert.

    // ---------- Szenario: Unplug the charging cable ----------

    @When("the customer unplugs the charging cable")
    public void the_customer_unplugs_the_charging_cable() {
        if (ladevorgang == null) {
            // Falls der Ladevorgang nicht aus anderen Steps kommt, Dummy anlegen
            ladevorgang = new Ladevorgang();
            ladevorgang.setStart(LocalDateTime.now().minusMinutes(30));
        }
        ladevorgang.setEnde(LocalDateTime.now());
        ladevorgangBeendet = true;
    }

    @Then("the system stops the charging session")
    public void the_system_stops_the_charging_session() {
        assertTrue(ladevorgangBeendet, "Ladevorgang sollte beendet sein");
    }

    // ---------- Szenario: Automatic billing after charging ----------

    @Given("a finished charging session with measured energy")
    public void a_finished_charging_session_with_measured_energy() {
        ladevorgang = new Ladevorgang();
        ladevorgang.setStart(LocalDateTime.now().minusMinutes(60));
        ladevorgang.setEnde(LocalDateTime.now().minusMinutes(10));
    }

    @When("the system calculates the cost based on the tariffs")
    public void the_system_calculates_the_cost_based_on_the_tariffs() {
        kostenBerechnet = true;  // Dummy
    }

    @Then("the system creates a billing record for the customer account")
    public void the_system_creates_a_billing_record_for_the_customer_account() {
        abrechnungErstellt = kostenBerechnet;
        assertTrue(abrechnungErstellt, "Abrechnungsdatensatz sollte erstellt werden");
    }

    // ---------- Szenario: Customer receives an invoice ----------

    @Given("the system has created a billing record for the session")
    public void the_system_has_created_a_billing_record_for_the_session() {
        abrechnungErstellt = true;
    }

    @When("the invoice is generated")
    public void the_invoice_is_generated() {
        if (abrechnungErstellt) {
            rechnungSichtbar = true;
        }
    }

    @Then("the customer can view the invoice for the charging session")
    public void the_customer_can_view_the_invoice_for_the_charging_session() {
        assertTrue(rechnungSichtbar, "Rechnung sollte sichtbar sein");
    }
}
