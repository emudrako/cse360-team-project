package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: EvaluationScore Class </p>
 *
 * <p> Description: This EvaluationScore class represents a single staff-assigned score
 *  for one student on one EvaluationParameter (Story 2). A student accumulates one
 *  EvaluationScore per active parameter; EvaluationScoreList.computeOverallGrade()
 *  combines them into a final grade using each parameter's weight. Staff can create a
 *  score and later update it -- there is exactly one EvaluationScore per
 *  (studentUsername, paramID) pair, enforced by EvaluationScoreList. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 */

public class EvaluationScore {
	/*
	 * These are the private attributes for this entity object
	 */
	private int           scoreID;
	private String        studentUsername;
	private int           paramID;
	private String        staffUsername;   // staff member who most recently saved this score
	private double        scoreValue;      // raw score, 0 to the parameter's maxScore
	private String        feedback;        // optional free-text feedback tied to this score
	private LocalDateTime scoredAt;

	/*****
	 * <p> Method: EvaluationScore(String studentUsername, int paramID,
	 *  String staffUsername, double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: This constructor is used to establish EvaluationScore entity
	 *  objects. Validates that the student and staff usernames are present and that
	 *  the score falls within the parameter's valid range [0, maxScore]. The caller
	 *  supplies maxScore (from the corresponding EvaluationParameter) so this class
	 *  does not need a direct dependency on EvaluationParameter. </p>
	 *
	 * @param studentUsername specifies the student being scored, must not be empty
	 *
	 * @param paramID specifies the EvaluationParameter this score is for
	 *
	 * @param staffUsername specifies the staff member assigning this score, must not
	 *  be empty
	 *
	 * @param scoreValue specifies the raw score being assigned
	 *
	 * @param maxScore specifies the maximum score allowed for the referenced parameter,
	 *  used only to validate scoreValue at construction time
	 *
	 * @throws IllegalArgumentException if studentUsername or staffUsername is empty,
	 *  or scoreValue is outside [0, maxScore]
	 *
	 * @see tests.EvaluateStudentDiscussionTest#testCreateValidScore()
	 * @see tests.EvaluateStudentDiscussionTest#testRejectEmptyStudentUsername()
	 * @see tests.EvaluateStudentDiscussionTest#testRejectEmptyStaffUsername()
	 * @see tests.EvaluateStudentDiscussionTest#testScoreLowerBoundaryValid()
	 * @see tests.EvaluateStudentDiscussionTest#testScoreLowerBoundaryInvalid()
	 * @see tests.EvaluateStudentDiscussionTest#testScoreUpperBoundaryValid()
	 * @see tests.EvaluateStudentDiscussionTest#testScoreUpperBoundaryInvalid()
	 *
	 */
	public EvaluationScore(String studentUsername, int paramID, String staffUsername,
			double scoreValue, double maxScore) {
		if (studentUsername == null || studentUsername.trim().isEmpty()) {
			throw new IllegalArgumentException("Student username must not be empty.");
		}
		if (staffUsername == null || staffUsername.trim().isEmpty()) {
			throw new IllegalArgumentException("Staff username must not be empty.");
		}
		if (scoreValue < 0 || scoreValue > maxScore) {
			throw new IllegalArgumentException(
					"Score must be between 0 and " + maxScore + " inclusive.");
		}

		this.studentUsername = studentUsername;
		this.paramID = paramID;
		this.staffUsername = staffUsername;
		this.scoreValue = scoreValue;
		this.feedback = null;
		this.scoredAt = LocalDateTime.now();
	}


	/*****
	 * <p> Method: int getScoreID() </p>
	 *
	 * <p> Description: This getter returns the ScoreID. </p>
	 *
	 * @return an int of the ScoreID
	 *
	 */
	public int getScoreID() { return scoreID; }


	/*****
	 * <p> Method: void setScoreID(int id) </p>
	 *
	 * <p> Description: This setter defines the ScoreID attribute. </p>
	 *
	 * @param id specifies the ScoreID assigned to this score by the database
	 *
	 */
	public void setScoreID(int id) { scoreID = id; }


	/*****
	 * <p> Method: String getStudentUsername() </p>
	 *
	 * <p> Description: This getter returns the StudentUsername. </p>
	 *
	 * @return a String of the student's username
	 *
	 */
	public String getStudentUsername() { return studentUsername; }


	/*****
	 * <p> Method: int getParamID() </p>
	 *
	 * <p> Description: This getter returns the ParamID this score was assigned for. </p>
	 *
	 * @return an int of the EvaluationParameter's ID
	 *
	 */
	public int getParamID() { return paramID; }


	/*****
	 * <p> Method: String getStaffUsername() </p>
	 *
	 * <p> Description: This getter returns the username of the staff member who most
	 *  recently saved this score. </p>
	 *
	 * @return a String of the staff member's username
	 *
	 */
	public String getStaffUsername() { return staffUsername; }


	/*****
	 * <p> Method: void setStaffUsername(String username) </p>
	 *
	 * <p> Description: This setter defines the StaffUsername attribute. Used when an
	 *  existing score is updated by a (possibly different) staff member. </p>
	 *
	 * @param username specifies the staff member updating this score
	 *
	 */
	public void setStaffUsername(String username) { this.staffUsername = username; }


	/*****
	 * <p> Method: double getScoreValue() </p>
	 *
	 * <p> Description: This getter returns the raw ScoreValue. </p>
	 *
	 * @return a double of the raw score assigned
	 *
	 */
	public double getScoreValue() { return scoreValue; }


	/*****
	 * <p> Method: void setScoreValue(double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: This setter defines the ScoreValue attribute, re-validating the
	 *  new value against the parameter's maxScore so an update can never bypass the
	 *  range check performed at construction time. </p>
	 *
	 * @param scoreValue specifies the new raw score
	 *
	 * @param maxScore specifies the maximum score allowed for the referenced parameter
	 *
	 * @throws IllegalArgumentException if scoreValue is outside [0, maxScore]
	 *
	 */
	public void setScoreValue(double scoreValue, double maxScore) {
		if (scoreValue < 0 || scoreValue > maxScore) {
			throw new IllegalArgumentException(
					"Score must be between 0 and " + maxScore + " inclusive.");
		}
		this.scoreValue = scoreValue;
	}


	/*****
	 * <p> Method: String getFeedback() </p>
	 *
	 * <p> Description: This getter returns the optional free-text feedback tied to
	 *  this score. </p>
	 *
	 * @return a String of the feedback, or null if none has been provided
	 *
	 */
	public String getFeedback() { return feedback; }


	/*****
	 * <p> Method: void setFeedback(String feedback) </p>
	 *
	 * <p> Description: This setter defines the Feedback attribute. </p>
	 *
	 * @param feedback specifies the feedback text to attach to this score
	 *
	 */
	public void setFeedback(String feedback) { this.feedback = feedback; }


	/*****
	 * <p> Method: LocalDateTime getScoredAt() </p>
	 *
	 * <p> Description: This getter returns the timestamp of when this score was most
	 *  recently saved. </p>
	 *
	 * @return a LocalDateTime of when this score was last saved
	 *
	 */
	public LocalDateTime getScoredAt() { return scoredAt; }


	/*****
	 * <p> Method: void setScoredAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the ScoredAt timestamp. Called when an
	 *  existing score is updated so the timestamp reflects the most recent save. </p>
	 *
	 * @param time specifies the new timestamp
	 *
	 */
	public void setScoredAt(LocalDateTime time) { this.scoredAt = time; }
}