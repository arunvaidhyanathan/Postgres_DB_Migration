package com.postgresql.migration.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.postgresql.migration.service.MigrationService;

import java.util.Map;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private static final Logger logger = LoggerFactory.getLogger(MigrationController.class);
    
    private final MigrationService migrationService;
    
    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }
    
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeMigration() {
        try {
            logger.info("Received request to execute migration");
            Map<String, Object> result = migrationService.executeMigration();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Migration execution failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "FAILED",
                            "message", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    @PostMapping("/rollback")
    public ResponseEntity<Map<String, Object>> rollbackMigration(
            @RequestParam(defaultValue = "1") int changes) {
        try {
            logger.info("Received request to rollback {} changes", changes);
            Map<String, Object> result = migrationService.rollbackMigration(changes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Migration rollback failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "FAILED",
                            "message", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMigrationStatus() {
        try {
            logger.info("Received request for migration status");
            Map<String, Object> result = migrationService.getMigrationStatus();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to get migration status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "FAILED",
                            "message", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateMigration() {
        try {
            logger.info("Received request to validate migration");
            Map<String, Object> result = migrationService.validateMigration();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Migration validation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "FAILED",
                            "message", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Migration API",
                "timestamp", System.currentTimeMillis()
        ));
    }
}