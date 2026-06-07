package guiSetOneTimePassword;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerSetOneTimePassword Class. </p>
 * 
 * <p> Description: The Java/FX-based Controller Set One Time Password Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Pete Echavarria © 2026 </p>
 * 
 * @author Pete Echavarria
 * 
 * @version 1.00		2026-06-02 Initial version
 */

public class ControllerSetOneTimePassword {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	
	// Message alerting user one-time password has been created
	protected static Alert alertOneTimePasswordSet = new Alert(AlertType.INFORMATION);
	// Message alerting user that no user has been selected for one-time password creation
	protected static Alert alertNoUserSelected = new Alert(AlertType.INFORMATION);
	
	public ControllerSetOneTimePassword() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	/**********
	 * <p> Method: doSelectUser() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 * 
	 */
	protected static void doSelectUser() {
		ViewSetOneTimePassword.theSelectedUser = 
				(String) ViewSetOneTimePassword.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewSetOneTimePassword.theSelectedUser);
	}
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewSetOneTimePassword.theRootPane.getChildren().clear();
		
		ViewSetOneTimePassword.theRootPane.getChildren().addAll(
				ViewSetOneTimePassword.label_PageTitle, ViewSetOneTimePassword.label_UserDetails,
				ViewSetOneTimePassword.button_UpdateThisUser, ViewSetOneTimePassword.line_Separator1,
				ViewSetOneTimePassword.label_SelectUser,
				ViewSetOneTimePassword.combobox_SelectUser, 
				ViewSetOneTimePassword.label_OneTimePassword,
				ViewSetOneTimePassword.text_OneTimePassword,
				ViewSetOneTimePassword.button_SetPassword,
				ViewSetOneTimePassword.line_Separator4, 
				ViewSetOneTimePassword.button_Return,
				ViewSetOneTimePassword.button_Logout,
				ViewSetOneTimePassword.button_Quit);
		
		// Set the title for the window
		ViewSetOneTimePassword.theStage.setTitle("CSE 360 Foundation Code: Admin Operations Page");
		ViewSetOneTimePassword.theStage.setScene(ViewSetOneTimePassword.theSetOneTimePasswordScene);
		ViewSetOneTimePassword.theStage.show();
	}
	
	
	/**********
	 * <p> Method: performSetPassword() </p>
	 * 
	 * <p> Description: This method allows an Admin manually create
	 * a one-time use password for a given user </p>
	 * 
	 */
	protected static void performSetPassword() {
		
		String password = ViewSetOneTimePassword.text_OneTimePassword.getText();
		String username = ViewSetOneTimePassword.combobox_SelectUser.getValue();
		
		// MISSING - Need to check for password validity. Waiting for @Sara Suarez to implement
		// password strength validation so those requirements can be referenced
		
		if (username == "<Select a User>") {
			alertNoUserSelected.setTitle("One-Time Password Not Created");
			alertNoUserSelected.setHeaderText("There is no user selected.");
			alertNoUserSelected.setContentText("Please select a user and try again.");
			alertNoUserSelected.showAndWait();
			ViewSetOneTimePassword.text_OneTimePassword.setText("");
			
			return;
		}
		
		theDatabase.updatePassword(username, password);
		theDatabase.updateOneTimePassword(username, "true");
		
		alertOneTimePasswordSet.setTitle("One-Time Password Has Been Created");
		alertOneTimePasswordSet.setHeaderText("One-Time password has been created for user " + username);
		alertOneTimePasswordSet.setContentText("Password: " + password);
		alertOneTimePasswordSet.showAndWait();
		ViewSetOneTimePassword.text_OneTimePassword.setText("");
		
		return;
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewSetOneTimePassword.theStage,
				ViewSetOneTimePassword.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewSetOneTimePassword.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}