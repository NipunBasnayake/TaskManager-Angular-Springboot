# Task Manager Application

A full-stack Task Manager application built with Angular and Spring Boot. This application allows users to create, view, edit and delete tasks, with optional JWT authentication.

## Repository

[GitHub Repository](https://github.com/NipunBasnayake/TaskManager-Angular-Springboot)

## How to Run the Application

### Prerequisites
- Docker and Docker Compose
- Java 17 (for running backend separately)
- Node.js and npm (for running frontend separately)
- MySQL (for running database separately)

### Option 1: Running with Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/NipunBasnayake/TaskManager-Angular-Springboot.git
cd TaskManager-Angular-Springboot
```

2. Run the application using Docker Compose:
```bash
docker-compose up
```

3. Access the application:
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html
   
4. Run the Angular frontend separately (since it's not included in the Docker Compose):
```bash
cd frontend
npm install
ng serve
```

5. Access the frontend at http://localhost:4200

### Option 2: Running Frontend and Backend Separately

#### Backend Setup:

1. Make sure MySQL is running with:
   - Database: task_manager_db
   - Username: root
   - Password: 1234

2. Navigate to the backend directory:
```bash
cd backend
```

3. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

4. The backend will be available at http://localhost:8080

#### Frontend Setup:

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the Angular application:
```bash
ng serve
```

4. Access the frontend at http://localhost:4200

## Database Setup

The application uses MySQL for data storage:

- Database Name: task_manager_db
- Username: root
- Password: 1234

When using Docker Compose, MySQL is automatically configured. If running locally:

1. Create the database:
```sql
CREATE DATABASE task_manager_db;
```

2. The tables will be automatically created by Hibernate based on the entity models.

The Task entity structure:
```java
public class Task {
    private Long id;
    private String title;
    private String description;
    private String status; // TO_DO, IN_PROGRESS, DONE
    private LocalDateTime createdAt;
}
```

## Authentication Credentials

JWT authentication has been implemented. Default user credentials:

- Username: admin
- Password: securePassword123

To authenticate:
1. Login at: http://localhost:4200/login or via POST `/api/auth/login`
2. Use the JWT token in subsequent requests with header: `Authorization: Bearer <token>`

## API Endpoints

### Task Endpoints
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Authentication Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

## Screenshots

### Task List Page
![Task List](screenshots/task-list.png)

### Task Form
![Task Form](screenshots/task-form.png)

### Task Details
![Task Details](screenshots/task-details.png)

### Login Page
![Login Page](screenshots/login.png)

## Troubleshooting

- **Database Connection Issues**: Verify database credentials (root/1234) and that MySQL is running on port 3306
- **Frontend API Calls Failing**: Check that CORS is properly configured on the backend
- **Docker Issues**: Use `docker-compose logs` to view detailed error messages