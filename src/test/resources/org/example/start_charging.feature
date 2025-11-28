Feature: Start charging session
  As a customer
  I want to start a charging session
  So that I can charge my vehicle.

  Scenario: Confirm customer ID
    Given a charging station with id "11" at location "City Center" in mode "DC" and status "IN_BETRIEB_FREI" is ready for use
    And a customer with name "Martin Keller", email "martin.keller@testmail.com" and customer ID "CUST-1023" is identified at the station
    When the customer enters customer ID "CUST-1023" at the charging station
    Then the system validates that customer ID "CUST-1023" exists
    And the system links the new charging session to the customer account with customer ID "CUST-1023"

  Scenario: Select charging mode
    Given a validated customer "Martin Keller" with customer ID "CUST-1023" is at charging station with id "11" that supports charging modes "AC" and "DC"
    When the customer selects charging mode "DC"
    Then the system configures charging station "11" with charging mode "DC"

  Scenario: Start the charging session
    Given a validated customer "Martin Keller" with customer ID "CUST-1023" is at charging station with id "11" configured with charging mode "DC"
    When the customer starts the charging session at "2025-11-20T10:00"
    Then the system begins delivering energy at charging station "11"
    And the system records the start time of the charging session as "2025-11-20T10:00"

  Scenario: Receive charging notifications
    Given an ongoing charging session "5001" for customer "Martin Keller" at charging station "11" started at "2025-11-20T10:00" with recorded energy "10.0" kWh
    When the charging progress changes and the recorded energy for session "5001" is updated to "20.0" kWh
    Then the system updates the charging session data to energy "20.0" kWh
    And the updated charging session status for session "5001" is available to be shown to the customer

