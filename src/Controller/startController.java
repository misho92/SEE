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
    private Button newUserButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    public void initialize(URL location, ResourceBundle resources) {

    	//http://messir.uni.lu:8085/jira/rest/api/2/user?username=Mihail
    	
        //The button event for the login button with JIRA API
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)   {
            	try {
            		//authentication
        			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/user?username=" + 
        					usernameField.getText());
        			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        			conn.setRequestMethod("GET");
        			conn.setRequestProperty("Accept", "application/json");
        			String auth = usernameField.getText() + ":" + passwordField.getText();
        			byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
        			String encoded = Base64.getEncoder().encodeToString(credentials);
        			conn.setRequestProperty("Authorization", "Basic " + encoded);
        			if(conn.getResponseCode() == 200){
        				try{	
        					//new DB().loadUser(email); */
        					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        					String response = br.readLine();
        					String email = null;
        					while (response != null) {
        						if(response.contains("email")){
        							email = response.split("emailAddress")[1].substring(3).split("\"")[0];
        							break;
        						}
        						response = br.readLine();
        					}
        					User user = new DB().loadUser(email);
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
                			                throw new RuntimeException(e);
                			            }
                			        }
                			    }
                			});
                			Parent root = (Parent) loader.load();
                			WelcomeController controller = loader.<WelcomeController>getController();
                			final Stage stage = new Stage();
                			stage.setTitle("Dashboard");
                			stage.setScene(new Scene(root,1500,800));
                			stage.show();
                			Stage closingStage = (Stage) loginButton.getScene().getWindow();
                			closingStage.close();
                		}catch(final IOException ex){
                			ex.getStackTrace();
                		}
        			}
        			else if(conn.getResponseCode() == 401){
        				Alert alert = new Alert(AlertType.ERROR);
                    	alert.setTitle("Error Dialog");
                    	alert.setHeaderText("Incorrect credentials");
                    	alert.setContentText("Incorrect credentials");
                    	alert.showAndWait();
        			}
        			else{
        				Alert alert = new Alert(AlertType.ERROR);
                    	alert.setTitle("Error Dialog");
                    	alert.setHeaderText("Error in the JIRA API");
                    	alert.setContentText("Error code " + conn.getResponseCode());
                    	alert.showAndWait();
        			}
        		}catch (MalformedURLException ex) {
        				ex.printStackTrace();
        			 } catch (IOException ex) {
        				ex.printStackTrace();
        			 }
            }
        });
    }
}