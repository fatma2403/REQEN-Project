Feature: Manage locations
  As an operator
  I want to manage locations
  So that I can maintain, update and organize charging stations.

  Scenario: Change operating status of a charging station
    Given a location with id "1", name "City Center" and address "Hauptstraße 1, 1010 Wien" exists with a charging station with id "10", mode "AC" and status "IN_BETRIEB_FREI"
    When the operator sets the charging station with id "10" to status "AUSSER_BETRIEB"
    Then the system marks the charging station with id "10" as "AUSSER_BETRIEB"
    And charging stations with status "AUSSER_BETRIEB" are not available for new charging sessions

  Scenario: Edit location data
    Given a location with id "1", name "City Center" and address "Hauptstraße 1, 1010 Wien" exists
    When the operator changes the location name to "City Center West" and the address to "Neubaustraße 5, 1070 Wien" and saves the changes
    Then the system stores the updated location data for location id "1" with name "City Center West" and address "Neubaustraße 5, 1070 Wien"

  Scenario: Remove a location
    Given a location with id "2", name "Mall Parking" and address "Einkaufspark 5, 4020 Linz" exists with charging stations
    When the operator removes the location with id "2"
    Then the system no longer shows the location with id "2" in the list of active locations

  Scenario: Assign a charging station to a location
    Given an unassigned charging station with id "20", mode "DC" and status "IN_BETRIEB_FREI" exists
    And a location with id "3", name "Highway Station" and address "Autobahn A1, Rastplatz West" exists
    When the operator assigns the charging station with id "20" to the location with id "3"
    Then the system links the charging station with id "20" to the location with id "3"