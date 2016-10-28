/**
 * 
 */
package com.mars.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author PATTLMX
 *
 */
public class ConnectionUtil {

	private static Connection connection = null;

	private static ConnectionUtil baseDAO = null;

	private ConnectionUtil() {

	}

	public static ConnectionUtil getInstance() {

		if (baseDAO == null) {
			baseDAO = new ConnectionUtil();
		}

		return baseDAO;
	}

	public Connection createConnection() throws ClassNotFoundException,
			SQLException, FileNotFoundException {
		Properties props = new Properties();
		FileInputStream in = new FileInputStream("db.properties");
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String driver = props.getProperty("jdbc.driver");
		if (driver != null) {
			Class.forName(driver);
		}

		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");

		if (connection == null) {
			System.out.println("Create Connection");
			// Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connection = DriverManager.getConnection(url, username,
					password);
			System.out.println("After Create Connection" + connection);
			return connection;
		} else {
			return connection;
		}
	}

	/**
	 * Sample Connection Test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ConnectionUtil dao = ConnectionUtil.getInstance();
		try {
			Connection ct = dao.createConnection();
			Statement st = ct.createStatement();
			ResultSet rs = st
					.executeQuery("select qi.LOT_NUMBER, qi.ROOT_NUMBER, qi.SALT_EXTENSION from mars.queue_items  qi LEFT JOIN mars.queue_item_transfers qit ON qi.id=qit.id and qi.STATE_ID=2 and qi.STOCK_ON_HAND='0' and (qit.Transfer_status='COMPLETE' or qit.TRANSFER_STATUS IS NULL)");

			while (rs.next()) {
				System.out.println("Root Number : " + rs.getString(2));

			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
