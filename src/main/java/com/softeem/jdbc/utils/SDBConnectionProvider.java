package com.softeem.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class SDBConnectionProvider extends ConnectionProvider {
	private final String DRIVER;
	private final String URL;
	private final String USER_NAME;
	private final String USER_PASSWORD;

	private int maxConnecting = 0;

	private int connCount = 0;
	private Connection conn;

	public SDBConnectionProvider(Properties properties, int maxConnecting) {
		this.DRIVER = properties.getProperty("driver");
		this.URL = properties.getProperty("url");
		this.USER_NAME = properties.getProperty("userName");
		this.USER_PASSWORD = properties.getProperty("userPassword");
		if (maxConnecting > 0) {
			this.maxConnecting = maxConnecting;
		}
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		try {
			if (maxConnecting > 0) {
				if (connCount < maxConnecting) {
					if (conn == null || conn.isClosed()) {
						conn = DriverManager.getConnection(URL, USER_NAME,
								USER_PASSWORD);
					}
				}else{
					return null;
				}
			}else{
				if (conn == null || conn.isClosed()) {
					conn = DriverManager.getConnection(URL, USER_NAME,
							USER_PASSWORD);
				}
			}
			connCount++;
			return conn;
		} catch (SQLException e) {
			System.out.println("create connection failed!" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void recycleConnection(Connection conn) {
		if(this.conn==conn){
			if(connCount>0){
				connCount--;
			}
		}
	}

	@Override
	public int getConnectionCount() {
		return connCount;
	}

	@Override
	public boolean closeProvider() {
		if(conn!=null){
			try {
				conn.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
