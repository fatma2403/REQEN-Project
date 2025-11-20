Feature: Manage customer data
  As an operator
  I want to manage customer data
  So that I can review and update customer information.

  Scenario: Check customer account balance
    Given a customer account with a balance exists
    When the operator opens the balance information for this customer
    Then the system shows the current account balance

  Scenario: Edit customer data
    Given a customer exists with stored data
    When the operator updates the customer data and saves the changes
    Then the system stores the updated customer data

  Scenario: View customer list
    Given multiple customers exist
    When the operator opens the customer list
    Then the system shows all customers in a list

  Scenario: Filter customer list
    Given multiple customers exist
    When the operator applies filter criteria to the customer list
    Then the system shows only customers that match the filter criteria
