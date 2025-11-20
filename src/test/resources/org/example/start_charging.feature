Feature: Start charging session
  As a customer
  I want to start a charging session
  So that I can charge my vehicle.

  Scenario: Confirm customer ID
    Given a charging station is ready for use
    And a customer is identified at the station
    When the customer presents or enters their customer ID
    Then the system validates the customer ID
    And links the charging session to the customer account

  Scenario: Select charging mode
    Given a validated customer at a compatible charging station
    When the customer selects AC or DC charging mode
    Then the system configures the charging station with the selected mode

  Scenario: Start the charging session
    Given a validated customer with a selected charging mode
    When the customer starts the charging session
    Then the system begins delivering energy to the vehicle
    And the system records the start time of the session

  Scenario: Receive charging notifications
    Given an ongoing charging session
    When the charging progress changes significantly
    Then the system sends a notification about the current status to the customer
