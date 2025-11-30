Feature: Use operator dashboard
  As an operator
  I want to open a dashboard
  So that I can monitor station status and relevant data in real time.

  Scenario: Monitor locations in real time
    Given an operator with id "OP-01", name "Admin Operator" and email "operator@testsystem.com" is logged into the dashboard
    And active locations exist: "City Center" with id "1" and charging stations "10" (AC, IN_BETRIEB_FREI) and "11" (DC, IN_BETRIEB_BESETZT), and "Highway Station" with id "2" and charging station "20" (DC, IN_BETRIEB_FREI)
    When the operator opens the real-time overview
    Then the system shows the current status of locations "City Center" and "Highway Station" and charging stations "10", "11" and "20"

  Scenario: View location data in the dashboard
    Given an operator with id "OP-01", name "Admin Operator" and email "operator@testsystem.com" is viewing the dashboard
    And an active location "City Center" with id "1" exists with charging stations "10" (AC, IN_BETRIEB_FREI) and "11" (DC, IN_BETRIEB_BESETZT) and a charging session "5001" from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh at station "11"
    When the operator selects the location "City Center" in the dashboard
    Then the system shows usage and status data for location "City Center" including charging session "5001" with energy "24.0" kWh and station status "10" IN_BETRIEB_FREI and "11" IN_BETRIEB_BESETZT

  Scenario: Customize dashboard
    Given an operator with id "OP-01", name "Admin Operator" and email "operator@testsystem.com" is viewing the dashboard with layout "Standard"
    When the operator changes the dashboard layout to "KPI_Compact" and saves the changes
    Then the system stores the customized dashboard layout "KPI_Compact" for operator id "OP-01"

  Scenario: Create reports from dashboard data
    Given an operator with id "OP-01", name "Admin Operator" and email "operator@testsystem.com" is logged into the dashboard
    And charging sessions exist for reporting: session "5001" from "2025-11-20T10:00" to "2025-11-20T10:30" with energy "24.0" kWh at station "11" and session "5002" from "2025-11-18T18:15" to "2025-11-18T18:45" with energy "18.0" kWh at station "20"
    When the operator generates a report for the time period from "2025-11-15" to "2025-11-21"
    Then the system creates a report with key performance indicators including total energy "42.0" kWh and number of sessions "2" for this period

  Scenario: Receive error messages
    Given charging stations are monitored by the system and the location "City Center" with id "1" has a charging station "11" (DC, IN_BETRIEB_FREI)
    When a fault with code "ERR-STS-11" occurs at charging station "11"
    Then the system shows an error message "Fault ERR-STS-11 at station 11" in the dashboard
    And the operator can see that station "11" at location "City Center" is affected