package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DB;
import logic.User;


public class StartController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;

    @FXML
    private Button register;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
    
    @FXML
    private ComboBox role;
    
    private URL url;
    
    private HttpURLConnection conn;
    
    private String email;
    
    private String displayName;
    
    private String encoded;
    
    private User user;

    public void initialize(URL location, ResourceBundle resources) {

    	//http://messir.uni.lu:8085/jira/rest/api/2/user?username=Mihail
    	
        //The button event for the login button with JIRA API
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)   {
            	try {
            		authenticate();
        			if(conn.getResponseCode() == 200){
        				extractUserData();
						if(new DB().isRegistered(email)){
							user = new DB().loadUser(email);
							loadGUI();	
						}
						else{
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error Dialog");
							alert.setHeaderText("You are not registered!");
							alert.setContentText("Please register to the tool");
							alert.showAndWait();
						}
        			}
        			else if(conn.getResponseCode() == 401){
        				incorrectCredentials();
        			}
        			else{
        				errorJIRA();
        			}
        		}catch (MalformedURLException ex) {
        				ex.printStackTrace();
        				displayError(ex);
        			 } catch (IOException ex) {
        				ex.printStackTrace();
        				displayError(ex);
        			 }
            }
        });
        register.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)   {
            	try {
            		authenticate();
        			if(conn.getResponseCode() == 200){
        				extractUserData();
						if(new DB().register(email,displayName,role.getSelectionModel().getSelectedItem().toString())){
							user = new User(email,displayName,role.getSelectionModel().getSelectedItem().toString());
							loadGUI();
						}
        			}
        			else if(conn.getResponseCode() == 401){
        				incorrectCredentials();
        			}
        			else{
        				errorJIRA();
        			}
        		}catch (MalformedURLException ex) {
        				ex.printStackTrace();
        				displayError(ex);
        			 } catch (IOException ex) {
        				ex.printStackTrace();
        				displayError(ex);
        			 }
            }
        });
    }
    
    public void authenticate() throws IOException{
    	//authentication
		URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/user?username=" + 
				usernameField.getText());
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		String auth = usernameField.getText() + ":" + passwordField.getText();
		byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
		encoded = Base64.getEncoder().encodeToString(credentials);
		conn.setRequestProperty("Authorization", "Basic " + encoded);
    }
    
    public void extractUserData(){
    	BufferedReader br;
    	String response;
		try {
			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			response = br.readLine();
			while (response != null) {
				email = response.split("emailAddress")[1].substring(3).split("\"")[0];
				displayName = response.split("displayName")[1].substring(3).split("\"")[0];
				response = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayError(e);
		}
    }
    
    public void loadGUI(){
    	final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Welcome.fxml"));
		//inject the conn into the loader at start up
		loader.setControllerFactory(new Callback<Class<?>, Object>() {
		    @Override
		    public Object call(Class<?> controllerClass) {
		        if (controllerClass == WelcomeController.class) {
		        	WelcomeController controller = new WelcomeController();
		            controller.initData(encoded, user);
		            return controller;
		        } else {
		            try {
		                return controllerClass.newInstance();
		            } catch (Exception e) {
		            	displayError(e);
		                throw new RuntimeException(e);
		            }
		        }
		    }
		});
		Parent root;
		try {
			root = (Parent) loader.load();
			WelcomeController controller = loader.<WelcomeController>getController();
			final Stage stage = new Stage();
			stage.setTitle("Dashboard");
			stage.setScene(new Scene(root,1500,800));
			stage.show();
			Stage closingStage = (Stage) loginButton.getScene().getWindow();
			closingStage.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayError(e);
		}
    }
    
    public void displayError(Exception ex){
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error Dialog");
    	alert.setHeaderText("Error");
    	alert.setContentText(ex.getMessage());
	}
    
    public void incorrectCredentials(){
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error Dialog");
    	alert.setHeaderText("Incorrect credentials");
    	alert.setContentText("Incorrect credentials");
    	alert.showAndWait();
	}
    
    public void errorJIRA() throws IOException{
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error Dialog");
    	alert.setHeaderText("Error in the JIRA API");
    	alert.setContentText("Error code " + conn.getResponseCode());
    	alert.showAndWait();
    }
}