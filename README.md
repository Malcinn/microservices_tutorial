# Spring Boot microservices 
Repository contains a sample application build on top of microservices architecture.

Link to Microservices tutorial:
https://www.youtube.com/playlist?list=PLSVW22jAG8pBnhAdq9S8BpLnZ0_jVBj0c

## Local environment configuration
### Local mongoDB docker container
1. Pull docker image: sudo docker pull mongo
2. Create container: sudo docker run -p 27017:27017 mongo
### Local mySQL docker container
1. Pull docker image: sudo docker pull mysql
2. Create container: sudo docker run -e MYSQL_ROOT_PASSWORD=docker_mysql -p 3306:3306 mysql
### Local keyCloak docker container
1. Create container: sudo docker run -p 8000:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.0 start-dev
2. Configure keyCloak:
   3. Log in to admin console localhost:8000 admin:admin
   4. Create realm with name: microservices-tutorial-realm
   5. Create Client
      6. name: api-gateway-client
      7. Authentication flow:
         8. Service accounts roles
      9. Go to credentials and copy client secret for test purposes we are using "'Client Credentials" OAuth authentication flow.
      10. Right now you can use client ID and client secret in postman app
#### To start up existing docker container
1. Check available containers: sudo docker ps -a
2. Start up container: sudo docker start CONTAINER_ID 


## Services
To start up each service use following command ./gradlew bootRun