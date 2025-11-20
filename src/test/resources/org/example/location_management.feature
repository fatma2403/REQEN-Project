Feature: Manage locations
  As an operator
  I want to manage locations
  So that I can maintain, update and organize charging stations.

  Scenario: Change operating status of a charging station
    Given a location with at least one charging station exists
    When the operator sets a charging station to inactive
    Then the system marks this station as inactive
    And inactive stations are not available for new charging sessions

  Scenario: Edit location data
    Given a location exists with stored address and name
    When the operator changes the location data and saves the changes
    Then the system stores the updated location data

  Scenario: Remove a location
    Given a location exists with charging stations
    When the operator removes this location
    Then the system no longer shows this location in the list of active locations

  Scenario: Assign a charging station to a location
    Given an unassigned charging station exists
    And a location exists
    When the operator assigns the charging station to the location
    Then the system links the charging station to that location
