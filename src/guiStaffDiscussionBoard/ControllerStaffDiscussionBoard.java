package guiStaffDiscussionBoard;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.Reply;
import entityClasses.ReplyList;
import guiDiscussionBoard.ControllerDiscussionBoard;


/*******
 * <p> Title: ControllerStaffDiscussionBoard Class. </p>
 *
 * <p> Description: The Java/FX-based Staff Discussion Board page controller. This class provides
 * the controller actions for the Staff Discussion Board page including loading posts and replies
 * from the database, creating post cards, handling reply creation, and navigation. It also
 * enables the ability to perform the Staff functions of giving feedback to students' posts and
 * flagging inappropriate posts.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-17 Initial version
 *
 */
public class ControllerStaffDiscussionBoard {
	
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
	// Tracks the currently selected post for use in displayPost() and newReplyForm()
	protected static Post currentPost = new Post();
	// Tracks whether the reply form is currently shown, and changes the post body display
	protected static boolean onReplyForm = false;
	
	/**
	 * Default constructor is not used.
	 */
	
	public ControllerStaffDiscussionBoard() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: Clears and rebuilds the Discussion Board page, reloading
	 * all posts and replies from the database to ensure newly created content
	 * appears immediately. </p>
	 *
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewStaffDiscussionBoard.theRootPane.getChildren().clear();
		ViewStaffDiscussionBoard.theRootPane.getChildren().addAll(
				ViewStaffDiscussionBoard.label_PageTitle, 
				ViewStaffDiscussionBoard.label_UserDetails,
				ViewStaffDiscussionBoard.button_MyPosts,
				ViewStaffDiscussionBoard.textfield_Search,
		        ViewStaffDiscussionBoard.button_Search,
		        ViewStaffDiscussionBoard.button_CreatePost,
		        ViewStaffDiscussionBoard.button_General,
		        ViewStaffDiscussionBoard.button_Homework,
		        ViewStaffDiscussionBoard.button_Quizzes,
		        ViewStaffDiscussionBoard.button_ViewAll,
		        ViewStaffDiscussionBoard.scrollPane_PostCards,
		        ViewStaffDiscussionBoard.scrollPane_PostBody,
		        ViewStaffDiscussionBoard.line_Separator4, 
		        ViewStaffDiscussionBoard.button_Home,
		        ViewStaffDiscussionBoard.button_Logout,
		        ViewStaffDiscussionBoard.button_Quit,
		        ViewStaffDiscussionBoard.label_Subtitle);
		
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
				e.printStackTrace();
			}
		}
		// Set the title for the window
		ViewStaffDiscussionBoard.theStage.setTitle("Discussion Board");
		ViewStaffDiscussionBoard.theStage.setScene(ViewStaffDiscussionBoard.theStaffDiscussionBoardScene);
		ViewStaffDiscussionBoard.theStage.show();
		ViewStaffDiscussionBoard.theStage.centerOnScreen();
		displayPostCards();
	}
	
	/**********
	 * <p> Method: performSearch() </p>
	 *
	 * <p> Description: Filters the post card list by a keyword entered in the search
	 * field. Matches against post title and body. Reloads all posts if the field
	 * is empty. </p>
	 *
	 */
	protected static void performSearch() {
	    // This allows the user to search for a keyword without regard to capitalization or spaces
		String keyword = ViewStaffDiscussionBoard.textfield_Search.getText().trim().toLowerCase();
	    if (keyword.isEmpty()) {
	        repaintTheWindow();
	        return;
	    }
	    ViewStaffDiscussionBoard.postCardList.getChildren().clear();
	    ViewStaffDiscussionBoard.scrollPane_PostBody.setContent(null);
	    try {
	        List<Post> allPosts = theDatabase.getPostObjects();
	        // Defaults to false but will turn true as soon as at least one post matching the
	        // keyword is found
	        boolean found = false;
	        for (Post post : allPosts) {
	            if (!post.getIsDeleted() &&
	               (post.getTitle().toLowerCase().contains(keyword) ||
	                post.getBody().toLowerCase().contains(keyword))) {
	            	ViewStaffDiscussionBoard.postCardList.getChildren().add(createPostCard(post));
	                found = true;
	            }
	        }
	        // If no post matching the keyword is found, display an error message
	        if (!found) {
	        	ViewStaffDiscussionBoard.postCardList.getChildren().add(
	                new Label("No posts matching: " + keyword));
	        }
	    } catch (Exception e) {
	    	ViewStaffDiscussionBoard.postCardList.getChildren().add(
	            new Label("Error: " + e.getMessage()));
	    }
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
		// Remove deleted posts
	    if (post.getIsDeleted()) {
	        return null;
	    }
	    VBox postCard = new VBox(5);
	    postCard.setPadding(new Insets(10));
	    postCard.setMinWidth(ViewStaffDiscussionBoard.scrollPane_PostCards.getMinWidth()-40);
	    postCard.setMaxWidth(ViewStaffDiscussionBoard.scrollPane_PostCards.getMinWidth()-40);
	    postCard.setStyle(
	    	    "-fx-border-color: #E0E0E0;" +
	    	    "-fx-border-radius: 8;" +
	    	    "-fx-background-color: white;" +
	    	    "-fx-background-radius: 8;" +
	    	    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    	);
	    
	    // Title at top in bold
	    Label title = new Label(post.getTitle());
	    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
	    
	    // Format timestamp nicely
	    String formattedTime = "";
	    if (post.getCreatedAt() != null) {
	        formattedTime = post.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    
	    // Author and timestamp on same line
	    Label authorAndTime = new Label("by " + post.getAuthorUsername() + "  •  " + formattedTime);
	    authorAndTime.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
	    
	    // Thread in upper right using HBox
	    Label thread = new Label(post.getThread());
	    thread.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");
	    
	    javafx.scene.layout.HBox bottomRow = new javafx.scene.layout.HBox();
	    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
	    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
	    bottomRow.getChildren().addAll(authorAndTime, spacer, thread);
	    
	    postCard.getChildren().addAll(title, bottomRow);
	    postCard.setCursor(Cursor.HAND);
	    postCard.setOnMouseClicked((_) -> {
	    	currentPost = post;
	        displayPost(post);
	    });
	    
	    return postCard;
	}
	
	
	/**********
	 * <p> Method: displayPostCards() </p>
	 * 
	 * <p> Description: This method populates the post cards Scroll Pane, which shows
	 * a list of posts by subject line. The post cards visible are based on which thread has
	 * been selected. If no thread has been selected, all post cards are visible. </p>
	 * 
	 *  
	 */
	protected static void displayPostCards() {
	    List<Post> allPosts = postList.getAllPosts();
		List<Post> newPostList = new ArrayList<>();
	    
	    if (ViewStaffDiscussionBoard.selectedThread.equals("General")) {
	        for (Post post : allPosts) {
	            if (post.getThread().equals("General") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else if (ViewStaffDiscussionBoard.selectedThread.equals("Homework")) {
	        for (Post post : allPosts) {
	            if (post.getThread().equals("Homework") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else if (ViewStaffDiscussionBoard.selectedThread.equals("Quizzes")) {
	        for (Post post : allPosts) {
	            if (post.getThread().equals("Quizzes") && !post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    else {
	        for (Post post : allPosts) {
	            if (!post.getIsDeleted()) {
	                newPostList.add(post);
	            }
	        }
	    }
	    
	    ViewStaffDiscussionBoard.postCardList.getChildren().clear();
	    for (Post post : newPostList) {
	        VBox card = createPostCard(post);
	        if (card != null) {
	        	ViewStaffDiscussionBoard.postCardList.getChildren().add(card);
	        }
	    }
	}
	
	
	/**********
	* <p> Method: displayPost() </p>
	*
	* <p> Description: This method populates the post body Scroll Pane with the
	* full post details when the user clicks on a post card. </p>
	*
	* @param post the Post object to display in the post body scroll pane
	*
	*/
	protected static void displayPost(Post post) {
		ViewStaffDiscussionBoard.scrollPane_PostBody.setContent(null);
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
		
		// Checks if the Reply button has been clicked so the newReplyForm can
	    // be displayed
	    if (onReplyForm == true) {
			VBox replyForm = newReplyForm();
			fullPost.getChildren().add(replyForm);
			// Sets onReplyFrom back to false so subsequent loading of the post
			// details will not include the newReplyForm
			onReplyForm = false;
		}
		
		List<Reply> replies = ControllerStaffDiscussionBoard.replyList.getAllReplies();
		for (Reply reply : replies) {
			// Checks if the Reply object in replies has a post ID matching the current post
			if (reply.getPostID() == currentPost.getPostID() && reply.getParentReplyID() == 0) {
				fullPost.getChildren().add(displayReply(reply));
			}
		}
		
		ViewStaffDiscussionBoard.scrollPane_PostBody.setContent(fullPost);
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
			{newReply(currentPost.getPostID(),
				textArea_ReplyContent.getText(), ViewStaffDiscussionBoard.theUser.getUserName(), 0, false, 0);
			repaintTheWindow();	
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
	 * @param reply the Reply object that is being replied to
	 * 
	 * @return a VBox containing the reply-to-reply form with text area and submit/cancel buttons
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
			if (newReply(currentPost.getPostID(),
				textArea_ReplyContent.getText(), ViewStaffDiscussionBoard.theUser.getUserName(),
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
	 * <p> Method: newReply() </p>
	 * 
	 * <p> Description: This creates a Reply object and then passes that Reply object
	 * to the createReply method in the database. </p>
	 * 
	 * @param postID the ID of the post being replied to
	 * @param body the text content of the reply
	 * @param authorUsername the username of the reply author
	 * @param parentReplyID the ID of the parent reply if replying to a reply, 0 if top-level
	 * @param hasReplies whether this reply has child replies
	 * @param numReplies the number of child replies
	 * 
	 * @return true if reply was created successfully, false otherwise
	 * 
	 */
	protected static boolean newReply(int postID, String body, String authorUsername, int parentReplyID, 
			boolean hasReplies, int numReplies) {
	    String errMsg = recognizers.PostReplyValidator.checkForValidReply(body);
	    if (!errMsg.isEmpty()) {
	        Alert alertError = new Alert(Alert.AlertType.INFORMATION);
	        alertError.setTitle("Error");
	        alertError.setHeaderText(errMsg);
	        alertError.showAndWait();
	        return false;
	    }
	    
	    Reply reply = new Reply(postID, body, authorUsername);
	    reply.setparentReplyID(parentReplyID);
	    reply.setHasReplies(hasReplies);
	    reply.setNumReplies(numReplies);
	    
	    try {
	        theDatabase.createReply(reply);
	    } catch (SQLException e) {
	        reply = null;
	    	e.printStackTrace();
	        String error = e.toString();
	        Alert alertError = new Alert(Alert.AlertType.INFORMATION);
	        alertError.setTitle("Error");
	        alertError.setHeaderText("Error creating the reply in the database.");
	        alertError.setContentText(error);
	        alertError.showAndWait();
	        return false;
	    }
	    
	    replyList.addReply(reply);
	    Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
	    alertSuccess.setContentText("Reply successfully created!");
	    alertSuccess.showAndWait();
	    repaintTheWindow();
	    displayPost(currentPost);
	    return true;
	}
	
	
	/**********
	 * <p> Method: displayReply() </p>
	 * 
	 * <p> Description: This method populates the post body Scroll Pane with the
	 * replies from the currently selected post. </p>
	 * 
	 * @param reply the Reply object to display
	 * 
	 * @return a VBox containing the reply author, body, and reply button
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
		
		// If the reply has child replies, nest those child replies under the
		// parent reply. Only replies to the post are allowed to have child
		// replies. No child replies shall have child replies.
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
	 * @param reply the Reply object to display
	 * 
	 * @return a VBox containing the reply author and body
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
	
	
	/**********
	 * <p> Method: performHome() </p>
	 * 
	 * <p> Description: This method returns the user to the student home page </p>
	 * 
	 */
	protected static void performHome() {
		guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffDiscussionBoard.theStage,
				ViewStaffDiscussionBoard.theUser);
		currentPost = new Post();
		ViewStaffDiscussionBoard.scrollPane_PostBody.setContent(null);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffDiscussionBoard.theStage);
		currentPost = new Post();
		ViewStaffDiscussionBoard.scrollPane_PostBody.setContent(null);
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