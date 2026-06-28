package guiCreatePost;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserLogin.ControllerUserLogin;

/*******
 * <p> Title: ViewCreatePost Class. </p>
 *
 * <p> Description: The Java/FX-based page for viewing the Create Post page.
 * Allows the student to compose and submit a new discussion post by entering a title, body, and 
 * selecting a thread category</p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 * @version 1.00		2026-06-23 Initial version
 *
 */


public class ViewCreatePost {

	/*-*******************************************************************************************

	Attributes

	 */

	// Height and width for the window
	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();


	// GUI Area 2 - ListView and Controls
	protected static Label label_PostsHeader = new Label();
	protected static Label label_TitleHeader = new Label("Title:");
	protected static javafx.scene.control.TextField textfield_Title = new javafx.scene.control.TextField();
	protected static Label label_BodyHeader = new Label("Body:");
	protected static javafx.scene.control.TextArea textarea_Body = new javafx.scene.control.TextArea();
	protected static Label label_ThreadHeader = new Label("Thread:");
	protected static javafx.scene.control.ComboBox<String> combobox_Thread = new javafx.scene.control.ComboBox<>();
	protected static Button button_Submit = new Button("Submit");
	protected static Label label_ErrorMessage = new Label();

	// GUI Area 3 
	protected static Button button_Cancel = new Button("Cancel");
	protected static Button button_Quit = new Button("X");
	
	private static ViewCreatePost theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theCreatePost;
	/*-*******************************************************************************************

	Constructors

	 */
	/**********
	 * <p> Method: displayCreatePost(Stage ps, User user) </p>
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
	
	public static void displayCreatePost(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewCreatePost();
		
		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("Create Post");
		theStage.setScene(theCreatePost);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewCreatePost() </p>
	 *
	 * <p> Description: Initializes all GUI elements. It is a Singleton and runs once. </p>
	 *
	 */
	private ViewCreatePost() {
	    theRootPane = new Pane();
		theRootPane.setStyle("-fx-background-color: #041E42;");// make the background navy
	    theCreatePost = new Scene(theRootPane, width, height);
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
	    button_Cancel.setOnAction((_) -> { ControllerCreatePost.performReturn(); });
		button_Cancel.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		// Quit Button		label_UserDetails.setStyle("-fx-text-fill: white;");
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
	    button_Quit.setOnAction((_) -> { ControllerCreatePost.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
	    
	    // GUI Area 2
		
		// Create Post title
	    label_PageTitle.setText("Create Post");
	    setupLabelUI(label_PageTitle, "Arial", 30, 400, Pos.CENTER, 310, 130);
	    label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    
	    // thread choice
	    setupLabelUI(label_ThreadHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 210, 200);
	    combobox_Thread.getItems().addAll("General", "Homework", "Quizzes");
	    combobox_Thread.setPromptText("<Select Thread>");
	    combobox_Thread.setValue("General");
	    combobox_Thread.setLayoutX(265);
	    combobox_Thread.setLayoutY(195);
	    label_ThreadHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    combobox_Thread.setStyle("-fx-font: 13 Dialog; -fx-background-color: white; -fx-border-color: #0062A3; -fx-border-radius: 8; -fx-background-radius: 8;");
	    
	    // Title for create post 
	    setupLabelUI(label_TitleHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 210, 240);
	    textfield_Title.setLayoutX(265);
	    textfield_Title.setLayoutY(240);
	    textfield_Title.setPrefWidth(500);
	    label_TitleHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    // Body for create post
	    setupLabelUI(label_BodyHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 210, 290);
	    textarea_Body.setLayoutX(265);
	    textarea_Body.setLayoutY(290);
	    textarea_Body.setPrefWidth(500);
	    textarea_Body.setPrefHeight(350);
	    label_BodyHeader.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    textfield_Title.setStyle("-fx-border-color: #0062A3; -fx-border-radius: 8; -fx-background-radius: 7; -fx-font-size: 13px;");
	    textarea_Body.setStyle("-fx-border-color: #0062A3; -fx-border-radius: 8; -fx-background-radius: 7; -fx-font-size: 13px;");

	    // submit button
	    setupButtonUI(button_Submit, "Dialog", 16, 100, Pos.CENTER, 660, 650);
	    button_Submit.setOnAction((_) -> { ControllerCreatePost.performCreatePost(); });
	    button_Submit.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px;");

	    setupLabelUI(label_ErrorMessage, "Arial", 14, 500, Pos.BASELINE_LEFT, 120, 480);
	    label_ErrorMessage.setStyle("-fx-text-fill: red;");
	    
	    theRootPane.getChildren().addAll(card,
	    	label_PageTitle, label_UserDetails,
	    	label_TitleHeader, textfield_Title,
	    	label_BodyHeader, textarea_Body,
	    	label_ThreadHeader, combobox_Thread,
	    	button_Submit, label_ErrorMessage,
	    	button_Cancel, button_Quit);
	}
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}
