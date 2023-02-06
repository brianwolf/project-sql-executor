package org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class StoreProcedure extends SQLExecutable {

	private static final String SCHEMA_DEFAULT = "dbo";
	private static final String CALL_FORMAT = "CALL %s.%s (%s)";
	private static final String EXEC_FORMAT = "EXEC %s.%s %s";

	/**
	 * Tipo de llamada de ejecucion para el sql, por ejemplo sql server usa EXEC y
	 * DB2 usa CALL
	 */
	public static enum TypeCall {
		CALL, EXEC
	}

	private String schema;
	private String name;
	private Map<String, Object> inParams;
	private List<String> outParams;
	private String cursorParam;
	private TypeCall typeCall;

	public StoreProcedure() {
		super();
		this.schema = SCHEMA_DEFAULT;
		this.typeCall = TypeCall.CALL;
	}

	public StoreProcedure(String schema, String name, Map<String, Object> parametersMap) {
		super(new ArrayList<>());
		this.schema = schema;
		this.name = name;
		this.inParams = parametersMap;
	}

	public StoreProcedure(String schema, String name, Map<String, Object> parametersMap, String typeCall) {
		super(new ArrayList<>());
		this.schema = schema;
		this.name = name;
		this.inParams = parametersMap;
		this.typeCall = TypeCall.valueOf(typeCall);
	}

	@Builder
	public StoreProcedure(List<String> resultColumns, String schema, String name, Map<String, Object> parametersMap,
			String typeCall) {
		super(resultColumns);
		this.schema = schema;
		this.name = name;
		this.inParams = parametersMap;
		this.typeCall = TypeCall.valueOf(typeCall);
	}

	public Boolean haveInParameters() {
		return inParams != null && !inParams.isEmpty();
	}

	public Boolean haveOutParameters() {
		return outParams != null && !outParams.isEmpty();
	}

	@JsonIgnore
	public String getFullName() {
		return getSchema() + "." + getName();
	}

	@JsonIgnore
	public String getStatementCallString() {

		String paramsString = "";

		if (haveInParameters()) {

			for (int i = 0; i < inParams.values().size(); i++) {
				paramsString += "?, ";
			}
			paramsString = paramsString.substring(0, paramsString.length() - 2);
		}

		if (haveOutParameters()) {

			if (haveInParameters()) {
				paramsString += ", ";
			}

			for (int i = 0; i < outParams.size(); i++) {
				paramsString += "?, ";
			}
			paramsString = paramsString.substring(0, paramsString.length() - 2);
		}

		String formatQuery = typeCall == TypeCall.CALL ? CALL_FORMAT : EXEC_FORMAT;
		return String.format(formatQuery, getSchema(), getName(), paramsString);
	}

}
