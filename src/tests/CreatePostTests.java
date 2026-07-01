package tests;

import java.sql.SQLException;

import database.Database;
import entityClasses.Post;

/*******
 * <p> Title: CreatePostTests. </p>
 *
 * <p> Description: This class is a console-based testbed that tests Story 1
 * requirements of allowing a user to create a post. Each testCase method will
 * call the testCreatePost method. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 */

public class CreatePostTests {
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
		System.out.println("===== Story 1: Create a Post Test Cases =====\n");
		// Calls the test cases for Story 1
		testCase1();
		testCase2();
		testCase3();
		testCase4();
		// Print statement to show the test results
		System.out.println("\n===== Results: " + passCount + " passed, " + failCount + " failed =====");
		db.closeConnection();
	}
	
	/**********
	 * <p> Method: testCreatePost </p>
	 *
	 * <p> Description: Tests Story 1 - Attempts to create Post objects by passing a title, body, and
	 * thread as arguments to the Post constructor. If successful, the method then attempts to save the
	 * post data in the database. </p>
	 * 
	 * <p> Requirement: Story 1 - A student can enter a post title, body, and select a thread to create a
	 * post. </p>
	 * 
	 * <p> Pass Condition: TEST CASE PASSED prints if the post title and body passed the input validation checks,
	 * the Post object was successfully created, and the Post data was saved to the database. TEST CASE FAILED
	 * prints if the post title and body failed the input validation checks or the Post data was not successfully
	 * saved to the database. </p>
	 * 
	 * @throws SQLException 
	 */
	private static void testCreatePost(String title, String body, String thread) {
		System.out.println("Test Case " + testCaseNum + ":\n\n"
	    		+ "Post Title: " + title + "\n"
	    		+ "Post Body: " + body + "\n"
	    		+ "Post Thread: " + thread + "\n");
		
		// Validate input using PostReplyValidator
	    String errMsg = recognizers.PostReplyValidator.checkForValidPost(title, body);
	    if (!errMsg.isEmpty()) {
	    	// Increments failCount if post could not be created
	        failCount++;
	    	System.out.println("Error cerating post" + errMsg);
	        System.out.println("Test Case #" + testCaseNum + " FAILED\n"
	        		+ "*--------------------*");
	        return;
	    }
	    else {
	    	System.out.println("Post title and body passed input validation check.\n");
	    }
	    
	    // Create the post and save it to the database
	    Post post = new Post(title, body, 
	        ("TestCase." + testCaseNum), thread);
	    try {
	       db.createPost(post);
	    } catch (Exception e) {
	        System.out.println("Error creating post in the database: " + e.getMessage());
	        // Increments failCount if post could not be created in the database
	        failCount++;
	        System.out.println("Test Case #" + testCaseNum + " FAILED\n"
	        		+ "*--------------------*");
	        return;
	    }
	    // Increments passCount if post was successfully created in the database
	    passCount++;
	    System.out.println("Post successfully saved in the database.\n");
	    System.out.println("Test Case #" + testCaseNum + " PASSED\n"
	    		+ "*--------------------*");
	    db.permanentDeletePost(post.getPostID());
	}
	
	// Positive test case 1 - title, body, and thread all meet the input requirements
	private static void testCase1() {
		String title = "Study Group";
	    String body = "I am creating a study group. If anyone is interested in "
	    		+ "joining, please let me know.";
	    String thread = "General";
	    
	    testCaseNum++;
	    testCreatePost(title, body, thread);
	}
	
	// Positive test case 2 - title, body, and thread all meet the input requirements
	private static void testCase2() {
		String title = "Homework 3 help needed!";
		String body = "Is anyone available to help me understand the "
			    		+ "requirements for HW3?";
		String thread = "Homework";
	    
		testCaseNum++;
		testCreatePost(title, body, thread);
	}
	
	// Negative test case 3 - title is empty so an error is printed
	private static void testCase3() {
		String title = "";
		String body = "When is TP2 due?";
		String thread = "Homework";
		    
		testCaseNum++;
		testCreatePost(title, body, thread);
		}
	
	// Negative test case 4 - body exceeds maximum allowed length so an error is printed
	private static void testCase4() {
		String title = "Mid-term study guide";
		String body = "fqefiuhakdfjnaklrjtgnakljfhadkfjgalkjfadlkgnakljdhfakjdghakljfaskdfjngakjsfakljthgfakljfnakldjthakljfnakljtnfakljfncaklrtjgaklwhefaskrtjlnaakjaklejhfalkrdjhalksfjhsdaskdhfaskedjfalksjfhaksjdhl"
				+ "asfkljhaskejlrhaklejfaklhjfaklhsjefdklhjsadfklhjafkljdhfklahjfkljsdhafkljshadfkljhafkljhaefkljnafkljansfkljhafklhjsaflkjnafkljsafnklajhfakdnsjfakljfnakljfdklsnjfdkljsdafnkljsnfakljrafhakldjsnakljfna"
				+ "alkfjhakljfnakldjfaskjfalkerjfalkdjfnakljhfalkjsefakljsnfaklnjsfglkajfehakletjfkldjsnfaklejnfkljsfklaenjrfakldfnkalejnrfkldjsfnklajfnskjdafakljnfklsjnaflkjsnaflkjsadnflkjnaflkjnsadfkljaneflkjnsdaflkaj"
				+ "asdflkjnsaflkjnasfdkljnakejsfnskdjanakljfnlkjsdnaflkjsnafkljhewqatlkahdglkanjfkaljsnvcalkienmfa;oliegnalkdjsngfeawq;lrnasdk;lgnalkenjfgaldkjfngalkenjgalkrgjnaskdfnvlgaleknjfalkdgsnalknjfaskgnjalkngfas"
				+ "askdjflnasktgnjalkdnsalkengfalkdngjalkdgsjalkngaldkghna;lkengalkergnas;kgdjnaelkgjnag;lkjnsaedglakergnaKDgsjlnalkejngasdfglkjnafeglkahnral;kdgjnaerlgkjunagalkegnaskdgfljnaeglkjnsadglkjnaglkandgflkejjn"
				+ "askfjlhaskjtlgaskfjlnakletjaelkfjnaetkljahsfdlknjatgakljfnalkrtjnaskjfdlakltjhakljsfnakldrtnaklfjshakljyhtewq;atjfaporqjo[pajfpoiagnaoiklgnwsgniakdlghqapoeitjqeporighadfk;ga;seilhjtoawpiejfa;lfikja;lo";
		String thread = "Quizzes";
			    
		testCaseNum++;
		testCreatePost(title, body, thread);
		}
}