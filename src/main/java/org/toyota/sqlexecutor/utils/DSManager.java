package org.toyota.sqlexecutor.utils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.toyota.sqlexecutor.models.connections.SQLJdbcConnection;
import org.toyota.sqlexecutor.models.connections.SQLJndiConnection;

@Service
public class DSManager {

	private static final Logger LOG = LoggerFactory.getLogger(DSManager.class);

	private Map<String, DataSource> dsMap;

	private DSManager() {
		this.dsMap = new HashMap<String, DataSource>();
	}

	/**
	 * @param name
	 * @param jdbc
	 */
	public void add(String name, SQLJdbcConnection jdbc) {
		dsMap.put(name, makeJDBC(jdbc));
	}

	/**
	 * @param name
	 * @param jndi
	 */
	public void add(String name, SQLJndiConnection jndi) {
		dsMap.put(name, makeJNDI(jndi));
	}

	/**
	 * Agrega el JDNI si existe, sino agrega el JDBC.
	 * 
	 * @param name
	 * @param jndi
	 * @param jdbc
	 */
	public void addJNDIorJDBC(String name, SQLJndiConnection jndi, SQLJdbcConnection jdbc) {

		if (existJNDI(jndi.getUrlConnection())) {
			LOG.info(String.format("Conexion JNDI encontrada en la ruta: %s", jndi.getUrlConnection()));
			add(name, jndi);
			return;
		}

		LOG.warn(String.format("Conexion JNDI NO encontrada en la ruta %s, usando JDBC", jndi.getUrlConnection()));
		LOG.info(String.format("Conexion JDBC en uso: %s", jdbc));
		add(name, jdbc);
	}

	/**
	 * @param name
	 */
	public void delete(String name) {
		dsMap.remove(name);
	}

	/**
	 * 
	 * @param con
	 * @return
	 */
	private DataSource makeJNDI(SQLJndiConnection con) {

		try {
			return InitialContext.doLookup(con.getUrlConnection());
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * @param con
	 * @return
	 */
	private DataSource makeJDBC(SQLJdbcConnection con) {

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(con.getDriverClassName());
		ds.setUrl(con.getUrl());
		ds.setUsername(con.getUsername());
		ds.setPassword(con.getPassword());
		ds.setInitialSize(con.getInitialSize());
		ds.setMaxTotal(con.getMaxTotal());
		ds.setMaxIdle(con.getMaxIdle());

		ds.setDefaultAutoCommit(false);

		return ds;
	}

	/**
	 * @return
	 */
	public Map<String, DataSource> getAll() {
		return dsMap;
	}

	/**
	 * @param dataSourceName
	 * @return
	 */
	public DataSource get(String dataSourceName) {
		return dsMap.get(dataSourceName);
	}

	/**
	 * 
	 * @param urlConnection
	 * @return
	 */
	public Boolean existJNDI(String urlConnection) {
		try {
			return InitialContext.doLookup(urlConnection) != null;
		} catch (Exception e) {
			return false;
		}
	}

}
