package guiStaffReview;

import java.sql.SQLException;
import java.util.List;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Feedback;

/*******
 * <p> Title: ControllerStaffReview Class. </p>
 *
 * <p> Description: Controller for the Staff Review screen, satisfying story 7. Handles 
 * navigation into this screen, loading posts for review, and all flag/note/resolve/review/feedback actions. </p>
 *
 * <p> Copyright: Ty Woolman © 2026 </p>
 *
 * @author Ty Woolman
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ControllerStaffReview {
	/**********
	 * <p> Method: performStaffReview(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen.
	 * Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void performStaffReview(Stage ps, User user) {
		ViewStaffReview.displayStaffReview(ps, user);
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the staff homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffReview.theStage, ViewStaffReview.theUser);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the program. </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}

	/**********
	 * <p> Method: performReadAllPosts() </p>
	 *
	 * <p> Description: Retrieves every non deleted post, including staff flag/
	 * review state, for display as review cards. </p>
	 *
	 * @return a List of all reviewable Post objects
	 *
	 */
	protected static List<Post> performReadAllPosts() {
		return ViewStaffReview.theDatabase.readAllPostsWithStaffFields();
	}

	/**********
	 * <p> Method: performFlagPost(int postID) </p>
	 *
	 * <p> Description: Flags the currently selected post, recording the logged in
	 * staff member's username and the current time. </p>
	 *
	 * @param postID specifies the ID of the post to flag
	 *
	 */
	protected static void performFlagPost(int postID) {
		ViewStaffReview.theDatabase.flagPost(postID, ViewStaffReview.theUser.getUserName());
	}

	/**********
	 * <p> Method: performResolvePost(int postID) </p>
	 *
	 * <p> Description: Marks the currently selected post's moderation action as
	 * resolved, recording the logged in staff member's username and the current time. </p>
	 *
	 * @param postID specifies the ID of the post to resolve
	 *
	 */
	protected static void performResolvePost(int postID) {
		ViewStaffReview.theDatabase.resolvePost(postID, ViewStaffReview.theUser.getUserName());
	}

	/**********
	 * <p> Method: performMarkReviewed(int postID) </p>
	 *
	 * <p> Description: Marks the currently selected post as reviewed during review,
	 * recording the logged in staff member's username and the current time. </p>
	 *
	 * @param postID specifies the ID of the post to mark reviewed
	 *
	 */
	protected static void performMarkReviewed(int postID) {
		ViewStaffReview.theDatabase.markPostReviewed(postID, ViewStaffReview.theUser.getUserName());
	}

	/**********
	 * <p> Method: performSaveStaffNote(int postID, String note) </p>
	 *
	 * <p> Description: Saves a private staff annotation on the currently selected
	 * post, explaining the concern behind a flag. </p>
	 *
	 * @param postID specifies the ID of the post to annotate
	 *
	 * @param note specifies the private staff note text
	 *
	 */
	protected static void performSaveStaffNote(int postID, String note) {
		ViewStaffReview.theDatabase.setPostStaffNote(postID, note);
	}

	/**********
	 * <p> Method: performSendFeedback(int postID, String targetUsername, String body) </p>
	 *
	 * <p> Description: Sends a piece of private feedback about the currently selected
	 * post, addressed to the target user. Visible to any staff member and to the chosen user only. </p>
	 *
	 * @param postID specifies the post the feedback is about
	 *
	 * @param targetUsername specifies the user the feedback is addressed to
	 *
	 * @param body specifies the feedback text
	 *
	 * @return an empty string on success, or an error message if the feedback could
	 *  not be sent
	 *
	 */
	protected static String performSendFeedback(int postID, String targetUsername, String body) {
		if (body == null || body.isBlank()) {
			return "Feedback cannot be empty.";
		}
		Feedback feedback = new Feedback(postID, ViewStaffReview.theUser.getUserName(), targetUsername, body);
		try {
			ViewStaffReview.theDatabase.createFeedback(feedback);
			return "";
		} catch (SQLException e) {
			return "*** Error *** Could not save feedback: " + e.getMessage();
		}
	}

	/**********
	 * <p> Method: performReadFeedbackForPost(int postID) </p>
	 *
	 * <p> Description: Retrieves all feedback entries that have been left on the
	 * specified post, oldest first, for display in the staff review details panel. </p>
	 *
	 * @param postID specifies the post whose feedback history should be retrieved
	 *
	 * @return a List of Feedback objects for the specified post
	 *
	 */
	protected static List<Feedback> performReadFeedbackForPost(int postID) {
		return ViewStaffReview.theDatabase.readFeedbackForPost(postID);
	}

	/**********
	 * <p> Method: performReadRepliesForPost(int postID) </p>
	 *
	 * <p> Description: Retrieves every reply to the specified post, including
	 * staff flag/note/resolve state, for display in the details panel's
	 * Replies list. </p>
	 *
	 * @param postID specifies the post whose replies should be retrieved
	 *
	 * @return a List of Reply objects for the specified post
	 *
	 */
	protected static List<Reply> performReadRepliesForPost(int postID) {
		return ViewStaffReview.theDatabase.readRepliesForPostWithStaffFields(postID);
	}

	/**********
	 * <p> Method: performFlagReply(int replyID) </p>
	 *
	 * <p> Description: Flags the currently selected reply, recording the logged in
	 * staff member's username and the current time. </p>
	 *
	 * @param replyID specifies the ID of the reply to flag
	 *
	 */
	protected static void performFlagReply(int replyID) {
		ViewStaffReview.theDatabase.flagReply(replyID, ViewStaffReview.theUser.getUserName());
	}

	/**********
	 * <p> Method: performResolveReply(int replyID) </p>
	 *
	 * <p> Description: Marks the currently selected reply's moderation action as
	 * resolved, recording the logged-in staff member's username and the current time. </p>
	 *
	 * @param replyID specifies the ID of the reply to resolve
	 *
	 */
	protected static void performResolveReply(int replyID) {
		ViewStaffReview.theDatabase.resolveReply(replyID, ViewStaffReview.theUser.getUserName());
	}

	/**********
	 * <p> Method: performSaveReplyStaffNote(int replyID, String note) </p>
	 *
	 * <p> Description: Saves a private staff annotation on the currently selected
	 * reply. Never visible to students. </p>
	 *
	 * @param replyID specifies the ID of the reply to annotate
	 *
	 * @param note specifies the private staff note text
	 *
	 */
	protected static void performSaveReplyStaffNote(int replyID, String note) {
		ViewStaffReview.theDatabase.setReplyStaffNote(replyID, note);
	}

	/**********
	 * <p> Method: createReplyRow(Reply reply) </p>
	 *
	 * <p> Description: Builds a single clickable row summarizing one reply for the
	 * replies list inside the post details panel, author, a body snippet, and a
	 * flag/resolve indicator. </p>
	 *
	 * @param reply specifies the Reply to render as a row
	 *
	 * @return an HBox containing the rendered row, or null if reply is null
	 *
	 */
	protected static HBox createReplyRow(Reply reply) {
		if (reply == null) return null;

		HBox row = new HBox(10);
		row.setStyle("-fx-background-color: #F8F9FB; -fx-background-radius: 6; -fx-padding: 8; "
				+ "-fx-border-color: #E4E8EC; -fx-border-radius: 6;");

		VBox content = new VBox(3);
		String snippet = reply.getBody() == null ? "" :
				(reply.getBody().length() > 80 ? reply.getBody().substring(0, 80) + "…" : reply.getBody());

		Label author = new Label(reply.getAuthorUsername() + ":");
		author.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

		Label body = new Label(snippet);
		body.setWrapText(true);
		body.setMaxWidth(340);
		body.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");

		content.getChildren().addAll(author, body);

		Label status = new Label(replyStatusLabelFor(reply));
		status.setStyle("-fx-font-size: 11px; -fx-text-fill: "
				+ (reply.getIsFlagged() && !reply.getIsResolved() ? "#BF0D3E;" : "#0062A3;"));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button reviewButton = new Button("Review");
		reviewButton.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5; "
				+ "-fx-font-size: 11px;");
		reviewButton.setOnAction((_) -> ViewStaffReview.showReplyDetails(reply));

		row.getChildren().addAll(content, spacer, status, reviewButton);
		return row;
	}

	/**********
	 * <p> Method: replyStatusLabelFor(Reply reply) </p>
	 *
	 * <p> Description: Builds the short staff status string shown on a reply
	 * row, reflecting flag/resolve state. </p>
	 *
	 * @param reply specifies the Reply to summarize
	 *
	 * @return a short status string such as "Flagged", "Resolved", or "—"
	 *
	 */
	private static String replyStatusLabelFor(Reply reply) {
		if (reply.getIsFlagged() && !reply.getIsResolved()) return "\u26A0 Flagged";
		if (reply.getIsFlagged() && reply.getIsResolved()) return "\u2713 Resolved";
		return "—";
	}

	/**********
	 * <p> Method: createPostCard(Post post) </p>
	 *
	 * <p> Description: Builds a single clickable card summarizing one post for the
	 * review card list, title, author, and a flag/reviewed indicator.
	 * Clicking the card opens the full post details panel for staff action. </p>
	 *
	 * @param post specifies the Post to render as a card
	 *
	 * @return an HBox containing the rendered card, or null if post is null
	 *
	 */
	protected static HBox createPostCard(Post post) {
		if (post == null) return null;

		HBox card = new HBox(5);
		card.setPrefSize(160, 140);
		card.setMaxSize(160, 140);
		card.setMinSize(160, 140);
		card.setStyle("-fx-background-color: #F2F5F8; -fx-background-radius: 8; -fx-padding: 10; "
				+ "-fx-border-color: #D0D8E0; -fx-border-radius: 8;");
		card.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.15)));

		VBox content = new VBox(6);

		Label title = new Label(post.getTitle());
		title.setWrapText(true);
		title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
		title.setMaxWidth(140);

		Label author = new Label("By: " + post.getAuthorUsername());
		author.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

		Label status = new Label(statusLabelFor(post));
		status.setStyle("-fx-font-size: 11px; -fx-text-fill: "
				+ (post.getIsFlagged() && !post.getIsResolved() ? "#BF0D3E;" : "#0062A3;"));

		Button viewButton = new Button("Review");
		viewButton.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5; "
				+ "-fx-font-size: 11px;");
		viewButton.setOnAction((_) -> ViewStaffReview.showPostDetails(post));

		content.getChildren().addAll(title, author, status, viewButton);
		card.getChildren().add(content);
		return card;
	}

	/**********
	 * <p> Method: statusLabelFor(Post post) </p>
	 *
	 * <p> Description: Builds the short staff status string shown on a post
	 * card, flag/resolve/review state. </p>
	 *
	 * @param post specifies the Post to summarize
	 *
	 * @return a short status string, "Flagged", "Resolved", "Reviewed", or "—"
	 *
	 */
	private static String statusLabelFor(Post post) {
		if (post.getIsFlagged() && !post.getIsResolved()) return "\u26A0 Flagged";
		if (post.getIsFlagged() && post.getIsResolved()) return "\u2713 Resolved";
		if (post.getIsReviewed()) return "\u2713 Reviewed";
		return "Not reviewed";
	}
}