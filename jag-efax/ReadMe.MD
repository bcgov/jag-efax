# eFax Service

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