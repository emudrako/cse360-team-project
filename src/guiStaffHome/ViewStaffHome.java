package guiStaffHome;

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
 * <p> Title: ViewStaffHome Class. </p>
 *
 * <p> Description: The Java/FX-based Staff Home Page. The page is a placeholder for the
 * Staff role. Functional widgets will be added in a future phase.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-01 Initial version
 *
 */

public class ViewStaffHome {

	/*-*******************************************************************************************

	Attributes

	 */
	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - placeholder, no widgets yet

	protected static Button button_DiscussionBoard = new Button("Discussion Board");
	protected static Button button_Parameters = new Button("Manage Parameters");
	protected static Button button_Threads = new Button("Manage Threads");
	protected static Button button_Requests = new Button("View / Create Requests");
	protected static Button button_Review = new Button("Review & Feedback");
	protected static Button button_Coverage = new Button("Student Coverage");

	// GUI Area 3
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewStaffHome theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffHomeScene;
	protected static final int theRole = 6;		// Admin: 1; Role1: 2; Role2: 3; Student: 4; Instructor: 5; Staff: 6

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayStaffHome(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Staff Home page. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStaffHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStaffHome();

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theViewStaffHomeScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewStaffHome() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStaffHome() {
		theRootPane = new Pane();
		theViewStaffHomeScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> { ControllerStaffHome.performUpdate(); });

		// GUI Area 2 - Staff feature navigation
		setupButtonUI(button_DiscussionBoard, "Dialog", 18, 300, Pos.CENTER, 20, 150);
		button_DiscussionBoard.setOnAction((_) -> { ControllerStaffHome.performDiscussionBoard(); });

		setupButtonUI(button_Parameters, "Dialog", 18, 300, Pos.CENTER, 340, 150);
		button_Parameters.setOnAction((_) -> { ControllerStaffHome.performParameters(); });

		setupButtonUI(button_Threads, "Dialog", 18, 300, Pos.CENTER, 660, 150);
		button_Threads.setOnAction((_) -> { ControllerStaffHome.performThreads(); });

		setupButtonUI(button_Requests, "Dialog", 18, 300, Pos.CENTER, 20, 210);
		button_Requests.setOnAction((_) -> { ControllerStaffHome.performRequests(); });

		setupButtonUI(button_Review, "Dialog", 18, 300, Pos.CENTER, 340, 210);
		button_Review.setOnAction((_) -> { ControllerStaffHome.performReview(); });

		setupButtonUI(button_Coverage, "Dialog", 18, 300, Pos.CENTER, 660, 210);
		button_Coverage.setOnAction((_) -> { ControllerStaffHome.performCoverage(); });

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> { ControllerStaffHome.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerStaffHome.performQuit(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			button_DiscussionBoard, button_Parameters, button_Threads,
			button_Requests, button_Review, button_Coverage);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
