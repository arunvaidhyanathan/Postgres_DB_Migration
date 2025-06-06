package com.postgresql.migration.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(@Qualifier("targetDataSource") DataSource targetDataSource,
                                   ResourceLoader resourceLoader) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(targetDataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setContexts("production");
        liquibase.setShouldRun(false); // We'll control this manually via API
        liquibase.setDropFirst(false);
        liquibase.setClearCheckSums(false);
        return liquibase;
    }
}