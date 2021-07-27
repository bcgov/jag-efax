[![Maintainability](https://api.codeclimate.com/v1/badges/aeeda29276d568472dd1/maintainability)](https://codeclimate.com/github/bcgov/jag-efax/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/aeeda29276d568472dd1/test_coverage)](https://codeclimate.com/github/bcgov/jag-efax/test_coverage)
# jag-efax
GitHub repository for the BPEL eFax Replacement project

## Technology Stack Used
| Layer   | Technology | 
| ------- | ------------ |
| Presentation | none |
| Business Logic | Java |
| Web Server | SpringBoot, Microsoft Exchange |

## Deployment (Local Development)

### Developer Workstation Requirements/Setup
**Recommended Configuration**
- 16 GB RAM
- SSD
- Core i7 7th generation or better
- Windows 10 Pro
- Docker
- Git 

## Configuration

### Webservice

From Docker or OpenShift, these environment variables can be used to configure the Webservice.

| Env Variable | Description |
| --- | --- |
| EXCHANGE_ENDPOINT | Required. URL to the Microsoft Exchange Server instance. |
| EXCHANGE_USERNAME | Required. Username to use to log into Microsoft Exchange Server. |
| EXCHANGE_PASSWORD | Required. Password to use to log into Microsoft Exchange Server. |
| EXCHANGE_SAVE_IN_SENT | Optional. Set to true if emails/faxes should be saved in the Exchange SentItems folder. |
| CALLBACK_ENDPOINT | Required. Callback to the Justin SOAP callback service. |
| PDF_ENDPOINT | Required. URL to a PDF flattening service (used on PDFs with a version > 1.5) |
| PDF_USERNAME | Required. Username to use to log into the PDF flattening service. |
| PDF_PASSWORD | Required. Password to use to log into the PDF flattening service. |
| REDIS_HOST | Required. Hostname of a Redis instance to use as a queuing service. |
| REDIS_PORT | Required. Port of the Redis instance. |
| REDIS_PASSWORD | Required. Password to use to log into the Redis instance.. |



