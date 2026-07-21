package guiStaffParameters;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;
import database.Database;
import entityClasses.EvaluationParameter;
import entityClasses.User;


/*******
 * <p> Title: ViewStaffParameters Class. </p>
 *
 * <p> Description:  GUI for the Staff Parameteres CRUD screen, satisfying STORY 2: 
 * Implement CRUD for Evaluation Parameters. Displays existing parameteres as a 
 * horizontal scrollable card list, and provides and inline for for creating new
 * parameters. </p>
 *
 * <p> Copyright: Maranda Martinez  © 2026 </p>
 *
 * @author Maranda Martinez
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
	protected static Label label_PageTitle = new Label("Parameters");
	protected static Label label_UserDetails = new Label();
	// Buttons to return to staff home, button to quit
	protected static Button button_Return = new Button("Home");
	protected static Button button_Quit = new Button("X");
	
	// Gui area II
	protected static HBox paramCardList = new HBox(10);
	protected static ScrollPane scrollPane_ParamCards = new ScrollPane(paramCardList); // Scroll pane for parameter view
	
	// GUI Area III — Create Parameter form
	protected static Button button_OpenCreateForm = new Button("Create New Parameter");

	protected static Label label_Name = new Label("Name:");
	protected static javafx.scene.control.TextField field_Name = new javafx.scene.control.TextField();

	protected static Label label_Description = new Label("Description (min 40 characters):");
	protected static javafx.scene.control.TextArea field_Description = new javafx.scene.control.TextArea();

	protected static Label label_MaxScore = new Label("Max Score (1-100):");
	protected static javafx.scene.control.TextField field_MaxScore = new javafx.scene.control.TextField();

	protected static Label label_Weight = new Label("Weight (1-10):");
	protected static javafx.scene.control.TextField field_Weight = new javafx.scene.control.TextField();

	protected static Button button_Submit = new Button("Submit");
	protected static Button button_Cancel = new Button("Cancel");
	private static ViewStaffParameters theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffParametersScene;
	public static Label label_ErrorMessage = new Label(); // error handling for create new parameter (Story 2)


	
	/*-*******************************************************************************************

	Entry Point

	*/
	
	/**********
	 * <p> Method: displayStaffParameters(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Staff Parameters screen,
	 *  satisfying STORY 2: Implement CRUD for Evaluation Parameters. Creates the
	 *  singleton view instance on first use, refreshes the list of existing
	 *  parameters from the database, and populates the horizontal parameter card
	 *  list each time the screen is shown. </p>
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
		
		// Refresh the list of all staff parameters for later display
		List<EvaluationParameter> allParams = ControllerStaffParameters.performReadAllStaffParameters();
		// Call to display all Parameters (Story 2)
		displayParamCards(allParams);
		theStage.show();
	}
	
	/*-*******************************************************************************************

	Constructors

	 */
	
	/**********
	 * <p> Method: ViewStaffParameters() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStaffParameters() {
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewStaffParametersScene = new Scene(theRootPane, width, height);
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


		setupLabelUI(label_PageTitle, "Arial", 28, 400, Pos.CENTER, 300, 140);
		// GUI Area II
		// Position and size the scroll pane so it sits inside the white card
		paramCardList.setPadding(new Insets(5));
		scrollPane_ParamCards.setLayoutX(220);
		scrollPane_ParamCards.setLayoutY(190);
		scrollPane_ParamCards.setPrefWidth(560);
		scrollPane_ParamCards.setPrefHeight(42);
		scrollPane_ParamCards.setStyle("-fx-background-color: transparent;");
		
		// GUI Area III — Create Parameter form setup
		setupButtonUI(button_OpenCreateForm, "Dialog", 14, 200, Pos.CENTER, 400, 250);
		button_OpenCreateForm.setOnAction((_) -> { showCreateForm(); });
		button_OpenCreateForm.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_Name, "Arial", 13, 200, Pos.BASELINE_LEFT, 240, 270);
		field_Name.setLayoutX(240);
		field_Name.setLayoutY(290);
		field_Name.setPrefWidth(300);

		setupLabelUI(label_Description, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 320);
		field_Description.setLayoutX(240);
		field_Description.setLayoutY(340);
		field_Description.setPrefSize(400, 200);

		setupLabelUI(label_MaxScore, "Arial", 13, 100, Pos.BASELINE_LEFT, 240, 545);
		field_MaxScore.setLayoutX(240);
		field_MaxScore.setLayoutY(565);
		field_MaxScore.setPrefWidth(120);

		setupLabelUI(label_Weight, "Arial", 13, 100, Pos.BASELINE_LEFT, 380, 545);
		field_Weight.setLayoutX(380);
		field_Weight.setLayoutY(565);
		field_Weight.setPrefWidth(120);

		setupButtonUI(button_Submit, "Dialog", 13, 120, Pos.CENTER, 510, 655);
		button_Submit.setOnAction((_) -> { handleSubmit(); });
		button_Submit.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Cancel, "Dialog", 13, 120, Pos.CENTER, 650, 655);
		button_Cancel.setOnAction((_) -> { hideCreateForm(); });
		button_Cancel.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_ErrorMessage, "Arial", 12, 400, Pos.BASELINE_LEFT, 240, 650);
		label_ErrorMessage.setStyle("-fx-text-fill: #BF0D3E;");

		// Start with the form hidden
		hideCreateForm();
		
		
		theRootPane.getChildren().addAll(card, label_UserDetails, label_PageTitle, button_Return, button_Quit,
			    scrollPane_ParamCards, button_OpenCreateForm,
			    label_Name, field_Name, label_Description, field_Description,
			    label_MaxScore, field_MaxScore, label_Weight, field_Weight,
			    button_Submit, button_Cancel, label_ErrorMessage);
	}
	
	/**********
	 * <p> Method: displayParamCards(List<EvaluationParameter> paramObjects) </p>
	 *
	 * <p> Description: Populates the parameter cards horizontal list with one card
	 *  per EvaluationParameter, satisfying the Read/display portion of STORY 2. </p>
	 *
	 * @param paramObjects the list of EvaluationParameter objects to display as cards
	 *
	 */
	protected static void displayParamCards(List<EvaluationParameter> paramObjects) {
	    paramCardList.getChildren().clear();
	    for (EvaluationParameter param : paramObjects) {
	        HBox card = ControllerStaffParameters.createParameterCard(param);
	        if (card != null) {
	            paramCardList.getChildren().add(card);
	        }
	    }
	}
	/**********
	 * <p> Method: showCreateForm() </p>
	 *
	 * <p> Description: Reveals the Create Parameter form fields and hides the
	 * "Create New Parameter" button, so only one mode is visible at a time. </p>
	 *
	 */
	private static void showCreateForm() {
	    button_OpenCreateForm.setVisible(false);
	    label_Name.setVisible(true);
	    field_Name.setVisible(true);
	    label_Description.setVisible(true);
	    field_Description.setVisible(true);
	    label_MaxScore.setVisible(true);
	    field_MaxScore.setVisible(true);
	    label_Weight.setVisible(true);
	    field_Weight.setVisible(true);
	    button_Submit.setVisible(true);
	    button_Cancel.setVisible(true);
	    label_ErrorMessage.setVisible(true);
	}

	/**********
	 * <p> Method: hideCreateForm() </p>
	 *
	 * <p> Description: Hides the Create Parameter form fields, clears any entered
	 * input, and returns to the default view with just the "Create New Parameter"
	 * button visible. </p>
	 *
	 */
	private static void hideCreateForm() {
	    button_OpenCreateForm.setVisible(true);
	    label_Name.setVisible(false);
	    field_Name.setVisible(false);
	    field_Name.clear();
	    label_Description.setVisible(false);
	    field_Description.setVisible(false);
	    field_Description.clear();
	    label_MaxScore.setVisible(false);
	    field_MaxScore.setVisible(false);
	    field_MaxScore.clear();
	    label_Weight.setVisible(false);
	    field_Weight.setVisible(false);
	    field_Weight.clear();
	    button_Submit.setVisible(false);
	    button_Cancel.setVisible(false);
	    label_ErrorMessage.setVisible(false);
	    label_ErrorMessage.setText("");
	}

	/**********
	 * <p> Method: handleSubmit() </p>
	 *
	 * <p> Description: Reads the Create form fields, parses maxScore/weight to double,
	 * and passes the input to the Controller. On success, refreshes the parameter card
	 * list and hides the form. </p>
	 *
	 */
	private static void handleSubmit() {
	    try {
	        double maxScore = Double.parseDouble(field_MaxScore.getText());
	        double weight = Double.parseDouble(field_Weight.getText());

	        ControllerStaffParameters.performCreateStaffParameter(
	            field_Name.getText(), field_Description.getText(), maxScore, weight);

	        // Refresh the card list to show the newly created parameter, and close the form
	        List<EvaluationParameter> allParams = ControllerStaffParameters.performReadAllStaffParameters();
	        displayParamCards(allParams);
	        hideCreateForm();

	    } catch (NumberFormatException e) {
	        label_ErrorMessage.setText("Max Score and Weight must be valid numbers.");
	    }
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

