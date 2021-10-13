/**
 * 
 */
package com.venkat.poc.rds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author VenkaT
 *
 */
public class RdsClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RdsClient rdsClient=new RdsClient();
		System.out.println(rdsClient.getDetails());
	}
	
	private ResultSet getDetails() {
		Connection connection = null;
		 String	host="jdbc:mysql://database-mysql-poc.cpcnpcxbmsiy.us-east-1.rds.amazonaws.com:3306";
		try {
			System.out.println("Trying to get the connection...");
			String secretName = "dev/mysql-poc";
			String region = "us-east-1";
			//String secretDetails = getSecret(secretName,region);
			//JsonNode secretNode = objectMapper.readTree(secretDetails);
			//String host = "jdbc:mysql://"+secretNode.get("host").asText()+":"+secretNode.get("port");
			System.out.println("Host of RDS - " + host);
			String username = "admin";//secretNode.get("username").asText();
			System.out.println("username of RDS - " + username);
			String password = "mysqladmin";//secretNode.get("password").textValue();
			// System.out.println("pwd of RDS - "+password);

			connection = DriverManager.getConnection(host, username, password);

			System.out.println("Connection forked...");
			PreparedStatement preparedStatement = connection.prepareStatement("select * from dev.member");
			
			
			// Statement statement=connection.createStatement();
			ResultSet insertFlag = preparedStatement.executeQuery();
			
			System.out.println("Number of records inserted: " + insertFlag);
			return insertFlag;
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			if (Objects.nonNull(connection)) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

}
