package Controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

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
	public void initialize(){
		//autocompletion assignee
		TextFields.bindAutoCompletion(assigneeField, new DB().getUsersName());
		//datepickers handlers
		start.setOnAction(event -> {
		    LocalDate date = start.getValue();
		    System.out.println("Selected date: " + date);
		});
		end.setOnAction(event -> {
		    LocalDate date = end.getValue();
		    System.out.println("Selected date: " + date);
		});
	}
}
