Feature: Manage payments
  As a customer
  I want to manage my payments
  So that I keep an overview of my past charging sessions.

  Scenario: View invoices
    Given a logged-in customer with past invoices
    When the customer opens the invoice overview
    Then the system shows a list of invoices for this customer

  Scenario: View charging history
    Given a logged-in customer with past charging sessions
    When the customer opens the charging history
    Then the system shows a list of charging sessions with main details
