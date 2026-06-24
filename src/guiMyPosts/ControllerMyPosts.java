package guiMyPosts;

import database.Database;

/*******
 * <p> Title: ControllerMyPosts Class. </p>
 * 
 * <p> Description: The Java/FX-based My Posts page.  This class provides the controller
 * actions for the My Posts page including ability for a student to view a list of all posts they have made, search through posts, and toggle
 * between read and unread replies.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * @version 1.00		2026-06-22 Initial version
 */

public class ControllerMyPosts {
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerMyPosts() {}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	/**********
	 * <p> Method: loadMyPosts() </p>
	 * 
	 * <p> Description:  </p>
	 * 
	 */
	protected static void loadMyPosts() {
	    // TODO: load posts for the current user from the database
	}
	
	
	/**********
	 * <p> Method: performToggleUnread() </p>
	 * 
	 * <p> Description: </p>
	 * 
	 */
	protected static void performToggleUnread() {
	    // TODO: toggle between all replies and unread-only replies
	}
	
	
	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: </p>
	 * 
	 */
	protected static void performSearch() {
	    // TODO: search
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the user's homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiStudentHome.ViewStudentHome.displayStudentHome(ViewMyPosts.theStage, ViewMyPosts.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page. </p>
	 * 
	 */
	protected static void performLogout() {
	    guiUserLogin.ViewUserLogin.displayUserLogin(ViewMyPosts.theStage);
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
