package tests;

import entityClasses.ThreadList;
import recognizers.PostReplyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: ThreadListTest </p>
 *
 * <p> Description: Tests for in-memory {@code ThreadList} read-only lookups and for
 * {@code PostReplyValidator.checkForValidThread(String, ThreadList)}.
 *
 * <p> Thread CRUD business logic (create, update, delete with guards) is tested at the
 * Database layer in {@code DatabaseTest}, following this project's convention that all
 * business logic lives in {@code Database}. This class covers only the container
 * operations ({@code addThread}, {@code getThreadByName}, {@code getThreadByID},
 * {@code threadExists}) and the validator that takes a live {@code ThreadList}. </p>
 *
 * <p> No database required — all tests use in-memory objects. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.01    2026-07-12 Rewritten to JUnit 5
 * @version 1.02    2026-07-18 Removed CRUD entity tests (moved to DatabaseTest per
 *                             project convention); kept lookup and validator tests only
 *
 */
class ThreadListTest {

    private ThreadList threadList;

    @BeforeEach
    void setUp() {
        threadList = new ThreadList();
        entityClasses.Thread general = new entityClasses.Thread("General", "Default fallback thread", "system");
        general.setIsDefault(true);
        threadList.addThread(general);
    }

    // -------------------------------------------------------------------------
    // getThreadByName
    // -------------------------------------------------------------------------

    @Test
    void getThreadByNameReturnsCorrectThread() {
        threadList.addThread(new entityClasses.Thread("Homework", "HW questions", "staffA"));
        entityClasses.Thread result = threadList.getThreadByName("Homework");
        assertNotNull(result);
        assertEquals("Homework", result.getName());
    }

    @Test
    void getThreadByNameReturnsNullWhenAbsent() {
        assertNull(threadList.getThreadByName("Quizzes"));
    }

    @Test
    void getThreadByNameReturnsNullForEmptyString() {
        assertNull(threadList.getThreadByName(""));
    }

    // -------------------------------------------------------------------------
    // threadExists
    // -------------------------------------------------------------------------

    @Test
    void threadExistsReturnsTrueForPresentThread() {
        assertTrue(threadList.threadExists("General"));
    }

    @Test
    void threadExistsReturnsFalseForAbsentThread() {
        assertFalse(threadList.threadExists("Homework"));
    }

    @Test
    void lookupsOnEmptyListReturnNullOrFalseWithoutException() {
        ThreadList emptyList = new ThreadList();
        assertDoesNotThrow(() -> {
            assertNull(emptyList.getThreadByName("General"));
            assertNull(emptyList.getThreadByID(1));
            assertFalse(emptyList.threadExists("General"));
        });
    }

    // -------------------------------------------------------------------------
    // PostReplyValidator.checkForValidThread(String, ThreadList)
    // -------------------------------------------------------------------------

    @Test
    void checkForValidThreadReturnsEmptyForExistingThread() {
        assertEquals("", PostReplyValidator.checkForValidThread("General", threadList));
    }

    @Test
    void checkForValidThreadReturnsErrorForAbsentThread() {
        String result = PostReplyValidator.checkForValidThread("Quizzes", threadList);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadReturnsErrorForEmptyName() {
        String result = PostReplyValidator.checkForValidThread("", threadList);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadReturnsErrorForNullNameWithoutNpe() {
        assertDoesNotThrow(() -> {
            String result = PostReplyValidator.checkForValidThread(null, threadList);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        });
    }

    @Test
    void checkForValidThreadReturnsErrorForNullListWithoutNpe() {
        assertDoesNotThrow(() -> {
            String result = PostReplyValidator.checkForValidThread("General", null);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        });
    }
}
