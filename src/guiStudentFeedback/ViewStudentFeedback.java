package guiStudentFeedback;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewStudentFeedback Class. </p>
 *
 * <p> Description: The Java/FX-based My Feedback page. Lets a student view all private
 * feedback staff have left addressed to them, satisfying the student-visible portion
 * of the STORY: "As a staff member, I can review students' posts and replies, and I can
 * provide private feedback to students ..." Styled to match the My Posts page. </p>
 *
 * <p> Copyright: OWNER_NAME_HERE © 2026 </p>
 *
 * @author OWNER_NAME_HERE
 *
 * @version 1.00		2026-07-26 Initial version
 *
 */
public class ViewStudentFeedback {

	/*-*******************************************************************************************

	Attributes

	 */

	// Height and width for the window, consistent with team UI style standards
	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	protected static Label label_PageTitle = new Label("My Feedback");
	protected static Label label_UserDetails = new Label();
	protected static Label label_Subtitle = new Label("Private feedback staff have left on your posts.");
	protected static Button button_MyPosts = new Button("My Posts");
	protected static Button button_DiscussionBoard = new Button("Discussion Board");
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");

	// GUI Area 2 — feedback card list
	protected static VBox feedbackCardList = new VBox(10);
	protected static ScrollPane scrollPane_FeedbackCards = new ScrollPane(feedbackCardList);

	// Singleton instance — null until first display call
	private static ViewStudentFeedback theView;
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theStudentFeedbackScene;

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayMyFeedback(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the My Feedback page. Creates
	 * the singleton view instance on first use, reloads the feedback list from the
	 * database each time the screen is shown, and shows the scene. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayMyFeedback(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStudentFeedback();

		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerStudentFeedback.loadMyFeedback();

		theStage.setTitle("My Feedback");
		theStage.setScene(theStudentFeedbackScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewStudentFeedback() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStudentFeedback() {
		theRootPane = new Pane();
		theStudentFeedbackScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");

		// GUI Area 1
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: #666666;");

		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		setupLabelUI(label_Subtitle, "Arial", 20, 500, Pos.BASELINE_LEFT, 20, 80);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");

		setupButtonUI(button_MyPosts, "Dialog", 12, 70, Pos.CENTER, 655, 10);
		button_MyPosts.setOnAction((_) -> { ControllerStudentFeedback.performGoToMyPosts(); });
		button_MyPosts.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_DiscussionBoard, "Dialog", 12, 70, Pos.CENTER, 730, 10);
		button_DiscussionBoard.setOnAction((_) -> { ControllerStudentFeedback.performGoToDiscussionBoard(); });
		button_DiscussionBoard.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> { ControllerStudentFeedback.performHome(); });
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> { ControllerStudentFeedback.performLogout(); });
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> { ControllerStudentFeedback.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		// GUI Area 2
		feedbackCardList.setPadding(new javafx.geometry.Insets(10));
		scrollPane_FeedbackCards.setLayoutX(20);
		scrollPane_FeedbackCards.setLayoutY(130);
		scrollPane_FeedbackCards.setPrefWidth(600);
		scrollPane_FeedbackCards.setPrefHeight(650);

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, label_Subtitle,
				button_MyPosts, button_DiscussionBoard, button_Home, button_Logout, button_Quit,
				scrollPane_FeedbackCards);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}