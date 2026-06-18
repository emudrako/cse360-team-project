package entityClasses;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: This Post class represents a discussion post entity in the system.  It contains
 *  the post's details such as the title, body content, the author who created it, the thread it
 *  belongs to, and whether it has been deleted. </p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * 
 */ 

public class Post {
	/*
	 * These are the private attributes for this entity object
	 */
	private int postID;
	private String title;
	private String body;
	private String authorUsername;
	private String thread;
	private boolean isDeleted;

	/*****
	 * <p> Method: Post() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public Post() {
		
	}

    /*****
     * <p> Method: Post(String title, String body, String authorUsername, String thread) </p>
     *
     * <p> Description: This constructor is used to establish Post entity objects. </p>
     *
     * @param title specifies the title of this post
     *
     * @param body specifies the body content of this post
     *
     * @param authorUsername specifies the username of the author who created the post
     *
     *@param thread specifies which thread this post belongs to
     *
     */
    // Constructor to initialize a new Post object with title, body, author username and thread 
    public Post(String title, String body, String authorUsername, String thread) {
        this.title = title;
        this.body = body;
        this.authorUsername = authorUsername;
        this.thread = thread;
        this.isDeleted = false; // Used for tracking deleted posts
    }
    
	/*****
	 * <p> Method: int getPostID() </p>
	 * 
	 * <p> Description: This getter returns the PostID. </p>
	 * 
	 * @return an int of the PostID
	 * 
	 */
	// Gets the current value of the PostID.
	public int getPostID() { return postID; }

	
	/*****
	 * <p> Method: void setPostID(int id) </p>
	 * 
	 * <p> Description: This setter defines the PostID attribute. </p>
	 * 
	 * @param id specifies the PostID assigned to this post by the database
	 * 
	 */
	// Sets the PostID once the database has generated it.
	public void setPostID(int id) { postID = id; }

	
	/*****
	 * <p> Method: String getTitle() </p>
	 * 
	 * <p> Description: This getter returns the Title. </p>
	 * 
	 * @return a String of the Title
	 * 
	 */
	// Gets the current value of the Title.
	public String getTitle() { return title; }

	
	/*****
	 * <p> Method: void setTitle(String t) </p>
	 * 
	 * <p> Description: This setter defines the Title attribute. </p>
	 * 
	 * @param t specifies the new title for this post
	 * 
	 */
	// Sets the Title.
	public void setTitle(String t) { title = t; }

	
	/*****
	 * <p> Method: String getBody() </p>
	 * 
	 * <p> Description: This getter returns the Body. </p>
	 * 
	 * @return a String of the Body
	 * 
	 */
	// Gets the current value of the Body.
	public String getBody() { return body; }

	
	/*****
	 * <p> Method: void setBody(String b) </p>
	 * 
	 * <p> Description: This setter defines the Body attribute. </p>
	 * 
	 * @param b specifies the new body content for this post
	 * 
	 */
	// Sets the Body.
	public void setBody(String b) { body = b; }

	
	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * 
	 * <p> Description: This getter returns the AuthorUsername. </p>
	 * 
	 * @return a String of the AuthorUsername
	 * 
	 */
	// Gets the current value of the AuthorUsername.
	public String getAuthorUsername() { return authorUsername; }

	
	/*****
	 * <p> Method: void setAuthorUsername(String a) </p>
	 * 
	 * <p> Description: This setter defines the AuthorUsername attribute. </p>
	 * 
	 * @param a specifies the username of the student who authored this post
	 * 
	 */
	// Sets the AuthorUsername.
	public void setAuthorUsername(String a) { authorUsername = a; }

	
	/*****
	 * <p> Method: String getThread() </p>
	 * 
	 * <p> Description: This getter returns the Thread. </p>
	 * 
	 * @return a String of the Thread
	 * 
	 */
	// Gets the current value of the Thread.
	public String getThread() { return thread; }

	
	/*****
	 * <p> Method: void setThread(String th) </p>
	 * 
	 * <p> Description: This setter defines the Thread attribute. </p>
	 * 
	 * @param th specifies which thread this post belongs to
	 * 
	 */
	// Sets the Thread.
	public void setThread(String th) { thread = th; }

	
	/*****
	 * <p> Method: boolean getIsDeleted() </p>
	 * 
	 * <p> Description: This getter returns the IsDeleted attribute. </p>
	 * 
	 * @return a boolean of TRUE if this post has been soft-deleted, FALSE otherwise
	 * 
	 */
	// Gets the current value of the IsDeleted attribute.
	public boolean getIsDeleted() { return isDeleted; }

	
	/*****
	 * <p> Method: void setIsDeleted(boolean d) </p>
	 * 
	 * <p> Description: This setter defines the IsDeleted attribute. </p>
	 * 
	 * @param d specifies TRUE if this post should be marked as soft-deleted, FALSE otherwise
	 * 
	 */
	// Sets the IsDeleted attribute.
	public void setIsDeleted(boolean d) { isDeleted = d; }
}