package org.toyota.sqlexecutor.helpers.sqlexecutor.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.toyota.sqlexecutor.helpers.sqlexecutor.SQLExecutorService;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables.SQLQuery;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.executables.StoreProcedure;
import org.toyota.sqlexecutor.helpers.sqlexecutor.model.results.SQLResult;
import org.toyota.sqlexecutor.helpers.sqlexecutor.utils.DSManager;
import org.toyota.sqlexecutor.helpers.sqlexecutor.utils.ResultBuilder;
import org.toyota.sqlexecutor.helpers.sqlexecutor.utils.TypesConversor;

@Service
public class SQLExecutorServiceImpl implements SQLExecutorService {

	private static final Logger LOG = LoggerFactory.getLogger(SQLExecutorServiceImpl.class);

	@Autowired
	private DSManager dsManager;

	/**
	 * Loguea el SP antes de ejecutarlo
	 *
	 * @param sp
	 */
	private void logSPBeforeExecute(StoreProcedure sp) {

		String parameters = sp.haveInParameters() ? "Parameters: " + sp.getInParams().toString()
				: "without Parameters";

		LOG.debug(String.format("SP: %s %s", sp.getFullName(), parameters));
	}

	/**
	 * Loguea la QUERY antes de ejecutarlo
	 *
	 * @param query
	 */
	private void logQueryBeforeExecute(SQLQuery query) {

		LOG.debug(String.format("SQL QUERY: %s", query));
	}

	/**
	 *
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private List<String> getColumnsNames(ResultSet resultSet) throws SQLException {

		List<String> columnsNames = new ArrayList<>();
		int columnSize = resultSet.getMetaData().getColumnCount();
		for (int i = 1; i <= columnSize; i++) {
			columnsNames.add(resultSet.getMetaData().getColumnLabel(i));
		}
		return columnsNames;
	}

	/**
	 *
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private SQLResult getResult(CallableStatement stmt, StoreProcedure sp) throws SQLException {

		SQLResult result = new SQLResult();

		if (stmt.getResultSet() != null) {

			while (stmt.getResultSet().next()) {

				Map<String, Object> row = new LinkedHashMap<>();
				for (String columnName : getColumnsNames(stmt.getResultSet())) {
					row.put(columnName, stmt.getResultSet().getObject(columnName));
				}
				result.getTable().add(row);
			}
		}

		if (sp.haveOutParameters()) {

			for (String outName : sp.getOutParams()) {
				result.getOuts().put(outName, stmt.getString(outName));
			}
		}

		return result;
	}

	/**
	 *
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private SQLResult getResult(PreparedStatement stmt) throws SQLException {

		SQLResult result = new SQLResult();

		if (stmt.getResultSet() != null) {

			while (stmt.getResultSet().next()) {

				Map<String, Object> row = new LinkedHashMap<>();
				for (String columnName : getColumnsNames(stmt.getResultSet())) {
					row.put(columnName, stmt.getResultSet().getObject(columnName));
				}
				result.getTable().add(row);
			}
		}

		return result;
	}

	/**
	 *
	 * @param con
	 * @param sp
	 * @return
	 * @throws SQLException
	 */
	private CallableStatement buildCalleableStatement(Connection con, StoreProcedure sp) throws SQLException {

		LOG.info(sp.getStatementCallString());

		CallableStatement stmt = con.prepareCall(sp.getStatementCallString());

		if (sp.haveInParameters()) {
			for (String key : sp.getInParams().keySet()) {
				stmt.setString(key, TypesConversor.toString(sp.getInParams().get(key)));
			}
		}

		if (sp.haveOutParameters()) {
			for (String key : sp.getOutParams()) {
				stmt.registerOutParameter(key, Types.VARCHAR);
			}
		}

		return stmt;
	}

	@Override
	public SQLResult execute(StoreProcedure sp, String dataSourceName) throws SQLException {

		Connection con = dsManager.get(dataSourceName).getConnection();
		SQLResult result = execute(sp, con);

		commit(con);
		close(con);

		return result;
	}

	@Override
	public SQLResult execute(SQLQuery query, String dataSourceName) throws SQLException {

		Connection con = dsManager.get(dataSourceName).getConnection();
		SQLResult result = execute(query, con);

		commit(con);
		close(con);

		return result;
	}

	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		return dsManager.get(dataSourceName).getConnection();
	}

	@Override
	public SQLResult execute(StoreProcedure sp, Connection con) throws SQLException {

		logSPBeforeExecute(sp);
		try {
			con.setAutoCommit(false);

			CallableStatement stmt = buildCalleableStatement(con, sp);
			stmt.execute();

			LOG.info(stmt.getString("salida"));

			SQLResult result = getResult(stmt, sp);
			return ResultBuilder.getResultChangedColumns(result, sp.getResultColumns());

		} catch (Exception e) {

			LOG.error(e.getLocalizedMessage(), e);
			if (con != null) {
				close(con);
			}
			throw e;
		}
	}

	@Override
	public SQLResult execute(SQLQuery query, Connection con) throws SQLException {

		logQueryBeforeExecute(query);
		try {
			con.setAutoCommit(false);

			PreparedStatement stmt = con.prepareStatement(query.getQuery());
			stmt.execute();

			SQLResult result = getResult(stmt);
			return ResultBuilder.getResultChangedColumns(result, query.getResultColumns());

		} catch (Exception e) {

			LOG.error(e.getLocalizedMessage(), e);
			if (con != null) {
				close(con);
			}
			throw e;
		}
	}

	@Override
	public void close(Connection con) throws SQLException {
		if (con != null) {
			con.setAutoCommit(true);
			con.close();
		}
	}

	@Override
	public void commit(Connection con) throws SQLException {
		if (con != null) {
			con.commit();
		}
	}

	@Override
	public void rollback(Connection con) throws SQLException {
		if (con != null) {
			con.rollback();
		}
	}

}
