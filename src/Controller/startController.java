package Controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
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

        //The button event for the login button with JIRA API
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)   {
            	try {
        			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=seemt");
        			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        			conn.setRequestMethod("GET");
        			conn.setRequestProperty("Accept", "application/json");
        			String auth = usernameField.getText() + ":" + passwordField.getText();
        			byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
        			String encoded = Base64.getEncoder().encodeToString(credentials);
        			conn.setRequestProperty("Authorization", "Basic " + encoded);
        			if(conn.getResponseCode() == 401){
        				Alert alert = new Alert(AlertType.ERROR);
                    	alert.setTitle("Error Dialog");
                    	alert.setHeaderText("Incorrect credentials");
                    	alert.setContentText("Incorrect credentials");
                    	alert.showAndWait();
        			}else{
        				try{
                			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Welcome.fxml"));
                			Parent root = (Parent) loader.load();
                			WelcomeController controller = loader.<WelcomeController>getController();
                			//controller.setCredentials(usernameField.getText(),passwordField.getText());
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
        		}catch (MalformedURLException ex) {
        				ex.printStackTrace();
        			 } catch (IOException ex) {
        				ex.printStackTrace();
        			 }
            }
        });
    }
}