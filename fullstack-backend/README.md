# Marketplace Backend

This is the backend service for the Marketplace project, built with Spring Boot and Java 21.

## API Documentation

The API is documented using OpenAPI/Swagger and is available when running the application at:
```
http://localhost:8080/swagger-ui/index.html
```

## Javadoc

API Javadoc documentation is automatically generated and published to GitHub Pages on each push to the main branch.

You can access the latest Javadoc at:
```
https://[your-github-username].github.io/[your-repo-name]/
```

## Development Setup

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose (for database)

### Running locally
1. Clone the repository
2. Set up environment variables in `.env` file
3. Start the database: `docker-compose up -d`
4. Run the application: `./mvnw spring-boot:run`

## Build and Test

```bash
# Run tests
./mvnw test

# Build the project
./mvnw clean package

# Generate Javadoc locally
./mvnw javadoc:javadoc
```
After running the Javadoc generation, the documentation will be available at `target/site/apidocs/index.html`. 