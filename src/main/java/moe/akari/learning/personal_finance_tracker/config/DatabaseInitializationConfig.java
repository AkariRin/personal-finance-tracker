package moe.akari.learning.personal_finance_tracker.config;

import moe.akari.learning.personal_finance_tracker.service.DatabaseInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Database initialization configuration
 * Automatically checks and initializes the database after the application starts.
 */
@Component
public class DatabaseInitializationConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationConfig.class);

    @Autowired
    private DatabaseInitializationService databaseInitializationService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        logger.info("Application is ready. Starting database validation...");
        try {
            databaseInitializationService.checkAndInitializeDatabase();
        } catch (Exception e) {
            logger.error("Failed to initialize database on startup", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}

