package guiStaffDiscussionBoard;

import javafx.stage.Stage;
import entityClasses.User;


/*******
 * <p> Title: CONTROLLER_NAME_HERE Class. </p>
 *
 * <p> Description: Stub controller for the SCREEN_DESCRIPTION_HERE screen.
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
public class ControllerStaffDiscussionBoard {

	/**********
	 * <p> Method: performMETHOD_NAME_HERE(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen
	 * (e.g., from a button on Staff Home). Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void performStaffDiscussionBoard(Stage ps, User user) {
		ViewStaffDiscussionBoard.displayStaffDiscussionBoard(ps, user);
	}
}