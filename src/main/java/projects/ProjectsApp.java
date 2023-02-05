/*building a menu-driven app that performs CRUD operations on a relational 
 * database that holds info on DIY projects*/

package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	/*Scanner is a Java Object that reads from a variety of sources
	*input source should be set to System.in to read from the console
	*the following is a private instance variable named scanner initialized to a new scanner object 
	*that passes System.in to the constructor*/
	private Scanner scanner = new Scanner(System.in);
	//private instance variable of type ProjectService calling the zero-argument constructor to initialize it
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	//the following is a private instance variable created that will hold a list of menu options (operations)
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			);
	// @formatter:on
	
	public static void main(String[] args) {
		//the following will call the method that processes the menu
		new ProjectsApp().processUserSelections();
	}

	/*the following instance method is used to display, get, and act upon the menu selections provided 
	 * by the user*/
	private void processUserSelections() {
		
		boolean done = false; //local variable
		//the following will loop until done == true
		while (!done) { 
			// create a try/catch block
			try {
				//create a variable that returns the value from getUserSelection()
				int selection = getUserSelection();
				//the following switch statement will process the user's selection
				switch (selection) {
				//if the input == -1
				case -1:
					done = exitMenu();
					break;
				//if the input == 1
				case 1:
					//call createProject() to collect project details and save them in project table
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
					
				//if input is neither -1 or 1
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} 
			//catch block to catch an exception
			catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}

	private void deleteProject() {
		listProjects ();
		
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		
		projectService.deleteProject(projectId);
		System.out.println("Project" + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
		curProject = null;
		}
	}

	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
			}
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		
		BigDecimal actualHours = getDecimalInput("Enter the actual hours + [" + curProject.getActualHours() + "]");
		
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
			
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
	}

	private void listProjects() {
		//the following will fetch all projects and print them out
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		//printing project ID and name for each project
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
		
		
	}

	//private method to gather project details from the user and put into a Project object
	//allowing another method to be called to save project details
	private void createProject() {
		//local variables
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
	
		Project project = new Project();
		//call setters on the Project object
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours); 
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		//call addProject() on the projectService object
		//pass it the Project object
		//assign it to variable dbProject
		Project dbProject = projectService.addProject(project);
		//print a success message
		System.out.println("You have successfully created project: " + dbProject);
		/*The value returned from projectService.addProject() is different from the 
		* Project object passed to the method. It contains the project ID that was added by MySQL.*/
		curProject = projectService.fetchProjectById(dbProject.getProjectId());
	}
	
	
	private BigDecimal getDecimalInput(String prompt) {
		 String input = getStringInput (prompt);
	 
		 if(Objects.isNull(input)) {
			 return null;
		 }
		 try {
			 //create new BigDecimal object & set number of decimal places to 2
			 return new BigDecimal(input).setScale(2);
		 }
		 catch (NumberFormatException e) {
			 throw new DbException(input + "is not a valid decimal number.");
		 }
	 }

	private boolean exitMenu() {
		 System.out.println("Exiting the menu.");
		 return true;
	}
	//The following method will print the operations and then accept user input as an int
	private int getUserSelection() {
		//call printOperations();
		printOperations();
		/*call getIntInput() and assign it to and input variable of type Integer
		the value may be null
		*Pass the following String literal "Enter a menu selection" as a parameter*/
		Integer input = getIntInput("Enter a menu selection");
		/*return statement that checks if value in local variable is null
		*if null return -1 (this will signal the app to exit
		*otherwise return input value*/
		return Objects.isNull(input) ? -1 : input;
	}
	/*the safest way to get an input line from the user is to input it as a String and then convert it 
	 * to the appropriate type. With this design, all the input methods will ultimately call the 
	 * String input method, which actually prints the prompt and uses the Scanner to get the user's input
	 * the following method accepts input from the user and converts it to an Integer (which could be null)*/
	private Integer getIntInput(String prompt) {
		/*get user input by calling getStringInput() method 
		* and assign it to a local variable named input of type String*/
		String input = getStringInput(prompt);
		//testing the value of input to see if it is null
		if (Objects.isNull(input)) {
			return null;
		}
		/*create a try/catch block to test that the value returned by getStringInput() 
		 * can be converted to an Integer*/
		try {
			/*the try block will convert the value in input (which is String if it 
			 * entered into the try block) to an Integer and return it*/
			return Integer.valueOf(input);
		} 
		//if conversion is not possible the NumberFormatException is thrown
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}
	//following method prints the prompt & gets input from user
	//the other input methods call this method & convert the input value to appropriate type
	//also called by methods that need to collect String data from the user
	private String getStringInput(String prompt) {
		//the following print statement will keep the cursor on the same line as the prompt
		//NOTE PRINT AND NOT PRINLN!!!
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		//the following will test the value of input
		//if it's blank it will return null
		//otherwise it will return a trimmed value
		return input.isBlank() ? null : input.trim();
	}
	//following method prints each available selection on a separate line in the console
	private void printOperations () {
	System.out.println("\nThese are the available selections. Press the Enter key to quit:");
	//the following is a Lambda expression used to menu selections (one on each line)
	operations.forEach(line -> System.out.println("  " + line));
	
	if(Objects.isNull(curProject)) {
		System.out.println("\nYou are not working with a project.");
	}
	else {
		System.out.println("\nYou are working with a project: " + curProject);
		}
	}
	/*Every List object must implement the forEach() method. 
	 * forEach() takes a Consumer interface object as a parameter. 
	 * Consumer has a single abstract method, accept(). 
	 * The accept() method takes a single parameter and returns nothing. 
	 * The Lambda expression has a single parameter and System.out.println returns nothing. 
	 * The Lambda expression thus matches the requirements for the accept() method.
	 * If you don't want to use a Lambda expression, you can use an enhanced for loop to print 
	 * the instructions.*/
}
