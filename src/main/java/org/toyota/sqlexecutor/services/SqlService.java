package org.toyota.sqlexecutor.services;

import java.sql.SQLException;
import java.util.List;

public interface SqlService {

    /**
     * Recarga los datasource del archivo de properties
     * 
     * @throws SQLException
     */
    List<String> loadDatasources() throws SQLException;
}
