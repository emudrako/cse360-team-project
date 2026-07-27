package guiStaffThreads;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;
import database.Database;
import entityClasses.Thread;
import entityClasses.User;


/*******
 * <p> Title: ViewStaffThreads Class. </p>
 *
 * <p> Description: GUI for the Staff Thread CRUD screen. Displays all existing
 *  threads as a horizontally scrollable card list, and provides an inline form
 *  for creating new threads and a detail panel for reading, updating, and deleting
 *  threads. The "General" thread card is grey and its detail panel hides the Edit
 *  and Delete buttons, enforcing the business rule that it may not be modified. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-07-25 Initial version
 *
 */
public class ViewStaffThreads {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = 1000;
	private static double height = 900;

	// GUI Area 1 — header bar
	protected static Label  label_PageTitle   = new Label("Threads");
	protected static Label  label_UserDetails = new Label();
	protected static Button button_Return     = new Button("Home");
	protected static Button button_Quit       = new Button("X");

	// GUI Area 2 — thread card scroll pane (always visible)
	protected static HBox       threadCardList         = new HBox(10);
	protected static ScrollPane scrollPane_ThreadCards = new ScrollPane(threadCardList);
	protected static Button     button_OpenCreateForm  = new Button("Create New Thread");

	// GUI Area 3 — shared form fields used by both create and detail modes
	protected static Label    label_Name        = new Label("Thread Name:");
	protected static TextField field_Name       = new TextField();
	protected static Label    label_Description = new Label("Description:");
	protected static TextArea  field_Description = new TextArea();

	// Create-mode buttons
	protected static Button button_Submit = new Button("Submit");
	protected static Button button_Cancel = new Button("Cancel");

	// Detail-mode buttons
	protected static Button button_Edit   = new Button("Edit");
	protected static Button button_Save   = new Button("Save");
	protected static Button button_Delete = new Button("Delete");
	protected static Button button_Back   = new Button("Back");

	// Status / error feedback label
	public static Label label_ErrorMessage = new Label();

	// Thread currently shown in the detail panel
	protected static Thread currentThread;

	private static ViewStaffThreads theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane  theRootPane;
	protected static User  theUser;

	private static Scene theViewStaffThreadsScene;


	/*-*******************************************************************************************

	Entry point

	 */

	/**********
	 * <p> Method: displayStaffThreads(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Staff Threads screen.
	 *  Creates the singleton view on first call. On every call, resets the screen
	 *  to list mode, clears any prior status message, and refreshes the thread cards
	 *  from the database. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStaffThreads(Stage ps, User user) {
		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewStaffThreads();

		theDatabase.getUserAccountDetails(user.getUserName());
		label_UserDetails.setText("User: " + theUser.getUserName());

		// Always start in list mode with a clean status area
		hideCreateForm();
		hideThreadDetails();
		label_ErrorMessage.setText("");

		// Refresh the card list
		List<Thread> allThreads = ControllerStaffThreads.performReadAllThreads();
		displayThreadCards(allThreads);

		theStage.setTitle("CSE 360 Foundations: Staff Threads");
		theStage.setScene(theViewStaffThreadsScene);
		theStage.show();
	}


	/*-*******************************************************************************************

	Constructor — runs once (singleton)

	 */

	/**********
	 * <p> Method: ViewStaffThreads() </p>
	 *
	 * <p> Description: Initialises all GUI elements. Singleton — runs exactly once. </p>
	 *
	 */
	private ViewStaffThreads() {
		theRootPane = new Pane();
		theViewStaffThreadsScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #041E42;");

		// White rounded card (same dimensions as every other staff page)
		javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
		card.setWidth(600);
		card.setHeight(620);
		card.setX(200);
		card.setY(80);
		card.setArcWidth(20);
		card.setArcHeight(20);
		card.setFill(javafx.scene.paint.Color.WHITE);
		card.setEffect(new javafx.scene.effect.DropShadow(20, javafx.scene.paint.Color.rgb(0, 0, 0, 0.3)));

		// ── GUI Area 1 — header ────────────────────────────────────────────────
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: white;");

		setupButtonUI(button_Return, "Dialog", 12, 70, Pos.CENTER, 880, 10);
		button_Return.setOnAction((_) -> ControllerStaffThreads.performReturn());
		button_Return.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> ControllerStaffThreads.performQuit());
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_PageTitle, "Arial", 28, 400, Pos.CENTER, 300, 130);

		// ── GUI Area 2 — thread card list ─────────────────────────────────────
		threadCardList.setPadding(new Insets(8));
		scrollPane_ThreadCards.setLayoutX(220);
		scrollPane_ThreadCards.setLayoutY(190);
		scrollPane_ThreadCards.setPrefWidth(560);
		scrollPane_ThreadCards.setPrefHeight(65);
		scrollPane_ThreadCards.setStyle("-fx-background-color: transparent;");

		setupButtonUI(button_OpenCreateForm, "Dialog", 14, 200, Pos.CENTER, 400, 420);
		button_OpenCreateForm.setOnAction((_) -> showCreateForm());
		button_OpenCreateForm.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		// ── GUI Area 3 — shared form fields ───────────────────────────────────
		setupLabelUI(label_Name, "Arial", 13, 200, Pos.BASELINE_LEFT, 240, 270);
		field_Name.setLayoutX(240);
		field_Name.setLayoutY(290);
		field_Name.setPrefWidth(300);

		setupLabelUI(label_Description, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 325);
		field_Description.setLayoutX(240);
		field_Description.setLayoutY(345);
		field_Description.setPrefSize(400, 130);
		field_Description.setWrapText(true);

		// Create-mode buttons
		setupButtonUI(button_Submit, "Dialog", 13, 120, Pos.CENTER, 510, 495);
		button_Submit.setOnAction((_) -> handleSubmit());
		button_Submit.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Cancel, "Dialog", 13, 120, Pos.CENTER, 650, 495);
		button_Cancel.setOnAction((_) -> hideCreateForm());
		button_Cancel.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		// Detail-mode buttons
		setupButtonUI(button_Edit, "Dialog", 13, 120, Pos.CENTER, 510, 495);
		button_Edit.setOnAction((_) -> {
			field_Name.setEditable(true);
			field_Description.setEditable(true);
			button_Edit.setVisible(false);
			button_Save.setVisible(true);
		});
		button_Edit.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Save, "Dialog", 13, 120, Pos.CENTER, 510, 495);
		button_Save.setOnAction((_) -> {
			ControllerStaffThreads.performUpdateThread(
				currentThread.getThreadID(), field_Name.getText(), field_Description.getText());
			field_Name.setEditable(false);
			field_Description.setEditable(false);
			button_Save.setVisible(false);
			button_Edit.setVisible(true);
			displayThreadCards(ControllerStaffThreads.performReadAllThreads());
		});
		button_Save.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Delete, "Dialog", 13, 120, Pos.CENTER, 240, 495);
		button_Delete.setOnAction((_) -> {
			ControllerStaffThreads.performDeleteThread(currentThread.getThreadID());
			displayThreadCards(ControllerStaffThreads.performReadAllThreads());
			hideThreadDetails();
		});
		button_Delete.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Back, "Dialog", 13, 120, Pos.CENTER, 650, 495);
		button_Back.setOnAction((_) -> hideThreadDetails());
		button_Back.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		// Status / error label
		setupLabelUI(label_ErrorMessage, "Arial", 12, 400, Pos.BASELINE_LEFT, 240, 545);
		label_ErrorMessage.setStyle("-fx-text-fill: #BF0D3E;");

		// Initialise to list mode
		hideCreateForm();
		hideThreadDetails();

		// Card is added first so form fields render on top of the white rectangle
		theRootPane.getChildren().addAll(
			label_UserDetails, button_Return, button_Quit,
			card,
			label_PageTitle,
			scrollPane_ThreadCards, button_OpenCreateForm,
			label_Name, field_Name, label_Description, field_Description,
			button_Submit, button_Cancel,
			button_Edit, button_Save, button_Delete, button_Back,
			label_ErrorMessage
		);
	}


	/*-*******************************************************************************************

	Package-visible display helpers called by the Controller

	 */

	/**********
	 * <p> Method: displayThreadCards(List Thread threads) </p>
	 *
	 * <p> Description: Clears and repopulates the thread card scroll pane. Called on
	 *  every page visit and after every create / delete operation. </p>
	 *
	 * @param threads the list of Thread objects to display as cards
	 *
	 */
	protected static void displayThreadCards(List<Thread> threads) {
		threadCardList.getChildren().clear();
		for (Thread t : threads) {
			HBox card = ControllerStaffThreads.createThreadCard(t);
			threadCardList.getChildren().add(card);
		}
	}

	/**********
	 * <p> Method: showThreadDetails(Thread thread) </p>
	 *
	 * <p> Description: Switches the view into detail mode for the given thread.
	 *  The Edit and Delete buttons are hidden when the thread is the protected
	 *  "General" thread, enforcing the business rule that it may not be modified. </p>
	 *
	 * @param thread the Thread whose details should be displayed
	 *
	 */
	protected static void showThreadDetails(Thread thread) {
		currentThread = thread;

		button_OpenCreateForm.setVisible(false);
		button_Submit.setVisible(false);
		button_Cancel.setVisible(false);

		label_Name.setVisible(true);
		field_Name.setVisible(true);
		field_Name.setText(thread.getName());
		field_Name.setEditable(false);

		label_Description.setVisible(true);
		field_Description.setVisible(true);
		field_Description.setText(thread.getDescription());
		field_Description.setEditable(false);

		// "General" thread: read-only — no edit or delete
		boolean editable = !thread.getIsDefault();
		button_Delete.setVisible(editable);
		button_Edit.setVisible(editable);
		button_Save.setVisible(false);
		button_Back.setVisible(true);

		label_ErrorMessage.setVisible(true);
		label_ErrorMessage.setText("");
	}


	/*-*******************************************************************************************

	Private view-state helpers

	 */

	/**
	 * Switches to create-form mode: hides the "Create New Thread" button and shows
	 * the blank editable form fields with Submit / Cancel buttons.
	 */
	private static void showCreateForm() {
		button_OpenCreateForm.setVisible(false);

		label_Name.setVisible(true);
		field_Name.setVisible(true);
		field_Name.clear();
		field_Name.setEditable(true);

		label_Description.setVisible(true);
		field_Description.setVisible(true);
		field_Description.clear();
		field_Description.setEditable(true);

		button_Submit.setVisible(true);
		button_Cancel.setVisible(true);
		button_Edit.setVisible(false);
		button_Save.setVisible(false);
		button_Delete.setVisible(false);
		button_Back.setVisible(false);

		label_ErrorMessage.setVisible(true);
		label_ErrorMessage.setText("");
	}

	/**
	 * Returns to list mode from create-form mode: restores the "Create New Thread"
	 * button and hides all form fields and feedback.
	 */
	private static void hideCreateForm() {
		button_OpenCreateForm.setVisible(true);

		label_Name.setVisible(false);
		field_Name.setVisible(false);
		field_Name.clear();

		label_Description.setVisible(false);
		field_Description.setVisible(false);
		field_Description.clear();

		button_Submit.setVisible(false);
		button_Cancel.setVisible(false);

		label_ErrorMessage.setVisible(false);
		label_ErrorMessage.setText("");
	}

	/**
	 * Returns to list mode from detail mode: restores the "Create New Thread"
	 * button and hides all form fields and detail buttons.
	 */
	private static void hideThreadDetails() {
		button_OpenCreateForm.setVisible(true);

		label_Name.setVisible(false);
		field_Name.setVisible(false);

		label_Description.setVisible(false);
		field_Description.setVisible(false);

		button_Edit.setVisible(false);
		button_Save.setVisible(false);
		button_Delete.setVisible(false);
		button_Back.setVisible(false);

		label_ErrorMessage.setVisible(false);
		label_ErrorMessage.setText("");
	}

	/**
	 * Reads the create-form fields, delegates to the controller, and on success
	 * refreshes the card list and returns to list mode. On validation failure the
	 * form stays open so the user can correct the input.
	 */
	private static void handleSubmit() {
		boolean success = ControllerStaffThreads.performCreateThread(
			field_Name.getText(), field_Description.getText());
		if (success) {
			displayThreadCards(ControllerStaffThreads.performReadAllThreads());
			hideCreateForm();
		}
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
