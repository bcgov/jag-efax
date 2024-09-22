# eFax Service

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