Feature: View charging locations
  As a customer
  I want to see an overview of locations
  So that I can find and select available charging stations.

  Scenario: View location overview
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And the following locations with charging stations exist:
      | Standort        | Adresse                          | LadestationID | Modus | Status              |
      | City Center     | Hauptstraße 1, 1010 Wien       | 1             | AC    | IN_BETRIEB_FREI     |
      | City Center     | Hauptstraße 1, 1010 Wien       | 2             | DC    | IN_BETRIEB_BESETZT  |
      | Mall Parking    | Einkaufspark 5, 4020 Linz      | 3             | AC    | IN_BETRIEB_FREI     |
      | Highway Station | Autobahn A1, Rastplatz West    | 4             | DC    | IN_BETRIEB_FREI     |
    When the customer opens the location overview
    Then the system shows a list of available locations including "City Center", "Mall Parking" and "Highway Station"

  Scenario: View location details
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And location "City Center" at address "Hauptstraße 1, 1010 Wien" has the following charging stations:
      | LadestationID | Modus | Status             |
      | 1             | AC    | IN_BETRIEB_FREI    |
      | 2             | DC    | IN_BETRIEB_BESETZT |
    When the customer opens the details of location "City Center"
    Then the system shows chargers for "City Center" including:
      | LadestationID | Modus | Status             |
      | 1             | AC    | IN_BETRIEB_FREI    |
      | 2             | DC    | IN_BETRIEB_BESETZT |

  Scenario: Filter locations
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And the following locations with charging stations exist:
      | Standort        | Adresse                          | LadestationID | Modus | Status           |
      | City Center     | Hauptstraße 1, 1010 Wien       | 1             | AC    | IN_BETRIEB_FREI  |
      | Mall Parking    | Einkaufspark 5, 4020 Linz      | 3             | AC    | IN_BETRIEB_FREI  |
      | Highway Station | Autobahn A1, Rastplatz West    | 4             | DC    | IN_BETRIEB_FREI  |
    When the customer filters locations by charging mode "DC"
    Then the system shows only locations including "Highway Station"

  Scenario: Reserve a charging station
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And location "City Center" has a free charging station with LadestationID 1
    And the customer is viewing the location details of "City Center"
    When the customer reserves charging station with LadestationID 1 for 15 minutes
    Then the system sets the charging station with LadestationID 1 as reserved for customer "martin.keller@testmail.com"
    And the station cannot be used by other customers during this time

