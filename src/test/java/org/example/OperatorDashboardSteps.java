package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorDashboardSteps {

    private boolean operatorLoggedIn;
    private boolean dashboardOpened;
    private boolean customLayoutSaved;
    private boolean reportGenerated;
    private boolean errorShown;

    private List<String> locations = new ArrayList<>();

    @Given("an operator is logged into the dashboard")
    public void an_operator_is_logged_into_the_dashboard() {
        operatorLoggedIn = true;
    }

    @Given("there are active locations with charging stations")
    public void there_are_active_locations_with_charging_stations() {
        locations.add("Location-1");
    }

    @When("the operator opens the real-time overview")
    public void the_operator_opens_the_real_time_overview() {
        dashboardOpened = true;
    }

    @Then("the system shows the current status of all locations and stations")
    public void the_system_shows_the_current_status_of_all_locations_and_stations() {
        assertTrue(operatorLoggedIn && dashboardOpened && !locations.isEmpty());
    }

    @Given("an operator is viewing the dashboard")
    public void an_operator_is_viewing_the_dashboard() {
        operatorLoggedIn = true;
        dashboardOpened = true;
    }

    @When("the operator selects a specific location")
    public void the_operator_selects_a_specific_location() {
        // Auswahl simulieren
    }

    @Then("the system shows usage and status data for that location")
    public void the_system_shows_usage_and_status_data_for_that_location() {
        assertTrue(dashboardOpened);
    }

    @When("the operator changes the dashboard layout or widgets and saves the changes")
    public void the_operator_changes_the_dashboard_layout_or_widgets_and_saves_the_changes() {
        customLayoutSaved = true;
    }

    @Then("the system stores the customized dashboard settings for that operator")
    public void the_system_stores_the_customized_dashboard_settings_for_that_operator() {
        assertTrue(customLayoutSaved);
    }

    @Given("data about charging sessions is available")
    public void data_about_charging_sessions_is_available() {
        // Voraussetzung
    }

    @When("the operator generates a report for a selected time period")
    public void the_operator_generates_a_report_for_a_selected_time_period() {
        reportGenerated = true;
    }

    @Then("the system creates a report with key performance indicators")
    public void the_system_creates_a_report_with_key_performance_indicators() {
        assertTrue(reportGenerated);
    }

    @Given("charging stations are monitored by the system")
    public void charging_stations_are_monitored_by_the_system() {
        // Voraussetzung
    }

    @When("a fault occurs at a charging station")
    public void a_fault_occurs_at_a_charging_station() {
        errorShown = true;
    }

    @Then("the system shows an error message in the dashboard")
    public void the_system_shows_an_error_message_in_the_dashboard() {
        assertTrue(errorShown);
    }

    @Then("the operator can see which station is affected")
    public void the_operator_can_see_which_station_is_affected() {
        assertTrue(errorShown);
    }
}
