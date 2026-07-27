package tests;

import entityClasses.Request;
import entityClasses.RequestList;
import guiOpenRequests.ControllerOpenRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: ControllerOpenRequestsTest </p>
 *
 * <p> Description: Tests for {@code ControllerOpenRequests} covering two defect families
 * from TP2 Test Designs:
 * <ul>
 *   <li><b>CWE 4 – Missing Authorization</b>: non-admin users must be blocked before
 *       {@code RequestList.closeRequest} is called; admins must succeed.</li>
 *   <li><b>CWE 13 – Null Pointer Dereference</b>: unknown IDs passed to
 *       {@code performDisplayRequest} or {@code performReopenRequest} must return null,
 *       not throw NPE.</li>
 * </ul>
 * No database required — all tests use in-memory {@code RequestList} objects.
 *
 * <p> NOTE: Tests are commented out pending implementation of performCloseRequest,
 * performDisplayRequest, and performReopenRequest in ControllerOpenRequests by the
 * assigned team member. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.01    2026-07-12 Rewritten to JUnit 5
 *
 */
class ControllerOpenRequestsTest {

    private RequestList requestList;
    private Request openedRequest;

    @BeforeEach
    void setUp() {
        requestList = new RequestList();

        // ID 1 — open
        openedRequest = new Request("staffUser", "Please add a new thread for Lab questions.");
        openedRequest.setRequestID(1);
        requestList.addRequest(openedRequest);

        // ID 2 — closed
        Request closedRequest = new Request("staffUser", "Please reset the grade for Quiz 2.");
        closedRequest.setRequestID(2);
        requestList.addRequest(closedRequest);
        requestList.closeRequest(2, "Grade reset completed by admin on 2026-07-12.");
    }


    // CWE 4 Missing Authorization
    // TODO: uncomment when performCloseRequest is implemented
//    @Test
//    void nonAdminCloseIsBlockedAndRequestRemainsOpen() {
//        RequestList requestList = new RequestList();
//        Request archiveRequest = new Request("staffUser", "Please archive old posts.");
//        archiveRequest.setRequestID(10);
//        requestList.addRequest(archiveRequest);
//
//        String error = ControllerOpenRequests.performCloseRequest(10, "Admin notes", false, requestList);
//
//        assertFalse(error.isEmpty());
//        assertFalse(archiveRequest.getIsClosed());
//        assertFalse(requestList.getAllOpenRequests().isEmpty());
//    }

//    @Test
//    void adminCloseSucceedsAndRequestIsClosed() {
//        RequestList requestList = new RequestList();
//        Request request = new Request("staffUser", "Please add a Zoom link.");
//        request.setRequestID(20);
//        requestList.addRequest(request);
//
//        String result = ControllerOpenRequests.performCloseRequest(20, "Zoom link added.", true, requestList);
//
//        assertEquals("", result);
//        assertTrue(request.getIsClosed());
//    }


    // CWE 13 Null Pointer Dereference
    // TODO: uncomment when performDisplayRequest and performReopenRequest are implemented
//    @Test
//    void displayUnknownIdReturnsNull() {
//        assertNull(ControllerOpenRequests.performDisplayRequest(9999, requestList));
//    }

//    @Test
//    void displayKnownIdReturnsCorrectRequest() {
//        Request request = ControllerOpenRequests.performDisplayRequest(1, requestList);
//        assertNotNull(request);
//        assertEquals(1, request.getRequestID());
//    }

//    @Test
//    void reopenUnknownIdReturnsNull() {
//        assertNull(ControllerOpenRequests.performReopenRequest(8888, requestList));
//    }

//    @Test
//    void reopenKnownClosedIdReturnsOpenRequest() {
//        Request request = ControllerOpenRequests.performReopenRequest(2, requestList);
//        assertNotNull(request);
//        assertFalse(request.getIsClosed());
//        assertEquals(2, request.getClosedRequestId());
//    }

    // Boundary values
    // TODO: uncomment when performDisplayRequest and performReopenRequest are implemented
//    @Test
//    void displayIdZeroReturnsNull() {
//        assertNull(ControllerOpenRequests.performDisplayRequest(0, requestList));
//    }

//    @Test
//    void reopenIdZeroReturnsNull() {
//        assertNull(ControllerOpenRequests.performReopenRequest(0, requestList));
//    }

//    @Test
//    void displayIdMaxValueReturnsNull() {
//        assertNull(ControllerOpenRequests.performDisplayRequest(Integer.MAX_VALUE, requestList));
//    }
}
