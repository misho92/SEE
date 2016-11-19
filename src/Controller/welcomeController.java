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
    private ListView listIssues;
	
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
	
	//get project roles http://messir.uni.lu:8085/jira/rest/api/2/user?username=Mihail&expand=applicationRoles
	
	@FXML
    public void initialize() {
        welcome.setText(name);
        parseIssues();
        listIssues.setItems(FXCollections.observableList(issues));
        listIssues.setPrefHeight(800);
        details.setFont(Font.font(null, FontWeight.BOLD, 20));
        //list view listener
        listIssues.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
			//URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=test");
			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=test");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			//String auth = name + ":" + pass;
			//byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
			//String encoded = Base64.getEncoder().encodeToString(credentials);
			//conn.setRequestProperty("Authorization", "Basic " + encoded);
			System.out.println(conn.getResponseCode());
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
}
