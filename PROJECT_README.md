# PostgreSQL Migration with Liquibase + Spring Boot

This project provides a comprehensive PostgreSQL database migration solution from on-premises to AWS using Liquibase with Spring Boot, featuring API control, rollback capabilities, and detailed logging.

## Features

- ✅ Java 17 + Spring Boot 3.2.0
- ✅ Liquibase 4.24.0 for database migrations
- ✅ Dual PostgreSQL datasource configuration (source + target)
- ✅ REST API for migration control
- ✅ Rollback capabilities with API control
- ✅ Comprehensive logging with separate log files
- ✅ XML-based changelog for maintainability
- ✅ Production-ready configuration

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/company/migration/
│   │       ├── MigrationApplication.java
│   │       ├── config/
│   │       │   ├── LiquibaseConfig.java
│   │       │   └── DataSourceConfig.java
│   │       ├── controller/
│   │       │   └── MigrationController.java
│   │       └── service/
│   │           └── MigrationService.java
│   └── resources/
│       ├── application.yml
│       ├── application-prod.yml
│       ├── liquibase.properties
│       ├── logback-spring.xml
│       └── db/
│           └── changelog/
│               ├── db.changelog-master.xml
│               ├── baseline/
│               │   └── 001-baseline-schema.xml
│               └── migrations/
└── pom.xml
```

## Configuration

### Database Configuration
Update `application.yml` and `application-prod.yml` with your database connection details:

```yaml
spring:
  datasource:
    source:  # On-premises PostgreSQL
      url: jdbc:postgresql://localhost:5432/dev_database
      username: dev_user
      password: dev_password
    target:  # AWS RDS PostgreSQL
      url: jdbc:postgresql://aws-prod-instance.region.rds.amazonaws.com:5432/prod_database
      username: prod_user
      password: prod_password
```

### Environment Variables (Production)
```bash
export SOURCE_DB_URL="jdbc:postgresql://source-host:5432/source_db"
export SOURCE_DB_USERNAME="source_user"
export SOURCE_DB_PASSWORD="source_password"
export TARGET_DB_URL="jdbc:postgresql://target-host:5432/target_db"
export TARGET_DB_USERNAME="target_user"
export TARGET_DB_PASSWORD="target_password"
```

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (source and target databases)

### Build
```bash
mvn clean compile
```

### Run
```bash
# Development
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Package
```bash
mvn clean package
java -jar target/postgresql-migration-1.0.0.jar --spring.profiles.active=prod
```

## API Endpoints

### Migration Control
- `POST /api/migration/execute` - Execute migration
- `POST /api/migration/rollback?changes=1` - Rollback migrations
- `GET /api/migration/status` - Get migration status
- `POST /api/migration/validate` - Validate migration
- `GET /api/migration/health` - Health check

### Example Usage
```bash
# Execute migration
curl -X POST http://localhost:8080/api/migration/execute

# Check status
curl http://localhost:8080/api/migration/status

# Rollback last change
curl -X POST http://localhost:8080/api/migration/rollback?changes=1

# Validate
curl -X POST http://localhost:8080/api/migration/validate
```

## Customizing for Your Database

### 1. Generate Baseline Schema
Replace the example content in `001-baseline-schema.xml` with your actual database structure:

```bash
# Use Liquibase to generate from existing database
mvn liquibase:generateChangeLog -Dliquibase.outputFile=src/main/resources/db/changelog/baseline/001-baseline-schema.xml
```

### 2. Add Your Tables, Views, Functions
Modify `001-baseline-schema.xml` to include:
- All schemas
- All tables with constraints
- All indexes
- All sequences
- All views
- All stored procedures/functions
- All roles and grants

### 3. Test Migration
1. Set up your source and target databases
2. Update connection configurations
3. Run validation: `curl -X POST http://localhost:8080/api/migration/validate`
4. Execute migration: `curl -X POST http://localhost:8080/api/migration/execute`

## Logging

Logs are written to multiple files:
- `logs/migration.log` - General application logs
- `logs/migration-execution.log` - Migration-specific logs
- `logs/liquibase.log` - Liquibase operation logs

## Production Deployment

1. Set environment variables for database connections
2. Use production profile: `--spring.profiles.active=prod`
3. Monitor logs in `/var/log/migration/`
4. Ensure backup strategy is in place
5. Test rollback procedures

## Security Considerations

- Enable API security in production (`migration.api.security.enabled=true`)
- Use secure database connections (SSL)
- Store passwords in environment variables or secret management
- Restrict API access to authorized users only
- Monitor migration logs for security events

## Troubleshooting

### Common Issues
1. **Connection refused**: Check database connectivity and credentials
2. **Changelog not found**: Verify file paths in configuration
3. **Migration fails**: Check logs in `logs/migration-execution.log`
4. **Rollback fails**: Ensure rollback SQL is properly defined in changesets

### Debugging
Enable debug logging by setting:
```yaml
logging:
  level:
    com.company.migration: DEBUG
    liquibase: DEBUG
```

## Next Steps

1. Customize the baseline schema XML with your actual database structure
2. Add proper error handling and monitoring
3. Implement backup procedures before migration
4. Set up CI/CD pipeline for automated testing
5. Add integration tests for migration scenarios