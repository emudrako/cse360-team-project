package recognizers;

import entityClasses.ThreadList;

/*******
 * <p> Title: PostReplyValidator Class. </p>
 *
 * <p> Description: This class evaluates whether a post or reply satisfies the input
 * validation requirements for the Student Discussion System. It checks that the title
 * and body are not empty and do not exceed their maximum allowed lengths, and it checks
 * that thread management actions are restricted to staff members.</p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 * @version 1.00    2026-06-17 Initial version for CSE 360 HW2 Stories 10 and 12
 * @version 1.01    2026-06-27 Added checkForValidThread(ThreadList), checkForValidParameter,
 *                             checkForValidRequestDescription, checkForValidAdminNotes
 *
 */

public class PostReplyValidator {

	// Maximum lengths allowed for post and reply content
	public static final int MAX_TITLE_LENGTH = 100;
	public static final int MAX_BODY_LENGTH = 1000;

	public static String checkForValidPost(String title, String body) {
		// Check title empty
		if (title == null || title.trim().isEmpty())
			return "*** Error *** The title is empty!";

		// Check title max length
		if (title.length() > MAX_TITLE_LENGTH)
			return "*** Error *** Title is too long. Maximum is "
				+ MAX_TITLE_LENGTH + " characters.";

		// Check body empty
		if (body == null || body.trim().isEmpty())
			return "*** Error *** The body is empty!";

		// Check body max length
		if (body.length() > MAX_BODY_LENGTH)
			return "*** Error *** Body is too long. Maximum is "
				+ MAX_BODY_LENGTH + " characters.";

		return "";
	}

	public static String checkForValidReply(String body) {
		// Check body empty
		if (body == null || body.trim().isEmpty())
			return "*** Error *** The body is empty!";

		// Check body max length
		if (body.length() > MAX_BODY_LENGTH)
			return "*** Error *** Body is too long. Maximum is "
				+ MAX_BODY_LENGTH + " characters.";

		return "";
	}

	public static String checkThreadPermission(boolean isStaff) {
		// Students do not have authority to create, edit, or delete threads (Story 12)
		if (!isStaff)
			return "*** Error *** Only staff members can create, edit, or delete threads.";

		return "";
	}
	
	public static String checkForValidThread(String thread) {
	    if (thread == null || (!thread.equals("General") && !thread.equals("Homework") && !thread.equals("Quizzes")))
	        return "*** Error *** The specified thread does not exist.";

	    return "";
	}

	/*****
	 * <p> Method: checkForValidThread(String thread, ThreadList threadList) </p>
	 *
	 * <p> Description: Replaces the hardcoded-name version above. Checks whether the
	 *  specified thread name exists in the live ThreadList rather than a fixed set of
	 *  known names. </p>
	 *
	 * @param thread specifies the thread name to validate
	 *
	 * @param threadList specifies the current list of threads to look up against
	 *
	 * @return an empty string if the thread exists, or an error message otherwise
	 *
	 */
	public static String checkForValidThread(String thread, ThreadList threadList) {
		if (thread == null || thread.trim().isEmpty())
			return "*** Error *** The thread name is empty.";

		if (threadList == null || !threadList.threadExists(thread))
			return "*** Error *** The specified thread does not exist.";

		return "";
	}

	/*****
	 * <p> Method: checkForValidParameter(String name, double maxScore, double weight) </p>
	 *
	 * <p> Description: Validates the fields of an EvaluationParameter before a create or
	 *  update operation. Name must be non-empty; maxScore must be positive; weight must
	 *  be in the range 0.0-1.0 inclusive. </p>
	 *
	 * @param name specifies the parameter name to validate
	 *
	 * @param maxScore specifies the maximum score to validate
	 *
	 * @param weight specifies the weight to validate (must be 0.0-1.0)
	 *
	 * @return an empty string if all fields are valid, or an error message otherwise
	 *
	 */
	public static String checkForValidParameter(String name, double maxScore, double weight) {
		if (name == null || name.trim().isEmpty())
			return "*** Error *** The parameter name is empty.";

		if (maxScore <= 0)
			return "*** Error *** Max score must be greater than zero.";

		if (weight < 0.0 || weight > 1.0)
			return "*** Error *** Weight must be between 0.0 and 1.0 inclusive.";

		return "";
	}

	/*****
	 * <p> Method: checkForValidRequestDescription(String description) </p>
	 *
	 * <p> Description: Validates a request description before a create or update
	 *  operation. The description must be non-empty. </p>
	 *
	 * @param description specifies the request description to validate
	 *
	 * @return an empty string if the description is valid, or an error message otherwise
	 *
	 */
	public static String checkForValidRequestDescription(String description) {
		if (description == null || description.trim().isEmpty())
			return "*** Error *** The request description is empty.";

		return "";
	}

	/*****
	 * <p> Method: checkForValidAdminNotes(String notes) </p>
	 *
	 * <p> Description: Validates admin notes before closing a request. Notes must be
	 *  non-empty so that the admin documents what action was taken. </p>
	 *
	 * @param notes specifies the admin notes to validate
	 *
	 * @return an empty string if the notes are valid, or an error message otherwise
	 *
	 */
	public static String checkForValidAdminNotes(String notes) {
		if (notes == null || notes.trim().isEmpty())
			return "*** Error *** Admin notes must not be empty when closing a request.";

		return "";
	}
}