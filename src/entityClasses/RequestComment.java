package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: RequestComment Class </p>
 *
 * <p> Description: This Request class represents an admin request entity in the system.
 *  Staff can request Admins to perform admin-specific actions. Requests appear in an open
 *  list visible to Staff and Admins. Admins can document actions taken and close requests.
 *  Staff can reopen a closed request; reopened requests have a reference to the orignal
 *  request in their subject line </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 * 
 * @version 1.00		2026-07-17 Initial version
 *
 */

public class RequestComment {
	/*
	 * These are the private attributes for this entity object
	 */
	private int           requestID;
	private String        commenterUsername;
	private String        description;
	private LocalDateTime createdAt;

	/*****
	 * <p> Method: RequestComment(int requestID, String commenterUsername, String description) </p>
	 *
	 * <p> Description: This constructor is used to establish Request entity objects.
	 *  Throws IllegalArgumentException if description is empty. </p>
	 *
	 * @param requestID specifies the request that the comment belongs to
	 * 
	 * @param requestorUsername specifies the username of the staff member submitting
	 * the request
	 *
	 * @param description specifies the description of the request; must be non-empty
	 *
	 * @throws IllegalArgumentException if description is null or blank
	 *
	 */
	public RequestComment(int requestID, String commenterUsername, String description) {
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException("Comment description must not be empty.");
		}
		this.requestID = requestID;
		this.commenterUsername = commenterUsername;
		this.description = description;
		this.createdAt = LocalDateTime.now();
	}


	/*****
	 * <p> Method: int getRequestID() </p>
	 *
	 * <p> Description: This getter returns the requestID. </p>
	 *
	 * @return an int of the requestID
	 *
	 */
	public int getRequestID() { return requestID; }


	/*****
	 * <p> Method: void setRequestID(int id) </p>
	 *
	 * <p> Description: This setter defines the RequestID attribute. </p>
	 *
	 * @param id specifies the requestID assigned to this request by the database
	 *
	 */
	public void setRequestID(int id) { requestID = id; }


	/*****
	 * <p> Method: String getCommenterUsername() </p>
	 *
	 * <p> Description: This getter returns the commenterUsername. </p>
	 *
	 * @return a String of the username who created this comment
	 *
	 */
	public String getCommenterUsername() { return commenterUsername; }
	
	
	/*****
	 * <p> Method: String getDescription() </p>
	 *
	 * <p> Description: This getter returns the description. </p>
	 *
	 * @return a String of the description
	 *
	 */
	public String getDescription() { return description; }


	/*****
	 * <p> Method: void setDescription(String description) </p>
	 *
	 * <p> Description: This setter defines the description attribute. </p>
	 *
	 * @param description specifies the new description for this request
	 *
	 */
	public void setDescription(String description) { this.description = description; }


	/*****
	 * <p> Method: void setCreatedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the createdAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this comment was created
	 *
	 */
	public void setCreatedAt(LocalDateTime time) { this.createdAt = time; }
	
	
	/*****
	 * <p> Method: LocalDateTime getCreatedAt() </p>
	 *
	 * <p> Description: This getter returns the createdAt timestamp. </p>
	 *
	 * @return the timestamp createAt
	 *
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }

}