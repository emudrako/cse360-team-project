package recognizers;

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
}