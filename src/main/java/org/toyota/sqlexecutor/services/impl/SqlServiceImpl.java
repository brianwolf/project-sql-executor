package org.toyota.sqlexecutor.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.toyota.sqlexecutor.helpers.sqlexecutor.SQLExecutorService;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.connections.SQLJdbcConnection;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables.SQLQuery;
import org.toyota.sqlexecutor.helpers.sqlexecutor.utils.DSManager;
import org.toyota.sqlexecutor.services.SqlService;

@Service
public class SqlServiceImpl implements SqlService {

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

            String url = mapds.get("url").toString();
            String driverClassName = mapds.get("driverClassName").toString();
            String username = mapds.get("username").toString();
            String password = mapds.get("password").toString();

            SQLJdbcConnection sqlJdbcConnection = new SQLJdbcConnection();
            sqlJdbcConnection.setUrl(url);
            sqlJdbcConnection.setDriverClassName(driverClassName);
            sqlJdbcConnection.setUsername(username);
            sqlJdbcConnection.setPassword(password);

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

            SQLQuery query = new SQLQuery("SELECT 1");
            sqlExecutorService.execute(query, dsName);

            dsListOnline.add(dsName);
        }

        return dsListOnline;
    }

}
