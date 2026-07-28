package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: RequestComment Class </p>
 *
 * <p> Description: This Request class represents an admin request entity in the system.
 *  Staff can request admins to perform admin-specific actions. Requests appear in an open
 *  list visible to staff and admins. Admins can document actions taken and close requests.
 *  Staff can reopen a closed request; reopened requests link back to the original closed
 *  request via closedRequestId. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
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
	 * <p> Method: RequestComment(String commenterUsername, String description) </p>
	 *
	 * <p> Description: This constructor is used to establish Request entity objects.
	 *  Throws IllegalArgumentException if description is empty. </p>
	 *
	 * @param requestorUsername specifies the username of the staff member submitting
	 *  the request
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
	 * <p> Description: This getter returns the RequestID. </p>
	 *
	 * @return an int of the RequestID
	 *
	 */
	public int getRequestID() { return requestID; }


	/*****
	 * <p> Method: void setRequestID(int id) </p>
	 *
	 * <p> Description: This setter defines the RequestID attribute. </p>
	 *
	 * @param id specifies the RequestID assigned to this request by the database
	 *
	 */
	public void setRequestID(int id) { requestID = id; }


	/*****
	 * <p> Method: String getRequestorUsername() </p>
	 *
	 * <p> Description: This getter returns the RequestorUsername. </p>
	 *
	 * @return a String of the username who submitted this request
	 *
	 */
	public String getCommenterUsername() { return commenterUsername; }
	
	
	/*****
	 * <p> Method: String getDescription() </p>
	 *
	 * <p> Description: This getter returns the Description. </p>
	 *
	 * @return a String of the Description
	 *
	 */
	public String getDescription() { return description; }


	/*****
	 * <p> Method: void setDescription(String description) </p>
	 *
	 * <p> Description: This setter defines the Description attribute. </p>
	 *
	 * @param description specifies the new description for this request
	 *
	 */
	public void setDescription(String description) { this.description = description; }



	/*****
	 * <p> Method: void setCreatedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the CreatedAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this request was created
	 *
	 */
	public void setCreatedAt(LocalDateTime time) { createdAt = time; }

}