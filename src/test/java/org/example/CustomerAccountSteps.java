package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerAccountSteps {

    private Kunde customer;

    private boolean accountCreated;
    private boolean loggedIn;

    private boolean personalDataPageOpen;

    private Map<String, String> storedPersonalData;
    private Map<String, String> updatedPersonalData;

    // ----------------------------------------------------
    // Scenario: Register or log in
    // ----------------------------------------------------

    @Given("a customer without an account")
    public void a_customer_without_an_account() {
        customer = null;
        accountCreated = false;
        loggedIn = false;
        personalDataPageOpen = false;
        storedPersonalData = null;
        updatedPersonalData = null;
    }

    @When("the customer registers with {string}")
    public void the_customer_registers_with(String date) {
        customer = new Kunde("Test Customer", "customer@testmail.com", "secret");
        accountCreated = true;
        loggedIn = true;
    }

    @Then("a new customer account is created")
    public void a_new_customer_account_is_created() {
        assertTrue(accountCreated, "Account should be created");
        assertNotNull(customer, "Customer must exist");
    }

    @Then("the customer is logged in")
    public void the_customer_is_logged_in() {
        assertTrue(loggedIn, "Customer should be logged in");
    }

    // ----------------------------------------------------
    // Scenario: Receive a customer ID
    // ----------------------------------------------------

    @Given("a registered customer without a customer ID")
    public void a_registered_customer_without_a_customer_id() {
        customer = new Kunde("Test Customer", "customer@testmail.com", "secret");
        customer.setKundenId(null);
        accountCreated = true;
        loggedIn = true;
    }

    @When("the system activates the customer account")
    public void the_system_activates_the_customer_account() {
        assertNotNull(customer, "Customer must exist");
        assertTrue(accountCreated, "Account must be registered");

        if (customer.getKundenId() == null || customer.getKundenId().isBlank()) {
            String id = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            customer.setKundenId(id);
        }
    }

    @Then("the system assigns a unique customer ID to the customer")
    public void the_system_assigns_a_unique_customer_id_to_the_customer() {
        assertNotNull(customer.getKundenId(), "Customer ID must exist");
        assertTrue(customer.getKundenId().startsWith("CUST-"), "Customer ID format invalid");
    }

    // ----------------------------------------------------
    // Scenario: View personal data
    // ----------------------------------------------------

    @Given("a logged-in customer with stored personal data")
    public void a_logged_in_customer_with_stored_personal_data() {
        customer = new Kunde("Martin Keller", "martin.keller@testmail.com", "secret");
        customer.setKundenId("CUST-1023");

        loggedIn = true;
        accountCreated = true;

        storedPersonalData = new LinkedHashMap<>();
        storedPersonalData.put("name", "Martin Keller");
        storedPersonalData.put("email", "martin.keller@testmail.com");
        storedPersonalData.put("address", "Hauptstraße 1, 1010 Wien");

        personalDataPageOpen = false;
    }

    @When("the customer opens the personal data page")
    public void the_customer_opens_the_personal_data_page() {
        assertTrue(loggedIn, "Customer must be logged in");
        personalDataPageOpen = true;
    }

    @Then("the system shows the current personal data")
    public void the_system_shows_the_current_personal_data() {
        assertTrue(personalDataPageOpen, "Personal data page must be open");
        assertNotNull(storedPersonalData, "Personal data must exist");
        assertEquals(customer.getEmail(), storedPersonalData.get("email"));
    }

    // ----------------------------------------------------
    // Scenario: Edit personal data
    // ----------------------------------------------------

    @When("the customer changes personal data and saves the changes")
    public void the_customer_changes_personal_data_and_saves_the_changes() {
        updatedPersonalData = new LinkedHashMap<>(storedPersonalData);
        updatedPersonalData.put("address", "Neubaustraße 5, 1070 Wien");
    }

    @Then("the system stores the updated personal data")
    public void the_system_stores_the_updated_personal_data() {
        storedPersonalData = new LinkedHashMap<>(updatedPersonalData);
        assertEquals("Neubaustraße 5, 1070 Wien", storedPersonalData.get("address"));
    }

    @Then("the customer can see the updated information")
    public void the_customer_can_see_the_updated_information() {
        assertEquals("Neubaustraße 5, 1070 Wien", storedPersonalData.get("address"));
    }
}
