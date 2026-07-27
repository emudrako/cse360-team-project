package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: EvaluationScoreList Class </p>
 *
 * <p> Description: This EvaluationScoreList class supports storing all current
 *  EvaluationScore objects in the system and provides the business logic for Story 3
 *  (Evaluate Student Discussion): saving/updating a student's score on a parameter,
 *  retrieving a student's saved scores, and computing a student's overall grade once
 *  every active EvaluationParameter has been scored. There is exactly one
 *  EvaluationScore per (studentUsername, paramID) pair -- saveOrUpdateScore()
 *  enforces this by updating in place rather than adding a duplicate, matching the
 *  update-in-place pattern RequestList uses for closing/reopening requests. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 */

public class EvaluationScoreList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<EvaluationScore> scores;

	/*****
	 * <p> Method: EvaluationScoreList() </p>
	 *
	 * <p> Description: This constructor establishes an empty EvaluationScoreList,
	 *  ready to have EvaluationScore objects added to it. </p>
	 *
	 */
	public EvaluationScoreList() {
		this.scores = new ArrayList<EvaluationScore>();
	}


	/*****
	 * <p> Method: void addScore(EvaluationScore score) </p>
	 *
	 * <p> Description: This method adds an EvaluationScore object directly to the
	 *  list. Intended for reconstructing the list from the database (e.g. inside
	 *  Database.readAllEvaluationScores()); application code that assigns a grade
	 *  should call saveOrUpdateScore() instead so duplicates for the same student and
	 *  parameter are never created. </p>
	 *
	 * @param score specifies the EvaluationScore object to be added to the list
	 *
	 */
	public void addScore(EvaluationScore score) {
		scores.add(score);
	}


	/*****
	 * <p> Method: List<EvaluationScore> getAllScores() </p>
	 *
	 * <p> Description: This getter returns the complete list of EvaluationScore
	 *  objects. </p>
	 *
	 * @return a List of all EvaluationScore objects currently stored
	 *
	 */
	public List<EvaluationScore> getAllScores() {
		return scores;
	}


	/*****
	 * <p> Method: List<EvaluationScore> getScoresForStudent(String studentUsername) </p>
	 *
	 * <p> Description: This method returns every EvaluationScore currently saved for
	 *  the specified student, one per scored parameter. Supports Story 3 criterion 3,
	 *  letting staff view a student's previously saved scores. </p>
	 *
	 * @param studentUsername specifies the student whose scores should be returned
	 *
	 * @return a List of EvaluationScore objects belonging to the specified student
	 *
	 */
	public List<EvaluationScore> getScoresForStudent(String studentUsername) {
		List<EvaluationScore> result = new ArrayList<EvaluationScore>();
		for (EvaluationScore s : scores) {
			if (s.getStudentUsername().equals(studentUsername)) {
				result.add(s);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: EvaluationScore getScoreForStudentAndParam(String studentUsername,
	 *  int paramID) </p>
	 *
	 * <p> Description: This method returns the single EvaluationScore matching the
	 *  specified student and parameter, or null if that parameter has not been scored
	 *  for that student yet. </p>
	 *
	 * @param studentUsername specifies the student to look up
	 *
	 * @param paramID specifies the EvaluationParameter to look up
	 *
	 * @return the matching EvaluationScore, or null if not found
	 *
	 */
	public EvaluationScore getScoreForStudentAndParam(String studentUsername, int paramID) {
		for (EvaluationScore s : scores) {
			if (s.getStudentUsername().equals(studentUsername) && s.getParamID() == paramID) {
				return s;
			}
		}
		return null;
	}


	/*****
	 * <p> Method: EvaluationScore saveOrUpdateScore(String studentUsername, int paramID,
	 *  String staffUsername, double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: This method saves a staff-assigned score for a student on a
	 *  parameter. If a score for this (student, parameter) pair already exists, it is
	 *  updated in place (new value, new staff username, new timestamp) rather than
	 *  creating a duplicate; otherwise a new EvaluationScore is created and added.
	 *  Supports Story 3 criteria 2 (assign a score) and 3 (update an existing score). </p>
	 *
	 * @param studentUsername specifies the student being scored
	 *
	 * @param paramID specifies the EvaluationParameter being scored
	 *
	 * @param staffUsername specifies the staff member assigning the score
	 *
	 * @param scoreValue specifies the raw score being assigned
	 *
	 * @param maxScore specifies the maximum score allowed for this parameter, used to
	 *  validate scoreValue
	 *
	 * @return the created or updated EvaluationScore
	 *
	 * @throws IllegalArgumentException if the underlying validation in EvaluationScore
	 *  fails (empty usernames or out-of-range score)
	 *
	 * @see tests.EvaluateStudentDiscussionTest#testSaveNewScore()
	 * @see tests.EvaluateStudentDiscussionTest#testUpdateExistingScoreDoesNotDuplicate()
	 * @see tests.EvaluateStudentDiscussionTest#testSaveRejectsOutOfRangeScore()
	 *
	 */
	public EvaluationScore saveOrUpdateScore(String studentUsername, int paramID,
			String staffUsername, double scoreValue, double maxScore) {
		EvaluationScore existing = getScoreForStudentAndParam(studentUsername, paramID);
		if (existing != null) {
			existing.setScoreValue(scoreValue, maxScore); // validates range, throws if invalid
			existing.setStaffUsername(staffUsername);
			existing.setScoredAt(java.time.LocalDateTime.now());
			return existing;
		}
		EvaluationScore created = new EvaluationScore(
				studentUsername, paramID, staffUsername, scoreValue, maxScore);
		scores.add(created);
		return created;
	}


	/*****
	 * <p> Method: boolean hasCompleteScores(String studentUsername,
	 *  EvaluationParameterList paramList) </p>
	 *
	 * <p> Description: This method returns true if the specified student has a saved
	 *  EvaluationScore for every parameter currently in paramList. Used by
	 *  computeOverallGrade() to decide whether an overall grade can be computed yet,
	 *  and can be called directly by the GUI to show scoring progress (e.g. "3 of 5
	 *  parameters scored"). </p>
	 *
	 * @param studentUsername specifies the student to check
	 *
	 * @param paramList specifies the current set of active EvaluationParameters
	 *
	 * @return true if every parameter in paramList has a saved score for this student,
	 *  false otherwise (including when paramList is empty, since there is nothing to
	 *  grade against yet)
	 *
	 */
	public boolean hasCompleteScores(String studentUsername, EvaluationParameterList paramList) {
		List<EvaluationParameter> activeParams = paramList.getAllParameters();
		if (activeParams.isEmpty()) {
			return false;
		}
		for (EvaluationParameter p : activeParams) {
			if (getScoreForStudentAndParam(studentUsername, p.getParamID()) == null) {
				return false;
			}
		}
		return true;
	}


	/*****
	 * <p> Method: Double computeOverallGrade(String studentUsername,
	 *  EvaluationParameterList paramList) </p>
	 *
	 * <p> Description: This method computes a student's overall grade, implementing
	 *  Story 3 criterion 6: each saved score is converted to a percentage of its
	 *  parameter's maxScore, multiplied by that parameter's weight, the results are
	 *  summed, and the sum is divided by the total combined weight of all active
	 *  parameters. The result is expressed as a percentage (0-100). Returns null,
	 *  rather than a partial or misleading number, until every active parameter has
	 *  been scored for this student -- callers should check hasCompleteScores() (or
	 *  the null return) before displaying a grade. </p>
	 *
	 * @param studentUsername specifies the student whose overall grade is computed
	 *
	 * @param paramList specifies the current set of active EvaluationParameters,
	 *  supplying each parameter's maxScore and weight
	 *
	 * @return the student's overall grade as a percentage (0-100), or null if
	 *  paramList is empty or the student has not yet been scored on every active
	 *  parameter
	 *
	 * @see tests.EvaluateStudentDiscussionTest#testOverallGradeSingleParameter()
	 * @see tests.EvaluateStudentDiscussionTest#testOverallGradeMultipleWeightedParameters()
	 * @see tests.EvaluateStudentDiscussionTest#testOverallGradeNullWhenIncomplete()
	 * @see tests.EvaluateStudentDiscussionTest#testOverallGradeNullWhenNoParameters()
	 *
	 */
	public Double computeOverallGrade(String studentUsername, EvaluationParameterList paramList) {
		List<EvaluationParameter> activeParams = paramList.getAllParameters();
		if (activeParams.isEmpty() || !hasCompleteScores(studentUsername, paramList)) {
			return null;
		}

		double weightedSum = 0.0;
		double totalWeight = 0.0;
		for (EvaluationParameter p : activeParams) {
			EvaluationScore s = getScoreForStudentAndParam(studentUsername, p.getParamID());
			double percentage = s.getScoreValue() / p.getMaxScore();
			weightedSum += percentage * p.getWeight();
			totalWeight += p.getWeight();
		}
		return (weightedSum / totalWeight) * 100.0;
	}
}