package guiInstructorHome;


/*******
 * <p> Title: ControllerInstructorHome Class. </p>
 *
 * <p> Description: The Java/FX-based Instructor Home Page controller. Handles button actions
 * defined by ViewInstructorHome. This page is a placeholder; additional actions will be
 * added in a future phase.
 *
 * The class has been written assuming that the View or the Model are the only class methods
 * that can invoke these methods. This is why each has been declared as protected.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-01 Initial version
 *
 */

public class ControllerInstructorHome {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated. Rather, it is a collection of
	protected static methods called by the View singleton.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerInstructorHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 *
	 * <p> Description: Directs the user to the User Update Page. </p>
	 *
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewInstructorHome.theStage, ViewInstructorHome.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewInstructorHome.theStage);
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
