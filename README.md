# Fullstack Marketplace Application

A modern, full-stack marketplace application built with Vue.js and Spring Boot. This project implements a robust e-commerce platform with real-time features, secure authentication, and a comprehensive API.

## Project Structure

The repository is organized into two main directories:

```
.
├── fullstack-frontend/     # Vue.js frontend application
└── fullstack-backend/      # Spring Boot backend application
```

## Technologies Used

### Frontend (`fullstack-frontend/`)
- **Framework**: Vue.js 3 with TypeScript
- **Build Tool**: Vite
- **State Management**: Pinia
- **API Client**: Axios
- **Real-time Communication**: STOMP.js, SockJS, WebSocket
- **Payment Integration**: Vipps MobilePay SDK
- **Testing**: Vitest
- **Code Quality**: Biome (linting and formatting)
- **API Generation**: Orval
- **Internationalization**: Vue I18n
- **Routing**: Vue Router
- **Package Manager**: pnpm

### Backend (`fullstack-backend/`)
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Security**: Spring Security with JWT
- **WebSocket**: Spring WebSocket
- **API Documentation**: SpringDoc OpenAPI
- **Validation**: Spring Validation
- **Development Tools**: 
  - Lombok
  - Spring Boot DevTools
  - Maven
  - Checkstyle
- **Testing**: 
  - Spring Boot Test
  - Spring Security Test
  - H2 Database (for testing)

## Development Setup

### Prerequisites
- Node.js (for frontend)
- Java 21 (for backend)
- MySQL
- pnpm (recommended for frontend)
- Maven (for backend)

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd fullstack-frontend
   ```

2. Install dependencies:
   ```bash
   pnpm install
   ```

3. Start the development server:
   ```bash
   pnpm dev
   ```

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd fullstack-backend
   ```

2. Configure your environment variables in `.env` file

3. Build and run the application:
   ```bash
   ./mvn spring-boot:run
   ```

## Development Tools and Workflow

### Frontend Development
- **Code Quality**: Uses Biome for linting and formatting
- **Git Hooks**: Husky for pre-commit hooks
- **API Generation**: Orval for TypeScript API client generation
- **Testing**: Vitest for unit testing
- **Biome**: 

### Backend Development
- **Code Style**: Checkstyle for code quality
- **Documentation**: Javadoc for API documentation
- **API Documentation**: SpringDoc OpenAPI for Swagger UI
- **Environment Management**: Spring Dotenv for environment variables

## API Documentation

The backend API is documented using SpringDoc OpenAPI. Access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Security

The application implements:
- JWT-based authentication
- Spring Security for authorization
- Environment-based configuration

## Testing

### Frontend Testing
```bash
cd fullstack-frontend
pnpm test
```

### Backend Testing
```bash
cd fullstack-backend
./mvn test
```

## Docker Support

The backend includes a `docker-compose.yml` file for starting a database container.

docker compose up -d

## Build and Deployment

### Frontend Production Build
```bash
cd fullstack-frontend
pnpm build
```

### Backend Production Build
```bash
cd fullstack-backend
./mvn clean package
```


