package guiNewAccount;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import recognizers.UserNameRecognizer;

/*******
 * <p> Title: ControllerNewAccount Class. </p>
 * 
 * <p> Description: The Java/FX-based New Account Page.  This class provides the controller actions
 * to allow the user to establish a new account after responding to an invitation and the use of a
 * one time code.
 * 
 * The controller deals with the user pressing the "User Step" button widget being click.  If also
 * supports the user click on the "Quit" button widget.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author Sara Suarez
 * @version 1.01 2026-06-06 Added max length check and real-time validation
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerNewAccount {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerNewAccount() {
	}
	
	/**********
	 * <p> Method: setUsername() </p>
	 *
	 * <p> Description: Called when the user types in the username field.
	 * Checks maximum length first (Story 3), then validates using the
	 * UserNameRecognizer FSM and displays real-time feedback (Story 2).</p>
	 *
	 * @author Sara Suarez
	 * @version 1.01 2026-06-06 Added max length check and real-time validation
	 */
	protected static void setUsername() {
	    String username = ViewNewAccount.text_Username.getText();
	    // Check max length first (Story 3)
	    if (username.length() > 16) {
	        ViewNewAccount.label_ValidationMessage.setText(
	            "Username is too long. Maximum is 16 characters.");
	        return;
	    }
	    // Validate with FSM in real time
	    String errMsg = UserNameRecognizer.checkForValidUserName(username);
	    if (!errMsg.isEmpty())
	        ViewNewAccount.label_ValidationMessage.setText(errMsg);
	    else
	        ViewNewAccount.label_ValidationMessage.setText("");
	}

	/**********
	 * <p> Method: setPassword1() </p>
	 *
	 * <p> Description: Called when the user types in the password field.
	 * Checks maximum length first (Story 3), then evaluates password
	 * strength in real time using the PasswordEvaluator (Story 2).</p>
	 *
	 * @author Sara Suarez
	 * @version 1.01 2026-06-06 Added max length check and real-time validation
	 */
	protected static void setPassword1() {
	    String password = ViewNewAccount.text_Password1.getText();
	    // Check max length first (Story 3)
	    if (password.length() > recognizers.PasswordEvaluator.MAX_PASSWORD_LENGTH) {
	        ViewNewAccount.label_ValidationMessage.setText(
	            "Password is too long. Maximum is 24 characters.");
	        return;
	    }
	    // Evaluate strength in real time (Story 2)
	    String result = recognizers.PasswordEvaluator.evaluatePassword(password);
	    if (!result.isEmpty())
	        ViewNewAccount.label_ValidationMessage.setText(result);
	    else
	        ViewNewAccount.label_ValidationMessage.setText("Password looks good!");
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**********
	 * <p> Method: public doCreateUser() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the User Setup
	 * button.  This method checks the input fields to see that they are valid.  If so, it then
	 * creates the account by adding information to the database.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doCreateUser() {
		
		// Fetch the username and password. (We use the first of the two here, but we will validate
		// that the two password fields are the same before we do anything with it.)
		String username = ViewNewAccount.text_Username.getText();
		String password = ViewNewAccount.text_Password1.getText();
		
		// Display key information to the log
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		// Initialize local variables that will be created during this process
		int roleCode = 0;
		User user = null;

		// Validate the username before proceeding
		String errMsg = UserNameRecognizer.checkForValidUserName(username);
		if (!errMsg.isEmpty()) {
			ViewNewAccount.alertUsernamePasswordError.setTitle("Invalid Username");
			ViewNewAccount.alertUsernamePasswordError.setHeaderText("The username is not valid.");
			ViewNewAccount.alertUsernamePasswordError.setContentText(
				errMsg.replace("\n*** ERROR *** ", "").trim());
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
			return;
		}

		// Make sure the two passwords are the same.
		if (ViewNewAccount.text_Password1.getText().
				compareTo(ViewNewAccount.text_Password2.getText()) == 0) {
			
			// The passwords match so we will set up the role and the User object base on the 
			// information provided in the invitation
			if (ViewNewAccount.theRole.compareTo("Admin") == 0) {
				roleCode = 1;
				user = new User(username, password, "", "", "", "", "", true, false, false);
			} else if (ViewNewAccount.theRole.compareTo("Role1") == 0) {
				roleCode = 2;
				user = new User(username, password, "", "", "", "", "", false, true, false);
			} else if (ViewNewAccount.theRole.compareTo("Role2") == 0) {
				roleCode = 3;
				user = new User(username, password, "", "", "", "", "", false, false, true);
			} else if (ViewNewAccount.theRole.compareTo("Student") == 0) {
				roleCode = 4;
				user = new User(username, password, "", "", "", "", "", false, false, false);
				user.setStudentRole(true);
			} else if (ViewNewAccount.theRole.compareTo("Instructor") == 0) {
				roleCode = 5;
				user = new User(username, password, "", "", "", "", "", false, false, false);
				user.setInstructorRole(true);
			} else if (ViewNewAccount.theRole.compareTo("Staff") == 0) {
				roleCode = 6;
				user = new User(username, password, "", "", "", "", "", false, false, false);
				user.setStaffRole(true);
			} else {
				System.out.println(
						"**** Trying to create a New Account for a role that does not exist!");
				System.exit(0);
			}
			
			// Unlike the FirstAdmin, we know the email address, so set that into the user as well.
        	user.setEmailAddress(ViewNewAccount.emailAddress);

        	// Inform the system about which role will be played
			applicationMain.FoundationsMain.activeHomePage = roleCode;
			
        	// Create the account based on user and proceed to the user account update page
            try {
            	// Create a new User object with the pre-set role and register in the database
            	theDatabase.register(user);
            } catch (SQLException e) {
                System.err.println("*** ERROR *** Database error: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // The account has been set, so remove the invitation from the system
            theDatabase.removeInvitationAfterUse(
            		ViewNewAccount.text_Invitation.getText());
            
            // Set the database so it has this user and the current user
            theDatabase.getUserAccountDetails(username);

            // Navigate to the Welcome Login Page
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}
