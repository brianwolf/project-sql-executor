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

	private void logQueryBeforeExecute(SQLQuery query) {

		LOG.debug(String.format("SQL QUERY: %s", query));
	}

	private List<String> getColumnsNames(ResultSet resultSet) throws SQLException {

		List<String> columnsNames = new ArrayList<>();
		int columnSize = resultSet.getMetaData().getColumnCount();
		for (int i = 1; i <= columnSize; i++) {
			columnsNames.add(resultSet.getMetaData().getColumnLabel(i));
		}
		return columnsNames;
	}

	private SQLResult getResult(CallableStatement stmt, StoreProcedure sp) throws SQLException {

		SQLResult result = new SQLResult();

		if (stmt.getResultSet() != null) {

			while (stmt.getResultSet().next()) {

				Map<String, Object> row = new LinkedHashMap<>();
				for (String columnName : getColumnsNames(stmt.getResultSet())) {
					row.put(columnName, stmt.getResultSet().getObject(columnName));
				}
				result.getResultset().add(row);
			}
		}

		if (sp.haveOutParameters()) {

			for (String outName : sp.getOutParams()) {
				if (!outName.equals(sp.getCursorParam())) {
					result.getOuts().put(outName, stmt.getObject(outName));
				}
			}
		}

		if (sp.getCursorParam() != null) {

			ResultSet resultsetCursor = (ResultSet) stmt.getObject(sp.getCursorParam());
			while (resultsetCursor.next()) {

				Map<String, Object> row = new LinkedHashMap<>();
				for (String columnName : getColumnsNames(resultsetCursor)) {
					row.put(columnName, resultsetCursor.getObject(columnName));
				}
				result.getResultset().add(row);
			}
		}

		return result;
	}

	private SQLResult getResult(PreparedStatement stmt) throws SQLException {

		SQLResult result = new SQLResult();

		if (stmt.getResultSet() != null) {

			while (stmt.getResultSet().next()) {

				Map<String, Object> row = new LinkedHashMap<>();
				for (String columnName : getColumnsNames(stmt.getResultSet())) {
					row.put(columnName, stmt.getResultSet().getObject(columnName));
				}
				result.getResultset().add(row);
			}
		}

		return result;
	}

	private CallableStatement buildCalleableStatement(Connection con, StoreProcedure sp) throws SQLException {

		LOG.info(sp.getStatementCallString());

		CallableStatement stmt = con.prepareCall(sp.getStatementCallString());

		if (sp.haveInParameters()) {
			for (int i = 0; i < sp.getInParams().size(); i++) {
				stmt.setString(i, TypesConversor.toString(sp.getInParams().get(i)));
			}
		}

		if (sp.haveOutParameters()) {
			for (String outParam : sp.getOutParams()) {

				int type = outParam.equals(sp.getCursorParam()) ? Types.REF_CURSOR : Types.VARCHAR;
				stmt.registerOutParameter(outParam, type);
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

		try {
			con.setAutoCommit(false);

			CallableStatement stmt = buildCalleableStatement(con, sp);
			stmt.execute();

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
