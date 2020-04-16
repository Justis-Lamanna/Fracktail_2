package com.github.lucbui.bot.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Value("${database.driverClassName}")
    private String driverClassName;

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.url:}")
    private String username;

    @Value("${database.url:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);

        if(StringUtils.isNotBlank(username)){
            dataSource.setUsername(username);
        }

        if(StringUtils.isNotBlank(password)){
            dataSource.setUsername(password);
        }

        return dataSource;
    }
}
