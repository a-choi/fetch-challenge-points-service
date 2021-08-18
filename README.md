
## Fetch Rewards Coding Exercise - Backend Software Engineering

###### Author - [Arthur Choi](https://github.com/a-choi)

---

# HOW TO RUN


#### Prerequisites
- git
- [Docker](https://www.docker.com/products/docker-desktop) _*or*_ [JDK11](https://openjdk.java.net/projects/jdk/11/) + [Maven 3.8.x](https://maven.apache.org/install.html)

From command line/terminal (bash, zsh, etc):
1) `git clone https://github.com/a-choi/fetch-challenge-points-service.git`
   

2) `cd path/to/fetch-challenge-points-service`
   

3) If using Docker:
   - `docker build -t <some_tag_name> .`
   - `docker run --rm -p 8080:8080 <some_tag_name>`
      - `--rm` tears down container when run exits 
   - This builds an image ~500MB, make sure to delete via Docker Desktop UI or cli when finished to free memory 
      - `docker rmi <some_tag_name>:latest`
   

4) If using Maven & JDK11:
   - `mvn clean install` or `mvn clean install -DskipTests`
   - Import/open with IntelliJ (unsure of other IDE's) and run
   - Alternatively, `mvn spring-boot:run`
   

5) Accessing the service:
   - Any HTTP client request to `http:localhost:8080/points/user`
   - For HTTP methods / request specifications, please view `http://localhost:8080/demo` containing API documentation  
   - Alternatively you can use the [OpenAPI](https://swagger.io/docs/specification/about/) integration in-browser to test the API
      - Navigate to `http://localhost:8080/demo` to send requests from interactive UI

5) CTRL + C to quit


6) To view & manage the underlying database in a console, navigate to `http:localhost:8080/h2-console` in a browser
   - database url: `jdbc:h2:mem:points_db`
   - username: `sa` (no password)

---

### _**Assumptions/Clarifications**_:
1) From the rules for determining what points to "spend" first:
    >We want no payer's points to go negative
    
    - One payer can owe points to many users
    - "_payer's points_" `==` points one payer owes to one user
    - "_payer's points_" `!=` sum of points one payer owes to all their users
    - Spending points `!=` new transaction 


2) Points can only be whole numbers (`long`/`int`)


3) API base path is `/points/user/{userId}`
   - `userId` is optional, when not provided will use a default user
      - See "_Bad Requests_" below
   - See `src/main/resources/schema.sql` and `src/main/resources/data.sql` for schema and pre-loaded test data, respectively


5) Bad Requests
   - References to payers/users that do not exist
      - There are *3* `payers` and *6* `users` in the database upon initialization
         - (see `src/main/resources/data.sql`)
         - Unless modified from the database console (see above), any request referring to non-existent `payers` or `users` will result in a bad request
   - Transaction amounts _**cannot**_ be `null`
   - Cannot spend negative points
---

Side note, if using Maven/JDK locally, you can run `mvn clean verify` and navigate to `target/site/index.html` to view a test report
