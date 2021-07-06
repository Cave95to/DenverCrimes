package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<String> getVertici(String categoria, int mese) {
		
		String sql = "SELECT DISTINCT offense_type_id "
				+ "FROM events "
				+ "WHERE offense_category_id = ? AND Month(reported_date) = ?";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, categoria);
			st.setInt(2, mese);
			
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
					list.add(res.getString("offense_type_id"));
				
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String categoria, int mese) {
	
		String sql = "SELECT e1.offense_type_id as t1, e2.offense_type_id as t2, COUNT(DISTINCT e1.neighborhood_id) AS peso "
				+ "FROM events as e1, events AS e2 "
				+ "WHERE e1.offense_type_id > e2.offense_type_id AND e1.offense_category_id = e2.offense_category_id AND "
				+ "MONTH(e1.reported_date) = MONTH(e2.reported_date) AND e1.neighborhood_id = e2.neighborhood_id "
				+ "AND MONTH(e1.reported_date) = ? AND e1.offense_category_id = ? "
				+ "GROUP BY e1.offense_type_id, e2.offense_type_id";
		
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(2, categoria);
			st.setInt(1, mese);
			
			List<Adiacenza> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
					list.add(new Adiacenza(res.getString("t1"), res.getString("t2"), res.getInt("peso")));
				
			}
			
			conn.close();
			return list ;
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	
	
	}

	public List<String> getCategorie() {
		
		String sql = "SELECT DISTINCT offense_category_id AS t FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
					list.add(res.getString("t"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Integer> getMesi() {
		String sql = "SELECT DISTINCT MONTH(reported_date) AS t FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Integer> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
					list.add(res.getInt("t"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
}
