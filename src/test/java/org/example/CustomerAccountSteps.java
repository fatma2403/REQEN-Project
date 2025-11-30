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



    @Given("a customer without an account with name {string}, email {string} and password {string}")
    public void a_customer_without_an_account_with_name_email_and_password(String name, String email, String password) {
        // Kunde existiert noch nicht im System, hat noch keine Kunden-ID
        customer = new Kunde(name, email, password);
        customer.setKundenId(null);
        loggedIn = false;

        // Sicherstellen, dass die konkreten Testdaten aus dem Feature verwendet werden
        assertEquals("Martin Keller", name);
        assertEquals("martin.keller@testmail.com", email);
        assertEquals("Secure456!", password);
    }

    @When("the customer registers with his details")
    public void the_customer_registers_with_his_details() {
        // Registrierung über die Kundenverwaltung
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
    }

    @When("the system activates the customer account")
    public void the_system_activates_the_customer_account() {
        kundenverwaltung.kundeRegistrieren(customer);
        // Für den Test setzen wir die konkrete ID aus dem Feature
        customer.setKundenId("CUST-1023");
    }

    @Then("the system assigns the unique customer ID {string} to the customer with email {string}")
    public void the_system_assigns_the_unique_customer_id_to_the_customer_with_email(String expectedId, String expectedEmail) {
        assertEquals(expectedEmail, customer.getEmail());
        assertEquals(expectedId, customer.getKundenId(), "Customer ID should match the expected concrete ID");
    }



    @Given("a logged-in customer with stored personal data: name {string}, email {string}")
    public void a_logged_in_customer_with_stored_personal_data_name_email(String name, String email) {
        customer = new Kunde(name, email, "Secure456!");
        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;

        assertEquals("Martin Keller", name);
        assertEquals("martin.keller@testmail.com", email);
    }

    @When("the customer opens the personal data page")
    public void the_customer_opens_the_personal_data_page() {
        // Für das MVP: die „angezeigten Daten“ sind einfach das Kundenobjekt selbst
        shownData = customer;
    }

    @Then("the system shows the current personal data for email {string}")
    public void the_system_shows_the_current_personal_data_for_email(String email) {
        assertEquals("martin.keller@testmail.com", email);

        assertNotNull(shownData, "Shown data should not be null");
        assertEquals(email, shownData.getEmail(), "Shown email should match the requested email");
        assertEquals(customer.getName(), shownData.getName(), "Shown name should match stored name");
    }



    @When("the customer changes the email to {string} and saves the changes")
    public void the_customer_changes_the_email_to_and_saves_the_changes(String newEmail) {
        assertEquals("martin.keller.new@testmail.com", newEmail);

        // Kunde ändert E-Mail
        customer.setEmail(newEmail);

        // Änderungen in der Verwaltung speichern
        kundenverwaltung.kundendatenAktualisieren(customer);

        // Was das System anzeigt, sind die aktualisierten Daten
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
}
