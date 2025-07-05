Feature: Mocked User Service Tests

  Scenario: Retrieve a user from a mocked service
    Given I have a mocked user service returning user with ID 99, email "mock.user@example.com", first name "Mock", and last name "User"
    When I request user with ID 99 from the mocked service
    Then the mocked user response should contain ID 99, email "mock.user@example.com", first name "Mock", and last name "User"