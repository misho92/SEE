package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class welcomeController {

	@FXML
	private Label welcome;
	
	@FXML
    private ListView listView;
	
	@FXML
    private Text details;
	
	@FXML
    private Text type;
	
	@FXML
    private Text priority;
	
	@FXML
    private Text status;
	
	static List<String> issues;
	
	private String name;
	
	private static HashMap<String,String> map;

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
        parseIssues();
        listView.setItems(FXCollections.observableList(issues));
        listView.setPrefHeight(800);
        details.setFont(Font.font(null, FontWeight.BOLD, 20));
        //list view listener
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            type.setText("Type: " + map.get(newValue.toString()).split("name")[1].substring(3).split("\"")[0]);
            priority.setText("Priority: " + map.get(newValue.toString()).split("priority")[2].split("name")[1].substring(3).split("\"")[0]);
            status.setText("Status: " + map.get(newValue.toString()).split("updated")[1].split("name")[1].substring(3).split("\"")[0]);
        });
    }
	
	public static void parseIssues(){
		map = new HashMap<String,String>();
		issues = new ArrayList<String>();
		String output = getAllIssues();
		String data [] = output.split("expand");
		for(int i = 2; i < data.length; i++){
			String issue = data[i].split("key")[1].substring(3).split("\"")[0];
			map.put(issue,data[i]);
			issues.add(issue);
		}
	}
	
	//getting all the current issues from jira for tesma
	public static String getAllIssues(){
		String output = "";
		try {
			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=test");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String response = br.readLine();
			while (response != null) {
				output += response;
				response = br.readLine();
			}
			System.out.println("Response");
			System.out.println(output);
			conn.disconnect();
		 } catch (MalformedURLException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
		return output;
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
