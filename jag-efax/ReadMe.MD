# eFax Service

This is a SOAP based service responsible for consuming a request to send a fax and then 
monitoring for it's successful tranmission to the intended fax recipient. 

This service relies on REDIS to maintain the 'in flight' fax requests and MS Graph API 
to interoperate with a designated service account outlook mailbox for sending the fax request to a faxing service endpoint 
and receiving the notification that the fax was (or wasn't) successfully sent.  

### Environmental Variables

| Key        | Description     |
| ------------------- | ---------------------- |
| CALLBACK_ENDPOINT | Justin Callback endpoint (Soap) |
| FAX_FORMAT | Fax service call format |
| PDF_ENDPOINT | Adobe Exchange Mgr endpoint |
| PDF_PASSWORD | Adobe Exchange Mgr password |
| PDF_USERNAME | Adobe Exchange Mgr username |
| REDIS_HOST |  Redis host name |
| REDIS_PASSWORD | Redis host password |
| REDIS_PORT | Redis host port | 
| MSG_CLIENTID      | MS Graph API Client Id     |
| MSG_AUTHORITY | MS Graph API Authority |
| MSG_SECRETKEY | MS Graph API Secret Key |
| MSG_EXPIRY_THRESHOLD | Expiry Days Threshold |
| MSG_ENDPOINT | MS Graph API Endpoint |
| MSG_EMAIL_ACCOUNT | Email Account Scoped for MS Graph API |
| MSG_SAVE_IN_SENT | Boolean 'true' or 'false' |
| MSG_TEMP_DIR | Java temp directory - defaults to Java Temp if not set |
| MSG_ADMIN_EMAIL | Azure Application Admin email (comma separated) |
| MSG_APP_NAME | Azure Appliction Name |

### Running locally 

1. Launch the Redis server container using Docker Desktop. 

If you need to install the latest Redis Server: 

```
	docker run -d --name redis -p 6379:6379  redis/redis-stack-server:latest 
```  

Once the container is up and running, set the admin password by using the redis-cli:  

```
	redis-cli  
	config set requirepass admin  
	quit  
```	 

2. Start the eFax application 

```
mvn spring-boot:run

```

3. Triggering a dummy SOAP request when running locally  

- Load the POSTMAN test collection from the 'postman' folder.
- Launch the test 'POST eFax Request 1'.  

Notes for populating the POSTMAN test call, Local, eFax Request 1: 

- Transport node holds the receiving fax phone number (this should be a real fax number) 
- Attachments must be available as a URL on a server accessible from the machine this service is running on.
- From/To nodes are just names - not email addresses. 
- DateTime node is populated with a ISO 8601 UTC time with UTC offset, example: '2024-09-20T11:04:18.522-07:00'  
- Set 'jobId' and 'subject' to the same unique value for each call. 
- Set 'body' node to either empty value or HTML enclosed in a CDATA tags.  
- Response from the call should be a HTTP Status 202 (accepted).
 

