package com.variant.extapi.standard.flush.jdbc;


import static com.variant.core.impl.CommonError.CONFIG_PROPERTY_NOT_SET;
import static com.variant.server.api.ConfigKeys.EVENT_FLUSHER_CLASS_INIT;

import java.sql.Connection;
import java.sql.DriverManager;

import com.typesafe.config.Config;
import com.variant.server.api.ServerException;
import com.variant.server.jdbc.TraceEventFlusherJdbc;
import com.variant.server.jdbc.JdbcService.Vendor;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to an 
 * instance of H2 database. The required database schema can be created by the
 * {@code create-schema.sql} SQL script, included with Variant server. 
 * <p>
 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
 * 
 * <ul>
 *  <li><code>url</code> - specifies the URL to the H2 database.
 *  <li><code>user</code> - the H2 database user.
 *  <li><code>password</code> - the H2 database user's password.
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {"url":"jdbc:h2:mem:variant;MVCC=true;DB_CLOSE_DELAY=-1;","user":"variant","password":"variant"}</code>
 * 
 * @since 0.5
 */
public class TraceEventFlusherH2 extends TraceEventFlusherJdbc {
	
	String url = null;
	String user = null;
	String password = null;
	
	public TraceEventFlusherH2(Config config) throws Exception {
		
		url = config.getString("url");
		if (url == null) throw new ServerException("Missing configuration property [url]");

		user = config.getString("user");
		if (user == null) throw new ServerException("Missing configuration property [user]");

		password = config.getString("password");
		if (password == null) throw new ServerException("Missing configuration property [password]");
		
	}

	@Override
	public Connection getJdbcConnection() throws Exception {
		Class.forName("org.h2.Driver");
		return DriverManager.getConnection(url, user, password);
	}

	@Override
	protected Vendor getJdbcVendor() {
		return Vendor.H2;
	}
	
}
