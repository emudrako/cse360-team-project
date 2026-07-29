package guiUserLogin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/*******
 * <p> Title: GUIStartupPage Class. </p>
 * 
 * <p> Description: The Java/FX-based System Startup Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewUserLogin {

	/*-********************************************************************************************

	Attributes

	 *********************************************************************************************/

	// These are the application values required by the user interface
	
	private static double width = 1000;
	private static double height = 900;

	private static Label label_ApplicationTitle = new Label("CSE 360 Discussion Board");

	// This set is for all subsequent starts of the system
	private static Label label_OperationalStartTitle = new Label("Welcome back ");
	private static Label label_LogInInsrtuctions = new Label("Enter your login Information ");
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);


	//	private User user;
	private static Label label_Username = new Label("Username");	
	protected static TextField text_Username = new TextField();
	private static Label label_Password = new Label("Password");	
	protected static PasswordField text_Password = new PasswordField();
	private static Button button_Login = new Button("Continue");	

	private static Label label_AccountSetupInsrtuctions = new Label("New here? "+	
			"Enter your invitation code:");
	private static TextField text_Invitation = new TextField();
	private static Button button_SetupAccount = new Button("Create Account");

	private static Button button_Quit = new Button("X");

	private static Stage theStage;	
	private static Pane theRootPane;
	public static Scene theUserLoginScene = null;	


	private static ViewUserLogin theView = null;	//	private static guiUserLogin.ControllerUserLogin theController;


	/*-********************************************************************************************

	Constructor

	 *********************************************************************************************/

	public static void displayUserLogin(Stage ps) {
		
		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewUserLogin();
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.		
		text_Username.setText("");		// Reset the username and password from the last use
		text_Password.setText("");
		text_Invitation.setText("");	// Same for the invitation code

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: User Login Page");		
		theStage.setScene(theUserLoginScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewUserLoginPage() </p>
	 * 
	 * <p> Description: This method is called when the application first starts. It must handle
	 * two cases: 1) when no has been established and 2) when one or more users have been 
	 * established.
	 * 
	 * If there are no users in the database, this means that the person starting the system jmust
	 * be an administrator, so a special GUI is provided to allow this Admin to set a username and
	 * password.
	 * 
	 * If there is at least one user, then a different display is shown for existing users to login
	 * and for potential new users to provide an invitation code and if it is valid, they are taken
	 * to a page where they can specify a username and password.</p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's methods
	 * 
	 * @param db specifies the Database to be used by this GUI and it's methods
	 * 
	 */
	private ViewUserLogin() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserLoginScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #041E42;"); // navy background
		// create a card to hold all application details for adding navy background
		
		// Set up the Quit button  
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerUserLogin.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		
		javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
		// White card in the center
		card.setWidth(450);
		card.setHeight(480);
		card.setX(275);
		card.setY(180);
		card.setArcWidth(20);
		card.setArcHeight(20);
		card.setFill(javafx.scene.paint.Color.WHITE);
		card.setEffect(new javafx.scene.effect.DropShadow(20, javafx.scene.paint.Color.rgb(0,0,0,0.3)));

		// Populate the window with the title and other common widgets and set their static state
		setupLabelUI(label_ApplicationTitle, "Arial", 24, 450, Pos.CENTER, 275, 210);
		label_ApplicationTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");


		setupLabelUI(label_OperationalStartTitle, "Arial", 16, 450, Pos.CENTER, 275, 260);
		label_OperationalStartTitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");

		// Existing user log in portion of the page

		setupLabelUI(label_LogInInsrtuctions, "Arial", 16, 450, Pos.CENTER, 275, 320);

		// Establish the text input operand field for the username
		setupLabelUI(label_Username, "Arial", 13, width, Pos.BASELINE_LEFT, 400, 365);
		setupTextUI(text_Username, "Arial", 20, 200, Pos.BASELINE_LEFT, 400, 385, true);
		text_Username.setPromptText("");

		// Establish the text input operand field for the password
		setupLabelUI(label_Password, "Arial", 13, width, Pos.BASELINE_LEFT, 400, 430);
		setupTextUI(text_Password, "Arial", 20, 200, Pos.BASELINE_LEFT, 400, 450, true);
		text_Password.setPromptText("");

		// Set up the Log In button
		setupButtonUI(button_Login, "Dialog", 17, 200, Pos.CENTER, 400, 500);
		button_Login.setOnAction((_) -> {ControllerUserLogin.doLogin(theStage); });

		alertUsernamePasswordError.setTitle("Invalid username/password!");
		alertUsernamePasswordError.setHeaderText(null);

		// Style for login button and Setup Account button
		button_Login.setStyle("-fx-background-color: #002D72; -fx-text-fill: white; -fx-background-radius: 16;");

		// The invitation to setup an account portion of the page

		setupLabelUI(label_AccountSetupInsrtuctions, "Arial", 14, width, Pos.BASELINE_LEFT, 382, 570);

		// Establish the text input operand field for the password
		setupTextUI(text_Invitation, "Arial", 12, 100, Pos.CENTER, 393, 600, true);
		text_Invitation.setPromptText("Invite Code");

		// Set up the setup button
		setupButtonUI(button_SetupAccount, "Dialog", 14, 110, Pos.CENTER, 498, 599);
		button_SetupAccount.setOnAction((_) -> {
			System.out.println("**** Calling doSetupAccount");
			ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
		});
		button_SetupAccount.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		//		theRootPane.getChildren().clear();


		theRootPane.getChildren().addAll(card,
				label_ApplicationTitle, 
				label_OperationalStartTitle,
				label_LogInInsrtuctions, label_AccountSetupInsrtuctions, text_Username,
				 text_Password, button_Login, text_Invitation, button_SetupAccount,
				button_Quit, label_Username, label_Password);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/

	/**********
	 * Private local method to initialize the standard fields for a label
	 */

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}		
}
