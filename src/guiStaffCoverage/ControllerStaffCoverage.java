package guiStaffCoverage;

import entityClasses.Post;
import entityClasses.Reply;
import javafx.stage.Stage;
import entityClasses.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*******
 * <p> Title: ControllerStaffCoverage Class. </p>
 *
 * <p> Description: Controller for the Staff Answer-Coverage Report screen.
 * Contains the core coverage computation logic (computeCoverage) and the
 * threshold check (meetsThreshold). These methods are also called directly
 * by CoverageReportTests to validate correctness without launching JavaFX.
 * Satisfies Story 5: Answer-Coverage Computation. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-19  Initial version
 */
public class ControllerStaffCoverage {

    // The minimum number of distinct peers a student must reply to.
    // Hardcoded at 3 per Story 5 AC5.2. A future iteration may make
    // this configurable via EvaluationParameter.
    public static final int PEER_THRESHOLD = 3;

    public ControllerStaffCoverage() {}

    /**********
     * <p> Method: performStaffCoverage(Stage ps, User user) </p>
     *
     * <p> Description: Called when staff navigates to this screen from
     * Staff Home. Displays the corresponding View. </p>
     *
     * @param ps   the JavaFX Stage
     * @param user the currently logged-in user
     */
    public static void performStaffCoverage(Stage ps, User user) {
        ViewStaffCoverage.displayStaffCoverage(ps, user);
    }

    /**********
     * <p> Method: performReturn() </p>
     *
     * <p> Description: Returns the user to the Staff Home page. </p>
     */
    protected static void performReturn() {
        guiStaffHome.ViewStaffHome.displayStaffHome(
            ViewStaffCoverage.theStage, ViewStaffCoverage.theUser);
    }

    /**********
     * <p> Method: performQuit() </p>
     *
     * <p> Description: Terminates the application. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }

    /**********
     * <p> Method: Map&lt;String, Integer&gt; computeCoverage(
     *     List&lt;Reply&gt; replies, List&lt;Post&gt; posts) </p>
     *
     * <p> Description: Computes the deduplicated peer-reply count for every
     * student who has made at least one reply. The filter applies in order:
     * (1) Skip replies where the target post is not found — guards against
     *     orphaned reply records.
     * (2) Skip self-replies — a student replying to their own post must not
     *     count toward their peer threshold (AC5.4).
     * (3) Add the post author to a per-student Set of distinct peers. The Set
     *     automatically deduplicates (AC5.3).
     * Extracted into its own method so CoverageReportTests can validate it
     * without launching JavaFX or connecting to a live database. </p>
     *
     * @param replies the full list of Reply objects from Database.getReplyObjects()
     * @param posts   the full list of Post objects from Database.getPostObjects()
     * @return a Map from student username to deduplicated peer count. Never null.
     */
    public static Map<String, Integer> computeCoverage(
            List<Reply> replies, List<Post> posts) {

        // Initialized before null checks so we always return non-null
        Map<String, Integer> coverageMap = new HashMap<>();

        // Guard: empty input means no one has replied to anything
        if (replies == null || replies.isEmpty() || posts == null || posts.isEmpty()) {
            return coverageMap;
        }

        // Build a postID -> Post lookup for O(1) author resolution per reply.
        // Avoids scanning the full posts list for every reply (O(R*P) -> O(R+P))
        Map<Integer, Post> postLookup = new HashMap<>();
        for (Post post : posts) {
            postLookup.put(post.getPostID(), post);
        }

        // Per-student Set of distinct peer authors already counted.
        // A Set guarantees each peer is counted only once (AC5.3)
        Map<String, Set<String>> peerSets = new HashMap<>();

        for (Reply reply : replies) {
            String replyAuthor = reply.getAuthorUsername();

            // (1) Skip if the target post is not in the lookup map
            Post targetPost = postLookup.get(reply.getPostID());
            if (targetPost == null) continue;

            String postAuthor = targetPost.getAuthorUsername();

            // (2) Skip self-replies — students cannot count replies to their
            //     own posts toward their peer threshold (AC5.4)
            if (replyAuthor.equals(postAuthor)) continue;

            // (3) Add the post author to this student's distinct peer Set.
            //     putIfAbsent initializes a new HashSet on first encounter
            peerSets.putIfAbsent(replyAuthor, new HashSet<>());
            peerSets.get(replyAuthor).add(postAuthor);
        }

        // Convert each peer Set to a plain integer count for the caller
        for (Map.Entry<String, Set<String>> entry : peerSets.entrySet()) {
            coverageMap.put(entry.getKey(), entry.getValue().size());
        }

        return coverageMap;
    }

    /**********
     * <p> Method: boolean meetsThreshold(int peerCount) </p>
     *
     * <p> Description: Returns true if peerCount meets or exceeds
     * PEER_THRESHOLD. Extracted into its own method so the threshold
     * comparison is defined in exactly one place. </p>
     *
     * @param peerCount the deduplicated peer count for a student
     * @return true if peerCount >= PEER_THRESHOLD
     */
    public static boolean meetsThreshold(int peerCount) {
        // >= so exactly 3 passes rather than being incorrectly flagged (AC5.2)
        return peerCount >= PEER_THRESHOLD;
    }
}