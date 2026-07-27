package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.EvaluationParameter;
import entityClasses.EvaluationParameterList;
import entityClasses.EvaluationScore;
import entityClasses.EvaluationScoreList;
import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.Reply;
import entityClasses.ReplyList;
import entityClasses.StudentActivitySummary;

/*******
 * <p> Title: EvaluateStudentDiscussionTest Class. </p>
 *
 * <p> Description: Test suite for STORY 3: Evaluate Student Discussion. Covers four
 *  layers separately: (1) the EvaluationScore entity's own validation, (2)
 *  EvaluationScoreList's business logic (upsert-in-memory and computeOverallGrade,
 *  which implements criterion 6's weighted-average formula), (3) StudentActivitySummary's
 *  post/reply/distinct-peer counting (criterion 1), and (4) the Database class's
 *  saveOrUpdateEvaluationScore()/readScoresForStudent(), confirming the H2 MERGE-based
 *  upsert actually updates in place rather than creating a duplicate row. Tests 1-3 run
 *  against plain Java objects with no database; test 4 uses Database.connectToTestDatabase()
 *  for a private in-memory H2 instance, the same pattern used elsewhere in this test suite. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-26  Initial version
 *
 */
public class EvaluateStudentDiscussionTest {

	private Database db;

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


	// =========================================================================
	// EvaluationScore entity validation
	// =========================================================================

	/*****
	 * <p> Method: testCreateValidScore() </p>
	 *
	 * <p> Description: Verifies that a valid EvaluationScore is constructed
	 *  successfully and its fields are readable afterward. </p>
	 *
	 */
	@Test
	public void testCreateValidScore() {
		EvaluationScore score = new EvaluationScore("alice", 1, "profSmith", 8.0, 10.0);
		assertEquals("alice", score.getStudentUsername());
		assertEquals(1, score.getParamID());
		assertEquals("profSmith", score.getStaffUsername());
		assertEquals(8.0, score.getScoreValue(), 0.001);
	}

	/*****
	 * <p> Method: testRejectEmptyStudentUsername() </p>
	 *
	 * <p> Description: Verifies that an empty student username is rejected. </p>
	 *
	 */
	@Test
	public void testRejectEmptyStudentUsername() {
		assertThrows(IllegalArgumentException.class,
				() -> new EvaluationScore("  ", 1, "profSmith", 8.0, 10.0));
	}

	/*****
	 * <p> Method: testRejectEmptyStaffUsername() </p>
	 *
	 * <p> Description: Verifies that an empty staff username is rejected. </p>
	 *
	 */
	@Test
	public void testRejectEmptyStaffUsername() {
		assertThrows(IllegalArgumentException.class,
				() -> new EvaluationScore("alice", 1, "", 8.0, 10.0));
	}

	/*****
	 * <p> Method: testScoreLowerBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that a score of exactly 0 is accepted. </p>
	 *
	 */
	@Test
	public void testScoreLowerBoundaryValid() {
		assertDoesNotThrow(() -> new EvaluationScore("alice", 1, "profSmith", 0.0, 10.0));
	}

	/*****
	 * <p> Method: testScoreLowerBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that a negative score is rejected. </p>
	 *
	 */
	@Test
	public void testScoreLowerBoundaryInvalid() {
		assertThrows(IllegalArgumentException.class,
				() -> new EvaluationScore("alice", 1, "profSmith", -0.01, 10.0));
	}

	/*****
	 * <p> Method: testScoreUpperBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that a score exactly equal to maxScore is accepted. </p>
	 *
	 */
	@Test
	public void testScoreUpperBoundaryValid() {
		assertDoesNotThrow(() -> new EvaluationScore("alice", 1, "profSmith", 10.0, 10.0));
	}

	/*****
	 * <p> Method: testScoreUpperBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that a score exceeding maxScore is rejected. </p>
	 *
	 */
	@Test
	public void testScoreUpperBoundaryInvalid() {
		assertThrows(IllegalArgumentException.class,
				() -> new EvaluationScore("alice", 1, "profSmith", 10.01, 10.0));
	}


	// =========================================================================
	// EvaluationScoreList: saveOrUpdateScore and computeOverallGrade
	// =========================================================================

	/*****
	 * <p> Method: testSaveNewScore() </p>
	 *
	 * <p> Description: Verifies that saving a score for a (student, parameter) pair
	 *  that has never been scored before adds exactly one EvaluationScore. </p>
	 *
	 */
	@Test
	public void testSaveNewScore() {
		EvaluationScoreList scores = new EvaluationScoreList();
		scores.saveOrUpdateScore("alice", 1, "profSmith", 7.0, 10.0);

		assertEquals(1, scores.getScoresForStudent("alice").size());
		assertEquals(7.0, scores.getScoreForStudentAndParam("alice", 1).getScoreValue(), 0.001);
	}

	/*****
	 * <p> Method: testUpdateExistingScoreDoesNotDuplicate() </p>
	 *
	 * <p> Description: Verifies that re-scoring the same (student, parameter) pair
	 *  updates the existing EvaluationScore in place rather than adding a second one,
	 *  satisfying Story 3 criterion 3 (updating a previously saved score). </p>
	 *
	 */
	@Test
	public void testUpdateExistingScoreDoesNotDuplicate() {
		EvaluationScoreList scores = new EvaluationScoreList();
		scores.saveOrUpdateScore("alice", 1, "profSmith", 6.0, 10.0);
		scores.saveOrUpdateScore("alice", 1, "profJones", 9.0, 10.0);

		assertEquals(1, scores.getScoresForStudent("alice").size());
		EvaluationScore updated = scores.getScoreForStudentAndParam("alice", 1);
		assertEquals(9.0, updated.getScoreValue(), 0.001);
		assertEquals("profJones", updated.getStaffUsername());
	}

	/*****
	 * <p> Method: testSaveRejectsOutOfRangeScore() </p>
	 *
	 * <p> Description: Verifies that saveOrUpdateScore propagates the
	 *  IllegalArgumentException from EvaluationScore's validation rather than
	 *  silently accepting an invalid score. </p>
	 *
	 */
	@Test
	public void testSaveRejectsOutOfRangeScore() {
		EvaluationScoreList scores = new EvaluationScoreList();
		assertThrows(IllegalArgumentException.class,
				() -> scores.saveOrUpdateScore("alice", 1, "profSmith", 15.0, 10.0));
	}

	/*****
	 * <p> Method: testOverallGradeSingleParameter() </p>
	 *
	 * <p> Description: Verifies criterion 6's formula for the simplest case: one
	 *  parameter, weight is irrelevant since it is the only one. A score of 8/10
	 *  should produce an overall grade of 80%. </p>
	 *
	 */
	@Test
	public void testOverallGradeSingleParameter() {
		EvaluationParameterList params = new EvaluationParameterList();
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 5.0);
		params.addParameter(clarity);

		EvaluationScoreList scores = new EvaluationScoreList();
		scores.saveOrUpdateScore("alice", clarity.getParamID(), "profSmith", 8.0, 10.0);

		Double grade = scores.computeOverallGrade("alice", params);
		assertNotNull(grade);
		assertEquals(80.0, grade, 0.001);
	}

	/*****
	 * <p> Method: testOverallGradeMultipleWeightedParameters() </p>
	 *
	 * <p> Description: Verifies criterion 6's formula across two differently-weighted
	 *  parameters (weights kept within EvaluationParameter's own valid 1-10 range).
	 *  Clarity: 8/10 (80%), weight 6. Helpfulness: 3/5 (60%), weight 4.
	 *  Weighted sum = 80*6 + 60*4 = 480 + 240 = 720; total weight = 10;
	 *  expected grade = 720 / 10 = 72%. </p>
	 *
	 */
	@Test
	public void testOverallGradeMultipleWeightedParameters() {
		EvaluationParameterList params = new EvaluationParameterList();
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 6.0);
		clarity.setParamID(1);
		EvaluationParameter helpfulness = new EvaluationParameter(
				"Helpfulness", "Evaluates whether the student's replies genuinely help peers.", 5.0, 4.0);
		helpfulness.setParamID(2);
		params.addParameter(clarity);
		params.addParameter(helpfulness);

		EvaluationScoreList scores = new EvaluationScoreList();
		scores.saveOrUpdateScore("alice", clarity.getParamID(), "profSmith", 8.0, 10.0);
		scores.saveOrUpdateScore("alice", helpfulness.getParamID(), "profSmith", 3.0, 5.0);

		Double grade = scores.computeOverallGrade("alice", params);
		assertNotNull(grade);
		assertEquals(72.0, grade, 0.001);
	}

	/*****
	 * <p> Method: testOverallGradeNullWhenIncomplete() </p>
	 *
	 * <p> Description: Verifies that computeOverallGrade returns null rather than a
	 *  misleading partial number when not every active parameter has been scored
	 *  yet, per criterion 6 ("Once all active parameters have been scored ..."). </p>
	 *
	 */
	@Test
	public void testOverallGradeNullWhenIncomplete() {
		EvaluationParameterList params = new EvaluationParameterList();
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 6.0);
		clarity.setParamID(1);
		EvaluationParameter helpfulness = new EvaluationParameter(
				"Helpfulness", "Evaluates whether the student's replies genuinely help peers.", 5.0, 4.0);
		helpfulness.setParamID(2);
		params.addParameter(clarity);
		params.addParameter(helpfulness);

		EvaluationScoreList scores = new EvaluationScoreList();
		scores.saveOrUpdateScore("alice", clarity.getParamID(), "profSmith", 8.0, 10.0);
		// helpfulness intentionally left unscored

		assertNull(scores.computeOverallGrade("alice", params));
		assertFalse(scores.hasCompleteScores("alice", params));
	}

	/*****
	 * <p> Method: testOverallGradeNullWhenNoParameters() </p>
	 *
	 * <p> Description: Verifies that computeOverallGrade returns null when no
	 *  EvaluationParameters exist at all, rather than dividing by a zero total weight. </p>
	 *
	 */
	@Test
	public void testOverallGradeNullWhenNoParameters() {
		EvaluationParameterList params = new EvaluationParameterList();
		EvaluationScoreList scores = new EvaluationScoreList();

		assertNull(scores.computeOverallGrade("alice", params));
	}


	// =========================================================================
	// StudentActivitySummary: post/reply/distinct-peer counting
	// =========================================================================

	/*****
	 * <p> Method: testSummaryCountsPostsAndReplies() </p>
	 *
	 * <p> Description: Verifies that buildSummary() correctly counts a student's
	 *  total posts and total replies, satisfying Story 3 criterion 1. </p>
	 *
	 */
	@Test
	public void testSummaryCountsPostsAndReplies() {
		PostList posts = new PostList();
		Post p1 = new Post("Question 1", "Body of question 1", "alice", "General");
		p1.setPostID(1);
		Post p2 = new Post("Question 2", "Body of question 2", "bob", "General");
		p2.setPostID(2);
		posts.addPost(p1);
		posts.addPost(p2);

		ReplyList replies = new ReplyList();
		Reply r1 = new Reply(2, "Reply to bob's post", "alice");
		r1.setReplyID(1);
		replies.addReply(r1);

		StudentActivitySummary summary = StudentActivitySummary.buildSummary("alice", posts, replies);

		assertEquals(1, summary.getTotalPosts());
		assertEquals(1, summary.getTotalReplies());
	}

	/*****
	 * <p> Method: testSummaryDedupsRepliesToSamePeer() </p>
	 *
	 * <p> Description: Verifies that multiple replies to posts by the same peer
	 *  collapse to a distinct-peer count of one, matching Story 5's coverage rules. </p>
	 *
	 */
	@Test
	public void testSummaryDedupsRepliesToSamePeer() {
		PostList posts = new PostList();
		Post bobPost1 = new Post("Bob's first post", "Body", "bob", "General");
		bobPost1.setPostID(1);
		Post bobPost2 = new Post("Bob's second post", "Body", "bob", "General");
		bobPost2.setPostID(2);
		posts.addPost(bobPost1);
		posts.addPost(bobPost2);

		ReplyList replies = new ReplyList();
		Reply r1 = new Reply(1, "First reply to bob", "alice");
		r1.setReplyID(1);
		Reply r2 = new Reply(2, "Second reply to bob", "alice");
		r2.setReplyID(2);
		replies.addReply(r1);
		replies.addReply(r2);

		StudentActivitySummary summary = StudentActivitySummary.buildSummary("alice", posts, replies);

		assertEquals(2, summary.getTotalReplies());
		assertEquals(1, summary.getDistinctPeersRepliedTo());
	}

	/*****
	 * <p> Method: testSummaryExcludesSelfReplies() </p>
	 *
	 * <p> Description: Verifies that a student replying to their own post does not
	 *  count toward their distinct-peer total, matching Story 5's self-reply rule. </p>
	 *
	 */
	@Test
	public void testSummaryExcludesSelfReplies() {
		PostList posts = new PostList();
		Post ownPost = new Post("Alice's own post", "Body", "alice", "General");
		ownPost.setPostID(1);
		posts.addPost(ownPost);

		ReplyList replies = new ReplyList();
		Reply selfReply = new Reply(1, "Replying to my own post", "alice");
		selfReply.setReplyID(1);
		replies.addReply(selfReply);

		StudentActivitySummary summary = StudentActivitySummary.buildSummary("alice", posts, replies);

		assertEquals(1, summary.getTotalReplies());
		assertEquals(0, summary.getDistinctPeersRepliedTo());
	}

	/*****
	 * <p> Method: testSummaryZeroActivityStudent() </p>
	 *
	 * <p> Description: Verifies that a student with no posts or replies gets a
	 *  summary of all zeros rather than an error, satisfying Story 5 criterion 5's
	 *  requirement that no student is omitted from the report. </p>
	 *
	 */
	@Test
	public void testSummaryZeroActivityStudent() {
		PostList posts = new PostList();
		ReplyList replies = new ReplyList();

		StudentActivitySummary summary = StudentActivitySummary.buildSummary("newStudent", posts, replies);

		assertEquals(0, summary.getTotalPosts());
		assertEquals(0, summary.getTotalReplies());
		assertEquals(0, summary.getDistinctPeersRepliedTo());
	}


	// =========================================================================
	// Database persistence: saveOrUpdateEvaluationScore / readScoresForStudent
	// =========================================================================

	/*****
	 * <p> Method: testSaveNewScorePersists() </p>
	 *
	 * <p> Description: Verifies that saveOrUpdateEvaluationScore() persists a new
	 *  score and that readScoresForStudent() can retrieve it afterward. </p>
	 *
	 */
	@Test
	public void testSaveNewScorePersists() throws SQLException {
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 5.0);
		db.createEvaluationParameter(clarity);

		db.saveOrUpdateEvaluationScore("alice", clarity.getParamID(), "profSmith", 7.0, 10.0);

		List<EvaluationScore> scores = db.readScoresForStudent("alice");
		assertEquals(1, scores.size());
		assertEquals(7.0, scores.get(0).getScoreValue(), 0.001);
	}

	/*****
	 * <p> Method: testReScoringUpdatesInPlace() </p>
	 *
	 * <p> Description: Verifies that calling saveOrUpdateEvaluationScore() twice for
	 *  the same (student, parameter) pair results in exactly one row in
	 *  EvaluationScoresDB with the latest value, confirming the MERGE-based upsert
	 *  works as intended rather than creating a duplicate. </p>
	 *
	 */
	@Test
	public void testReScoringUpdatesInPlace() throws SQLException {
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 5.0);
		db.createEvaluationParameter(clarity);

		db.saveOrUpdateEvaluationScore("alice", clarity.getParamID(), "profSmith", 6.0, 10.0);
		db.saveOrUpdateEvaluationScore("alice", clarity.getParamID(), "profJones", 9.0, 10.0);

		List<EvaluationScore> scores = db.readScoresForStudent("alice");
		assertEquals(1, scores.size());
		assertEquals(9.0, scores.get(0).getScoreValue(), 0.001);
		assertEquals("profJones", scores.get(0).getStaffUsername());
	}

	/*****
	 * <p> Method: testReadScoresForStudentEmptyWhenNoneSaved() </p>
	 *
	 * <p> Description: Verifies that a student with no saved scores yet gets an
	 *  empty list rather than null or an error, satisfying Story 3 criterion 3's
	 *  implicit requirement that the scoring screen has something safe to display
	 *  before any scores exist. </p>
	 *
	 */
	@Test
	public void testReadScoresForStudentEmptyWhenNoneSaved() {
		List<EvaluationScore> scores = db.readScoresForStudent("brandNewStudent");
		assertNotNull(scores);
		assertTrue(scores.isEmpty());
	}

	/*****
	 * <p> Method: testSaveRejectsInvalidScoreAtDatabaseLayer() </p>
	 *
	 * <p> Description: Verifies that Database.saveOrUpdateEvaluationScore() rejects
	 *  an out-of-range score before touching the database, the same way
	 *  updateEvaluationParameter() reuses EvaluationParameter's constructor for
	 *  validation. </p>
	 *
	 */
	@Test
	public void testSaveRejectsInvalidScoreAtDatabaseLayer() throws SQLException {
		EvaluationParameter clarity = new EvaluationParameter(
				"Clarity", "Measures how clearly the student communicates their ideas.", 10.0, 5.0);
		db.createEvaluationParameter(clarity);

		assertThrows(IllegalArgumentException.class,
				() -> db.saveOrUpdateEvaluationScore(
						"alice", clarity.getParamID(), "profSmith", 25.0, 10.0));
	}
}