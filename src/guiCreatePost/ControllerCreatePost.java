package guiCreatePost;

import database.Database;

/*******
 * <p> Title: ControllerCreatePost Class. </p>
 * 
 * <p> Description: The Java/FX-based My Posts page.  Supports Story 1 (Create a Post) and Story 5 (Post to a Specific Thread). 
 * This class provides the controller actions for the Create Post page including ability for a student
 *  to compose a new post with a title, body, and thread selection, validates input and submits it to the discussion board database.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * @version 1.00		2026-06-23 Initial version
 */

public class ControllerCreatePost {
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCreatePost() {}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	

	/**********
	 * <p> Method: performCreatePost() </p>
	 * 
	 * <p> Description: This method validates the title and body input, saves it to the database,
	 * clears the form fields, and navigates back to the Discussion Board,
	 * Displays an error message if validation fails or a database exception occurs. </p>
	 * 
	 */
	protected static void performCreatePost() {
	    String title = ViewCreatePost.textfield_Title.getText();
	    String body = ViewCreatePost.textarea_Body.getText();
	    String thread = ViewCreatePost.combobox_Thread.getValue();
	    
	    // If no thread is selected, the thread defaults to General
	    if(thread.isEmpty()) {
	    	thread = "General";
	    }
	    // Validate input using PostReplyValidator
	    String errMsg = recognizers.PostReplyValidator.checkForValidPost(title, body);
	    if (!errMsg.isEmpty()) {
	        ViewCreatePost.label_ErrorMessage.setText(errMsg);
	        return;
	    }
	    
	    // Create the post and save to database
	    entityClasses.Post post = new entityClasses.Post(title, body, 
	        ViewCreatePost.theUser.getUserName(), thread);
	    try {
	        theDatabase.createPost(post);
	        // Clear fields and return to student home on success
	        ViewCreatePost.textfield_Title.clear();
	        ViewCreatePost.textarea_Body.clear();
	        ViewCreatePost.combobox_Thread.setValue("General");
	        ViewCreatePost.label_ErrorMessage.setText("");
	        guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(
	        	    ViewCreatePost.theStage, ViewCreatePost.theUser);
	    } catch (Exception e) {
	        ViewCreatePost.label_ErrorMessage.setText("Error creating post: " + e.getMessage());
	    }
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the user's homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiDiscussionBoard.ViewDiscussionBoard.displayDiscussionBoard(ViewCreatePost.theStage, ViewCreatePost.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page. </p>
	 * 
	 */
	protected static void performLogout() {
	    guiUserLogin.ViewUserLogin.displayUserLogin(ViewCreatePost.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
	
}