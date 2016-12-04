package logic;

import java.sql.Connection;
import java.sql.Date;
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
		static final String DB_URL = "jdbc:mysql://localhost/see?allowMultiQueries=true";

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
				displayErrorDB();
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
						res.getString("description"), res.getInt("priority"));
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

		public void addTask(String title, String description, String assignee, String mainTask, String status,
				Date start, Date end, String issueName, String priority) {
			getConnection();
			try {
				//avoid sql injection
				st = conn.prepareStatement("INSERT INTO TASK VALUES(?,?,?,?,?,?,?,?,?)");
				st.setString(1, title);
				st.setString(2, description);
				st.setString(3, assignee);
				st.setString(4, mainTask);
				st.setString(5, status);
				st.setDate(6, start);
				st.setDate(7, end);
				st.setString(8, issueName);
				st.setString(9, priority);
				st.executeUpdate();
				System.out.println("Query executed");
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
		}

		public ArrayList<String> loadTask(String task, String item, String title) {
			// TODO Auto-generated method stub
			getConnection();
			ArrayList<String> details = new ArrayList<String>();
			try {
				if(task.equals("task")){
					st = conn.prepareStatement("SELECT * FROM TASK WHERE title = ?");
					st.setString(1, item);
				}
				else{
					st = conn.prepareStatement("SELECT * FROM TASK WHERE mainTask = ? AND title = ?");
					st.setString(1, item);
					st.setString(2, title);
				}
				// avoid sql injection
				
				rs = st.executeQuery();
		
				while (rs.next()) {
					// get all display name of a user
					details.add(rs.getString("title"));
					details.add(rs.getString("description"));
					details.add(rs.getString("assignee"));
					details.add(rs.getString("mainTask"));
					details.add(rs.getString("status"));
					details.add(rs.getDate("startDate").toString());
					details.add(rs.getDate("dueDate").toString());
					details.add(rs.getString("issueName"));
					details.add(rs.getString("priority"));
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
			} finally {
				// finally block used to close resources
				try {
					if (st != null)
						st.close();
				} catch (SQLException se2) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			return details;
		}

		public void editTask(String title, String description, String assignee, String mainTask, String status,
				Date start, Date end, String issueName, String priority, String oldValue) {
			getConnection();
			try {
				//edit subtask
				if(mainTask != null){
					st = conn.prepareStatement("UPDATE TASK SET title = ?, description = ?, assignee = ?, mainTask = ?, "
							+ "status = ?, startDate = ?, dueDate = ?, priority = ? WHERE title = ?");
					st.setString(1, title);
					st.setString(2, description);
					st.setString(3, assignee);
					st.setString(4, mainTask);
					st.setString(5, status);
					st.setDate(6, start);
					st.setDate(7, end);
					st.setString(8, priority);
					st.setString(9, oldValue);
				}
				else{
					//edit task
					st = conn.prepareStatement("UPDATE TASK SET title = ?, description = ?, assignee = ?, mainTask = null, "
							+ "status = ?, startDate = ?, dueDate = ?, priority = ? WHERE title = ?; UPDATE TASK SET "
							+ "mainTask = ? WHERE mainTask = ?");
					st.setString(1, title);
					st.setString(2, description);
					st.setString(3, assignee);
					st.setString(4, status);
					st.setDate(5, start);
					st.setDate(6, end);
					st.setString(7, priority);
					st.setString(8, oldValue);
					st.setString(9, title);
					st.setString(10, oldValue);
				}
				st.executeUpdate();
				System.out.println("Query executed");
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
		}

		public User loadUser(String email) {
			getConnection();
			User user = new User();
			ArrayList<logic.Task> tasks = new ArrayList<logic.Task>();
			try {
				st = conn.prepareStatement("SELECT * FROM USER WHERE EMAIL = ?");
				// avoid sql injection
				st.setString(1, email);
				rs = st.executeQuery();
		
				while (rs.next()) {
					user.setEmail(email);
					user.setName(rs.getString("displayName"));
					user.setRole(new Role(rs.getString("role")));
				}
				
				st = conn.prepareStatement("SELECT * FROM TASK WHERE assignee = ?");
				st.setString(1, user.getName());
				rs = st.executeQuery();
				while (rs.next()) {
					tasks.add(new Task(rs.getString("title"), user, new Status(rs.getString("status")), 
							rs.getDate("startDate"), rs.getDate("dueDate"), rs.getString("description"), 
							rs.getInt("priority")));
				}
				user.setTasks(tasks);
				System.out.println("Query executed");
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Query failed to execute");
				displayErrorDB();
			} finally {
				// finally block used to close resources
				try {
					if (st != null)
						st.close();
				} catch (SQLException se2) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			return user;
		}

		public String getAssigneeForTask(String task) {
			getConnection();
			String name = null;
			try {
				st = conn.prepareStatement("SELECT * FROM TASK WHERE title = ?");
				// avoid sql injection
				st.setString(1, task);
				rs = st.executeQuery();
				while (rs.next()) {
					name = rs.getString("assignee");
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
			} finally {
				// finally block used to close resources
				try {
					if (st != null)
						st.close();
				} catch (SQLException se2) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			return name;
		}
}
