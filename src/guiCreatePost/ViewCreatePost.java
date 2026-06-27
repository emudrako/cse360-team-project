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
	protected static Button button_UpdateThisUser = new Button("Account Update");


	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - ListView and Controls
	protected static Label label_PostsHeader = new Label();
	private static Line line_Separator4 = new Line(20, 525, width-20, 525);
	protected static Label label_TitleHeader = new Label("Title:");
	protected static javafx.scene.control.TextField textfield_Title = new javafx.scene.control.TextField();
	protected static Label label_BodyHeader = new Label("Body:");
	protected static javafx.scene.control.TextArea textarea_Body = new javafx.scene.control.TextArea();
	protected static Label label_ThreadHeader = new Label("Thread:");
	protected static javafx.scene.control.ComboBox<String> combobox_Thread = new javafx.scene.control.ComboBox<>();
	protected static Button button_Submit = new Button("Submit");
	protected static Label label_ErrorMessage = new Label();

	// GUI Area 3 
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
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
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");// make the background white
	    theCreatePost = new Scene(theRootPane, width, height);
	 
	    // GUI Area 1
	    label_PageTitle.setText("Create Post");
	    setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	    
	    label_UserDetails.setText("User: " + theUser.getUserName());
	    setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
	    
	    setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
	    button_UpdateThisUser.setOnAction((_) -> { guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
	    
	    // GUI Area 2
	 // GUI Area 2
	    setupLabelUI(label_TitleHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 110);
	    textfield_Title.setLayoutX(120);
	    textfield_Title.setLayoutY(110);
	    textfield_Title.setPrefWidth(500);

	    setupLabelUI(label_BodyHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 160);
	    textarea_Body.setLayoutX(120);
	    textarea_Body.setLayoutY(160);
	    textarea_Body.setPrefWidth(500);
	    textarea_Body.setPrefHeight(200);

	    setupLabelUI(label_ThreadHeader, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 380);
	    combobox_Thread.getItems().addAll("General", "Homework", "Quizzes");
	    combobox_Thread.setPromptText("<Select Thread>");
	    combobox_Thread.setValue("General");
	    combobox_Thread.setLayoutX(120);
	    combobox_Thread.setLayoutY(378);

	    setupButtonUI(button_Submit, "Dialog", 16, 150, Pos.CENTER, 300, 430);
	    button_Submit.setOnAction((_) -> { ControllerCreatePost.performCreatePost(); });

	    setupLabelUI(label_ErrorMessage, "Arial", 14, 500, Pos.BASELINE_LEFT, 120, 480);
	    label_ErrorMessage.setStyle("-fx-text-fill: red;");
	    
	    // GUI Area 3
	    setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	    button_Return.setOnAction((_) -> { ControllerCreatePost.performReturn(); });

	    setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 270, 540);
	    button_Logout.setOnAction((_) -> { ControllerCreatePost.performLogout(); });

	    setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 520, 540);
	    button_Quit.setOnAction((_) -> { ControllerCreatePost.performQuit(); });

	    theRootPane.getChildren().addAll(
	    	label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	    	label_TitleHeader, textfield_Title,
	    	label_BodyHeader, textarea_Body,
	    	label_ThreadHeader, combobox_Thread,
	    	button_Submit, label_ErrorMessage,
	    	line_Separator4, button_Return, button_Logout, button_Quit);
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
