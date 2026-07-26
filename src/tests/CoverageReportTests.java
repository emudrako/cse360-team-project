package tests;

import entityClasses.Post;
import entityClasses.Reply;
import guiStaffCoverage.ControllerStaffCoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*******
 * <p> Title: CoverageReportTests </p>
 *
 * <p> Description: This class is a console-based testbed that tests the Students
 * Answer Coverage Computation aspect. It exercises the
 * ControllerStaffCoverage.computeCoverage() method directly, without launching
 * JavaFX or connecting to a live H2 database, so tests are fast and repeatable.
 * This follows the same console-test pattern used by Story7Tests and
 * RelatedPostsTests in the team's TP2 codebase. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-10  Initial version — 9 test cases covering all
 *                             requirements from TP3 Testing Requirements.pdf
 */
public class CoverageReportTests {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("===== Students Answer Coverage Computation Test Cases =====\n");
        testStudentMeetsThresholdExactly();
        testStudentExceedsThreshold();
        testStudentBelowThreshold();
        testStudentHasZeroReplies();
        testDeduplicationMultipleRepliesToSamePeer();
        testSelfReplyExclusion();
        testReportCompleteness();
        testCountOfOne();
        testLargeInput();
        System.out.println("\n===== Results: " + passCount + " passed, " + failCount + " failed =====");
    }

    private static void check(boolean condition, String testName, String detail) {
        if (condition) {
            passCount++;
            System.out.println("PASS - " + testName + ": " + detail);
        } else {
            failCount++;
            System.out.println("FAIL - " + testName + ": " + detail);
        }
    }

    /**********
     * <p> Method: testStudentMeetsThresholdExactly() </p>
     *
     * <p> Description: Tests R1 and R3. Verifies a student who replied to exactly
     * 3 distinct peers gets count=3 and is not flagged. Boundary value at threshold. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: PASS if count=3 and meetsThreshold returns true. </p>
     */
    private static void testStudentMeetsThresholdExactly() {
        System.out.println("-- Test 1: Student meets threshold exactly (Boundary count = 3) --");
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
        check(count == 3, "Test1-Count", "Expected 3 distinct peers, got " + count);
        check(ControllerStaffCoverage.meetsThreshold(count), "Test1-NotFlagged", "Student with count=3 should meet threshold");
    }

    /**********
     * <p> Method: testStudentExceedsThreshold() </p>
     *
     * <p> Description: Tests R1 and R3. Verifies a student with 5 distinct peers
     * gets count=5 and is not flagged. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: PASS if count=5 and meetsThreshold returns true. </p>
     */
    private static void testStudentExceedsThreshold() {
        System.out.println("\n-- Test 2: Student exceeds threshold --");
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) posts.add(makePost(i, "peer" + i));
        List<Reply> replies = new ArrayList<>();
        for (int i = 1; i <= 5; i++) replies.add(makeReply(i, "userA"));
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);
        check(count == 5, "Test2-Count", "Expected 5 distinct peers, got " + count);
        check(ControllerStaffCoverage.meetsThreshold(count), "Test2-NotFlagged", "Student with count=5 should meet threshold");
    }

    /**********
     * <p> Method: testStudentBelowThreshold() </p>
     *
     * <p> Description: Tests R1 and R3. Verifies a student with 2 distinct peers
     * gets count=2 and is flagged. Boundary value just below threshold. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: PASS if count=2 and meetsThreshold returns false. </p>
     */
    private static void testStudentBelowThreshold() {
        System.out.println("\n-- Test 3: Student below threshold (Boundary count = 2) --");
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userC"));
        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        replies.add(makeReply(2, "userA"));
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);
        check(count == 2, "Test3-Count", "Expected 2 distinct peers, got " + count);
        check(!ControllerStaffCoverage.meetsThreshold(count), "Test3-Flagged", "Student with count=2 should be flagged");
    }

    /**********
     * <p> Method: testStudentHasZeroReplies() </p>
     *
     * <p> Description: Tests R1, R3, R5. Verifies that a student with no replies
     * gets count=0 not null, and is flagged. No NullPointerException thrown. </p>
     *
     * <p> Requirement: R1, R3, R5 </p>
     *
     * <p> Pass Condition: PASS if result not null, count=0, meetsThreshold false. </p>
     */
    private static void testStudentHasZeroReplies() {
        System.out.println("\n-- Test 4: Student has zero replies --");
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        List<Reply> replies = new ArrayList<>();
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        check(result != null, "Test4-NotNull", "Result map must not be null");
        int count = result.getOrDefault("userA", 0);
        check(count == 0, "Test4-Count", "Expected count=0 for student with no replies, got " + count);
        check(!ControllerStaffCoverage.meetsThreshold(count), "Test4-Flagged", "Student with count=0 should be flagged");
    }

    /**********
     * <p> Method: testDeduplicationMultipleRepliesToSamePeer() </p>
     *
     * <p> Description: Tests R2. Verifies that 4 replies to the same peer count
     * as 1, not 4. Deduplication prevents gaming the requirement. </p>
     *
     * <p> Requirement: R2 </p>
     *
     * <p> Pass Condition: PASS if count=1. </p>
     */
    private static void testDeduplicationMultipleRepliesToSamePeer() {
        System.out.println("\n-- Test 5: Multiple replies to same peer deduplicated --");
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
        check(count == 1, "Test5-Deduplication", "4 replies to same peer must count as 1, got " + count);
        check(!ControllerStaffCoverage.meetsThreshold(count), "Test5-Flagged", "Student with count=1 should be flagged");
    }

    /**********
     * <p> Method: testSelfReplyExclusion() </p>
     *
     * <p> Description: Tests R4. Verifies that a reply to the student's own post
     * does not count toward their peer total. Count must be 2, not 3. </p>
     *
     * <p> Requirement: R4 </p>
     *
     * <p> Pass Condition: PASS if count=2, confirming self-reply excluded. </p>
     */
    private static void testSelfReplyExclusion() {
        System.out.println("\n-- Test 6: Self-reply excluded from count --");
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        posts.add(makePost(2, "userC"));
        posts.add(makePost(3, "userA")); // userA's own post
        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA")); // counts
        replies.add(makeReply(2, "userA")); // counts
        replies.add(makeReply(3, "userA")); // must NOT count — self-reply
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);
        check(count == 2, "Test6-SelfReplyExcluded", "Self-reply must not count, expected 2, got " + count);
        check(!ControllerStaffCoverage.meetsThreshold(count), "Test6-Flagged", "Student with count=2 should be flagged");
    }

    /**********
     * <p> Method: testReportCompleteness() </p>
     *
     * <p> Description: Tests R7. Verifies that a zero-reply student returns 0
     * via getOrDefault without throwing an exception. </p>
     *
     * <p> Requirement: R7 </p>
     *
     * <p> Pass Condition: PASS if active student has count=1 and zero-reply
     * student returns 0 via getOrDefault. </p>
     */
    private static void testReportCompleteness() {
        System.out.println("\n-- Test 7: Report completeness -- zero-reply student handled --");
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int countA = result.getOrDefault("userA", 0);
        check(countA == 1, "Test7-ActiveStudent", "Active student userA should have count=1, got " + countA);
        int countC = result.getOrDefault("userC", 0);
        check(countC == 0, "Test7-ZeroReplyStudent", "Zero-reply student userC should return 0, got " + countC);
        check(!ControllerStaffCoverage.meetsThreshold(countC), "Test7-ZeroReplyFlagged", "Zero-reply student should be flagged");
    }

    /**********
     * <p> Method: testCountOfOne() </p>
     *
     * <p> Description: Tests R1 and R3 at boundary count=1. Verifies single-reply
     * student is correctly flagged. </p>
     *
     * <p> Requirement: R1, R3 </p>
     *
     * <p> Pass Condition: PASS if count=1 and meetsThreshold returns false. </p>
     */
    private static void testCountOfOne() {
        System.out.println("\n-- Test 8: Count = 1 (well below threshold) --");
        List<Post> posts = new ArrayList<>();
        posts.add(makePost(1, "userB"));
        List<Reply> replies = new ArrayList<>();
        replies.add(makeReply(1, "userA"));
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        int count = result.getOrDefault("userA", 0);
        check(count == 1, "Test8-Count", "Expected count=1, got " + count);
        check(!ControllerStaffCoverage.meetsThreshold(count), "Test8-Flagged", "Student with count=1 should be flagged");
    }

    /**********
     * <p> Method: testLargeInput() </p>
     *
     * <p> Description: Tests NF1 (performance). 30 students each replying to
     * 10 different peers (~300 replies). Verifies all complete correctly. </p>
     *
     * <p> Requirement: NF1 </p>
     *
     * <p> Pass Condition: PASS if all 30 students have count=10. </p>
     */
    private static void testLargeInput() {
        System.out.println("\n-- Test 9: Large input (30 students, ~300 replies) --");
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 30; i++) posts.add(makePost(i, "peer" + i));
        List<Reply> replies = new ArrayList<>();
        for (int student = 1; student <= 30; student++) {
            for (int postNum = 1; postNum <= 10; postNum++) {
                replies.add(makeReply(postNum, "student" + student));
            }
        }
        Map<String, Integer> result = ControllerStaffCoverage.computeCoverage(replies, posts);
        boolean allCorrect = true;
        for (int student = 1; student <= 30; student++) {
            int count = result.getOrDefault("student" + student, 0);
            if (count != 10) { allCorrect = false; break; }
        }
        check(result.size() == 30, "Test9-AllStudents", "All 30 students should appear, got " + result.size());
        check(allCorrect, "Test9-CorrectCounts", "All 30 students should have count=10");
    }

    private static Post makePost(int postID, String author) {
        Post p = new Post("Test Title", "Test Body", author, "General");
        p.setPostID(postID);
        return p;
    }

    private static Reply makeReply(int postID, String author) {
        return new Reply(postID, "Test reply body", author);
    }
}