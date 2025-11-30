package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorDashboardSteps {

    // --- einfache Modellklassen nur für die Tests ---

    private static class Operator {
        String id;
        String name;
        String email;
        String layout;
        boolean loggedIn;
        boolean viewingDashboard;
    }

    private static class ChargingStation {
        String id;
        String mode;
        String status;
        String locationId;

        ChargingStation(String id, String mode, String status, String locationId) {
            this.id = id;
            this.mode = mode;
            this.status = status;
            this.locationId = locationId;
        }
    }

    private static class Location {
        String id;
        String name;
        List<ChargingStation> stations = new ArrayList<>();

        Location(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class ChargingSession {
        String sessionId;
        String stationId;
        String start;
        String end;
        double energyKWh;
        String locationName;

        ChargingSession(String sessionId, String stationId,
                        String start, String end,
                        double energyKWh, String locationName) {
            this.sessionId = sessionId;
            this.stationId = stationId;
            this.start = start;
            this.end = end;
            this.energyKWh = energyKWh;
            this.locationName = locationName;
        }
    }

    private static class Report {
        double totalEnergy;
        int numberOfSessions;
        String fromDate;
        String toDate;
    }

    // --- Zustand für alle Szenarien ---

    private Operator operator;
    private final List<Location> locations = new ArrayList<>();
    private final List<ChargingSession> sessions = new ArrayList<>();

    private boolean realTimeOverviewOpened;
    private Location selectedLocation;

    private Report lastReport;

    private String lastErrorMessage;
    private String errorLocationName;
    private String errorStationId;

    // --------------------------------------------------------------------
    // Scenario: Monitor locations in real time
    // --------------------------------------------------------------------

    @Given("an operator with id {string}, name {string} and email {string} is logged into the dashboard")
    public void an_operator_with_id_op01_is_logged_into_the_dashboard(String id, String name, String email) {
        operator = new Operator();
        operator.id = id;
        operator.name = name;
        operator.email = email;
        operator.loggedIn = true;

        assertEquals("OP-01", id);
        assertEquals("Admin Operator", name);
        assertEquals("operator@testsystem.com", email);
    }

    @Given("active locations exist: {string} with id {string} and charging stations {string} \\(AC, IN_BETRIEB_FREI) and {string} \\(DC, IN_BETRIEB_BESETZT), and {string} with id {string} and charging station {string} \\(DC, IN_BETRIEB_FREI)")
    public void active_locations_exist_city_center_and_highway_station(
            String loc1Name, String loc1Id,
            String station10Id, String station11Id,
            String loc2Name, String loc2Id,
            String station20Id
    ) {
        locations.clear();

        Location cityCenter = new Location(loc1Id, loc1Name);
        cityCenter.stations.add(new ChargingStation(station10Id, "AC", "IN_BETRIEB_FREI", loc1Id));
        cityCenter.stations.add(new ChargingStation(station11Id, "DC", "IN_BETRIEB_BESETZT", loc1Id));

        Location highway = new Location(loc2Id, loc2Name);
        highway.stations.add(new ChargingStation(station20Id, "DC", "IN_BETRIEB_FREI", loc2Id));

        locations.add(cityCenter);
        locations.add(highway);

        assertEquals("City Center", loc1Name);
        assertEquals("1", loc1Id);
        assertEquals("Highway Station", loc2Name);
        assertEquals("2", loc2Id);
    }

    @When("the operator opens the real-time overview")
    public void the_operator_opens_the_real_time_overview() {
        assertNotNull(operator, "Operator should be initialized");
        assertTrue(operator.loggedIn, "Operator should be logged in");
        realTimeOverviewOpened = true;
    }

    @Then("the system shows the current status of locations {string} and {string} and charging stations {string}, {string} and {string}")
    public void the_system_shows_current_status_of_locations_and_stations(
            String loc1Name, String loc2Name,
            String st10, String st11, String st20
    ) {
        assertTrue(realTimeOverviewOpened, "Real-time overview should be opened");

        boolean hasLoc1 = locations.stream().anyMatch(l -> l.name.equals(loc1Name));
        boolean hasLoc2 = locations.stream().anyMatch(l -> l.name.equals(loc2Name));
        assertTrue(hasLoc1, "Location " + loc1Name + " should be present");
        assertTrue(hasLoc2, "Location " + loc2Name + " should be present");

        boolean has10 = locations.stream().flatMap(l -> l.stations.stream())
                .anyMatch(s -> s.id.equals(st10));
        boolean has11 = locations.stream().flatMap(l -> l.stations.stream())
                .anyMatch(s -> s.id.equals(st11));
        boolean has20 = locations.stream().flatMap(l -> l.stations.stream())
                .anyMatch(s -> s.id.equals(st20));

        assertTrue(has10, "Station " + st10 + " should be present");
        assertTrue(has11, "Station " + st11 + " should be present");
        assertTrue(has20, "Station " + st20 + " should be present");
    }

    // --------------------------------------------------------------------
    // Scenario: View location data in the dashboard
    // --------------------------------------------------------------------

    @Given("an operator with id {string}, name {string} and email {string} is viewing the dashboard")
    public void an_operator_with_id_op01_is_viewing_the_dashboard(String id, String name, String email) {
        operator = new Operator();
        operator.id = id;
        operator.name = name;
        operator.email = email;
        operator.loggedIn = true;
        operator.viewingDashboard = true;

        assertEquals("OP-01", id);
        assertEquals("Admin Operator", name);
        assertEquals("operator@testsystem.com", email);
    }

    @Given("an active location {string} with id {string} exists with charging stations {string} \\(AC, IN_BETRIEB_FREI) and {string} \\(DC, IN_BETRIEB_BESETZT) and a charging session {string} from {string} to {string} with energy {string} kWh at station {string}")
    public void an_active_location_with_id_exists_with_charging_stations_ac_in_betrieb_frei_and_dc_in_betrieb_besetzt_and_a_charging_session_from_to_with_energy_k_wh_at_station(
            String locName,
            String locId,
            String station10Id,
            String station11Id,
            String sessionId,
            String start,
            String end,
            String energy,
            String stationIdForSession
    ) {
        locations.clear();
        sessions.clear();

        Location cityCenter = new Location(locId, locName);
        cityCenter.stations.add(new ChargingStation(station10Id, "AC", "IN_BETRIEB_FREI", locId));
        cityCenter.stations.add(new ChargingStation(station11Id, "DC", "IN_BETRIEB_BESETZT", locId));
        locations.add(cityCenter);

        ChargingSession session = new ChargingSession(
                sessionId,
                stationIdForSession,
                start,
                end,
                Double.parseDouble(energy),
                locName
        );
        sessions.add(session);

        assertEquals("City Center", locName);
        assertEquals("1", locId);
        assertEquals("5001", sessionId);
        assertEquals("11", stationIdForSession);
        assertEquals("24.0", energy);
    }

    @When("the operator selects the location {string} in the dashboard")
    public void the_operator_selects_the_location_city_center_in_the_dashboard(String locationName) {
        assertTrue(operator.viewingDashboard, "Operator should be viewing the dashboard");

        selectedLocation = locations.stream()
                .filter(l -> l.name.equals(locationName))
                .findFirst()
                .orElse(null);

        assertNotNull(selectedLocation, "Selected location should exist: " + locationName);
    }

    @Then("the system shows usage and status data for location {string} including charging session {string} with energy {string} kWh and station status {string} IN_BETRIEB_FREI and {string} IN_BETRIEB_BESETZT")
    public void the_system_shows_usage_and_status_data_for_city_center(
            String locName,
            String sessionId,
            String energy,
            String station10Id,
            String station11Id
    ) {
        assertNotNull(selectedLocation, "Location should be selected");
        assertEquals(locName, selectedLocation.name);

        ChargingSession session = sessions.stream()
                .filter(s -> s.sessionId.equals(sessionId))
                .findFirst()
                .orElse(null);

        assertNotNull(session, "Session " + sessionId + " should exist");
        assertEquals(Double.parseDouble(energy), session.energyKWh, 0.0001);

        ChargingStation st10 = selectedLocation.stations.stream()
                .filter(s -> s.id.equals(station10Id))
                .findFirst()
                .orElse(null);
        ChargingStation st11 = selectedLocation.stations.stream()
                .filter(s -> s.id.equals(station11Id))
                .findFirst()
                .orElse(null);

        assertNotNull(st10, "Station " + station10Id + " should exist");
        assertNotNull(st11, "Station " + station11Id + " should exist");

        assertEquals("IN_BETRIEB_FREI", st10.status);
        assertEquals("IN_BETRIEB_BESETZT", st11.status);
    }

    // --------------------------------------------------------------------
    // Scenario: Customize dashboard
    // --------------------------------------------------------------------

    @Given("an operator with id {string}, name {string} and email {string} is viewing the dashboard with layout {string}")
    public void an_operator_viewing_dashboard_with_layout_standard(
            String id, String name, String email, String layout
    ) {
        operator = new Operator();
        operator.id = id;
        operator.name = name;
        operator.email = email;
        operator.layout = layout;
        operator.loggedIn = true;
        operator.viewingDashboard = true;

        assertEquals("Standard", layout);
    }

    @When("the operator changes the dashboard layout to {string} and saves the changes")
    public void the_operator_changes_the_dashboard_layout_to_kpi_compact_and_saves(String newLayout) {
        assertNotNull(operator, "Operator should exist");
        operator.layout = newLayout;
    }

    @Then("the system stores the customized dashboard layout {string} for operator id {string}")
    public void the_system_stores_the_customized_dashboard_layout_for_op01(String expectedLayout, String expectedId) {
        assertNotNull(operator, "Operator should exist");
        assertEquals(expectedId, operator.id);
        assertEquals(expectedLayout, operator.layout);
    }

    // --------------------------------------------------------------------
    // Scenario: Create reports from dashboard data
    // --------------------------------------------------------------------

    @Given("charging sessions exist for reporting: session {string} from {string} to {string} with energy {string} kWh at station {string} and session {string} from {string} to {string} with energy {string} kWh at station {string}")
    public void charging_sessions_exist_for_reporting_session_from_to_with_energy_k_wh_at_station_and_session_from_to_with_energy_k_wh_at_station(
            String s1Id, String s1Start, String s1End, String s1Energy, String s1Station,
            String s2Id, String s2Start, String s2End, String s2Energy, String s2Station
    ) {
        sessions.clear();

        sessions.add(new ChargingSession(
                s1Id, s1Station, s1Start, s1End,
                Double.parseDouble(s1Energy), "City Center"
        ));
        sessions.add(new ChargingSession(
                s2Id, s2Station, s2Start, s2End,
                Double.parseDouble(s2Energy), "Highway Station"
        ));

        assertEquals("5001", s1Id);
        assertEquals("24.0", s1Energy);
        assertEquals("11", s1Station);

        assertEquals("5002", s2Id);
        assertEquals("18.0", s2Energy);
        assertEquals("20", s2Station);
    }

    @When("the operator generates a report for the time period from {string} to {string}")
    public void the_operator_generates_a_report_for_the_time_period(String from, String to) {
        assertNotNull(operator, "Operator should exist");
        assertTrue(operator.loggedIn, "Operator should be logged in");

        lastReport = new Report();
        lastReport.fromDate = from;
        lastReport.toDate = to;
        lastReport.numberOfSessions = sessions.size();
        lastReport.totalEnergy = sessions.stream()
                .mapToDouble(s -> s.energyKWh)
                .sum();

        assertEquals("2025-11-15", from);
        assertEquals("2025-11-21", to);
    }

    @Then("the system creates a report with key performance indicators including total energy {string} kWh and number of sessions {string} for this period")
    public void the_system_creates_a_report_with_key_performance_indicators_including_total_energy_k_wh_and_number_of_sessions_for_this_period(
            String expectedEnergy, String expectedSessions
    ) {
        assertNotNull(lastReport, "Report should have been generated");

        double energy = Double.parseDouble(expectedEnergy);
        int sessionsCount = Integer.parseInt(expectedSessions);

        assertEquals(energy, lastReport.totalEnergy, 0.0001);
        assertEquals(sessionsCount, lastReport.numberOfSessions);
    }

    // --------------------------------------------------------------------
    // Scenario: Receive error messages
    // --------------------------------------------------------------------

    @Given("charging stations are monitored by the system and the location {string} with id {string} has a charging station {string} \\(DC, IN_BETRIEB_FREI)")
    public void charging_stations_are_monitored_and_city_center_has_station_11(
            String locName, String locId, String stationId
    ) {
        locations.clear();

        Location loc = new Location(locId, locName);
        loc.stations.add(new ChargingStation(stationId, "DC", "IN_BETRIEB_FREI", locId));
        locations.add(loc);
    }

    @When("a fault with code {string} occurs at charging station {string}")
    public void a_fault_with_code_err_sts_11_occurs_at_station_11(String faultCode, String stationId) {
        lastErrorMessage = "Fault " + faultCode + " at station " + stationId;
        errorStationId = stationId;

        Location loc = locations.stream()
                .filter(l -> l.stations.stream().anyMatch(s -> s.id.equals(stationId)))
                .findFirst()
                .orElse(null);
        if (loc != null) {
            errorLocationName = loc.name;
        }
    }

    @Then("the system shows an error message {string} in the dashboard")
    public void the_system_shows_an_error_message_in_the_dashboard(String expectedMessage) {
        assertEquals(expectedMessage, lastErrorMessage);
    }

    @Then("the operator can see that station {string} at location {string} is affected")
    public void the_operator_can_see_which_station_is_affected(String expectedStationId, String expectedLocationName) {
        assertEquals(expectedStationId, errorStationId);
        assertEquals(expectedLocationName, errorLocationName);
    }
}
