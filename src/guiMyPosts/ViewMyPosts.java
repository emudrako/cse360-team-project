package guiMyPosts;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;
import database.Database;
import entityClasses.User;
import guiDiscussionBoard.ControllerDiscussionBoard;

/*******
 * <p> Title: ViewMyPosts Class. </p>
 *
 * <p> Description: The Java/FX-based page for viewing the My Posts page.
 * Allows the student to view their submitted posts, filter for unread replies,
 * search replies by user or keyword, and select a specific post to view its full body.</p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 * @version 1.00		2026-06-22 Initial version
 *
 */


public class ViewMyPosts {

	/*-*******************************************************************************************

	Attributes

	 */
	// Height and width for the window

	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Label label_Subtitle = new Label("Track your discussions and replies.");
	// App navigation buttons
	protected static Button button_discussionBoard = new Button("Discussion Board");
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");


	// GUI Area 2
	// Search by keyword, user and unread filters
	protected static Label label_PostsHeader = new Label("");
	protected static Label label_Filter = new Label("Filter:");
	protected static Button button_ToggleUnread = new Button("Unread");
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();
	protected static javafx.scene.control.TextField textfield_Search = new javafx.scene.control.TextField();
	protected static Button button_Search = new Button("→");
	
	// Thread filter buttons
	protected static Button button_General = new Button ("General");
	protected static Button button_Homework = new Button ("Homework");
	protected static Button button_Quizzes = new Button("Quizzes");
	protected static Button button_ViewAll = new Button ("View All");
	protected static String selectedThread = "";
	
	// Create post button for story 1
	protected static Button button_CreatePost = new Button("Create a New Post +");
	
	// GUI Area 3 post cards and post body , including delete post button
	protected static Button button_DeletePost = new Button("Delete");
	protected static javafx.scene.layout.VBox postCardList = new javafx.scene.layout.VBox(10);
	protected static javafx.scene.control.ScrollPane scrollPane_PostCards = new javafx.scene.control.ScrollPane(postCardList);
	protected static javafx.scene.control.ScrollPane scrollPane_PostBody = new javafx.scene.control.ScrollPane();
	protected static entityClasses.Post currentPost = new entityClasses.Post();


	private static ViewMyPosts theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theMyPostsScene;
	/*-*******************************************************************************************

	Constructors

	 */
	/**********
	 * <p> Method: displayMyPosts(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the My Posts page to be displayed.
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
	 * @param user specifies the User. currently logged in
	 */
	
	public static void displayMyPosts(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewMyPosts();
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerMyPosts.loadMyPosts();

		theStage.setTitle("My Posts");
		theStage.setScene(theMyPostsScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewMyPosts() </p>
	 *
	 * <p> Description: Initializes all GUI elements. It is a Singleton and runs once. </p>
	 *
	 */
	private ViewMyPosts() {
	    theRootPane = new Pane();
		theMyPostsScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");// make the background white
	 
	    // GUI Area 1
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		// Title formatting and style
	    label_PageTitle.setText("My Posts");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		// Subtitle
		setupLabelUI(label_Subtitle, "Arial", 20, 400, Pos.BASELINE_LEFT, 20, 80);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
	    
		
		// Buttons on the right hand side for app navigation
		setupButtonUI(button_discussionBoard, "Dialog", 12, 70, Pos.CENTER, 732, 10);
		button_discussionBoard.setOnAction((_) -> {ControllerMyPosts.performGoToDiscussionBoard(); });
		
		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> {ControllerMyPosts.performHome(); });

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> {ControllerMyPosts.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerMyPosts.performQuit(); });
	    
		//Style for Page Title and User Details
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");;
		label_UserDetails.setStyle("-fx-text-fill: #666666;");
		
		//Style for navigation buttons
		button_discussionBoard.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		
	    // GUI Area 2
	    setupLabelUI(label_PostsHeader, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 100);
	    
	    setupLabelUI(label_Filter, "Arial", 13, 40, Pos.BASELINE_LEFT, 20, 132);
	    label_Filter.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
	    
	    //filter / toggle my posts by unread/read
	    setupButtonUI(button_ToggleUnread, "Dialog", 12, 70, Pos.CENTER, 60, 128);
	    button_ToggleUnread.setOnAction((_) -> { ControllerMyPosts.performToggleUnread(); });
	    button_ToggleUnread.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

	    //filter my posts by user
	    setupComboBoxUI(combobox_SelectUser, "Dialog", 12, 85, (button_ToggleUnread.getLayoutX()+
	            button_ToggleUnread.getMinWidth()+5), 128);
	    combobox_SelectUser.setMaxWidth(90);
	    List<String> userList = theDatabase.getUserList();	
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
		combobox_SelectUser.setStyle("-fx-font: 12 Dialog; -fx-background-color: white; -fx-border-color: #0062A3; -fx-border-radius: 5;");
	    
		// Search box
		textfield_Search.setLayoutX(combobox_SelectUser.getLayoutX()+combobox_SelectUser.getMinWidth()+10);
	    textfield_Search.setLayoutY(128);
	    textfield_Search.setPrefHeight(22);
	    textfield_Search.setStyle("-fx-font-size: 12px;");
	    textfield_Search.setPrefWidth(100);
	    textfield_Search.setPromptText("Keyword");
	    textfield_Search.setStyle("-fx-font-size: 12px; -fx-border-color: #0062A3; -fx-border-radius: 5; -fx-background-radius: 5;");
	    
	    // Search button
	    setupButtonUI(button_Search, "Dialog", 12, 25, Pos.CENTER, (textfield_Search.getLayoutX()+
	            textfield_Search.getPrefWidth()+5), 128);
	    button_Search.setOnAction((_) -> { ControllerMyPosts.searchReplies(); });
	    button_Search.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
	    
	    //Buttons for filtering through threads
	    setupButtonUI(button_General, "Dialog", 13, 80, Pos.CENTER, 20, 160);
	    button_General.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
	    button_General.setOnAction((_) -> { selectedThread = "General"; ControllerMyPosts.loadMyPosts(); });

	    setupButtonUI(button_Homework, "Dialog", 13, 80, Pos.CENTER, 110, 160);
	    button_Homework.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
	    button_Homework.setOnAction((_) -> { selectedThread = "Homework"; ControllerMyPosts.loadMyPosts(); });

	    setupButtonUI(button_Quizzes, "Dialog", 13, 80, Pos.CENTER, 200, 160);
	    button_Quizzes.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
	    button_Quizzes.setOnAction((_) -> { selectedThread = "Quizzes"; ControllerMyPosts.loadMyPosts(); });

	    setupButtonUI(button_ViewAll, "Dialog", 13, 80, Pos.CENTER, 290, 160);
	    button_ViewAll.setStyle("-fx-background-color: #002D72; -fx-text-fill: white; -fx-background-radius: 5;");
	    button_ViewAll.setOnAction((_) -> { selectedThread = ""; ControllerMyPosts.loadMyPosts(); });
	    
	    // Create new post button taking you to related posts
	    setupButtonUI(button_CreatePost, "Dialog", 12, 150, Pos.CENTER, 440, 160);
	    button_CreatePost.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");
	    button_CreatePost.setOnAction((_) -> { guiRelatedPosts.ViewRelatedPosts.displayRelatedPosts(theStage, theUser); });
	    
	    // GUI Area 3
    
	    scrollPane_PostCards.setLayoutX(20);
	    scrollPane_PostCards.setLayoutY(193);
	    scrollPane_PostCards.setPrefWidth(400);
	    scrollPane_PostCards.setPrefHeight(600);
	    postCardList.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

	    scrollPane_PostBody.setLayoutX(435);
	    scrollPane_PostBody.setLayoutY(193);
	    scrollPane_PostBody.setPrefWidth(550);
	    scrollPane_PostBody.setPrefHeight(600);

		// Delete button
	    setupButtonUI(button_DeletePost, "Dialog", 12, 70, Pos.CENTER, 900, 205);
 
		button_DeletePost.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		button_DeletePost.setVisible(false); 
		button_DeletePost.setOnAction((_) -> ControllerMyPosts.performDeletePost());
		
	    theRootPane.getChildren().addAll(
	    	label_PageTitle, label_UserDetails,
	    	label_PostsHeader, scrollPane_PostCards, scrollPane_PostBody,
	    	button_ToggleUnread, combobox_SelectUser, textfield_Search, 
	    	button_Search, button_Home, button_Logout, button_Quit, button_discussionBoard, label_Filter, button_General, 
	    	button_Homework, button_ViewAll, button_Quizzes, button_CreatePost, label_Subtitle, button_DeletePost);
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

}
