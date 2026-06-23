package guiStudentHome;

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
 * <p> Title: ViewStudentHome Class. </p>
 *
 * <p> Description: The Java/FX-based Student Home Page. The page is a placeholder for the
 * Student role. Functional widgets will be added in a future phase.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-01 Initial version
 *
 */

public class ViewStudentHome {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Update Account");

	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - placeholder, no widgets yet

	private static Line line_Separator4 = new Line(20, 525, width-20, 525);
	protected static Button button_CreatePost = new Button("Create a Post");

	// GUI Area 3
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewStudentHome theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStudentHomeScene;
	protected static final int theRole = 4;		// Admin: 1; Role1: 2; Role2: 3; Student: 4
	protected static Button button_MyPosts = new Button("View My Posts");
	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayStudentHome(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Student Home page. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStudentHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStudentHome();

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewStudentHomeScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewStudentHome() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStudentHome() {
		theRootPane = new Pane();
		theViewStudentHomeScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> { ControllerStudentHome.performUpdate(); });

		// GUI Area 2 - placeholder

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> { ControllerStudentHome.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerStudentHome.performQuit(); });
		
		setupButtonUI(button_MyPosts, "Dialog", 18, 250, Pos.CENTER, 300, 300);
		button_MyPosts.setOnAction((_) -> { ControllerStudentHome.performMyPosts(); });
		
		setupButtonUI(button_CreatePost, "Dialog", 18, 250, Pos.CENTER, 20, 300);
		button_CreatePost.setOnAction((_) -> { ControllerStudentHome.performCreatePost(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			line_Separator4, button_Logout, button_Quit, button_MyPosts, button_CreatePost);
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
