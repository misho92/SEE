package Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.controlsfx.control.textfield.TextFields;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DB;

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
	
	private String issue;
	
	private String task;
	
	WelcomeController welcomeController;
	
	private int indexTask;
	
	private int indexSub;
	
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
                	if(task != null)new DB().addTask(titleField.getText(), description.getText(), 
                			assigneeField.getText(), task, "NEW", java.sql.Date.valueOf(start.getValue()), 
                			java.sql.Date.valueOf(end.getValue()), issue, 
                			priority.getSelectionModel().getSelectedItem().toString());
                	else new DB().addTask(titleField.getText(), description.getText(), assigneeField.getText(), null, 
                			"NEW", java.sql.Date.valueOf(start.getValue()), java.sql.Date.valueOf(end.getValue()), 
                			issue, priority.getSelectionModel().getSelectedItem().toString());
                	//shut stage on clicking save
                	Stage stage = (Stage) save.getScene().getWindow();
                	stage.close();
                	welcomeController.initialize();
                	welcomeController.listTasks.getSelectionModel().select(indexTask);
                	welcomeController.listSubTasks.getSelectionModel().select(indexSub);
                }
            }
        });
	}
	
	public void edit(String task, String oldValue){
		save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkValidity()){
                	if(task.equals("task"))new DB().editTask(titleField.getText(), description.getText(), 
                			assigneeField.getText(), null, "NEW", java.sql.Date.valueOf(start.getValue()), 
                			java.sql.Date.valueOf(end.getValue()), issue, 
                			priority.getSelectionModel().getSelectedItem().toString(), oldValue);
                	else new DB().editTask(titleField.getText(), description.getText(), assigneeField.getText(), task, 
                			"NEW", java.sql.Date.valueOf(start.getValue()), java.sql.Date.valueOf(end.getValue()), 
                			issue, priority.getSelectionModel().getSelectedItem().toString(), oldValue);
                	//shut stage on clicking save
                	Stage stage = (Stage) save.getScene().getWindow();
                	stage.close();
                }
            }
        });
	}
	
	public void initData(String issue, WelcomeController welcomeController, String task, int indexTask, int indexSub){
		this.issue = issue;
		this.welcomeController = welcomeController;
		this.task = task;
		this.indexTask = indexTask;
		this.indexSub = indexSub;
	}
	
	public boolean checkValidity(){
		boolean valid = false;
		if(titleField.getLength() > 20){
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Too many characters");
        	alert.setContentText("Please type maximum 20 characters for the title");
        	alert.showAndWait();
		}
		else if(!titleField.getText().trim().isEmpty() && titleField.getText() != null && 
				!assigneeField.getText().trim().isEmpty() && assigneeField.getText() != null && end.getValue() != null){
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

	public void setTextTitleField(String text) {
		this.titleField.setText(text);
	}
	
	public void setTextAssigneeField(String text) {
		this.assigneeField.setText(text);
	}
	
	public void setPriority(String text) {
		this.priority.getSelectionModel().select(Math.abs(Integer.parseInt(text) - 5));
	}
	
	public void setStartDate(String start) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(start, formatter);
		this.start.setValue(date);
	}
	
	public void setDueDate(String end) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse((CharSequence) end, formatter);
		this.end.setValue(date);
	}
	
	public void setDescription(String text) {
		this.description.setText(text);
	}
}
