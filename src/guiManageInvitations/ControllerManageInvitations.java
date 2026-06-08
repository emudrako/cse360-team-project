package guiManageInvitations;

import database.Database;

/*******
 * <p> Title: ControllerManageInvitations Class. </p>
 * 
 * <p> Description: The Java/FX-based Manage Invitations page.  This class provides the controller
 * actions for the Manage Invitations page, including loading the invitation list, deleting a selected invitation and 
 * returning the user to the Admin home page.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * @version 1.00		2026-06-07 Initial version
 */


public class ControllerManageInvitations{
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerManageInvitations() {}
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	/**********
	 * <p> Method: performDelete() </p>
	 * 
	 * <p> Description: This method will delete the invitation code and attributes at the Admin's request </p>
	 * 
	 */
	protected static void performDelete(){
		String selectedItem = ViewManageInvitations.listview_Invitations.getSelectionModel().getSelectedItem(); // Get the selected invitation from list
		if (selectedItem == null) return; // if there was nothing selected do nothing
		
		// Extract the invitation code from the selected item and remove it from the database
		String code = selectedItem.split(" \\| ")[0];
		theDatabase.removeInvitationAfterUse(code);
		
		ViewManageInvitations.displayManageInvitations(ViewManageInvitations.theStage, ViewManageInvitations.theUser);// Refresh the list
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewManageInvitations.theStage,
				ViewManageInvitations.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageInvitations.theStage);
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