package guiStudentFeedback;

import java.util.List;

import database.Database;
import entityClasses.Feedback;
import entityClasses.Post;

/*******
 * <p> Title: ControllerStudentFeedback Class. </p>
 *
 * <p> Description: Controller for the student-facing My Feedback page, satisfying the
 * student-visible portion of the STORY: "As a staff member, I can ... provide private
 * feedback to students ..." Loads every Feedback entry addressed to the logged-in
 * student and builds a card for each, showing the feedback text, which staff member
 * sent it, and which post it relates to. A student only ever sees feedback where they
 * are the targetUsername — never feedback addressed to anyone else, and never the
 * staff-only flag/note/resolve/review state that lives alongside it in the database. </p>
 *
 * <p> Copyright: OWNER_NAME_HERE © 2026 </p>
 *
 * @author OWNER_NAME_HERE
 *
 * @version 1.00		2026-07-26 Initial version
 *
 */
public class ControllerStudentFeedback {

	/**
	 * Default constructor is not used.
	 */
	public ControllerStudentFeedback() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: loadMyFeedback() </p>
	 *
	 * <p> Description: Retrieves every Feedback entry addressed to the current user
	 * and populates the feedback card list. Displays a message if none exist. </p>
	 *
	 */
	protected static void loadMyFeedback() {
		ViewStudentFeedback.feedbackCardList.getChildren().clear();

		String currentUsername = ViewStudentFeedback.theUser.getUserName();
		List<Feedback> myFeedback = theDatabase.readFeedbackForTargetUser(currentUsername);

		if (myFeedback.isEmpty()) {
			javafx.scene.control.Label empty = new javafx.scene.control.Label(
					"You have no feedback yet.");
			empty.setStyle("-fx-text-fill: #888888;");
			ViewStudentFeedback.feedbackCardList.getChildren().add(empty);
			return;
		}

		for (Feedback fb : myFeedback) {
			ViewStudentFeedback.feedbackCardList.getChildren().add(createFeedbackCard(fb));
		}
	}

	/**********
	 * <p> Method: createFeedbackCard(Feedback fb) </p>
	 *
	 * <p> Description: Builds a styled card for a single feedback entry, showing which
	 * staff member sent it, which post it relates to (by title, looked up via the
	 * post's ID), the feedback message itself, and a formatted timestamp. </p>
	 *
	 * @param fb specifies the Feedback object to render
	 *
	 * @return a styled VBox card
	 *
	 */
	private static javafx.scene.layout.VBox createFeedbackCard(Feedback fb) {
		javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(6);
		card.setPadding(new javafx.geometry.Insets(15));
		card.setMaxWidth(560);
		card.setStyle(
				"-fx-border-color: #E0E0E0;" +
				"-fx-border-radius: 8;" +
				"-fx-background-color: white;" +
				"-fx-background-radius: 8;" +
				"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
		);

		// Look up the related post so the student has context for the feedback
		String postTitle = "(original post unavailable)";
		Post relatedPost = theDatabase.readPost(fb.getPostID());
		if (relatedPost != null) {
			postTitle = relatedPost.getTitle();
		}

		javafx.scene.control.Label from = new javafx.scene.control.Label("From: " + fb.getStaffUsername());
		from.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #041E42;");

		javafx.scene.control.Label about = new javafx.scene.control.Label("About your post: " + postTitle);
		about.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");

		javafx.scene.control.TextArea body = new javafx.scene.control.TextArea(fb.getBody());
		body.setWrapText(true);
		body.setEditable(false);
		body.setPrefHeight(70);

		String formattedTime = "";
		if (fb.getCreatedAt() != null) {
			formattedTime = fb.getCreatedAt().format(
					java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
		}
		javafx.scene.control.Label timestamp = new javafx.scene.control.Label(formattedTime);
		timestamp.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

		card.getChildren().addAll(from, about, body, timestamp);
		return card;
	}

	/**********
	 * <p> Method: performHome() </p>
	 *
	 * <p> Description: Returns the user to the Student Home page. </p>
	 *
	 */
	protected static void performHome() {
		guiStudentHome.ViewStudentHome.displayStudentHome(ViewStudentFeedback.theStage, ViewStudentFeedback.theUser);
	}

	/**********
	 * <p> Method: performGoToDiscussionBoard() </p>
	 *
	 * <p> Description: Navigates the user to the Discussion Board page. </p>
	 *
	 */
	protected static void performGoToDiscussionBoard() {
		guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(ViewStudentFeedback.theStage, ViewStudentFeedback.theUser);
	}

	/**********
	 * <p> Method: performGoToMyPosts() </p>
	 *
	 * <p> Description: Navigates the user to the My Posts page. </p>
	 *
	 */
	protected static void performGoToMyPosts() {
		guiMyPosts.ViewMyPosts.displayMyPosts(ViewStudentFeedback.theStage, ViewStudentFeedback.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentFeedback.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the execution of the program. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}