Feature: View charging locations

  As a customer
  I want to see an overview of locations
  So that I can find and select available charging stations.

  Scenario: View location overview
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And locations exist: "City Center" at "Hauptstraße 1, 1010 Wien" with chargers (1 AC IN_BETRIEB_FREI, 2 DC IN_BETRIEB_BESETZT), "Mall Parking" at "Einkaufspark 5, 4020 Linz" with chargers (3 AC IN_BETRIEB_FREI), "Highway Station" at "Autobahn A1, Rastplatz West" with chargers (4 DC IN_BETRIEB_FREI)
    When the customer opens the location overview
    Then the system shows a list of available locations including "City Center", "Mall Parking" and "Highway Station"

  Scenario: View location details
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And locations exist: "City Center" at "Hauptstraße 1, 1010 Wien" with chargers (1 AC IN_BETRIEB_FREI, 2 DC IN_BETRIEB_BESETZT)
    And the customer is on the location overview
    When the customer opens the details of location "City Center"
    Then the system shows chargers for location "City Center" with (1 AC IN_BETRIEB_FREI, 2 DC IN_BETRIEB_BESETZT)

  Scenario: Filter locations by DC chargers
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    And locations exist: "City Center" at "Hauptstraße 1, 1010 Wien" with chargers (1 AC IN_BETRIEB_FREI, 2 DC IN_BETRIEB_BESETZT), "Mall Parking" at "Einkaufspark 5, 4020 Linz" with chargers (3 AC IN_BETRIEB_FREI), "Highway Station" at "Autobahn A1, Rastplatz West" with chargers (4 DC IN_BETRIEB_FREI)
    When the customer filters for locations with DC chargers
    Then the system shows only locations that have at least one DC charger: "City Center" and "Highway Station"

  Scenario: Placeholder
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    Then the system is ready