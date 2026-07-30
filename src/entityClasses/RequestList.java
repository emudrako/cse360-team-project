package entityClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/*******
 * <p> Title: RequestList Class </p>
 *
 * <p> Description: This RequestList class supports storing all current Request objects in
 *  the system and provides business logic for managing those requests. Validation
 *  (non-empty description, non-empty admin notes) lives here and throws
 *  IllegalArgumentException -- same pattern PostReplyValidator uses for Post and Reply. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 */

public class RequestList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<Request> requests;

	/*****
	 * <p> Method: RequestList() </p>
	 *
	 * <p> Description: This constructor establishes an empty RequestList, ready to have
	 *  Request objects added to it. </p>
	 *
	 */
	public RequestList() {
		this.requests = new ArrayList<Request>();
	}


	/*****
	 * <p> Method: void addRequest(Request r) </p>
	 *
	 * <p> Description: This method adds a Request object to the RequestList. </p>
	 *
	 * @param request specifies the Request object to be added to the list
	 *
	 */
	public void addRequest(Request request) {
		requests.add(request);
	}


	/*****
	 * <p> Method: List<Request> getAllRequests() </p>
	 *
	 * <p> Description: This method returns all Request objects. </p>
	 *
	 * @return a List of all Request objects
	 *
	 */
	public List<Request> getAllRequests() {
		List<Request> result = this.requests;
		return result;
	}
	
	
	/*****
	 * <p> Method: List<Request> getAllOpenRequests() </p>
	 *
	 * <p> Description: This method returns all open (not closed) Request objects. </p>
	 *
	 * @return a List of all Request objects where closed is FALSE
	 *
	 */
	public List<Request> getAllOpenRequests() {
		List<Request> result = new ArrayList<Request>();
		for (Request r : requests) {
			if (!r.getIsClosed()) {
				result.add(r);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: List<Request> getAllClosedRequests() </p>
	 *
	 * <p> Description: This method returns all closed Request objects. </p>
	 *
	 * @return a List of all Request objects where closed is TRUE
	 *
	 */
	public List<Request> getAllClosedRequests() {
		List<Request> result = new ArrayList<Request>();
		for (Request r : requests) {
			if (r.getIsClosed()) {
				result.add(r);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: Request getRequestByID(int requestID) </p>
	 *
	 * <p> Description: This method returns the Request object matching the specified
	 *  requestID, or null if no such request exists. </p>
	 *
	 * @param requestID specifies the ID of the request to find
	 *
	 * @return the Request object matching the specified requestID, or null if not found
	 *
	 */
	public Request getRequestByID(int requestID) {
		for (Request r : requests) {
			if (r.getRequestID() == requestID) {
				return r;
			}
		}
		return null;
	}


	/*****
	 * <p> Method: void updateDescription(int requestID, String newDescription) </p>
	 *
	 * <p> Description: This method updates the description of an open request. Validates
	 *  that newDescription is non-empty and that the request is open. </p>
	 *
	 * @param requestID specifies the ID of the request to update
	 *
	 * @param newDescription specifies the new description; must be non-empty
	 *
	 * @throws IllegalArgumentException if newDescription is empty or the request is closed
	 *
	 */
	public void updateDescription(int requestID, String newDescription) {
		if (newDescription == null || newDescription.trim().isEmpty()) {
			throw new IllegalArgumentException("Request description must not be empty.");
		}
		Request r = getRequestByID(requestID);
		if (r == null || r.getIsClosed()) {
			throw new IllegalArgumentException("Cannot update description: request is closed or not found.");
		}
		r.setDescription(newDescription);
	}


	/*****
	 * <p> Method: void closeRequest(int requestID, String assignedTo, String adminNotes) </p>
	 *
	 * <p> Description: This method closes an open request with admin notes documenting
	 *  the action taken. Validates that adminNotes is non-empty. </p>
	 *
	 * @param requestID specifies the ID of the request to close
	 * 
	 * @param assignedTo specifies who closed the request
	 *
	 * @param adminNotes specifies the admin's notes; must be non-empty
	 * 
	 * @see RequestManagementTests.java for JUnit tests
	 *
	 * @throws IllegalArgumentException if adminNotes is empty or request is not found
	 *
	 */
	public void closeRequest(int requestID, String assignedTo, String adminNotes) {
		if (adminNotes == null || adminNotes.trim().isEmpty()) {
			throw new IllegalArgumentException("Admin notes must not be empty when closing a request.");
		}
		Request request = getRequestByID(requestID);
		if (request == null) {
			throw new IllegalArgumentException("Request not found.");
		}
		LocalDateTime closedAt = LocalDateTime.now();
		request.setAssignedTo(assignedTo);
		request.setAdminNotes(adminNotes);
		request.setStatus("Closed");
		request.setClosedAt(closedAt);
		request.setIsClosed(true);
	}


	/*****
	 * <p> Method: Request reopenRequest(int closedRequestId, String newDescription) </p>
	 *
	 * <p> Description: This method creates a new open Request linked to the original closed
	 *  request via closedRequestId. The original request remains unchanged. </p>
	 *
	 * @param closedRequestId specifies the ID of the original closed request to reopen
	 * 
	 * @param newDescription specifies the description for the re-opened request
	 * 
	 * @see RequestManagementTests.java for JUnit tests
	 *
	 * @return a new open Request with closedRequestId set to the original request's ID,
	 *  or null if the original request is not found
	 *
	 */
	public Request reopenRequest(int closedRequestId, String newDescription) {
		Request original = getRequestByID(closedRequestId);
		if (original == null) {
			return null;
		}
		// If the request to be re-opened was itself a re-opened request, this
		// removes the request reference in the subject line
		String requestSubject = original.getSubject();
		int index = requestSubject.indexOf('[');
		if (index != -1) {
			requestSubject = requestSubject.substring(0, index-1);
		}
		// Adds a reference to the closed parent request in the subject line
		requestSubject += " [Re-Opened from "
				+ "ID: " + original.getRequestID() + "]";
		Request newRequest = new Request(original.getRequestorUsername(), requestSubject, newDescription);
		// Opens the new request with an Assigned status
		newRequest.setStatus("Assigned");
		// The new request will be automatically assigned to the Admin that closed it
		newRequest.setAssignedTo(original.getAssignedTo());
		newRequest.setClosedRequestId(original.getRequestID());
		
		return newRequest;
	}
}
