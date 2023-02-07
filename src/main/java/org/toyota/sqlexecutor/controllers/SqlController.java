package org.toyota.sqlexecutor.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.toyota.sqlexecutor.helpers.sqlexecutor.SQLExecutorService;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables.SQLQuery;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables.StoreProcedure;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.results.SQLResult;
import org.toyota.sqlexecutor.services.SqlService;

@RestController
@RequestMapping("/api/v1/sql")
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private SQLExecutorService sqlExecutorService;

    @GetMapping("/datasources")
    public ResponseEntity<?> getDatasources() throws Exception {

        List<String> result = sqlService.loadDatasources();

        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/datasources/{datasource}/query")
    public ResponseEntity<?> executeQuery(@PathVariable String datasource, @RequestBody SQLQuery sqlQuery)
            throws SQLException {

        SQLResult result = sqlExecutorService.execute(sqlQuery, datasource);

        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/datasources/{datasource}/procedure")
    public ResponseEntity<?> executeProcedure(@PathVariable String datasource, @RequestBody StoreProcedure procedure)
            throws SQLException {

        SQLResult result = sqlExecutorService.execute(procedure, datasource);

        return ResponseEntity.status(200).body(result);
    }

}
