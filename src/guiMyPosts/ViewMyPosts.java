package guiMyPosts;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewMyPosts Class. </p>
 *
 * <p> Description: The Java/FX-based page for viewing the My Posts page.
 * Allows the student to ...</p>
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

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Update Account");


	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - Post Cards and Post Body
	protected static Label label_PostsHeader = new Label("My Posts");
	protected static javafx.scene.layout.VBox postCardList = new javafx.scene.layout.VBox(10);
	protected static javafx.scene.control.ScrollPane scrollPane_PostCards = new javafx.scene.control.ScrollPane(postCardList);
	protected static javafx.scene.control.ScrollPane scrollPane_PostBody = new javafx.scene.control.ScrollPane();
	protected static entityClasses.Post currentPost = new entityClasses.Post();
	protected static Button button_ToggleUnread = new Button("Show Unread Only");
	protected static Label label_SearchReplies = new Label ("Search Replies: ");
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();
	protected static javafx.scene.control.TextField textfield_Search = new javafx.scene.control.TextField();
	protected static Button button_Search = new Button("Search");

	// GUI Area 3 
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
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
	 * @param user specifies the .. currently logged in
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
	    theMyPostsScene = new Scene(theRootPane, 960, height);
	 
	    // GUI Area 1
	    label_PageTitle.setText("My Posts");
	    setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	    
	    label_UserDetails.setText("User: " + theUser.getUserName());
	    setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
	    
	    setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
	    button_UpdateThisUser.setOnAction((_) -> { guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
	    
	    // GUI Area 2
	 // GUI Area 2
	    setupLabelUI(label_PostsHeader, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 100);

	    scrollPane_PostCards.setLayoutX(20);
	    scrollPane_PostCards.setLayoutY(120);
	    scrollPane_PostCards.setPrefWidth(350);
	    scrollPane_PostCards.setPrefHeight(380);

	    scrollPane_PostBody.setLayoutX(390);
	    scrollPane_PostBody.setLayoutY(120);
	    scrollPane_PostBody.setPrefWidth(550);
	    scrollPane_PostBody.setPrefHeight(380);

	    setupButtonUI(button_ToggleUnread, "Dialog", 14, 150, Pos.CENTER, 20, 510);
	    button_ToggleUnread.setOnAction((_) -> { ControllerMyPosts.performToggleUnread(); });

	    setupLabelUI(label_SearchReplies, "Arial", 12, 80, Pos.BASELINE_LEFT, button_ToggleUnread.getLayoutX()+
	    		button_ToggleUnread.getMinWidth()+20, 515);
	    
	    setupComboBoxUI(combobox_SelectUser, "Dialog", 14, 250, (label_SearchReplies.getLayoutX()+
	    		label_SearchReplies.getMinWidth()+20), 510);
	    List<String> userList = theDatabase.getUserList();	
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
	    
		textfield_Search.setLayoutX(combobox_SelectUser.getLayoutX()+combobox_SelectUser.getMinWidth()+10);
	    textfield_Search.setLayoutY(510);
	    textfield_Search.setPrefWidth(200);
	    textfield_Search.setPromptText("Keyword");
	    
	    setupButtonUI(button_Search, "Dialog", 14, 50, Pos.CENTER, (textfield_Search.getLayoutX()+
	    		textfield_Search.getPrefWidth()+10), 510);
	    button_Search.setOnAction((_) -> { ControllerMyPosts.searchReplies(); });
	    
	    // GUI Area 3
	    setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	    button_Return.setOnAction((_) -> { ControllerMyPosts.performReturn(); });

	    setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 270, 540);
	    button_Logout.setOnAction((_) -> { ControllerMyPosts.performLogout(); });

	    setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 520, 540);
	    button_Quit.setOnAction((_) -> { ControllerMyPosts.performQuit(); });

	    theRootPane.getChildren().addAll(
	    	label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	    	label_PostsHeader, scrollPane_PostCards, scrollPane_PostBody,
	    	button_ToggleUnread,label_SearchReplies, combobox_SelectUser, textfield_Search, 
	    	button_Search, button_Return, button_Logout, button_Quit);
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
