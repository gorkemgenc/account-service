# Project

This project is to create a simplified view of a commerce interface. It is a project which is an application running on the JVM that manages 
accounts, products and stores. Tis project is also run as a standalone server.

## Project Scope

This project was to create a simplified view of a commerce interface. As mentioned above, it can run as a standalone server. In this application, you can manage accounts,
products and stores. You can create account with default 0.0 balance and you can manage account by adding amount (deposit) and by removing amount (withdraw). Additionally,
you can purchase available products according to account's balance and products' availability. Moreover, you can add new product, delete product and update product through this
application. If you want to list all transactions for given account, it is also possible. Another thing is that you can list all products and you can list all available products
via Store part. In Store part, user can buy product if both user's balance and product are valid. There are some restrictions for this application and you can reach these restrictions
in below parts.

## Technologies

This application was developed with Java9, Spring Boot and Maven2. You can find the detail information regarding the this project such as requirements, running procedure, 
testing procedure, api endpoints, out of scope and scalable system scope. There is an api rate limiter in this project to handle some dangerous attacks. Default rate limit of
each second is 20 but it can be easily changed. You can reach all Api via Swagger which is Api documentation tool. Moreover, thanks to Flyway, database migration is also added
to this project.  

Technologies was used:

1. Java9
2. Spring Boot
3. Maven
4. Spring Data JPA
5. MySQL (for database)
6. H2 (in memory database for unit test)
7. Swagger (for API endpoint tracking)
8. RateLimiter
9. Actuator
10. slf4j
11. Maven
12. JUnit
13. Flyway (for db migration)
14. ObjectMapper (from entity to dto converter)

## Requirements and steps to run this application
1. Install Java 9
2. Maven to build the application. 
3. Download and install MySQL server
4. Connect to the MySQL server
5. Make mysql configurations in application.properties

```
CHANGE THIS AREA IN APPLICATON.PROPERTIES

spring.datasource.url = jdbc:mysql://localhost:3306/yourschema <------------- CHANGE THIS AREA   
spring.datasource.username = *****                             <------------- CHANGE THIS AREA
spring.datasource.password = *****                             <------------- CHANGE THIS AREA
```

6. I created a database schema which name is "accountdb", if you want you can create a schema like

```
create schema accountdb;
use accountdb;
```

Otherwise you need to create your own database schema. If you cannot create your own schema, you face with an error.


6. There is a db migration for this project. This means you don't need to run sql scripts manually. You can also check database migration history in "flyway_schema_history"
table. If you want to look the sql scripts, you can reach from "account-service/src/main/resources/db.migration" path. Additionally, there is no any index on database now,
but we can decrease the minimum latency with adding correct index.

7. run these commands respectively. First command helps us to run all unit tests inside the application. Unit tests includes ControllerTest, ServiceTest and RepositoryTest.
There are 64 different unit test for these parts. The second command helps us to run project.

``` 
mvn clean test 
mvn spring-boot:run
``` 

## Running

If you want to run this application you need to follow the "Requirements and steps to run this application" part. You should run the "AccountServiceApplication" class and 
this application is running on port 8080. You can change this port from application.properties.

Additionally you can use swagger ui to trace api endpoints.

``` 
http://localhost:8080/swagger-ui.html#/
``` 

## Testing

There are 64 different unit tests regarding this application. For the enterprise application, we can implement unit test for all functionalities. 
If you want to check unit test, you can see the details in test folder. Both Repository, Service and Controller elements have unit test.

Run test command

``` 
mvn clean test
``` 

Additionally you can check in memory database (H2) via http://localhost:8080/h2-console

## Endpoints

You can find the required parameters and detail information at swagger ui. 

``` 
--- ACCOUNT CONTROLLER----

Creating a new account
@POST /account/create
Body: None
Response:
{
  "account": {
     "id": 1,
     "balance": 0
   }
}


Depositing funds to an existing account
@POST /account/deposit
Body:
{
  "accountId": 1,
  "amount": 3000
}
Response:
{
  "account": {
    "id": 1,
    "balance": 3000
  }
}


Withdrawing funds from an existing account
@POST /account/withdraw
Body:
{
  "accountId": 1,
  "amount": 1231
}
Response:
{
  "account": {
    "id": 1,
    "balance": 1769
  }
}



List all transactions made on an account
@POST /account/listTransactions
Body:
{
  "accountId": 1
}
Response:
{
  "transactions": [
  {
    "id": 1,
    "accountId": 1,
    "amount": 3000,
    "date": "2018-10-01T10:55:22.958+0000",
    "type": "DEPOSIT"
  },
  {
    "id": 2,
    "accountId": 1,
    "amount": 1231,
    "date": "2018-10-01T10:55:24.845+0000",
    "type": "WITHDRAW"
  },
  {
    "id": 3,
    "accountId": 1,
    "amount": 250,
    "date": "2018-10-01T10:56:41.517+0000",
    "type": "PURCHASE",
    "productId": 1
  }]
}


--- PRODUCT CONTROLLER ---

Create a new product for purchase
@POST /product/create
Body:
{
  "product": {
    "name": "Salad",
    "price": 250,
    "count": 200 
  }
}
Response:
{
  "product": {
    "id": 1,
    "name": "Salad",
    "price": 250,
    "count": 200
  }
}


Getting the whole list of available products
@GET /product/list
Body: None
Response:
{
  "products": [
  {
    "id": 1,
    "name": "Salad",
    "price": 250,
    "count": 200
  }]
}


Delete an existing product
@POST /product/delete
Body:
{
  "product": {
  "id": 1
}
}
Response:
{
  "product": {
    "id": 1,
    "name": "Salad",
    "price": 250,
    "count": 199
  }
}


Update an existing product
@POST /product/update
Body:
{
  "product": {
    "id": 1,
    "name": "Orange",
    "price": 1000,
    "count": 200
  }
}
Response:
{
  "product": {
    "id": 1,
    "name": "Orange",
    "price": 1000,
    "count": 200
  }
}


--- STORE CONTROLLER ---

List currently available products
@GET /store/list
Body: None
Response:
{
  "products": [
  {
    "id": 1,
    "name": "Orange",
    "price": 1000,
    "count": 200
  },
  {
    "id": 2,
    "name": "Bread",
    "price": 100,
    "count": 200
  }]
}


Buy a product using an account and its available balance
@POST /store/buy
Body:
{
  "accountId": 1,
  "productId": 1
}
Response:
{
  "transaction": {
    "id": 3,
    "accountId": 1,
    "amount": 250,
    "date": "2018-10-01T10:56:41.517+0000",
    "type": "PURCHASE",
    "productId": 1
  }
}

``` 

## Out of Scope

1. ) Currency and currency convert procedure
2. ) User based approach (just work with accountId)
3. ) Authentication
4. ) Authorization
5. ) Another security approaches (such as JWT)
6. ) Index creation
7. ) Integration test
8. ) Paging and Sorting
9. ) Api Version mechanism

## Restrictions and Choices

1. ) For balance and amount, we are using BigDecimal. BigDecimal helps us to keep huge data. Also we can use float numbers
2. ) Account id should be positive
3. ) Account balance shouldn't be smaller than 0
4. ) For deposit operation, accountId must be valid and amount must be greater than zero
5. ) For withdraw operation, accountId must be valid and amount must be greater than zero
6. ) For withdraw operation, if account balance is smaller than withdraw amount, it gives error
7. ) For listTransaction operation, if there is no any transaction of account, it returns empty list
8. ) Each product name should be unique
9. ) You cannot create or update product with existing product name
10. ) You cannot create or update product if price is smaller than or equals to zero
11. ) You cannot create or update product if product count is smaller than zero
12. ) If you want to delete product, product count should be positive
13. ) If you want to delete product, product id should be valid
14. ) You can list just available product in the store. Available means that product count should be greater than zero
15. ) If you want to buy product in the store, accountId and productId should be valid
16. ) If you want to buy product in the store, product count should be positive
17. ) If you want to buy product in the store, your account balance should be enough

Notice that, in this application, there are some custom Error Messages regarding the possible errors. 


## May be Good

Notice that for the transactional operation, if you are communicating with another service, we can use globaltransactionid coming from
other service. This another service can provide a globaltransactionid to trace its transactions. Notice that globaltransactionid should be unique
and it can be UUID. Additionally, we are returning 200 Http Status Code for success operations but we can return another 2XX Http Status Codes
such as 201 (created). Additionally, we can add currency mechanism into this project

## Think as a Large System

If we think this system is a large scalable system we can use microservices. For this application,
we need to ensure that this system supports high availability, high reliability and minimum latency. We can use globalId
for each transaction and this should be unique in our system. It can be UUID and it helps us to ensure transaction global id
is unique. For other keys, we can use KGS(Key generation service). KGS serves pregenerated keys to another services and
we can also ensure each key is unique thanks to KGS. Microservice helps us to scale our system easily and we can easily increase and
decrease heavy load application instance. If system is traffic-heavy, we can create new instance otherwise 
we can decrease microservice instance. Another important part is load balancer. We can use hardware load balancer
such as NGINX at the first part of the system. NGINX can be located between client and web server. For another parts we 
can use software load balancer. (Between web server and application server, between application server and database, between
application server and cache). Another important point is for every server, the best approach is having 3 more replicas.
We can use Redis or Memcache for cache mechanism and it can be global(shared) cache mechanism. We can decide to setup
Redis to different servers or to application servers. We can use LRU for caching mechanism but we need to decide the 
cache structure very carefully since there are a lot of transaction operations for this application. Another part is 
we can use message broker like Kafka for payment procedure. Kafka acts as a queue and we can use Kafka at payment API 
Gateway part. For each payment service can get responsible queue task from Kafka topic. For this system, our database
should support ACID mechanism because we don't have a chance to make a mistake in the account operations. Good approach 
is using MySQL or PostGreSQL. These are supporting ACID properties in default. Another important procedure is we can
log every step to follow the transactions. We can use monitoring application such as Datadog and alert mechanism to eliminate
possible problems. Moreover, If you are using cloud service such as AWS, Azure or Google Cloud Platform, they automatically
support %99 availability. But they are using Round Robin approach for load balancer procedure. Round robin approach
stops sending a request if server is death with Round Robin procedure but if your server is heavy, round robin
approach cannot handle the this problem and continue to send request to this server. You can implement intelligent
Round Robin algorithm to your own load balancer. 




 








