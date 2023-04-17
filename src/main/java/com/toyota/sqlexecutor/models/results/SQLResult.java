package com.toyota.sqlexecutor.models.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SQLResult {

    private List<Map<String, Object>> resultset;
    private Map<String, Object> outs;

    public SQLResult() {
        this.resultset = new ArrayList<>();
        this.outs = new HashMap<>();
    }
}
