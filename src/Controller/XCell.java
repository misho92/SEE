package Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import logic.DB;

//custom listview cell/row
class XCell extends ListCell<String> {
    HBox hbox = new HBox();
    Label label = new Label("(empty)");
    Pane pane = new Pane();
    Button button = new Button("X");
    String lastItem;
    ListView<String> listTasks;
    ListView<String> listSubTasks;
    boolean success = false;
    String task;

    public XCell(ListView<String> listTasks, ListView<String> listSubTasks, String task) {
        super();
        this.listTasks = listTasks;
        this.listSubTasks = listSubTasks;
        this.task = task;
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
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