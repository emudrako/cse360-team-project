package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: SearchRepliesTests. </p>
 *
 * <p> Description: This class is a console-based testbed that tests Story 8
 * requirements of allowing a user to search their replies by specifying a 
 * username and/or keyword. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 */

public class SearchRepliesTests {
	private static Database db;
	private static int passCount = 0;
	private static int failCount = 0;
	private static int testCaseNum = 0;
	private static List<Post> posts = new ArrayList<>();
	private static List<Reply> replies = new ArrayList<>();
	
	public static void main(String[] args) throws SQLException {
		try {
			db = new Database();
			db.connectToDatabase();
		} catch (SQLException e) {
			System.err.println("Could not connect to database: " + e.getMessage());
			return;
		}
		// Print statement to show in console what test is being run
		System.out.println("===== Story 8: Search Replies by Author and/or Keyword =====\n");
		// Calls the test cases for Story 8
		testCase1();
		testCase2();
		testCase3();
		// Print statement to show the test results
		System.out.println("\n===== Results: " + passCount + " passed, " + failCount + " failed =====");
		db.closeConnection();
	}
	
	/**********
	 * <p> Method: testSearchReplies </p>
	 *
	 * <p> Description: Tests Story 8 - Calls the getReplyObjects method from the database to build
	 * a list of Reply objects to be searched through. The currentUser variable will be used to specify
	 * the username of the simulated current user. Then, another username and/or a keyword will be 
	 * specified to search through the reply list to find a list of Reply objects that were created by
	 * the specified user and/or contain the specified keyword. </p>
	 * 
	 * <p> Requirement: Story 8 - A student can search through their replies to find replies matching
	 * a specified user and/or keyword. </p>
	 * 
	 * <p> Pass Condition: TEST CASE PASSED prints if the test case return a Reply that matches the specified
	 * user and/or keyword. TEST CASE FAILED prints if the test case returns a Reply that doesn't match the
	 * specified user and/or keyword </p>
	 * 
	 * @param currentUser  Specifies the user who created the post(s) that the replies are in response to
	 * 
	 * @param username  Specifies the username to search for replies from
	 * 
	 * @param keyword  Specifies the keyword to search for replies that contain it
	 * 
	 */
	
	private static void testSearchReplies(String currentUser, String username, String keyword) {
		System.out.println("Test Case " + testCaseNum + ":\n");
		
		try {
			posts = db.getPostObjects();
			replies = db.getReplyObjects();
		} catch (SQLException e) {
			System.err.println("Error getting posts of replies from the database: " + e.getMessage());
			return;
		}
		
		// Checks to see if specified user exists
		int count = 0;
		for (Reply reply: replies) {
			if (reply.getAuthorUsername().equals(username)) {
				count++;
			}
		}
		if (count == 0) {
			failCount++;
			System.out.println("User does not exist.");
			System.out.println("Test Case #" + testCaseNum + " FAILED\n"
		    		+ "*--------------------*");
			return;
		}
		
		List<Post> userPosts = new ArrayList<>();
		
		for (Post post : posts) {
			if (post.getAuthorUsername().equals(currentUser)) {
				userPosts.add(post);
			}
		}
		
		List<Reply> searchResults = new ArrayList<>();
		
		if (!username.isEmpty() && !keyword.isEmpty()) {
			for (Reply reply : replies) {
				for (Post post : userPosts) {
					if (reply.getPostID() == post.getPostID() && reply.getAuthorUsername().equals(username)
							&& reply.getBody().contains(keyword)) {
						searchResults.add(reply);
					}
				}
			}
		}
		if (!username.isEmpty() && keyword.isEmpty()) {
			for (Reply reply : replies) {
				for (Post post : userPosts) {
					if (reply.getPostID() == post.getPostID() && reply.getAuthorUsername().equals(username)) {
						searchResults.add(reply);
					}
				}
			}
		}
		if (username.isEmpty() && !keyword.isEmpty()) {
			for (Reply reply : replies) {
				for (Post post : userPosts) {
					if (reply.getPostID() == post.getPostID() && reply.getBody().contains(keyword)) {
						searchResults.add(reply);
					}
				}
			}
		}
		
		System.out.println("Username: " + username);
		System.out.println("Keyword: " + keyword + "\n");
		
		int numFailed = 0;
		if (searchResults.isEmpty()) {
			System.out.println("No results found matching specified username and/or keyword.\n");
		}
		else {
			int numCounted = 0;
			for (Reply reply : searchResults) {
				numCounted++;
				System.out.println("Reply " + numCounted + " - Username returned: " + reply.getAuthorUsername() + "\n"
						+ "Reply " + numCounted + " - Body returned: " + reply.getBody() +"\n");
				if (!reply.getAuthorUsername().equals(username) || !reply.getBody().contains(keyword)) {
					numFailed++;
				}
			}
		}
		
		if (numFailed > 0) {
			failCount++;
			System.out.println("Test Case #" + testCaseNum + " FAILED\n"
		    		+ "*--------------------*");
		}
		else {
			passCount++;
			System.out.println("Test Case #" + testCaseNum + " PASSED\n"
		    		+ "*--------------------*\n");
		}
		
	}
	
	// Positive test case 1 - Chris.Redfield is a valid username and he has a reply to a post from Jill.Valentine
	// that contains the word "team"
	private static void testCase1() {
		testCaseNum++;
		testSearchReplies("Jill.Valentine", "Chris.Redfield", "team");
	}
	
	// Positive test case 2 - Chris.Redfield is a valid username and he has a reply to a post from Jill.Valentine
	// that contains the word "team"
	private static void testCase2() {
		testCaseNum++;
		testSearchReplies("Jill.Valentine", "Chris.Redfield", "homework");
	}
	
	// Negative test case 3 - Barry.Burton is not a valid username
	private static void testCase3() {
		testCaseNum++;
		testSearchReplies("Jill.Valentine", "Barry.Burton", "help");
	}
	
}