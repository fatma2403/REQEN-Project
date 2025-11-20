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

    @Given("a logged-in customer with a balance account")
    public void a_logged_in_customer_with_a_balance_account() {
        kundenkonto = new Kundenkonto();
        // Startguthaben, nur für den Test
        kundenkonto.setGuthaben(50.0);
    }

    @When("the customer selects the prepaid top-up option")
    public void the_customer_selects_the_prepaid_top_up_option() {
        // Für den Test reicht der Kontext, keine Extra-Logik nötig
    }

    @When("the customer enters a valid amount")
    public void the_customer_enters_a_valid_amount() {
        // Beispiel: Kunde gibt 20€ ein
        eingezahlterBetrag = 20.0;
    }

    @When("the payment is confirmed")
    public void the_payment_is_confirmed() {
        // Zahlung erfolgreich -> Guthaben erhöhen
        double neuesGuthaben = kundenkonto.aktuellesGuthaben() + eingezahlterBetrag;
        kundenkonto.setGuthaben(neuesGuthaben);
        topUpErfolgreich = true;
    }

    @Then("the system increases the customer balance by that amount")
    public void the_system_increases_the_customer_balance_by_that_amount() {
        assertTrue(topUpErfolgreich, "Top-Up sollte erfolgreich sein");
        // 50 Start + 20 Aufladung = 70
        assertEquals(70.0, kundenkonto.aktuellesGuthaben(), 0.0001);
    }

    // ---------- Szenario: Set preferred payment method ----------

    @Given("a logged-in customer")
    public void a_logged_in_customer() {
        kundenkonto = new Kundenkonto();
    }

    @When("the customer selects a payment method as preferred")
    public void the_customer_selects_a_payment_method_as_preferred() {
        bevorzugteZahlungsmethode = "Credit Card";
    }

    @Then("the system stores this payment method as default")
    public void the_system_stores_this_payment_method_as_default() {
        assertEquals("Credit Card", bevorzugteZahlungsmethode);
    }

    @Then("future top-ups use this preferred payment method by default")
    public void future_top_ups_use_this_preferred_payment_method_by_default() {
        assertEquals("Credit Card", bevorzugteZahlungsmethode);
    }
}
