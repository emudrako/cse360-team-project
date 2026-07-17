package guiStaffThreads;

import javafx.stage.Stage;
import entityClasses.User;


/*******
 * <p> Title: ControllerStaffThreads Class. </p>
 *
 * <p> Description: Stub controller for the Staff Threads screen.
 * Currently just handles navigation into this screen from Staff Home; will be
 * expanded with real event handlers as this feature is built out. </p>
 *
 * <p> Copyright: OWNER_NAME_HERE © 2026 </p>
 *
 * @author OWNER_NAME_HERE
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ControllerStaffThreads {

	/**********
	 * <p> Method: performControllerStaffThreadsStage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen
	 * (e.g., from a button on Staff Home). Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void performStaffThreads(Stage ps, User user) {
		ViewStaffThreads.displayStaffThreads(ps, user);
	}
	

	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the staff homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffThreads.theStage, ViewStaffThreads.theUser);
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