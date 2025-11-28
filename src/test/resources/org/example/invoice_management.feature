Feature: Manage invoices
  As an operator
  I want to manage invoices
  So that I can calculate, review and export them.

  Scenario: Calculate cost of a charging session
    Given a completed charging session for customer "Martin Keller" with email "martin.keller@testmail.com" from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh at charging mode "DC"
    And tariffs are defined with price per kWh "0.45" and price per minute "0.10" for charging mode "DC"
    When the operator triggers the cost calculation
    Then the system calculates the total cost of the session as "13.80"

  Scenario: Generate invoice entry
    Given the cost of a completed charging session for customer "Martin Keller" with email "martin.keller@testmail.com" is "13.80"
    When the operator confirms the billing
    Then the system creates an invoice with number "1001", date "2025-11-20" and total amount "13.80" for customer with email "martin.keller@testmail.com"

  Scenario: Check details of a charging session
    Given an invoice with number "1001" exists for customer "Martin Keller" with email "martin.keller@testmail.com" and charging session from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh, price per kWh "0.45" and price per minute "0.10"
    When the operator opens the invoice details for invoice number "1001"
    Then the system shows energy used "24.0" kWh, time period from "2025-11-20T10:00" to "2025-11-20T10:30" and applied tariffs "0.45" per kWh and "0.10" per minute

  Scenario: Filter invoices by period or customer
    Given invoices exist in the system: invoice "1001" for customer email "martin.keller@testmail.com" with date "2025-11-20" and invoice "1002" for customer email "laura.fischer@testmail.com" with date "2025-10-15"
    When the operator filters invoices by date range from "2025-11-01" to "2025-11-30" and customer email "martin.keller@testmail.com"
    Then the system shows only invoices "1001" in the result list

  Scenario: Show invoice list
    Given invoices exist in the system: invoice "1001" for customer email "martin.keller@testmail.com" and invoice "1002" for customer email "laura.fischer@testmail.com"
    When the operator opens the invoice overview
    Then the system displays a list of all invoices including "1001" and "1002"

  Scenario: Export invoices
    Given a list of invoices "1001" and "1002" is displayed in the invoice overview
    When the operator exports the invoices
    Then the system provides the invoices in a downloadable file "invoices_2025-11_export.csv"
