package logic;

import java.util.ArrayList;

public class User {

	private String email;
	
	private String name;
	
	private Role role;
	
	private ArrayList<Task> tasks;
	
	public User(String name){
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
	
	public void addTask(Task t){
		this.tasks.add(t);
	}
	
	public void removeTask(Task t){
		this.tasks.remove(t);
	}
}
