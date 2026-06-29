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
 * <p> Description: The Java/FX-based page for viewing the class discussion board.</p>
 * 
 * @author Pete Echavarria
 * 
 * @version 1.00		2026-06-14 Initial version
 *  
 */

public class ViewDiscussionBoard {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = 1000;
	private static double height = 900;

	// A list of post objects that will be populated from the database
	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Label label_Subtitle = new Label("Your class forum");
	// It is used for quitting the application, logging
	// out, and returning to home. Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static String selectedThread = "";
	protected static Button button_Home = new Button("Home");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("X");
	protected static Button button_MyPosts = new Button("My Posts");
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// Area 2: This contains the button to create a new post and contains a list of 
	//threads for the student to select from

	protected static Button button_General = new Button ("General");
	protected static Button button_Homework = new Button ("Homework");
	protected static Button button_Quizzes = new Button("Quizzes");
	protected static Button button_ViewAll = new Button ("View All");
	
	
	//  This is the search bar and submit and Create post
	protected static TextField textfield_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static Button button_CreatePost = new Button("+");
	protected static VBox postCardList = new VBox(10);
	
	// Area 3: This shows the student a list of the subject lines of each post in
	// the selected thread
	protected static VBox subjectList = new VBox(10);
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(postCardList);
	// Keeps track of the currently selected post for use in displayPost and
	// newReplyForm methods
	protected static Post currentPost = new Post();
	// Area 4: This shows a list of the body of the selected post and all replies
	protected static VBox newPost = new VBox();
	protected static VBox userPosts = new VBox(10);
	protected static ScrollPane scrollPane_PostBody = new ScrollPane();
	// Keeps track of whether the user is creating a reply. This will change how
	// Posts and Replies are displayed in the scroolPane_PostBody
	protected static boolean onReplyForm = false;
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, height-60, width-20, height-60);
	

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with user posts
	private static ViewDiscussionBoard theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;						// The Stage that JavaFX has established for us
	protected static Pane theRootPane;						// The Pane that holds all the GUI widgets 
	protected static User theUser;							// The current user of the application
		
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
		
		label_PageTitle.setText("Discussion Board");
		setupLabelUI(label_PageTitle, "Arial", 40, 400, Pos.BASELINE_LEFT, 20, 45);
		
		setupLabelUI(label_Subtitle, "Arial", 24, 400, Pos.BASELINE_LEFT, 23, 100);
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		
		setupButtonUI(button_MyPosts, "Dialog", 12, 70, Pos.CENTER, 772, 10);
		button_MyPosts.setOnAction((_) ->
		    {guiMyPosts.ViewMyPosts.displayMyPosts(theStage, theUser);});
		
		setupButtonUI(button_Home, "Dialog", 12, 52, Pos.CENTER, 845, 10);
		button_Home.setOnAction((_) -> {ControllerDiscussionBoard.performHome(); });

		setupButtonUI(button_Logout, "Dialog", 12, 57, Pos.CENTER, 900, 10);
		button_Logout.setOnAction((_) -> {ControllerDiscussionBoard.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
		button_Quit.setOnAction((_) -> {ControllerDiscussionBoard.performQuit(); });
		
		button_MyPosts.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Home.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Logout.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		
		// GUI Area 2
		
		// Search row
		setupTextUI(textfield_Search, "Arial", 14, 250, Pos.BASELINE_LEFT, 20, 190, true);

		setupButtonUI(button_Search, "Dialog", 14, 75, Pos.CENTER, 265, 190);
		button_Search.setOnAction((_) -> { ControllerDiscussionBoard.performSearch(); });

		setupButtonUI(button_CreatePost, "Dialog", 14, 30, Pos.CENTER, 340, 190);
		button_CreatePost.setOnAction((_) -> { guiRelatedPosts.ViewRelatedPosts.displayRelatedPosts(theStage, theUser); });
		
		setupButtonUI(button_General, "Dialog", 13, 80, Pos.CENTER, 20, 220);
		button_General.setOnAction((_) -> { selectedThread = "General"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });

		setupButtonUI(button_Homework, "Dialog", 13, 80, Pos.CENTER, 110, 220);
		button_Homework.setOnAction((_) -> { selectedThread = "Homework"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });

		setupButtonUI(button_Quizzes, "Dialog", 13, 80, Pos.CENTER, 200, 220);
		button_Quizzes.setOnAction((_) -> { selectedThread = "Quizzes"; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });

		setupButtonUI(button_ViewAll, "Dialog", 13, 80, Pos.CENTER, 290, 220);
		button_ViewAll.setOnAction((_) -> { selectedThread = ""; displayPostCards(ControllerDiscussionBoard.postList.getAllPosts()); });
		
		
		// GUI Area 3
		setupScrollPane(scrollPane_PostCards, 10, 400, 460, 20, 258);
		
		// GUI Area 4
		setupScrollPane(scrollPane_PostBody, 0, 550, 460, 435, 258);
		
		
		label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");
		label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		label_UserDetails.setStyle("-fx-text-fill: #666666;");
		
		// This is the end of the GUI Widgets for the page
	}	
	
	/**********
	 * <p> Method: displayPostCards() </p>
	 * 
	 * <p> Description: This method populates the post cards Scroll Pane with which shows
	 * a list of posts by subject line. The post cards visible is based on which thread has
	 * been selected. If no thread has been selected, all post cards are visible. </p>
	 * 
	 */
	protected static void displayPostCards(List<Post> postObjects) {
		List<Post> newPostList = new ArrayList<>();
		
		if (selectedThread.equals("General")) {
			for (Post post : postObjects) {
				if (post.getThread().equals("General")) {
					newPostList.add(post);
				}
			}
		}
		else if (selectedThread.equals("Homework")) {
			for (Post post : postObjects) {
				if (post.getThread().equals("Homework")) {
					newPostList.add(post);
				}
			}
		}
		else if (selectedThread.equals("Quizzes")) {
			for (Post post : postObjects) {
				if (post.getThread().equals("Quizzes")) {
					newPostList.add(post);
				}
			}
		}

		else {
			newPostList = postObjects;
		}
		
		postCardList.getChildren().clear();
		for (Post post : newPostList) {
		    postCardList.getChildren().add(ControllerDiscussionBoard.createPostCard(post));
		}
		selectedThread = "";
	}
	
	/**********
	 * <p> Method: newReplyForm() </p>
	 * 
	 * <p> Description: This method populates the new reply VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a reply. </p>
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
	 * contains all necessary fields for the user to create a reply. </p>
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
				reply.getReplyID(), false, reply.getNumReplies())) {
			theDatabase.updateHasReplies(reply.getReplyID(), true);
			reply.setHasReplies(true);
			theDatabase.updateNumReplies(reply.getReplyID(), reply.getNumReplies()+1);
			reply.setNumReplies(reply.getNumReplies()+1);
			displayPost(currentPost);
			}
			});
		button_Cancel.setOnAction((_) -> {
			displayPost(currentPost);
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
	 * @param c		The Text Field object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Text Field
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
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