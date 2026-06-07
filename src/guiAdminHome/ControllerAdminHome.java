package guiAdminHome;

import database.Database;
import javafx.scene.layout.Region;

import java.time.LocalDateTime; // Import LocalDateTime for invitation code expiration functionality
import java.time.format.FormatStyle; // Import for invitation code format in alert
import java.time.format.DateTimeFormatter; // Import for invitation code format in alert
/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Automatically set the invitation expiry to 1 hour from current date and time.
		LocalDateTime deadline = LocalDateTime.now().plusHours(1); 
		
		//Format the deadline for readability
		String formattedDeadline = deadline.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
		
		// Inform the user that the invitation has been sent and display the invitation code, email address and expiration
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole, deadline);
		
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress + "\nInvitation code expires: " + formattedDeadline;
		
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		// Resize the alert message to display full details
		ViewAdminHome.alertEmailSent.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		ViewAdminHome.alertEmailSent.getDialogPane().setMinWidth(400);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		// Implementing setOnetimePassword function
		guiSetOneTimePassword.ViewSetOneTimePassword.displaySetOneTimePassword(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		System.out.println("\n*** WARNING ***: Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Delete User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		System.out.println("\n*** WARNING ***: List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("List User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		// First checks if emailAddress is empty before checking any other requirements
		if (emailAddress.isEmpty()) {
			ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Email address is empty.");
			ViewAdminHome.alertEmailError.setContentText(
					"Please enter a valid email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			ViewAdminHome.text_InvitationEmailAddress.setText("");
			return true;
		}
		
		// Make a local copy of the emailAddress for validation
		String email = emailAddress;
		// Keeps track of the current FSM state
		int state = 0;
		// Sets next state depending on current input
		int nextState = -1;
		// Keeps track of the current char index
		int currentCharIndex = 0;
		// Keeps track of the current char
		char currentChar = email.charAt(0);
		// While true, email validation continues
		boolean running = true;
		// Returns true if the email address meets all requirements, false if otherwise
		boolean invalidEmail = false;
		
		if (email.isEmpty()) {
			ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Email address is empty.");
			ViewAdminHome.alertEmailError.setContentText(
					"Please enter a valid email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			ViewAdminHome.text_InvitationEmailAddress.setText("");
			return true;
		}
		if (email.length() <5) {
			ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
			ViewAdminHome.alertEmailError.setContentText(
					"Please enter an email address with the proper format.");
			ViewAdminHome.alertEmailError.showAndWait();
			ViewAdminHome.text_InvitationEmailAddress.setText("");
			return true;
		}
		
		if (email.length() > 64) {
			ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Email address is too long.");
			ViewAdminHome.alertEmailError.setContentText(
					"Please enter an email address less than 65 characters long.");
			ViewAdminHome.alertEmailError.showAndWait();
			ViewAdminHome.text_InvitationEmailAddress.setText("");
			return true;
		}
		
		while (running) {
			currentChar = email.charAt(currentCharIndex);
			
			switch (state) {
			case 0:
				if(currentChar == '.') {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
					ViewAdminHome.alertEmailError.setContentText(
							"Email address cannot start with a period.");					
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				else if (currentChar >= 'A' && currentChar <= 'Z' ||
						currentChar >= 'a' && currentChar <= 'z' ||
						currentChar >= '0' && currentChar <= '9' ||
						currentChar == '-' ||
						currentChar == '_') {
					currentCharIndex++;
					nextState = 1;
				}
				else {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
					ViewAdminHome.alertEmailError.setContentText(
							"Please enter an email address with the proper format.");
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				break;
			
			case 1:
				/**
				if (currentCharIndex == email.length()-1 && currentChar != '@') {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
					ViewAdminHome.alertEmailError.setContentText(
							"Please enter an email address with the proper format.");
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				*/
				if (currentChar >= 'A' && currentChar <= 'Z' ||
				currentChar >= 'a' && currentChar <= 'z' ||
				currentChar >= '0' && currentChar <= '9' ||
				currentChar == '-' ||
				currentChar == '_' ||
				currentChar == '.') {
					currentCharIndex++;
					nextState = 1;
				}
				else if (currentChar == '@') {
					if (currentCharIndex == email.length()-1) {
							ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
							ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
							ViewAdminHome.alertEmailError.setContentText(
									"Please enter an email address with the proper format.");
							ViewAdminHome.alertEmailError.showAndWait();
							ViewAdminHome.text_InvitationEmailAddress.setText("");
							invalidEmail = true;
							running = false;
					}
					else {
						currentCharIndex++;
						nextState = 2;
					}
				}
				else {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText("Email address contains an invalid character or a space.");
					ViewAdminHome.alertEmailError.setContentText(
							"Allowed characters: A-Z  a-z  0-9  .  -  _");
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				break;
			
			case 2:
				if (currentChar >= 'A' && currentChar <= 'Z' ||
				currentChar >= 'a' && currentChar <= 'z' ||
				currentChar >= '0' && currentChar <= '9' ||
				currentChar == '-' ||
				currentChar == '_') {
					if (currentCharIndex == email.length()-1) {
						ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
						ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
						ViewAdminHome.alertEmailError.setContentText(
								"Please enter an email address with the proper format.");
						ViewAdminHome.alertEmailError.showAndWait();
						ViewAdminHome.text_InvitationEmailAddress.setText("");
						invalidEmail = true;
						running = false;
					}
					else {
					currentCharIndex++;
					nextState = 2;
					}
				}
				else if (currentChar == '.') {
					if (currentCharIndex == email.length()-1) {
						ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
						ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
						ViewAdminHome.alertEmailError.setContentText(
								"Email address cannot end with a period");
						ViewAdminHome.alertEmailError.showAndWait();
						ViewAdminHome.text_InvitationEmailAddress.setText("");
						invalidEmail = true;
						running = false;
					}
					else {
						currentCharIndex++;
						nextState = 3;
					}
				}
				else {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
					ViewAdminHome.alertEmailError.setContentText(
							"Please enter an email address with the proper format.");
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				break;
				
			case 3:
				if (currentChar >= 'A' && currentChar <= 'Z' ||
				currentChar >= 'a' && currentChar <= 'z' ||
				currentChar >= '0' && currentChar <= '9' ||
				currentChar == '-' ||
				currentChar == '_')	{
					currentCharIndex++;
					nextState = 3;
				}
				else if (currentChar == '.') {
					if (currentCharIndex == email.length()-1) {
						ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
						ViewAdminHome.alertEmailError.setHeaderText("Invalid email address format.");
						ViewAdminHome.alertEmailError.setContentText(
								"Email address cannot end with a period");
						ViewAdminHome.alertEmailError.showAndWait();
						ViewAdminHome.text_InvitationEmailAddress.setText("");
						invalidEmail = true;
						running = false;
					}
					else {
						currentCharIndex++;
						nextState = 3;
					}
				}
				else {
					ViewAdminHome.alertEmailError.setTitle("Invalid Email Address");
					ViewAdminHome.alertEmailError.setHeaderText(
							"Email address contains an invalid character or a space.");
					ViewAdminHome.alertEmailError.setContentText(
							"Allowed characters: A-Z  a-z  0-9  .  -  _");
					ViewAdminHome.alertEmailError.showAndWait();
					ViewAdminHome.text_InvitationEmailAddress.setText("");
					invalidEmail = true;
					running = false;
				}
				break;
			}
			
			if (currentCharIndex == email.length()) {
				running = false;
			}
			state = nextState;
		}
		
		return invalidEmail;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
