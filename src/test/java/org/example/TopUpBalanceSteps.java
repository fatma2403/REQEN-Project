package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class TopUpBalanceSteps {

    private Kunde customer;
    private Kundenkonto account;

    private double startBalance;
    private double topUpAmount;
    private boolean topUpSuccessful;

    private PaymentMethod currentPaymentMethod;
    private PaymentMethod preferredPaymentMethod;

    // ----------------------------------------------------
    // Given (Scenario 2 uses this shorter login step)
    // ----------------------------------------------------
    @Given("a logged-in customer with name {string} and email {string}")
    public void a_logged_in_customer_with_name_and_email(String name, String email) {
        customer = new Kunde(name, email, "Secure456!");

        // create a default account so later steps never hit null
        account = new Kundenkonto(1, 0.0);
        startBalance = 0.0;

        topUpAmount = 0.0;
        topUpSuccessful = false;
        currentPaymentMethod = null;
        preferredPaymentMethod = null;
    }

    // ----------------------------------------------------
    // Scenario 1: Use the prepaid system
    // ----------------------------------------------------
    @Given("a logged-in customer with name {string}, email {string} and a balance account with current balance {double} EUR")
    public void a_logged_in_customer_with_name_email_and_balance(
            String name,
            String email,
            Double balance
    ) {
        customer = new Kunde(name, email, "Secure456!");
        account = new Kundenkonto(1, balance);
        startBalance = balance;

        topUpAmount = 0.0;
        topUpSuccessful = false;
        preferredPaymentMethod = null;
        currentPaymentMethod = null;
    }

    @When("the customer selects the prepaid top-up option")
    public void the_customer_selects_the_prepaid_top_up_option() {
        assertNotNull(account, "Account must exist");
    }

    @When("the customer enters the amount {double} EUR")
    public void the_customer_enters_the_amount(Double amount) {
        topUpAmount = amount;
        currentPaymentMethod = PaymentMethod.CREDIT_CARD;
    }

    @When("the payment is confirmed")
    public void the_payment_is_confirmed() {
        if (topUpAmount > 0) {
            account.guthabenAufladen(topUpAmount, currentPaymentMethod);
            topUpSuccessful = true;
        } else {
            topUpSuccessful = false;
        }
    }

    @Then("the system increases the customer balance to {double} EUR")
    public void the_system_increases_the_customer_balance_to(Double expectedBalance) {
        assertTrue(topUpSuccessful, "Top-up should be successful");
        assertEquals(expectedBalance, account.aktuellesGuthaben(), 0.0001);
    }

    // ----------------------------------------------------
    // Negative / error case
    // ----------------------------------------------------
    @Then("the top up is rejected")
    public void the_top_up_is_rejected() {
        assertFalse(topUpSuccessful, "Top-up must be rejected");
    }

    @Then("the system keeps the customer balance at {double} EUR")
    public void the_system_keeps_the_customer_balance_at(Double expectedBalance) {
        assertNotNull(account, "Account must exist");
        assertEquals(expectedBalance, account.aktuellesGuthaben(), 0.0001);
    }

    // ----------------------------------------------------
    // Scenario 2: Set preferred payment method
    // ----------------------------------------------------
    @When("the customer selects {string} as preferred payment method")
    public void the_customer_selects_as_preferred_payment_method(String methodName) {
        preferredPaymentMethod = PaymentMethod.valueOf(methodName);
    }

    @Then("the system stores {string} as the default payment method")
    public void the_system_stores_as_the_default_payment_method(String methodName) {
        PaymentMethod expected = PaymentMethod.valueOf(methodName);
        assertEquals(expected, preferredPaymentMethod);
    }

    @Then("future top-ups for {string} use {string} by default")
    public void future_top_ups_for_use_by_default(String email, String methodName) {
        PaymentMethod expected = PaymentMethod.valueOf(methodName);

        assertNotNull(customer, "Customer must exist");
        assertEquals(email, customer.getEmail(), "Email must match");
        assertEquals(expected, preferredPaymentMethod,
                "Future top-ups should use the stored default payment method");
    }
}
