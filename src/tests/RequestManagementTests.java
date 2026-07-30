package tests;

import database.Database;
import entityClasses.Request;
import entityClasses.RequestList;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*******
 * <p> Title: RequestManagementTests class </p>
 *
 * <p> Description: This class is a console-based JUnit testbed that tests the
 * program's create, close, and re-open methods for Request objects. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 */

class RequestManagementTests {
	private static Database db;
	
	/*****
	 * <p> Method: setUp() </p>
	 *
	 * <p> Description: Opens a fresh private in-memory H2 database before each test
	 *  that touches the Database class, so tests never interfere with each other or
	 *  with the production database. </p>
	 *
	 */
	@BeforeEach
	public void setUp() throws SQLException {
		db = new Database();
		db.connectToTestDatabase();
	}
	
	/*****
	 * <p> Method: testCreateRequest() </p>
	 *
	 * <p> Description: Verifies that a request object can be created in program memory
	 * and is saved in the database. </p>
	 *
	 */
	@Test
	void testCreateRequest() {
		String requestorUsername = "Jill.Valentine";
		String subject = "Password reset";
		String description = "I am locked out of my account. Please send me a one-time password "
				+ "so I can reset my password.";
		
		Request request = new Request(requestorUsername, subject, description);
		
		assertEquals("Jill.Valentine", request.getRequestorUsername());
		assertEquals("Password reset", request.getSubject());
		assertEquals("I am locked out of my account. Please send me a one-time password "
				+ "so I can reset my password.", request.getDescription());
		
		try {
			db.createRequest(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			Request fromDB = db.readRequest(request.getRequestID());
			assertEquals("Jill.Valentine", fromDB.getRequestorUsername());
			assertEquals("Password reset", fromDB.getSubject());
			assertEquals("I am locked out of my account. Please send me a one-time password "
					+ "so I can reset my password.", fromDB.getDescription());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*****
	 * <p> Method: testCloseRequest() </p>
	 *
	 * <p> Description: Verifies that a request can be closed and will have its status updated
	 * in program memory and in the database. </p>
	 *
	 */
	@Test
	void testCloseRequest() {
		RequestList allRequests = new RequestList();
		
		String requestorUsername = "Chris.Redfield";
		String subject = "Admin access";
		String description = "I am requesting to have admin priveleges added to my account.";
		String assignedTo = "Barry.Burton";
		String adminNotes = "Request completed.";
		
		Request request = new Request(requestorUsername, subject, description);
		
		try {
			db.createRequest(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		allRequests.addRequest(request);
		allRequests.closeRequest(request.getRequestID(), assignedTo, adminNotes);
		
		assertEquals("Closed", request.getStatus());
		assertEquals("Barry.Burton", request.getAssignedTo());
		assertEquals("Request completed.", request.getAdminNotes());
		
		db.closeRequest(request.getRequestID(), assignedTo, adminNotes);
		
		try {
			Request fromDB = db.readRequest(request.getRequestID());
			assertEquals("Closed", fromDB.getStatus());
			assertEquals("Barry.Burton", fromDB.getAssignedTo());
			assertEquals("Request completed.", fromDB.getAdminNotes());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*****
	 * <p> Method: testReopenRequest() </p>
	 *
	 * <p> Description: Verifies that a request can be re-opened and will have its status updated
	 * in program memory and in the database. </p>
	 *
	 */
	@Test
	void testReopenRequest() {
		RequestList allRequests = new RequestList();
		
		String requestorUsername = "Chris.Redfield";
		String subject = "Admin access";
		String description = "I am requesting to have admin priveleges added to my account.";
		String assignedTo = "Barry.Burton";
		String adminNotes = "Request completed.";
		String newDescription = "Admin priveleges not working.";
		
		Request request = new Request(requestorUsername, subject, description);
		
		try {
			db.createRequest(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		allRequests.addRequest(request);
		allRequests.closeRequest(request.getRequestID(), assignedTo, adminNotes);
		
		assertEquals("Closed", request.getStatus());
		assertEquals("Barry.Burton", request.getAssignedTo());
		assertEquals("Request completed.", request.getAdminNotes());
		
		db.closeRequest(request.getRequestID(), assignedTo, adminNotes);
		
		Request newRequest = allRequests.reopenRequest(request.getRequestID(), newDescription);
		try {
			db.createRequest(newRequest);
			db.updateClosedRequestID(newRequest.getRequestID(), request.getRequestID());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(request.getRequestID(), newRequest.getClosedRequestId());
		assertEquals("Assigned", newRequest.getStatus());
		assertEquals("Barry.Burton", newRequest.getAssignedTo());
		assertEquals("Admin priveleges not working.", newRequest.getDescription());
		
		try {
			Request fromDB = db.readRequest(newRequest.getRequestID());
			assertEquals(request.getRequestID(), fromDB.getClosedRequestId());
			assertEquals("Assigned", fromDB.getStatus());
			assertEquals("Barry.Burton", fromDB.getAssignedTo());
			assertEquals("Admin priveleges not working.", fromDB.getDescription());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}