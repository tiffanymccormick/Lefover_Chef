# Leftover Chef 

A Spring Boot application that helps you find recipes based on your leftover ingredients. Input what you have in your fridge, and Leftover Chef suggests delicious meals to make!

## What you need to have installed already

- Java 17 or higher
- Maven 3.6.3 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, or VS Code)

1. Clone the repository:
```bash
git clone https://github.com/yourusername/leftover-chef.git
cd leftover-chef
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Open your browser and navigate to:
```
http://localhost:8080
```

## Features

- Search recipes by ingredients
- Match scoring system
- Custom recipe magnets
- Responsive design
- Local storage for recipe history

## Project Structure

```
leftoverChef/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/leftoverchef/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── *.html
│   │       │   ├── *.js
│   │       │   └── *.css
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Configuration

The application uses the following default configuration in `application.properties`:

```properties
server.port=8080
spring.servlet.multipart.max-file-size=32MB
spring.servlet.multipart.max-request-size=32MB
```

## Development

1. Import the project into your IDE
2. Make sure you have the Lombok plugin installed
3. Run the `LeftoverChefApplication` class

## Testing

Run the tests using:
```bash
./mvnw test
```

## Building for Production

Create a production-ready JAR:
```bash
./mvnw package
```

The JAR will be created in the `target` directory.

## Running in Production

Run the production JAR:
```bash
java -jar target/leftover-chef-0.0.1-SNAPSHOT.jar
```

## Tech Stack

- Backend: Spring Boot 3.2.3
- Frontend: HTML5, CSS3, JavaScript
- Build Tool: Maven
- Testing: JUnit 5
- Data Format: JSON
