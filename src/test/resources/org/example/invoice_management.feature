Feature: Manage invoices
  As an operator
  I want to manage invoices
  So that I can calculate, review and export them.

  Scenario: Calculate cost of a charging session
    Given a completed charging session with measured energy
    And tariffs are defined for the location and charging mode
    When the operator triggers the cost calculation
    Then the system calculates the total cost of the session

  Scenario: Generate invoice entry
    Given the cost for a charging session is calculated
    When the operator confirms the billing
    Then the system creates an invoice entry in the billing system

  Scenario: Check details of a charging session
    Given an invoice exists for a charging session
    When the operator opens the invoice details
    Then the system shows energy used, time period and applied tariffs

  Scenario: Filter invoices by period or customer
    Given invoices exist in the system
    When the operator filters invoices by date range or customer
    Then the system shows only invoices that match the filter criteria

  Scenario: Show invoice list
    Given invoices exist in the system
    When the operator opens the invoice overview
    Then the system displays a list of all invoices

  Scenario: Export invoices
    Given a list of invoices is displayed
    When the operator exports the invoices
    Then the system provides the invoices in a downloadable file format
