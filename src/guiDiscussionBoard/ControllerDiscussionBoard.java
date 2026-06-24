package guiDiscussionBoard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.Reply;
import entityClasses.ReplyList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/*******
 * <p> Title: ControllerDiscussionBoard Class. </p>
 * 
 * <p> Description: The Java/FX-based Discussion Board page controller. This class provides
 * the controller actions for the Discussion Board page including loading posts and replies
 * from the database, creating post cards, handling reply creation, and navigation.</p>
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Pete Echavarria © 2026 </p>
 * 
 * @author Pete Echavarria
 * 
 * @version 1.00		2026-06-02 Initial version
 */

public class ControllerDiscussionBoard {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	// A list of post objects that will be populated from the database
	public static PostList postList = new PostList();
	// A list of reply objects that will be populated from the database
	public static ReplyList replyList = new ReplyList();
	
	/**
	 * Default constructor is not used.
	 */
	
	public ControllerDiscussionBoard() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDiscussionBoard.theRootPane.getChildren().clear();
		
		ViewDiscussionBoard.theRootPane.getChildren().addAll(
				ViewDiscussionBoard.label_PageTitle, ViewDiscussionBoard.label_UserDetails,
				ViewDiscussionBoard.button_UpdateThisUser, ViewDiscussionBoard.line_Separator1,
				ViewDiscussionBoard.button_NewPost,
				ViewDiscussionBoard.button_MyPosts,
				ViewDiscussionBoard.button_RelatedPosts,
				ViewDiscussionBoard.thread_Header,
				ViewDiscussionBoard.thread_General,
				ViewDiscussionBoard.thread_Homework,
				ViewDiscussionBoard.thread_Quizzes,
				ViewDiscussionBoard.scrollPane_PostCards,
				ViewDiscussionBoard.scrollPane_PostBody,
				ViewDiscussionBoard.line_Separator4, 
				ViewDiscussionBoard.button_Return,
				ViewDiscussionBoard.button_Logout,
				ViewDiscussionBoard.button_Quit);
		
		// Always reload posts from the database to ensure newly created posts appear immediately
		List<Post> posts = new ArrayList<>();
		try {
		    postList = new PostList();
		    posts = theDatabase.getPostObjects();
		    for (Post post : posts) {
		        postList.addPost(post);
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	
		// If replyList is empty, creates a ReplyList by calling the getReplyObjects method 
		// from the database
		List<Reply> replies = new ArrayList<>();
		if (!replyList.getAllReplies().isEmpty()) {
			replies = replyList.getAllReplies();
		}
		else {
			try {
				replies = theDatabase.getReplyObjects();
				for (Reply reply : replies) {
					replyList.addReply(reply);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Set the title for the window
		ViewDiscussionBoard.theStage.setTitle("Discussion Board");
		ViewDiscussionBoard.theStage.setScene(ViewDiscussionBoard.theDiscussionBoardScene);
		ViewDiscussionBoard.theStage.show();
		ViewDiscussionBoard.theStage.centerOnScreen();
		ViewDiscussionBoard.displayPostCards(posts);
	}
	
	
	/**********
	 * <p> Method: createPostCard() </p>
	 * 
	 * <p> Description: This method creates post cards for the postList.
	 * The card contains information about a post, including the title and author
	 * so a user can decide which post to view. They can then click on the card to 
	 * view the full post. </p>
	 * 
	 * @param post the Post object from which to create the post card from
	 * 
	 */
	protected static VBox createPostCard(Post post) {
		VBox postCard = new VBox(5);
		postCard.setPadding(new Insets(10));
		postCard.setMinWidth(ViewDiscussionBoard.scrollPane_PostCards.getMinWidth()-20);
		postCard.setStyle(
				"-fx-border-color: lightgray;" +
				"-fx-border-radius: 5;" +
				"-fx-background-color: white;" +
				"-fx-background-radius: 5;"
				);
		
		Label title = new Label(post.getTitle());
		title.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;");
		Label author = new Label("Author: " + post.getAuthorUsername());
		Label thread = new Label("Thread: " + post.getThread());
		Label timestamp = new Label(post.getCreatedAt() != null ? 
			    post.getCreatedAt().toString() : "");
			postCard.getChildren().add(timestamp);
		
		postCard.getChildren().addAll(title, author, thread);
		postCard.setCursor(Cursor.HAND);
		postCard.setOnMouseClicked((_) ->
		{ViewDiscussionBoard.currentPost = post;
		ViewDiscussionBoard.displayPost(post);});
		
		return postCard;
	}
	
	/**********
	 * <p> Method: newReply() </p>
	 * 
	 * <p> Description: This creates a Reply object and then passes that Rely object
	 * to the createReply method in the database. </p>
	 * 
	 */
	protected static void newReply(int postID, String body, String authorUsername) {
	    String errMsg = recognizers.PostReplyValidator.checkForValidReply(body);
	    if (!errMsg.isEmpty()) {
	        Alert alertError = new Alert(Alert.AlertType.INFORMATION);
	        alertError.setTitle("Error");
	        alertError.setHeaderText(errMsg);
	        alertError.showAndWait();
	        return;
	    }
	    
	    Reply reply = new Reply(postID, body, authorUsername);
	    
	    try {
	        theDatabase.createReply(reply);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    replyList.addReply(reply);
	    Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
	    alertSuccess.setContentText("Reply successfully created!");
	    alertSuccess.showAndWait();
	    ViewDiscussionBoard.scrollPane_PostBody.setContent(null);
	    repaintTheWindow();
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiStudentHome.ViewStudentHome.displayStudentHome(ViewDiscussionBoard.theStage,
				ViewDiscussionBoard.theUser);
		ViewDiscussionBoard.currentPost = new Post();
		ViewDiscussionBoard.scrollPane_PostBody.setContent(null);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDiscussionBoard.theStage);
		ViewDiscussionBoard.currentPost = new Post();
		ViewDiscussionBoard.scrollPane_PostBody.setContent(null);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}