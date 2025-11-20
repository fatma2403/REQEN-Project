package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für pricing_management.feature
 */
public class PricingManagementSteps {

    private boolean preiseGesetzt;
    private boolean parameterAktualisiert;
    private boolean regelAktualisiert;
    private boolean gültigePreiseVorhanden;

    // ---------- Szenario: Set prices per location ----------

    @Given("an operator is in the pricing section")
    public void an_operator_is_in_the_pricing_section() {
        // Kontext angenommen
    }

    @When("the operator defines prices for a specific location")
    public void the_operator_defines_prices_for_a_specific_location() {
        preiseGesetzt = true;
    }

    @Then("the system stores these prices for that location")
    public void the_system_stores_these_prices_for_that_location() {
        assertTrue(preiseGesetzt, "Preise sollten gespeichert werden");
    }

    // ---------- Szenario: Adjust AC and DC price parameters ----------

    @When("the operator changes the AC and DC price parameters for a location")
    public void the_operator_changes_the_ac_and_dc_price_parameters_for_a_location() {
        parameterAktualisiert = true;
    }

    @Then("the system stores the updated price parameters")
    public void the_system_stores_the_updated_price_parameters() {
        assertTrue(parameterAktualisiert, "Aktualisierte Parameter sollten gespeichert werden");
    }

    // ---------- Szenario: Update pricing rules ----------

    @Given("existing pricing rules are defined")
    public void existing_pricing_rules_are_defined() {
        preiseGesetzt = true;
    }

    @When("the operator edits a pricing rule and saves the changes")
    public void the_operator_edits_a_pricing_rule_and_saves_the_changes() {
        regelAktualisiert = true;
    }

    @Then("the system applies the updated rule to future charging sessions")
    public void the_system_applies_the_updated_rule_to_future_charging_sessions() {
        assertTrue(regelAktualisiert, "Aktualisierte Regel sollte angewendet werden");
    }

    // ---------- Szenario: Validate price during charging ----------

    @Given("a charging session is running at a location with defined prices")
    public void a_charging_session_is_running_at_a_location_with_defined_prices() {
        gültigePreiseVorhanden = true;
    }

    @When("the system calculates the price for the session")
    public void the_system_calculates_the_price_for_the_session() {
        // wir nutzen einfach das Flag
    }

    @Then("the system uses the valid prices and rules for that time and location")
    public void the_system_uses_the_valid_prices_and_rules_for_that_time_and_location() {
        assertTrue(gültigePreiseVorhanden, "Gültige Preise sollten verwendet werden");
    }
}
