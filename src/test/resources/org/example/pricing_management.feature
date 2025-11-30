Feature: Manage pricing
  As an operator
  I want to manage pricing
  So that I can control billing per location and charging mode.

  Scenario: Set prices per location
    Given an operator with id "OP-01", name "Admin Operator" is in the pricing section
    And a location "City Center" with id "1" exists
    When the operator defines prices for location "City Center" with charging mode "AC", price per kWh "0.35", price per minute "0.05" valid from "2025-11-01T00:00"
    Then the system stores a pricing rule for location "City Center" with charging mode "AC", price per kWh "0.35", price per minute "0.05" and valid from "2025-11-01T00:00"

  Scenario: Adjust AC and DC price parameters
    Given an operator with id "OP-01", name "Admin Operator" is in the pricing section
    And existing pricing rules for location "City Center" with id "1" are defined: AC price per kWh "0.35", price per minute "0.05" and DC price per kWh "0.45", price per minute "0.10" valid from "2025-11-01T00:00"
    When the operator changes the AC price to "0.40" per kWh and "0.06" per minute and the DC price to "0.50" per kWh and "0.12" per minute for location "City Center" and saves the changes
    Then the system stores updated pricing rules for location "City Center" with AC price per kWh "0.40", price per minute "0.06" and DC price per kWh "0.50", price per minute "0.12"

  Scenario: Update pricing rules
    Given an existing pricing rule with id "101" for location "City Center" with charging mode "AC", price per kWh "0.40", price per minute "0.06" and valid from "2025-11-01T00:00" is defined
    When the operator edits this pricing rule to price per kWh "0.38", price per minute "0.05" and valid from "2025-12-01T00:00" and saves the changes
    Then the system applies the updated pricing rule with id "101" so that future charging sessions at location "City Center" with mode "AC" starting on or after "2025-12-01T00:00" use price per kWh "0.38" and price per minute "0.05"

  Scenario: Validate price during charging
    Given a charging session is running for customer "Martin Keller" at location "City Center" with id "1" on a DC charging station with id "11" from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh
    And a pricing rule for location "City Center" with charging mode "DC", price per kWh "0.45", price per minute "0.10" and valid from "2025-11-01T00:00" exists
    When the system calculates the price for the session at time "2025-11-20T10:30"
    Then the system uses the pricing rule with price per kWh "0.45" and price per minute "0.10" for location "City Center" and charging mode "DC" and calculates a total price of "13.80"