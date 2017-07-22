package com.softeem.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.softeem.jdbc.dbprovider.exceptions.IllegalParameterException;

class VDBConnectionProvider extends ConnectionProvider {

	private final String DRIVER;
	private final String URL;
	private final String USER_NAME;
	private final String USER_PASSWORD;

	private int maxConnecting;
	
	private int connectingCount;
	private Set<ConnObj> connSet;

	private class ConnObj {
		private int connCount = 0;
		private Connection conn;

		public Connection getConn() {
			return conn;
		}
		public void setConnection(Connection conn) {
			this.conn = conn;
		}
		public int getConnCount() {
			return connCount;
		}
		public void connCountAdd() {
			connCount++;
		}
		public boolean connCountminus() {
			connCount--;
			return connCount == 0 ? true : false;
		}
	}

	public VDBConnectionProvider(Properties properties, int maxConnecting) {
		DRIVER = properties.getProperty("driver");
		URL = properties.getProperty("url");
		USER_NAME = properties.getProperty("userName");
		USER_PASSWORD = properties.getProperty("userPassword");
		if(maxConnecting>0){
			this.maxConnecting = maxConnecting;
		}else{
			throw new IllegalParameterException();
		}

		connSet = new HashSet<>();
	}

	@Override
	public Connection getConnection() {
		if (!connSet.isEmpty()) {
			for (ConnObj cObj : connSet) {
				if (cObj.getConnCount() < maxConnecting) {
					cObj.connCountAdd();
					connectingCount++;
					return cObj.getConn();
				}
			}
		}
		ConnObj connObj = null;
		try {
			Class.forName(DRIVER);
			connObj = new ConnObj();
			connObj.setConnection(DriverManager.getConnection(URL,
					USER_NAME, USER_PASSWORD));
			connObj.connCountAdd();
			connSet.add(connObj);
			connectingCount++;
			return connObj.getConn();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("failed for create new connection!\n"+e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void recycleConnection(Connection conn) {
		try {
			for(ConnObj cobj:connSet){
				if(conn==cobj.getConn()){
					if(cobj.connCountminus()){
						if(cobj.getConn()!=null){
							cobj.getConn().close();
						}
						connSet.remove(cobj);
						return;
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("failed for close connection!"+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int getConnectionCount() {
		return connectingCount;
	}

	@Override
	public boolean closeProvider() {
		ConnObj connObj=null;
		try {
			Iterator<ConnObj> iterator=connSet.iterator();
			while(iterator.hasNext()){
				connObj=iterator.next();
				if(connObj.getConn()!=null){
					connObj.getConn().close();
				}
				iterator.remove();
			}
			return true;
		} catch (SQLException e) {
			System.out.println("close provider failed!"+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
