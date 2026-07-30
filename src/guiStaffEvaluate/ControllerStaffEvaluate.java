package guiStaffEvaluate;

import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entityClasses.EvaluationParameter;
import entityClasses.EvaluationParameterList;
import entityClasses.EvaluationScore;
import entityClasses.EvaluationScoreList;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.StudentActivitySummary;
import entityClasses.User;
import guiStaffCoverage.ControllerStaffCoverage;

/*******
 * <p> Title: ControllerStaffEvaluate Class. </p>
 *
 * <p> Description: Controller for the Evaluate Student Discussion screen,
 *  satisfying STORY 3: Evaluate Student Discussion. Builds the per-student
 *  activity summary staff see before scoring (criterion 1), saves and updates
 *  per-parameter scores (criteria 2 and 3), tells staff to create parameters
 *  first if none exist yet (criterion 4), and computes a student's overall
 *  grade once every active parameter has been scored (criterion 6). The
 *  distinct-peer count in the activity summary is taken directly from
 *  ControllerStaffCoverage.computeCoverage() (Story 5) rather than being
 *  recomputed here, so a student's coverage number is identical whether staff
 *  are looking at this screen or the Answer-Coverage Report.
 *  Follows the MVC Controller pattern established by the Foundations-SU26
 *  code — static methods only, never instantiated. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00		2026-07-26 Initial version
 *
 */
public class ControllerStaffEvaluate {

	/**********
	 * <p> Method: performStaffEvaluate(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen
	 *  from Staff Home. Displays the corresponding View. </p>
	 *
	 * @param ps   specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void performStaffEvaluate(Stage ps, User user) {
		ViewStaffEvaluate.displayStaffEvaluate(ps, user);
	}

	/**********
	 * <p> Method: performGetStudentList() </p>
	 *
	 * <p> Description: Retrieves the list of student usernames for the student
	 *  selector, satisfying Story 3 criterion 1: "A staff user can select a
	 *  student from a list." </p>
	 *
	 * @return a List of student usernames; a list containing only the
	 *  placeholder if the database call fails
	 *
	 */
	public static List<String> performGetStudentList() {
		List<String> students = ViewStaffEvaluate.theDatabase.getStudentUserList();
		if (students == null) {
			students = new ArrayList<String>();
			students.add("<Student>");
		}
		return students;
	}

	/**********
	 * <p> Method: performGetActiveParameters() </p>
	 *
	 * <p> Description: Retrieves every currently defined EvaluationParameter.
	 *  Story 3 criterion 4 is handled by the View checking whether this list
	 *  is empty. </p>
	 *
	 * @return a List of all EvaluationParameter objects, or an empty list if
	 *  none exist or the database call fails
	 *
	 */
	public static List<EvaluationParameter> performGetActiveParameters() {
		try {
			return ViewStaffEvaluate.theDatabase.readAllEvaluationParameters();
		} catch (SQLException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText(
				"*** ERROR *** Could not load parameters.");
			return new ArrayList<EvaluationParameter>();
		}
	}

	/**********
	 * <p> Method: performGetStudentSummary(String studentUsername) </p>
	 *
	 * <p> Description: Builds the discussion-activity summary shown to staff
	 *  before scoring, satisfying Story 3 criterion 1. Total posts and total
	 *  replies are counted directly from the full Post/Reply lists; the
	 *  distinct-peer count is taken from
	 *  ControllerStaffCoverage.computeCoverage() instead of being recomputed
	 *  here, reusing Story 5's already-tested dedup and self-reply rules rather
	 *  than duplicating them. Reusing Story 5's method ensures both screens
	 *  always show the same peer count for the same student — if the count were
	 *  recomputed here, a future bug fix in one place might not be applied to
	 *  the other. </p>
	 *
	 * <p> Validated by: EvaluateStudentDiscussionTest.testSummaryCountsPostsAndReplies(),
	 *  EvaluateStudentDiscussionTest.testSummaryDedupsRepliesToSamePeer(),
	 *  EvaluateStudentDiscussionTest.testSummaryExcludesSelfReplies(),
	 *  EvaluateStudentDiscussionTest.testSummaryZeroActivityStudent(). </p>
	 *
	 * @param studentUsername specifies the student to summarize
	 *
	 * @return a StudentActivitySummary for this student, or a summary of all
	 *  zeros if the database call fails
	 *
	 */
	public static StudentActivitySummary performGetStudentSummary(
			String studentUsername) {
		try {
			List<Post> allPosts = ViewStaffEvaluate.theDatabase.getPostObjects();
			List<Reply> allReplies = ViewStaffEvaluate.theDatabase.getReplyObjects();

			int totalPosts = 0;
			for (Post p : allPosts) {
				if (p.getAuthorUsername().equals(studentUsername)) totalPosts++;
			}

			int totalReplies = 0;
			for (Reply r : allReplies) {
				if (r.getAuthorUsername().equals(studentUsername)) totalReplies++;
			}

			// Reuse Story 5's coverage computation rather than duplicating its
			// dedup / self-reply rules here
			Map<String, Integer> coverageMap =
				ControllerStaffCoverage.computeCoverage(allReplies, allPosts);
			int distinctPeers = coverageMap.getOrDefault(studentUsername, 0);

			return new StudentActivitySummary(
				studentUsername, totalPosts, totalReplies, distinctPeers);
		} catch (SQLException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText(
				"*** ERROR *** Could not load student activity.");
			return new StudentActivitySummary(studentUsername, 0, 0, 0);
		}
	}

	/**********
	 * <p> Method: performGetScoresForStudent(String studentUsername) </p>
	 *
	 * <p> Description: Retrieves the scores already saved for this student so
	 *  the scoring form can be pre-populated, satisfying Story 3 criterion 3. </p>
	 *
	 * @param studentUsername specifies the student whose scores to retrieve
	 *
	 * @return a List of EvaluationScore objects; empty if none exist yet
	 *
	 */
	public static List<EvaluationScore> performGetScoresForStudent(
			String studentUsername) {
		return ViewStaffEvaluate.theDatabase.readScoresForStudent(studentUsername);
	}

	/**********
	 * <p> Method: performSaveScore(String studentUsername, int paramID,
	 *  String staffUsername, double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: Saves or updates a single parameter score for a student,
	 *  satisfying Story 3 criteria 2 and 3. Validation occurs inside the
	 *  EvaluationScore constructor; if validation fails or the database write
	 *  fails, the error message is displayed via label_ErrorMessage rather than
	 *  being thrown back to the caller, matching the pattern
	 *  ControllerStaffParameters.performCreateStaffParameter() uses. </p>
	 *
	 * <p> Validated by: EvaluateStudentDiscussionTest.testSaveNewScorePersists(),
	 *  EvaluateStudentDiscussionTest.testReScoringUpdatesInPlace(),
	 *  EvaluateStudentDiscussionTest.testSaveRejectsInvalidScoreAtDatabaseLayer(),
	 *  EvaluateStudentDiscussionTest.testSaveNewScore(),
	 *  EvaluateStudentDiscussionTest.testUpdateExistingScoreDoesNotDuplicate(). </p>
	 *
	 * @param studentUsername specifies the student being scored
	 *
	 * @param paramID         specifies the EvaluationParameter being scored
	 *
	 * @param staffUsername   specifies the staff member assigning the score
	 *
	 * @param scoreValue      specifies the raw score being assigned
	 *
	 * @param maxScore        specifies the maximum score for this parameter
	 *
	 * @return true if the score was saved successfully, false otherwise
	 *
	 */
	public static boolean performSaveScore(String studentUsername, int paramID,
			String staffUsername, double scoreValue, double maxScore) {
		try {
			ViewStaffEvaluate.theDatabase.saveOrUpdateEvaluationScore(
				studentUsername, paramID, staffUsername, scoreValue, maxScore);
			return true;
		} catch (IllegalArgumentException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText(e.getMessage());
			return false;
		} catch (SQLException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText(
				"*** ERROR *** Could not save score.");
			return false;
		}
	}

	/**********
	 * <p> Method: performComputeOverallGrade(String studentUsername) </p>
	 *
	 * <p> Description: Computes a student's overall grade once every active
	 *  parameter has been scored, satisfying Story 3 criterion 6. Formula:
	 *  for each parameter, convert score to percentage of its scale, multiply
	 *  by weight, sum all results, divide by total combined weight. Returns null
	 *  when scoring is incomplete to prevent misleading partial grades from
	 *  appearing on screen. </p>
	 *
	 * <p> Validated by: EvaluateStudentDiscussionTest.testOverallGradeSingleParameter(),
	 *  EvaluateStudentDiscussionTest.testOverallGradeMultipleWeightedParameters(),
	 *  EvaluateStudentDiscussionTest.testOverallGradeNullWhenIncomplete(),
	 *  EvaluateStudentDiscussionTest.testOverallGradeNullWhenNoParameters(). </p>
	 *
	 * @param studentUsername specifies the student whose overall grade is computed
	 *
	 * @return the overall grade as a percentage (0-100), or null if incomplete
	 *
	 */
	public static Double performComputeOverallGrade(String studentUsername) {
		try {
			EvaluationParameterList paramList = new EvaluationParameterList();
			for (EvaluationParameter p :
					ViewStaffEvaluate.theDatabase.readAllEvaluationParameters()) {
				paramList.addParameter(p);
			}

			EvaluationScoreList scoreList = new EvaluationScoreList();
			for (EvaluationScore s :
					ViewStaffEvaluate.theDatabase.readScoresForStudent(studentUsername)) {
				scoreList.addScore(s);
			}

			return scoreList.computeOverallGrade(studentUsername, paramList);
		} catch (SQLException e) {
			return null;
		}
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the user to the Staff Home page. </p>
	 *
	 */
	protected static void performReturn() {
		guiStaffHome.ViewStaffHome.displayStaffHome(
			ViewStaffEvaluate.theStage, ViewStaffEvaluate.theUser);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}