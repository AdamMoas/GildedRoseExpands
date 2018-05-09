# Gilded Rose Expanded API


## Running the Project

### Requirements

- JDK 1.8
- Gradle 2.3+
- Git
- Intellij IDEA
- Postman

### Setup

- Clone the repository or download and unzip
- Start Intellij IDEA or Spring compatible IDE
- Select File -> Import Project -> Locate build.gradle
- Follow dialogs until import is complete

### Running Locally

- Locate the Main.java file: src/main/java/Main.java
- Press the "Play" icon to the left of the Main class. Alternatively, right click the Main.java file and select Run "Main".
- The Spring Boot app will initialize a server instance, and is now ready to interact with the API.

### API Calls

In order to make API calls, a browser or Postman is required. A .keystore file has been generated for this project, but it is not signed, so a security warning will appear. If using Postman, please go to "Spanner" icon -> Settings -> General -> Turn off SSL certificate verification.

localhost:8080 - If port 8080 is being occupied elsewhere while running this application, the port can be changed in: src/main/resources/environment.properties -> server.port

The API responses are formatted in JSON as it is a popular response format, is human readable and I am familiar with it.

### Limitations & Production Considerations

As a prototype project, a number of standard practices that would be critical in a production application have been omitted:

- The authentication credentials are currently hardcoded. This is only for purposes of a demo, and would never be done in a production-ready application.
- viewItem and viewAllItems both track surge pricing and update all values. It would be better if a single item view was to update only its own, individual surge price if the view requirements were met, but as this was out of scope for this exercise, it was not implemented.
- Real database usage - this was out of scope as well, so a simple hashMap implementation was used instead.

### Viewing the list of available items

Viewing the list of items does not require authentication, and can be viewed with the below GET method:

GET: `https://localhost:8080/items/`

A response such as the below should be returned:

```
[
    {
        "name": "Plant",
        "description": "Plants are mainly multi-cellular, predominantly photosynthetic eukaryotes of the kingdom Plantae.",
        "price": 1
    },
    {
        "name": "Car",
        "description": "Ferrari 458",
        "price": 144000
    },
    {
        "name": "Book",
        "description": "A book is a series of pages assembled for easy portability and reading.",
        "price": 12
    },
    {
        "name": "Food",
        "description": "Provides nutritional support for an organism",
        "price": 300
    }
]
```

#### Viewing a single item

Although there was not deliverable requested for viewing a single item, it felt natural to add this feature in as an extension in the case of a user wanting to view a single item's details if a UI was to be built.

To access the viewItem API call, a 2-step authentication process is required: 

1. Use the below POST request to obtain a token:

In order to authenticate using the OAuth2, basic authentication is required. In Postman's Authentication tab, please provide the below credentials:

- Authentication Type: Basic Auth
- Username: authorized
- Password: authorizedpassword
 
POST: `https://localhost:8080/oauth/token?grant_type=password&username=adam&password=qazqaz123`

This will present a response containing: `"access_token": "token-value"`

2. Copy the above token-value, this will be required for our next request.

3. Paste your token-value into the below request and send the GET request.

GET: `https://localhost:8080/items/view?name=Car&access_token=token-value`

This should present you with the below response:

```
{
    "name": "Car",
    "description": "Ferrari 458",
    "price": 144000
}
```

#### Surge Pricing

A requirement for the application was to provide an Uber-like surge pricing feature that would increase all item prices by 10% if the items had been viewed more than 10 times within a 60 minute window.

To achieve this, a record of each item view’s GET request had to be recorded, along with its current window within the time period. If the number of calls exceeded 10 calls and was within the 60 minute window from the initial call, then the prices of each item would be incremented by 10%.

First consideration is that a counter for each view must be maintained from the first call of a new 60 minute window. This is assuming no calls have been made in the past 60 minutes. The next consideration is what to do if the call requirements are not met within the window. 

One such example situation is, given 1 call could be made in the first minute of a new window, and 9 calls are then made on the 59th minute, once the 60 minutes expire, the window would reset, however, this would fall short on the requirement, as if 2 calls were made minutes later, the surge price should be applied, as the first call in the 59th minute should now be considered a new window starting point.

To solve this problem, a reference to the last call must be kept up-to-date, allowing for the latest 60 minute window to be in sync with the surge price triggering call, in this case from the 11th GET request.


#### Buying an item

To access the buy API call, a 2-step authentication process is required: 

1. Use the below POST request to obtain a token:

In order to authenticate using the OAuth2, basic authentication is required. In Postman's Authentication tab, please provide the below credentials:
- Authentication Type: Basic Auth
- Username: authorized
- Password: authorizedpassword

POST: `https://localhost:8080/oauth/token?grant_type=password&username=adam&password=qazqaz123`

This will present a response containing: `"access_token": "token-value"`

2. Copy the above token-value, this will be required for our next request.

3. Paste your token-value into the below request and send the GET request.

GET: `https://localhost:8080/items/buy?name=Car&access_token=token-value`

This should present you with the below response:

```
{
    "result": "Purchase successful"
}
```

I have hardcoded each item to have 6 quantity arbitrarily. If the buy request is made 6 times, a subsequent call will result in the below response: 

```
{
    "result": "Item out of Stock"
}
```

##### Unauthorized access attempts

If the above steps were not followed a 401 unauthorized response should be displayed. 

For the purpose of this demo, Oauth2 was utilized, along with hardcoded username and password values that were used- in a production app this would not be the case. 

###### Token Expiration

Tokens are valid for 4 minutes, after which a new token must be requested using POST: `https://localhost:8080/oauth/token?grant_type=password&username=adam&password=qazqaz123`

```
{
    "error": "invalid_token",
    "error_description": "Access token expired: token-value"
}
```

### Testing

A number of integration and unit tests were added to this project. Verifying the surge pricing calculation was done by verifying the expected results against a given value and the surge price percentage.
To test the integration of the API, the mockMvc Spring library was used. This enabled all API calls to be tested with the required authentication and data to be passed in.

- Locate the Main.java file: src/test/java/
- Right click the src/test/java folder and select Run 'All Tests'
- All tests should be executed.

#### Project Structure & Architecture

As the Spring framework was utilized in the development of the application, the core component was dependency injection as an IoC container architecture. This was utilized with @Autowiring / Injecting the dependencies - I chose to avoid field injection as this is seen as poor practice, and instead made sure that constructor injection was used instead.
Another module used was the Spring JPA module, this was used for the ORM implementation for the Item POJO. Spring also handles the web aspect of the development with the Web MVC design pattern along with providing the mockMVC module, allowing for easier integration testing.

Although many of the project folders contain a single file, this was done in mind of scaling and adding features.

- `config`: Contains the SurgeConfig annotated class. It is used to provide parameters for the SurgePriceUtil class.
- `controller`: Contains the ItemController implementation of the ItemApi interface. This uses the ItemService and builds the API requests and provides the response on the JSON format.
- `data`: Contains the MockItemDatabase which instantiates a HashMap containing Item objects with values. It also contains the BuyItem result object that is used when returning the item's purchase success message.
- `exceptions`: Contains a RunTimeException handler to provide an appropriate message in the case of the Item being out of stock.
- `interfaces`: Contains the ItemApi interface. This utilizes the Retrofit library to map the ItemService's API calls to the correct URIs and converting them into a type-safe Java Object. 
- `model`: Contains the Item POJO along with the use of the Java Persistence API to model the Item object as database store-able entity.
- `security`: Contains configuration files Implementing AuthorizationServerConfigurerAdapter, OAuth2, and ResourceServerConfigurerAdapter required to setup user authentication. 
- `service`: Contains The ConfigurationService that has @Value mapping for default property values by referencing the properties assigned in the environment.properties file. It also contains the ItemService where the surge pricing, item quantity and mocked items are determined. 
- `utils`: Contains the SurgePrice utility class, providing the application with public methods to calculate the surge price percentages based on the requirements set.

### Authentication & Reasoning
The Oauth2 authentication library was used as it provided a robust implementation and the ability to use HTTPS, provided the keystore was included, which is ample for the purpose of this demo.
Although credentials should not be stored within hardcoded values, this was out of the scope of the project.

### Libraries Used

#### Spring Boot Framework
The Spring Framework is a Java framework that provides small, lightweight modules with functionality such as dependency injection, authentication, data persistence and mocked testing. Spring Boot is a further extension that enables quick deployment of application.

#### Retrofit
The Retrofit library converts HTTP API calls into type-safe Java interface. This was used when building the ItemApi class to provide correct API call paths for the app to pass in appropriate values.

#### OAuth2
OAuth 2 is an industry-standard protocol for authorization. Its primary focus is on client developer simplicity, and provides specific authorization flows for web applications. It was utilized to effectively provide a means of authentication for the buying use case of the application.