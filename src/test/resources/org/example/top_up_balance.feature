Feature: Top up balance
  As a customer
  I want to top up my balance in advance
  So that I can pay my charging sessions easily.

  Scenario: Use the prepaid system
    Given a logged-in customer with a balance account
    When the customer selects the prepaid top-up option
    And the customer enters a valid amount
    And the payment is confirmed
    Then the system increases the customer balance by that amount

  Scenario: Set preferred payment method
    Given a logged-in customer
    When the customer selects a payment method as preferred
    Then the system stores this payment method as default
    And future top-ups use this preferred payment method by default
