@API
Feature: User Management API Tests

  @API
  Scenario: Get a list of users
    Given I set the base URI to "https://reqres.in/api"
    And I have a request for "users" endpoint
    When I send a GET request to "users" with query parameter "page" as "2"
    Then the response status code should be 200
    And the response should contain a list of users

  @API
  Scenario: Get a single user by ID
    Given I set the base URI to "https://reqres.in/api"
    And I have a request for "users" endpoint
    When I send a GET request to "users" for user with ID 1
    Then the response status code should be 200
    And the response should contain user with ID 1 and email "george.bluth@reqres.in"

  @API
  Scenario: Create a new user
    Given I set the base URI to "https://reqres.in/api"
    And I have a request for "users" endpoint
    And I set the request body with name "morpheus" and job "leader"
    When I send a POST request to "users"
    Then the response status code should be 201
    And the response should contain name "morpheus" and job "leader"
    And the response should contain an ID


