package guiStaffParameters;

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
import guiStaffParameters.ControllerStaffParameters;


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
public class ViewStaffParameters{

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;
	
	// GUI Area 1
	// Labels for page title and display name of logged in user
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	// Buttons to return to staff home, button to quit
	protected static Button button_Return = new Button("Home");
	protected static Button button_Quit = new Button("X");
	
	
	private static ViewStaffParameters theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffParametersScene;
	public static Label label_ErrorMessage = new Label(); // error handling for create new parameter (Story 2)


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
	public static void displayStaffParameters(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStaffParameters();

		theDatabase.getUserAccountDetails(user.getUserName());

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Staff Parameters");
		theStage.setScene(theViewStaffParametersScene);
		theStage.show();
	}

	/**********
	 * <p> Method:  </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStaffParameters() {
		theRootPane = new Pane();
		theViewStaffParametersScene = new Scene(theRootPane, width, height);
		
		// Gui area i
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: white;");
		// Return button, returns user to the staff home page
		setupButtonUI(button_Return, "Dialog", 12, 70, Pos.CENTER, 880, 10);
	    button_Return.setOnAction((_) -> { ControllerStaffParameters.performReturn(); });
		button_Return.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		// Quit Button		
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
	    button_Quit.setOnAction((_) -> { ControllerStaffParameters.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		theRootPane.getChildren().addAll(label_UserDetails, label_PageTitle, button_Return, button_Quit);
	}
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}

