Feature: End charging session
  As a customer
  I want to end the charging session
  So that the charging is safely completed and billed.

  Scenario: Unplug the charging cable
    Given an ongoing charging session "5001" for customer "Martin Keller" with customer ID "CUST-1023" at charging station "11" started at "2025-11-20T10:00"
    When the customer unplugs the charging cable at "2025-11-20T10:30"
    Then the system stops the charging session "5001" and records the end time "2025-11-20T10:30"

  Scenario: Unplug fails when session is unknown
    Given an ongoing charging session "5001" for customer "Martin Keller" with customer ID "CUST-1023" at charging station "11" started at "2025-11-20T10:00"
    When the customer unplugs the charging cable for session "UNKNOWN" at "2025-11-20T10:30"
    Then the system rejects stopping session "UNKNOWN"
    And the system shows a stop charging error message

  Scenario: Unplug is ignored when charging session is already finished
    Given a finished charging session "5001" for customer "Martin Keller" with customer ID "CUST-1023" ended at "2025-11-20T10:30"
    When the customer unplugs the charging cable again
    Then the system keeps the charging session "5001" unchanged
    And the system shows a charging session already finished message

  Scenario: Automatic billing after charging
    Given a finished charging session "5001" for customer "Martin Keller" with customer ID "CUST-1023" from "2025-11-20T10:00" to "2025-11-20T10:30" with measured energy "24.0" kWh
    And a pricing rule exists for location "City Center" with charging mode "DC", price per kWh "0.45" and price per minute "0.10" valid from "2025-11-01T00:00"
    When the system calculates the cost for session "5001" based on the tariffs
    Then the system creates a billing record for customer ID "CUST-1023" with total amount "13.80"

  Scenario: Customer receives an invoice
    Given the system has created a billing record for session "5001" with invoice number "1001" and total amount "13.80" for customer ID "CUST-1023"
    When the invoice "1001" is generated
    Then the customer "Martin Keller" can view invoice "1001" with total amount "13.80" for charging session "5001"
