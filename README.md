# 📝 Daily Notes Application

A Spring Boot REST API for managing daily notes with MongoDB storage.
The project is fully Dockerized, includes Swagger (OpenAPI) documentation and Spring Boot Actuator for monitoring.

## Table of Contents

---
- [Features](#Features)
- [Tech Stack](#Tech-Stack)
- [Project Structure](#Project-Structure)
- [Quick Start with Docker Compose](#Quick-Start-with-Docker-Compose)
- [Running Without Docker](#Running-Without-Docker)
- [API Documentation (Swagger)](#API-Documentation)
- [API Testing](#API-Testing)
- [REST API Endpoints](#REST-API-Endpoints)
- [Tag Filtering](#Tag-Filtering)
- [MongoDB Verification](#MongoDB-Verification)
- [Stopping the Application](#Stopping-the-Application)
- [Troubleshooting](#Troubleshooting)
- [Testing](#Testing)
- [Support](#Support)
- [Production-Ready Highlights](#Production-Ready-Highlights)


## ✨ Features

- CRUD operations for notes
- Tag-based filtering (BUSINESS, PERSONAL, IMPORTANT)
- Word statistics for each note
- Pagination and sorting (newest first)
- Input validation
- Health checks with Actuator
- Interactive API documentation with Swagger

## 🧱 Tech Stack

- Java 17 (for local development)
- Spring Boot
- Spring Data MongoDB
- Spring Boot Actuator
- Swagger / OpenAPI
- MongoDB
- Maven (for local development only)
- Docker and Docker Compose
- Docker Desktop installed and running

## 📂 Project Structure (Simplified)
```bash
notesapp/
├── src/main/java
│   └── com.example.notes
│       ├── controller
│       ├── service
│       ├── repository
│       └── model
├── src/main/resources
│   └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🚀 Quick Start with Docker Compose

### Prerequisites
- Docker
- Docker Compose
- Docker Desktop Running

### Method 1: Using Docker Compose (Recommended)

1. **Build and run the application:**
   ```bash
   docker-compose up --build

2. **Wait for services to start (takes 1-2 minutes)**
- MongoDB will start on port 27017
- Spring Boot app will start on port 8080
 
3. **Verify services are running:**

   ```bash
   docker ps
   
You should see both notesapp and mongodb containers.

4. **Test the application:**

   ```bash
   curl http://localhost:8080/actuator/health

Expected response:

   {
   "status": "UP"
   }       


### Method 2: Step by Step Docker Commands

1. **Build the application JAR:**

   ```bash
   mvn clean package
   
2. **Build Docker image:**

   ```bash
   docker build -t notes-app .

3. **Start MongoDB:**

   ```bash
   docker run -d --name mongodb -p 27017:27017 -e MONGO_INITDB_DATABASE=notesdb mongo:latest
   
4. **Start the application:**

   ```bash
   docker run -d --name notes-app -p 8080:8080 --link mongodb -e SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/notesdb notes-app
   

## Running Without Docker
### Prerequisites for Local Run:
- Java 17
- Maven
- MongoDB installed locally

### Steps:

1. **Start MongoDB locally:**

   ```bash
   mongod

2. **Run the application:**

   ```bash
   mvn spring-boot:run
   
3. Application will be available at: http://localhost:8080

## 📘 API Documentation (Swagger)

Swagger UI is available at:

👉 http://localhost:8080/swagger-ui/index.html

Use Swagger to:

   - Explore all endpoints
   - Test requests
   - Validate request/response models

## API Testing
Once running, test the API endpoints:
1. **Create a Note**

   ```bash
   curl -X POST http://localhost:8080/api/notes \
   -H "Content-Type: application/json" \
   -d '{
   "title": "Meeting Notes",
   "text": "Discussed project requirements and timelines",
   "tags": ["BUSINESS", "IMPORTANT"]
   }'

2. **Get All Notes (Paginated)**

   ```bash
   curl "http://localhost:8080/api/notes?page=0&size=10"

3. **Get Notes Filtered by Tags**

   ```bash
   curl "http://localhost:8080/api/notes?tags=BUSINESS,PERSONAL&page=0&size=10"

4. **Get Specific Note**

   ```bash
   curl http://localhost:8080/api/notes/{note-id}

5. **Get Word Statistics**

   ```bash
   curl http://localhost:8080/api/notes/{note-id}/statistics

6. Update Note

   ```bash
   curl -X PUT http://localhost:8080/api/notes/{note-id} \
   -H "Content-Type: application/json" \
   -d '{
   "title": "Updated Title",
   "text": "Updated content",
   "tags": ["PERSONAL"]
   }'

7. **Delete Note**

   ```bash
   curl -X DELETE http://localhost:8080/api/notes/{note-id}
   
## 🔗 REST API Endpoints

|Method	      |Endpoint	                         |Description                            |
|:------------|:---------------------------------|:-----------------------------------------|
|POST	      |/api/notes	                     |Create a new note                          |
|GET 	      |/api/notes	                     |Get all notes (paginated, filtered by tags)|
|GET	      |/api/notes/{id}	                 |Get specific note details                  |
|GET	      |/api/notes/{id}/statistics	     |Get word statistics for a note             |
|PUT	      |/api/notes/{id}	                 |Update a note                              |
|DELETE	  |/api/notes/{id}	                 |Delete a note                              |
|GET        |/actuator/health	                 |Application health check                   |

## Tag Filtering
Available tags: BUSINESS, PERSONAL, IMPORTANT

Examples:
- ?tags=BUSINESS - Notes with BUSINESS tag
- ?tags=BUSINESS,PERSONAL - Notes with BUSINESS OR PERSONAL tags
- No tags parameter - All notes

## 🛠 MongoDB Verification

Check stored data inside MongoDB container:

```bash
docker exec -it mongodb mongosh notesdb --eval "db.notes.find().pretty()"
```

## 🛑 Stopping the Application

Stop containers:
```bash
docker-compose down
```

Stop containers and remove volumes:
```bash
docker-compose down -v
```

## ⚠️ Troubleshooting
### Application not starting:
1. Check if Docker Desktop is running
2. Check if ports 8080 and 27017 are available
3. View logs: docker-compose logs notes-app

### MongoDB connection issues:
1. Check if MongoDB container is running: docker ps
2. Test MongoDB: docker exec -it mongodb mongosh --eval "db.adminCommand('ping')"
3. View MongoDB logs: docker-compose logs mongodb

### Build issues:
1. Clean build: mvn clean package
2. Rebuild containers: docker-compose down && docker-compose up --build
3. Stopping the Application


## 🧪 Testing

Run the test suite:

   ```bash
      mvn test 
   ```

Run specific test:

   ```bash
      mvn test -Dtest=NoteControllerTest 
   ```

## 🎤 Support

If you encounter issues:
1. Check the logs: docker-compose logs
2. Verify Docker is running: docker version
3. Ensure ports are not in use
4. Check application health: curl http://localhost:8080/actuator/health

   ```text
   Key Files That Fulfill Requirements
   
   1. **`docker-compose.yml`** - Docker Compose setup
   2. **`Dockerfile`** - Application containerization  
   3. **`RUNNING_INSTRUCTIONS.md`** - Complete running steps
   4. **`NoteController.java`** - RESTful API endpoints
   5. **`Note.java`** - MongoDB document model
   6. **`NoteRepository.java`** - Spring Data MongoDB repository
   7. **`application.properties`** - Spring Boot configuration
   ```
### Verification Commands

After running `docker-compose up --build`, verify everything works:

   ```bash
   # Check containers
   docker ps
   ```
### Test health
curl http://localhost:8080/actuator/health

### Test API
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title": "Test", "text": "Hello world", "tags": ["PERSONAL"]}'

### Verify data in MongoDB
   ```bash
   docker exec -it mongodb mongosh notesdb --eval "db.notes.find()"
   ```

## ✅ Production-Ready Highlights

- ✔ Docker Compose orchestration
- ✔ Health checks via Actuator
- ✔ Swagger API documentation
- ✔ MongoDB persistence
- ✔ Clean REST architecture
- ✔ Ready for frontend integration