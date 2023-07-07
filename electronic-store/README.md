# Electronic Store application

To run the project:
```
./mvnw org.springframework.boot:spring-boot-maven-plugin:run
```
To run the test:

```
./mvnw test
```

### Issues with this application
There are a few deficiencies with this application:

1. I am getting "Connection refused" in the test cases for ProductController, even though I can send GET requests to the local server.
1. As a result of the first point, I wasn't able to test create, update and delete features on the Product entity.
1. There are no test cases on the functions in BasketController.
1. There is no consideration on concurrency concerns.
1. The entity, controller and repository classes should be put in separate packages.
