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
	
	private static double width = 1200;
	private static double height = 900;

	// A list of post objects that will be populated from the database
	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// Area 2: This contains the button to create a new post, a button for the student to view only
	// their posts, and contains a list of threads for the student to select from
	protected static Button button_NewPost = new Button("New Post");
	protected static Button button_MyPosts = new Button("My Posts");
	protected static String selectedThread = "";
	protected static Label thread_Header = new Label ("Threads");
	protected static Label thread_General = new Label ("General");
	protected static Label thread_Homework = new Label ("Homework");
	protected static Label thread_Quizzes = new Label ("Quizzes");
	
	// Area 3: This shows the student a list of the subject lines of each post in
	// the selected thread
	protected static VBox subjectList = new VBox(10);
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(subjectList);
	// Keeps track of the currently selected post
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
	
	// GUI Area 5: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

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
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Discussion Board Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, width-200, 45);
		button_UpdateThisUser.setOnAction((_) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2
		setupButtonUI(button_NewPost, "Dialog", 16, 50, Pos.CENTER, 20, 130);
		button_NewPost.setOnAction((_) ->
			{newPostForm(); });
		
		setupButtonUI(button_MyPosts, "Dialog", 16, 50, Pos.CENTER, 20, button_NewPost.getLayoutY()+100);
		button_MyPosts.setOnAction((_) ->
			{selectedThread = "My Posts";
			displayPostCards(ControllerDiscussionBoard.postList.getAllPosts());});
		
		setupLabelUI(thread_Header, "Arial", 14, 50, Pos.BASELINE_LEFT, 20, button_MyPosts.getLayoutY()+80);
		thread_Header.setStyle("-fx-underline: true;");
		
		setupLabelUI(thread_General, "Arial", 20, 50, Pos.BASELINE_LEFT, 20, thread_Header.getLayoutY()+22);
		thread_General.setStyle("-fx-text-fill: blue;");
		thread_General.setCursor(Cursor.HAND);
		thread_General.setOnMouseClicked((_) ->
			{selectedThread = "General";
			displayPostCards(ControllerDiscussionBoard.postList.getAllPosts());});
		
		setupLabelUI(thread_Homework, "Arial", 20, 50, Pos.BASELINE_LEFT, 20, thread_General.getLayoutY()+30);
		thread_Homework.setStyle("-fx-text-fill: Orange;");
		thread_Homework.setCursor(Cursor.HAND);
		thread_Homework.setOnMouseClicked((_) ->
		{selectedThread = "Homework";
		displayPostCards(ControllerDiscussionBoard.postList.getAllPosts());});
		
		setupLabelUI(thread_Quizzes, "Arial", 20, 50, Pos.BASELINE_LEFT, 20, thread_Homework.getLayoutY()+30);
		thread_Quizzes.setStyle("-fx-text-fill: red;");
		thread_Quizzes.setCursor(Cursor.HAND);
		thread_Quizzes.setOnMouseClicked((_) ->
		{selectedThread = "Quizzes";
		displayPostCards(ControllerDiscussionBoard.postList.getAllPosts());});
		
		// GUI Area 3
		setupScrollPane(scrollPane_PostCards, 10, 350, 700, width-1025, height-780);
		
		// GUI Area 4
		setupScrollPane(scrollPane_PostBody, 0, 575, 700, (scrollPane_PostCards.getLayoutX() +
				scrollPane_PostCards.getMinWidth() + 20), height-780);
		
		// GUI Area 5	
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, height-45);
		button_Return.setOnAction((_) -> {ControllerDiscussionBoard.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, height-45);
		button_Logout.setOnAction((_) -> {ControllerDiscussionBoard.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, height-45);
		button_Quit.setOnAction((_) -> {ControllerDiscussionBoard.performQuit(); });
		
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
		
		if (selectedThread == "General") {
			for (Post post : postObjects) {
				if (post.getThread().equals("General")) {
					newPostList.add(post);
				}
			}
		}
		else if (selectedThread == "Homework") {
			for (Post post : postObjects) {
				if (post.getThread().equals("Homework")) {
					newPostList.add(post);
				}
			}
		}
		else if (selectedThread == "Quizzes") {
			for (Post post : postObjects) {
				if (post.getThread().equals("Quizzes")) {
					newPostList.add(post);
				}
			}
		}
		else if (selectedThread == "My Posts") {
			for (Post post : postObjects) {
				if (post.getAuthorUsername().equals(theUser.getUserName())) {
					newPostList.add(post);
				}
			}
		}
		else {
			newPostList = postObjects;
		}
		
		VBox postCards = new VBox(10);
		
		for (Post post : newPostList) {
			VBox postCard = ControllerDiscussionBoard.createPostCard(post);
			postCards.getChildren().add(postCard);
		}
		
		scrollPane_PostCards.setContent(postCards);
		selectedThread = "";
	}
	
	/**********
	 * <p> Method: newPostForm() </p>
	 * 
	 * <p> Description: This method populates the new post VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a post. </p>
	 * 
	 */
	protected static void newPostForm() {
		VBox vBox_PostForm = new VBox(10);
		
		Label label_Header = new Label("New Post");
		label_Header.setFont(Font.font("Arial", 14));
		label_Header.setStyle("-fx-font-weight: bold;");
		
		Label label_Title = new Label("Title:");
		TextField textField_Title = new TextField();
		
		Label label_Thread = new Label("Thread:");
		ComboBox<String> comboBox_Thread = new ComboBox<>();
		comboBox_Thread.getItems().addAll("General", "Homework", "Quizzes");
		comboBox_Thread.setPromptText("<Select Thread>");
		
		TextArea textArea_Body = new TextArea();
		textArea_Body.setWrapText(true);
		textArea_Body.setPrefRowCount(12);
		
		Button button_Submit = new Button("Submit");
		Button button_Cancel = new Button("Cancel");
		HBox hBox_Buttons = new HBox(10, button_Submit, button_Cancel);
		
		vBox_PostForm.getChildren().addAll(
				label_Header,
				label_Title,
				textField_Title,
				label_Thread,
				comboBox_Thread,
				textArea_Body,
				hBox_Buttons);
		
		ViewDiscussionBoard.scrollPane_PostBody.setContent(vBox_PostForm);
		
		button_Submit.setOnAction((_) ->
			{ControllerDiscussionBoard.newPost(textField_Title.getText(), textArea_Body.getText(), theUser.getUserName(),
					comboBox_Thread.getValue());
			});
		button_Cancel.setOnAction((_) ->
			{scrollPane_PostBody.setContent(null);});
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
				textArea_ReplyContent.getText(), theUser.getUserName());
				displayPost(currentPost);
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
		
		if (onReplyForm == true) {
			VBox replyForm = newReplyForm();
			fullPost.getChildren().add(replyForm);
			onReplyForm = false;
		}
		
		List<Reply> replies = ControllerDiscussionBoard.replyList.getAllReplies();
		for (Reply reply : replies) {
			if (reply.getPostID() == currentPost.getPostID()) {
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
		author.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 12px;");
		
		TextArea body = new TextArea(reply.getBody());
		body.setPrefHeight(100);
		body.setWrapText(true);
		body.setEditable(false);
		
		viewReply.getChildren().addAll(
				author,
				body
				);
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