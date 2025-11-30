Feature: Manage customer account
  As a customer
  I want to create and manage my customer account
  So that I can register, log in and maintain my personal data.

  Scenario: Register or log in
    Given a customer without an account with name "Martin Keller", email "martin.keller@testmail.com" and password "Secure456!"
    When the customer registers with his details
    Then a new customer account for email "martin.keller@testmail.com" is created
    And the customer with email "martin.keller@testmail.com" is logged in

  Scenario: Receive a customer ID
    Given a registered customer without a customer ID with name "Martin Keller" and email "martin.keller@testmail.com"
    When the system activates the customer account
    Then the system assigns the unique customer ID "CUST-1023" to the customer with email "martin.keller@testmail.com"

  Scenario: View personal data
    Given a logged-in customer with stored personal data: name "Martin Keller", email "martin.keller@testmail.com"
    When the customer opens the personal data page
    Then the system shows the current personal data for email "martin.keller@testmail.com"

  Scenario: Edit personal data
    Given a logged-in customer with stored personal data: name "Martin Keller", email "martin.keller@testmail.com"
    When the customer changes the email to "martin.keller.new@testmail.com" and saves the changes
    Then the system stores the updated personal data
    And the customer can see the updated information with email "martin.keller.new@testmail.com"