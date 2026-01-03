# EducationBase

A Java application for managing educational data including courses and academic programs with MySQL database integration.

## Features

- **Course Management**: Store and manage online courses with platform information
- **Academic Programs**: Track university degrees (Bachelor's, Associate's, Teaching degrees)
- **MySQL Integration**: Persistent data storage with HikariCP connection pooling
- **JSON Data Loading**: Automatic import of courses and programs from JSON files
- **Data Export**: Export database contents back to JSON format

## Tech Stack

- Java 21
- Maven 3.9+
- MySQL 8.0
- HikariCP 5.1.0 (Connection Pool)
- Jackson 2.18.0 (JSON Processing)
- Lombok 1.18.34
- SLF4J + Logback (Logging)

## Project Structure

```
src/main/java/me/pieralini/educationbase/
├── Application.java          # Main entry point
├── config/                   # Configuration management
├── model/                    # Data models (Curso, Faculdade)
├── repository/               # Database operations
├── service/                  # Business logic
└── loader/                   # JSON data loading
```

## Setup

1. **Database**: Create a MySQL database named `educationbase_db`
2. **Configure**: Update `src/main/resources/config.yml` with your database credentials
3. **Build**: `mvn clean compile`
4. **Run**: `mvn exec:java -Dexec.mainClass=me.pieralini.educationbase.Application`

## Data Models

### Curso (Course)
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Auto-increment primary key |
| nome | String | Course name |
| descricao | String | Course description |
| plataforma | String | Platform name |

### Faculdade (Academic Program)
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Auto-increment primary key |
| nomeCurso | String | Program name |
| tipoGraduacao | Enum | BACHARELADO, TECNOLOGO, LICENCIATURA |
| duracaoSemestres | Integer | Duration in semesters |

## License

MIT License