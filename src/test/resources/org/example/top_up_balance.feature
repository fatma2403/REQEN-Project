Feature: Top up balance
  As a customer
  I want to top up my balance in advance
  So that I can pay my charging sessions easily.

  Scenario: Use the prepaid system
    Given a logged-in customer with name "Martin Keller", email "martin.keller@testmail.com" and a balance account with current balance 25.00 EUR
    When the customer selects the prepaid top-up option
    And the customer enters the amount 40.00 EUR
    And the payment is confirmed
    Then the system increases the customer balance to 65.00 EUR

  Scenario: Set preferred payment method
    Given a logged-in customer with name "Martin Keller" and email "martin.keller@testmail.com"
    When the customer selects "CREDIT_CARD" as preferred payment method
    Then the system stores "CREDIT_CARD" as the default payment method
    And future top-ups for "martin.keller@testmail.com" use "CREDIT_CARD" by default