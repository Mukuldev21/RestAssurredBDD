package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class UserSteps {

    // Base URL for the ReqRes.in API
    private static final String BASE_URL = "[https://reqres.in/api](https://reqres.in/api)";

    // Rest Assured objects to hold request and response
    private RequestSpecification request;
    private Response response;

    // Mock object for UserService to demonstrate API mocking
    private UserService userServiceMock;

    /**
     * Hook that runs before each scenario.
     * Initializes Rest Assured base URI and sets up Mockito mocks.
     */
    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given(); // Initialize request specification for each scenario.

        // Initialize Mockito mocks for scenarios that use mocking
        userServiceMock = Mockito.mock(UserService.class);
        // If you were using @InjectMocks, you would call MockitoAnnotations.openMocks(this);
        // For this example, we're manually injecting or using the mock directly.
    }

    /**
     * Hook that runs after each scenario.
     * Resets Rest Assured settings to default to avoid interference between scenarios.
     */
    @After
    public void teardown() {
        RestAssured.reset(); // Resets baseURI, basePath, etc.
    }

    // --- Real API Test Step Definitions ---

    @Given("I set the base URI to {string}")
    public void iSetTheBaseURITo(String uri) {
        RestAssured.baseURI = uri;
        System.out.println("Base URI set to: " + uri);
    }

    @Given("I have a request for {string} endpoint")
    public void iHaveARequestForEndpoint(String endpoint) {
        request = RestAssured.given().log().all(); // Log all request details for debugging.
        System.out.println("Request initialized for endpoint: " + endpoint);
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        response = request.when().get(endpoint);
        System.out.println("GET request sent to: " + endpoint);
        response.then().log().all(); // Log all response details for debugging.
    }

    @When("I send a GET request to {string} with query parameter {string} as {string}")
    public void iSendAGETRequestToWithQueryParameterAs(String endpoint, String paramName, String paramValue) {
        request.queryParam(paramName, paramValue);
        response = request.when().get(endpoint);
        System.out.println("GET request sent to: " + endpoint + " with query param: " + paramName + "=" + paramValue);
        response.then().log().all();
    }

    @When("I send a GET request to {string} for user with ID {int}")
    public void iSendAGETRequestToForUserWithID(String endpoint, int userId) {
        response = request.when().get(endpoint + "/" + userId);
        System.out.println("GET request sent to: " + endpoint + "/" + userId);
        response.then().log().all();
    }

    @Given("I set the request body with name {string} and job {string}")
    public void iSetTheRequestBodyWithNameAndJob(String name, String job) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("job", job);
        request.contentType("application/json").body(body);
        System.out.println("Request body set: " + body);
    }

    @When("I send a POST request to {string}")
    public void iSendAPOSTRequestTo(String endpoint) {
        response = request.when().post(endpoint);
        System.out.println("POST request sent to: " + endpoint);
        response.then().log().all();
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
        System.out.println("Verified status code: " + statusCode);
    }

    @Then("the response should contain a list of users")
    public void theResponseShouldContainAListOfUsers() {
        response.then().body("data", notNullValue());
        response.then().body("data", hasSize(greaterThan(0)));
        response.then().body("data[0].id", notNullValue());
        response.then().body("data[0].email", notNullValue());
        System.out.println("Verified response contains a list of users.");
    }

    @Then("the response should contain user with ID {int} and email {string}")
    public void theResponseShouldContainUserWithIDAndEmail(int userId, String email) {
        response.then().body("data.id", equalTo(userId));
        response.then().body("data.email", equalTo(email));
        System.out.println("Verified user ID " + userId + " and email " + email + " in response.");
    }

    @Then("the response should contain name {string} and job {string}")
    public void theResponseShouldContainNameAndJob(String name, String job) {
        response.then().body("name", equalTo(name));
        response.then().body("job", equalTo(job));
        System.out.println("Verified name " + name + " and job " + job + " in response.");
    }

    @Then("the response should contain an ID")
    public void theResponseShouldContainAnID() {
        response.then().body("id", notNullValue());
        System.out.println("Verified response contains an ID.");
    }

    // --- API Mocking Step Definitions ---

    /**
     * Represents a simplified User object for mocking purposes.
     */
    public static class User {
        private int id;
        private String email;
        private String firstName;
        private String lastName;

        // Constructor
        public User(int id, String email, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters (needed for assertions)
        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    /**
     * A simple service interface that would typically make API calls.
     * We will mock this interface.
     */
    public interface UserService {
        User getUser(int id);
    }

    /**
     * Given step to set up the mock behavior for a user service.
     * This simulates what the API would return without making a real call.
     */
    @Given("I have a mocked user service returning user with ID {int}, email {string}, first name {string}, and last name {string}")
    public void iHaveAMockedUserServiceReturningUser(int id, String email, String firstName, String lastName) {
        User mockUser = new User(id, email, firstName, lastName);
        // Configure the mock: when getUser(id) is called, return the mockUser object.
        Mockito.when(userServiceMock.getUser(id)).thenReturn(mockUser);
        System.out.println("Mocked UserService to return user ID: " + id);
    }

    /**
     * When step to simulate calling the mocked user service.
     * In a real application, this would be where your application code calls the service.
     */
    @When("I request user with ID {int} from the mocked service")
    public void iRequestUserWithIDFromTheMockedService(int id) {
        // Call the mocked service method. This will return the predefined mockUser.
        User retrievedUser = userServiceMock.getUser(id);
        // Store the retrieved user in a response-like object for later assertions,
        // or directly assert if the scenario is simple.
        // For demonstration, we'll use a simple map to simulate a response body for assertion.
        Map<String, Object> mockResponseMap = new HashMap<>();
        mockResponseMap.put("id", retrievedUser.getId());
        mockResponseMap.put("email", retrievedUser.getEmail());
        mockResponseMap.put("first_name", retrievedUser.getFirstName());
        mockResponseMap.put("last_name", retrievedUser.getLastName());
        // We'll store this in a generic map to mimic a JSON response structure for assertion.
        // In a real scenario, you might have a dedicated object or a more complex mock setup.
        response = RestAssured.given().body(mockResponseMap).get(); // Dummy GET to populate 'response' for 'then' steps.
        System.out.println("Requested user ID " + id + " from mocked service. Mocked data: " + mockResponseMap);
    }

    /**
     * Then step to verify the data received from the mocked service.
     */
    @Then("the mocked user response should contain ID {int}, email {string}, first name {string}, and last name {string}")
    public void theMockedUserResponseShouldContain(int id, String email, String firstName, String lastName) {
        // Assertions against the 'response' object that was populated by the mocked data.
        Assert.assertEquals(id, response.jsonPath().getInt("id"));
        Assert.assertEquals(email, response.jsonPath().getString("email"));
        Assert.assertEquals(firstName, response.jsonPath().getString("first_name"));
        Assert.assertEquals(lastName, response.jsonPath().getString("last_name"));
        System.out.println("Verified mocked user response.");
    }
}
