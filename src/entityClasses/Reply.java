package entityClasses;

import java.time.LocalDateTime;

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
	private int parentReplyID;
	private String body;
	private String authorUsername;
	private LocalDateTime createdAt;
	private boolean hasReplies;
	private int numReplies;
	private boolean isFlagged;
	private String  flaggedBy;
	private LocalDateTime flaggedAt;
	private String  staffNote;   // private staff annotation, not visible to students
	private boolean isResolved;
	private String  resolvedBy;
	private LocalDateTime resolvedAt;
	
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
    	this.hasReplies = false;
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
	 * <p> Method: int getParentReplyID() </p>
	 * 
	 * <p> Description: This getter returns the parentReplyID. </p>
	 * 
	 * @return an int of the parentReplyID this reply is responding to
	 * 
	 */
	// Gets the current value of the PostID.
	public int getParentReplyID() { return parentReplyID; }

	
	/*****
	 * <p> Method: void setParentReplyID(int id) </p>
	 * 
	 * <p> Description: This setter defines the parentReplyID attribute. </p>
	 * 
	 * @param id specifies which reply this reply is responding to
	 * 
	 */
	// Sets the parentReplyID.
	public void setparentReplyID(int id) { parentReplyID = id; }

	
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
	 * <p> Method: void setBody(String body) </p>
	 * 
	 * <p> Description: This setter defines the Body attribute. </p>
	 * 
	 * @param body specifies the new body content for this reply
	 * 
	 */
	// Sets the Body.
	public void setBody(String body) { this.body = body; }

	
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
	 * <p> Method: void setAuthorUsername(String userName) </p>
	 * 
	 * <p> Description: This setter defines the AuthorUsername attribute. </p>
	 * 
	 * @param userName specifies the username of the student who authored this reply
	 * 
	 */
	// Sets the AuthorUsername.
	public void setAuthorUsername(String userName) { authorUsername = userName; }


	/*****
	 * <p> Method: LocalDateTime getCreatedAt() </p>
	 *
	 * <p> Description: This getter returns the timestamp when this reply was created. </p>
	 *
	 * @return a LocalDateTime of when this reply was created
	 *
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }


	/*****
	 * <p> Method: void setCreatedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the CreatedAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this reply was created
	 *
	 */
	public void setCreatedAt(LocalDateTime time) { createdAt = time; }
	
	
	/*****
	 * <p> Method: int getNumReplies() </p>
	 *
	 * <p> Description: This getter defines returns the int value
	 * for numReplies </p>
	 *
	 * @return the int value for numReplies
	 *
	 */
	public int getNumReplies() { return numReplies; }
	
	
	/*****
	 * <p> Method: void setNumReplies(int replyNum) </p>
	 *
	 * <p> Description: This setter defines the int value
	 * for numReplies </p>
	 *
	 * @param the int value for numReplies
	 *
	 */
	public void setNumReplies(int replyNum) { this.numReplies = replyNum; }
	
	
	/*****
	 * <p> Method: boolean getHasReplies() </p>
	 *
	 * <p> Description: This getter defines returns the boolean value
	 * for hasReplies </p>
	 *
	 * @return the boolean value for hasReplies
	 *
	 */
	public boolean getHasReplies() { return hasReplies; }
	
	
	/*****
	 * <p> Method: void setHasReplies(boolean replyStatus) </p>
	 *
	 * <p> Description: This setter defines the boolean hasReplies value. </p>
	 *
	 * @param replyStatus will be true to indicate that the Reply has replies
	 * or false to indicate that the Reply has no replies.
	 *
	 */
	public void setHasReplies(boolean replyStatus) { this.hasReplies = replyStatus; }


	/*****
	 * <p> Method: boolean getIsFlagged() </p>
	 *
	 * <p> Description: This getter returns the IsFlagged attribute. </p>
	 *
	 * @return a boolean TRUE if this reply has been flagged by staff, FALSE otherwise
	 *
	 */
	public boolean getIsFlagged() { return isFlagged; }


	/*****
	 * <p> Method: void setIsFlagged(boolean isFlagged) </p>
	 *
	 * <p> Description: This setter defines the IsFlagged attribute. </p>
	 *
	 * @param isFlagged specifies TRUE if this reply should be marked as flagged
	 *
	 */
	public void setIsFlagged(boolean isFlagged) { this.isFlagged = isFlagged; }


	/*****
	 * <p> Method: String getFlaggedBy() </p>
	 *
	 * <p> Description: This getter returns the FlaggedBy username. </p>
	 *
	 * @return a String of the username of the staff member who flagged this reply
	 *
	 */
	public String getFlaggedBy() { return flaggedBy; }


	/*****
	 * <p> Method: void setFlaggedBy(String username) </p>
	 *
	 * <p> Description: This setter defines the FlaggedBy attribute. </p>
	 *
	 * @param username specifies the username of the staff member flagging this reply
	 *
	 */
	public void setFlaggedBy(String username) { flaggedBy = username; }


	/*****
	 * <p> Method: String getStaffNote() </p>
	 *
	 * <p> Description: This getter returns the StaffNote. This is a private staff
	 *  annotation not visible to students. </p>
	 *
	 * @return a String of the staff note
	 *
	 */
	public String getStaffNote() { return staffNote; }


	/*****
	 * <p> Method: void setStaffNote(String note) </p>
	 *
	 * <p> Description: This setter defines the StaffNote attribute. </p>
	 *
	 * @param note specifies the private staff annotation for this reply
	 *
	 */
	public void setStaffNote(String note) { staffNote = note; }


	/*****
	 * <p> Method: boolean getIsResolved() </p>
	 *
	 * <p> Description: This getter returns the IsResolved attribute. </p>
	 *
	 * @return a boolean TRUE if this reply has been resolved by staff, FALSE otherwise
	 *
	 */
	public boolean getIsResolved() { return isResolved; }


	/*****
	 * <p> Method: void setIsResolved(boolean isResolved) </p>
	 *
	 * <p> Description: This setter defines the IsResolved attribute. </p>
	 *
	 * @param isResolved specifies TRUE if this reply has been resolved by staff
	 *
	 */
	public void setIsResolved(boolean isResolved) { this.isResolved = isResolved; }


	/*****
	 * <p> Method: String getResolvedBy() </p>
	 *
	 * <p> Description: This getter returns the ResolvedBy username. </p>
	 *
	 * @return a String of the username of the staff member who resolved this reply
	 *
	 */
	public String getResolvedBy() { return resolvedBy; }


	/*****
	 * <p> Method: void setResolvedBy(String username) </p>
	 *
	 * <p> Description: This setter defines the ResolvedBy attribute. </p>
	 *
	 * @param username specifies the username of the staff member resolving this reply
	 *
	 */
	public void setResolvedBy(String username) { resolvedBy = username; }
	
	public LocalDateTime getFlaggedAt() { return flaggedAt; }
	public void setFlaggedAt(LocalDateTime time) { flaggedAt = time; }

	public LocalDateTime getResolvedAt() { return resolvedAt; }
	public void setResolvedAt(LocalDateTime time) { resolvedAt = time; }
}