package guiStaffThreads;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entityClasses.Thread;
import entityClasses.User;
import javafx.scene.control.Label;


/*******
 * <p> Title: ControllerStaffThreads Class. </p>
 *
 * <p> Description: Controller for the Staff Thread CRUD screen. Handles navigation,
 *  creation, reading, updating, and deletion of discussion threads. The "General"
 *  thread is protected at the database layer — update and delete calls on it throw
 *  an IllegalArgumentException that is caught here and surfaced to the user. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-07-25 Initial version
 *
 */
public class ControllerStaffThreads {

	/**********
	 * <p> Method: performStaffThreads(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen.
	 * Displays the corresponding View. </p>
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
	 * <p> Method: performReadAllThreads() </p>
	 *
	 * <p> Description: Retrieves and returns the list of all threads from the database.
	 * On failure, sets the error message label and returns an empty list. </p>
	 *
	 * @return the list of all Thread objects, or an empty list if the database call fails
	 *
	 */
	public static List<Thread> performReadAllThreads() {
		try {
			return ViewStaffThreads.theDatabase.readAllThreads();
		} catch (Exception e) {
			ViewStaffThreads.label_ErrorMessage.setText("*** ERROR *** Could not load threads.");
			return new ArrayList<>();
		}
	}


	/**********
	 * <p> Method: performCreateThread(String name, String description) </p>
	 *
	 * <p> Description: Creates a new Thread and persists it to the database.
	 * Validation is enforced by the database layer (name and description checks,
	 * duplicate name check). Returns true on success, false on any failure,
	 * and always sets label_ErrorMessage with the result. </p>
	 *
	 * @param name specifies the thread name; must be non-empty and not already taken
	 *
	 * @param description specifies the thread description; must be non-empty
	 *
	 * @return true if the thread was created successfully, false otherwise
	 *
	 */
	public static boolean performCreateThread(String name, String description) {
		try {
			Thread newThread = new Thread(name, description, ViewStaffThreads.theUser.getUserName());
			ViewStaffThreads.theDatabase.createThread(newThread);
			ViewStaffThreads.label_ErrorMessage.setText("Thread created successfully.");
			return true;
		} catch (IllegalArgumentException e) {
			ViewStaffThreads.label_ErrorMessage.setText(e.getMessage());
			return false;
		} catch (SQLException e) {
			ViewStaffThreads.label_ErrorMessage.setText("*** ERROR *** Could not save thread to the database.");
			return false;
		}
	}


	/**********
	 * <p> Method: performUpdateThread(int threadID, String name, String description) </p>
	 *
	 * <p> Description: Updates an existing thread's name and description.
	 * The "General" thread is blocked at the database layer.
	 * Sets label_ErrorMessage with the result. </p>
	 *
	 * @param threadID specifies the ID of the thread to update
	 *
	 * @param name specifies the new thread name
	 *
	 * @param description specifies the new thread description
	 *
	 */
	public static void performUpdateThread(int threadID, String name, String description) {
		try {
			ViewStaffThreads.theDatabase.updateThread(threadID, name, description);
			ViewStaffThreads.label_ErrorMessage.setText("Thread updated successfully.");
		} catch (IllegalArgumentException e) {
			ViewStaffThreads.label_ErrorMessage.setText(e.getMessage());
		} catch (SQLException e) {
			ViewStaffThreads.label_ErrorMessage.setText("*** ERROR *** Could not update thread.");
		}
	}


	/**********
	 * <p> Method: performDeleteThread(int threadID) </p>
	 *
	 * <p> Description: Deletes a thread by ID. All posts in the deleted thread are
	 * migrated to "General" by the database layer before deletion. The "General"
	 * thread is blocked at the database layer.
	 * Sets label_ErrorMessage with the result. </p>
	 *
	 * @param threadID specifies the ID of the thread to delete
	 *
	 */
	public static void performDeleteThread(int threadID) {
		try {
			ViewStaffThreads.theDatabase.deleteThread(threadID);
			ViewStaffThreads.label_ErrorMessage.setText("Thread deleted. Its posts were moved to General.");
		} catch (IllegalArgumentException e) {
			ViewStaffThreads.label_ErrorMessage.setText(e.getMessage());
		} catch (SQLException e) {
			ViewStaffThreads.label_ErrorMessage.setText("*** ERROR *** Could not delete thread.");
		}
	}


	/**********
	 * <p> Method: createThreadCard(Thread thread) </p>
	 *
	 * <p> Description: Builds a single styled, clickable card for the given Thread.
	 * The "General" thread card uses a grey colour to signal it is protected.
	 * Clicking a card opens the detail panel for that thread. </p>
	 *
	 * @param thread specifies the Thread to build a card for
	 *
	 * @return an HBox containing the styled card
	 *
	 */
	protected static HBox createThreadCard(Thread thread) {
		HBox card = new HBox(5);
		card.setPadding(new Insets(5, 10, 5, 10));
		card.setMaxHeight(40);

		String cardColour = thread.getIsDefault() ? "#555555" : "#0062A3";
		card.setStyle(
			"-fx-background-color: " + cardColour + ";" +
			"-fx-background-radius: 8;" +
			"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
		);

		String displayName = thread.getIsDefault()
			? thread.getName() + " (protected)"
			: thread.getName();
		Label title = new Label(displayName);
		title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

		card.getChildren().add(title);
		card.setCursor(Cursor.HAND);
		card.setOnMouseClicked((_) -> ViewStaffThreads.showThreadDetails(thread));

		return card;
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the user to the Staff Home page. </p>
	 *
	 */
	protected static void performReturn() {
		guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffThreads.theStage, ViewStaffThreads.theUser);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
