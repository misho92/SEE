package Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import logic.DB;
import logic.User;

//custom listview cell/row
class XCell extends ListCell<String> {
    HBox hbox = new HBox();
    Label label = new Label("(empty)");
    Pane pane = new Pane();
    Button button = new Button("X");
    ImageView edit = new ImageView();
    String lastItem;
    ListView<String> listTasks;
    ListView<String> listSubTasks;
    boolean success = false;
    String task;
    TaskController controller;
    User user;

    public XCell(ListView<String> listTasks, ListView<String> listSubTasks, String task, User user) {
        super();
        this.listTasks = listTasks;
        this.listSubTasks = listSubTasks;
        this.task = task;
        this.user = user;
        File file = new File("D:/Workspace/SEE/src/images/pencil.jpg");
        Image image = new Image(file.toURI().toString());
        edit.setImage(image);
        hbox.getChildren().addAll(label, pane, edit, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        edit.setOnMouseClicked(event -> edit());
        if(user.getRole().getRole().equals("developer")) button.setDisable(true);
        //deletion button handler
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("button X clicked " + lastItem);
                success = new DB().deleteTask(lastItem);
                if(success) {
                	if(task.equals("task")) {
                		listTasks.getItems().remove(lastItem);
                		if(listSubTasks.getItems().size() != 0) listSubTasks.getItems().clear();
                	}
                	else listSubTasks.getItems().remove(lastItem);
                }
            }
        });
    }
    
    public void edit(){
    	final FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/addTask.fxml"));
		Parent root = null;
		ArrayList<String> result;
		try {
			root = (Parent) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller = loader.<TaskController>getController();
		final Stage stage = new Stage();
        Scene scene = new Scene(root, 500, 650);
    	if(task.equals("task")){
    		stage.setTitle("Edit Task");
    		result = new DB().loadTask(task, lastItem, "");
    		controller.setTextTitleField(result.get(0));
    		controller.setAssignees(result.get(2));
    		controller.setPriority(result.get(8));
    		controller.setStartDate(result.get(5));
    		controller.setDueDate(result.get(6));
    		controller.setDescription(result.get(1));
    		controller.setStatus(result.get(4));
    		controller.edit(task, result.get(0));
    	}
    	else{
    		stage.setTitle("Edit subTask");
    		result = new DB().loadTask(task, listTasks.getSelectionModel().getSelectedItem().toString(), 
    				listSubTasks.getSelectionModel().getSelectedItem());
    		controller.setTextTitleField(result.get(0));
    		controller.setAssignees(result.get(2));
    		controller.setPriority(result.get(8));
    		controller.setStartDate(result.get(5));
    		controller.setDueDate(result.get(6));
    		controller.setDescription(result.get(1));
    		controller.setStatus(result.get(4));
    		controller.edit(listTasks.getSelectionModel().getSelectedItem().toString(), result.get(0));
    	}
        stage.setScene(scene);
        stage.show();
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            label.setText(item!=null ? item : "<null>");
            //only edit tasks assigned to you
            if(!new DB().getAssigneeForTask(item).contains(user.getName())) {
        		edit.setDisable(true);
        	}
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