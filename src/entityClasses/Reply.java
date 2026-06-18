package entityClasses;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: This Reply class represents a reply entity in the system. It contains the replies details such as the author who created it,
 * the body content and which post it belongs to   </p>
 * 
 * <p> Copyright: Maranda Martinez © 2026 </p>
 * 
 * @author Maranda Martinez
 * 
 * 
 */ 

public class Reply {
	/*
	 * These are the private attributes for this entity object
	 */
	private int replyID;
	private int postID;
	private String body;
	private String authorUsername;
	
	/*****
	 * <p> Method: Reply() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public Reply() {
		
	}
	
	
    /*****
     * <p> Method: Reply(int postID, String body, String authorUsername) </p>
     *
     * <p> Description:  This constructor is used to establish Reply entity objects</p>
     *
     * @param postID specifies the ID of the post this reply is responding to
     *
     * @param body specifies the body content of this reply
     *
     * @param authorUsername specifies the username of the creator of the reply
     *
     */
    // Constructor to initialize a new Reply object
    public Reply(int postID, String body, String authorUsername) {
    	this.postID = postID;
    	this.body = body;
    	this.authorUsername = authorUsername;
    }
    
    
	/*****
	 * <p> Method: int getReplyID() </p>
	 * 
	 * <p> Description: This getter returns the ReplyID. </p>
	 * 
	 * @return an int of the ReplyID
	 * 
	 */
	// Gets the current value of the ReplyID.
	public int getReplyID() { return replyID; }

	
	/*****
	 * <p> Method: void setReplyID(int id) </p>
	 * 
	 * <p> Description: This setter defines the ReplyID attribute. </p>
	 * 
	 * @param id specifies the ReplyID assigned to this reply by the database
	 * 
	 */
	// Sets the ReplyID once the database has generated it.
	public void setReplyID(int id) { replyID = id; }

	
	/*****
	 * <p> Method: int getPostID() </p>
	 * 
	 * <p> Description: This getter returns the PostID. </p>
	 * 
	 * @return an int of the PostID this reply is responding to
	 * 
	 */
	// Gets the current value of the PostID.
	public int getPostID() { return postID; }

	
	/*****
	 * <p> Method: void setPostID(int id) </p>
	 * 
	 * <p> Description: This setter defines the PostID attribute. </p>
	 * 
	 * @param id specifies which post this reply is responding to
	 * 
	 */
	// Sets the PostID.
	public void setPostID(int id) { postID = id; }

	
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
	 * @param b specifies the new body content for this reply
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
	 * @param a specifies the username of the student who authored this reply
	 * 
	 */
	// Sets the AuthorUsername.
	public void setAuthorUsername(String a) { authorUsername = a; }

}