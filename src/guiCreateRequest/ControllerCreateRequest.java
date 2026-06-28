package guiCreateRequest;

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

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewCreateRequest.theStage);
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
