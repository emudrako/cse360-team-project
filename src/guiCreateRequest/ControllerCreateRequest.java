package guiCreateRequest;

import database.Database;
import entityClasses.Request;
import guiStaffRequests.ControllerStaffRequests;
import guiStaffRequests.ViewStaffRequests;
import guiUserLogin.ViewUserLogin;

/*******
 * <p> Title: ControllerCreateRequest Class. </p>
 *
 * <p> Description: The Java/FX-based Create Request Page controller. Handles button actions
 * defined by ViewCreateRequest. Allows Staff to created a request for an Admin to perform
 * admin-specific actions.
 *
 * The class has been written assuming that the View or the Model are the only class methods
 * that can invoke these methods. This is why each has been declared as protected.</p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-17 Initial Version
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
	 * <p> Description: This method validates the subject and description input of the request,
	 * saves it to the database, clears the form fields, and navigates back to the Staff Requests
	 * page. Displays an error message if validation fails or a database exception occurs. </p>
	 * 
	 */
	protected static void performCreateRequest(String requestorUsername, String subject, String description) {
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
	    
	   // Create the request and save to database
	   Request request = new Request(requestorUsername, subject, description);
	    try {
	        theDatabase.createRequest(request);
	    } catch (Exception e) {
	    	ViewCreateRequest.label_ErrorMessage.setText("Error creating request: " + e.getMessage());
	    }
	    ControllerStaffRequests.allRequests.addRequest(request);
	    ViewCreateRequest.displayCreateRequest(
        		ViewCreateRequest.theStage, ViewCreateRequest.theUser);
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
