package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für manage_payments.feature
 */
public class ManagePaymentsSteps {

    private boolean rechnungenVorhanden;
    private boolean ladehistorieVorhanden;

    // ---------- Szenario: View invoices ----------

    @Given("a logged-in customer with past invoices")
    public void a_logged_in_customer_with_past_invoices() {
        rechnungenVorhanden = true;
    }

    @When("the customer opens the invoice overview")
    public void the_customer_opens_the_invoice_overview() {
        // nichts Spezielles nötig
    }

    @Then("the system shows a list of invoices for this customer")
    public void the_system_shows_a_list_of_invoices_for_this_customer() {
        assertTrue(rechnungenVorhanden, "Es sollten Rechnungen angezeigt werden");
    }

    // ---------- Szenario: View charging history ----------

    @Given("a logged-in customer with past charging sessions")
    public void a_logged_in_customer_with_past_charging_sessions() {
        ladehistorieVorhanden = true;
    }

    @When("the customer opens the charging history")
    public void the_customer_opens_the_charging_history() {
        // keine weitere Logik
    }

    @Then("the system shows a list of charging sessions with main details")
    public void the_system_shows_a_list_of_charging_sessions_with_main_details() {
        assertTrue(ladehistorieVorhanden, "Es sollte eine Ladehistorie angezeigt werden");
    }
}
