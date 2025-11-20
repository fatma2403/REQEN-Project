Feature: View charging locations
  As a customer
  I want to see an overview of locations
  So that I can find and select available charging stations.

  Scenario: View location overview
    Given a customer exists
    And there are several locations with charging stations
    When the customer opens the location overview
    Then the system shows a list of available locations

  Scenario: View location details
    Given a customer exists
    And there are several locations with charging stations
    And the customer is on the location overview
    When the customer opens the details of a location
    Then the system shows information about chargers and status at that location

  Scenario: Filter locations
    Given a customer exists
    And there are several locations with different attributes
    When the customer applies filter criteria to the location overview
    Then the system shows only locations that match the selected criteria

  Scenario: Reserve a charging station
    Given a customer exists
    And there is a location with at least one available charging station
    And the customer is viewing the location details
    When the customer reserves a charging station
    Then the system blocks this charging station for the customer for a limited time
