Feature: Manage payments
  As a customer
  I want to manage my payments
  So that I keep an overview of my past charging sessions.

  Scenario: View invoices
    Given a logged-in customer with name "Martin Keller", email "martin.keller@testmail.com" and customer ID "CUST-1023"
    And past invoices exist for this customer: invoice "1001" dated "2025-11-20" with total amount "13.80" and invoice "1002" dated "2025-11-10" with total amount "9.50"
    When the customer opens the invoice overview
    Then the system shows a list of invoices "1001" and "1002" for customer ID "CUST-1023"

  Scenario: View charging history
    Given a logged-in customer with name "Martin Keller", email "martin.keller@testmail.com" and customer ID "CUST-1023"
    And past charging sessions exist for this customer: session "5001" from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh at location "City Center" and session "5002" from "2025-11-18T18:15" to "2025-11-18T18:45" with energy "18.0" kWh at location "Mall Parking"
    When the customer opens the charging history
    Then the system shows a list of charging sessions with main details including session "5001" with start "2025-11-20T10:00", end "2025-11-20T10:30", energy "24.0" kWh and location "City Center" and session "5002" with start "2025-11-18T18:15", end "2025-11-18T18:45", energy "18.0" kWh and location "Mall Parking"