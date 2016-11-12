package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class WelcomeController {

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
	static final String DB_URL = "jdbc:mysql://localhost/see";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	Connection conn = null;
	PreparedStatement st = null;
	ResultSet rs = null;
	String username;
	String pass;

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
			//byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
			//String encoded = Base64.getEncoder().encodeToString(credentials);
			//conn.setRequestProperty("Authorization", "Basic " + encoded);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			else if(conn.getResponseCode() == 401){
				Alert alert = new Alert(AlertType.ERROR);
            	alert.setTitle("Error Dialog");
            	alert.setHeaderText("Incorrect credentials");
            	alert.setContentText("Incorrect credentials");
            	alert.showAndWait();
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
	
	public void setCredentials(String username,String pass){
		this.username = username;
		this.pass = pass;
	}
	
	//send email notification
	public boolean sendEmail(){
		String to = null;
		try{
			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/user?username=" + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			String auth = username + ":" + pass;
			byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
			String encoded = Base64.getEncoder().encodeToString(credentials);
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String response = br.readLine();
			while (response != null) {
				to += response;
				response = br.readLine();
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		to = to.split("emailAddress")[1].substring(3).split("\"")[0];
		System.out.println(to);
		boolean isSend = false;
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("user","pass");
				}
			});
		session.setDebug(true);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("matachev92@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("Subject");
			message.setText("Message");
			Transport.send(message);
			System.out.println("Email sent!");
			isSend = true;
	      }catch (MessagingException mex) {
	    	  System.out.println("Email was not sent!");
	    	  Alert alert = new Alert(AlertType.ERROR);
          	  alert.setTitle("Error Dialog");
          	  alert.setHeaderText("Email notification not sent");
          	  alert.setContentText("Email notification not sent");
          	  alert.showAndWait();
	    	  mex.printStackTrace();
	      }
		return isSend;
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

	public boolean login(String username, String pass) {
		try {
			//avoid sql injection
			st = conn.prepareStatement("SELECT * FROM SEE.user WHERE username = ? AND pass = ?");
			st.setString(1, username);
			st.setString(2, pass);

			rs = st.executeQuery();
			
			if (rs.next()) {
				name = "Welcome " + rs.getString("username");
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
