package entityClasses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*******
 * <p> Title: StudentActivitySummary Class </p>
 *
 * <p> Description: This StudentActivitySummary class is a read-only data holder for the
 *  discussion-activity snapshot Story 3 shows staff before they score a student: total
 *  posts, total replies, and how many distinct classmates the student has replied to.
 *  The distinct-peer count uses the same dedup and self-reply rules as Story 5's
 *  answer-coverage computation (a reply to the same peer more than once still counts
 *  as one distinct peer; replying to your own post never counts), so a student's
 *  coverage number is consistent whether they are looking at the Coverage Report or
 *  the Evaluate Student Discussion screen. If Story 5's computeCoverage() is
 *  accessible from this package, prefer calling it directly instead of
 *  buildSummary() below, so the two screens share one implementation rather than two
 *  that happen to agree. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 */

public class StudentActivitySummary {
	/*
	 * These are the private attributes for this entity object
	 */
	private String studentUsername;
	private int    totalPosts;
	private int    totalReplies;
	private int    distinctPeersRepliedTo;

	/*****
	 * <p> Method: StudentActivitySummary(String studentUsername, int totalPosts,
	 *  int totalReplies, int distinctPeersRepliedTo) </p>
	 *
	 * <p> Description: This constructor establishes a StudentActivitySummary. It is
	 *  package-visible construction via buildSummary() below; the fields are already
	 *  computed by the time this constructor runs, so no validation beyond
	 *  non-negativity is needed here. </p>
	 *
	 * @param studentUsername specifies which student this summary describes
	 *
	 * @param totalPosts specifies the student's total post count
	 *
	 * @param totalReplies specifies the student's total reply count
	 *
	 * @param distinctPeersRepliedTo specifies the number of distinct classmates whose
	 *  posts this student has replied to
	 *
	 */
	public StudentActivitySummary(String studentUsername, int totalPosts, int totalReplies,
			int distinctPeersRepliedTo) {
		this.studentUsername = studentUsername;
		this.totalPosts = totalPosts;
		this.totalReplies = totalReplies;
		this.distinctPeersRepliedTo = distinctPeersRepliedTo;
	}


	/*****
	 * <p> Method: StudentActivitySummary buildSummary(String studentUsername,
	 *  PostList postList, ReplyList replyList) </p>
	 *
	 * <p> Description: This static factory method computes a StudentActivitySummary
	 *  for one student directly from the existing PostList and ReplyList entity
	 *  collections, with no new database queries. It counts the student's posts,
	 *  counts the student's replies, and separately builds a deduplicated set of the
	 *  distinct authors whose posts the student replied to, excluding the student's
	 *  own posts. This mirrors Story 5's per-student coverage rules exactly:
	 *  duplicate replies to the same peer collapse to one entry in the set, and a
	 *  reply to your own post is skipped entirely. </p>
	 *
	 * @param studentUsername specifies the student to summarize
	 *
	 * @param postList specifies the full PostList to search for this student's posts
	 *
	 * @param replyList specifies the full ReplyList to search for this student's
	 *  replies and to resolve each reply's parent post author
	 *
	 * @return a StudentActivitySummary with totalPosts, totalReplies, and
	 *  distinctPeersRepliedTo populated for the specified student
	 *
	 * @see tests.EvaluateStudentDiscussionTest#testSummaryCountsPostsAndReplies()
	 * @see tests.EvaluateStudentDiscussionTest#testSummaryDedupsRepliesToSamePeer()
	 * @see tests.EvaluateStudentDiscussionTest#testSummaryExcludesSelfReplies()
	 * @see tests.EvaluateStudentDiscussionTest#testSummaryZeroActivityStudent()
	 *
	 */
	public static StudentActivitySummary buildSummary(String studentUsername,
			PostList postList, ReplyList replyList) {
		int totalPosts = postList.getPostsByAuthor(studentUsername).size();

		int totalReplies = 0;
		Set<String> distinctPeers = new HashSet<String>();

		List<Reply> allReplies = replyList.getAllReplies();
		for (Reply r : allReplies) {
			if (!r.getAuthorUsername().equals(studentUsername)) {
				continue;
			}
			totalReplies++;

			Post parentPost = postList.getPostByID(r.getPostID());
			if (parentPost == null) {
				continue; // parent post not found; nothing to attribute this reply to
			}
			String peerUsername = parentPost.getAuthorUsername();
			if (!peerUsername.equals(studentUsername)) {
				distinctPeers.add(peerUsername);
			}
			// replies to one's own post are intentionally excluded from distinctPeers
		}

		return new StudentActivitySummary(
				studentUsername, totalPosts, totalReplies, distinctPeers.size());
	}


	/*****
	 * <p> Method: String getStudentUsername() </p>
	 *
	 * <p> Description: This getter returns the StudentUsername this summary
	 *  describes. </p>
	 *
	 * @return a String of the student's username
	 *
	 */
	public String getStudentUsername() { return studentUsername; }


	/*****
	 * <p> Method: int getTotalPosts() </p>
	 *
	 * <p> Description: This getter returns the student's TotalPosts count. </p>
	 *
	 * @return an int of the total number of posts this student has authored
	 *
	 */
	public int getTotalPosts() { return totalPosts; }


	/*****
	 * <p> Method: int getTotalReplies() </p>
	 *
	 * <p> Description: This getter returns the student's TotalReplies count. </p>
	 *
	 * @return an int of the total number of replies this student has authored
	 *
	 */
	public int getTotalReplies() { return totalReplies; }


	/*****
	 * <p> Method: int getDistinctPeersRepliedTo() </p>
	 *
	 * <p> Description: This getter returns the number of distinct classmates whose
	 *  posts this student has replied to at least once. Multiple replies to the same
	 *  peer are deduplicated; replies to the student's own posts are excluded. </p>
	 *
	 * @return an int of the number of distinct peers this student has replied to
	 *
	 */
	public int getDistinctPeersRepliedTo() { return distinctPeersRepliedTo; }
}