Feature: User handles data using the app
  Data handling

  Scenario: User uploads data from file
    Given the user has a data file to upload
    And the file is the right type and correctly formatted
    When the user uploads the file
    Then the system adds the new data to the current data
    And prompts the user to decide what to keep for each duplicate entry