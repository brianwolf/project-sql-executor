package com.toyota.sqlexecutor.configs;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.toyota.sqlexecutor.services.SqlService;

@Configuration
public class SQLExecutorConfig {

    @Autowired
    private SqlService sqlService;

    @Bean
    public List<String> loadDatasources() throws SQLException {
        return sqlService.loadDatasources();
    }
}
