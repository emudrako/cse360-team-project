package guiCreateRequest;

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
 * <p> Title: ViewCreateRequest Class. </p>
 *
 * <p> Description: The Java/FX-based Create Request Page. Allows staff to submit a new
 * admin request describing an action they need an admin to perform. This page is a
 * placeholder; functional widgets will be added in a future phase.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-27 Initial placeholder version
 *
 */

public class ViewCreateRequest {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();

	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - placeholder, no widgets yet

	private static Line line_Separator4 = new Line(20, 525, width-20, 525);

	// GUI Area 3
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewCreateRequest theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewCreateRequestScene;

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayCreateRequest(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Create Request page. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayCreateRequest(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewCreateRequest();

		theDatabase.getUserAccountDetails(user.getUserName());

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Create Request Page");
		theStage.setScene(theViewCreateRequestScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewCreateRequest() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewCreateRequest() {
		theRootPane = new Pane();
		theViewCreateRequestScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Create Request Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: ");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		// GUI Area 2 - placeholder

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> { ControllerCreateRequest.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerCreateRequest.performQuit(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Separator1,
			line_Separator4, button_Logout, button_Quit);
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
