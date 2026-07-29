package guiStaffDiscussionBoard;

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
 * <p> Title: ViewStaffDiscussionBoard class  </p>
 *
 * <p> Description: The Java/FX-based page for viewing the Staff discussion board. Provides a
 * view similar to the Student Discussion board. This gui implements a search bar, thread filter 
 * buttons, post card list, post body display and app navigation buttons </p>
 *
 * <p> Copyright: Pete Echavarria  © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-17 Initial version
 *
 */
public class ViewStaffDiscussionBoard{

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
	// Subtitle displayed to set context for the discussion board
	protected static Label label_Subtitle = new Label("Your class forum");

	// Buttons for navigating, allowing going back home, logging out, viewing my posts, and quitting
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");
	protected static Button button_MyPosts = new Button("My Posts");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// GUI Area 2: 

	// Search bar and its submit button
	// Search bar and submit button
	protected static TextField textfield_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	
	// Tracks the currently selected thread filter; an empty string means all threads are shown
	protected static String selectedThread = "";
	// Buttons for thread filtering
	protected static Button button_General = new Button ("General");
	protected static Button button_Homework = new Button ("Homework");
	protected static Button button_Quizzes = new Button("Quizzes");
	protected static Button button_ViewAll = new Button ("View All");

	// Create new post button
	protected static Button button_CreatePost = new Button("Create a New Post +");
	
	
	// GUI Area 3: 
	
	// VBox containing post cards populated by displayPostCards()
	protected static VBox postCardList = new VBox(10);
	// Scroll pane that wraps postCardList to allow scrolling through post cards
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(postCardList);
	
	
	// GUI Area 4: 
	
	// Scroll pane that shows the full post of the current post and its replies
	protected static ScrollPane scrollPane_PostBody = new ScrollPane();
	// Line separator to partition the post area from the button of the page
	protected static Line line_Separator4 = new Line(20, height-60, width-20, height-60);

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with user posts
	private static ViewStaffDiscussionBoard theView;	// Singleton instance preventing re-initialization

	protected static Stage theStage;  // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets 
	protected static User theUser;	// The current user of the application
		
	public static Scene theStaffDiscussionBoardScene = null;	// The Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayStaffDiscussionBoard(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the StaffDiscussionBoard page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup. If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * It then sets the Scene onto the stage, and makes it visible to the user. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the current user to display their username
	 *
	 */
	public static void displayStaffDiscussionBoard(Stage ps, User user) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStaffDiscussionBoard();
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		ControllerStaffDiscussionBoard.repaintTheWindow();
	}

	/**********
	 * <p> Method: ViewStaffDiscussionBoard() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the changeable
	 * fields using the displayDiscussionBoard method.</p>
	 *
	 */
	public ViewStaffDiscussionBoard() {	
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffDiscussionBoardScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");// make the background white
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: #666666;");
		
		label_PageTitle.setText("Staff Discussion Board");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
		
		setupLabelUI(label_Subtitle, "Arial", 20, 400, Pos.BASELINE_LEFT, 23, 80);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		
		setupButtonUI(button_MyPosts, "Dialog", 12, 70, Pos.CENTER, 772, 10);
		button_MyPosts.setOnAction((_) -> {guiMyPosts.ViewMyPosts.displayMyPosts(theStage, theUser);});
		button_MyPosts.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> {ControllerStaffDiscussionBoard.performHome(); });
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> {ControllerStaffDiscussionBoard.performLogout(); });
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
    
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerStaffDiscussionBoard.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		
		
		
		// GUI Area 2
		
		// Search row
		setupTextUI(textfield_Search, "Arial", 14, 250, Pos.BASELINE_LEFT, 20, 128, true);

		setupButtonUI(button_Search, "Dialog", 14, 75, Pos.CENTER, 270, 128);
		button_Search.setOnAction((_) -> { ControllerStaffDiscussionBoard.performSearch(); });
		button_Search.setStyle("-fx-background-color: #0062A3; -fx-text-displayPostCardsfill: white; -fx-background-radius: 5;");

		setupButtonUI(button_CreatePost, "Dialog", 12, 150, Pos.CENTER, 440, 160);
		button_CreatePost.setOnAction((_) -> { guiRelatedPosts.ViewRelatedPosts.displayRelatedPosts(theStage, theUser); });
		button_CreatePost.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_General, "Dialog", 13, 80, Pos.CENTER, 20, 160);
		button_General.setOnAction((_) -> { selectedThread = "General"; ControllerStaffDiscussionBoard.displayPostCards();
				selectedThread = "";});
		button_General.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Homework, "Dialog", 13, 80, Pos.CENTER, 110, 160);
		button_Homework.setOnAction((_) -> { selectedThread = "Homework"; ControllerStaffDiscussionBoard.displayPostCards();
				selectedThread = "";});
		button_Homework.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quizzes, "Dialog", 13, 80, Pos.CENTER, 200, 160);
		button_Quizzes.setOnAction((_) -> { selectedThread = "Quizzes"; ControllerStaffDiscussionBoard.displayPostCards();
				selectedThread = "";});
		button_Quizzes.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_ViewAll, "Dialog", 13, 80, Pos.CENTER, 290, 160);
		button_ViewAll.setOnAction((_) -> { selectedThread = ""; ControllerStaffDiscussionBoard.displayPostCards(); });
		button_ViewAll.setStyle("-fx-background-color: #002D72; -fx-text-fill: white; -fx-background-radius: 5;");

		
		// GUI Area 3
		setupScrollPane(scrollPane_PostCards, 10, 400, 600, 20, 193);
		
		// GUI Area 4
		setupScrollPane(scrollPane_PostBody, 0, 550, 600, 435, 193);
		

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
		s.setMaxWidth(w);
		s.setMinHeight(h);
		s.setMaxHeight(h);
		s.setLayoutX(x);
		s.setLayoutY(y);
	}
}