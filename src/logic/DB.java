package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DB {
	
	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost/see";

		// Database credentials
		static final String USER = "root";
		static final String PASS = "root";

		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String username;
		String pass;
		
		public Connection getConnection() {

			try {
				Class.forName(JDBC_DRIVER);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Connecting to database...");
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Connected to database");
			return conn;
		}
		
		public boolean login(String username, String pass) {
			try {
				//avoid sql injection
				st = conn.prepareStatement("SELECT * FROM SEE.user WHERE username = ? AND pass = ?");
				st.setString(1, username);
				st.setString(2, pass);

				rs = st.executeQuery();
				
				if (rs.next()) {
					//name = "Welcome " + rs.getString("username");
				    return true;
				}
				
				/*Extract data from result set
				while (rs.next()) {
					// Retrieve by column name
					name = "Welcome " + rs.getString("username");
				}*/
				
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
			}
			finally{
			      //finally block used to close resources
			      try{
			         if(st!=null)
			            st.close();
			      }catch(SQLException se2){}
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			}
			return false;
		}
		
		public boolean register(String username, String pass){
			try {
				st = conn.prepareStatement("INSERT INTO see.user VALUES(?,?)");
				st.setString(1, username);
				st.setString(2, pass);
				int rows = st.executeUpdate();
				
				if (rows != 0) {
				    return true;
				}
				
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
				Alert alert = new Alert(AlertType.ERROR);
	        	alert.setTitle("Error Dialog");
	        	alert.setHeaderText("Something went wrong");
	        	alert.setContentText("Registration failed");
	        	alert.showAndWait();
			}
			finally{
			      //finally block used to close resources
			      try{
			         if(st!=null)
			            st.close();
			      }catch(SQLException se2){}
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			}
			return false;
		}

}
