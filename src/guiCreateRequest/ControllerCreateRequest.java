package guiCreateRequest;

import database.Database;
import entityClasses.Request;
import guiStaffRequests.ViewStaffRequests;
import guiUserLogin.ViewUserLogin;

/*******
 * <p> Title: ControllerCreateRequest Class. </p>
 *
 * <p> Description: The Java/FX-based Create Request Page controller. Handles button actions
 * defined by ViewCreateRequest. This page is a placeholder; actions for submitting a new
 * admin request will be added in a future phase.
 *
 * The class has been written assuming that the View or the Model are the only class methods
 * that can invoke these methods. This is why each has been declared as protected.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-27 Initial placeholder version
 *
 */

public class ControllerCreateRequest {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated. Rather, it is a collection of
	protected static methods called by the View singleton.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCreateRequest() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	
	/**********
	 * <p> Method: performCreateRequest() </p>
	 * 
	 * <p> Description: This method validates the title and body input, saves it to the database,
	 * clears the form fields, and navigates back to the Discussion Board,
	 * Displays an error message if validation fails or a database exception occurs. </p>
	 * 
	 */
	protected static void performCreateRequest() {
	    String subject = ViewCreateRequest.textfield_Subject.getText();
	    String description = ViewCreateRequest.textarea_Description.getText();
	    String errMsg;
	    
	 // Validate input using PostReplyValidator
	    errMsg = recognizers.PostReplyValidator.checkForValidRequestSubject(subject);
	    if (!errMsg.isEmpty()) {
	        ViewCreateRequest.label_ErrorMessage.setText(errMsg);
	        return;
	    }
	    errMsg = recognizers.PostReplyValidator.checkForValidRequestDescription(description);
	    if (!errMsg.isEmpty()) {
	        ViewCreateRequest.label_ErrorMessage.setText(errMsg);
	        return;
	    }
	    
	    // Create the post and save to database
	   Request request = new Request(ViewCreateRequest.theUser.getUserName(), subject, description);
	    try {
	        theDatabase.createRequest(request);
	        // Clear fields and return to Staff home on success
	        ViewCreateRequest.textfield_Subject.clear();
	        ViewCreateRequest.textarea_Description.clear();
	        ViewCreateRequest.label_ErrorMessage.setText("");
	        ViewCreateRequest.displayCreateRequest(
	        		ViewCreateRequest.theStage, ViewCreateRequest.theUser);
	    } catch (Exception e) {
	    	ViewCreateRequest.label_ErrorMessage.setText("Error creating post: " + e.getMessage());
	    }
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the current user to the View Staff Requests page. </p>
	 *
	 */
	protected static void performReturn() {
		ViewStaffRequests.displayStaffRequests(ViewCreateRequest.theStage, ViewCreateRequest.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 *
	 */
	protected static void performLogout() {
		ViewUserLogin.displayUserLogin(ViewCreateRequest.theStage);
	}

	
	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the execution of the program. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
