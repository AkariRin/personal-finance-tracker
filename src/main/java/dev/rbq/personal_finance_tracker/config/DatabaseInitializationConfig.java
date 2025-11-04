package dev.rbq.personal_finance_tracker.config;

import dev.rbq.personal_finance_tracker.service.DatabaseInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化配置
 * 应用程序启动后自动检查并初始化数据库
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

