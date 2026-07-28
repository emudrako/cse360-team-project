package guiStaffReview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Feedback;


/*******
 * <p> Title: ViewStaffReview Class </p>
 *
 * <p> Description: GUI for the Staff Review screen, satisfying story 7, displays every 
 * post as a card, clicking a card opens a details panel where staff can flag the post, 
 * attach a private staff note, resolve the flag, mark the post reviewed, and send private 
 * feedback to the post's author. All flag/note/resolve/review state is staff
 * only, feedback is visible to staff and to the reviewed student only. </p>
 *
 * <p> Copyright: Ty Woolman © 2026 </p>
 *
 * @author Ty Woolman
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ViewStaffReview{

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	// Window dimensions consistent with the team's UI style standards
	private static double width = 1000;
	private static double height = 1050;
	
	// GUI Area 1
	// Labels for page title and display name of logged in user
	protected static Label label_PageTitle = new Label("Review and Feedback");
	protected static Label label_UserDetails = new Label();
	// Buttons to return to staff home, button to quit
	protected static Button button_Return = new Button("Home");
	protected static Button button_Quit = new Button("X");

	// GUI Area 2 list of post cards
	protected static HBox postCardList = new HBox(10);
	protected static ScrollPane scrollPane_PostCards = new ScrollPane(postCardList);

	// GUI Area 3 post details panel, shown in place of the card list once a card is clicked
	protected static Post currentPost;

	protected static Reply currentReply;
	protected static boolean reviewingReply = false;

	protected static Label label_DetailTitle = new Label();
	protected static Label label_DetailMeta = new Label();
	protected static TextArea field_DetailBody = new TextArea();
	protected static Label label_StatusLine = new Label();

	protected static Button button_Flag = new Button("Flag Post");
	protected static Button button_Resolve = new Button("Resolve");
	protected static Button button_MarkReviewed = new Button("Mark Reviewed");

	protected static Button button_BackToPost = new Button("Back to Post");

	protected static Label label_RepliesHeader = new Label("Replies:");
	protected static VBox repliesBox = new VBox(6);
	protected static ScrollPane scrollPane_Replies = new ScrollPane(repliesBox);

	protected static Label label_NoteHeader = new Label("Private Staff Note:");
	protected static TextArea field_StaffNote = new TextArea();
	protected static Button button_SaveNote = new Button("Save Note");

	protected static Label label_FeedbackHeader = new Label("Send Private Feedback to Author:");
	protected static TextArea field_NewFeedback = new TextArea();
	protected static Button button_SendFeedback = new Button("Send Feedback");

	protected static Label label_FeedbackHistoryHeader = new Label("Feedback History:");
	protected static VBox feedbackHistoryBox = new VBox(6);
	protected static ScrollPane scrollPane_FeedbackHistory = new ScrollPane(feedbackHistoryBox);

	protected static Button button_Done = new Button("Done");
	protected static Label label_DetailError = new Label();

	
	private static ViewStaffReview theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewStaffReviewScene;


	/*-*******************************************************************************************

	Entry Point

	 */

	/**********
	 * <p> Method: displayStaffReview(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Staff Review screen. Creates
	 * the view instance on first use, refreshes the list of reviewable posts
	 * from the database, and populates the horizontal post card list each time the
	 * screen is shown. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void displayStaffReview(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewStaffReview();

		theDatabase.getUserAccountDetails(user.getUserName());

		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Staff Review");
		theStage.setScene(theViewStaffReviewScene);

		// Always return to the card list view when entering this screen
		hidePostDetails();
		refreshPostCards();

		theStage.show();
	}

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: ViewStaffReview() </p>
	 *
	 * <p> Description: Initializes all GUI elements. </p>
	 *
	 */
	private ViewStaffReview() {
		theRootPane = new Pane();
		theViewStaffReviewScene = new Scene(theRootPane, width, height);
		theRootPane.setStyle("-fx-background-color: #041E42;");

		javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
		card.setWidth(600);
		card.setHeight(770);
		card.setX(200);
		card.setY(80);
		card.setArcWidth(20);
		card.setArcHeight(20);
		card.setFill(javafx.scene.paint.Color.WHITE);
		card.setEffect(new javafx.scene.effect.DropShadow(20, javafx.scene.paint.Color.rgb(0,0,0,0.3)));

		// GUI area 1
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
		label_UserDetails.setStyle("-fx-text-fill: white;");
		// Return button, returns user to the staff home page
		setupButtonUI(button_Return, "Dialog", 12, 70, Pos.CENTER, 880, 10);
	    button_Return.setOnAction((_) -> { ControllerStaffReview.performReturn(); });
		button_Return.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");
		// Quit Button		
		setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
	    button_Quit.setOnAction((_) -> { ControllerStaffReview.performQuit(); });
		button_Quit.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_PageTitle, "Arial", 28, 400, Pos.CENTER, 300, 140);

		// GUI Area 2
		postCardList.setPadding(new Insets(5));
		scrollPane_PostCards.setLayoutX(220);
		scrollPane_PostCards.setLayoutY(190);
		scrollPane_PostCards.setPrefWidth(560);
		scrollPane_PostCards.setPrefHeight(460);
		scrollPane_PostCards.setStyle("-fx-background-color: transparent;");

		// GUI Area 3
		setupLabelUI(label_DetailTitle, "Arial", 18, 520, Pos.BASELINE_LEFT, 240, 200);
		label_DetailTitle.setStyle("-fx-font-weight: bold;");

		setupLabelUI(label_DetailMeta, "Arial", 12, 520, Pos.BASELINE_LEFT, 240, 222);
		label_DetailMeta.setStyle("-fx-text-fill: #555555;");

		field_DetailBody.setLayoutX(240);
		field_DetailBody.setLayoutY(235);
		field_DetailBody.setPrefSize(520, 90);
		field_DetailBody.setWrapText(true);
		field_DetailBody.setEditable(false);

		setupLabelUI(label_StatusLine, "Arial", 12, 520, Pos.BASELINE_LEFT, 240, 335);
		label_StatusLine.setStyle("-fx-text-fill: #BF0D3E; -fx-font-weight: bold;");

		setupButtonUI(button_Flag, "Dialog", 12, 120, Pos.CENTER, 240, 355);
		button_Flag.setOnAction((_) -> handleFlag());
		button_Flag.setStyle("-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_Resolve, "Dialog", 12, 120, Pos.CENTER, 370, 355);
		button_Resolve.setOnAction((_) -> handleResolve());
		button_Resolve.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_MarkReviewed, "Dialog", 12, 120, Pos.CENTER, 500, 355);
		button_MarkReviewed.setOnAction((_) -> handleMarkReviewed());
		button_MarkReviewed.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupButtonUI(button_BackToPost, "Dialog", 12, 120, Pos.CENTER, 500, 355);
		button_BackToPost.setOnAction((_) -> handleBackToPost());
		button_BackToPost.setStyle("-fx-background-color: #041E42; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_RepliesHeader, "Arial", 13, 520, Pos.BASELINE_LEFT, 240, 390);

		repliesBox.setPadding(new Insets(5));
		scrollPane_Replies.setLayoutX(240);
		scrollPane_Replies.setLayoutY(410);
		scrollPane_Replies.setPrefSize(520, 120);
		scrollPane_Replies.setStyle("-fx-background-color: transparent;");

		setupLabelUI(label_NoteHeader, "Arial", 13, 520, Pos.BASELINE_LEFT, 240, 545);

		field_StaffNote.setLayoutX(240);
		field_StaffNote.setLayoutY(560);
		field_StaffNote.setPrefSize(520, 55);
		field_StaffNote.setWrapText(true);

		setupButtonUI(button_SaveNote, "Dialog", 12, 120, Pos.CENTER, 240, 622);
		button_SaveNote.setOnAction((_) -> handleSaveNote());
		button_SaveNote.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_FeedbackHeader, "Arial", 13, 520, Pos.BASELINE_LEFT, 240, 655);

		field_NewFeedback.setLayoutX(240);
		field_NewFeedback.setLayoutY(670);
		field_NewFeedback.setPrefSize(520, 55);
		field_NewFeedback.setWrapText(true);

		setupButtonUI(button_SendFeedback, "Dialog", 12, 150, Pos.CENTER, 240, 730);
		button_SendFeedback.setOnAction((_) -> handleSendFeedback());
		button_SendFeedback.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_DetailError, "Arial", 12, 400, Pos.BASELINE_LEFT, 400, 735);
		label_DetailError.setStyle("-fx-text-fill: #BF0D3E;");
		label_DetailError.setMouseTransparent(true);

		setupButtonUI(button_Done, "Dialog", 12, 120, Pos.CENTER, 630, 730);
		button_Done.setOnAction((_) -> hidePostDetails());
		button_Done.setStyle("-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

		setupLabelUI(label_FeedbackHistoryHeader, "Arial", 13, 520, Pos.BASELINE_LEFT, 240, 765);

		feedbackHistoryBox.setPadding(new Insets(5));
		scrollPane_FeedbackHistory.setLayoutX(240);
		scrollPane_FeedbackHistory.setLayoutY(785);
		scrollPane_FeedbackHistory.setPrefSize(520, 55);
		scrollPane_FeedbackHistory.setStyle("-fx-background-color: transparent;");

		hidePostDetails();

		theRootPane.getChildren().addAll(card, label_UserDetails, label_PageTitle, button_Return, button_Quit,
				scrollPane_PostCards,
				label_DetailTitle, label_DetailMeta, field_DetailBody, label_StatusLine,
				button_Flag, button_Resolve, button_MarkReviewed, button_BackToPost,
				label_RepliesHeader, scrollPane_Replies,
				label_NoteHeader, field_StaffNote, button_SaveNote,
				label_FeedbackHeader, field_NewFeedback, button_SendFeedback,
				label_FeedbackHistoryHeader, scrollPane_FeedbackHistory,
				button_Done, label_DetailError);
	}

	/*-*******************************************************************************************

	Card list / details panel logic

	 */

	/**********
	 * <p> Method: refreshPostCards() </p>
	 *
	 * <p> Description: Reloads every reviewable post from the database and rebuilds
	 * the horizontal card list. </p>
	 *
	 */
	protected static void refreshPostCards() {
		List<Post> allPosts = ControllerStaffReview.performReadAllPosts();
		postCardList.getChildren().clear();
		for (Post post : allPosts) {
			HBox card = ControllerStaffReview.createPostCard(post);
			if (card != null) {
				postCardList.getChildren().add(card);
			}
		}
	}

	/**********
	 * <p> Method: showPostDetails(Post post) </p>
	 *
	 * <p> Description: Hides the card list and reveals the details panel for the
	 * chosen post. </p>
	 *
	 * @param post specifies the Post whose details should be displayed
	 *
	 */
	protected static void showPostDetails(Post post) {
		currentPost = post;
		currentReply = null;
		reviewingReply = false;

		scrollPane_PostCards.setVisible(false);

		label_DetailTitle.setText(post.getTitle());
		label_DetailTitle.setVisible(true);

		label_DetailMeta.setText("By: " + post.getAuthorUsername() + "   |   Thread: " + post.getThread());
		label_DetailMeta.setVisible(true);

		field_DetailBody.setText(post.getBody());
		field_DetailBody.setVisible(true);

		label_StatusLine.setText(buildStatusLine(post));
		label_StatusLine.setVisible(true);

		field_StaffNote.setText(post.getStaffNote() == null ? "" : post.getStaffNote());
		field_StaffNote.setVisible(true);
		field_NewFeedback.clear();
		field_NewFeedback.setVisible(true);

		label_NoteHeader.setVisible(true);
		label_FeedbackHeader.setVisible(true);
		label_FeedbackHistoryHeader.setVisible(true);
		button_Flag.setVisible(true);
		button_Resolve.setVisible(true);
		button_MarkReviewed.setVisible(true);
		button_BackToPost.setVisible(false);
		button_SaveNote.setVisible(true);
		button_SendFeedback.setVisible(true);
		button_Done.setVisible(true);
		scrollPane_FeedbackHistory.setVisible(true);
		label_DetailError.setVisible(true);
		label_DetailError.setText("");

		label_RepliesHeader.setVisible(true);
		scrollPane_Replies.setVisible(true);
		refreshRepliesList(post.getPostID());

		refreshFeedbackHistory(post.getPostID());
	}

	/**********
	 * <p> Method: showReplyDetails(Reply reply) </p>
	 *
	 * <p> Description: Switches the details panel into reply review mode, reuses the
	 * post fields to show the reply's author, body, and flag/resolve status. </p>
	 *
	 * @param reply specifies the Reply whose details should be displayed
	 *
	 */
	protected static void showReplyDetails(Reply reply) {
		currentReply = reply;
		reviewingReply = true;

		label_DetailTitle.setText("Reply by " + reply.getAuthorUsername());
		label_DetailMeta.setText("Replying within: " + (currentPost != null ? currentPost.getTitle() : ""));
		field_DetailBody.setText(reply.getBody());
		label_StatusLine.setText(buildReplyStatusLine(reply));
		field_StaffNote.setText(reply.getStaffNote() == null ? "" : reply.getStaffNote());
		field_NewFeedback.clear();
		label_DetailError.setText("");

		label_RepliesHeader.setVisible(false);
		scrollPane_Replies.setVisible(false);
		button_MarkReviewed.setVisible(false);
		button_BackToPost.setVisible(true);
	}

	/**********
	 * <p> Method: hidePostDetails() </p>
	 *
	 * <p> Description: Hides the details panel and returns to the card list view. </p>
	 *
	 */
	protected static void hidePostDetails() {
		currentPost = null;
		currentReply = null;
		reviewingReply = false;

		label_DetailTitle.setVisible(false);
		label_DetailMeta.setVisible(false);
		field_DetailBody.setVisible(false);
		label_StatusLine.setVisible(false);
		button_Flag.setVisible(false);
		button_Resolve.setVisible(false);
		button_MarkReviewed.setVisible(false);
		button_BackToPost.setVisible(false);
		label_RepliesHeader.setVisible(false);
		scrollPane_Replies.setVisible(false);
		label_NoteHeader.setVisible(false);
		field_StaffNote.setVisible(false);
		button_SaveNote.setVisible(false);
		label_FeedbackHeader.setVisible(false);
		field_NewFeedback.setVisible(false);
		button_SendFeedback.setVisible(false);
		label_FeedbackHistoryHeader.setVisible(false);
		scrollPane_FeedbackHistory.setVisible(false);
		button_Done.setVisible(false);
		label_DetailError.setVisible(false);
		label_DetailError.setText("");

		scrollPane_PostCards.setVisible(true);
	}

	/**********
	 * <p> Method: buildStatusLine(Post post) </p>
	 *
	 * <p> Description: Builds the staff status summary shown at the top of the
	 * details panel, showing flag/resolve/review and who performed each action. </p>
	 *
	 * @param post specifies the Post to summarize
	 *
	 * @return a readable status string
	 *
	 */
	private static String buildStatusLine(Post post) {
		StringBuilder sb = new StringBuilder();
		if (post.getIsFlagged()) {
			sb.append("Flagged by ").append(post.getFlaggedBy());
			if (post.getIsResolved()) {
				sb.append(" — Resolved by ").append(post.getResolvedBy());
			} else {
				sb.append(" — Not yet resolved");
			}
		} else {
			sb.append("Not flagged");
		}
		if (post.getIsReviewed()) {
			sb.append("   |   Reviewed by ").append(post.getReviewedBy());
		} else {
			sb.append("   |   Not reviewed");
		}
		return sb.toString();
	}

	/**********
	 * <p> Method: buildReplyStatusLine(Reply reply) </p>
	 *
	 * <p> Description: Builds the staff status summary shown at the top of the
	 * details panel while reviewing a reply, reflecting flag/resolve state and who
	 * performed each action. </p>
	 *
	 * @param reply specifies the Reply to summarize
	 *
	 * @return a readable status string
	 *
	 */
	private static String buildReplyStatusLine(Reply reply) {
		if (reply.getIsFlagged()) {
			StringBuilder sb = new StringBuilder("Flagged by ").append(reply.getFlaggedBy());
			if (reply.getIsResolved()) {
				sb.append(" — Resolved by ").append(reply.getResolvedBy());
			} else {
				sb.append(" — Not yet resolved");
			}
			return sb.toString();
		}
		return "Not flagged";
	}

	/**********
	 * <p> Method: refreshRepliesList(int postID) </p>
	 *
	 * <p> Description: Reloads and redisplays every reply to the chosen post as a
	 * row in the Replies list, each clickable to open reply review mode. </p>
	 *
	 * @param postID specifies the post whose replies should be shown
	 *
	 */
	private static void refreshRepliesList(int postID) {
		repliesBox.getChildren().clear();
		List<Reply> replies = ControllerStaffReview.performReadRepliesForPost(postID);
		if (replies.isEmpty()) {
			Label none = new Label("No replies on this post yet.");
			none.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
			repliesBox.getChildren().add(none);
			return;
		}
		for (Reply reply : replies) {
			HBox row = ControllerStaffReview.createReplyRow(reply);
			if (row != null) {
				repliesBox.getChildren().add(row);
			}
		}
	}

	/**********
	 * <p> Method: refreshFeedbackHistory(int postID) </p>
	 *
	 * <p> Description: Reloads and redisplays every feedback entry left on the
	 * specified post, oldest first. </p>
	 *
	 * @param postID specifies the post whose feedback history should be shown
	 *
	 */
	private static void refreshFeedbackHistory(int postID) {
		feedbackHistoryBox.getChildren().clear();
		List<Feedback> history = ControllerStaffReview.performReadFeedbackForPost(postID);
		if (history.isEmpty()) {
			Label none = new Label("No feedback has been sent on this post yet.");
			none.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
			feedbackHistoryBox.getChildren().add(none);
			return;
		}
		for (Feedback fb : history) {
			Label entry = new Label("To " + fb.getTargetUsername() + " from " + fb.getStaffUsername()
					+ ":  " + fb.getBody());
			entry.setWrapText(true);
			entry.setMaxWidth(500);
			entry.setStyle("-fx-font-size: 12px;");
			feedbackHistoryBox.getChildren().add(entry);
		}
	}

	/**********
	 * <p> Method: handleFlag() </p>
	 *
	 * <p> Description: Flags the currently displayed post and refreshes the details
	 * panel to reflect the new status. </p>
	 *
	 */
	private static void handleFlag() {
		if (reviewingReply) {
			if (currentReply == null) return;
			ControllerStaffReview.performFlagReply(currentReply.getReplyID());
			reloadCurrentReply();
			return;
		}
		if (currentPost == null) return;
		ControllerStaffReview.performFlagPost(currentPost.getPostID());
		reloadCurrentPost();
	}

	/**********
	 * <p> Method: handleResolve() </p>
	 *
	 * <p> Description: Resolves the currently displayed post's moderation action and
	 * refreshes the details panel to reflect the new status. </p>
	 *
	 */
	private static void handleResolve() {
		if (reviewingReply) {
			if (currentReply == null) return;
			ControllerStaffReview.performResolveReply(currentReply.getReplyID());
			reloadCurrentReply();
			return;
		}
		if (currentPost == null) return;
		ControllerStaffReview.performResolvePost(currentPost.getPostID());
		reloadCurrentPost();
	}

	/**********
	 * <p> Method: handleMarkReviewed() </p>
	 *
	 * <p> Description: Marks the currently displayed post as reviewed and refreshes
	 * the details panel to reflect the new status. </p>
	 *
	 */
	private static void handleMarkReviewed() {
		if (currentPost == null) return;
		ControllerStaffReview.performMarkReviewed(currentPost.getPostID());
		reloadCurrentPost();
	}

	/**********
	 * <p> Method: handleSaveNote() </p>
	 *
	 * <p> Description: Saves the private staff note text for the currently displayed
	 * post. </p>
	 *
	 */
	private static void handleSaveNote() {
		if (reviewingReply) {
			if (currentReply == null) return;
			ControllerStaffReview.performSaveReplyStaffNote(currentReply.getReplyID(), field_StaffNote.getText());
			currentReply.setStaffNote(field_StaffNote.getText());
			label_DetailError.setText("Note saved.");
			return;
		}
		if (currentPost == null) return;
		ControllerStaffReview.performSaveStaffNote(currentPost.getPostID(), field_StaffNote.getText());
		label_DetailError.setText("Note saved.");
	}

	/**********
	 * <p> Method: handleSendFeedback() </p>
	 *
	 * <p> Description: Sends the entered feedback text to the currently displayed
	 * post's author, clears the input field on success, and refreshes the feedback
	 * history. </p>
	 *
	 */
	private static void handleSendFeedback() {
		if (currentPost == null) return;
		String targetUsername = reviewingReply && currentReply != null
				? currentReply.getAuthorUsername()
				: currentPost.getAuthorUsername();
		String result = ControllerStaffReview.performSendFeedback(
				currentPost.getPostID(), targetUsername, field_NewFeedback.getText());
		if (result.isEmpty()) {
			field_NewFeedback.clear();
			label_DetailError.setText("Feedback sent.");
			refreshFeedbackHistory(currentPost.getPostID());
		} else {
			label_DetailError.setText(result);
		}
	}

	/**********
	 * <p> Method: handleBackToPost() </p>
	 *
	 * <p> Description: Leaves reply review mode and returns the details panel to
	 * showing the original post. </p>
	 *
	 */
	private static void handleBackToPost() {
		if (currentPost == null) return;
		reloadCurrentPost();
	}

	/**********
	 * <p> Method: reloadCurrentPost() </p>
	 *
	 * <p> Description: Rereads the currently displayed post's staff fields from the
	 * database and redisplays the details panel, so flag/resolve/review actions are
	 * reflected immediately. </p>
	 *
	 */
	private static void reloadCurrentPost() {
		if (currentPost == null) return;
		int postID = currentPost.getPostID();
		List<Post> allPosts = ControllerStaffReview.performReadAllPosts();
		for (Post p : allPosts) {
			if (p.getPostID() == postID) {
				showPostDetails(p);
				break;
			}
		}
		refreshPostCards();
	}

	/**********
	 * <p> Method: reloadCurrentReply() </p>
	 *
	 * <p> Description: Rereads the currently displayed reply's staff fields from the
	 * database and redisplays reply review mode, so flag/resolve actions are
	 * reflected immediately. </p>
	 *
	 */
	private static void reloadCurrentReply() {
		if (currentReply == null || currentPost == null) return;
		int replyID = currentReply.getReplyID();
		List<Reply> replies = ControllerStaffReview.performReadRepliesForPost(currentPost.getPostID());
		for (Reply r : replies) {
			if (r.getReplyID() == replyID) {
				showReplyDetails(r);
				break;
			}
		}
	}

	
	/*-********************************************************************************************

	Helper methods to reduce code length

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
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,double y) {
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}