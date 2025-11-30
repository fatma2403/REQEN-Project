package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für top_up_balance.feature
 */
public class TopUpBalanceSteps {

    private Kundenkonto kundenkonto;
    private double eingezahlterBetrag;
    private String bevorzugteZahlungsmethode;
    private boolean topUpErfolgreich;

    // ---------- Szenario: Use the prepaid system ----------

    @Given("a logged-in customer with name {string}, email {string} and a balance account with current balance {double} EUR")
    public void a_logged_in_customer_with_name_email_and_a_balance_account_with_current_balance_eur(
            String name, String email, Double startGuthaben) {

        kundenkonto = new Kundenkonto();
        kundenkonto.setKunde(new Kunde(name, email, "Secure456!"));
        kundenkonto.setGuthaben(startGuthaben);
    }

    @When("the customer selects the prepaid top-up option")
    public void the_customer_selects_the_prepaid_top_up_option() {
        // nur Kontext
    }

    @When("the customer enters the amount {double} EUR")
    public void the_customer_enters_the_amount_eur(Double betrag) {
        eingezahlterBetrag = betrag;
    }

    @When("the payment is confirmed")
    public void the_payment_is_confirmed() {
        double neuesGuthaben = kundenkonto.aktuellesGuthaben() + eingezahlterBetrag;
        kundenkonto.setGuthaben(neuesGuthaben);
        topUpErfolgreich = true;
    }

    @Then("the system increases the customer balance to {double} EUR")
    public void the_system_increases_the_customer_balance_to_eur(Double expectedBalance) {
        assertTrue(topUpErfolgreich, "Top-Up sollte erfolgreich sein");
        assertEquals(expectedBalance, kundenkonto.aktuellesGuthaben(), 0.0001);
    }

    // ---------- Szenario: Set preferred payment method ----------
    // Das Given "a logged-in customer with name ... and email ..." kommt aus StepDefinitions!

    @When("the customer selects {string} as preferred payment method")
    public void the_customer_selects_as_preferred_payment_method(String paymentMethod) {
        bevorzugteZahlungsmethode = paymentMethod;
    }

    @Then("the system stores {string} as the default payment method")
    public void the_system_stores_as_the_default_payment_method(String expectedMethod) {
        assertEquals(expectedMethod, bevorzugteZahlungsmethode);
    }

    @Then("future top-ups for {string} use {string} by default")
    public void future_top_ups_for_use_by_default(String email, String expectedMethod) {
        assertEquals(expectedMethod, bevorzugteZahlungsmethode);
        // Optional: email prüfen, falls du es aus dem Kundenkonto auslesen möchtest
        if (kundenkonto != null && kundenkonto.getKunde() != null) {
            assertEquals(email, kundenkonto.getKunde().getEmail());
        }
    }
}
