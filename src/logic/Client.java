package logic;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Client extends Application implements Hello{

	//private Client() {}

	@Override
	  public void start(Stage stage) throws IOException {
	    AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/GUI/Start.fxml"));
        Scene scene = new Scene(root, 1500, 800);
        stage.setTitle("Tesma collaboration development tool");
        stage.setScene(scene);
        stage.show();
	    
	  }
	
	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.getRegistry(2016);
			Hello stub = (Hello) registry.lookup("Hello");
			String response = stub.connection();
			System.out.println("Server: " + response);
			launch(args);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
		
	}

	@Override
	public String connection() throws RemoteException {
		// TODO Auto-generated method stub
		return "Hi from Client";
	}
}