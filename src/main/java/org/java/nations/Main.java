package org.java.nations;

import java.math.BigDecimal;
import java.math.BigInteger;
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
		
		// query da passare al PreparedStatement che prende gli stati che contengono la sottostringa
		final String sql = " SELECT c.country_id, c.name as country_name, r.name as region_name, c2.name as continent_name "
						 + " FROM countries c "
						 + " JOIN regions r "
						 + "	ON c.region_id = r.region_id "
						 + " JOIN continents c2 "
						 + "	ON r.continent_id = c2.continent_id "
						 + " WHERE c.name LIKE ? ";
		
		// query da passare al PreparedStatement che prende le statistiche del paese selezionato in base all'id 
		final String sqlDetails = "	SELECT  c.name, cs.`year` as year, cs.population as population, cs.gdp as PIL	"
								+ "	FROM countries c "
								+ "	JOIN country_languages cl "
								+ "	ON c.country_id = cl.country_id "
								+ "	JOIN country_stats cs "
								+ "	ON c.country_id = cs.country_id "
								+ "	WHERE c.country_id = ? "
								+ "	ORDER BY cs.`year` DESC "
								+ "	LIMIT 1; ";
		
		// query da passare al PreparedStatement che prende le lingue del paese selezionato in base all'id
		final String sqlLangs = " SELECT l.`language` "
							  + " FROM languages l "
							  + " JOIN country_languages cl "
							  + "	ON l.language_id = cl.language_id "
							  + " JOIN countries c "
							  + "	On cl.country_id = c.country_id "
							  + " WHERE c.country_id = ?; ";
		
		
		// stmpo le colonne della tabella
		System.out.printf("\n%-10s %-20s %-30s %-50s\n",  "ID",
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
				
				
				System.out.printf("\n%-10s %-20s %-30s %-50s\n",  id,
								country_name,
								region_name
								,continent_name);	
			}
			
			
			// scegli l'id del paese
			System.out.println("\nScegli l'id di un paese: ");
			int idToFind = sc.nextInt();
			
			// preparo e cerco le lingue parlate nel paese
			PreparedStatement psLangs = conn.prepareStatement(sqlLangs);
			psLangs.setInt(1, idToFind);
			ResultSet rsLangs = psLangs.executeQuery();
			
			System.out.print("Laguages: ");
			while(rsLangs.next()) {
				String lang = rsLangs.getString("language");
				
				
				System.out.print(lang + (rsLangs.isLast() ? " " : ", "));
			}	
			
			
			// preparo e cerco le statistiche di un paese
			PreparedStatement psDet = conn.prepareStatement(sqlDetails);
			psDet.setInt(1, idToFind);
			ResultSet rsDet = psDet.executeQuery();
			
			while(rsDet.next()) {
				String name = rsDet.getString("name");
				System.out.println("\n\nDettagli per il paese: " + name);
				int year = rsDet.getInt("year");
				int population = rsDet.getInt("population");
				BigDecimal pil = rsDet.getBigDecimal("PIL");
				
				System.out.println("Anno: " + year );
				System.out.println("Popolazione: " + population );
				System.out.println("PIL: " + pil );
				
			}
		} catch (Exception e) {
			
			System.out.println("Errore di connessione: " + e.getMessage());
		}
		
		System.out.println("\n----------------------------------\n");
		System.out.println("The end");
	}
}
