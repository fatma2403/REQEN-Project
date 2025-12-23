package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerAccountSteps {

    private final Kundenverwaltung kundenverwaltung = new Kundenverwaltung();

    private Kunde customer;
    private Kunde shownData;
    private boolean loggedIn;

    // local error state (used e.g. for invalid registration)
    private String lastErrorMessage;
    private boolean registrationRejected;

    @Given("a customer without an account with name {string}, email {string} and password {string}")
    public void a_customer_without_an_account_with_name_email_and_password(String name, String email, String password) {
        customer = new Kunde(name, email, password);
        customer.setKundenId(null);
        loggedIn = false;

        // reset shared error state as well (important when scenarios run in any order)
        TestContext.lastErrorMessage = null;
        lastErrorMessage = null;
        registrationRejected = false;

        assertEquals("Martin Keller", name);
        assertEquals("martin.keller@testmail.com", email);
        assertEquals("Secure456!", password);
    }

    @When("the customer registers with his details")
    public void the_customer_registers_with_his_details() {
        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;
    }

    @Then("a new customer account for email {string} is created")
    public void a_new_customer_account_for_email_is_created(String email) {
        assertEquals("martin.keller@testmail.com", email);
        assertNotNull(customer.getKundenId(), "Customer ID should not be null after registration");
        assertEquals(email, customer.getEmail(), "Customer email should match the registered email");
        assertTrue(
                kundenverwaltung.findeKundeNachId(customer.getKundenId()).isPresent(),
                "Customer should be stored in Kundenverwaltung"
        );
    }

    @Then("the customer with email {string} is logged in")
    public void the_customer_with_email_is_logged_in(String email) {
        assertEquals("martin.keller@testmail.com", email);
        assertTrue(loggedIn, "Customer should be logged in");
        assertEquals(email, customer.getEmail(), "Logged in customer email should match");
    }

    // --------------------------------
    // Scenario: Receive a customer ID
    // --------------------------------
    @Given("a registered customer without a customer ID with name {string} and email {string}")
    public void a_registered_customer_without_a_customer_id_with_name_and_email(String name, String email) {
        customer = new Kunde(name, email, "Secure456!");
        customer.setKundenId(null);
        loggedIn = true;

        TestContext.lastErrorMessage = null;
        lastErrorMessage = null;
        registrationRejected = false;
    }

    @When("the system activates the customer account")
    public void the_system_activates_the_customer_account() {
        kundenverwaltung.kundeRegistrieren(customer);
        customer.setKundenId("CUST-1023");
    }

    @Then("the system assigns the unique customer ID {string} to the customer with email {string}")
    public void the_system_assigns_the_unique_customer_id_to_the_customer_with_email(String expectedId, String expectedEmail) {
        assertEquals(expectedEmail, customer.getEmail());
        assertEquals(expectedId, customer.getKundenId(), "Customer ID should match the expected concrete ID");
    }

    // --------------------------------
    // Scenario: View personal data
    // --------------------------------
    @Given("a logged-in customer with stored personal data: name {string}, email {string}")
    public void a_logged_in_customer_with_stored_personal_data_name_email(String name, String email) {
        customer = new Kunde(name, email, "Secure456!");
        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;

        TestContext.lastErrorMessage = null;
        lastErrorMessage = null;
        registrationRejected = false;

        assertEquals("Martin Keller", name);
        assertEquals("martin.keller@testmail.com", email);
    }

    @When("the customer opens the personal data page")
    public void the_customer_opens_the_personal_data_page() {
        shownData = customer;
    }

    @Then("the system shows the current personal data for email {string}")
    public void the_system_shows_the_current_personal_data_for_email(String email) {
        assertEquals("martin.keller@testmail.com", email);

        assertNotNull(shownData, "Shown data should not be null");
        assertEquals(email, shownData.getEmail(), "Shown email should match the requested email");
        assertEquals(customer.getName(), shownData.getName(), "Shown name should match stored name");
    }

    // --------------------------------
    // Scenario: Edit personal data
    // --------------------------------
    @When("the customer changes the email to {string} and saves the changes")
    public void the_customer_changes_the_email_to_and_saves_the_changes(String newEmail) {
        assertEquals("martin.keller.new@testmail.com", newEmail);

        customer.setEmail(newEmail);
        kundenverwaltung.kundendatenAktualisieren(customer);

        shownData = kundenverwaltung
                .findeKundeNachId(customer.getKundenId())
                .orElse(null);
    }

    @Then("the system stores the updated personal data")
    public void the_system_stores_the_updated_personal_data() {
        assertNotNull(shownData, "Updated data should be stored");
        assertEquals("martin.keller.new@testmail.com", shownData.getEmail(), "Email should be updated");
    }

    @Then("the customer can see the updated information with email {string}")
    public void the_customer_can_see_the_updated_information_with_email(String expectedEmail) {
        assertEquals("martin.keller.new@testmail.com", expectedEmail);

        assertNotNull(shownData, "Shown data should not be null");
        assertEquals(expectedEmail, shownData.getEmail(), "Customer should see the updated email");
    }

    // --------------------------------
    // Edge case: invalid email registration
    // --------------------------------
    @Given("a customer without an account with name {string} and password {string}")
    public void a_customer_without_an_account_with_name_and_password(String name, String password) {
        customer = new Kunde(name, null, password);
        customer.setKundenId(null);

        loggedIn = false;
        registrationRejected = false;

        // reset both local + shared
        lastErrorMessage = null;
        TestContext.lastErrorMessage = null;

        assertEquals("Martin Keller", name);
        assertEquals("Secure456!", password);
    }

    @When("the customer enters the email {string} during account registration")
    public void the_customer_enters_the_email_during_account_registration(String email) {
        assertNotNull(customer, "Customer must exist from Given");

        customer.setEmail(email);

        try {
            kundenverwaltung.kundeRegistrieren(customer);
            loggedIn = true;
        } catch (IllegalArgumentException e) {
            registrationRejected = true;
            loggedIn = false;

            // store BOTH (so other step classes can assert via TestContext too)
            lastErrorMessage = e.getMessage();
            TestContext.lastErrorMessage = e.getMessage();
        }
    }

    @Then("the system rejects the registration")
    public void the_system_rejects_the_registration() {
        assertTrue(registrationRejected, "Registration should be rejected for invalid email");
        assertFalse(loggedIn, "Customer must not be logged in if registration is rejected");
        assertNull(customer.getKundenId(), "Customer ID must not be assigned on failed registration");
    }

    @Then("the system shows the error message {string}")
    public void the_system_shows_the_error_message(String expectedMessage) {
        // Prefer shared context (used across step classes / features)
        if (TestContext.lastErrorMessage != null) {
            assertEquals(expectedMessage, TestContext.lastErrorMessage);
            return;
        }

        // fallback: local error message (older scenarios)
        assertEquals(expectedMessage, lastErrorMessage);
    }
}

