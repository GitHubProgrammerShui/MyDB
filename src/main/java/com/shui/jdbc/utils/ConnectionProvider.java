package com.shui.jdbc.utils;

import java.sql.Connection;
import java.util.Properties;

public abstract class ConnectionProvider {
	
	/**
	 * 根据提供者被创建时给定的连接信息提供一个连接
	 * @return 提供成功返回连接，失败则返回null
	 */
	public abstract Connection getConnection();
	
	/**
	 * 回收由该提供者分发出的Connection，如果传入的Connection不是由它的提供者分发出的，则不会被回收
	 * @param conn 需要被回收的连接
	 */
	public abstract void recycleConnection(Connection conn);
	
	/**
	 * 返回该提供者已经提供了多少个Connection
	 * @return 返回一个正整数，表示该提供者已经提供了多少个Connection
	 */
	public abstract int getConnectionCount();
	
	/**
	 * 关闭提供者
	 * @return 关闭成功返回true，失败则返回false
	 */
	public abstract boolean closeProvider();
	
	/**
	 * 获取一个用于连接指定的数据库的固定连接数量提供者，方法需要提供一个properties对象，内部含有驱动类，url，用户
	 * 名，密码这四个对象，创建出来的数据库连接提供者可以根据使用情况创建Connection，该方法创建的数据库连接提供者默认最多管理
	 * 5个Connection，每个Connection最多连接10次。
	 * @param properties
	 * 				包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @return
	 * 返回一个对应的数据库连接提供者
	 */
	public static ConnectionProvider newFixedDatabaseConnectionProvider(Properties properties){
		return new FDBConnectionProvider(properties);
	}
	
	/**
	 * 获取一个用于连接指定的数据库的固定连接数量提供者，方法需要提供一个properties对象，内部含有驱动类，url，用户
	 * 名，密码这四个对象，创建出来的数据库连接提供者可以根据使用情况创建Connection
	 * @param properties
	 * 				包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @param connSum  指定连接提供者中最多有多少个Connection
	 * @param maxConnecting  指定连接提供者中每个Connection最多同时连接多少次
	 * @return 返回一个对应的数据库连接提供者
	 */
	public static ConnectionProvider newFixedDatabaseConnectionProvider(Properties properties,int connSum,int maxConnecting){
		return new FDBConnectionProvider(properties,connSum,maxConnecting);
	}
	
	/**
	 * 获取一个用于连接数据库的变长数据库连接提供者，方法需要提供properties对象，该提供者可以变化管理Connection，当Connection不够用时
	 * 会自动创建新的Connection
	 * @param properties
	 * 				包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @param maxConnecting  指定每个Connection最大连接数目
	 * @return 返回一个可变数据库连接提供者
	 */
	public static ConnectionProvider newVariableDatabaseConnectionProvider(Properties properties,int maxConnecting){
		return new VDBConnectionProvider(properties,maxConnecting);
	}
	
	/**
	 * 获取一个用于连接数据库的变长数据库连接提供者，方法需要提供一个Properties对象，该提供者可以变化管理Connection，当Connection
	 * 不够用时自动创建新的Connection。该方法默认每个Connection最多可同时连接10次
	 * @param properties
	 * 				包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @return 返回一个变长数据库连接提供者
	 */
	public static ConnectionProvider newVariableDatabaseConnectionProvider(Properties properties){
		return new VDBConnectionProvider(properties,10);
	}
	
	/**
	 * 获取一个用于连接数据库的单个连接提供者，方法需提供一个Properties对象，内部只是用一个Connection进行分发，因此该提供者对
	 * Connection的重用度最高，但是也比较容易出问题，建议这种连接提供者只用于连接数较少的情况下。
	 * @param properties
	 * 				包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @param maxConnecting 指定每个Connection最大连接数目，由于该连接提供者只有一个Connection，所以这也是连接提供者能提供的最多
	 * 连接数。该参数值如果等于或小于0，则分发数无上限
	 * @return 返回一个单个连接提供者。
	 */
	public static ConnectionProvider newSingleDatabaseConnectionProvider(Properties properties,int maxConnecting){
		return new SDBConnectionProvider(properties,maxConnecting);
	}
	
	/**
	 * 创建一个用于连接数据库的单个连接提供者，方法需提供一个Properties对象，内部只是用一个Connection进行分发，因此该提供者对
	 * Connection的重用度最高，但是也比较容易出问题，建议这种连接提供者只用于连接数较少的情况下。同时，该方法创建的单个线程提供者会无上限的
	 * 提供连接，相当于该方法的另一个重载静态方法的第二个参数为0.
	 * @param properties
	 *  			包含有连接数据库所需的驱动类，url，用户名，密码的properties对象。通过这四个键指定连接数据库所需
	 * 的四个信息：<br/>
	 * 1，driver：驱动名<br/>
	 * 2，url：数据库url<br/>
	 * 3，userName：登录数据库用户名<br/>
	 * 4，userPassword：登录数据库密码<br/>
	 * @return 返回一个单个连接提供者。
	 */
	public static ConnectionProvider newSingleDatabaseConnectionProvider(Properties properties){
		return new SDBConnectionProvider(properties,0);
	}
}