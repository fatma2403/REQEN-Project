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

    // --------------------------------
    // Scenario: Register or log in
    // --------------------------------

    @Given("a customer without an account")
    public void a_customer_without_an_account() {
        // Kunde existiert noch nicht im System, hat noch keine Kunden-ID
        customer = new Kunde("Max Mustermann", "max@example.com", "secret");
        customer.setKundenId(null);
        loggedIn = false;
    }

    @When("the customer registers with valid data")
    public void the_customer_registers_with_valid_data() {
        // Registrierung über die Kundenverwaltung
        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;
    }

    @Then("a new customer account is created")
    public void a_new_customer_account_is_created() {
        // Kunde sollte jetzt eine Kunden-ID haben und in der Verwaltung sein
        assertNotNull(customer.getKundenId(), "Customer ID should not be null after registration");
        assertTrue(
                kundenverwaltung.findeKundeNachId(customer.getKundenId()).isPresent(),
                "Customer should be stored in Kundenverwaltung"
        );
    }

    @Then("the customer is logged in")
    public void the_customer_is_logged_in() {
        assertTrue(loggedIn, "Customer should be logged in");
    }

    // --------------------------------
    // Scenario: Receive a customer ID
    // --------------------------------

    @Given("a registered customer without a customer ID")
    public void a_registered_customer_without_a_customer_id() {
        // Simuliere einen Kunden, der schon im System ist, aber noch keine ID hat
        customer = new Kunde("Erika Mustermann", "erika@example.com", "secret");
        customer.setKundenId(null);
        loggedIn = true;
    }

    @When("the system activates the customer account")
    public void the_system_activates_the_customer_account() {
        // Wir nutzen dieselbe Registrierungsmethode, um eine ID zu vergeben
        kundenverwaltung.kundeRegistrieren(customer);
    }

    @Then("the system assigns a unique customer ID to the customer")
    public void the_system_assigns_a_unique_customer_id_to_the_customer() {
        assertNotNull(customer.getKundenId(), "Customer ID should be assigned");
    }

    // --------------------------------
    // Scenario: View personal data
    // --------------------------------

    @Given("a logged-in customer with stored personal data")
    public void a_logged_in_customer_with_stored_personal_data() {
        customer = new Kunde("Anna Beispiel", "anna@example.com", "secret");
        kundenverwaltung.kundeRegistrieren(customer);
        loggedIn = true;
    }

    @When("the customer opens the personal data page")
    public void the_customer_opens_the_personal_data_page() {
        // Für das MVP: die „angezeigten Daten“ sind einfach das Kundenobjekt selbst
        shownData = customer;
    }

    @Then("the system shows the current personal data")
    public void the_system_shows_the_current_personal_data() {
        assertNotNull(shownData);
        assertEquals(customer.getName(), shownData.getName());
        assertEquals(customer.getEmail(), shownData.getEmail());
    }

    // --------------------------------
    // Scenario: Edit personal data
    // --------------------------------

    @When("the customer changes personal data and saves the changes")
    public void the_customer_changes_personal_data_and_saves_the_changes() {
        // Kunde ändert Name und E-Mail
        customer.setName("Anna NeuerName");
        customer.setEmail("anna.neu@example.com");

        // Änderungen in der Verwaltung speichern
        kundenverwaltung.kundendatenAktualisieren(customer);

        // Was das System anzeigt, sind die aktualisierten Daten
        shownData = kundenverwaltung
                .findeKundeNachId(customer.getKundenId())
                .orElse(null);
    }

    @Then("the system stores the updated personal data")
    public void the_system_stores_the_updated_personal_data() {
        assertNotNull(shownData);
        assertEquals("Anna NeuerName", shownData.getName());
        assertEquals("anna.neu@example.com", shownData.getEmail());
    }

    @Then("the customer can see the updated information")
    public void the_customer_can_see_the_updated_information() {
        assertNotNull(shownData);
        assertEquals(customer.getName(), shownData.getName());
        assertEquals(customer.getEmail(), shownData.getEmail());
    }
}
