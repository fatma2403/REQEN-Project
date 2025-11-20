Feature: Manage pricing
  As an operator
  I want to manage pricing
  So that I can control billing per location and charging mode.

  Scenario: Set prices per location
    Given an operator is in the pricing section
    When the operator defines prices for a specific location
    Then the system stores these prices for that location

  Scenario: Adjust AC and DC price parameters
    Given an operator is in the pricing section
    When the operator changes the AC and DC price parameters for a location
    Then the system stores the updated price parameters

  Scenario: Update pricing rules
    Given existing pricing rules are defined
    When the operator edits a pricing rule and saves the changes
    Then the system applies the updated rule to future charging sessions

  Scenario: Validate price during charging
    Given a charging session is running at a location with defined prices
    When the system calculates the price for the session
    Then the system uses the valid prices and rules for that time and location
