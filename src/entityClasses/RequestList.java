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
	 * <p> Method: void closeRequest(int requestID, String adminNotes) </p>
	 *
	 * <p> Description: This method closes an open request with admin notes documenting
	 *  the action taken. Validates that adminNotes is non-empty. </p>
	 *
	 * @param requestID specifies the ID of the request to close
	 *
	 * @param adminNotes specifies the admin's notes; must be non-empty
	 *
	 * @throws IllegalArgumentException if adminNotes is empty or request is not found
	 *
	 */
	public void closeRequest(int requestID, String adminNotes) {
		if (adminNotes == null || adminNotes.trim().isEmpty()) {
			throw new IllegalArgumentException("Admin notes must not be empty when closing a request.");
		}
		Request r = getRequestByID(requestID);
		if (r == null) {
			throw new IllegalArgumentException("Request not found.");
		}
		r.setAdminNotes(adminNotes);
		r.setIsClosed(true);
		r.setClosedAt(LocalDateTime.now());
	}


	/*****
	 * <p> Method: Request reopenRequest(int closedRequestId) </p>
	 *
	 * <p> Description: This method creates a new open Request linked to the original closed
	 *  request via closedRequestId. The original request remains unchanged. </p>
	 *
	 * @param closedRequestId specifies the ID of the original closed request to reopen
	 *
	 * @return a new open Request with closedRequestId set to the original request's ID,
	 *  or null if the original request is not found
	 *
	 */
	public Request reopenRequest(int closedRequestId) {
		Request original = getRequestByID(closedRequestId);
		if (original == null) {
			return null;
		}
		Request reopened = new Request(original.getRequestorUsername(), original.getDescription());
		reopened.setClosedRequestId(closedRequestId);
		requests.add(reopened);
		return reopened;
	}
}
