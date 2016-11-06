package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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


public class startController implements Initializable {

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

        //The button event for the login button
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)   {
            	welcomeController welcome = new welcomeController();
            	welcome.getConnection();
            	if(welcome.login(usernameField.getText(),passwordField.getText())){
            		try{
            			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Welcome.fxml"));
            			//welcomeController welcome = (welcomeController)loader.getController();
            			Parent root = (Parent) loader.load();
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
            	else{
            		Alert alert = new Alert(AlertType.ERROR);
                	alert.setTitle("Error Dialog");
                	alert.setHeaderText("Incorrect login");
                	alert.setContentText("Incorrect login");
                	alert.showAndWait();	
            	}
            }
        });
        }
    }