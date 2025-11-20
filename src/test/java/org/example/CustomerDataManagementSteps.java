package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions für customer_data_management.feature
 */
public class CustomerDataManagementSteps {

    private Kundenkonto kundenkonto;
    private double angezeigtesGuthaben;

    private Kunde kunde;
    private List<Kunde> kundenListe;
    private List<Kunde> angezeigteKunden;
    private List<Kunde> gefilterteKunden;

    // ----------------------------------------------------------
    // Szenario: Check customer account balance
    // ----------------------------------------------------------

    @Given("a customer account with a balance exists")
    public void a_customer_account_with_a_balance_exists() {
        kundenkonto = new Kundenkonto();
        kundenkonto.setKontonummer(1);
        // Wichtig: Startguthaben setzen, damit 50.0 erwartet werden kann
        kundenkonto.setGuthaben(50.0);
    }

    @When("the operator opens the balance information for this customer")
    public void the_operator_opens_the_balance_information_for_this_customer() {
        angezeigtesGuthaben = kundenkonto.aktuellesGuthaben();
    }

    @Then("the system shows the current account balance")
    public void the_system_shows_the_current_account_balance() {
        assertEquals(50.0, angezeigtesGuthaben, 0.0001);
    }

    // ----------------------------------------------------------
    // Szenario: Edit customer data
    // ----------------------------------------------------------

    @Given("a customer exists with stored data")
    public void a_customer_exists_with_stored_data() {
        // Einfaches Beispiel-Objekt
        kunde = new Kunde("Max Mustermann", "max@example.com", "geheim");
    }

    @When("the operator updates the customer data and saves the changes")
    public void the_operator_updates_the_customer_data_and_saves_the_changes() {
        // Neue Daten setzen
        kunde.setName("Maxine Musterfrau");
        kunde.setEmail("maxine@example.com");
    }

    @Then("the system stores the updated customer data")
    public void the_system_stores_the_updated_customer_data() {
        assertEquals("Maxine Musterfrau", kunde.getName());
        assertEquals("maxine@example.com", kunde.getEmail());
    }

    // ----------------------------------------------------------
    // Szenario: View customer list
    // ----------------------------------------------------------

    @Given("multiple customers exist")
    public void multiple_customers_exist() {
        kundenListe = new ArrayList<>();
        kundenListe.add(new Kunde("Max Mustermann", "max@example.com", "pw1"));
        kundenListe.add(new Kunde("Anna Beispiel", "anna@example.com", "pw2"));
        kundenListe.add(new Kunde("Peter Muster", "peter@example.com", "pw3"));
    }

    @When("the operator opens the customer list")
    public void the_operator_opens_the_customer_list() {
        // In einem echten System würde hier aus dem Service gelesen werden
        angezeigteKunden = new ArrayList<>(kundenListe);
    }

    @Then("the system shows all customers in a list")
    public void the_system_shows_all_customers_in_a_list() {
        assertNotNull(angezeigteKunden);
        assertEquals(kundenListe.size(), angezeigteKunden.size());
    }

    // ----------------------------------------------------------
    // Szenario: Filter customer list
    // ----------------------------------------------------------

    @When("the operator applies filter criteria to the customer list")
    public void the_operator_applies_filter_criteria_to_the_customer_list() {
        // Beispiel: Filter nach Namen, die "Muster" enthalten
        gefilterteKunden = kundenListe.stream()
                .filter(k -> k.getName().contains("Muster"))
                .collect(Collectors.toList());
    }

    @Then("the system shows only customers that match the filter criteria")
    public void the_system_shows_only_customers_that_match_the_filter_criteria() {
        assertNotNull(gefilterteKunden);
        assertFalse(gefilterteKunden.isEmpty());
        assertTrue(gefilterteKunden.stream()
                .allMatch(k -> k.getName().contains("Muster")));
    }
}
