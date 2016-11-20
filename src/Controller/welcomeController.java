package Controller;

import java.io.BufferedReader;
import java.io.File;
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

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.Toolkit.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DB;

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
	
	private String name;
	
	private static HashMap<String,String> map;
	
	private ArrayList<logic.Task> tasks;
	
	private ArrayList<logic.Task> subTasks;
	
	@FXML
    private ImageView plus;
	
	@FXML
    private ImageView sPlus;
	
	//get project roles http://messir.uni.lu:8085/jira/rest/api/2/user?username=Mihail&expand=applicationRoles
	
	@FXML
    public void initialize() {
        welcome.setText(name);
        parseIssues();
        listIssues.setItems(FXCollections.observableList(issues));
        listIssues.setPrefHeight(800);
        listTasks.setPrefHeight(800);
        listSubTasks.setPrefHeight(800);
        details.setFont(Font.font(null, FontWeight.BOLD, 20));
        devDetails.setFont(Font.font(null, FontWeight.BOLD, 20));
        subDevDetails.setFont(Font.font(null, FontWeight.BOLD, 20));
        //list view listener
        listIssues.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            type.setText("Type: " + map.get(newValue.toString()).split("name")[1].substring(3).split("\"")[0]);
            priority.setText("Priority: " + map.get(newValue.toString()).split("priority")[2].split("name")[1].substring(3).split("\"")[0]);
            status.setText("Status: " + map.get(newValue.toString()).split("updated")[1].split("name")[1].substring(3).split("\"")[0]);
            if(listTasks.getItems().size() != 0) listTasks.getItems().clear();
            tasks = new DB().getTasksForIssue(newValue.toString());
            if(listSubTasks.getItems().size() != 0) listSubTasks.getItems().clear();
            for(int i = 0; i < tasks.size(); i++ ){
            	listTasks.getItems().add(tasks.get(i).getTitle());
            	logic.Task t = tasks.get(i);
            	/*deleting tasks and subtasks if any from list view
            	if(t.getSubTasks().size() != 0) t.getSubTasks().clear();
            	tasks.remove(t);
            	for(logic.Task t: tasks){
            		listTasks.getItems().add(t.getTitle());
            	}*/
            	listTasks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
		            public ListCell<String> call(ListView<String> param) {
		                return new XCell(listTasks);
		            }
		        });
            }
            //description.setDisable(true);
        });
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
        			//issues.remove(2);
        			//listIssues.setItems(FXCollections.observableList(issues));
        			if(listTasks.getItems().size() != 0) listSubTasks.getItems().clear();
        			for(int j = 0; j < tasks.get(i).getSubTasks().size(); j++ ){
        				listSubTasks.getItems().add(tasks.get(i).getSubTasks().get(j).getTitle());
        				listSubTasks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
        		            public ListCell<String> call(ListView<String> param) {
        		                return new XCell(listSubTasks);
        		            }
        		        });
                    }
        		}
            }
        });
        
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
        File file = new File("D:/Workspace/SEE/src/images/plus.png");
        Image image = new Image(file.toURI().toString());
        plus.setImage(image);
        sPlus.setImage(image);
        plus.setOnMouseClicked(event -> openTask());
        sPlus.setOnMouseClicked(event -> openSubTask());
    }
	
	private void openTask() {
		System.out.println("openTask clicked");
	}
	
	private void openSubTask(){
		System.out.println("openSubTask clicked");
	}

	static class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("X");
        String lastItem;
        ListView<String> listView;
        boolean success = false;

        public XCell(ListView<String> listView) {
            super();
            this.listView = listView;
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("button X clicked " + lastItem);
                    success = new DB().deleteTask(lastItem);
                    if(success) listView.getItems().remove(lastItem);
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item!=null ? item : "<null>");
                //listView.getItems().remove(item);
                /*
                 * File image = new File("D:/Workspace/SEE/src/images/expand.png");
                try {
					setGraphic(new ImageView(new Image(image.toURL().toString())));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 */
                setGraphic(hbox);
            }
        }
    }
	
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
