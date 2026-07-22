package tests;

import org.junit.jupiter.api.Test;
import recognizers.PostReplyValidator;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: PostReplyValidatorTest </p>
 *
 * <p> Description: Tests for {@code PostReplyValidator} covering:
 * <ul>
 *   <li>TD-01 group: {@code checkThreadPermission} — CWE 4 (Missing Authorization):
 *       non-staff callers must be blocked; staff callers must be allowed.</li>
 *   <li>TD-02 group: {@code checkForValidThreadName} — thread name must be non-null,
 *       non-blank, and at most MAX_THREAD_NAME_LENGTH characters.</li>
 *   <li>TD-03 group: {@code checkForValidThreadDescription} — description is optional
 *       (null/blank allowed) but must not exceed MAX_THREAD_DESC_LENGTH characters.</li>
 * </ul>
 * <p> All methods return {@code ""} on success and a non-empty error string on failure. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.01    2026-07-12 Rewritten to JUnit 5
 * @version 1.02    2026-07-18 Added TD-02 and TD-03 groups for thread name/description
 *                             validation (TP3 thread CRUD story)
 *
 */
class PostReplyValidatorTest {

    // -------------------------------------------------------------------------
    // checkThreadPermission
    // -------------------------------------------------------------------------

    @Test
    void checkThreadPermissionReturnsErrorForNonStaff() {
        String result = PostReplyValidator.checkThreadPermission(false);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkThreadPermissionReturnsEmptyStringForStaff() {
        assertEquals("", PostReplyValidator.checkThreadPermission(true));
    }

    @Test
    void checkThreadPermissionIsDeterministicForNonStaff() {
        String firstResult  = PostReplyValidator.checkThreadPermission(false);
        String secondResult = PostReplyValidator.checkThreadPermission(false);
        assertEquals(firstResult, secondResult);
    }

    @Test
    void checkThreadPermissionIsDeterministicForStaff() {
        assertEquals("", PostReplyValidator.checkThreadPermission(true));
        assertEquals("", PostReplyValidator.checkThreadPermission(true));
    }

    // -------------------------------------------------------------------------
    // checkForValidThreadName
    // -------------------------------------------------------------------------

    @Test
    void checkForValidThreadNameReturnsEmptyForValidName() {
        assertEquals("", PostReplyValidator.checkForValidThreadName("Homework 3"));
    }

    @Test
    void checkForValidThreadNameReturnsErrorForNullName() {
        String result = PostReplyValidator.checkForValidThreadName(null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadNameReturnsErrorForEmptyName() {
        String result = PostReplyValidator.checkForValidThreadName("");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadNameReturnsErrorForWhitespaceName() {
        String result = PostReplyValidator.checkForValidThreadName("   ");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadNameReturnsErrorWhenTooLong() {
        String tooLong = "A".repeat(PostReplyValidator.MAX_THREAD_NAME_LENGTH + 1);
        String result = PostReplyValidator.checkForValidThreadName(tooLong);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadNameReturnsEmptyAtMaxLength() {
        String atLimit = "A".repeat(PostReplyValidator.MAX_THREAD_NAME_LENGTH);
        assertEquals("", PostReplyValidator.checkForValidThreadName(atLimit));
    }

    // -------------------------------------------------------------------------
    // checkForValidThreadDescription
    // -------------------------------------------------------------------------

    @Test
    void checkForValidThreadDescriptionReturnsEmptyForValidDescription() {
        assertEquals("", PostReplyValidator.checkForValidThreadDescription("Questions about HW3"));
    }

    @Test
    void checkForValidThreadDescriptionReturnsEmptyForNull() {
        // Description is optional — null is acceptable
        assertEquals("", PostReplyValidator.checkForValidThreadDescription(null));
    }

    @Test
    void checkForValidThreadDescriptionReturnsEmptyForBlank() {
        // Description is optional — blank is acceptable
        assertEquals("", PostReplyValidator.checkForValidThreadDescription(""));
    }

    @Test
    void checkForValidThreadDescriptionReturnsErrorWhenTooLong() {
        String tooLong = "A".repeat(PostReplyValidator.MAX_THREAD_DESC_LENGTH + 1);
        String result = PostReplyValidator.checkForValidThreadDescription(tooLong);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void checkForValidThreadDescriptionReturnsEmptyAtMaxLength() {
        String atLimit = "A".repeat(PostReplyValidator.MAX_THREAD_DESC_LENGTH);
        assertEquals("", PostReplyValidator.checkForValidThreadDescription(atLimit));
    }
}
