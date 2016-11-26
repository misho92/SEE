package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DB;
import logic.User;

public class WelcomeController {

	@FXML
	private Label welcome;
	
	@FXML
    private ListView listIssues;
	
	@FXML
    private ListView listTasks;
	
	@FXML
    private ListView listSubTasks;
	
	@FXML
    private Text details;
	
	@FXML
    private Text type;
	
	@FXML
    private Text priority;
	
	@FXML
    private Text status;
	
	@FXML
    private Text devDetails;
	
	@FXML
    private Text title;
	
	@FXML
    private Text assignee;
	
	@FXML
    private Text tStatus;
	
	@FXML
    private Text tPriority;
	
	@FXML
    private Text start;
	
	@FXML
    private Text end;
	
	@FXML
    private TextArea description;
	
	@FXML
    private Text subDevDetails;
	
	@FXML
    private Text sTitle;
	
	@FXML
    private Text sAssignee;
	
	@FXML
    private Text sStatus;
	
	@FXML
    private Text sPriority;
	
	@FXML
    private Text sStart;
	
	@FXML
    private Text sEnd;
	
	@FXML
    private TextArea sDescription;
	
	static List<String> issues;
	
	private static HashMap<String,String> map;
	
	private ArrayList<logic.Task> tasks;
	
	private ArrayList<logic.Task> subTasks;
	
	@FXML
    private ImageView plus;
	
	@FXML
    private ImageView sPlus;
	
	private String encoded;
	
	private User currentUser;
	
	//get project roles http://messir.uni.lu:8085/jira/rest/api/2/user?username=Mihail&expand=applicationRoles
	
	//initialize all components
	@FXML
    public void initialize() {
        parseIssues();
        listIssues.setItems(FXCollections.observableList(issues));
        listIssues.setPrefHeight(800);
        listTasks.setPrefHeight(800);
        listSubTasks.setPrefHeight(800);
        details.setFont(Font.font(null, FontWeight.BOLD, 20));
        devDetails.setFont(Font.font(null, FontWeight.BOLD, 20));
        subDevDetails.setFont(Font.font(null, FontWeight.BOLD, 20));
        //issue list listener
        listIssues.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            type.setText("Type: " + map.get(newValue.toString()).split("name")[1].substring(3).split("\"")[0]);
            priority.setText("Priority: " + map.get(newValue.toString()).split("priority")[2].split("name")[1].substring(3).split("\"")[0]);
            status.setText("Status: " + map.get(newValue.toString()).split("updated")[1].split("name")[1].substring(3).split("\"")[0]);
            if(listTasks.getItems().size() != 0) listTasks.getItems().clear();
            tasks = new DB().getTasksForIssue(newValue.toString());
            if(listSubTasks.getItems().size() != 0) listSubTasks.getItems().clear();
            for(int i = 0; i < tasks.size(); i++ ){
            	listTasks.getItems().add(tasks.get(i).getTitle());
            	listTasks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
		            public ListCell<String> call(ListView<String> param) {
		                return new XCell(listTasks, listSubTasks, "task", currentUser);
		            }
		        });
            }
            //description.setDisable(true);
        });
        
        //task list listener
        listTasks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	for(int i = 0; i < tasks.size(); i++ ){
        		if(tasks.get(i).getTitle().equals(newValue)){
        			title.setText("Title: " + tasks.get(i).getTitle());
        			assignee.setText("Assignee: " + tasks.get(i).getAssignee().getName());
        			tStatus.setText("Status: " + tasks.get(i).getStatus().getStatus());
        			tPriority.setText("Priority: " + tasks.get(i).getPriority());
        			start.setText("Start date: " + tasks.get(i).getStartDate());
        			end.setText("Due date: " + tasks.get(i).getDueDate());
        			description.setText(tasks.get(i).getDescription());
        			subTasks = tasks.get(i).getSubTasks();
        			if(listTasks.getItems().size() != 0) listSubTasks.getItems().clear();
        			for(int j = 0; j < tasks.get(i).getSubTasks().size(); j++ ){
        				listSubTasks.getItems().add(tasks.get(i).getSubTasks().get(j).getTitle());
        				listSubTasks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
        		            public ListCell<String> call(ListView<String> param) {
        		                return new XCell(listTasks,listSubTasks,"subtask", currentUser);
        		            }
        		        });
                    }
        		}
            }
        });
        
        //subtask list listener
        listSubTasks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for(int i = 0; i < subTasks.size(); i++){
            	if(subTasks.get(i).getTitle().equals(newValue)){
            		sTitle.setText("Title: " + subTasks.get(i).getTitle());
        			sAssignee.setText("Assignee: " + subTasks.get(i).getAssignee().getName());
        			sStatus.setText("Status: " + subTasks.get(i).getStatus().getStatus());
        			sPriority.setText("Priority: " + subTasks.get(i).getPriority());
        			sStart.setText("Start date: " + subTasks.get(i).getStartDate());
        			sEnd.setText("Due date: " + subTasks.get(i).getDueDate());
        			sDescription.setText(subTasks.get(i).getDescription());
            	}
            }
        });
        
        //adding the plus image
        File file = new File("D:/Workspace/SEE/src/images/plus.png");
        Image image = new Image(file.toURI().toString());
        plus.setImage(image);
        sPlus.setImage(image);
        plus.setOnMouseClicked(event -> openTask());
        sPlus.setOnMouseClicked(event -> openSubTask());
    }
	
	//handling adding a task
	private void openTask() {
		if(!listIssues.getSelectionModel().isEmpty()){
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/addTask.fxml"));
			Parent root = null;
			try {
				root = (Parent) loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TaskController controller = loader.<TaskController>getController();
			controller.initData(listIssues.getSelectionModel().getSelectedItem().toString(), this, null);
			final Stage stage = new Stage();
	        Scene scene = new Scene(root, 500, 650);
	        stage.setTitle("Add Task");
	        stage.setScene(scene);
	        stage.show();
		}
		else{
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Select an issue");
        	alert.setContentText("Pick an issue to which you want to associate the task");
        	alert.showAndWait();
		}
	}
	
	//handling adding a subtask
	private void openSubTask(){
		if(!listTasks.getSelectionModel().isEmpty()){
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/addTask.fxml"));
			Parent root = null;
			try {
				root = (Parent) loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TaskController controller = loader.<TaskController>getController();
			controller.initData(listIssues.getSelectionModel().getSelectedItem().toString(), this, listTasks.getSelectionModel().getSelectedItem().toString());
			final Stage stage = new Stage();
	        Scene scene = new Scene(root, 500, 650);
	        stage.setTitle("Add subtask");
	        stage.setScene(scene);
	        stage.show();
		}
		else{
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Select a task");
        	alert.setContentText("Pick a task to which you want to associate the subtask");
        	alert.showAndWait();
		}
	}
	
	//parse all issues from JIRA API
	public void parseIssues(){
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
	public String getAllIssues(){
		String output = "";
		try {
			//URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=test");
			URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/search?jql=project=SEEMT");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + encoded);
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
			System.out.println("Response \n" + output);
			conn.disconnect();
		 } catch (MalformedURLException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
		return output;
	}

	public void initData(String encoded, User user) {
		this.encoded = encoded;
		this.currentUser = user;
	}
}
