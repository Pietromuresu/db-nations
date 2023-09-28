package org.java.nations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		// dati per la connessione al DB 
		final String url = "jdbc:mysql://localhost:8889/db-nations";
		final String user = "root";
		final String password = "root";
		
		// chiedo all'utente di inserire una stringa e la formatto per poterla utilizzare con il binding
		System.out.println("Inserisci una stringa e trova il paese:");
		String search = sc.nextLine();
		String searched = "%" + search + "%";
		
		// query da passare al PreparedStatement
		final String sql = " SELECT c.country_id, c.name as country_name, r.name as region_name, c2.name as continent_name "
						 + " FROM countries c "
						 + " JOIN regions r "
						 + "	ON c.region_id = r.region_id "
						 + " JOIN continents c2 "
						 + "	ON r.continent_id = c2.continent_id "
						 + " WHERE c.name LIKE ? ";
		
		System.out.printf("\n%-10s %-50s %-50s %-50s\n",  "ID",
				"COUNTRY",
				"REGION",
				"CONTINENT");	
		
		
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			
			// preparo la query e aggiungo la stringa che abbiamo fatto inserire all'utente
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, searched);
			ResultSet rs = ps.executeQuery();
			
			// prendo tutti i dati riportati e li stampo
			while(rs.next()) {
				int id = rs.getInt("country_id");
				String country_name =rs.getString("country_name");
				String region_name =rs.getString("region_name");
				String continent_name =rs.getString("continent_name");
				
				
				System.out.printf("\n%-10s %-50s %-50s %-50s\n",  id,
								country_name,
								region_name
								,continent_name);	
			}
			
		} catch (Exception e) {
			
			System.out.println("Errore di connessione: " + e.getMessage());
		}
		
		System.out.println("\n----------------------------------\n");
		System.out.println("The end");
	}
}
