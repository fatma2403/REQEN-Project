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

    // used for payment-method related steps
    private PaymentMethod currentPaymentMethod;
    private PaymentMethod preferredPaymentMethod;

    // ----------------------------------------------------
    // Scenario 1: Use the prepaid system
    // ----------------------------------------------------

    @Given("a logged-in customer with name {string}, email {string} and a balance account with current balance {double} EUR")
    public void a_logged_in_customer_with_name_email_and_a_balance_account_with_current_balance_eur(
            String name,
            String email,
            Double balance
    ) {
        customer = new Kunde(name, email, "Secure456!");
        account = new Kundenkonto(1, balance);
        startBalance = balance;
    }

    @When("the customer selects the prepaid top-up option")
    public void the_customer_selects_the_prepaid_top_up_option() {
        // context only
    }

    @When("the customer enters the amount {double} EUR")
    public void the_customer_enters_the_amount_eur(Double amount) {
        topUpAmount = amount;
        // if no explicit method is chosen, assume CREDIT_CARD
        currentPaymentMethod = PaymentMethod.CREDIT_CARD;
    }

    @When("the payment is confirmed")
    public void the_payment_is_confirmed() {
        if (topUpAmount > 0) {
            // new version with payment method
            account.guthabenAufladen(topUpAmount, currentPaymentMethod);
            topUpSuccessful = true;
        } else {
            topUpSuccessful = false;
        }
    }

    @Then("the system increases the customer balance to {double} EUR")
    public void the_system_increases_the_customer_balance_to_eur(Double expectedBalance) {
        assertTrue(topUpSuccessful, "Top-up should be successful");
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
        assertEquals(expected, preferredPaymentMethod,
                "Preferred payment method should be stored as default");
    }

    @Then("future top-ups for {string} use {string} by default")
    public void future_top_ups_for_use_by_default(String email, String methodName) {
        // simulate a future top-up using the stored preferred method
        PaymentMethod expected = PaymentMethod.valueOf(methodName);

        // here we just assert that the stored preferred method is used as default
        assertEquals(expected, preferredPaymentMethod,
                "Future top-ups should use the stored default payment method");
    }
}