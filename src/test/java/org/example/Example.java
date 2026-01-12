package org.example;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Example {
    int number1;
    @Given("the first number {int}")
    public void theFirstNumber(int arg0) {

        number1 = arg0;
    }

    @And("the second number is {int}")
    public void theSecondNumberIs(int arg0) {
        int number2;
        throw new PendingException();
    }

    @When("the two number added")
    public void theTwoNumberAdded() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the result is {int}")
    public void theResultIs(int arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
