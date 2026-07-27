package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import entityClasses.Post;
import entityClasses.Reply;
import guiStaffCoverage.ControllerStaffCoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*******
 * <p> Title: CoverageReportTests </p>
 *
 * <p> Description: JUnit 5 test class for the Students Answer Coverage Computation
 * aspect. Tests ControllerStaffCoverage.computeCoverage() and meetsThreshold()
 * directly without launching JavaFX or connecting to a live H2 database.
 * Covers all requirements from TP3 Testing Requirements.pdf. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 2.00  2026-07-26  Converted to JUnit 5
 */
public class CoverageReportTests {

    // ── Helper methods ────────────────────────────────────────────────────────

    /**
     * Creates a Post with a specific postID and author for use in tests.
     * Uses setter injection to set postID directly without a database round-trip.
     */
    private Post makePost(int postID, String author) {
        Post p = new Post("Test Title", "Test Body", author, "General");
        p.setPostID(postID);
        return p;
    }

    /**
     * Creates a Reply targeting a specific postID authored by the given username.
     */
    private Reply makeReply(int postID, String author) {
        return new Reply(postID, "Test reply body", author);
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    /**********
     * <p> Method: testStudentMeetsThresholdExactly() </p>
     *
     * <p> Description: Tests R1 and R3. A student who replied to exactly 3 distinct
     * peers must receive count=3 and must not be flagged. Boundary value at threshold.
     * The threshold must be inclusive — exactly 3 passes. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: count equals 3 and meetsThreshold returns true. </p>
     */
    @Test
    public void testStudentMeetsThresholdExactly() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userC"));
        posts.add(makePost(3, "userD"));

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        replies.add(makeReply(2, "userA"));
        replies.add(makeReply(3, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(3, count, "Expected 3 distinct peers");
        assertTrue(ControllerStaffCoverage.meetsThreshold(count), "Count=3 should meet threshold");
    }

    /**********
     * <p> Method: testStudentExceedsThreshold() </p>
     *
     * <p> Description: Tests R1 and R3. A student with 5 distinct peers must get
     * count=5 and must not be flagged. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: count equals 5 and meetsThreshold returns true. </p>
     */
    @Test
    public void testStudentExceedsThreshold() {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) posts.add(makePost(i, "peer" + i));

        List<Reply> replies = new ArrayList<>();
        for (int i = 1; i <= 5; i++) replies.add(makeReply(i, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(5, count, "Expected 5 distinct peers");
        assertTrue(ControllerStaffCoverage.meetsThreshold(count), "Count=5 should meet threshold");
    }

    /**********
     * <p> Method: testStudentBelowThreshold() </p>
     *
     * <p> Description: Tests R1 and R3. A student with 2 distinct peers must get
     * count=2 and must be flagged. Boundary value just below threshold. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: count equals 2 and meetsThreshold returns false. </p>
     */
    @Test
    public void testStudentBelowThreshold() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userC"));

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        replies.add(makeReply(2, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(2, count, "Expected 2 distinct peers");
        assertFalse(ControllerStaffCoverage.meetsThreshold(count), "Count=2 should be flagged");
    }

    /**********
     * <p> Method: testStudentHasZeroReplies() </p>
     *
     * <p> Description: Tests R1, R3, R5. A student with no replies must return
     * count=0 not null. No NullPointerException must be thrown. </p>
     *
     * <p> Requirement: R1, R3, R5 </p>
     *
     * <p> Pass Condition: result not null, count=0, meetsThreshold false. </p>
     */
    @Test
    public void testStudentHasZeroReplies() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));

        List<Reply> replies = new ArrayList<>();

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);

        assertNotNull(result, "Result map must not be null");
        int count = result.getOrDefault("userA", 0);
        assertEquals(0, count, "Expected count=0 for student with no replies");
        assertFalse(ControllerStaffCoverage.meetsThreshold(count), "Count=0 should be flagged");
    }

    /**********
     * <p> Method: testDeduplicationMultipleRepliesToSamePeer() </p>
     *
     * <p> Description: Tests R2. Four replies to posts by the same peer must count
     * as 1 not 4. Without deduplication students could game the requirement. </p>
     *
     * <p> Requirement: R2 </p>
     *
     * <p> Pass Condition: count equals 1. </p>
     */
    @Test
    public void testDeduplicationMultipleRepliesToSamePeer() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userB"));
        posts.add(makePost(3, "userB"));
        posts.add(makePost(4, "userB"));

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        replies.add(makeReply(2, "userA"));
        replies.add(makeReply(3, "userA"));
        replies.add(makeReply(4, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(1, count, "4 replies to same peer must count as 1");
        assertFalse(ControllerStaffCoverage.meetsThreshold(count), "Count=1 should be flagged");
    }

    /**********
     * <p> Method: testSelfReplyExclusion() </p>
     *
     * <p> Description: Tests R4. A reply to the student's own post must not count
     * toward their peer total. Count must be 2 not 3. </p>
     *
     * <p> Requirement: R4 </p>
     *
     * <p> Pass Condition: count equals 2, confirming self-reply excluded. </p>
     */
    @Test
    public void testSelfReplyExclusion() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userC"));
        posts.add(makePost(3, "userA")); // own post

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA")); // counts
        replies.add(makeReply(2, "userA")); // counts
        replies.add(makeReply(3, "userA")); // must NOT count

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(2, count, "Self-reply must not count, expected 2");
        assertFalse(ControllerStaffCoverage.meetsThreshold(count), "Count=2 should be flagged");
    }

    /**********
     * <p> Method: testReportCompleteness() </p>
     *
     * <p> Description: Tests R7. A zero-reply student must return 0 via getOrDefault
     * without throwing an exception. No student may be silently omitted. </p>
     *
     * <p> Requirement: R7 </p>
     *
     * <p> Pass Condition: active student has count=1, zero-reply student returns 0. </p>
     */
    @Test
    public void testReportCompleteness() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);

        assertEquals(1, result.getOrDefault("userA", 0), "Active student should have count=1");
        assertEquals(0, result.getOrDefault("userC", 0), "Zero-reply student should return 0");
        assertFalse(ControllerStaffCoverage.meetsThreshold(0), "Zero-reply student should be flagged");
    }

    /**********
     * <p> Method: testCountOfOne() </p>
     *
     * <p> Description: Tests R1 and R3 at boundary count=1. A student who replied
     * to only one peer must be flagged. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: count equals 1 and meetsThreshold returns false. </p>
     */
    @Test
    public void testCountOfOne() {
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));

        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);

        assertEquals(1, count, "Expected count=1");
        assertFalse(ControllerStaffCoverage.meetsThreshold(count), "Count=1 should be flagged");
    }

    /**********
     * <p> Method: testLargeInput() </p>
     *
     * <p> Description: Tests NF1 (performance). 30 students each replying to 10
     * different peers (~300 replies total). All must complete correctly. </p>
     *
     * <p> Requirement: NF1 </p>
     *
     * <p> Pass Condition: all 30 students have count=10. </p>
     */
    @Test
    public void testLargeInput() {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 30; i++) posts.add(makePost(i, "peer" + i));

        List<Reply> replies = new ArrayList<>();
        for (int student = 1; student <= 30; student++) {
            for (int postNum = 1; postNum <= 10; postNum++) {
                replies.add(makeReply(postNum, "student" + student));
            }
        }

        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);

        assertEquals(30, result.size(), "All 30 students should appear in result");
        for (int student = 1; student <= 30; student++) {
            assertEquals(10, result.getOrDefault("student" + student, 0),
                "student" + student + " should have count=10");
        }
    }
}