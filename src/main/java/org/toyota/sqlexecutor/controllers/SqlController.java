package org.toyota.sqlexecutor.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.toyota.sqlexecutor.services.SqlService;

@RestController
@RequestMapping("/sql")
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @GetMapping("/ds")
    public ResponseEntity<?> getDatasources() throws Exception {

        List<String> result = sqlService.loadDatasources();

        return ResponseEntity.status(200).body(result);
    }

}
