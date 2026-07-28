package entityClasses;
 
import java.time.LocalDateTime;
 
/*******
 * <p> Title: Feedback Class. </p>
 *
 * <p> Description: Represents a single piece of private feedback a staff member leaves
 * on a specific post, addressed to a user.
 * Feedback is visible to any staff member and to the user it is addressed to,
 * but never to any other student. </p>
 *
 * <p> Copyright: Ty Woolman © 2026 </p>
 *
 * @author Ty Woolman
 *
 */

public class Feedback {
	 
	private int feedbackID;
	private int postID;
	private String staffUsername;
	private String targetUsername;
	private String body;
	private LocalDateTime createdAt;
 
	/**********
	 * <p> Method: Feedback(int postID, String staffUsername, String targetUsername, String body) </p>
	 *
	 * <p> Description: Constructs a new Feedback object. The database generated feedbackID and 
	 * createdAt are set afterward by their setters. </p>
	 *
	 */
	public Feedback(int postID, String staffUsername, String targetUsername, String body) {
		this.postID = postID;
		this.staffUsername = staffUsername;
		this.targetUsername = targetUsername;
		this.body = body;
	}
 
	public int getFeedbackID() { return feedbackID; }
	public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }
 
	public int getPostID() { return postID; }
	public void setPostID(int postID) { this.postID = postID; }
 
	public String getStaffUsername() { return staffUsername; }
	public void setStaffUsername(String staffUsername) { this.staffUsername = staffUsername; }
 
	public String getTargetUsername() { return targetUsername; }
	public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }
 
	public String getBody() { return body; }
	public void setBody(String body) { this.body = body; }
 
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
