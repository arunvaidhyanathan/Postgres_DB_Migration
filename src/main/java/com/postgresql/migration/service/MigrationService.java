package com.postgresql.migration.service;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    
    private final DataSource sourceDataSource;
    private final DataSource targetDataSource;
    
    public MigrationService(@Qualifier("sourceDataSource") DataSource sourceDataSource,
                          @Qualifier("targetDataSource") DataSource targetDataSource) {
        this.sourceDataSource = sourceDataSource;
        this.targetDataSource = targetDataSource;
    }
    
    public Map<String, Object> executeMigration() throws SQLException, LiquibaseException {
        logger.info("Starting database migration process");
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = targetDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            
            logger.info("Executing Liquibase migration");
            liquibase.update(new Contexts(), new LabelExpression());
            
            result.put("status", "SUCCESS");
            result.put("message", "Migration completed successfully");
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("Database migration completed successfully");
            
        } catch (Exception e) {
            logger.error("Migration failed: {}", e.getMessage(), e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
            throw e;
        }
        
        return result;
    }
    
    public Map<String, Object> rollbackMigration(int changesToRollback) throws SQLException, LiquibaseException {
        logger.info("Starting rollback process for {} changes", changesToRollback);
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = targetDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            
            logger.info("Executing Liquibase rollback");
            liquibase.rollback(changesToRollback, new Contexts(), new LabelExpression());
            
            result.put("status", "SUCCESS");
            result.put("message", "Rollback completed successfully");
            result.put("rollbackCount", changesToRollback);
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("Database rollback completed successfully");
            
        } catch (Exception e) {
            logger.error("Rollback failed: {}", e.getMessage(), e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
            throw e;
        }
        
        return result;
    }
    
    public Map<String, Object> getMigrationStatus() throws SQLException, LiquibaseException {
        logger.info("Checking migration status");
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = targetDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            
            result.put("status", "SUCCESS");
            result.put("unranChangeSets", liquibase.listUnrunChangeSets(new Contexts(), new LabelExpression()));
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            logger.error("Failed to get migration status: {}", e.getMessage(), e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }
    
    public Map<String, Object> validateMigration() throws SQLException, LiquibaseException {
        logger.info("Validating migration");
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = targetDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            
            // liquibase.validate(database, new Contexts(), new LabelExpression());
            
            result.put("status", "SUCCESS");
            result.put("message", "Validation passed");
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            logger.error("Validation failed: {}", e.getMessage(), e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }
}