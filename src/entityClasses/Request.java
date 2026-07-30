package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Request Class </p>
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

public class Request {
	/*
	 * These are the private attributes for this entity object
	 */
	private int           requestID;
	private String        requestorUsername;
	private String		  subject;
	private String        description;
	private String		  status;
	private String		  assignedTo;
	private boolean       isClosed;
	private String        adminNotes;         // null until admin closes the request
	private int           closedRequestId;    // -1 if original; original's ID if this is a reopened request
	private LocalDateTime createdAt;
	private LocalDateTime closedAt;           // null until closeRequest() is called

	/*****
	 * <p> Method: Request(String requestorUsername, String description) </p>
	 *
	 * <p> Description: This constructor is used to establish Request entity objects.
	 *  Throws IllegalArgumentException if description is empty. </p>
	 *
	 * @param requestorUsername specifies the username of the staff member submitting
	 *  the request
	 *
	 * @param description specifies the description of the request; must be non-empty
	 * 
	 * @see RequestManagementTests#testCreateRequest()
	 *
	 * @throws IllegalArgumentException if description is null or blank
	 *
	 */
	public Request(String requestorUsername, String subject, String description) {
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException("Request description must not be empty.");
		}
		this.requestorUsername = requestorUsername;
		this.subject = subject;
		this.description = description;
		this.status = "Open";
		this.assignedTo = "Unassigned";
		this.isClosed = false;
		this.adminNotes = null;
		this.closedRequestId = -1;
		this.createdAt = LocalDateTime.now();
		this.closedAt = null;
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
	public String getRequestorUsername() { return requestorUsername; }


	/*****
	 * <p> Method: String getSubject() </p>
	 *
	 * <p> Description: This getter returns the Subject. </p>
	 *
	 * @return a String of the Subject
	 *
	 */
	public String getSubject() { return subject; }


	/*****
	 * <p> Method: void setSubject(String subject) </p>
	 *
	 * <p> Description: This setter defines the Subject attribute. </p>
	 *
	 * @param subject specifies the new subject for this request
	 *
	 */
	public void setSubject(String subject) { this.subject = subject; }
	
	
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
	 * <p> Method: String getStatus() </p>
	 *
	 * <p> Description: This getter returns the Status. </p>
	 *
	 * @return a String of the Status
	 *
	 */
	public String getStatus() { return status; }


	/*****
	 * <p> Method: void setStatus(String status) </p>
	 *
	 * <p> Description: This setter defines the Status attribute. </p>
	 *
	 * @param status specifies the new status for this request
	 *
	 */
	public void setStatus(String status) { this.status = status; }
	
	
	/*****
	 * <p> Method: String getAssignedTo() </p>
	 *
	 * <p> Description: This getter returns the assignedTo value. </p>
	 *
	 * @return a String of the assignedTo value
	 *
	 */
	public String getAssignedTo() { return assignedTo; }


	/*****
	 * <p> Method: void setAssignedTo(String assignedTo) </p>
	 *
	 * <p> Description: This setter defines the assignedTo attribute. </p>
	 *
	 * @param assignedTo specifies the new assignedTo value for this request
	 *
	 */
	public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
	
	
	/*****
	 * <p> Method: boolean getIsClosed() </p>
	 *
	 * <p> Description: This getter returns the IsClosed attribute. Mirrors isDeleted on Post. </p>
	 *
	 * @return a boolean TRUE if this request has been closed, FALSE otherwise
	 *
	 */
	public boolean getIsClosed() { return isClosed; }


	/*****
	 * <p> Method: void setIsClosed(boolean isClosed) </p>
	 *
	 * <p> Description: This setter defines the IsClosed attribute. </p>
	 *
	 * @param isClosed specifies TRUE if this request should be marked as closed
	 *
	 */
	public void setIsClosed(boolean isClosed) { this.isClosed = isClosed; }


	/*****
	 * <p> Method: String getAdminNotes() </p>
	 *
	 * <p> Description: This getter returns the AdminNotes. Null until admin closes
	 *  the request. </p>
	 *
	 * @return a String of the AdminNotes, or null if not yet closed
	 *
	 */
	public String getAdminNotes() { return adminNotes; }


	/*****
	 * <p> Method: void setAdminNotes(String notes) </p>
	 *
	 * <p> Description: This setter defines the AdminNotes attribute. </p>
	 *
	 * @param notes specifies the admin's notes documenting the action taken
	 *
	 */
	public void setAdminNotes(String notes) { adminNotes = notes; }


	/*****
	 * <p> Method: int getClosedRequestId() </p>
	 *
	 * <p> Description: This getter returns the ClosedRequestId. -1 if this is an
	 *  original request; holds the original request's ID if this is a reopened request. </p>
	 *
	 * @return an int of the ClosedRequestId (-1 for original requests)
	 *
	 */
	public int getClosedRequestId() { return closedRequestId; }


	/*****
	 * <p> Method: void setClosedRequestId(int id) </p>
	 *
	 * <p> Description: This setter defines the ClosedRequestId attribute. </p>
	 *
	 * @param id specifies the original request's ID that this request was reopened from
	 *
	 */
	public void setClosedRequestId(int id) { closedRequestId = id; }


	/*****
	 * <p> Method: LocalDateTime getCreatedAt() </p>
	 *
	 * <p> Description: This getter returns the timestamp when this request was created. </p>
	 *
	 * @return a LocalDateTime of when this request was created
	 *
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }


	/*****
	 * <p> Method: void setCreatedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the CreatedAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this request was created
	 *
	 */
	public void setCreatedAt(LocalDateTime time) { createdAt = time; }


	/*****
	 * <p> Method: LocalDateTime getClosedAt() </p>
	 *
	 * <p> Description: This getter returns the timestamp when this request was closed.
	 *  Null until closeRequest() is called. </p>
	 *
	 * @return a LocalDateTime of when this request was closed, or null if still open
	 *
	 */
	public LocalDateTime getClosedAt() { return closedAt; }


	/*****
	 * <p> Method: void setClosedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the ClosedAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this request was closed
	 *
	 */
	public void setClosedAt(LocalDateTime time) { closedAt = time; }
}
