package org.toyota.sqlexecutor.helpers.sqlexecutor.model.connections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SQLJdbcConnection {

	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private Integer initialSize;
	private Integer maxTotal;
	private Integer maxIdle;

	public SQLJdbcConnection() {
		this.initialSize = 1;
		this.maxTotal = 1;
		this.maxIdle = 1;
	}
}
