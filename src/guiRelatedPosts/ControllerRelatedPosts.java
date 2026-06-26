package guiRelatedPosts;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.Post;
import javafx.scene.layout.VBox;

/*******
 * <p> Title: ControllerRelatedPosts Class. </p>
 *
 * <p> Description: The Java/FX-based Related Posts Page controller. This class
 * implements Student User Story 2 ("View Related Posts from Others") AND Student User
 * Story 4 ("View and Search Others' Posts by Keyword"), collapsed into one screen by
 * team agreement on 2026-06-24.
 *
 * The Acceptance Criteria for Story 2 require that:
 * 	(1) the screen displays posts from other students that may relate to the topics
 * 		the student is interested in,
 * 	(2) each post entry shows enough information to assess relevance, and
 * 	(3) the list renders without error even when no related posts exist.
 *
 * The Acceptance Criteria for Story 4 add that:
 * 	(4) the student can enter one or more keywords and receive a filtered list of
 * 		matching posts from other students, and
 * 	(5) an empty result set is handled gracefully.
 *
 * This controller satisfies (1)–(5) by retrieving every Post from the H2 database via
 * Database.getPostObjects() (consistent with how Pete's ControllerDiscussionBoard
 * reads posts), filtering out the current student's own posts (Story 2 is specifically
 * about posts "from others") and any post marked isDeleted (a soft-deleted post should
 * not be shown to a student browsing for related content), optionally narrowing the
 * list further by a case-insensitive keyword match against title and body, and then
 * building one result card per remaining post using the team's styled card visual
 * (white background, light-gray border, drop shadow). Each card shows the thread,
 * title, author, timestamp, and a short excerpt — enough for the student to judge
 * relevance without needing to open the full post.
 *
 * The class has been written assuming that the View or the Model are the only class
 * methods that can invoke these methods. This is why each has been declared as
 * protected, consistent with every other controller in the team's Foundations-SU26
 * code (e.g. ControllerDiscussionBoard, ControllerCreatePost). Do not change any of
 * these methods to public.</p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00		2026-06-20 Initial version (Student User Story 2: View Related
 *						Posts from Others)
 * @version 2.00		2026-06-26 Refactored to load posts from the H2 database
 *						(matching the team's database-backed Discussion Board pattern),
 *						and to support keyword filtering (Story 4 collapsed into this
 *						screen). Added performCancel to return the student to the
 *						Discussion Board where the flow started.
 * @version 2.10		2026-06-26 Renders matching posts as styled cards (matching the
 *						team's UI Style Guide) instead of plain ListView lines, so the
 *						page is visually consistent with Pete's Discussion Board.
 *
 */

public class ControllerRelatedPosts {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated. Rather, it is a collection
	of protected static methods that can be called by the View (which is a singleton
	instantiated object) and the Model is just a stub, consistent with the rest of the
	Foundations-SU26 code.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRelatedPosts() {
	}

	// The maximum number of characters of a post's body to show on a card, so a very
	// long post does not blow up the card height. Cards beyond this length are cut
	// off with an ellipsis.
	private static final int BODY_EXCERPT_LENGTH = 140;

	// Reference for the in-memory database. The team's Discussion Board, MyPosts, and
	// CreatePost screens all read posts from this same database via getPostObjects(),
	// so this controller does too — rather than the in-memory PostList in
	// FoundationsMain which is only populated as a leftover from earlier development.
	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: loadRelatedPosts(String keyword) </p>
	 *
	 * <p> Description: This method retrieves every Post from the H2 database, filters
	 * out the current student's own posts and any posts that have been soft-deleted,
	 * optionally narrows the list further by a case-insensitive keyword match against
	 * title and body, and populates the result-card area on ViewRelatedPosts with one
	 * styled card per remaining post.
	 *
	 * If, after filtering, there are no related posts to show, this method instead
	 * displays the "No matching posts found" message so the page still renders
	 * cleanly with zero posts, satisfying both the Story 2 and Story 4 Acceptance
	 * Criteria that empty result sets are handled gracefully. </p>
	 *
	 * @param keyword the keyword to filter posts by. An empty string means "show all
	 *  related posts" (Story 2 behavior); a non-empty string narrows the results
	 *  (Story 4 behavior).
	 *
	 */
	protected static void loadRelatedPosts(String keyword) {

		ViewRelatedPosts.postCardList.getChildren().clear();

		String currentUsername = ViewRelatedPosts.theUser.getUserName();
		String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

		List<Post> allPosts;
		try {
			// Read posts from the H2 database, consistent with how Pete's
			// ControllerDiscussionBoard populates its post list.
			allPosts = theDatabase.getPostObjects();
		} catch (SQLException e) {
			e.printStackTrace();
			// On a database failure, render the empty-state view so the screen
			// still works rather than crashing the JavaFX thread.
			repaintTheWindow();
			return;
		}

		int matchCount = 0;
		for (Post post : allPosts) {

			// Skip posts authored by the current student — Story 2 is specifically
			// about surfacing posts "from others," not the student's own posts.
			if (post.getAuthorUsername().equals(currentUsername)) continue;

			// Skip soft-deleted posts (Story 6) — a deleted post should not appear
			// to a student browsing for related content, even though it is preserved
			// in the database so existing replies to it can still show the "original
			// post has been deleted" message.
			if (post.getIsDeleted()) continue;

			// If a keyword was given, narrow to posts whose title or body contains
			// that keyword (case-insensitive). This is the Story 4 behavior. An
			// empty keyword skips this filter so the student sees all related posts.
			if (!normalizedKeyword.isEmpty()) {
				String title = post.getTitle() == null ? "" : post.getTitle().toLowerCase();
				String body  = post.getBody()  == null ? "" : post.getBody().toLowerCase();
				if (!title.contains(normalizedKeyword) && !body.contains(normalizedKeyword)) {
					continue;
				}
			}

			VBox card = buildCardForPost(post);
			ViewRelatedPosts.postCardList.getChildren().add(card);
			matchCount++;
		}

		// If nothing matched, show the empty-state label inside the card area so
		// the screen renders cleanly with zero results.
		if (matchCount == 0) {
			ViewRelatedPosts.postCardList.getChildren()
					.add(ViewRelatedPosts.label_NoRelatedPosts);
		}

		repaintTheWindow();
	}


	/**********
	 * <p> Method: VBox buildCardForPost(Post post) </p>
	 *
	 * <p> Description: Extracts the displayable fields from a Post and delegates
	 * the actual JavaFX card construction to the View's buildPostCard helper. This
	 * keeps post-data formatting (Controller responsibility) separate from widget-
	 * tree construction (View responsibility). </p>
	 *
	 * @param post the Post object whose fields should be displayed
	 *
	 * @return a styled VBox card representing the post
	 *
	 */
	private static VBox buildCardForPost(Post post) {

		String body = post.getBody() == null ? "" : post.getBody();
		String excerpt = (body.length() > BODY_EXCERPT_LENGTH)
				? body.substring(0, BODY_EXCERPT_LENGTH) + "..."
				: body;

		String timestamp = "";
		if (post.getCreatedAt() != null) {
			timestamp = post.getCreatedAt().format(
					java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
		}

		return ViewRelatedPosts.buildPostCard(
				post.getThread() == null ? "" : post.getThread(),
				post.getTitle() == null ? "" : post.getTitle(),
				post.getAuthorUsername() == null ? "" : post.getAuthorUsername(),
				timestamp,
				excerpt);
	}


	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: This method establishes the list of widgets in the root Pane.
	 * The widget list is fixed here (no longer depending on whether there are matching
	 * posts) because the empty-state message has been moved into the card-list VBox,
	 * so the page structure itself stays constant. </p>
	 *
	 */
	protected static void repaintTheWindow() {

		ViewRelatedPosts.theRootPane.getChildren().clear();

		ViewRelatedPosts.theRootPane.getChildren().addAll(
				ViewRelatedPosts.label_UserDetails,
				ViewRelatedPosts.label_PageTitle,
				ViewRelatedPosts.label_Subtitle,
				ViewRelatedPosts.button_Cancel,
				ViewRelatedPosts.button_Logout,
				ViewRelatedPosts.button_Quit,
				ViewRelatedPosts.textfield_Search,
				ViewRelatedPosts.button_Search,
				ViewRelatedPosts.button_Reset,
				ViewRelatedPosts.label_ResultsHeader,
				ViewRelatedPosts.scrollPane_PostCards,
				ViewRelatedPosts.button_ContinueToCreate,
				ViewRelatedPosts.line_Separator4);

		ViewRelatedPosts.theStage.setTitle("Find Related Posts");
		ViewRelatedPosts.theStage.setScene(ViewRelatedPosts.theRelatedPostsScene);
		ViewRelatedPosts.theStage.show();
	}


	/**********
	 * <p> Method: performCancel() </p>
	 *
	 * <p> Description: This method returns the student to the Discussion Board.
	 * Cancel is used (instead of "Return to Student Home") because this screen is
	 * now reached from the Discussion Board's "+" New Post button, so cancelling
	 * the posting flow should drop the student back where they started rather than
	 * one level higher at the Student Home page. </p>
	 *
	 */
	protected static void performCancel() {
		guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(
				ViewRelatedPosts.theStage, ViewRelatedPosts.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: This method logs out the current user and proceeds to the
	 * normal login page, consistent with every other Foundations-SU26 controller's
	 * performLogout method. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRelatedPosts.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: This method terminates the execution of the program,
	 * consistent with every other Foundations-SU26 controller's performQuit method.
	 * </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}