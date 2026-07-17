package guiStaffDiscussionBoard;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;


/*******
 * <p> Title:  </p>
 *
 * <p> Description:  </p>
 *
 * <p> Copyright:  © 2026 </p>
 *
 * @author 
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ViewStaffDiscussionBoard {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;

	private static ViewStaffDiscussionBoard theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffDiscussionBoardScene;

	// Basic placeholder UI elements
	private static Label label_UserDetails = new Label();
	private static Label label_PageTitle = new Label("");
	private static Button button_BackToHome = new Button("Back to Home");


	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method:  </p>
	 *
	 * <p> Description:  </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStaffDiscussionBoard(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStaffDiscussionBoard();

		theDatabase.getUserAccountDetails(user.getUserName());

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Staff Discussion Board Page");
		theStage.setScene(theViewStaffDiscussionBoardScene);
		theStage.show();
	}

	/**********
	 * <p> Method:  </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStaffDiscussionBoard() {
		theRootPane = new Pane();
		theViewStaffDiscussionBoardScene = new Scene(theRootPane, width, height);

		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(20);

		label_PageTitle.setFont(new Font(24));
		label_PageTitle.setLayoutX(width / 2 - 80);
		label_PageTitle.setLayoutY(100);

		button_BackToHome.setLayoutX(20);
		button_BackToHome.setLayoutY(height - 60);
		button_BackToHome.setOnAction(e -> guiStaffHome.ViewStaffHome.displayStaffHome(theStage, theUser));

		theRootPane.getChildren().addAll(label_UserDetails, label_PageTitle, button_BackToHome);
	}
}


