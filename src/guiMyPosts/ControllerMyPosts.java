package guiMyPosts;
import java.sql.SQLException;
import java.util.List;
import database.Database;
import entityClasses.Reply;
import guiDiscussionBoard.ViewDiscussionBoard;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorInput;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/*******
 * <p> Title: ControllerMyPosts Class. </p>
 * 
 * <p> Description: The Java/FX-based My Posts page.  This class provides the controller
 * actions for the My Posts page including ability for a student to view a list of all posts they have made, search through posts, and toggle
 * between read and unread replies.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * @version 1.00		2026-06-22 Initial version
 */

public class ControllerMyPosts {
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	
	// boolean to track toggle state
	private static boolean showingUnreadOnly = false;
	
	public ControllerMyPosts() {}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	/**********
	 * <p> Method: loadMyPosts() </p>
	 * 
	 * <p> Description:  Retrieves all posts by the curent user from the database and creates
	 * a post card for each, and populates the post card scroll pane. Displays a message
	 * if no posts exist.</p>
	 * 
	 */
	protected static void loadMyPosts() {
	    ViewMyPosts.postCardList.getChildren().clear();
	    ViewMyPosts.scrollPane_PostBody.setContent(null);

	    String currentUsername = ViewMyPosts.theUser.getUserName();

	    try {
	        List<entityClasses.Post> allPosts = theDatabase.getPostObjects();
	        boolean hasPosts = false;

	        for (entityClasses.Post post : allPosts) {
	            if (post.getAuthorUsername().equals(currentUsername) && !post.getIsDeleted()) {
	            	// Thread filtering
	                if (ViewMyPosts.selectedThread.isEmpty() ||
	                    post.getThread().equals(ViewMyPosts.selectedThread)) {
	                    int replyCount = theDatabase.getReplyCount(post.getPostID());
	                    int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	                    ViewMyPosts.postCardList.getChildren().add(createPostCard(post, replyCount, unreadCount));
	                    hasPosts = true;
	                }
	            }
	        }

	        if (!hasPosts) {
	            javafx.scene.control.Label empty = new javafx.scene.control.Label("No posts yet.");
	            ViewMyPosts.postCardList.getChildren().add(empty);
	        }

	    } catch (Exception e) {
	        javafx.scene.control.Label err = new javafx.scene.control.Label("Error loading posts: " + e.getMessage());
	        ViewMyPosts.postCardList.getChildren().add(err);
	    }
	}
	
	
	/**********
	 * <p> Method: performToggleUnread() </p>
	 * 
	 * <p> Description: Allows toggle in the post card list between showing all posts and showing
	 * only posts with unread replies.</p>
	 * 
	 */
	protected static void performToggleUnread() {
	    showingUnreadOnly = !showingUnreadOnly;
	    
	    if (showingUnreadOnly) {
	        ViewMyPosts.button_ToggleUnread.setText("All");
	    } else {
	        ViewMyPosts.button_ToggleUnread.setText("Unread");
	    }
	    
	    ViewMyPosts.postCardList.getChildren().clear();
	    ViewMyPosts.scrollPane_PostBody.setContent(null);
	    String currentUsername = ViewMyPosts.theUser.getUserName();
	    
	    try {
	        List<entityClasses.Post> allPosts = theDatabase.getPostObjects();
	        boolean hasPosts = false;
	        
	        for (entityClasses.Post post : allPosts) {
	            if (post.getAuthorUsername().equals(currentUsername) && !post.getIsDeleted()) {
	                int replyCount = theDatabase.getReplyCount(post.getPostID());
	                int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	                
	                if (showingUnreadOnly && unreadCount == 0) continue;
	                
	                ViewMyPosts.postCardList.getChildren().add(createPostCard(post, replyCount, unreadCount));
	                hasPosts = true;
	            }
	        }
	        
	        if (!hasPosts) {
	            javafx.scene.control.Label empty = new javafx.scene.control.Label(
	                showingUnreadOnly ? "No unread replies." : "No posts yet.");
	            ViewMyPosts.postCardList.getChildren().add(empty);
	        }
	        
	    } catch (Exception e) {
	        javafx.scene.control.Label err = new javafx.scene.control.Label("Error: " + e.getMessage());
	        ViewMyPosts.postCardList.getChildren().add(err);
	    }
	    if (ViewMyPosts.currentPost != null && ViewMyPosts.currentPost.getPostID() != 0) {
	        displayPostBody(ViewMyPosts.currentPost);
	    }
	}
	
	
	
	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Fileters the current user's post by the keyboard entered into the search field.
	 * Matches against post title and body, it reloads all posts if the field field is empty.</p>
	 * 
	 */
	protected static void performSearch() {
	    String keyword = ViewMyPosts.textfield_Search.getText().trim();
	    
	    if (keyword.isEmpty()) {
	        loadMyPosts();
	        return;
	    }
	    
	    ViewMyPosts.postCardList.getChildren().clear();
	    ViewMyPosts.scrollPane_PostBody.setContent(null);
	    String currentUsername = ViewMyPosts.theUser.getUserName();
	    
	    try {
	        List<entityClasses.Post> allPosts = theDatabase.getPostObjects();
	        boolean hasPosts = false;
	        
	        for (entityClasses.Post post : allPosts) {
	            if (post.getAuthorUsername().equals(currentUsername) && !post.getIsDeleted()) {
	                if (post.getTitle().contains(keyword) || post.getBody().contains(keyword)) {
	                    int replyCount = theDatabase.getReplyCount(post.getPostID());
	                    int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	                    ViewMyPosts.postCardList.getChildren().add(createPostCard(post, replyCount, unreadCount));
	                    hasPosts = true;
	                }
	            }
	        }
	        
	        if (!hasPosts) {
	            javafx.scene.control.Label empty = new javafx.scene.control.Label("No posts matching: " + keyword);
	            ViewMyPosts.postCardList.getChildren().add(empty);
	        }
	        
	    } catch (Exception e) {
	        javafx.scene.control.Label err = new javafx.scene.control.Label("Error: " + e.getMessage());
	        ViewMyPosts.postCardList.getChildren().add(err);
	    }
	}
	
	/**********
	 * <p> Method: searchReplies() </p>
	 * 
	 * <p> Description: Searches all posts from the current user to find replies that match a specific
	 * username and/or keyword. </p>
	 * 
	 */
	protected static void searchReplies() {
	    String keyword = ViewMyPosts.textfield_Search.getText().trim().toLowerCase();
	    String username = ViewMyPosts.combobox_SelectUser.getValue();
	    ViewMyPosts.textfield_Search.clear();
	    ViewMyPosts.combobox_SelectUser.getSelectionModel().select(0);
	    int foundReplies = 0;
	    List<entityClasses.Post> myPosts = new java.util.ArrayList<>();
	    List<entityClasses.Reply> myReplies = new java.util.ArrayList<>();
	    List<entityClasses.Reply> searchedReplies = new java.util.ArrayList<>();
	    
	    if (keyword.isEmpty() && username.equals("User")) {
	        loadMyPosts();
	        return;
	    }
	    
	    ViewMyPosts.postCardList.getChildren().clear();
	    ViewMyPosts.scrollPane_PostBody.setContent(null);
	    String currentUsername = ViewMyPosts.theUser.getUserName();
	    
	    try {
			List<entityClasses.Post> allPosts = theDatabase.getPostObjects();
			List<entityClasses.Reply> allReplies = theDatabase.getReplyObjects(); 
			// Searches all posts for posts that are created by the current user
			// then adds them to the myPosts list
			for (entityClasses.Post post : allPosts) {
				if (post.getAuthorUsername().equals(currentUsername)) {
					myPosts.add(post);
				}
			}
			// Searches all replies for replies that are made to a post created by
			// the current user then adds them to the myReplies list
			for (entityClasses.Reply reply : allReplies) {
				for (entityClasses.Post post : myPosts) {
					if (reply.getPostID() == post.getPostID()) {
						myReplies.add(reply);
					}
				}
			}
		} catch (SQLException e) {
			javafx.scene.control.Label err = new javafx.scene.control.Label("Error: " + e.getMessage());
	        ViewMyPosts.postCardList.getChildren().add(err);
		}
	    
	    for (entityClasses.Reply reply : myReplies) {
	    	if (!username.equals("<User>") && !keyword.isEmpty()) {
	    		if (reply.getAuthorUsername().equals(username) && reply.getBody().toLowerCase().contains(keyword)) {
	    			searchedReplies.add(reply);
	    			foundReplies++;
	    		}
	    	}
	    	if (username.equals("<User>") && !keyword.isEmpty()) {
	    		if (reply.getBody().toLowerCase().contains(keyword)) {
	    			searchedReplies.add(reply);
	    			foundReplies++;
	    		}
	    	}
	    	if (!username.equals("<User>") && keyword.isEmpty()) {
	    		if (reply.getAuthorUsername().equals(username)) {
	    			searchedReplies.add(reply);
	    			foundReplies++;
	    		}
	    	}
	    }
	    
	    
	    if (foundReplies == 0) {
	    	if (!username.equals("<User>") && !keyword.isEmpty()) {	    	
	    		javafx.scene.control.Label empty = new javafx.scene.control.Label("No replies from user: " +
	    		username + " and matching keyword: " + '"' + keyword + '"');
	            ViewMyPosts.postCardList.getChildren().add(empty);
	    	}
	    	if (!username.equals("<User>") && keyword.isEmpty()) {
	    		javafx.scene.control.Label empty = new javafx.scene.control.Label("No replies from user: " +
	    	    username);
	    	    ViewMyPosts.postCardList.getChildren().add(empty);
	    	}
	    	if (username.equals("<User>") && !keyword.isEmpty()) {
	    		javafx.scene.control.Label empty = new javafx.scene.control.Label("No replies matching keyword  " +
	    		'"' + keyword + '"');
	    	    ViewMyPosts.postCardList.getChildren().add(empty);
	    	}
	    }
	    else {
	    	ViewMyPosts.scrollPane_PostBody.setContent(null);
	    	VBox replyList = new VBox();
	    	for (entityClasses.Reply reply : myReplies) {
	    		for (entityClasses.Post post : myPosts) {
	    			if (post.getPostID() == reply.getPostID() && !post.getIsDeleted()) {
	    				int replyCount = theDatabase.getReplyCount(post.getPostID());
		                int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	    				ViewMyPosts.postCardList.getChildren().add(createPostCard(post, replyCount, unreadCount));
	    				javafx.scene.control.Label postTitle = new Label("From post: " + post.getTitle());
	    				postTitle.setFont( new Font("Arial", 16));
	    				postTitle.setTextFill(Color.BLUE);
	    				replyList.getChildren().add(postTitle);
	    				replyList.getChildren().add(displayReply(reply));
	    			}
	    			if (post.getPostID() == reply.getPostID() && post.getIsDeleted()) {
	    				javafx.scene.control.Label postDeleted = new javafx.scene.control.Label("Original post has " +
	    			    	"been deleted");
	    				postDeleted.setFont( new Font("Arial", 16));
	    				postDeleted.setTextFill(Color.BLUE);
	    				replyList.getChildren().add(postDeleted);
	    				replyList.getChildren().add(displayReply(reply));
	    			}
	    		}
	    	}
	    	ViewMyPosts.scrollPane_PostBody.setContent(replyList);
	    }
	}
	
	private static javafx.scene.layout.VBox createPostCard(entityClasses.Post post, int replyCount, int unreadCount) {
	    javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(5);
	    card.setPadding(new javafx.geometry.Insets(15, 10, 15, 15));
	    card.setMinWidth(ViewMyPosts.scrollPane_PostCards.getPrefWidth()-40);
	    card.setMaxWidth(ViewMyPosts.scrollPane_PostCards.getPrefWidth()-40);
	    card.setStyle(
	        "-fx-border-color: #E0E0E0;" +
	        "-fx-border-radius: 8;" +
	        "-fx-background-color: white;" +
	        "-fx-background-radius: 8;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    );
	    
	    // Title at top in bold
	    javafx.scene.control.Label title = new javafx.scene.control.Label(post.getTitle());
	    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
	    
	    // Thread in upper right
	    javafx.scene.control.Label thread = new javafx.scene.control.Label(post.getThread());
	    thread.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");
	    
	    javafx.scene.layout.HBox topRow = new javafx.scene.layout.HBox();
	    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
	    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
	    topRow.getChildren().addAll(title, spacer, thread);
	    
	    // Format timestamp
	    String formattedTime = "";
	    if (post.getCreatedAt() != null) {
	        formattedTime = post.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    
	    // Author and timestamp on same line
	    javafx.scene.control.Label authorAndTime = new javafx.scene.control.Label(
	        "by " + post.getAuthorUsername() + "  •  " + formattedTime);
	    authorAndTime.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
	    
	    // Reply and unread counts
	    javafx.scene.control.Label counts = new javafx.scene.control.Label(
	        "Replies: " + replyCount + " | Unread: " + unreadCount);
	    counts.setStyle("-fx-font-size: 11px;");
	    
	    card.getChildren().addAll(topRow, authorAndTime, counts);
	    card.setCursor(javafx.scene.Cursor.HAND);
	    card.setOnMouseClicked((_) -> {
	        ViewMyPosts.currentPost = post;
	        displayPostBody(post);
	    });
	    
	    return card;
	}
/**********
 * <p> Method: displayPostBody(Post post)</>
 * <p> Description: Populates the post body and has a scroll ability, it shows the selected post's
 * title, thread, body and all of its associated replies, it also marks each reply as read for the current 
 * user upon display.</p>
 * @param post the Post object whose full content and replies should be displayed.
 */
	private static void displayPostBody(entityClasses.Post post) {
	    javafx.scene.layout.VBox fullPost = new javafx.scene.layout.VBox(5);
	    fullPost.setPadding(new javafx.geometry.Insets(10));
	    
	    javafx.scene.control.Label title = new javafx.scene.control.Label(post.getTitle());
	    title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
	    
	    javafx.scene.control.Label thread = new javafx.scene.control.Label("Thread: " + post.getThread());
	    
	    javafx.scene.control.TextArea body = new javafx.scene.control.TextArea(post.getBody());
	    body.setPrefHeight(150);
	    body.setWrapText(true);
	    body.setEditable(false);
	    
	    fullPost.getChildren().addAll(title, thread, body);
	    
	    try {
	        List<entityClasses.Reply> replies = theDatabase.readRepliesForPost(post.getPostID());
	        for (entityClasses.Reply reply : replies) {
	        	theDatabase.markReplyAsRead(reply.getReplyID(), ViewMyPosts.theUser.getUserName());
	            javafx.scene.layout.VBox replyBox = new javafx.scene.layout.VBox(3);
	            replyBox.setPadding(new javafx.geometry.Insets(8));
	            replyBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 3;");
	            javafx.scene.control.Label replyAuthor = new javafx.scene.control.Label(reply.getAuthorUsername() + " says:");
	            replyAuthor.setStyle("-fx-font-weight: bold;");
	         // Display the timestamp when the reply was created
	            javafx.scene.control.Label replyTimestamp = new javafx.scene.control.Label(
	                reply.getCreatedAt() != null ? reply.getCreatedAt().toString() : "");
	            javafx.scene.control.TextArea replyBody = new javafx.scene.control.TextArea(reply.getBody());
	            replyBody.setPrefHeight(80);
	            replyBody.setWrapText(true);
	            replyBody.setEditable(false);	     
	            replyBox.getChildren().addAll(replyAuthor, replyTimestamp, replyBody);	            
	            fullPost.getChildren().add(replyBox);
	        }
	    } catch (Exception e) {
	        fullPost.getChildren().add(new javafx.scene.control.Label("Error loading replies."));
	    }
	    
	    ViewMyPosts.scrollPane_PostBody.setContent(fullPost);
	 // Show delete button only for the current user's posts
	    ViewMyPosts.button_DeletePost.setVisible(
	        post.getAuthorUsername().equals(ViewMyPosts.theUser.getUserName())
	    );
	}
	
	/**********
	 * <p> Method: displayReply() </p>
	 * 
	 * <p> Description: This method populates the post body Scroll Pane with the
	 * replies from the user's search criteria. </p>
	 * @param reply  the Reply object to render
	 * @return       a VBox containing the reply author and body
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
		
		Button button_Reply = new Button("Reply");
		button_Reply.setFont(Font.font("Dialog", 14));
		button_Reply.setMinWidth(50);
		
		viewReply.getChildren().addAll(
				author,
				body,
				button_Reply
				);
		return viewReply;
	}
	
	/**********
	 * <p> Method: performHome() </p>
	 * 
	 * <p> Description: This method returns the user to the user's homepage </p>
	 * 
	 */
	protected static void performHome() {
	    guiStudentHome.ViewStudentHome.displayStudentHome(ViewMyPosts.theStage, ViewMyPosts.theUser);
	}
	
	protected static void performGoToDiscussionBoard() {
	    guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(ViewMyPosts.theStage, ViewMyPosts.theUser);
	}
	
	/**********
	 * <p> Method: performDeletePost() </p>
	 * 
	 * <p> Description: Shows confirmation dialog then soft deletes the currently viewed post
	 * if the user confirms. Only the student's own posts can be deleted. </p>
	 * 
	 */
	protected static void performDeletePost() {
	    if (ViewMyPosts.currentPost == null || ViewMyPosts.currentPost.getPostID() == 0) {
	        return;
	    }

	    if (!ViewMyPosts.currentPost.getAuthorUsername().equals(ViewMyPosts.theUser.getUserName())) {
	        showAlert("Error", "You can only delete your own posts.", javafx.scene.control.Alert.AlertType.ERROR);
	        return;
	    }

	    // Confirmation Dialog
	    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Delete Post");
	    alert.setHeaderText("Are you sure?");
	    alert.setContentText("This action cannot be undone.\n\nThe post will be removed from view, but any replies will remain.");

	    alert.showAndWait().ifPresent(response -> {
	        if (response == javafx.scene.control.ButtonType.OK) {
	            try {
	                theDatabase.deletePost(ViewMyPosts.currentPost.getPostID());
	                
	                // Refresh the view
	                loadMyPosts();
	                ViewMyPosts.scrollPane_PostBody.setContent(null);
	                ViewMyPosts.button_DeletePost.setVisible(false);
	                ViewMyPosts.currentPost = new entityClasses.Post(); // reset
	                
	                showAlert("Success", "Post has been deleted.", javafx.scene.control.Alert.AlertType.INFORMATION);
	            } catch (Exception e) {
	                showAlert("Error", "Failed to delete post: " + e.getMessage(), 
	                         javafx.scene.control.Alert.AlertType.ERROR);
	            }
	        }
	    });
	}


	private static void showAlert(String title, String message, javafx.scene.control.Alert.AlertType type) {
	    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page. </p>
	 * 
	 */
	protected static void performLogout() {
	    guiUserLogin.ViewUserLogin.displayUserLogin(ViewMyPosts.theStage);
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
