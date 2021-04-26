package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	public boolean fermateCollegate(Fermata f1, Fermata f2) {
		
		String sql="SELECT COUNT(*) AS cnt "
				+"FROM connessione c "
				+"WHERE (id_stazP=? AND id_stazA=?) OR (id_stazP=? AND id_stazA=?)";
		
		try {
			Connection conn=DBConnect.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setInt(1, f1.getIdFermata());
			st.setInt(2, f2.getIdFermata());
			st.setInt(3, f2.getIdFermata());
			st.setInt(4, f1.getIdFermata());
			
			ResultSet rs=st.executeQuery();
			
			rs.first();
			
			int conteggio=rs.getInt("cnt");
			
			conn.close();
			
			return (conteggio>0);
		} catch (SQLException e) {
			throw new RuntimeException("Errore DB", e);
		}
	}
	
	public List<Connessione> getAllConnessioni(List<Fermata> fermate){
		String sql="SELECT id_connessione, id_linea, id_stazP, id_stazA "
				+"FROM connessione "
				+"WHERE id_stazP>id_stazA"; //uso > così dimezzo dim tabella (Ho tante andate quanti ritorni)
		
		try {
			Connection conn=DBConnect.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			ResultSet rs=st.executeQuery();
			
			List<Connessione> result=new ArrayList<>();
			while(rs.next()) {
				int id_partenza=rs.getInt("id_stazP");
				Fermata f_part=null;
				for(Fermata f:fermate)
					if(f.getIdFermata()==id_partenza)
						f_part=f;
				int id_arrivo=rs.getInt("id_stazA");
				Fermata f_arr=null;
				for(Fermata f:fermate)
					if(f.getIdFermata()==id_arrivo)
						f_arr=f;

				Connessione c=new Connessione(rs.getInt("id_connessione"),
						null, //ignoro perchè adesso non mi serve
						f_part, //Mi serve OGGETTO fermata e non solo id
						f_arr
						);
				result.add(c);
			}
			
			conn.close();
			return result;
		} catch(SQLException e) {
			throw new RuntimeException("Errore DB", e);
		}
		
	}


}
