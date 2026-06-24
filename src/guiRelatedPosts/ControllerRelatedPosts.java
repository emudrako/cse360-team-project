package guiRelatedPosts;

import java.util.List;
import entityClasses.Post;
import entityClasses.PostList;

/*******
 * <p> Title: ControllerRelatedPosts Class. </p>
 *
 * <p> Description: The Java/FX-based Related Posts Page controller. This class implements
 * Student User Story 2: "View Related Posts from Others." Before a student posts a question
 * or statement, this controller retrieves the existing posts made by other students so the
 * student can check whether the question has already been asked, helping avoid duplicate
 * posts and surfacing answers that already exist.
 *
 * The Acceptance Criteria for Story 2 require that:
 * 	(1) the screen displays posts from other students that may relate to the topics the
 * 		student is interested in,
 * 	(2) each post entry shows enough information to assess relevance, and
 * 	(3) the list renders without error even when no related posts exist.
 *
 * This controller satisfies those criteria by retrieving every Post from the shared PostList,
 * filtering out the current student's own posts (Story 2 is specifically about posts "from
 * others") and any post marked isDeleted (a soft-deleted post should not be shown to a student
 * browsing for related content), and then building one display line per remaining post that
 * includes the title, author, thread, and a short excerpt of the body — enough for the student
 * to judge relevance without needing to open the full post.
 *
 * The class has been written assuming that the View or the Model are the only class methods
 * that can invoke these methods. This is why each has been declared as protected, consistent
 * with every other controller in the Foundations-SU26 code (e.g. ControllerAddRemoveRoles,
 * ControllerDeleteUser). Do not change any of these methods to public.</p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00		2026-06-20 Initial version (Student User Story 2: View Related Posts
 * 									from Others)
 *
 */

public class ControllerRelatedPosts {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated. Rather, it is a collection of
	protected static methods that can be called by the View (which is a singleton instantiated
	object) and the Model is often just a stub, consistent with the rest of Foundations-SU26.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRelatedPosts() {
	}

	// The maximum number of characters of a post's body to show in the list, so a very long post
	// does not overwhelm the display. This keeps every entry roughly the same height, which
	// matters for a ListView showing a variable number of rows.
	private static final int BODY_EXCERPT_LENGTH = 80;

	// Reference to the shared, in-memory store of all Post objects created so far. This mirrors
	// how the rest of Foundations-SU26 keeps a single shared reference to the Database
	// (applicationMain.FoundationsMain.database) rather than each page creating its own copy.
	// Story 4 (Sara) and Story 1 (Maranda) read from and write to this same PostList, so all
	// student-facing pages must share this one instance rather than each holding a private list.
	protected static PostList thePostList = applicationMain.FoundationsMain.postList;


	/**********
	 * <p> Method: loadRelatedPosts() </p>
	 *
	 * <p> Description: This method retrieves every Post from the shared PostList, filters out
	 * the current student's own posts and any posts that have been soft-deleted, and populates
	 * the ListView on ViewRelatedPosts with one line per remaining post.
	 *
	 * If, after filtering, there are no related posts to show, this method instead displays the
	 * "No related posts found" message so the page still renders cleanly with zero posts, per
	 * the Story 2 Acceptance Criteria ("The list renders without error even when no related
	 * posts exist"). This follows the same pattern ViewAddRemoveRoles uses for showing one of
	 * two possible layouts depending on the current state of the data. </p>
	 *
	 */
	protected static void loadRelatedPosts() {

		ViewRelatedPosts.listView_RelatedPosts.getItems().clear();

		List<Post> allPosts = thePostList.getAllPosts();
		String currentUsername = ViewRelatedPosts.theUser.getUserName();

		for (Post post : allPosts) {

			// Skip posts authored by the current student — Story 2 is specifically about
			// surfacing posts "from others," not the student's own posts.
			if (post.getAuthorUsername().equals(currentUsername)) continue;

			// Skip soft-deleted posts (Story 6) — a deleted post should not appear to a student
			// browsing for related content, even though it is preserved in the PostList so
			// existing replies to it can still show the "original post has been deleted" message.
			if (post.getIsDeleted()) continue;

			ViewRelatedPosts.listView_RelatedPosts.getItems().add(formatPostEntry(post));
		}

		repaintTheWindow();
	}


	/**********
	 * <p> Method: String formatPostEntry(Post post) </p>
	 *
	 * <p> Description: This method builds the single display line for one Post, showing the
	 * title, author, thread, and a short excerpt of the body. These four pieces of information
	 * are exactly what the Story 2 Acceptance Criteria require: "enough information to assess
	 * relevance" without needing to open the full post. </p>
	 *
	 * @param post specifies the Post to be formatted for display
	 *
	 * @return a String containing the formatted display line for this Post
	 *
	 */
	private static String formatPostEntry(Post post) {

		String body = post.getBody();
		String excerpt = (body.length() > BODY_EXCERPT_LENGTH)
				? body.substring(0, BODY_EXCERPT_LENGTH) + "..."
				: body;

		return "[" + post.getThread() + "] " + post.getTitle() +
				"  —  by " + post.getAuthorUsername() + "\n        " + excerpt;
	}


	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: This method determines the current state of the page (whether there are
	 * any related posts to show) and then establishes the appropriate list of widgets in the
	 * Pane, following the same dynamic-content pattern used by ControllerAddRemoveRoles and
	 * ControllerSetOneTimePassword. </p>
	 *
	 */
	protected static void repaintTheWindow() {

		ViewRelatedPosts.theRootPane.getChildren().clear();

		boolean hasRelatedPosts = !ViewRelatedPosts.listView_RelatedPosts.getItems().isEmpty();

		if (hasRelatedPosts) {
			ViewRelatedPosts.theRootPane.getChildren().addAll(
					ViewRelatedPosts.label_PageTitle, ViewRelatedPosts.label_UserDetails,
					ViewRelatedPosts.button_UpdateThisUser, ViewRelatedPosts.line_Separator1,
					ViewRelatedPosts.label_RelatedPostsTitle,
					ViewRelatedPosts.listView_RelatedPosts,
					ViewRelatedPosts.line_Separator4,
					ViewRelatedPosts.button_Return, ViewRelatedPosts.button_Logout,
					ViewRelatedPosts.button_Quit);
		}
		else {
			// No related posts exist (or all of this student's own posts were filtered out),
			// so show the empty-state message instead of an empty ListView. This satisfies the
			// Story 2 Acceptance Criteria requirement that the screen "renders without error"
			// when there are zero related posts.
			ViewRelatedPosts.theRootPane.getChildren().addAll(
					ViewRelatedPosts.label_PageTitle, ViewRelatedPosts.label_UserDetails,
					ViewRelatedPosts.button_UpdateThisUser, ViewRelatedPosts.line_Separator1,
					ViewRelatedPosts.label_RelatedPostsTitle,
					ViewRelatedPosts.label_NoRelatedPosts,
					ViewRelatedPosts.line_Separator4,
					ViewRelatedPosts.button_Return, ViewRelatedPosts.button_Logout,
					ViewRelatedPosts.button_Quit);
		}

		ViewRelatedPosts.theStage.setTitle("CSE 360 Foundations: Related Posts Page");
		ViewRelatedPosts.theStage.setScene(ViewRelatedPosts.theRelatedPostsScene);
		ViewRelatedPosts.theStage.show();
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: This method returns the student to the Student Home page, consistent
	 * with how every other Foundations-SU26 sub-page returns to its role's home page (e.g.
	 * ControllerAddRemoveRoles.performReturn returns Admins to ViewAdminHome). </p>
	 *
	 */
	protected static void performReturn() {
		guiStudentHome.ViewStudentHome.displayStudentHome(ViewRelatedPosts.theStage,
				ViewRelatedPosts.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page, consistent with every other Foundations-SU26 controller's performLogout method. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRelatedPosts.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: This method terminates the execution of the program, consistent with
	 * every other Foundations-SU26 controller's performQuit method. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}