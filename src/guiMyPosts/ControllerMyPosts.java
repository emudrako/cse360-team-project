package guiMyPosts;
import java.util.List;
import database.Database;
import javafx.geometry.Insets;


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
	 * <p> Description:  </p>
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
	                int replyCount = theDatabase.getReplyCount(post.getPostID());
	                int unreadCount = theDatabase.getUnreadReplyCount(post.getPostID(), currentUsername);
	                ViewMyPosts.postCardList.getChildren().add(createPostCard(post, replyCount, unreadCount));
	                hasPosts = true;
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
	 * <p> Description: </p>
	 * 
	 */
	protected static void performToggleUnread() {
	    showingUnreadOnly = !showingUnreadOnly;
	    
	    if (showingUnreadOnly) {
	        ViewMyPosts.button_ToggleUnread.setText("Show All Replies");
	    } else {
	        ViewMyPosts.button_ToggleUnread.setText("Show Unread Only");
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
	 * <p> Description: </p>
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
	
	private static javafx.scene.layout.VBox createPostCard(entityClasses.Post post, int replyCount, int unreadCount) {
	    javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(5);
	    card.setPadding(new javafx.geometry.Insets(10));
	    card.setMinWidth(330);
	    card.setStyle(
	        "-fx-border-color: lightgray;" +
	        "-fx-border-radius: 5;" +
	        "-fx-background-color: white;" +
	        "-fx-background-radius: 5;"
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
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the user's homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(ViewMyPosts.theStage, ViewMyPosts.theUser);
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
