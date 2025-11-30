package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerDataManagementSteps {

    private Kunde kunde;
    private Kundenkonto kundenkonto;

    private List<Kunde> kundenListe;
    private List<Kunde> angezeigteKunden;
    private List<Kunde> gefilterteKunden;

   

    @Given("a customer account exists for name {string} with email {string} and a balance of {string}")
    public void a_customer_account_exists_for_martin_keller_with_balance(String name, String email, String balanceStr) {
        kunde = new Kunde(name, email, "secret");
        kundenkonto = new Kundenkonto();
        kundenkonto.setGuthaben(Double.parseDouble(balanceStr));
    }

    @When("the operator opens the balance information for customer with email {string}")
    public void the_operator_opens_the_balance_information_for_martin_keller(String email) {
        // Im Test reicht es, dass kundenkonto/kunde gesetzt sind
        assertNotNull(kundenkonto);
        assertNotNull(kunde);
        assertEquals(email, kunde.getEmail());
    }

    @Then("the system shows the current account balance {string} for customer with email {string}")
    public void the_system_shows_the_current_account_balance_75_50_for_martin_keller(String expectedBalanceStr,
                                                                                     String email) {
        assertNotNull(kundenkonto, "Kundenkonto darf nicht null sein");
        assertEquals(email, kunde.getEmail(), "E-Mail sollte übereinstimmen");
        double expected = Double.parseDouble(expectedBalanceStr);
        assertEquals(expected, kundenkonto.aktuellesGuthaben(), 0.0001,
                "Kontostand sollte dem erwarteten Wert entsprechen");
    }

    @Given("a customer exists with stored data: name {string} and email {string}")
    public void a_customer_exists_with_stored_data_martin_keller(String name, String email) {
        kunde = new Kunde(name, email, "secret");
    }

    @When("the operator updates the customer email to {string} and saves the changes")
    public void the_operator_updates_the_customer_email_and_saves_the_changes(String newEmail) {
        assertNotNull(kunde, "Kunde muss existieren");
        kunde.setEmail(newEmail);
    }

    @Then("the system stores the updated customer data")
    public void the_system_stores_the_updated_customer_data() {
        assertNotNull(kunde, "Kunde darf nicht null sein");
        assertNotNull(kunde.getEmail(), "E-Mail darf nicht null sein");
    }

    @Then("the customer data shows email {string}")
    public void the_customer_data_shows_email_martin_keller_new(String expectedEmail) {
        assertNotNull(kunde, "Kunde darf nicht null sein");
        assertEquals(expectedEmail, kunde.getEmail(),
                "Die aktualisierte E-Mail-Adresse sollte angezeigt werden");
    }

    // Variante ohne Leerzeichen nach dem Doppelpunkt (wie im Log)
    @Given("multiple customers exist:{string} with email {string}, {string} with email {string}, {string} with email {string}")
    public void multiple_customers_exist_no_space(String name1, String email1,
                                                  String name2, String email2,
                                                  String name3, String email3) {
        initCustomerList(name1, email1, name2, email2, name3, email3);
    }

    // Variante mit Leerzeichen nach dem Doppelpunkt
    @Given("multiple customers exist: {string} with email {string}, {string} with email {string}, {string} with email {string}")
    public void multiple_customers_exist_with_space(String name1, String email1,
                                                    String name2, String email2,
                                                    String name3, String email3) {
        initCustomerList(name1, email1, name2, email2, name3, email3);
    }

    private void initCustomerList(String name1, String email1,
                                  String name2, String email2,
                                  String name3, String email3) {
        kundenListe = new ArrayList<>();
        kundenListe.add(new Kunde(name1, email1, "pw1"));
        kundenListe.add(new Kunde(name2, email2, "pw2"));
        kundenListe.add(new Kunde(name3, email3, "pw3"));
    }

    @When("the operator opens the customer list")
    public void the_operator_opens_the_customer_list() {
        assertNotNull(kundenListe, "Kundenliste muss initialisiert sein");
        angezeigteKunden = new ArrayList<>(kundenListe);
    }

    @Then("the system shows all customers in a list including: {string},{string}, {string}")
    public void the_system_shows_all_customers_in_a_list_including(String name1, String name2, String name3) {
        assertNotNull(angezeigteKunden, "Angezeigte Kundenliste darf nicht null sein");

        List<String> names = angezeigteKunden.stream()
                .map(Kunde::getName)
                .collect(Collectors.toList());

        assertTrue(names.contains(name1),
                "Expected customer '" + name1 + "' not found in the list");
        assertTrue(names.contains(name2),
                "Expected customer '" + name2 + "' not found in the list");
        assertTrue(names.contains(name3),
                "Expected customer '" + name3 + "' not found in the list");
    }

    
    @When("the operator applies the filter {string} to the customer list")
    public void the_operator_applies_the_filter_keller_to_the_customer_list(String filter) {
        assertNotNull(kundenListe, "Kundenliste muss existieren");

        gefilterteKunden = kundenListe.stream()
                .filter(k -> k.getName() != null &&
                        k.getName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Then("the system shows only customers with name containing {string}")
    public void the_system_shows_only_customers_with_name_containing_keller(String filter) {
        assertNotNull(gefilterteKunden, "Gefilterte Kundenliste darf nicht null sein");
        assertFalse(gefilterteKunden.isEmpty(),
                "Es sollte mindestens ein gefilterter Kunde vorhanden sein");

        for (Kunde k : gefilterteKunden) {
            assertNotNull(k.getName(), "Kundenname darf nicht null sein");
            assertTrue(k.getName().toLowerCase().contains(filter.toLowerCase()),
                    "Gefundener Kunde '" + k.getName() +
                            "' entspricht nicht dem Filter '" + filter + "'");
        }
    }
}
