package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

//DB class for all related activities
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
		
		//connect to mysql
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
				displayErrorDB();
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
				displayErrorDB();
			}
			return task;
		}
		
		//delete all tasks and its subtasks
		public boolean deleteTask(String task){
			getConnection();
			boolean success = false;
			try {
				//avoid sql injection
				st = conn.prepareStatement("DELETE FROM TASK WHERE title = ? OR mainTask = ?");
				st.setString(1, task);
				st.setString(2, task);
				st.executeUpdate();
				System.out.println("Query executed");
				success = true;
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
				success = false;
				displayErrorDB();
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
			return success;
		}
		
		public void displayErrorDB(){
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Error in Database");
        	alert.setContentText("Could not execute query");
        	alert.showAndWait();
		}

		//get all usernamaes
		public ArrayList<String> getUsersName() {
			// TODO Auto-generated method stub
			getConnection();
			ArrayList<String> names = new ArrayList<String>();
			try {
				//avoid sql injection
				st = conn.prepareStatement("SELECT * FROM USER");
				rs = st.executeQuery();

				while (rs.next()) {
					//get all display name of a user
					names.add(rs.getString("displayName"));
				}
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
				displayErrorDB();
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
			return names;
		}
}
