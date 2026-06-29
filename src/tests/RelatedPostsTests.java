package tests;

import database.Database;
import entityClasses.Post;
import entityClasses.PostList;
import guiRelatedPosts.ControllerRelatedPosts;

import java.sql.SQLException;
import java.util.List;

/*******
 * <p> Title: RelatedPostsTests. </p>
 *
 * <p> Description: This class is a console-based testbed that tests Story 2 and
 * Story 4 requirements. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 */

public class RelatedPostsTests {
	private static Database db;
	private static int passCount = 0;
	private static int failCount = 0;

	public static void main(String[] args) throws SQLException {
		try {
			db = new Database();
			db.connectToDatabase();
		} catch (SQLException e) {
			System.err.println("Could not connect to database: " + e.getMessage());
			return;
		}
		// Print statement to show in console what test is being run
		System.out.println("===== Story 2 & 4: Related Posts Test Cases =====\n");
		// Call the tests for story 2 and 4
		testStory2FiltersOwnPosts();
		testStory4SearchByKeyword();
		// Print statement to show the test results
		System.out.println("\n===== Results: " + passCount + " passed, " + failCount + " failed =====");
		db.closeConnection();
	}

	// Helper method to report a pass/fail result with a message
	private static void check(boolean condition, String testName, String detail) {
		if (condition) {
			passCount++;
			System.out.println("PASS - " + testName + ": " + detail);
		} else {
			failCount++;
			System.out.println("FAIL - " + testName + ": " + detail);
		}
	}

	/**********
	 * <p> Method: testStory2FiltersOwnPosts() </p>
	 *
	 * <p> Description: Tests Story 2 - View Related Posts from Others.
	 * Creates test posts from two different authors, then verifies that
	 * filterPosts excludes the current student's own post and includes
	 * the other student's post. </p>
	 *
	 * <p> Requirement: Story 2 - A student can view posts from other
	 * students that may be related to their own question.</p>
	 *
	 * <p> Pass Condition: PASS prints if the current student's own post is
	 * excluded and the other student's post is included. FAIL prints if
	 * either is not handled correctly.</p>
	 *
	 * @throws SQLException
	 */
	private static void testStory2FiltersOwnPosts() throws SQLException {
		System.out.println("-- Story 2: View Related Posts --");
		// Create fake posts for testing
		Post p1 = new Post("My own question", "Help me with this", "testuser1", "General");
		db.createPost(p1);
		Post p2 = new Post("Other student's question", "Anyone know this?", "testuser2", "General");
		db.createPost(p2);

		List<Post> allPosts = db.getPostObjects();

		// Positive: current student should see the OTHER student's post
		List<Post> related = ControllerRelatedPosts.filterPosts(allPosts, "testuser1", "");
		boolean seesOther = false;
		for (Post p : related) {
			if (p.getAuthorUsername().equals("testuser2")) seesOther = true;
		}
		check(seesOther, "Story2-Positive", "Other student's post appears in related list");

		// Negative: current student should NOT see their own post
		boolean seesOwn = false;
		for (Post p : related) {
			if (p.getAuthorUsername().equals("testuser1")) seesOwn = true;
		}
		check(!seesOwn, "Story2-Negative", "Own post is excluded from related list");
	}

	/**********
	 * <p> Method: testStory4SearchByKeyword() </p>
	 *
	 * <p> Description: Tests Story 4 - View and Search Others' Posts by
	 * Keyword. Creates test posts and verifies that a keyword search
	 * returns matching posts, and that a non-matching keyword returns
	 * an empty list.</p>
	 *
	 * <p> Requirement: Story 4 - A student can search other students'
	 * posts by keyword from the Find Related Posts page.</p>
	 *
	 * <p> Pass Condition: PASS prints if the keyword entered returns at
	 * least one matching post for the positive test, and an empty list
	 * for the negative test. FAIL prints if the search returns
	 * unexpected results.</p>
	 *
	 * @throws SQLException
	 */
	private static void testStory4SearchByKeyword() throws SQLException {
		System.out.println("\n-- Story 4: Search Others' Posts by Keyword --");
		Post p1 = new Post("Recursion help", "I'm stuck on base cases", "testuser2", "Homework");
		db.createPost(p1);

		List<Post> allPosts = db.getPostObjects();

		// Positive: searching for a keyword that exists returns matching posts
		List<Post> positiveResults = ControllerRelatedPosts.filterPosts(allPosts, "testuser1", "recursion");
		check(positiveResults.size() > 0, "Story4-Positive", "Found matching posts");

		// Negative: searching for a keyword that does not exist returns an empty list
		List<Post> negativeResults = ControllerRelatedPosts.filterPosts(allPosts, "testuser1", "xyz");
		check(negativeResults.isEmpty(), "Story4-Negative", "Found 0 posts");
	}
}