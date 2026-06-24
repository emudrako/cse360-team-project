package guiRelatedPosts;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewRelatedPosts Class. </p>
 *
 * <p> Description: The Java/FX-based Related Posts Page. This page implements Student User
 * Story 2: "View Related Posts from Others." Before a student creates a post, this page lets
 * them browse existing posts made by other students so they can check whether their question
 * has already been asked and answered, helping avoid duplicate posts.
 *
 * The structure of this page follows the same MVC pattern, GUI Area layout (Area 1: title and
 * user details; Area 2: page-specific content; Area 3: navigation buttons), and singleton
 * instantiation pattern used by ViewDeleteUser, ViewAddRemoveRoles, and ViewStudentHome in the
 * Foundations-SU26 code. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00		2026-06-20 Initial version (Student User Story 2: View Related Posts
 * 									from Others)
 *
 */

public class ViewRelatedPosts {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: Page title, current user, and a shortcut to the Account Update page. This
	// mirrors the Area 1 layout used by every other Foundations-SU26 page so the application
	// feels consistent as the student navigates between pages.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: The actual content of this page — a scrollable list of posts made by other
	// students. A ListView is used instead of individual Labels because the number of posts is
	// not fixed; the Acceptance Criteria for Story 2 explicitly requires that the screen renders
	// without error whether there are zero, one, or many related posts, so a dynamically sized
	// widget is required rather than a fixed set of Label widgets.
	protected static Label label_RelatedPostsTitle =
			new Label("Posts from other students that may be related to your question:");
	protected static ListView<String> listView_RelatedPosts = new ListView<String>();
	protected static Label label_NoRelatedPosts =
			new Label("No related posts found. You may be the first to ask!");

	protected static Line line_Separator4 = new Line(20, 525, width-20, 525);

	// GUI Area 3: Standard navigation buttons present on every Foundations-SU26 page.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRelatedPosts theView;	// Used to determine if instantiation is needed

	// Reference for the in-memory database so this package has access, consistent with how every
	// other GUI package in Foundations-SU26 reaches the database.
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged-in student

	public static Scene theRelatedPostsScene = null;	// The Scene this page populates


	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayRelatedPosts(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Related Posts page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters around. It then
	 * checks to see if the page has already been instantiated as a singleton. If not, it creates
	 * the singleton instance, initializing the static aspects of the GUI widgets.
	 *
	 * After that, the Controller is asked to populate the list of related posts, since that data
	 * is dynamic and depends on the current state of the system's posts, not on the user who is
	 * currently logged in. This mirrors how ViewAddRemoveRoles delegates dynamic, state-dependent
	 * GUI population to the Controller rather than doing it directly in the View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 *
	 * @param user specifies the User (the currently logged-in student) for this GUI
	 *
	 */
	public static void displayRelatedPosts(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI by creating the
		// singleton instance of this class
		if (theView == null) theView = new ViewRelatedPosts();

		label_UserDetails.setText("User: " + theUser.getUserName());

		// Delegate the dynamic, state-dependent population of the related-posts list to the
		// Controller, which is the only class permitted to query the PostList for this page.
		ControllerRelatedPosts.loadRelatedPosts();
	}


	/**********
	 * <p> Method: ViewRelatedPosts() </p>
	 *
	 * <p> Description: This constructor initializes all the elements of the graphical user
	 * interface. This method determines the location, size, font, and event handlers for each
	 * GUI object.
	 *
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the changeable
	 * fields using the displayRelatedPosts method, consistent with the established Foundations-
	 * SU26 MVC pattern. </p>
	 *
	 */
	private ViewRelatedPosts() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theRelatedPostsScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Related Posts Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		// GUI Area 2
		setupLabelUI(label_RelatedPostsTitle, "Arial", 18, width-40, Pos.BASELINE_LEFT, 20, 120);

		listView_RelatedPosts.setLayoutX(20);
		listView_RelatedPosts.setLayoutY(150);
		listView_RelatedPosts.setPrefWidth(width-40);
		listView_RelatedPosts.setPrefHeight(355);

		setupLabelUI(label_NoRelatedPosts, "Arial", 18, width, Pos.CENTER, 0, 300);

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerRelatedPosts.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerRelatedPosts.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerRelatedPosts.performQuit(); });

		// This is the end of the GUI Widgets for the page.

		// Due to the fact that the page's content (Area 2) changes depending on whether there are
		// any related posts, the act of populating theRootPane's children has been delegated to
		// the Controller's repaintTheWindow method, following the same approach used by
		// ViewAddRemoveRoles for its own dynamic content.
	}


	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
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
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}