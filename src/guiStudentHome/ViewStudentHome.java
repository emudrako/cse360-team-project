package guiStudentHome;

import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;


/*******
 * <p> Title: ViewStudentHome Class. </p>
 *
 * <p> Description: The Java/FX-based Student Home Page. The page is a placeholder for the
 * Student role. Functional widgets will be added in a future phase.</p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00		2026-06-01 Initial version
 *
 */

public class ViewStudentHome {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = 1000;
	private static double height = 900;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Label label_UserWelcome = new Label();
	protected static Button button_UpdateThisUser = new Button("Update Account");


	// GUI Area 2
	protected static Button button_DiscussionBoard = new Button("CSE 360 Discussion Board\nSee Whats Happening!");
	
	protected static Button button_MyPosts = new Button();


	// GUI Area 3
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");
	
	protected static Button button_ContinueToCreate = new Button("Create a Post");

	private static ViewStudentHome theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	
	private static javafx.scene.text.Text text_MyPostsTitle = new javafx.scene.text.Text("My Posts");
	private static javafx.scene.text.Text text_MyPostsUnread = new javafx.scene.text.Text();

	private static Scene theViewStudentHomeScene;
	protected static final int theRole = 4;// Admin: 1; Role1: 2; Role2: 3; Student: 4
	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayStudentHome(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Student Home page. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStudentHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStudentHome();

		theDatabase.getUserAccountDetails(user.getUserName());
	    buildMyPostsGraphic();
		updateMyPostsButtonText();
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		theStage.setScene(theViewStudentHomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: updateMyPostsButtonText() </p>
	 *
	 * <p> Description: Have two sizes in my posts button. </p>
	 *
	 */
	protected static void buildMyPostsGraphic() {
	    text_MyPostsTitle.setFont(Font.font("Dialog", 30));
	    text_MyPostsTitle.setFill(javafx.scene.paint.Color.WHITE);
	    text_MyPostsTitle.setTextAlignment(TextAlignment.CENTER);

	    text_MyPostsUnread.setFont(Font.font("Dialog", 10));
	    text_MyPostsUnread.setFill(javafx.scene.paint.Color.WHITE);
	    text_MyPostsUnread.setTextAlignment(TextAlignment.CENTER);

	    javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(4, text_MyPostsTitle, text_MyPostsUnread);
	    vbox.setAlignment(Pos.CENTER);
	    button_MyPosts.setGraphic(vbox);
	}
	
	/**********
	 * <p> Method: updateMyPostsButtonText() </p>
	 *
	 * <p> Description: Updates my posts button with unread replies. </p>
	 *
	 */
	
	private static void updateMyPostsButtonText() {
	    int unread = getTotalUnreadReplies();
	    if (unread > 0) {
	        text_MyPostsUnread.setText(unread + " unread repl" + (unread == 1 ? "y" : "ies"));
	    } else {
	        text_MyPostsUnread.setText("0 unread replies");
	    }
	}
	
	/**********
	 * <p> Method: getTotalUnreadReplies() </p>
	 *
	 * <p> Description: Gets unread reply count for current user. </p>
	 *
	 */
	private static int getTotalUnreadReplies() {
	    String currentUsername = theUser.getUserName();
	    int totalUnread = 0;
	    
	    try {
	        List<entityClasses.Post> allPosts = theDatabase.getPostObjects();
	        
	        for (entityClasses.Post post : allPosts) {
	            if (post.getAuthorUsername().equals(currentUsername) && !post.getIsDeleted()) {
	                int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	                totalUnread += unreadCount;
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("Error calculating unread replies: " + e.getMessage());
	    }
	    
	    return totalUnread;
	}

	/**********
	 * <p> Method: ViewStudentHome() </p>
	 *
	 * <p> Description: Initializes all GUI elements. Singleton — runs once. </p>
	 *
	 */
	private ViewStudentHome() {
		theRootPane = new Pane();
		theViewStudentHomeScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");

		label_PageTitle.setText("Student Home Page");
        setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 45);
        label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
        label_UserDetails.setStyle("-fx-text-fill: #666666;");
        
        label_UserWelcome.setText("Welcome Back, " + theUser.getUserName());
        setupLabelUI(label_UserWelcome, "Arial", 45, 400, Pos.BASELINE_LEFT, 240, 170);
        label_UserWelcome.setStyle("-fx-text-fill: #666666;");

        setupButtonUI(button_DiscussionBoard, "Dialog", 50, 200, Pos.CENTER, 170, 300);
        button_DiscussionBoard.setAlignment(Pos.CENTER);
        button_DiscussionBoard.setTextAlignment(TextAlignment.CENTER);
        button_DiscussionBoard.setOnAction((_) -> ControllerStudentHome.displayDiscussionBoard());
        button_DiscussionBoard.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
        
        setupButtonUI(button_MyPosts, "Dialog", 40, 290, Pos.CENTER, 190, 508);
        button_MyPosts.setMinHeight(button_ContinueToCreate.getMinHeight());
        button_MyPosts.setAlignment(Pos.CENTER);
        buildMyPostsGraphic();
        updateMyPostsButtonText();
        button_MyPosts.setOnAction((_) -> guiMyPosts.ViewMyPosts.displayMyPosts(theStage, theUser));
        button_MyPosts.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");
        
        setupButtonUI(button_Logout, "Dialog", 12, 70, Pos.CENTER, 769, 10);
        button_Logout.setOnAction((_) -> ControllerStudentHome.performLogout());
        button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
        
        setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
        button_Quit.setOnAction((_) -> ControllerStudentHome.performQuit());
        button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
        
        setupButtonUI(button_UpdateThisUser, "Dialog", 12, 115, Pos.CENTER, 842, 10);
        button_UpdateThisUser.setOnAction((_) -> ViewUserUpdate.displayUserUpdate(theStage, theUser));
        button_UpdateThisUser.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
        
        setupButtonUI(button_ContinueToCreate, "Dialog", 40, 200, Pos.CENTER, 520, 510);
		button_ContinueToCreate.setOnAction((_) -> {guiCreatePost.ViewCreatePost.displayCreatePost(theStage, theUser); });
		button_ContinueToCreate.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");
        
        
        theRootPane.getChildren().addAll(
                label_PageTitle,
                label_UserDetails,
                label_UserWelcome,
                button_UpdateThisUser,
                button_DiscussionBoard,
                button_MyPosts,
                button_Logout,
                button_Quit,
                button_ContinueToCreate
                );

	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
		
		
	}
}
