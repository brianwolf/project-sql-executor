package com.toyota.sqlexecutor.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import com.toyota.sqlexecutor.models.connections.SQLJdbcConnection;
import com.toyota.sqlexecutor.models.executables.SQLQuery;
import com.toyota.sqlexecutor.services.SQLExecutorService;
import com.toyota.sqlexecutor.services.SqlService;
import com.toyota.sqlexecutor.utils.DSManager;

@Service
public class SqlServiceImpl implements SqlService {

    private static Logger logger = LoggerFactory.getLogger(SqlServiceImpl.class);

    @Autowired
    private DSManager dsManager;

    @Autowired
    private SQLExecutorService sqlExecutorService;

    @Override
    public List<String> loadDatasources() throws SQLException {

        FileSystemResource yamlFile = new FileSystemResource("application.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(yamlFile.getFile());

        List<String> dsListOnline = new ArrayList<>();

        for (Object ds : config.getList("datasources")) {

            @SuppressWarnings("unchecked")
            HashMap<String, Object> mapds = (HashMap<String, Object>) ds;

            SQLJdbcConnection sqlJdbcConnection = new SQLJdbcConnection();
            sqlJdbcConnection.setUrl(mapds.get("url").toString());
            sqlJdbcConnection.setUsername(mapds.get("username").toString());
            sqlJdbcConnection.setPassword(mapds.get("password").toString());

            if (mapds.containsKey("driverClassName")) {
                String driverClassName = mapds.get("driverClassName").toString();
                sqlJdbcConnection.setDriverClassName(driverClassName);
            }

            if (mapds.containsKey("initialSize")) {
                Integer initialSize = Integer.valueOf(mapds.get("initialSize").toString());
                sqlJdbcConnection.setInitialSize(initialSize);
            }

            if (mapds.containsKey("maxTotal")) {
                Integer maxTotal = Integer.valueOf(mapds.get("maxTotal").toString());
                sqlJdbcConnection.setMaxIdle(maxTotal);
            }

            if (mapds.containsKey("maxIdle")) {
                Integer maxIdle = Integer.valueOf(mapds.get("maxIdle").toString());
                sqlJdbcConnection.setMaxIdle(maxIdle);
            }

            String dsName = mapds.get("name").toString();
            dsManager.add(dsName, sqlJdbcConnection);

            try {

                String stringQuery = sqlJdbcConnection.getUrl().contains("oracle")
                        ? "select user from dual"
                        : "SELECT 1";

                SQLQuery query = new SQLQuery(stringQuery);
                sqlExecutorService.execute(query, dsName);

                logger.info(String.format("Connection successful datasource -> %s", dsName));
                dsListOnline.add(dsName);

            } catch (SQLException e) {
                logger.warn(String.format("Connection error datasource -> %s", dsName));
                logger.warn(e.getCause().toString());
            }
        }

        return dsListOnline;
    }

}
