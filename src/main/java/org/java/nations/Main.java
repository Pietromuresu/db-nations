package org.java.nations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {

	public static void main(String[] args) {
		
		final String url = "jdbc:mysql://localhost:8889/db-nations";
		final String user = "root";
		final String password = "root";
		
		final String sql = " SELECT c.country_id, c.name as country_name, r.name as region_name, c2.name as continent_name "
						 + " FROM countries c "
						 + " JOIN regions r "
						 + "	ON c.region_id = r.region_id "
						 + " JOIN continents c2 "
						 + "	ON r.continent_id = c2.continent_id; ";
		
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
				
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("country_id");
				String country_name =rs.getString("country_name");
				String region_name =rs.getString("region_name");
				String continent_name =rs.getString("continent_name");
				
				System.out.println("\nid: " + id
								+ "\ncountry_name:" + country_name
								+ "\nregion_name:" + region_name
								+ "\ncontinent_name:" + continent_name);	
			}
			
		} catch (Exception e) {
			
			System.out.println("Errore di connessione: " + e.getMessage());
		}
		
		System.out.println("\n----------------------------------\n");
		System.out.println("The end");
	}
}
