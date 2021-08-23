# new feature
# Tags: optional

Feature: EFax can be sent successfully

  Scenario: Verify eFax can be sent
    Given an eFax service is available
    When an eFax with attachments is sent
    Then verify the eFax is sent successfully