package moe.akari.learning.personal_finance_tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Verify that the database table structure meets expectations
     */
    public boolean validateDatabaseSchema() {
        try {
            // Check if required tables exist
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'",
                String.class
            );

            // Check required tables
            if (!tables.contains("user") || !tables.contains("bill")) {
                logger.warn("Required tables are missing. Found tables: {}", tables);
                return false;
            }

            // Verify the user table structure
            if (!validateUserTable()) {
                logger.warn("User table structure is invalid");
                return false;
            }

            // Verify the bill table structure
            if (!validateBillTable()) {
                logger.warn("Bill table structure is invalid");
                return false;
            }

            logger.info("Database schema validation passed");
            return true;
        } catch (Exception e) {
            logger.error("Error validating database schema", e);
            return false;
        }
    }

    /**
     * Verify the user table structure
     */
    private boolean validateUserTable() {
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user'"
            );

            if (columns.size() < 4) {
                return false;
            }

            // Checking for required columns
            boolean hasUsername = false;
            boolean hasName = false;
            boolean hasPassword = false;
            boolean hasBalance = false;

            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                switch (columnName) {
                    case "username":
                        hasUsername = true;
                        break;
                    case "name":
                        hasName = true;
                        break;
                    case "password":
                        hasPassword = true;
                        break;
                    case "balance":
                        hasBalance = true;
                        break;
                }
            }

            return hasUsername && hasName && hasPassword && hasBalance;
        } catch (Exception e) {
            logger.error("Error validating user table", e);
            return false;
        }
    }

    /**
     * Verify the bill table structure
     */
    private boolean validateBillTable() {
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bill'"
            );

            if (columns.size() < 7) {
                return false;
            }

            // Checking for required columns
            boolean hasUuid = false;
            boolean hasAmount = false;
            boolean hasType = false;
            boolean hasMethod = false;
            boolean hasCounterparty = false;
            boolean hasTime = false;
            boolean hasUserUsername = false;

            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                switch (columnName) {
                    case "uuid":
                        hasUuid = true;
                        break;
                    case "amount":
                        hasAmount = true;
                        break;
                    case "type":
                        hasType = true;
                        break;
                    case "method":
                        hasMethod = true;
                        break;
                    case "counterparty":
                        hasCounterparty = true;
                        break;
                    case "time":
                        hasTime = true;
                        break;
                    case "user_username":
                        hasUserUsername = true;
                        break;
                }
            }

            return hasUuid && hasAmount && hasType && hasMethod &&
                   hasCounterparty && hasTime && hasUserUsername;
        } catch (Exception e) {
            logger.error("Error validating bill table", e);
            return false;
        }
    }

    /**
     * Clear and reinitialize the database
     */
    public void reinitializeDatabase() {
        try {
            logger.info("Starting database reinitialization...");

            // Disable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Get all tables
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'",
                String.class
            );

            // Delete all tables
            for (String table : tables) {
                logger.info("Dropping table: {}", table);
                jdbcTemplate.execute("DROP TABLE IF EXISTS " + table);
            }

            // Enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            // Execute the database.sql script
            executeSqlScript();

            logger.info("Database reinitialization completed successfully");
        } catch (Exception e) {
            logger.error("Error reinitializing database", e);
            throw new RuntimeException("Failed to reinitialize database", e);
        }
    }

    /**
     * Execute the database.sql script
     */
    private void executeSqlScript() {
        try {
            ClassPathResource resource = new ClassPathResource("database.sql");
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            // Split SQL statements
            String[] statements = sql.split(";");

            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    logger.debug("Executing SQL: {}", trimmedStatement);
                    jdbcTemplate.execute(trimmedStatement);
                }
            }

            logger.info("SQL script executed successfully");
        } catch (Exception e) {
            logger.error("Error executing SQL script", e);
            throw new RuntimeException("Failed to execute SQL script", e);
        }
    }

    /**
     * Check and initialize the database at application startup
     */
    public void checkAndInitializeDatabase() {
        logger.info("Checking database schema...");

        if (!validateDatabaseSchema()) {
            logger.warn("Database schema validation failed. Reinitializing database...");
            reinitializeDatabase();

            // Verify again
            if (validateDatabaseSchema()) {
                logger.info("Database reinitialization successful and validated");
            } else {
                logger.error("Database validation failed after reinitialization");
                throw new RuntimeException("Failed to initialize database correctly");
            }
        } else {
            logger.info("Database schema is valid. No reinitialization needed.");
        }
    }
}

