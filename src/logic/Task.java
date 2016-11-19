package logic;

import java.util.ArrayList;
import java.util.Date;

import logic.User;
import logic.Status;

public class Task {

	private String title;
	
	private User assignee;
	
	private Status status;
	
	private Date startDate;
	
	private Date dueDate;
	
	private String description;
	
	private ArrayList<Task> subTasks;
	
	private int priority;
	
	public Task(String title, User assignee, Status status, Date startDate, Date dueDate, String description, 
			int priority){
		this.title = title;
		this.assignee = assignee;
		this.status = status;
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.description = description;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Task> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(ArrayList<Task> subTasks) {
		this.subTasks = subTasks;
	}
	
	public void addSubTask(Task t){
		this.subTasks.add(t);
	}
	
	public void removeSubTask(Task t){
		this.subTasks.remove(t);
	}
	
}
