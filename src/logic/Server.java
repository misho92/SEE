package logic;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Hello {

	public Server() {
	}

	public String connection() {
		return "You are connected to the server!";
	}

	public static void main(String args[]) {
		
		try {
			Server obj = new Server();
			Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);
			//Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.createRegistry(2016);
			// Registry registry = LocateRegistry.getRegistry();
			registry.bind("Hello", stub);
			System.err.println("Server up and running");
			/*while(true){
				stub = (Hello) registry.lookup("Hello");
				stub.sayHello();
			}*/
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}