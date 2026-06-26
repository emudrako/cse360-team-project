package guiRelatedPosts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewRelatedPosts Class. </p>
 *
 * <p> Description: The Java/FX-based Related Posts Page. This page implements Student
 * User Story 2 ("View Related Posts from Others") AND Student User Story 4 ("View and
 * Search Others' Posts by Keyword"), collapsed into a single screen by team agreement
 * on 2026-06-24 after Pete added the central Discussion Board.
 *
 * The role of this screen is a "pre-post duplicate check": when a student clicks the
 * "+" (New Post) button on the Discussion Board, instead of going directly to the
 * Create Post screen, they first land here and are invited to search whether anyone
 * else has already asked their question. If a match is found, they can read that post
 * instead of duplicating it; if nothing relevant comes back, they continue to the
 * Create Post screen by clicking "Continue to Create Post."
 *
 * This satisfies Story 2 (browse posts from others to avoid duplicates) and Story 4
 * (keyword-search posts from others) in a single, more useful workflow than either
 * story specifies on its own.
 *
 * The visual style of this page matches the team's CSE 360 Discussion UI Style Guide
 * established for TP2 (navy #041E42 titles, accent blue #0062A3 primary buttons, red
 * #BF0D3E close button, white-card post results with drop shadows, gray subtext for
 * timestamps). This keeps the page visually consistent with Pete's Discussion Board,
 * since this page is part of the Discussion Board posting flow. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00		2026-06-20 Initial version (Student User Story 2: View Related
 *						Posts from Others)
 * @version 2.00		2026-06-26 Refactored into a pre-post duplicate-check tool that
 *						also satisfies Student User Story 4 (keyword search of others'
 *						posts); reached from the Discussion Board's "+" New Post button
 *						instead of from the Student Home page
 * @version 2.10		2026-06-26 Restyled to match the team's CSE 360 Discussion UI
 *						Style Guide (navy/accent blue/white cards/drop shadows) so the
 *						page is visually consistent with Pete's Discussion Board
 *
 */

public class ViewRelatedPosts {

	/*-*******************************************************************************************

	Attributes

	 */

	// Per the team's CSE 360 Discussion UI Style Guide, all pages use 1000x700. This
	// matches the dimensions used by Pete's ViewDiscussionBoard (width axis) so the
	// page does not visually "shift" when the student transitions from one to the other.
	private static double width = 1000;
	private static double height = 700;

	// GUI Area 1: Page title, subtitle, current user, and top-right navigation bar
	// (Cancel / Logout / X). This matches the Pete Discussion Board layout exactly so
	// the student does not perceive a layout shift when transitioning between screens.
	protected static Label label_UserDetails = new Label();
	protected static Label label_PageTitle = new Label();
	protected static Label label_Subtitle =
			new Label("Before posting, check if your question has already been asked.");

	protected static Button button_Cancel = new Button("Cancel");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");

	// GUI Area 2: The search-before-posting workspace.
	// The search bar lets the student narrow the list to posts that may relate to
	// their question. This is the core Story 4 ("search by keyword") interaction.
	protected static TextField textfield_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static Button button_Reset = new Button("Show All");
	protected static Label label_ResultsHeader =
			new Label("Posts from other students that may be related:");

	// The results area uses a scrollable VBox of "post cards" rather than a plain
	// ListView so each result is rendered in the same visual style Pete uses on the
	// Discussion Board (white background, light-gray border, drop shadow). This makes
	// the screen feel like a natural extension of the Discussion Board.
	protected static VBox postCardList = new VBox(10);
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(postCardList);

	// The empty-state message shown inside the results area when no posts match. Per
	// the Story 2 and Story 4 Acceptance Criteria, the screen must render cleanly
	// even when there are zero matches.
	protected static Label label_NoRelatedPosts =
			new Label("No matching posts found. You may be the first to ask!");

	// The bridge to the Create Post screen. This button is what makes this a "pre-post
	// duplicate check": after looking at related posts, the student can proceed to
	// actually create their post by clicking here. Styled as a primary call-to-action
	// in accent blue per the style guide.
	protected static Button button_ContinueToCreate =
			new Button("Continue to Create Post");

	// Bottom separator, matching the line position used by Pete's Discussion Board.
	protected static Line line_Separator4 = new Line(20, height-60, width-20, height-60);

	// These attributes are used to configure the page and populate it with the user's
	// information.
	private static ViewRelatedPosts theView;	// Used to determine if instantiation is needed

	// Reference for the in-memory database so this package has access, consistent
	// with how every other GUI package in the team's Foundations-SU26 code reaches
	// the database.
	@SuppressWarnings("unused")
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
	 * <p> Description: This method is the single entry point from outside this package
	 * to display the Related Posts page.
	 *
	 * It first sets up the shared attributes so the rest of the package doesn't have
	 * to pass parameters around. It then checks to see if the page has already been
	 * instantiated as a singleton. If not, it creates the singleton instance,
	 * initializing the static aspects of the GUI widgets.
	 *
	 * After that, the Controller is asked to load posts and re-render the window,
	 * since the data is dynamic and depends on the current database state. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 *
	 * @param user specifies the User (the currently logged-in student) for this GUI
	 *
	 */
	public static void displayRelatedPosts(Stage ps, User user) {

		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewRelatedPosts();

		label_UserDetails.setText("User: " + theUser.getUserName());

		// On entry, start with all related posts shown (no keyword filter yet). The
		// student can then narrow with the search bar.
		textfield_Search.clear();
		ControllerRelatedPosts.loadRelatedPosts("");
	}


	/**********
	 * <p> Method: ViewRelatedPosts() </p>
	 *
	 * <p> Description: This constructor initializes all the elements of the graphical
	 * user interface. This method determines the location, size, font, color, and
	 * event handlers for each GUI object.
	 *
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the
	 * changeable fields using the displayRelatedPosts method. </p>
	 *
	 */
	private ViewRelatedPosts() {

		theRootPane = new Pane();
		theRelatedPostsScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");

		// GUI Area 1 — Header
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: #666666;");

		label_PageTitle.setText("Find Related Posts");
		setupLabelUI(label_PageTitle, "Arial", 40, 600, Pos.BASELINE_LEFT, 20, 55);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		setupLabelUI(label_Subtitle, "Arial", 18, 700, Pos.BASELINE_LEFT, 23, 100);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");

		// Top-right navigation, matching Pete's Discussion Board pattern.
		setupButtonUI(button_Cancel, "Dialog", 12, 70, Pos.CENTER, 772, 10);
		button_Cancel.setOnAction((_) -> {ControllerRelatedPosts.performCancel(); });
		button_Cancel.setStyle(
				"-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 845, 10);
		button_Logout.setOnAction((_) -> {ControllerRelatedPosts.performLogout(); });
		button_Logout.setStyle(
				"-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 905, 10);
		button_Quit.setOnAction((_) -> {ControllerRelatedPosts.performQuit(); });
		button_Quit.setStyle(
				"-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		// GUI Area 2 — Search row, matching the Discussion Board's search row visually.
		setupTextUI(textfield_Search, "Arial", 14, 540, Pos.BASELINE_LEFT, 20, 145);
		textfield_Search.setPromptText("Type a keyword (e.g. \"recursion\", \"HW2\")...");

		setupButtonUI(button_Search, "Dialog", 14, 90, Pos.CENTER, 565, 145);
		button_Search.setOnAction((_) ->
			{ControllerRelatedPosts.loadRelatedPosts(textfield_Search.getText()); });
		button_Search.setStyle(
				"-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Reset, "Dialog", 14, 90, Pos.CENTER, 660, 145);
		button_Reset.setOnAction((_) -> {
			textfield_Search.clear();
			ControllerRelatedPosts.loadRelatedPosts("");
		});
		button_Reset.setStyle(
				"-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 5;");

		// Results header
		setupLabelUI(label_ResultsHeader, "Arial", 18, width-40,
				Pos.BASELINE_LEFT, 20, 195);
		label_ResultsHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		// Results area — scrollable card list
		postCardList.setPadding(new Insets(5));
		scrollPane_PostCards.setLayoutX(20);
		scrollPane_PostCards.setLayoutY(225);
		scrollPane_PostCards.setMinWidth(width - 40);
		scrollPane_PostCards.setPrefWidth(width - 40);
		scrollPane_PostCards.setMinHeight(330);
		scrollPane_PostCards.setMaxHeight(330);
		scrollPane_PostCards.setStyle(
				"-fx-background: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5;");
		scrollPane_PostCards.setFitToWidth(true);

		// Empty-state label, shown inside the card area when no results.
		setupLabelUI(label_NoRelatedPosts, "Arial", 14, width-80,
				Pos.CENTER, 0, 0);
		label_NoRelatedPosts.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");

		// Primary call-to-action — Continue to Create Post.
		setupButtonUI(button_ContinueToCreate, "Dialog", 16, 280, Pos.CENTER,
				360, 580);
		button_ContinueToCreate.setOnAction((_) ->
			{guiCreatePost.ViewCreatePost.displayCreatePost(theStage, theUser); });
		button_ContinueToCreate.setStyle(
				"-fx-background-color: #0062A3; -fx-text-fill: white; "
				+ "-fx-background-radius: 5; -fx-font-weight: bold;");

		// The act of populating theRootPane's children and the post-card list is
		// delegated to the Controller's repaintTheWindow method, since both change
		// based on the current search results.
	}


	/*-*******************************************************************************************

	Static helper used by the Controller to build a single result card in the team's
	visual style. Lives here in the View (not the Controller) because it returns a
	JavaFX widget tree, which is View-layer responsibility under MVC.

	 */

	/**********
	 * <p> Method: VBox buildPostCard(String thread, String title, String author,
	 *  String timestamp, String excerpt) </p>
	 *
	 * <p> Description: Constructs a single rounded white card with a drop shadow
	 * showing one matching post's identifying information: the thread it belongs to,
	 * its title (bold), its author and timestamp (gray subtext), and a short excerpt
	 * of the body. The card visual matches Pete's Discussion Board card style so the
	 * two screens feel like one continuous experience. </p>
	 *
	 * @param thread the post's thread name (e.g. "General", "Homework")
	 * @param title the post's title
	 * @param author the username of the post's author
	 * @param timestamp a formatted human-readable creation timestamp
	 * @param excerpt a short excerpt of the post's body
	 * @return a VBox styled as a single result card
	 *
	 */
	protected static VBox buildPostCard(String thread, String title, String author,
			String timestamp, String excerpt) {

		VBox card = new VBox(5);
		card.setPadding(new Insets(10));
		card.setMinWidth(width - 80);
		card.setMaxWidth(width - 80);
		card.setStyle(
				"-fx-border-color: #E0E0E0;"
				+ "-fx-border-radius: 8;"
				+ "-fx-background-color: white;"
				+ "-fx-background-radius: 8;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

		Label threadLabel = new Label(thread);
		threadLabel.setStyle(
				"-fx-font-size: 11px; -fx-text-fill: #666666; -fx-font-style: italic;");

		HBox topRow = new HBox();
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		topRow.getChildren().addAll(titleLabel, spacer, threadLabel);

		Label authorAndTime = new Label("by " + author + "  •  " + timestamp);
		authorAndTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

		Label excerptLabel = new Label(excerpt);
		excerptLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
		excerptLabel.setWrapText(true);

		card.getChildren().addAll(topRow, authorAndTime, excerptLabel);
		card.setCursor(Cursor.HAND);
		return card;
	}


	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p,
			double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p,
			double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	private static void setupTextUI(TextField t, String ff, double f, double w, Pos p,
			double x, double y) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
}