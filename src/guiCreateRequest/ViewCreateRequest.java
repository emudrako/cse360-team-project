package guiCreateRequest;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;


/*******
 * <p> Title: ViewCreateRequest Class. </p>
 *
 * <p> Description: The Java/FX-based page for viewing the Create Request page. Allows the 
 * Staff to compose and submit a new Admin request by entering a subject and description. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-17 Initial version
 *
 */


public class ViewCreateRequest {

	/*-*******************************************************************************************

	Attributes

	 */

	// Height and width for the window, consistent with team UI style standards
	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	
	// Labels for page title and display name of logged in user
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	// Buttons to cancel if choose to not continue with creating a request, button to quit
	protected static Button button_Cancel = new Button("Cancel");
	protected static Button button_Quit = new Button("X");

	
	// GUI Area 2 - ListView and Controls
	
	// Input fields for request subject and description
	protected static Label label_SubjectHeader = new Label("Subject:");
	protected static javafx.scene.control.TextField textfield_Subject = new javafx.scene.control.TextField();
	protected static Label label_DescriptionHeader = new Label("Description:");
	protected static javafx.scene.control.TextArea textarea_Description = new javafx.scene.control.TextArea();
	// Submit button to create the request
	protected static Button button_Submit = new Button("Submit");
	// Displays validation error messages to the user
	protected static Label label_ErrorMessage = new Label();

	
	// GUI Area 3 
	
	// Singleton instance - null until first display call
	private static ViewCreateRequest theView;

	protected static Stage theStage;  // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets 
	protected static User theUser;	// The current user of the application

	private static Scene theCreateRequest; // The Scene each invocation populates
	/*-*******************************************************************************************

	Constructors

	 */
	/**********
	 * <p> Method: displayCreateRequest(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Create Post page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User currently logged in
	 */
	
	public static void displayCreateRequest(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewCreateRequest();
		
		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("Create Request");
		theStage.setScene(theCreateRequest);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewCreateRequest() </p>
	 *
	 * <p> Description: Initializes all GUI elements of the Create Request page.
	 * It is a Singleton and runs once. </p>
	 *
	 */
	private ViewCreateRequest() {
	    theRootPane = new Pane();
		theRootPane.setStyle("-fx-background-color: #041E42;");// make the background navy
	    theCreateRequest = new Scene(theRootPane, width, height);
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
	    
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: white;");
		// Cancel Button Style, takes user to discussion board
		setupButtonUI(button_Cancel, "Dialog", 12, 70, Pos.CENTER, 880, 10);
	    button_Cancel.setOnAction((_) -> { ControllerCreateRequest.performReturn(); });
		button_Cancel.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		// Quit Button		
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
	    button_Quit.setOnAction((_) -> { ControllerCreateRequest.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
	    
	    // GUI Area 2
		
		// Create Request title
	    label_PageTitle.setText("Create Request");
	    setupLabelUI(label_PageTitle, "Arial", 30, 400, Pos.CENTER, 310, 130);
	    label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    
	    // Subject for create request 
	    setupLabelUI(label_SubjectHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 210, 200);
	    textfield_Subject.setLayoutX(265);
	    textfield_Subject.setLayoutY(220);
	    textfield_Subject.setPrefWidth(500);
	    label_SubjectHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    // Description for create request
	    setupLabelUI(label_DescriptionHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 210, 260);
	    textarea_Description.setLayoutX(265);
	    textarea_Description.setLayoutY(280);
	    textarea_Description.setPrefWidth(500);
	    textarea_Description.setPrefHeight(350);
	    label_DescriptionHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    textfield_Subject.setStyle("-fx-border-color: #0062A3; -fx-border-radius: 8; -fx-background-radius: 7; -fx-font-size: 13px;");
	    textarea_Description.setStyle("-fx-border-color: #0062A3; -fx-border-radius: 8; -fx-background-radius: 7; -fx-font-size: 13px;");

	    // submit button
	    setupButtonUI(button_Submit, "Dialog", 16, 100, Pos.CENTER, 660, 650);
	    button_Submit.setOnAction((_) -> { 
	    		String requestorUsername = theUser.getUserName();
	    		String subject = textfield_Subject.getText();
	    		String description = textarea_Description.getText();
	    		ControllerCreateRequest.performCreateRequest(requestorUsername, subject, description);
	    		ViewCreateRequest.textfield_Subject.clear();
		        ViewCreateRequest.textarea_Description.clear();
		        ViewCreateRequest.label_ErrorMessage.setText("");
	    		ControllerCreateRequest.performReturn(); });
	    button_Submit.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px;");

	    
	    setupLabelUI(label_ErrorMessage, "Arial", 14, 500, Pos.BASELINE_LEFT, 120, 480);
	    label_ErrorMessage.setStyle("-fx-text-fill: red;");
	    
	    theRootPane.getChildren().addAll(card,
	    	label_PageTitle, label_UserDetails,
	    	label_SubjectHeader, textfield_Subject,
	    	label_DescriptionHeader, textarea_Description,
	    	button_Submit, label_ErrorMessage,
	    	button_Cancel, button_Quit);
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