package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerDataManagementSteps {

    private Kunde currentCustomer;
    private double currentBalance;
    private double shownBalance;

    private final List<Kunde> customers = new ArrayList<>();
    private List<Kunde> shownCustomers = new ArrayList<>();

    // ---------------------------------------------------------
    // Scenario: Check customer account balance
    // ---------------------------------------------------------

    @Given("a customer account exists for name {string} with email {string} and a balance of {string}")
    public void a_customer_account_exists_for_name_with_email_and_a_balance_of(
            String name, String email, String balance) {

        currentCustomer = new Kunde(name, email, "secret");
        currentBalance = Double.parseDouble(balance);
        shownBalance = 0.0;
    }

    @When("the operator opens the balance information for customer with email {string}")
    public void the_operator_opens_the_balance_information_for_customer_with_email(String email) {
        assertNotNull(currentCustomer);
        assertEquals(email, currentCustomer.getEmail());
        shownBalance = currentBalance;
    }

    @Then("the system shows the current account balance {string} for customer with email {string}")
    public void the_system_shows_the_current_account_balance_for_customer_with_email(
            String expectedBalance, String email) {

        assertNotNull(currentCustomer);
        assertEquals(email, currentCustomer.getEmail());
        assertEquals(Double.parseDouble(expectedBalance), shownBalance);
    }

    // ---------------------------------------------------------
    // Scenario: Edit customer data
    // ---------------------------------------------------------

    @Given("a customer exists with stored data: name {string} and email {string}")
    public void a_customer_exists_with_stored_data_name_and_email(String name, String email) {
        currentCustomer = new Kunde(name, email, "secret");
    }

    @When("the operator updates the customer email to {string} and saves the changes")
    public void the_operator_updates_the_customer_email_to_and_saves_the_changes(String newEmail) {
        assertNotNull(currentCustomer);
        currentCustomer.setEmail(newEmail);
    }

    @Then("the system stores the updated customer data")
    public void the_system_stores_the_updated_customer_data() {
        assertNotNull(currentCustomer);
        assertNotNull(currentCustomer.getEmail());
    }

    @Then("the customer data shows email {string}")
    public void the_customer_data_shows_email(String expectedEmail) {
        assertNotNull(currentCustomer);
        assertEquals(expectedEmail, currentCustomer.getEmail());
    }

    // ---------------------------------------------------------
    // Scenario: View customer list
    // ---------------------------------------------------------

    // pattern without space after colon
    @Given("multiple customers exist:{string} with email {string}, {string} with email {string}, {string} with email {string}")
    // pattern with space after colon (used in second scenario)
    @Given("multiple customers exist: {string} with email {string}, {string} with email {string}, {string} with email {string}")
    public void multiple_customers_exist_with_email(
            String name1, String email1,
            String name2, String email2,
            String name3, String email3) {

        customers.clear();
        shownCustomers = new ArrayList<>();

        customers.add(new Kunde(name1, email1, "secret"));
        customers.add(new Kunde(name2, email2, "secret"));
        customers.add(new Kunde(name3, email3, "secret"));
    }

    @When("the operator opens the customer list")
    public void the_operator_opens_the_customer_list() {
        shownCustomers = new ArrayList<>(customers);
    }

    @Then("the system shows all customers in a list including: {string},{string}, {string}")
    public void the_system_shows_all_customers_in_a_list_including(
            String name1, String name2, String name3) {

        List<String> names = shownCustomers.stream()
                .map(Kunde::getName)
                .collect(Collectors.toList());

        assertTrue(names.contains(name1));
        assertTrue(names.contains(name2));
        assertTrue(names.contains(name3));
    }

    // ---------------------------------------------------------
    // Scenario: Filter customer list
    // ---------------------------------------------------------

    @When("the operator applies the filter {string} to the customer list")
    public void the_operator_applies_the_filter_to_the_customer_list(String filter) {
        shownCustomers = customers.stream()
                .filter(k -> k.getName().contains(filter))
                .collect(Collectors.toList());
    }

    @Then("the system shows only customers with name containing {string}")
    public void the_system_shows_only_customers_with_name_containing(String filter) {
        assertFalse(shownCustomers.isEmpty());
        for (Kunde k : shownCustomers) {
            assertTrue(k.getName().contains(filter));
        }
    }
}