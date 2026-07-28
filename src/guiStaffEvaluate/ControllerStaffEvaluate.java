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
 * <p> Description: Controller for the Evaluate Student Discussion screen, satisfying
 *  STORY 3: Evaluate Student Discussion. Builds the per-student activity summary
 *  staff see before scoring (criterion 1), saves and updates per-parameter scores
 *  (criteria 2 and 3), tells staff to create parameters first if none exist yet
 *  (criterion 4), and computes a student's overall grade once every active
 *  parameter has been scored (criterion 6). The distinct-peer count in the
 *  activity summary is taken directly from
 *  {@code ControllerStaffCoverage.computeCoverage()} (Story 5) rather than being
 *  recomputed here, so a student's coverage number is identical whether staff are
 *  looking at this screen or the Answer-Coverage Report. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-26  Initial version
 *
 */
public class ControllerStaffEvaluate {

	/**********
	 * <p> Method: performStaffEvaluate(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen (e.g.,
	 *  from a button on Staff Home). Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
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
	 * @return a List of student usernames prefixed with a "&lt;Student&gt;"
	 *  placeholder; a list containing only the placeholder if the database call
	 *  fails
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
	 * <p> Description: Retrieves every currently defined EvaluationParameter; the
	 *  scoring form is built from whatever this returns. Story 3 criterion 4
	 *  ("If no Evaluation Parameters have been created yet, the screen tells the
	 *  staff user to go create some in Story 2 first") is handled by the View
	 *  checking whether this list is empty. </p>
	 *
	 * @return a List of all EvaluationParameter objects, or an empty list if none
	 *  have been created yet or the database call fails
	 *
	 */
	public static List<EvaluationParameter> performGetActiveParameters() {
		try {
			return ViewStaffEvaluate.theDatabase.readAllEvaluationParameters();
		} catch (SQLException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText("*** ERROR *** Could not load parameters.");
			return new ArrayList<EvaluationParameter>();
		}
	}

	/**********
	 * <p> Method: performGetStudentSummary(String studentUsername) </p>
	 *
	 * <p> Description: Builds the discussion-activity summary shown to staff
	 *  before scoring, satisfying Story 3 criterion 1 ("see a summary of that
	 *  student's discussion activity: total posts, total replies, and how many
	 *  distinct peers they've replied to"). Total posts and total replies are
	 *  counted directly from the full Post/Reply lists; the distinct-peer count
	 *  is taken from {@code ControllerStaffCoverage.computeCoverage()} instead of
	 *  being recomputed here, reusing Story 5's already-tested dedup and
	 *  self-reply rules rather than duplicating them. </p>
	 *
	 * @param studentUsername specifies the student to summarize
	 *
	 * @return a StudentActivitySummary for this student, or a summary of all
	 *  zeros if the database call fails
	 *
	 */
	public static StudentActivitySummary performGetStudentSummary(String studentUsername) {
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

			return new StudentActivitySummary(studentUsername, totalPosts, totalReplies, distinctPeers);
		} catch (SQLException e) {
			ViewStaffEvaluate.label_ErrorMessage.setText("*** ERROR *** Could not load student activity.");
			return new StudentActivitySummary(studentUsername, 0, 0, 0);
		}
	}

	/**********
	 * <p> Method: performGetScoresForStudent(String studentUsername) </p>
	 *
	 * <p> Description: Retrieves the scores already saved for this student so the
	 *  scoring form can be pre-populated, satisfying Story 3 criterion 3 ("view
	 *  and update scores they've already saved for a student"). </p>
	 *
	 * @param studentUsername specifies the student whose scores should be
	 *  retrieved
	 *
	 * @return a List of EvaluationScore objects for this student; empty if none
	 *  exist yet
	 *
	 */
	public static List<EvaluationScore> performGetScoresForStudent(String studentUsername) {
		return ViewStaffEvaluate.theDatabase.readScoresForStudent(studentUsername);
	}

	/**********
	 * <p> Method: performSaveScore(String studentUsername, int paramID,
	 *  String staffUsername, double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: Saves or updates a single parameter score for a student,
	 *  satisfying Story 3 criteria 2 (assign a score) and 3 (update an existing
	 *  score). Validation occurs inside the EvaluationScore constructor
	 *  (delegated to from Database.saveOrUpdateEvaluationScore()); if validation
	 *  fails or the database write fails, the resulting error message is
	 *  displayed to the user via label_ErrorMessage rather than being thrown back
	 *  to the caller, matching the pattern
	 *  ControllerStaffParameters.performCreateStaffParameter() uses. </p>
	 *
	 * @param studentUsername specifies the student being scored
	 *
	 * @param paramID specifies the EvaluationParameter being scored
	 *
	 * @param staffUsername specifies the staff member assigning the score
	 *
	 * @param scoreValue specifies the raw score being assigned
	 *
	 * @param maxScore specifies the maximum score allowed for this parameter
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
			ViewStaffEvaluate.label_ErrorMessage.setText("*** ERROR *** Could not save score.");
			return false;
		}
	}

	/**********
	 * <p> Method: performComputeOverallGrade(String studentUsername) </p>
	 *
	 * <p> Description: Computes a student's overall grade once every active
	 *  parameter has been scored, satisfying Story 3 criterion 6 ("the system
	 *  computes an overall grade by converting each score to a percentage of its
	 *  scale, multiplying by that parameter's weight, summing the results, and
	 *  dividing by the total combined weight"). Reconstructs an
	 *  EvaluationScoreList and EvaluationParameterList from the database and
	 *  delegates the weighted-average math to
	 *  EvaluationScoreList.computeOverallGrade(), which already returns null when
	 *  scoring is incomplete. </p>
	 *
	 * @param studentUsername specifies the student whose overall grade is
	 *  computed
	 *
	 * @return the student's overall grade as a percentage (0-100), or null if no
	 *  parameters exist yet or not every active parameter has been scored for
	 *  this student
	 *
	 */
	public static Double performComputeOverallGrade(String studentUsername) {
		try {
			EvaluationParameterList paramList = new EvaluationParameterList();
			for (EvaluationParameter p : ViewStaffEvaluate.theDatabase.readAllEvaluationParameters()) {
				paramList.addParameter(p);
			}

			EvaluationScoreList scoreList = new EvaluationScoreList();
			for (EvaluationScore s : ViewStaffEvaluate.theDatabase.readScoresForStudent(studentUsername)) {
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
	 * <p> Description: This method returns the user to the staff homepage </p>
	 *
	 */
	protected static void performReturn() {
		guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffEvaluate.theStage, ViewStaffEvaluate.theUser);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: This method terminates the execution of the program. It
	 *  leaves the database in a state where the normal login page will be
	 *  displayed when the application is restarted. </p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}