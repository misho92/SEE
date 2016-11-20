package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DB {
	
	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost/see";

		// Database credentials
		static final String USER = "root";
		static final String PASS = "root";

		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String username;
		String pass;
		
		public Connection getConnection() {

			try {
				Class.forName(JDBC_DRIVER);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Connecting to database...");
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Connected to database");
			return conn;
		}
		
		public boolean login(String username, String pass) {
			try {
				//avoid sql injection
				st = conn.prepareStatement("SELECT * FROM SEE.user WHERE username = ? AND pass = ?");
				st.setString(1, username);
				st.setString(2, pass);

				rs = st.executeQuery();
				
				if (rs.next()) {
					//name = "Welcome " + rs.getString("username");
				    return true;
				}
				
				/*Extract data from result set
				while (rs.next()) {
					// Retrieve by column name
					name = "Welcome " + rs.getString("username");
				}*/
				
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
			}
			finally{
			      //finally block used to close resources
			      try{
			         if(st!=null)
			            st.close();
			      }catch(SQLException se2){}
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			}
			return false;
		}
		
		public boolean register(String username, String pass){
			try {
				st = conn.prepareStatement("INSERT INTO see.user VALUES(?,?)");
				st.setString(1, username);
				st.setString(2, pass);
				int rows = st.executeUpdate();
				
				if (rows != 0) {
				    return true;
				}
				
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
				Alert alert = new Alert(AlertType.ERROR);
	        	alert.setTitle("Error Dialog");
	        	alert.setHeaderText("Something went wrong");
	        	alert.setContentText("Registration failed");
	        	alert.showAndWait();
			}
			finally{
			      //finally block used to close resources
			      try{
			         if(st!=null)
			            st.close();
			      }catch(SQLException se2){}
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			}
			return false;
		}

		//get all tasks and subtasks for a particular issue
		public ArrayList<Task> getTasksForIssue(String issue){
			ArrayList<Task> tasks = new ArrayList<Task>();
			getConnection();
			try {
				//avoid sql injection
				st = conn.prepareStatement("SELECT * FROM TASK WHERE issueName = ? AND mainTask IS NULL");
				st.setString(1, issue);
				rs = st.executeQuery();

				while (rs.next()) {
					//get all subtasks of a task
					Task t = new Task(rs.getString("title"), new User(rs.getString("assignee")), 
						new Status(rs.getString("status")), rs.getDate("startDate"), rs.getDate("dueDate"), 
						rs.getString("description"), rs.getInt("priority"));
						getSubTasks(t);
						tasks.add(t);
				}
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
			}
			finally{
			      //finally block used to close resources
			      try{
			         if(st!=null)
			            st.close();
			      }catch(SQLException se2){}
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			}
			return tasks;
		}
		
		public Task getSubTasks(Task task){
			ArrayList<Task> tasks = new ArrayList<Task>();
			getConnection();
			try {
				//avoid sql injection
				ResultSet res = null;
				st = conn.prepareStatement("SELECT * FROM TASK WHERE mainTask = ?");
				st.setString(1, task.getTitle());
				res = st.executeQuery();

				while (res.next()) {
					//get all subtasks of a task
					Task t = new Task(res.getString("title"), new User(res.getString("assignee")), 
						new Status(res.getString("status")), res.getDate("startDate"), res.getDate("dueDate"), 
						res.getString("description"), rs.getInt("priority"));
						tasks.add(t);
				}
				task.setSubTasks(tasks);
				System.out.println("Query executed");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
			}
			return task;
		}
}
