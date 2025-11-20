Feature: End charging session
  As a customer
  I want to end the charging session
  So that the charging is safely completed and billed.

  Scenario: Unplug the charging cable
    Given an ongoing charging session
    When the customer unplugs the charging cable
    Then the system stops the charging session

  Scenario: Automatic billing after charging
    Given a finished charging session with measured energy
    When the system calculates the cost based on the tariffs
    Then the system creates a billing record for the customer account

  Scenario: Customer receives an invoice
    Given the system has created a billing record for the session
    When the invoice is generated
    Then the customer can view the invoice for the charging session
