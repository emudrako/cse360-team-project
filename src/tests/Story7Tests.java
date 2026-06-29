package tests;

import database.Database;
import entityClasses.Post;
import entityClasses.PostList;
import java.sql.SQLException;
import java.util.List;

/*******
 * <p> Title: Story7Tests. </p>
 *
 * <p> Description: This class is a console-based testbed that tests story 7 requirements. </p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 */

public class Story7Tests {
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
		System.out.println("===== Story 7: Search all Posts Test Cases =====\n");
		// Call the test for story 7
		testStory7SearchAllPosts();
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
	 * <p> Method: testStory7SearchAllPosts() </p>
	 *
	 * <p> Description: Tests Story 7 - Search All Posts by Keyword Across Threads.
	 * Creates test posts across multiple threads, loads them into a PostLists, and verifies that
	 * keyword search returns matching posts and handles no matches gracefully. </p>
	 * 
	 * <p> Requirement: Story 7- A student can search for posts using a keyword
	 * across all threads from the Discussion board page</p>
	 * 
	 * <p> Pass Condition: PASS prints if the keyword entered returns at least one matching post
	 * for the positive test, and an empty list for the negative test. FAIL prints if the search 
	 * results unexpected results.</p>
	 * 
	 * @throws SQLException 
	 */
	private static void testStory7SearchAllPosts() throws SQLException {
		System.out.println("-- Story 7: Search All Posts --");
		// Create fake posts for testing
		Post p1 = new Post("Homework 2 Help", "I keep getting this error", "testuser1", "Homework");
		db.createPost(p1);
		Post p2 = new Post("Quiz study tips", "Any advice on how to study for the quizzes?", "testuser1", "Quizzes");
		db.createPost(p2);
		Post p3 = new Post("Javadoc not compiling", "I keep getting an error when trying to run my Javadoc", "testuser2", "General");
		db.createPost(p3);
		Post p4 = new Post("Confused on HW1 requirements", "I am confused on the unclear HW1 requirements.", "testuser1", "Homework");
		db.createPost(p4);
		Post p5 = new Post("Team Project grades", "Has anyone's grade for the project been posted?", "testuser2", "General");
		db.createPost(p5);
		
		// Create a list of posts with the posts just created
		PostList list = new PostList();
		List<Post> allPosts = db.getPostObjects();
		for (Post post : allPosts) {
		    list.addPost(post);
		}
		// Positive: Searching for a specific keyword that exists in a post(s) 
		// in the post list returns posts with that keyword
		List<Post> positiveResults = list.getPostsByKeyword("Javadoc");
		check(positiveResults.size() > 0, "Story7-Positive", "Found matching posts");
		// Negative: Searching for a specific keyword that does not exist in any posts in 
	    // the post list returns an empty list of posts
		List<Post> negativeResults = list.getPostsByKeyword("xyz");
		check(negativeResults.isEmpty(), "Story7-Negative", "Found 0 posts");

	}
}