package guiDiscussionBoard;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

/*******
 * <p> Title: guiDiscussionBoard Class. </p>
 * 
 * <p> Description: The Java/FX-based page for viewing the class discussion board. Supports Story 7 (Search All Posts by
 * Keyword) and Story 4 (Search other Posts by Keyword) Provides a serach bar, thread filter buttons,
 * post card list, post body display and app navigation buttons.</p>
 * 
 * @author Pete Echavarria
 * @author Maranda Martinez (redesign)
 * 
 * @version 1.00		2026-06-14 Initial version
 *  
 */

public class ViewDiscussionBoard {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 900;

	// A list of post objects that will be populated from the database
	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	
	
	// GUI Area 1

    //  Labels for page title and user name 
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	// Subtitle displayed to set context for the discussion board
	protected static Label label_Subtitle = new Label("Your class forum");

	// Buttons for navigating, allowing going back home, logging out, viewing my posts, and quitting
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");
	protected static Button button_MyPosts = new Button("My Posts");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// GUI Area 2: 

	//  Search bar and its submit button
	// Search bar and submit button , supports Story 7 (Search All Posts by Keyword)
	protected static TextField textfield_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	
	// Tracks the currently selected thread filter; an empty string means all threads are shown (Story 7)
	protected static String selectedThread = "";
	// Buttons for thread filtering (Story 7)
	protected static Button button_General = new Button ("General");
	protected static Button button_Homework = new Button ("Homework");
	protected static Button button_Quizzes = new Button("Quizzes");
	protected static Button button_ViewAll = new Button ("View All");

	// Create new post button
	protected static Button button_CreatePost = new Button("Create a New Post +");
	
	
	// GUI Area 3: 
	
	// VBox containing post cards populated by displayPostCards() (Used for Story 7 and 4)
	protected static VBox postCardList = new VBox(10);
	// Scroll pane that waraps postCardList to allow scrolling through post cards
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(postCardList);
	// Tracks the currently selected post for use in displayPost() and newReplyForm()
	protected static Post currentPost = new Post();
	
	
	// GUI Area 4: 
	
	// Scroll pane that shows the full posdy of the current post and its replies
	protected static ScrollPane scrollPane_PostBody = new ScrollPane();
	// Tracks whether the reply form is currently shown, and changes the post body display
	protected static boolean onReplyForm = false;
	// Line separator to partition the post area from the buttom of the page
	protected static Line line_Separator4 = new Line(20, height-60, width-20, height-60);

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with user posts
	private static ViewDiscussionBoard theView;	// Singleton instance preventing re-initialization
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;  // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets 
	protected static User theUser;	// The current user of the application
		
	public static Scene theDiscussionBoardScene = null;	// The Scene each invocation populates


	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayDiscussionBoard(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the DiscussionBoard page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * It then sets the Scene onto the stage, and makes it visible to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the current user to display their username
	 *
	 */
	public static void displayDiscussionBoard(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewDiscussionBoard();
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		ControllerDiscussionBoard.repaintTheWindow();
		
	}

	
	/**********
	 * <p> Method: guiViewDiscussionBoard() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the changeable
	 * fields using the displayDiscussionBoard method.</p>
	 * 
	 */
	public ViewDiscussionBoard() {	
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theDiscussionBoardScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #FFFFFF;");// make the background white
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: #666666;");
		
		label_PageTitle.setText("Discussion Board");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 35);
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
		
		setupLabelUI(label_Subtitle, "Arial", 20, 400, Pos.BASELINE_LEFT, 23, 80);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		
		setupButtonUI(button_MyPosts, "Dialog", 12, 70, Pos.CENTER, 772, 10);
		button_MyPosts.setOnAction((_) -> {guiMyPosts.ViewMyPosts.displayMyPosts(theStage, theUser);});
		button_MyPosts.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> {ControllerDiscussionBoard.performHome(); });
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> {ControllerDiscussionBoard.performLogout(); });
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
    
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerDiscussionBoard.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");
		
		
		
		// GUI Area 2
		
		// Search row
		setupTextUI(textfield_Search, "Arial", 14, 250, Pos.BASELINE_LEFT, 20, 128, true);

		setupButtonUI(button_Search, "Dialog", 14, 75, Pos.CENTER, 270, 128);
		button_Search.setOnAction((_) -> { ControllerDiscussionBoard.performSearch(); });
		button_Search.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_CreatePost, "Dialog", 12, 150, Pos.CENTER, 440, 160);
		button_CreatePost.setOnAction((_) -> { guiRelatedPosts.ViewRelatedPosts.displayRelatedPosts(theStage, theUser); });
		button_CreatePost.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_General, "Dialog", 13, 80, Pos.CENTER, 20, 160);
		button_General.setOnAction((_) -> { selectedThread = "General"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });
		button_General.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		
		setupButtonUI(button_Homework, "Dialog", 13, 80, Pos.CENTER, 110, 160);
		button_Homework.setOnAction((_) -> { selectedThread = "Homework"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });
		button_Homework.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Quizzes, "Dialog", 13, 80, Pos.CENTER, 200, 160);
		button_Quizzes.setOnAction((_) -> { selectedThread = "Quizzes"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });
		button_Quizzes.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_ViewAll, "Dialog", 13, 80, Pos.CENTER, 290, 160);
		button_ViewAll.setOnAction((_) -> { selectedThread = ""; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });
		button_ViewAll.setStyle("-fx-background-color: #002D72; -fx-text-fill: white; -fx-background-radius: 5;");

		
		// GUI Area 3
		setupScrollPane(scrollPane_PostCards, 10, 400, 600, 20, 193);
		
		// GUI Area 4
		setupScrollPane(scrollPane_PostBody, 0, 550, 600, 435, 193);
		

		// This is the end of the GUI Widgets for the page
	}	
	
	/**********
	 * <p> Method: displayPostCards() </p>
	 * 
	 * <p> Description: This method populates the post cards Scroll Pane with which shows
	 * a list of posts by subject line. The post cards visible is based on which thread has
	 * been selected. If no thread has been selected, all post cards are visible. </p>
	 * 
	 * @param postObjects the list of Post objects to display as post cards
	 *  
	 */
	protected static void displayPostCards(List<Post> postObjects) {
	    List<Post> newPostList = new ArrayList<>();
	    
	    if (selectedThread.equals("General")) {
	        for (Post post : postObjects) {
	            if (post.getThread().equals("General") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else if (selectedThread.equals("Homework")) {
	        for (Post post : postObjects) {
	            if (post.getThread().equals("Homework") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else if (selectedThread.equals("Quizzes")) {
	        for (Post post : postObjects) {
	            if (post.getThread().equals("Quizzes") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else {
	        for (Post post : postObjects) {
	            if (!post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    
	    postCardList.getChildren().clear();
	    for (Post post : newPostList) {
	        VBox card = ControllerDiscussionBoard.createPostCard(post);
	        if (card != null) {
	            postCardList.getChildren().add(card);
	        }
	    }
	    selectedThread = "";
	}
	
	/**********
	 * <p> Method: newReplyForm() </p>
	 * 
	 * <p> Description: This method populates the new reply VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a reply. </p>
	 * 
	 * @return a VBox containing the reply form with a text area and submit/cancel buttons
	 *  
	 */
	protected static VBox newReplyForm() {
		VBox vBox_ReplyForm = new VBox(5);
		vBox_ReplyForm.setPadding(new Insets(15));
		
		Label label_Title = new Label("New Reply");
		label_Title.setFont(Font.font("Arial", 12));
		label_Title.setStyle("-fx-font-weight: bold;");
		
		TextArea textArea_ReplyContent = new TextArea();
		textArea_ReplyContent.setWrapText(true);
		textArea_ReplyContent.setPrefRowCount(12);
		
		Button button_Submit = new Button("Submit");
		Button button_Cancel = new Button("Cancel");
		HBox hBox_Buttons = new HBox(10, button_Submit, button_Cancel);
		
		vBox_ReplyForm.getChildren().addAll(
				label_Title,
				textArea_ReplyContent,
				hBox_Buttons);
				
		button_Submit.setOnAction((_) ->
			{ControllerDiscussionBoard.newReply(currentPost.getPostID(),
				textArea_ReplyContent.getText(), theUser.getUserName(), 0, false, 0);
				displayPost(currentPost);
			});
		button_Cancel.setOnAction((_) ->
			{displayPost(currentPost);
			});
		return vBox_ReplyForm;
		}
	
	/**********
	 * <p> Method: replyToReplyForm() </p>
	 * 
	 * <p> Description: This method populates the reply to reply VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a reply to an existing reply. </p>
	 * 
	 */
	protected static VBox replyToReplyForm(Reply reply) {
		VBox vBox_ReplyForm = new VBox(5);
		vBox_ReplyForm.setPadding(new Insets(15));
		
		Label label_Title = new Label("New Reply");
		label_Title.setFont(Font.font("Arial", 12));
		label_Title.setStyle("-fx-font-weight: bold;");
		
		TextArea textArea_ReplyContent = new TextArea();
		textArea_ReplyContent.setWrapText(true);
		textArea_ReplyContent.setPrefRowCount(12);
		
		Button button_Submit = new Button("Submit");
		Button button_Cancel = new Button("Cancel");
		HBox hBox_Buttons = new HBox(10, button_Submit, button_Cancel);
		
		vBox_ReplyForm.getChildren().addAll(
				label_Title,
				textArea_ReplyContent,
				hBox_Buttons);
				
		button_Submit.setOnAction((_) -> {
			if (ControllerDiscussionBoard.newReply(currentPost.getPostID(),
				textArea_ReplyContent.getText(), theUser.getUserName(),
				reply.getReplyID(), false, 0)) {
				theDatabase.updateHasReplies(reply.getReplyID(), true);
				reply.setHasReplies(true);
				theDatabase.updateNumReplies(reply.getReplyID(),
						reply.getNumReplies()+1);
				reply.setNumReplies(reply.getNumReplies()+1);
				displayPost(currentPost);	
			}
			});
		button_Cancel.setOnAction((_) ->
			{displayPost(currentPost);
			});
		return vBox_ReplyForm;
		}
	
	/**********
	* <p> Method: displayPost() </p>
	*
	* <p> Description: This method populates the post body Scroll Pane with the
	* full post details when the user clicks on a post card. </p>
	*
	*/
	protected static void displayPost(Post post) {
	    ViewDiscussionBoard.scrollPane_PostBody.setContent(null);
	    VBox fullPost = new VBox(5);
	    fullPost.setPadding(new Insets(10));
	    fullPost.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: #E0E0E0;" +
	        "-fx-border-radius: 8;" +
	        "-fx-background-radius: 8;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    );

	    // soft deleted
	    if (post.getIsDeleted()) {
	        Label deletedMsg = new Label("Original post has been deleted");
	        deletedMsg.setStyle("-fx-font-size: 16px; -fx-text-fill: #BF0D3E; -fx-font-weight: bold;");
	        fullPost.getChildren().add(deletedMsg);
	    }
	    else {
	    	Label title = new Label(post.getTitle());
	    	title.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 18px;");
		
	    	Label author = new Label("Author: " + post.getAuthorUsername());
	    	Label thread = new Label("Thread: " + post.getThread());
		
	    	TextArea body = new TextArea(post.getBody());
	    	body.setPrefHeight(100);
			body.setWrapText(true);
			body.setEditable(false);
		
			Button button_Reply = new Button("Reply");
			button_Reply.setOnAction((_) ->
				{onReplyForm = true;
				displayPost(post);
			});
		
		fullPost.getChildren().addAll(
				title,
				author,
				thread,
				body,
				button_Reply
				);
	    }
		
		if (onReplyForm == true) {
			VBox replyForm = newReplyForm();
			fullPost.getChildren().add(replyForm);
			onReplyForm = false;
		}
		
		List<Reply> replies = ControllerDiscussionBoard.replyList.getAllReplies();
		for (Reply reply : replies) {
			if (reply.getPostID() == currentPost.getPostID() && reply.getParentReplyID() == 0) {
				fullPost.getChildren().add(displayReply(reply));
			}
		}
		
		ViewDiscussionBoard.scrollPane_PostBody.setContent(fullPost);
	} 

	
	/**********
	 * <p> Method: displayReply() </p>
	 * 
	 * <p> Description: This method populates the post body Scroll Pane with the
	 * replies from the currently selected post. </p>
	 * 
	 * @param reply the Reply object to display
	 * @return a VBOX containing the reply author, body, and reply button
	 * 
	 */
	protected static VBox displayReply(Reply reply) {
		VBox viewReply = new VBox(5);
		viewReply.setPadding(new Insets(15));
		
		Label author = new Label(reply.getAuthorUsername() + " says:");
		author.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;");
		
		TextArea body = new TextArea(reply.getBody());
		body.setPrefHeight(100);
		body.setWrapText(true);
		body.setEditable(false);
		
		Button button_Reply = new Button("Reply");
		button_Reply.setOnAction((_) -> {
			VBox replyToReply = new VBox(replyToReplyForm(reply));
			viewReply.getChildren().add(replyToReply);
		});
		
		if (!reply.getHasReplies()) {
			viewReply.getChildren().addAll(
				author,
				body,
				button_Reply
				);
		}
		else {
			VBox childReplies = new VBox(5);
			childReplies.setPadding(new Insets(15));
			
			for (Reply tempReply : ControllerDiscussionBoard.replyList.getAllReplies()) {
				if (tempReply.getParentReplyID() == reply.getReplyID()) {
					childReplies.getChildren().add(displayReplyToReply(tempReply));
				}
			}
			
			TitledPane childRepliesPane = new TitledPane(("View replies to " + reply.getAuthorUsername()
			+ "  |  Number of replies: " + reply.getNumReplies()), childReplies);
			childRepliesPane.setExpanded(false);
			
			viewReply.getChildren().addAll(
				author,
				body,
				button_Reply,
				childRepliesPane
				);
		}
		return viewReply;
	}
	
	/**********
	 * <p> Method: displayReplyToReply() </p>
	 * 
	 * <p> Description: This method populates the post body Scroll Pane with the
	 * replies to replies from the currently selected post. </p>
	 * 
	 */
	protected static VBox displayReplyToReply(Reply reply) {
		VBox viewReply = new VBox(5);
		viewReply.setPadding(new Insets(15));
		
		Label author = new Label(reply.getAuthorUsername() + " says:");
		author.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;");
		
		TextArea body = new TextArea(reply.getBody());
		body.setPrefHeight(100);
		body.setWrapText(true);
		body.setEditable(false);
			
		viewReply.getChildren().addAll(
			author,
			body);
		return viewReply;	
	}
		
	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
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
			double y){
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
	
	/**********
	 * Private local method to initialize the standard fields for a Text Field
	 * 
	 * @param t     The Text Field object to be initialized
	 * @param ff    The font to be used
	 * @param f     The size of the font to be used
	 * @param w     The width of the Text Field
	 * @param p     The alignment of the text field
	 * @param x     The location from the left edge (x axis)
	 * @param y     The location from the top (y axis)
	 * @param e     Whether the text field is editable
	 */
	protected static void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x,
			double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a Scroll Pane
	 * 
	 * @param s		The Scroll Pane object to be initialized
	 * @param p		The padding of the Scroll Pane object
	 * @param w		The width of the Scroll Pane object
	 * @param h		The height of the Scroll Pane object	
	 * @param x		The x position of the Scroll Pane object
	 * @param y		The y position of the Scroll Pane object
	 * 
	 */
	protected static void setupScrollPane(ScrollPane s, double p, double w, double h, double x, double y) {
		s.setPadding(new Insets(p));
		s.setMinWidth(w);
		s.setMinHeight(h);
		s.setMaxHeight(h);
		s.setLayoutX(x);
		s.setLayoutY(y);
	}
}