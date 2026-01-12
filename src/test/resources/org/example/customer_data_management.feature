Feature: Manage customer data
  As an operator
  I want to manage customer data
  So that I can review and update customer information.

  Scenario: Check customer account balance
    Given a customer account exists for name "Martin Keller" with email "martin.keller@testmail.com" and a balance of "75.50"
    When the operator opens the balance information for customer with email "martin.keller@testmail.com"
    Then the system shows the current account balance "75.50" for customer with email "martin.keller@testmail.com"

  Scenario: Edit customer data
    Given a customer exists with stored data: name "Martin Keller" and email "martin.keller@testmail.com"
    When the operator updates the customer email to "martin.keller.new@testmail.com" and saves the changes
    Then the system stores the updated customer data
    And the customer data shows email "martin.keller.new@testmail.com"

  Scenario: View customer list
    Given multiple customers exist:"Martin Keller" with email "martin.keller@testmail.com", "Laura Fischer" with email "laura.fischer@testmail.com", "Jonas Weber" with email "jonas.weber@testmail.com"
    When the operator opens the customer list
    Then the system shows all customers in a list including: "Martin Keller","Laura Fischer", "Jonas Weber"

  Scenario: Filter customer list
    Given multiple customers exist: "Martin Keller" with email "martin.keller@testmail.com", "Laura Fischer" with email "laura.fischer@testmail.com", "Jonas Weber" with email "jonas.weber@testmail.com"
    When the operator applies the filter "Keller" to the customer list
    Then the system shows only customers with name containing "Keller"

  Scenario: Reject invalid email update
    Given a customer exists with stored data: name "Martin Keller" and email "martin.keller@testmail.com"
    When the operator updates the customer email to "invalid-email" and saves the changes
    Then the system rejects the email update