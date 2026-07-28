package guiStaffEvaluate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;
import entityClasses.EvaluationParameter;
import entityClasses.EvaluationScore;
import entityClasses.StudentActivitySummary;
import entityClasses.User;

/*******
 * <p> Title: ViewStaffEvaluate Class. </p>
 *
 * <p> Description: GUI for the Evaluate Student Discussion screen, satisfying
 *  STORY 3: Evaluate Student Discussion. Staff select a student from a dropdown,
 *  see that student's discussion-activity summary (total posts, total replies,
 *  distinct peers replied to), enter a score for each active Evaluation
 *  Parameter, save the scores, and see the student's overall grade once every
 *  active parameter has been scored. If no Evaluation Parameters exist yet, the
 *  scoring form is replaced with a message directing staff to Manage Evaluation
 *  Parameters (Story 2) first. Styled to match ViewStaffParameters.java and
 *  ViewStaffCoverage.java: dark navy background with a white rounded card,
 *  absolute layout on a Pane. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-26  Initial version
 *
 */
public class ViewStaffEvaluate {

	/*-*******************************************************************************************

	Attributes

	 */

	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;

	// GUI Area I — top bar
	protected static Label label_PageTitle = new Label("Evaluate Student Discussion");
	protected static Label label_UserDetails = new Label();
	protected static Button button_Return = new Button("Home");
	protected static Button button_Quit = new Button("X");

	// GUI Area II — student selector and activity summary
	protected static Label label_SelectStudent = new Label("Student:");
	protected static ComboBox<String> combo_Student = new ComboBox<String>();

	protected static Label label_SummaryTitle = new Label("Discussion Activity");
	protected static Label label_TotalPosts = new Label("Total Posts: —");
	protected static Label label_TotalReplies = new Label("Total Replies: —");
	protected static Label label_DistinctPeers = new Label("Distinct Peers Replied To: —");

	// GUI Area III — scoring form (built dynamically, one row per parameter)
	protected static Label label_NoParametersMessage = new Label(
			"No Evaluation Parameters exist yet. Go create some in "
			+ "Manage Evaluation Parameters (Story 2) first.");
	protected static VBox vbox_ScoreRows = new VBox(8);
	protected static ScrollPane scrollPane_ScoreRows = new ScrollPane(vbox_ScoreRows);

	// GUI Area IV — save action and results
	protected static Button button_Save = new Button("Save Scores");
	protected static Label label_OverallGrade = new Label("Overall Grade: —");
	public static Label label_ErrorMessage = new Label();

	// Maps each currently-displayed parameter's paramID to its score input field,
	// rebuilt every time buildScoringForm() runs so button_Save always reads the
	// fields actually on screen
	private static Map<Integer, TextField> scoreFieldsByParamID = new HashMap<Integer, TextField>();
	// Parallel map of paramID -> maxScore, needed to validate each field's input
	// before it is sent to the Controller
	private static Map<Integer, Double> maxScoreByParamID = new HashMap<Integer, Double>();

	private static ViewStaffEvaluate theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffEvaluateScene;

	/*-*******************************************************************************************

	Entry Point

	 */

	/**********
	 * <p> Method: displayStaffEvaluate(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Evaluate Student
	 *  Discussion screen. Creates the singleton view instance on first use,
	 *  refreshes the student dropdown from the database, and shows the scene. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStaffEvaluate(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStaffEvaluate();

		theDatabase.getUserAccountDetails(user.getUserName());
		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Evaluate Student Discussion");

		refreshStudentList();
		clearSummaryAndForm();

		theStage.setScene(theViewStaffEvaluateScene);
		theStage.show();
	}

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: ViewStaffEvaluate() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStaffEvaluate() {
		theRootPane = new Pane();
		theViewStaffEvaluateScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #041E42;");

		// White rounded card, same pattern used by ViewStaffParameters and ViewStaffCoverage
		javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
		card.setWidth(900);
		card.setHeight(780);
		card.setX(50);
		card.setY(70);
		card.setArcWidth(20);
		card.setArcHeight(20);
		card.setFill(javafx.scene.paint.Color.WHITE);
		card.setEffect(new javafx.scene.effect.DropShadow(20, javafx.scene.paint.Color.rgb(0, 0, 0, 0.3)));

		// ── GUI Area I: top bar (outside the card) ─────────────────────────
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: white;");

		setupButtonUI(button_Return, "Dialog", 12, 70, Pos.CENTER, 880, 10);
		button_Return.setOnAction((_) -> { ControllerStaffEvaluate.performReturn(); });
		button_Return.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> { ControllerStaffEvaluate.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_PageTitle, "Arial", 24, 860, Pos.CENTER, 70, 105);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		// ── GUI Area II: student selector and activity summary ────────────
		setupLabelUI(label_SelectStudent, "Arial", 13, 100, Pos.BASELINE_LEFT, 70, 155);
		label_SelectStudent.setStyle("-fx-text-fill: #041E42;");

		combo_Student.setLayoutX(150);
		combo_Student.setLayoutY(140);
		combo_Student.setPrefWidth(260);
		combo_Student.setOnAction((_) -> { handleStudentSelected(); });

		setupLabelUI(label_SummaryTitle, "Arial", 14, 400, Pos.BASELINE_LEFT, 450, 155);
		label_SummaryTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		setupLabelUI(label_TotalPosts, "Arial", 12, 250, Pos.BASELINE_LEFT, 450, 175);
		label_TotalPosts.setStyle("-fx-text-fill: #333333;");

		setupLabelUI(label_TotalReplies, "Arial", 12, 250, Pos.BASELINE_LEFT, 450, 195);
		label_TotalReplies.setStyle("-fx-text-fill: #333333;");

		setupLabelUI(label_DistinctPeers, "Arial", 12, 380, Pos.BASELINE_LEFT, 450, 215);
		label_DistinctPeers.setStyle("-fx-text-fill: #333333;");

		// Thin separator under the summary section
		javafx.scene.shape.Line summaryLine = new javafx.scene.shape.Line(70, 235, 940, 235);
		summaryLine.setStyle("-fx-stroke: #E0E0E0;");

		// ── GUI Area III: scoring form ──────────────────────────────────────
		setupLabelUI(label_NoParametersMessage, "Arial", 13, 800, Pos.BASELINE_LEFT, 70, 270);
		label_NoParametersMessage.setWrapText(true);
		label_NoParametersMessage.setStyle("-fx-text-fill: #BF0D3E;");
		label_NoParametersMessage.setVisible(false);

		vbox_ScoreRows.setPadding(new Insets(4));
		scrollPane_ScoreRows.setLayoutX(70);
		scrollPane_ScoreRows.setLayoutY(255);
		scrollPane_ScoreRows.setPrefWidth(860);
		scrollPane_ScoreRows.setPrefHeight(400);
		scrollPane_ScoreRows.setFitToWidth(true);
		scrollPane_ScoreRows.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

		// ── GUI Area IV: save action and results ───────────────────────────
		setupButtonUI(button_Save, "Dialog", 13, 150, Pos.CENTER, 70, 670);
		button_Save.setOnAction((_) -> { handleSave(); });
		button_Save.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_OverallGrade, "Arial", 16, 400, Pos.BASELINE_LEFT, 250, 680);
		label_OverallGrade.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

		setupLabelUI(label_ErrorMessage, "Arial", 12, 800, Pos.BASELINE_LEFT, 70, 720);
		label_ErrorMessage.setStyle("-fx-text-fill: #BF0D3E;");

		theRootPane.getChildren().addAll(
				card,
				label_UserDetails, button_Return, button_Quit, label_PageTitle,
				label_SelectStudent, combo_Student,
				label_SummaryTitle, label_TotalPosts, label_TotalReplies, label_DistinctPeers,
				summaryLine,
				label_NoParametersMessage, scrollPane_ScoreRows,
				button_Save, label_OverallGrade, label_ErrorMessage);
	}

	/*-*******************************************************************************************

	Student selection and summary

	 */

	/**********
	 * <p> Method: refreshStudentList() </p>
	 *
	 * <p> Description: Repopulates the student ComboBox from the database,
	 *  satisfying Story 3 criterion 1 ("A staff user can select a student from a
	 *  list"). Called once when the screen is first shown. </p>
	 *
	 */
	private static void refreshStudentList() {
		List<String> students = ControllerStaffEvaluate.performGetStudentList();
		combo_Student.getItems().setAll(students);
		combo_Student.setValue(students.isEmpty() ? "<Student>" : students.get(0));
	}

	/**********
	 * <p> Method: clearSummaryAndForm() </p>
	 *
	 * <p> Description: Resets the summary labels, scoring form, and overall grade
	 *  to their empty state. Called on screen entry and whenever the placeholder
	 *  "&lt;Student&gt;" is selected. </p>
	 *
	 */
	private static void clearSummaryAndForm() {
		label_TotalPosts.setText("Total Posts: —");
		label_TotalReplies.setText("Total Replies: —");
		label_DistinctPeers.setText("Distinct Peers Replied To: —");
		label_OverallGrade.setText("Overall Grade: —");
		label_ErrorMessage.setText("");
		vbox_ScoreRows.getChildren().clear();
		label_NoParametersMessage.setVisible(false);
		scoreFieldsByParamID.clear();
		maxScoreByParamID.clear();
	}

	/**********
	 * <p> Method: handleStudentSelected() </p>
	 *
	 * <p> Description: Runs whenever a student is chosen in the ComboBox. Loads
	 *  that student's activity summary (criterion 1), builds the scoring form
	 *  from the currently active parameters, and pre-populates it with any
	 *  scores already saved (criterion 3). If no parameters exist yet, shows
	 *  label_NoParametersMessage instead of a blank form (criterion 4). </p>
	 *
	 */
	private static void handleStudentSelected() {
		label_ErrorMessage.setText("");
		String selected = combo_Student.getValue();

		if (selected == null || selected.equals("<Student>")) {
			clearSummaryAndForm();
			return;
		}

		// Criterion 1: total posts, total replies, distinct peers replied to
		StudentActivitySummary summary = ControllerStaffEvaluate.performGetStudentSummary(selected);
		label_TotalPosts.setText("Total Posts: " + summary.getTotalPosts());
		label_TotalReplies.setText("Total Replies: " + summary.getTotalReplies());
		label_DistinctPeers.setText("Distinct Peers Replied To: " + summary.getDistinctPeersRepliedTo());

		buildScoringForm(selected);
		refreshOverallGrade(selected);
	}

	/*-*******************************************************************************************

	Scoring form

	 */

	/**********
	 * <p> Method: buildScoringForm(String studentUsername) </p>
	 *
	 * <p> Description: Builds one row per active EvaluationParameter, each with
	 *  the parameter's name/description and a score TextField, pre-populated
	 *  with the student's existing score for that parameter if one has been
	 *  saved (criterion 3). If no EvaluationParameters exist, hides the form and
	 *  shows label_NoParametersMessage instead (criterion 4). </p>
	 *
	 * @param studentUsername specifies the student the form is being built for
	 *
	 */
	private static void buildScoringForm(String studentUsername) {
		vbox_ScoreRows.getChildren().clear();
		scoreFieldsByParamID.clear();
		maxScoreByParamID.clear();

		List<EvaluationParameter> activeParams = ControllerStaffEvaluate.performGetActiveParameters();

		if (activeParams.isEmpty()) {
			label_NoParametersMessage.setVisible(true);
			scrollPane_ScoreRows.setVisible(false);
			button_Save.setDisable(true);
			return;
		}
		label_NoParametersMessage.setVisible(false);
		scrollPane_ScoreRows.setVisible(true);
		button_Save.setDisable(false);

		List<EvaluationScore> existingScores = ControllerStaffEvaluate.performGetScoresForStudent(studentUsername);

		for (EvaluationParameter param : activeParams) {
			maxScoreByParamID.put(param.getParamID(), param.getMaxScore());

			HBox row = new HBox(10);
			row.setPadding(new Insets(4, 8, 4, 8));

			Label lblName = new Label(param.getName() + " (max " + param.getMaxScore()
					+ ", weight " + param.getWeight() + ")");
			lblName.setMinWidth(600);
			lblName.setFont(Font.font("Arial", 12));
			lblName.setStyle("-fx-text-fill: #041E42;");

			TextField field = new TextField();
			field.setPrefWidth(80);

			// Pre-populate with the student's existing score, satisfying criterion 3
			for (EvaluationScore existing : existingScores) {
				if (existing.getParamID() == param.getParamID()) {
					field.setText(String.valueOf(existing.getScoreValue()));
					break;
				}
			}

			scoreFieldsByParamID.put(param.getParamID(), field);

			row.getChildren().addAll(lblName, field);
			vbox_ScoreRows.getChildren().add(row);
		}
	}

	/**********
	 * <p> Method: handleSave() </p>
	 *
	 * <p> Description: Reads every score field currently on screen and saves each
	 *  non-empty one via the Controller, satisfying criteria 2 and 3. Fields left
	 *  blank are skipped rather than treated as zero, so staff can score
	 *  parameters incrementally across multiple visits. After saving, refreshes
	 *  the overall grade (criterion 6). </p>
	 *
	 */
	private static void handleSave() {
		label_ErrorMessage.setText("");
		String selected = combo_Student.getValue();
		if (selected == null || selected.equals("<Student>")) {
			label_ErrorMessage.setText("Select a student before saving scores.");
			return;
		}

		boolean anySaved = false;
		boolean anyFailed = false;

		for (Map.Entry<Integer, TextField> entry : scoreFieldsByParamID.entrySet()) {
			int paramID = entry.getKey();
			String text = entry.getValue().getText();
			if (text == null || text.trim().isEmpty()) {
				continue; // leave unscored parameters alone; staff can score incrementally
			}

			try {
				double scoreValue = Double.parseDouble(text.trim());
				double maxScore = maxScoreByParamID.get(paramID);
				boolean saved = ControllerStaffEvaluate.performSaveScore(
						selected, paramID, theUser.getUserName(), scoreValue, maxScore);
				if (saved) anySaved = true; else anyFailed = true;
			} catch (NumberFormatException e) {
				label_ErrorMessage.setText("All scores must be valid numbers.");
				anyFailed = true;
			}
		}

		if (anyFailed) {
			if (label_ErrorMessage.getText().isEmpty()) {
				label_ErrorMessage.setText("*** ERROR *** One or more scores could not be saved.");
			}
		} else if (anySaved) {
			label_ErrorMessage.setText("Scores saved successfully");
		} else {
			label_ErrorMessage.setText("Enter at least one score before saving.");
		}

		// Refresh the form so freshly saved values are confirmed on screen, and
		// re-check whether an overall grade can now be computed
		buildScoringForm(selected);
		refreshOverallGrade(selected);
	}

	/**********
	 * <p> Method: refreshOverallGrade(String studentUsername) </p>
	 *
	 * <p> Description: Recomputes and displays the student's overall grade,
	 *  satisfying criterion 6. Shows a pending message rather than a number until
	 *  every active parameter has been scored. </p>
	 *
	 * @param studentUsername specifies the student whose overall grade is shown
	 *
	 */
	private static void refreshOverallGrade(String studentUsername) {
		Double grade = ControllerStaffEvaluate.performComputeOverallGrade(studentUsername);
		if (grade == null) {
			label_OverallGrade.setText("Overall Grade: pending (not all parameters scored yet)");
		} else {
			label_OverallGrade.setText(String.format("Overall Grade: %.1f%%", grade));
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
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}