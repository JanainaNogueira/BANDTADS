package br.ufpr.bantads.conta_service.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(
            @Qualifier("writeDataSource") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}
