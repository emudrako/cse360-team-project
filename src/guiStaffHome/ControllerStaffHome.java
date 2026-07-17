package guiStaffHome;


/*******
 * <p> Title: ControllerStaffHome Class. </p>
 *
 * <p> Description: The Java/FX-based Staff Home Page controller. Handles button actions
 * defined by ViewStaffHome. This page is a placeholder; additional actions will be
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

public class ControllerStaffHome {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated. Rather, it is a collection of
	protected static methods called by the View singleton.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerStaffHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 *
	 * <p> Description: Directs the user to the User Update Page. </p>
	 *
	 */

	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performDiscussionBoard() </p>
	 *
	 * <p> Description: Directs the staff user to the Staff Discussion Board page. </p>
	 *
	 */
	protected static void performDiscussionBoard() {
		guiStaffDiscussionBoard.ViewStaffDiscussionBoard.displayStaffDiscussionBoard(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performParameters() </p>
	 *
	 * <p> Description: Directs the staff user to the Manage Parameters page. </p>
	 *
	 */
	protected static void performParameters() {
		guiStaffParameters.ViewStaffParameters.displayStaffParameters(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performThreads() </p>
	 *
	 * <p> Description: Directs the staff user to the Manage Threads page. </p>
	 *
	 */
	protected static void performThreads() {
		guiStaffThreads.ViewStaffThreads.displayStaffThreads(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performRequests() </p>
	 *
	 * <p> Description: Directs the staff user to the View Requests page. </p>
	 *
	 */
	protected static void performRequests() {
		guiStaffRequests.ViewStaffRequests.displayStaffRequests(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performFeedback() </p>
	 *
	 * <p> Description: Directs the staff user to the Review & Feedback page. </p>
	 *
	 */
	protected static void performReview() {
		guiStaffReview.ViewStaffReview.displayStaffReview(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p> Method: performCoverage() </p>
	 *
	 * <p> Description: Directs the staff user to the Student Coverage page. </p>
	 *
	 */
	protected static void performCoverage() {
		guiStaffCoverage.ViewStaffCoverage.displayStaffCoverage(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}
	
	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
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
