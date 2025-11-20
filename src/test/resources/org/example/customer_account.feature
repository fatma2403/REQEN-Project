Feature: Manage customer account
  As a customer
  I want to create and manage my customer account
  So that I can register, log in and maintain my personal data.

  Scenario: Register or log in
    Given a customer without an account
    When the customer registers with valid data
    Then a new customer account is created
    And the customer is logged in

  Scenario: Receive a customer ID
    Given a registered customer without a customer ID
    When the system activates the customer account
    Then the system assigns a unique customer ID to the customer

  Scenario: View personal data
    Given a logged-in customer with stored personal data
    When the customer opens the personal data page
    Then the system shows the current personal data

  Scenario: Edit personal data
    Given a logged-in customer with stored personal data
    When the customer changes personal data and saves the changes
    Then the system stores the updated personal data
    And the customer can see the updated information
