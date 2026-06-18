package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: ReplyList Class </p>
 *
 * <p> Description: This ReplyList class supports storing all current Reply objects in the
 *  system, as well as retrieving any subset of those replies, such as the replies that
 *  respond to a specific post. </p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 *
 */

public class ReplyList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<Reply> replies;

	/*****
	 * <p> Method: ReplyList() </p>
	 *
	 * <p> Description: This constructor establishes an empty ReplyList, ready to have
	 * Reply objects added to it. </p>
	 *
	 */
	// Constructor to initialize an empty list of Replies.
	public ReplyList() {
		this.replies = new ArrayList<Reply>();
	}


	/*****
	 * <p> Method: void addReply(Reply reply) </p>
	 *
	 * <p> Description: This method adds a Reply object to the ReplyList. </p>
	 *
	 * @param reply specifies the Reply object to be added to the list
	 *
	 */
	// Adds a Reply to the list.
	public void addReply(Reply reply) {
		replies.add(reply);
	}


	/*****
	 * <p> Method: List<Reply> getAllReplies() </p>
	 *
	 * <p> Description: This getter returns the complete list of Reply objects. </p>
	 *
	 * @return a List of all Reply objects currently stored
	 *
	 */
	// Gets the complete list of Replies.
	public List<Reply> getAllReplies() {
		return replies;
	}


	/*****
	 * <p> Method: List<Reply> getRepliesByPostID(int postID) </p>
	 *
	 * <p> Description: This method returns the subset of Reply objects that respond
	 * to a specified post. </p>
	 *
	 * @param postID specifies the ID of the post whose replies should be returned
	 *
	 * @return a List of Reply objects responding to the specified postID
	 *
	 */
	// Gets the subset of Replies belonging to a specific post.
	public List<Reply> getRepliesByPostID(int postID) {
		List<Reply> result = new ArrayList<Reply>();
		for (Reply r : replies) {
			if (r.getPostID() == postID) {
				result.add(r);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: Reply getReplyByID(int replyID) </p>
	 *
	 * <p> Description: This method returns the single Reply object matching the
	 * specified replyID, or null if no such reply exists. </p>
	 *
	 * @param replyID specifies the ID of the reply to find
	 *
	 * @return the Reply object matching the specified replyID, or null if not found
	 *
	 */
	// Finds a specific Reply by its ID.
	public Reply getReplyByID(int replyID) {
		for (Reply r : replies) {
			if (r.getReplyID() == replyID) {
				return r;
			}
		}
		return null;
	}
}