package Controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.util.Callback;
import logic.DB;

import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.control.textfield.TextFields;

public class TaskController {

	@FXML
    private TextField titleField;
	
	@FXML
    private TextField assigneeField;
	
	@FXML
    private ComboBox priority;
	
	@FXML
    private DatePicker start;
	
	@FXML
    private DatePicker end;
	
	@FXML
    private Button save;
	
	@FXML
    private TextArea description;
	
	String issue;
	
	WelcomeController welcomeController;
	
	@FXML
	public void initialize(){
		//autocompletion assignee
		TextFields.bindAutoCompletion(assigneeField, new DB().getUsersName());
		//set today
		start.setValue(LocalDate.now());
		//datepickers handlers
		start.setOnAction(event -> {
		    LocalDate date = start.getValue();
		    System.out.println("Selected date: " + date);
		});
		end.setOnAction(event -> {
		    LocalDate date = end.getValue();
		    System.out.println("Selected date: " + date);  
		});
		
		//custom datecell to disable past days for start
		Callback<DatePicker, DateCell> dayStart = new Callback<DatePicker, DateCell>() {
			@Override
		    public DateCell call(final DatePicker datePicker) {
		        return new DateCell() {
		        	@Override
		        	public void updateItem(LocalDate item, boolean empty) {
		        		super.updateItem(item, empty);
		        		if (item.isBefore(LocalDate.now())) {
		        			setDisable(true);
		        			setStyle("-fx-background-color: #EEEEEE;");
		        		}
		        	}
		        };
		    }
		};
		start.setDayCellFactory(dayStart);
		
		//custom date cell to disable past days to end in order to avoid situations where due date is earlier than start
		Callback<DatePicker, DateCell> dayEnd = new Callback<DatePicker, DateCell>() {
			@Override
		    public DateCell call(final DatePicker datePicker) {
		        return new DateCell() {
		        	@Override
		        	public void updateItem(LocalDate item, boolean empty) {
		        		super.updateItem(item, empty);
		        		//1 day after the start
		        		if (item.isBefore(start.getValue().plusDays(1))) {
		        			setDisable(true);
		        			setStyle("-fx-background-color: #EEEEEE;");
		        		}
		        	}
		        };
		    }
		};
		end.setDayCellFactory(dayEnd);
		
		save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkValidity()){
                	new DB().addTask(titleField.getText(), description.getText(), assigneeField.getText(), "NEW", 
                			java.sql.Date.valueOf(start.getValue()), java.sql.Date.valueOf(end.getValue()), issue, 
                			priority.getSelectionModel().getSelectedItem().toString());
                	//shut stage on clicking save
                	Stage stage = (Stage) save.getScene().getWindow();
                	stage.close();
                	welcomeController.initialize();
                }
            }
        });
	}
	
	public void initDate(String issue, WelcomeController welcomeController){
		this.issue = issue;
		this.welcomeController = welcomeController;
	}
	
	public boolean checkValidity(){
		boolean valid = false;
		if(titleField.getText() != "" && assigneeField.getText() != "" && end.getValue() != null){
			valid = true;
		}
		else{
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Empty fields");
        	alert.setContentText("Please type in all fields");
        	alert.showAndWait();
		}
		return valid;
	}
}
