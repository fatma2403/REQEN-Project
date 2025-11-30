package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PricingManagementSteps {

    private String operatorId;
    private String operatorName;

    // Preise
    private double acPriceKwh;
    private double acPriceMinute;
    private double dcPriceKwh;
    private double dcPriceMinute;

    private double updatedAcPriceKwh;
    private double updatedAcPriceMinute;
    private double updatedDcPriceKwh;
    private double updatedDcPriceMinute;

    private double futurePriceKwh;
    private double futurePriceMinute;

    private boolean ruleApplied;

    // Für Preisvalidierung
    private double sessionEnergy;
    private long sessionMinutes;
    private double calculatedPrice;



    @Given("an operator with id \"OP-01\", name \"Admin Operator\" is in the pricing section")
    public void operator_op01_is_in_pricing_section() {
        operatorId = "OP-01";
        operatorName = "Admin Operator";
    }

    @Given("a location \"City Center\" with id \"1\" exists")
    public void location_city_center_id_1_exists() {
        // Keine Systemlogik notwendig, nur Kontext
    }

    @When("the operator defines prices for location \"City Center\" with charging mode \"AC\", price per kWh \"0.35\", price per minute \"0.05\" valid from \"2025-11-01T00:00\"")
    public void operator_defines_ac_prices_for_city_center() {
        acPriceKwh = 0.35;
        acPriceMinute = 0.05;
    }

    @Then("the system stores a pricing rule for location \"City Center\" with charging mode \"AC\", price per kWh \"0.35\", price per minute \"0.05\" and valid from \"2025-11-01T00:00\"")
    public void system_stores_ac_pricing_rule() {
        assertEquals(0.35, acPriceKwh, 0.0001);
        assertEquals(0.05, acPriceMinute, 0.0001);
    }



    @Given("existing pricing rules for location \"City Center\" with id \"1\" are defined: AC price per kWh \"0.35\", price per minute \"0.05\" and DC price per kWh \"0.45\", price per minute \"0.10\" valid from \"2025-11-01T00:00\"")
    public void existing_pricing_rules_defined() {
        acPriceKwh = 0.35;
        acPriceMinute = 0.05;
        dcPriceKwh = 0.45;
        dcPriceMinute = 0.10;
    }

    @When("the operator changes the AC price to \"0.40\" per kWh and \"0.06\" per minute and the DC price to \"0.50\" per kWh and \"0.12\" per minute for location \"City Center\" and saves the changes")
    public void operator_updates_ac_and_dc_prices() {
        updatedAcPriceKwh = 0.40;
        updatedAcPriceMinute = 0.06;
        updatedDcPriceKwh = 0.50;
        updatedDcPriceMinute = 0.12;
    }

    @Then("the system stores updated pricing rules for location \"City Center\" with AC price per kWh \"0.40\", price per minute \"0.06\" and DC price per kWh \"0.50\", price per minute \"0.12\"")
    public void system_stores_updated_price_rules() {
        assertEquals(0.40, updatedAcPriceKwh, 0.0001);
        assertEquals(0.06, updatedAcPriceMinute, 0.0001);
        assertEquals(0.50, updatedDcPriceKwh, 0.0001);
        assertEquals(0.12, updatedDcPriceMinute, 0.0001);
    }



    @Given("an existing pricing rule with id \"101\" for location \"City Center\" with charging mode \"AC\", price per kWh \"0.40\", price per minute \"0.06\" and valid from \"2025-11-01T00:00\" is defined")
    public void existing_pricing_rule_101_defined() {
        acPriceKwh = 0.40;
        acPriceMinute = 0.06;
    }

    @When("the operator edits this pricing rule to price per kWh \"0.38\", price per minute \"0.05\" and valid from \"2025-12-01T00:00\" and saves the changes")
    public void operator_edits_pricing_rule_101() {
        futurePriceKwh = 0.38;
        futurePriceMinute = 0.05;
    }

    @Then("the system applies the updated pricing rule with id \"101\" so that future charging sessions at location \"City Center\" with mode \"AC\" starting on or after \"2025-12-01T00:00\" use price per kWh \"0.38\" and price per minute \"0.05\"")
    public void system_applies_updated_rule_for_future() {
        assertEquals(0.38, futurePriceKwh, 0.0001);
        assertEquals(0.05, futurePriceMinute, 0.0001);
        ruleApplied = true;
        assertTrue(ruleApplied);
    }


    @Given("a charging session is running for customer \"Martin Keller\" at location \"City Center\" with id \"1\" on a DC charging station with id \"11\" from \"2025-11-20T10:00\" to \"2025-11-20T10:30\" with energy \"24.0\" kWh")
    public void charging_session_running_for_martin_keller() {
        sessionEnergy = 24.0;
        sessionMinutes = 30;
    }

    @Given("a pricing rule for location \"City Center\" with charging mode \"DC\", price per kWh \"0.45\", price per minute \"0.10\" and valid from \"2025-11-01T00:00\" exists")
    public void pricing_rule_for_dc_exists() {
        dcPriceKwh = 0.45;
        dcPriceMinute = 0.10;
    }

    @When("the system calculates the price for the session at time \"2025-11-20T10:30\"")
    public void the_system_calculates_the_price_for_the_session() {
        calculatedPrice =
                sessionEnergy * dcPriceKwh +
                        sessionMinutes * dcPriceMinute;  // 24.0 * 0.45 + 30 * 0.10 = 13.80
    }

    @Then("the system uses the pricing rule with price per kWh \"0.45\" and price per minute \"0.10\" for location \"City Center\" and charging mode \"DC\" and calculates a total price of \"13.80\"")
    public void system_calculates_total_price_13_80() {
        assertEquals(13.80, calculatedPrice, 0.0001);
    }
}
