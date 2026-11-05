package com.mju.dearseoul.common.config;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class LoggingProfileTest {
    private ConfigurableApplicationContext start(String profile) {
        // Loads real YAML and Spring Boot logging without starting a server or opening a DB.
        return new SpringApplicationBuilder(LoggingOnlyConfiguration.class)
                .web(WebApplicationType.NONE)
                .profiles(profile)
                .run();
    }

    @Test
    void developmentEnablesExternalCallDiagnosticsWithoutWireOrSqlLogs() {
        try (var context = start("dev")) {
            var logger = LoggerFactory.getLogger("com.mju.dearseoul.external.api");
            assertTrue(logger.isDebugEnabled());
            assertEquals("INFO", context.getEnvironment().getProperty("logging.level.root"));
            assertFalse(LoggerFactory.getLogger("org.springframework.ai").isDebugEnabled());
            assertFalse(LoggerFactory.getLogger("okhttp3").isDebugEnabled());
            assertFalse(LoggerFactory.getLogger("org.hibernate.SQL").isErrorEnabled());
        }
    }

    @Test
    void productionSuppressesRoutineLogsButKeepsErrors() {
        try (var context = start("prod")) {
            var logger = LoggerFactory.getLogger("com.mju.dearseoul.external.api");
            assertFalse(logger.isDebugEnabled());
            assertFalse(logger.isInfoEnabled());
            assertFalse(logger.isWarnEnabled());
            assertTrue(logger.isErrorEnabled());
            assertEquals("ERROR", context.getEnvironment().getProperty("logging.level.root"));
            assertEquals("off", context.getEnvironment().getProperty("spring.main.banner-mode"));
            assertFalse(LoggerFactory.getLogger("org.springframework.ai").isWarnEnabled());
            assertFalse(LoggerFactory.getLogger("com.google").isWarnEnabled());
            assertFalse(LoggerFactory.getLogger("org.hibernate.SQL").isErrorEnabled());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class LoggingOnlyConfiguration {}
}
