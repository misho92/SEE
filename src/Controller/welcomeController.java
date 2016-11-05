package Controller;

import java.sql.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class welcomeController {

	@FXML
	private Label welcome;
	
	private String name;

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/test";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	
	@FXML
    public void initialize() {
        welcome.setText(name);
    }
    
	
	
	public void getText(){
		System.out.println(welcome.getText());
	}

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

	public void getSelect() {
		try {
			stmt = conn.createStatement();

			String sql;
			sql = "SELECT * FROM test.persons";

			rs = stmt.executeQuery(sql);
			
			//Extract data from result set

			while (rs.next()) {
				// Retrieve by column name
				int id = rs.getInt("PersonID");
				String last = rs.getString("LastName");
				name = "Welcome " + rs.getString("FirstName");
				String address = rs.getString("Address");
				String city = rs.getString("City");
			}
			initialize();
			System.out.println("Query executed");
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Query failed to execute");
		}
		finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){}
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		}
	}
}
