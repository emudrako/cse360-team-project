package tests;

import java.sql.SQLException;

import database.Database;
import entityClasses.Post;

/*******
 * <p> Title: PostToThreadTests. </p>
 *
 * <p> Description: This class is a console-based testbed that tests Story 5
 * requirements of allowing a user to create a post for a specific thread. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 */

public class PostToThreadTests {
	private static Database db;
	private static int passCount = 0;
	private static int failCount = 0;
	private static int testCaseNum = 0;
	
	public static void main(String[] args) throws SQLException {
		try {
			db = new Database();
			db.connectToDatabase();
		} catch (SQLException e) {
			System.err.println("Could not connect to database: " + e.getMessage());
			return;
		}
		// Print statement to show in console what test is being run
		System.out.println("===== Story 5: Post to a Specific Thread =====\n");
		// Calls the test cases for Story 5
		testCase1();
		testCase2();
		testCase3();
		// Print statement to show the test results
		System.out.println("\n===== Results: " + passCount + " passed, " + failCount + " failed =====");
		db.closeConnection();
	}
	
	/**********
	 * <p> Method: testPostToThread </p>
	 *
	 * <p> Description: Tests Story 5 - Attempts to create a Post objects by passing a title, body, and
	 * thread as arguments to the Post constructor. If successful, the method then attempts to save the
	 * post data in the database. The getThread method will then be called to ensure the appropriate thread
	 * is returned. </p>
	 * 
	 * <p> Requirement: Story 5 - A student can create a post for a specific thread. </p>
	 * 
	 * <p> Pass Condition: TEST CASE PASSED prints if the getThread method for the Post object and the
	 * getThread method for the database both return the correct thread name. TEST CASE FAILED prints 
	 * if either the getThread method for the Post object or the getThread method for the database return
	 * the incorrect thread name. </p>
	 * 
	 * @param title  Specifies the title of the post that will be passed as an argument to construct a Post object
	 * 
	 * @param body  Specifies the body of the post that will be passed as an argument to construct a Post object
	 * 
	 * @param thread  Specifies the thread that will be passed as an argument to construct a Post object
	 * 
	 */
	public static void testPostToThread(String title, String body, String thread) {
		System.out.println("Test Case " + testCaseNum + ":\n\n"
	    		+ "Post Title: " + title + "\n"
	    		+ "Post Body: " + body + "\n"
	    		+ "Post Thread: " + thread + "\n");
		
		Post post = new Post(title, body, ("TestCase. " + testCaseNum), thread);
		
		 try {
		       db.createPost(post);
		    } catch (Exception e) {
		        System.out.println("Error creating post in the database: " + e.getMessage());
		        // Increments failCount if post could not be created in the database
		        failCount++;
		        return;
		    }
		 
		 String postObjectThread = post.getThread();
		 String dbPostThread;
		 try {
			 dbPostThread = db.getThread(post.getPostID());
		    } catch (Exception e) {
		        System.out.println("Error getting post thread from the database: " + e.getMessage());
		        return;
		    }
		 
		 System.out.println("Post thread retrieved from Post object: " + '"' + postObjectThread + '"');
		 System.out.println("Post thread retrieved from database: " + '"' + dbPostThread + '"' + "\n");
		 
		 if(!postObjectThread.isBlank() && !dbPostThread.isBlank()) {
			 passCount++;
			 System.out.println("Test Case #" + testCaseNum + " PASSED\n"
			    		+ "*--------------------*\n");
		 }
		 else {
			 failCount++;
			 System.out.println("Test Case #" + testCaseNum + " FAILED\n"
			    		+ "*--------------------*\n");
		 } 
		 db.permanentDeletePost(post.getPostID());
	}
	
	// Positive test case 1 - Specified thread is "Homework" which is a valid thread
	public static void testCase1() {
		String title = "Homework help";
	    String body = "Has anyone started the homework and would be willing"
	    		+ "to help me?";
	    String thread = "Homework";
	    
	    testCaseNum++;
	    testPostToThread(title, body, thread);
	}
	
	// Positive test case 2 - Specified thread is blank, therefore it will default to
	// "General"
	private static void testCase2() {
		String title = "Homework 3 help needed!";
		String body = "Is anyone available to help me understand the "
				    		+ "requirements for HW3?";
		String thread = "";
		    
		testCaseNum++;
		testPostToThread(title, body, thread);
	}
	
	// Negative test case 3 - Specified thread is "Music" which is not a valid thread.
	// The createPost method in the database calls the PostReplyValidator.checkForValidThread
	// method and an error is printed.
	private static void testCase3() {
		String title = "Playlist Recommendations?";
		String body = "What does everyone listen to when doing homework?";
		String thread = "Music";
			    
		testCaseNum++;
		testPostToThread(title, body, thread);
	}
}