package guiStaffRequests;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;


/*******
 * <p> Title: ViewStaffRequests class  </p>
 *
 * <p> Description: The Java/FX-based page for viewing Staff requests. Provides
 * a view that shows a list of request that can be filtered by their status - Open,
 * Assigned, and Closed. Once a request is clicked on from the list, the full request
 * will be displayed, as well as all comments on the request. </p>
 *
 * <p> Copyright: Pete Echavarria  © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-17 Initial version
 *
 */
public class ViewStaffRequests{

	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;
	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
		
	
	// GUI Area 1

    //  Labels for page title and user name 
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();

	// Buttons for navigating, allowing going back home, logging out, and quitting
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// GUI Area 2: 

	// Search bar and submit button for searching requests by keyword(s)
	protected static TextField textfield_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	
	// Tracks the currently selected status filter. An empty string means all requests are shown
	protected static String selectedStatus = "";
	// Buttons for status filtering
	protected static Button button_Open = new Button ("Open");
	protected static Button button_Assigned = new Button ("Assigned");
	protected static Button button_Closed = new Button("Closed");
	protected static Button button_ViewAll = new Button ("View All");

	// Create new request button
	protected static Button button_NewRequest = new Button("New Request");
	
	
	// GUI Area 3: 
	
	// VBox containing the list of request cards populated by displayRequestCards()
	protected static VBox requestCardList = new VBox(10);
	// Scroll pane that wraps requestCardList to allow scrolling through request cards
	protected static ScrollPane scrollPane_RequestCardList = new ScrollPane(requestCardList);
	// Request object to be displayed when its associated request card is clicked
	
	
	// GUI Area 4: 
	
	// Scroll pane that shows the full details of the currently selected request and its replies
	protected static ScrollPane scrollPane_RequestDetails = new ScrollPane();
	// Line separator to partition the post area from the button of the page
	protected static Line line_Separator4 = new Line(20, height-60, width-20, height-60);

	// This is the end of the GUI objects for the page.
	
	private static ViewStaffRequests theView;	// Singleton instance preventing re-initialization

	protected static Stage theStage;  // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets 
	protected static User theUser;	// The current user of the application
		
	public static Scene theStaffRequestsScene = null;	// The Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayStaffRequests(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Staff Requests page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup. If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * It then sets the Scene onto the stage, and makes it visible to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the current user to display their username
	 *
	 */
	public static void displayStaffRequests(Stage ps, User user) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStaffRequests();
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		ControllerStaffRequests.repaintTheWindow();
		
	}

	/**********
	 * <p> Method: ViewStaffRequests() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the changeable
	 * fields using the displayStaffRequests method.</p>
	 *
	 */
	public ViewStaffRequests() {	
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffRequestsScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");// make the background white
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: #666666;");
		
		label_PageTitle.setText("Staff Requests");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
		
		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> {ControllerStaffRequests.performHome();
				ControllerStaffRequests.currentRequest = null;
				scrollPane_RequestDetails.setContent(null); });
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> {ControllerStaffRequests.performLogout(); 
				ControllerStaffRequests.currentRequest = null;
				scrollPane_RequestDetails.setContent(null); });
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
    
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerStaffRequests.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		
		
		// GUI Area 2
		
		// Search row
		setupTextUI(textfield_Search, "Arial", 14, 250, Pos.BASELINE_LEFT, 20, 128, true);

		setupButtonUI(button_Search, "Dialog", 14, 75, Pos.CENTER, 275, 128);
		button_Search.setOnAction((_) -> { ControllerStaffRequests.performSearch(); });
		button_Search.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_NewRequest, "Dialog", 12, 150, Pos.CENTER, 440, 160);
		button_NewRequest.setOnAction((_) -> { guiCreateRequest.ViewCreateRequest.displayCreateRequest(theStage, theUser);
				ControllerStaffRequests.currentRequest = null;
				scrollPane_RequestDetails.setContent(null); });
		button_NewRequest.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Open, "Dialog", 13, 80, Pos.CENTER, 20, 160);
		button_Open.setOnAction((_) -> { selectedStatus = "Open"; ControllerStaffRequests.displayRequestCards();
				selectedStatus = "";});
		button_Open.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Assigned, "Dialog", 13, 80, Pos.CENTER, 110, 160);
		button_Assigned.setOnAction((_) -> { selectedStatus = "Assigned"; ControllerStaffRequests.displayRequestCards();
				selectedStatus = "";});
		button_Assigned.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Closed, "Dialog", 13, 80, Pos.CENTER, 200, 160);
		button_Closed.setOnAction((_) -> { selectedStatus = "Closed"; ControllerStaffRequests.displayRequestCards();
		selectedStatus = "";});
		button_Closed.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_ViewAll, "Dialog", 13, 80, Pos.CENTER, 290, 160);
		button_ViewAll.setOnAction((_) -> { selectedStatus = ""; ControllerStaffRequests.displayRequestCards(); });
		button_ViewAll.setStyle("-fx-background-color: #002D72; -fx-text-fill: white; -fx-background-radius: 5;");
	
		
		// GUI Area 3
		setupScrollPane(scrollPane_RequestCardList, 10, 400, 600, 20, 193);
		
		
		// GUI Area 4
		setupScrollPane(scrollPane_RequestDetails, 0, 550, 600, 435, 193);
		

		// This is the end of the GUI Widgets for the page
	}	
	

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
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
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
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
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a Text Field
	 * 
	 * @param t     The Text Field object to be initialized
	 * @param ff    The font to be used
	 * @param f     The size of the font to be used
	 * @param w     The width of the Text Field
	 * @param p     The alignment of the text field
	 * @param x     The location from the left edge (x axis)
	 * @param y     The location from the top (y axis)
	 * @param e     Whether the text field is editable
	 */
	protected static void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x,
			double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a Scroll Pane
	 * 
	 * @param s		The Scroll Pane object to be initialized
	 * @param p		The padding of the Scroll Pane object
	 * @param w		The width of the Scroll Pane object
	 * @param h		The height of the Scroll Pane object	
	 * @param x		The x position of the Scroll Pane object
	 * @param y		The y position of the Scroll Pane object
	 * 
	 */
	protected static void setupScrollPane(ScrollPane s, double p, double w, double h, double x, double y) {
		s.setPadding(new Insets(p));
		s.setMinWidth(w);
		s.setMinHeight(h);
		s.setMaxHeight(h);
		s.setLayoutX(x);
		s.setLayoutY(y);
	}
}