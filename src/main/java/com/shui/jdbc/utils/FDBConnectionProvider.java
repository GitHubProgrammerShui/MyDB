package com.shui.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.shui.jdbc.dbprovider.exceptions.IllegalParameterException;

/**
 * 数据库连接提供者，专门用于提供数据库连接
 * @author shuibaoqin
 */
class FDBConnectionProvider extends ConnectionProvider {

	
	private Properties properties;
	
	
	private final String DRIVER;
	private final String URL;
	private final String USER_NAME;
	private final String USER_PASSWORD;
	
	private Connection[] conns =null;
	private int[] connNum = null;
	private int connCount=0;
	private int maxConnecting;
	
	
	public FDBConnectionProvider(Properties properties,int connSum,int maxConnecting){
		DRIVER=properties.getProperty("driver");
		URL=properties.getProperty("url");
		USER_NAME=properties.getProperty("userName");
		USER_PASSWORD=properties.getProperty("userPassword");
		if(connSum>0&&maxConnecting>0){
			conns=new Connection[connSum];
			connNum=new int[connSum];
			this.maxConnecting=maxConnecting;
		}else{
			throw new IllegalParameterException();
		}
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cannot found the driver", e);
		}
	}
	
	public FDBConnectionProvider(Properties properties){
		this(properties, 5, 10);
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 */
	@Override
	public Connection getConnection() {
		try {
			for (int i = 0; i < connNum.length; i++) {
				if (connNum[i] < maxConnecting) {
					if (conns[i] == null || conns[i].isClosed()) {
						conns[i] = DriverManager.getConnection(URL, USER_NAME,
								USER_PASSWORD);
					}
					connNum[i]++;
					connCount++;
					return conns[i];
				}
			}
			return null;
		} catch (SQLException e) {
			System.out.println("the exception has been thrown during the connection creating----"
							+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 回收连接
	 * 
	 * 该方法可以对由该管理器分发的Connection进行回收
	 * 
	 * @param conn 需要被回收的Connection
	 */
	@Override
	public void recycleConnection(Connection conn) {
		if (conn!=null) {
			Class cls = conn.getClass();
			int i = 0;
			try {
				for (i = 0; i < conns.length; i++) {
					if (cls == conns[i].getClass()) {
						connNum[i]--;
						connCount--;
						if (connNum[i] == 0) {
							if (conns[i] != null) {
								conns[i].close();
							}
						}
						System.out.println("回收成功！");
						return;
					}
				}
			} catch (SQLException e) {
				System.out.println("回收出现异常，在连接池的第" + i + "个连接中---"
						+ e.getMessage());
				e.printStackTrace();
			}
		}else{
			System.out.println("回收失败");
		}
	}
	
	/**
	 * 返回一个正整数，代表该Connection提供者分发出了多少个Connection
	 * @return
	 */
	@Override
	public int getConnectionCount(){
		return connCount;
	}

	/**
	 * 关闭连接提供者
	 * 
	 * @return
	 */
	@Override
	public boolean closeProvider() {
		try {
			for (Connection conn : conns) {
				if (conn != null) {
					conn.close();
				}
			}
			return true;
		} catch (SQLException e) {
			System.out.println("关闭失败！" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
