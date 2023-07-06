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
There are a few issues with this application:

1. The ProductRepository cannot be initialized, whether @autowired annotation is used or not. If @autowired annotation is on, the code wouldn't run.
1. As a result of the first point, only the SimpleController is working.
1. Also as a result of the first point, only the contextLoads test passes.
1. There is no consideration on concurrency.
