Feature: Use operator dashboard
  As an operator
  I want to open a dashboard
  So that I can monitor station status and relevant data in real time.

  Scenario: Monitor locations in real time
    Given an operator is logged into the dashboard
    And there are active locations with charging stations
    When the operator opens the real-time overview
    Then the system shows the current status of all locations and stations

  Scenario: View location data in the dashboard
    Given an operator is viewing the dashboard
    When the operator selects a specific location
    Then the system shows usage and status data for that location

  Scenario: Customize dashboard
    Given an operator is viewing the dashboard
    When the operator changes the dashboard layout or widgets and saves the changes
    Then the system stores the customized dashboard settings for that operator

  Scenario: Create reports from dashboard data
    Given an operator is logged into the dashboard
    And data about charging sessions is available
    When the operator generates a report for a selected time period
    Then the system creates a report with key performance indicators

  Scenario: Receive error messages
    Given charging stations are monitored by the system
    When a fault occurs at a charging station
    Then the system shows an error message in the dashboard
    And the operator can see which station is affected
