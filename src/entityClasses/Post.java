package entityClasses;

import java.time.LocalDateTime;

/**
 * <p><b>Title:</b> Post Class</p>
 *
 * <p><b>Description:</b> The {@code Post} class represents a single student discussion
 * post in the Student Discussion System.  Each post carries a title, a body, an author,
 * a thread assignment, a creation timestamp, a soft-delete flag, and a set of staff
 * oversight fields.  Together these attributes and their accessors enable all Student
 * User Stories and lay the structural groundwork for the Staff Epics that involve post
 * review, thread management, and evaluation.</p>
 *
 * <p>This class is a pure data holder.  All database persistence ({@code createPost},
 * {@code readPost}, {@code updatePost}, {@code deletePost}) lives in
 * {@code database.Database}.  All input validation lives in
 * {@code recognizers.PostReplyValidator}.  This separation of concerns keeps each class
 * focused on a single responsibility and makes unit testing straightforward.</p>
 *
 * <hr>
 * <p><b>Student capabilities supported by this class:</b></p>
 * <ul>
 *   <li><b>Creating a post:</b> The four-argument constructor accepts {@code title},
 *       {@code body}, {@code authorUsername}, and {@code thread}, assembling the entity
 *       that {@code Database.createPost()} then persists.  {@code isDeleted} is
 *       initialized to {@code false} here because a brand-new post is never deleted.</li>
 *   <li><b>Browsing and discovering others' posts:</b> {@code title}, {@code body},
 *       {@code authorUsername}, and {@code thread} supply all the content a student
 *       needs to skim a list of posts and decide which ones to open, including posts
 *       that might answer a question they were about to ask.</li>
 *   <li><b>Viewing a student's own posts with reply and unread counts:</b> {@code postID}
 *       and {@code authorUsername} are the join keys used by
 *       {@code Database.readPostsByAuthor()}, {@code Database.getReplyCount()}, and
 *       {@code Database.getUnreadReplyCount()} to build the per-student summary list.
 *       {@code createdAt} enables chronological ordering of the list.</li>
 *   <li><b>Keyword search across posts:</b> {@code title} and {@code body} are the two
 *       fields that {@code PostList.getPostsByKeyword()} searches; the implementation
 *       uses a set to avoid returning the same post twice when the keyword appears in
 *       both fields.  The {@code thread} field enables optional per-thread scoping
 *       when the student specifies a thread, or all threads are searched when none is
 *       specified.</li>
 *   <li><b>Thread assignment with "General" as the default:</b> The {@code thread} field
 *       stores the thread name chosen by the student.  When no thread is selected, the
 *       GUI layer supplies {@code "General"}, satisfying the default-thread requirement
 *       without any logic in this class.</li>
 *   <li><b>Deleting a post (soft-delete):</b> {@code isDeleted} implements the
 *       soft-delete pattern: the database row is retained so existing replies can still
 *       display "the original post has been deleted" rather than encountering a broken
 *       reference.  List and search views filter out posts where
 *       {@code isDeleted == true}.  Students receive an "Are you sure?" confirmation in
 *       the GUI before the flag is set.</li>
 *   <li><b>Thread management is reserved for staff:</b> Students interact with
 *       {@code thread} only as a read-only label on a post.  Creating, editing, and
 *       deleting threads is enforced at the GUI and database layers to be a staff-only
 *       operation.</li>
 * </ul>
 *
 * <hr>
 * <p><b>Staff Oversight Fields (Staff Epic — post review and private feedback):</b>
 * Although the staff epics are not required for TP2, the Post entity is intentionally
 * designed to support them so no schema migration is needed when they are implemented
 * in TP3:</p>
 * <ul>
 *   <li>{@code isFlagged} and {@code flaggedBy} — support flagging inappropriate
 *       content so staff can assess and manage interactions among students and maintain
 *       a positive atmosphere.  {@code Database.readFlaggedPosts()} surfaces these to
 *       the staff dashboard.</li>
 *   <li>{@code staffNote} — stores a private per-post annotation visible only to staff,
 *       enabling the private-feedback capability described in the staff review epic.
 *       Keeping it on the entity avoids an extra database join at read time.</li>
 *   <li>{@code isResolved} and {@code resolvedBy} — close the moderation loop so staff
 *       can track which flagged posts have been addressed and by whom, providing a full
 *       audit trail.</li>
 *   <li>{@code thread} also supports the thread-management staff epic: when a thread is
 *       deleted, {@code Database.deleteThread()} reassigns its posts to {@code "General"}
 *       by updating this field on every affected post.</li>
 * </ul>
 *
 * <p><b>Copyright:</b> Maranda Martinez © 2026</p>
 *
 * @author Maranda Martinez
 *
 * @version 2.00  2026-06-27  Added createdAt, staff oversight fields (isFlagged,
 *                             flaggedBy, staffNote, isResolved, resolvedBy), fixed
 *                             self-assignment bugs in setTitle / setBody / setThread,
 *                             and added comprehensive Javadoc citing user stories.
 * @version 1.00  2026-05-12  Initial version — postID, title, body, authorUsername,
 *                             thread, isDeleted.
 *
 * @see entityClasses.PostList
 * @see database.Database
 * @see recognizers.PostReplyValidator
 */
public class Post {

    // =========================================================================
    // CORE IDENTITY AND CONTENT FIELDS
    // =========================================================================

    /**
     * Database-generated primary key that uniquely identifies this post.
     *
     * <p>Remains {@code 0} until {@code Database.createPost()} persists the row and
     * the auto-incremented ID is written back via {@code setPostID()}.  All subsequent
     * CRUD calls, reply associations, and unread-tracking records reference this value
     * as the join key.</p>
     */
    private int postID;

    /**
     * Short descriptive title of this post; maximum 100 characters.
     *
     * <p>Required to create a post and displayed in post listing views so students
     * can quickly identify relevant content.  One of two fields searched by
     * {@code PostList.getPostsByKeyword()} during keyword searches.  Validated as
     * non-empty and at most 100 characters by
     * {@code PostReplyValidator.checkForValidTitle()} before any write operation.</p>
     */
    private String title;

    /**
     * Full body content of this post; maximum 1000 characters.
     *
     * <p>Required to create a post and rendered when a student opens a post.  Second
     * of two fields searched by {@code PostList.getPostsByKeyword()} during keyword
     * searches.  Validated as non-empty and at most 1000 characters by
     * {@code PostReplyValidator.checkForValidBody()} before any write operation.</p>
     */
    private String body;

    /**
     * Username of the student who created this post.
     *
     * <p>Links the post to a {@code User} record without embedding the full user object,
     * avoiding circular dependencies.  Used by {@code PostList.getPostsByAuthor()} to
     * retrieve a student's own posts for the personal post summary view, and by the GUI
     * layer to enforce that only the author may edit or delete a post.</p>
     */
    private String authorUsername;

    /**
     * Name of the discussion thread this post belongs to (e.g., {@code "General"}).
     *
     * <p>When a student does not choose a thread, the GUI supplies {@code "General"} so
     * that every post is always assigned to a thread.  Keyword searches that specify a
     * thread filter by this field; searches with no thread specified examine all threads.
     * The thread-management staff epic relies on this field: when a thread is deleted,
     * {@code Database.deleteThread()} updates this value to {@code "General"} for every
     * post in the deleted thread, so no posts are lost.</p>
     */
    private String thread;

    // =========================================================================
    // LIFECYCLE FIELDS
    // =========================================================================

    /**
     * Timestamp recording the exact moment this post was created.
     *
     * <p>Remains {@code null} until {@code Database.createPost()} sets it using the
     * database server time, making the database the single authoritative source of
     * truth for timestamps.  Persisted to and restored from the {@code PostsDB} table
     * so that post lists can be sorted chronologically.  Also serves as an audit-trail
     * datum useful for staff review.</p>
     */
    private LocalDateTime createdAt;

    /**
     * Soft-delete flag: {@code true} once the author has deleted this post.
     *
     * <p>The user story requires: "When I delete a post, any replies to that post are
     * not deleted, but anyone viewing the reply will see a message saying that the
     * original post has been deleted."  Physical deletion would break existing reply
     * records; the soft-delete pattern keeps the row in place so reply views can detect
     * the deletion and show the required placeholder message.  Normal list and search
     * views apply a {@code WHERE isDeleted = FALSE} filter so deleted posts do not
     * appear to other students.</p>
     *
     * <p>Initialized to {@code false} in the constructor — a new post is never
     * pre-deleted.</p>
     */
    private boolean isDeleted;

    // =========================================================================
    // STAFF OVERSIGHT FIELDS  (Staff Epic — post review and private feedback)
    // Included in TP2 to avoid a schema migration when the staff epics are
    // implemented in TP3.  None of these fields is exposed in student-facing views.
    // =========================================================================

    /**
     * Staff oversight flag: {@code true} if a staff member has flagged this post as
     * requiring attention (e.g., inappropriate language, policy violation).
     *
     * <p>The staff review epic states: "As a staff member, I can review students' posts
     * and replies, and I can provide private feedback to students and other staff, so I
     * can assess, coach, mentor, and manage interactions among students and staff to
     * maintain a positive atmosphere and deal with inappropriate posts."  Flagged posts
     * are surfaced in the staff review dashboard via
     * {@code Database.readFlaggedPosts()}.</p>
     */
    private boolean isFlagged;

    /**
     * Username of the staff member who most recently flagged this post.
     *
     * <p>Provides an audit trail for the moderation workflow described in the staff
     * review epic.  Recording who raised the flag allows a supervisor to follow up with
     * the flagging staff member if clarification is needed, and prevents duplicate flags
     * from being attributed to the wrong person.</p>
     */
    private String flaggedBy;
    
    private LocalDateTime flaggedAt;

    /**
     * Private staff annotation for this post, not visible to students.
     *
     * <p>Supports the private-feedback capability in the staff review epic.  Storing
     * the note directly on the post entity avoids an extra database join when rendering
     * the staff review screen.  The GUI layer is responsible for ensuring this field is
     * never shown to student users.</p>
     */
    private String staffNote;   // private staff annotation, not visible to students

    /**
     * Resolution flag: {@code true} once a staff member has reviewed the flagged post
     * and determined that no further action is required.
     *
     * <p>Closes the moderation loop for the staff review epic.  A post transitions from
     * {@code isFlagged=true, isResolved=false} (pending review) to
     * {@code isResolved=true} (moderation complete) when staff invoke
     * {@code Database.resolvePost()}.  The staff dashboard filters on this flag to
     * show only posts that still need attention.</p>
     */
    private boolean isResolved;

    /**
     * Username of the staff member who resolved the moderation action on this post.
     *
     * <p>Pairs with {@code isResolved} to complete the audit trail for the staff review
     * epic: {@code flaggedBy} records who raised the concern and {@code resolvedBy}
     * records who closed it.  Both fields together allow team leads to review moderation
     * decisions after the fact.</p>
     */
    private String resolvedBy;
    
    private LocalDateTime resolvedAt;
    
    private boolean isReviewed;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;


    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /*****
     * <p> Method: Post() </p>
     *
     * <p> Description: No-argument default constructor.  Required by some reflection-
     * based frameworks and retained for compatibility, but not used by application
     * code — all posts are created via the four-argument constructor. </p>
     */
    public Post() {
    }

    /*****
     * <p> Method: Post(String title, String body, String authorUsername, String thread) </p>
     *
     * <p> Description: Primary constructor used by all application code to create a new
     * Post entity before persisting it with {@code Database.createPost()}.  Covers the
     * student capability to post statements and questions to a chosen thread, with
     * {@code "General"} as the default when no thread is selected. </p>
     *
     * <p> {@code isDeleted} is explicitly set to {@code false} here because a brand-new
     * post is never in a deleted state; this makes the invariant visible in the code
     * rather than relying on Java's default boolean initialization. </p>
     *
     * <p> Input validation (non-empty fields, length limits, valid thread name) is the
     * caller's responsibility via {@code PostReplyValidator} before invoking this
     * constructor. </p>
     *
     * @param title          the short descriptive title of the post
     * @param body           the full content of the post
     * @param authorUsername the username of the student creating the post
     * @param thread         the thread this post is assigned to; {@code "General"} when
     *                       no thread is selected by the student
     */
    public Post(String title, String body, String authorUsername, String thread) {
    	this.title = title;
        this.body = body;
        this.authorUsername = authorUsername;
        this.isDeleted = false; // a new post is never deleted; explicit to document the invariant
        if (thread.isBlank()) {
        	this.thread = "General";
        }
        else {
        	this.thread = thread;
        }
    }


    // =========================================================================
    // CORE IDENTITY AND CONTENT — GETTERS AND SETTERS
    // =========================================================================

    /*****
     * <p> Method: int getPostID() </p>
     *
     * <p> Description: Returns the database-assigned primary key of this post.
     * The value is {@code 0} before the post is persisted.  After
     * {@code Database.createPost()} returns, the ID is populated via
     * {@code setPostID()} and is used as the join key for reply queries, unread-count
     * queries, and soft-delete operations. </p>
     *
     * @return the integer primary key assigned by the database, or {@code 0} if not
     *         yet persisted
     */
    public int getPostID() { return postID; }

    /*****
     * <p> Method: void setPostID(int id) </p>
     *
     * <p> Description: Sets the database-generated primary key.  Called exclusively
     * by {@code Database.createPost()} after the INSERT statement executes and the
     * auto-incremented ID becomes available.  Not intended to be called by any other
     * code path. </p>
     *
     * @param id the auto-incremented integer primary key assigned by the database
     */
    public void setPostID(int id) { postID = id; }

    /*****
     * <p> Method: String getTitle() </p>
     *
     * <p> Description: Returns the title of this post.  Used by list views to display
     * a summary row so students can quickly identify relevant content, and by
     * {@code PostList.getPostsByKeyword()} when performing keyword searches. </p>
     *
     * @return the title string, at most 100 characters
     */
    public String getTitle() { return title; }

    /*****
     * <p> Method: void setTitle(String title) </p>
     *
     * <p> Description: Updates the title of this post.  Called by
     * {@code Database.updatePost()} after the caller has validated the new title
     * through {@code PostReplyValidator.checkForValidTitle()}. </p>
     *
     * <p> Note: uses {@code this.title} to avoid the self-assignment bug that would
     * result from writing {@code title = title} (the parameter shadowing the field). </p>
     *
     * @param title the new title; must be non-empty and at most 100 characters
     */
    public void setTitle(String title) { this.title = title; }

    /*****
     * <p> Method: String getBody() </p>
     *
     * <p> Description: Returns the full body content of this post.  Rendered when a
     * student opens a post, and searched alongside {@code title} by
     * {@code PostList.getPostsByKeyword()} during keyword searches. </p>
     *
     * @return the body string, at most 1000 characters
     */
    public String getBody() { return body; }

    /*****
     * <p> Method: void setBody(String body) </p>
     *
     * <p> Description: Updates the body content of this post.  Called by
     * {@code Database.updatePost()} after the caller has validated the new body
     * through {@code PostReplyValidator.checkForValidBody()}. </p>
     *
     * <p> Note: uses {@code this.body} to avoid the self-assignment bug that would
     * result from writing {@code body = body}. </p>
     *
     * @param body the new body content; must be non-empty and at most 1000 characters
     */
    public void setBody(String body) { this.body = body; }

    /*****
     * <p> Method: String getAuthorUsername() </p>
     *
     * <p> Description: Returns the username of the student who authored this post.
     * Used by {@code PostList.getPostsByAuthor()} to retrieve the per-student post list
     * and by the GUI to enforce that only the author may edit or delete the post. </p>
     *
     * @return the author's username string
     */
    public String getAuthorUsername() { return authorUsername; }

    /*****
     * <p> Method: void setAuthorUsername(String username) </p>
     *
     * <p> Description: Updates the author username.  In normal usage the author does
     * not change after creation; this setter exists to allow {@code Database.readPost()}
     * to reconstruct a {@code Post} object from a result set. </p>
     *
     * @param username the username of the student who authored this post
     */
    public void setAuthorUsername(String username) { authorUsername = username; }

    /*****
     * <p> Method: String getThread() </p>
     *
     * <p> Description: Returns the name of the discussion thread this post belongs to.
     * Used by keyword-search views when the student scopes a search to a specific thread,
     * and by {@code Database.deleteThread()} to identify posts that need to be reassigned
     * to {@code "General"} when a thread is deleted. </p>
     *
     * @return the thread name string, e.g., {@code "General"}
     */
    public String getThread() { return thread; }

    /*****
     * <p> Method: void setThread(String thread) </p>
     *
     * <p> Description: Updates the thread assignment of this post.  In normal student
     * usage a post's thread does not change after creation; this setter supports the
     * thread-management staff epic where {@code Database.deleteThread()} reassigns all
     * posts in a deleted thread to {@code "General"}. </p>
     *
     * <p> Note: uses {@code this.thread} to avoid the self-assignment bug that would
     * result from writing {@code thread = thread}. </p>
     *
     * @param thread the new thread name; {@code "General"} is the system-wide default
     */
    public void setThread(String thread) { this.thread = thread; }


    // =========================================================================
    // LIFECYCLE — GETTERS AND SETTERS
    // =========================================================================

    /*****
     * <p> Method: LocalDateTime getCreatedAt() </p>
     *
     * <p> Description: Returns the timestamp when this post was created, or
     * {@code null} before {@code Database.createPost()} is called.  Used to sort post
     * lists chronologically and as part of the audit trail visible to staff during post
     * review. </p>
     *
     * @return the creation timestamp, or {@code null} if the post has not yet been
     *         persisted
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /*****
     * <p> Method: void setCreatedAt(LocalDateTime time) </p>
     *
     * <p> Description: Sets the creation timestamp.  Called by
     * {@code Database.createPost()} immediately after the INSERT executes, using the
     * time returned by the database rather than by the application JVM to ensure
     * consistency across distributed instances. </p>
     *
     * @param time the creation timestamp from the database
     */
    public void setCreatedAt(LocalDateTime time) { createdAt = time; }

    /*****
     * <p> Method: boolean getIsDeleted() </p>
     *
     * <p> Description: Returns {@code true} if this post has been soft-deleted by its
     * author.  Student-facing list and search views check this flag and exclude deleted
     * posts from results.  Reply views check this flag to decide whether to show the
     * post body or the placeholder message "the original post has been deleted." </p>
     *
     * @return {@code true} if the post is soft-deleted, {@code false} otherwise
     */
    public boolean getIsDeleted() { return isDeleted; }

    /*****
     * <p> Method: void setIsDeleted(boolean deleted) </p>
     *
     * <p> Description: Marks this post as soft-deleted.  Called by
     * {@code Database.deletePost()} rather than issuing a SQL DELETE, so reply records
     * remain intact and can display the required placeholder message.  Setting this to
     * {@code false} is intentionally not exposed in the student GUI — post restoration
     * is a staff function. </p>
     *
     * @param deleted {@code true} to soft-delete this post, {@code false} to restore it
     */
    public void setIsDeleted(boolean deleted) { isDeleted = deleted; }


    // =========================================================================
    // STAFF OVERSIGHT — GETTERS AND SETTERS
    // =========================================================================

    /*****
     * <p> Method: boolean getIsFlagged() </p>
     *
     * <p> Description: Returns {@code true} if a staff member has flagged this post
     * for review.  The staff dashboard calls {@code Database.readFlaggedPosts()} which
     * filters on this value to present only posts needing attention. </p>
     *
     * @return {@code true} if this post has been flagged by staff, {@code false}
     *         otherwise
     */
    public boolean getIsFlagged() { return isFlagged; }

    /*****
     * <p> Method: void setIsFlagged(boolean isFlagged) </p>
     *
     * <p> Description: Sets the staff-flag on this post.  Called by
     * {@code Database.flagPost()} after a staff member chooses to flag the post for
     * review.  Uses {@code this.isFlagged} to avoid self-assignment because the
     * parameter name matches the field name. </p>
     *
     * @param isFlagged {@code true} to flag this post, {@code false} to un-flag it
     */
    public void setIsFlagged(boolean isFlagged) { this.isFlagged = isFlagged; }

    /*****
     * <p> Method: String getFlaggedBy() </p>
     *
     * <p> Description: Returns the username of the staff member who flagged this post.
     * Displayed in the staff review dashboard to identify who raised the concern so
     * supervisors can follow up if needed. </p>
     *
     * @return the username of the flagging staff member, or {@code null} if unflagged
     */
    public String getFlaggedBy() { return flaggedBy; }

    /*****
     * <p> Method: void setFlaggedBy(String username) </p>
     *
     * <p> Description: Records which staff member flagged this post.  Called by
     * {@code Database.flagPost()} at the same time as {@code setIsFlagged(true)}. </p>
     *
     * @param username the username of the staff member performing the flag action
     */
    public void setFlaggedBy(String username) { flaggedBy = username; }

    /*****
     * <p> Method: String getStaffNote() </p>
     *
     * <p> Description: Returns the private staff annotation for this post.  This note
     * is intentionally never exposed to the post author or other students; the GUI layer
     * is responsible for omitting it from student-facing views. </p>
     *
     * @return the staff note string, or {@code null} if none has been written
     */
    public String getStaffNote() { return staffNote; }

    /*****
     * <p> Method: void setStaffNote(String note) </p>
     *
     * <p> Description: Sets the private staff annotation on this post.  Called by
     * {@code Database.setPostStaffNote()} after a staff member submits feedback through
     * the staff review GUI.  May be called multiple times as staff refine their
     * feedback. </p>
     *
     * @param note the private annotation text
     */
    public void setStaffNote(String note) { staffNote = note; }

    /*****
     * <p> Method: boolean getIsResolved() </p>
     *
     * <p> Description: Returns {@code true} once a staff member has reviewed this
     * flagged post and closed the moderation action.  The staff dashboard uses this
     * flag to separate "needs review" from "resolved" post lists, reducing noise for
     * staff who have already handled a case. </p>
     *
     * @return {@code true} if this post's moderation action has been resolved,
     *         {@code false} if it is still pending
     */
    public boolean getIsResolved() { return isResolved; }

    /*****
     * <p> Method: void setIsResolved(boolean isResolved) </p>
     *
     * <p> Description: Closes the moderation action on this post.  Called by
     * {@code Database.resolvePost()} when staff confirm that no further action is
     * needed.  Uses {@code this.isResolved} to avoid self-assignment because the
     * parameter name matches the field name. </p>
     *
     * @param isResolved {@code true} to mark the moderation action as resolved
     */
    public void setIsResolved(boolean isResolved) { this.isResolved = isResolved; }

    /*****
     * <p> Method: String getResolvedBy() </p>
     *
     * <p> Description: Returns the username of the staff member who resolved the
     * moderation action on this post.  Together with {@code flaggedBy}, this provides
     * a complete audit trail: who raised the concern and who closed it. </p>
     *
     * @return the username of the resolving staff member, or {@code null} if not yet
     *         resolved
     */
    public String getResolvedBy() { return resolvedBy; }

    /*****
     * <p> Method: void setResolvedBy(String username) </p>
     *
     * <p> Description: Records which staff member resolved the moderation action on
     * this post.  Called by {@code Database.resolvePost()} at the same time as
     * {@code setIsResolved(true)}. </p>
     *
     * @param username the username of the staff member closing the moderation action
     */
    public void setResolvedBy(String username) { resolvedBy = username; }
    
    /*****
     * <p> Method: LocalDateTime getFlaggedAt() </p>
     *
     * <p> Description: Returns the timestamp when this post was most recently flagged. </p>
     *
     * @return the flag timestamp, or {@code null} if never flagged
     */
    public LocalDateTime getFlaggedAt() { return flaggedAt; }

    /*****
     * <p> Method: void setFlaggedAt(LocalDateTime time) </p>
     *
     * <p> Description: Records when this post was flagged. </p>
     *
     * @param time the timestamp of the flag action
     */
    public void setFlaggedAt(LocalDateTime time) { flaggedAt = time; }

    /*****
     * <p> Method: LocalDateTime getResolvedAt() </p>
     *
     * <p> Description: Returns the timestamp when this post's moderation action was
     * resolved. </p>
     *
     * @return the resolution timestamp, or {@code null} if not yet resolved
     */
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    /*****
     * <p> Method: void setResolvedAt(LocalDateTime time) </p>
     *
     * <p> Description: Records when this post's moderation action was resolved. </p>
     *
     * @param time the timestamp of the resolve action
     */
    public void setResolvedAt(LocalDateTime time) { resolvedAt = time; }

    /*****
     * <p> Method: boolean getIsReviewed() </p>
     *
     * <p> Description: Returns once a staff member has marked this post
     * reviewed during grading, so nothing gets missed. Separate from the flag/resolve
     * moderation workflow. </p>
     *
     * @return {@code true} if this post has been marked reviewed, {@code false} otherwise
     */
    public boolean getIsReviewed() { return isReviewed; }

    /*****
     * <p> Method: void setIsReviewed(boolean isReviewed) </p>
     *
     * <p> Description: Marks this post as reviewed. </p>
     *
     * @param isReviewed {@code true} to mark this post reviewed
     */
    public void setIsReviewed(boolean isReviewed) { this.isReviewed = isReviewed; }

    /*****
     * <p> Method: String getReviewedBy() </p>
     *
     * <p> Description: Returns the username of the staff member who marked this post
     * reviewed. </p>
     *
     * @return the username of the reviewing staff member, or {@code null} if not yet
     *         reviewed
     */
    public String getReviewedBy() { return reviewedBy; }

    /*****
     * <p> Method: void setReviewedBy(String username) </p>
     *
     * <p> Description: Records which staff member marked this post reviewed. </p>
     *
     * @param username the username of the staff member marking the post reviewed
     */
    public void setReviewedBy(String username) { reviewedBy = username; }

    /*****
     * <p> Method: LocalDateTime getReviewedAt() </p>
     *
     * <p> Description: Returns the timestamp when this post was marked reviewed. </p>
     *
     * @return the review timestamp, or {@code null} if not yet reviewed
     */
    public LocalDateTime getReviewedAt() { return reviewedAt; }

    /*****
     * <p> Method: void setReviewedAt(LocalDateTime time) </p>
     *
     * <p> Description: Records when this post was marked reviewed. </p>
     *
     * @param time the timestamp of the review action
     */
    public void setReviewedAt(LocalDateTime time) { reviewedAt = time; }
}
