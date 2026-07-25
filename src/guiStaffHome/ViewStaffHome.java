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
import guiStudentHome.ControllerStudentHome;
import guiUserUpdate.ViewUserUpdate;


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
	protected static Label label_ApplicationTitle = new Label("CSE 360 Discussion Board");
	protected static Label label_UserDetails = new Label();

	protected static Button button_Quit = new Button("X");

	// GUI Area 2 - placeholder, no widgets yet
	protected static Label label_UserHome = new Label();
	protected static Button button_DiscussionBoard = new Button("Discussion Board");
	protected static Button button_Parameters = new Button("Manage Parameters");
	protected static Button button_Threads = new Button("Manage Threads");
	protected static Button button_Requests = new Button("View Requests");
	protected static Button button_Review = new Button("Review & Feedback");
	protected static Button button_Coverage = new Button("Student Coverage");	
	protected static Button button_UpdateThisUser = new Button("Update Account");
	protected static Button button_Logout = new Button("Logout");

	// GUI Area 3
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
		theRootPane.setStyle("-fx-background-color: #041E42;");

		// Gui area I
		// setup a card to have all the fields inside
		javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
		card.setWidth(600);
		card.setHeight(620);
		card.setX(200);
		card.setY(80);
		card.setArcWidth(20);
		card.setArcHeight(20);
		card.setFill(javafx.scene.paint.Color.WHITE);
		card.setEffect(new javafx.scene.effect.DropShadow(20, javafx.scene.paint.Color.rgb(0,0,0,0.3)));

		// GUI Area 1
		setupLabelUI(label_ApplicationTitle, "Arial", 24, 450, Pos.CENTER, 300, 30);
		label_ApplicationTitle.setStyle("-fx-text-fill: WHITE; -fx-font-weight: bold;");


		label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
        label_UserDetails.setStyle("-fx-text-fill: WHITE;");		

        // Button to exit the application
        setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
        button_Quit.setOnAction((_) -> ControllerStaffHome.performQuit());
        button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");


		// GUI Area 2 - Staff feature navigation
		label_UserHome.setText("Welcome, " + theUser.getUserName());
        setupLabelUI(label_UserHome, "Arial", 16, 450, Pos.CENTER, 270, 130);	
		label_UserHome.setStyle("-fx-text-fill: #041E42; -fx-font-style: italic;");

        
		setupButtonUI(button_DiscussionBoard, "Dialog", 16, 300, Pos.CENTER, 350, 200);
		button_DiscussionBoard.setOnAction((_) -> { ControllerStaffHome.performDiscussionBoard(); });
		button_DiscussionBoard.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Parameters, "Dialog", 16, 300, Pos.CENTER, 350, 260);
		button_Parameters.setOnAction((_) -> { ControllerStaffHome.performParameters(); });
		button_Parameters.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Threads, "Dialog", 16, 300, Pos.CENTER, 350, 320);
		button_Threads.setOnAction((_) -> { ControllerStaffHome.performThreads(); });
		button_Threads.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Requests, "Dialog", 16, 300, Pos.CENTER, 350, 380);
		button_Requests.setOnAction((_) -> { ControllerStaffHome.performRequests(); });
		button_Requests.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Review, "Dialog", 16, 300, Pos.CENTER, 350, 440);
		button_Review.setOnAction((_) -> { ControllerStaffHome.performReview(); });
		button_Review.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Coverage, "Dialog", 16, 300, Pos.CENTER, 350, 500);
		button_Coverage.setOnAction((_) -> { ControllerStaffHome.performCoverage(); });
		button_Coverage.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
        //Button for updating user details
        setupButtonUI(button_UpdateThisUser, "Dialog", 16, 300, Pos.CENTER, 350, 560);
		button_UpdateThisUser.setOnAction((_) -> { ControllerStaffHome.performUpdate(); });
        button_UpdateThisUser.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
        
		//Button to logout of application as current user. 
        setupButtonUI(button_Logout, "Dialog", 16, 300, Pos.CENTER, 350, 620);
        button_Logout.setOnAction((_) -> ControllerStaffHome.performLogout());
        button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");


		// GUI Area 3
		theRootPane.getChildren().addAll(
			label_UserDetails, button_Quit, label_ApplicationTitle, card, label_UserHome, button_Logout, button_UpdateThisUser,
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
