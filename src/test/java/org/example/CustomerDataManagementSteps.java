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

    @Given("a customer account exists for name \"Martin Keller\" with email \"martin.keller@testmail.com\" and a balance of \"75.50\"")
    public void a_customer_account_exists_for_martin_keller_with_balance() {
        kunde = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
        kundenkonto = new Kundenkonto();
        kundenkonto.setKontonummer(1);
        kundenkonto.setGuthaben(75.50);
    }

    @When("the operator opens the balance information for customer with email \"martin.keller@testmail.com\"")
    public void the_operator_opens_the_balance_information_for_martin_keller() {
        assertEquals("martin.keller@testmail.com", kunde.getEmail());
        angezeigtesGuthaben = kundenkonto.aktuellesGuthaben();
    }

    @Then("the system shows the current account balance \"75.50\" for customer with email \"martin.keller@testmail.com\"")
    public void the_system_shows_the_current_account_balance_75_50_for_martin_keller() {
        assertEquals("martin.keller@testmail.com", kunde.getEmail());
        assertEquals(75.50, angezeigtesGuthaben, 0.0001);
    }

    // ----------------------------------------------------------
    // Szenario: Edit customer data
    // ----------------------------------------------------------

    @Given("a customer exists with stored data: name \"Martin Keller\" and email \"martin.keller@testmail.com\"")
    public void a_customer_exists_with_stored_data_martin_keller() {
        kunde = new Kunde("Martin Keller", "martin.keller@testmail.com", "Secure456!");
    }

    @When("the operator updates the customer email to \"martin.keller.new@testmail.com\" and saves the changes")
    public void the_operator_updates_the_customer_email_and_saves_the_changes() {
        kunde.setEmail("martin.keller.new@testmail.com");
        // hier könnte man optional einen Service-Aufruf simulieren
    }

    @Then("the system stores the updated customer data")
    public void the_system_stores_the_updated_customer_data() {
        assertEquals("Martin Keller", kunde.getName());
        assertEquals("martin.keller.new@testmail.com", kunde.getEmail());
    }

    @Then("the customer data shows email \"martin.keller.new@testmail.com\"")
    public void the_customer_data_shows_email_martin_keller_new() {
        assertEquals("martin.keller.new@testmail.com", kunde.getEmail());
    }

    // ----------------------------------------------------------
    // Szenario: View customer list
    // ----------------------------------------------------------

    // Variante ohne Leerzeichen nach dem Doppelpunkt (wie im View-Szenario)
    @Given("multiple customers exist:\"Martin Keller\" with email \"martin.keller@testmail.com\", \"Laura Fischer\" with email \"laura.fischer@testmail.com\", \"Jonas Weber\" with email \"jonas.weber@testmail.com\"")
    public void multiple_customers_exist_no_space() {
        initialisiereMehrereKunden();
    }

    // Variante mit Leerzeichen nach dem Doppelpunkt (wie im Filter-Szenario)
    @Given("multiple customers exist: \"Martin Keller\" with email \"martin.keller@testmail.com\", \"Laura Fischer\" with email \"laura.fischer@testmail.com\", \"Jonas Weber\" with email \"jonas.weber@testmail.com\"")
    public void multiple_customers_exist_with_space() {
        initialisiereMehrereKunden();
    }

    private void initialisiereMehrereKunden() {
        kundenListe = new ArrayList<>();
        kundenListe.add(new Kunde("Martin Keller", "martin.keller@testmail.com", "pw1"));
        kundenListe.add(new Kunde("Laura Fischer", "laura.fischer@testmail.com", "pw2"));
        kundenListe.add(new Kunde("Jonas Weber", "jonas.weber@testmail.com", "pw3"));
    }

    @When("the operator opens the customer list")
    public void the_operator_opens_the_customer_list() {
        angezeigteKunden = new ArrayList<>(kundenListe);
    }

    @Then("the system shows all customers in a list including: \"Martin Keller\",\"Laura Fischer\",\"Jonas Weber\"")
    public void the_system_shows_all_customers_in_a_list_including_names() {
        assertNotNull(angezeigteKunden);
        assertEquals(3, angezeigteKunden.size());

        List<String> namen = angezeigteKunden.stream()
                .map(Kunde::getName)
                .collect(Collectors.toList());

        assertTrue(namen.contains("Martin Keller"));
        assertTrue(namen.contains("Laura Fischer"));
        assertTrue(namen.contains("Jonas Weber"));
    }

    // ----------------------------------------------------------
    // Szenario: Filter customer list
    // ----------------------------------------------------------

    @When("the operator applies the filter \"Keller\" to the customer list")
    public void the_operator_applies_the_filter_keller_to_the_customer_list() {
        gefilterteKunden = kundenListe.stream()
                .filter(k -> k.getName().contains("Keller"))
                .collect(Collectors.toList());
    }

    @Then("the system shows only customers with name containing \"Keller\"")
    public void the_system_shows_only_customers_with_name_containing_keller() {
        assertNotNull(gefilterteKunden);
        assertFalse(gefilterteKunden.isEmpty());
        assertTrue(gefilterteKunden.stream()
                .allMatch(k -> k.getName().contains("Keller")));
    }
}
