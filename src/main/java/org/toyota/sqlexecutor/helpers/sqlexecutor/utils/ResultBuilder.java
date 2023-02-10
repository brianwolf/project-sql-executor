package org.toyota.sqlexecutor.helpers.sqlexecutor.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.toyota.sqlexecutor.helpers.sqlexecutor.model.results.SQLResult;

public class ResultBuilder {

    /**
     * @param resultMap
     * @param resultColumns
     * @return
     */
    private static Boolean canRenameColumns(SQLResult result, List<String> resultColumns) {
        return resultColumns != null && result.getResultset() != null && !resultColumns.isEmpty()
                && !result.getResultset().isEmpty();
    }

    /**
     * @param resultMap
     * @param resultColumns
     * @return
     */
    public static SQLResult getResultChangedColumns(SQLResult result, List<String> resultColumns) {

        if (!canRenameColumns(result, resultColumns)) {
            return result;
        }

        SQLResult resultChangedNames = new SQLResult();
        resultChangedNames.setResultset(new ArrayList<>(result.getResultset()));

        List<String> listKeys = new ArrayList<>(result.getResultset().get(0).keySet());

        Integer iterations = resultColumns.size() > result.getResultset().size()
                ? resultColumns.size() - result.getResultset().size()
                : resultColumns.size();

        for (Map<String, Object> mapChangedNames : resultChangedNames.getResultset()) {

            for (int i = 0; i < iterations; i++) {

                String key = listKeys.get(i);
                String newKey = resultColumns.get(i);
                changeKeyFromMap(key, newKey, mapChangedNames);
            }
        }

        return resultChangedNames;
    }

    /**
     * @param key
     * @param newKey
     * @param map
     */
    private static void changeKeyFromMap(String key, String newKey, Map<String, Object> map) {
        Object value = map.remove(key);
        map.put(newKey, value);
    }
}
