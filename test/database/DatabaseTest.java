package database;

import entityClasses.Post;
import entityClasses.Reply;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: DatabaseTest </p>
 *
 * <p> Description: Integration tests for the Database class. Covers read-tracking methods
 * (getReplyCount, markReplyAsRead, isReplyReadByUser, getUnreadReplyCount,
 * getUnreadRepliesForPost) and post/reply CRUD (updatePost, deletePost, updateReply,
 * deleteReply). Each test method gets a fresh private in-memory H2 database so tests
 * are fully isolated from one another and from the production file database. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00    2026-06-19 Initial version — read-tracking tests
 * @version 1.01    2026-06-27 Added updatePost, deletePost, updateReply, deleteReply tests
 *
 */
class DatabaseTest {

    static final String TITLE        = "Test Title";
    static final String BODY         = "Test body content";
    static final String THREAD       = "General";
    static final String AUTHOR_A     = "userA";
    static final String AUTHOR_B     = "userB";
    static final String REPLY_BODY_1 = "Reply one";
    static final String REPLY_BODY_2 = "Reply two";

    private Database db;

    @BeforeEach
    void setUp() throws SQLException {
        db = new Database();
        db.connectToTestDatabase();
    }

    @AfterEach
    void tearDown() {
        db.closeConnection();
    }

    // Story 10: count of replies to a post
    @Test
    void replyCountIsZeroForNewPost() throws SQLException {
        Post post = createPost();
        assertEquals(0, db.getReplyCount(post.getPostID()));
    }

    @Test
    void replyCountIsOneAfterOneReply() throws SQLException {
        Post post = createPost();
        createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        assertEquals(1, db.getReplyCount(post.getPostID()));
    }

    @Test
    void replyCountReflectsMultipleReplies() throws SQLException {
        Post post = createPost();
        createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getReplyCount(post.getPostID()));
    }

    @Test
    void replyCountIsIsolatedPerPost() throws SQLException {
        Post post1 = createPost();
        Post post2 = new Post(TITLE, BODY, AUTHOR_B, THREAD);
        db.createPost(post2);
        createReply(post2.getPostID(), REPLY_BODY_1, AUTHOR_A);
        createReply(post2.getPostID(), REPLY_BODY_2, AUTHOR_A);
        assertEquals(0, db.getReplyCount(post1.getPostID()));
        assertEquals(2, db.getReplyCount(post2.getPostID()));
    }

    // Story 11: unread replies count
    @Test
    void replyIsUnreadByDefault() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        assertFalse(db.isReplyReadByUser(reply.getReplyID(), AUTHOR_A));
    }

    @Test
    void replyIsReadAfterMarkingAsRead() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(reply.getReplyID(), AUTHOR_A);
        assertTrue(db.isReplyReadByUser(reply.getReplyID(), AUTHOR_A));
    }

    @Test
    void markingReplyReadIsIdempotent() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(reply.getReplyID(), AUTHOR_A);
        assertDoesNotThrow(() -> db.markReplyAsRead(reply.getReplyID(), AUTHOR_A));
        assertTrue(db.isReplyReadByUser(reply.getReplyID(), AUTHOR_A));
    }

    @Test
    void readStatusIsPerUser() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(reply.getReplyID(), AUTHOR_A);
        assertFalse(db.isReplyReadByUser(reply.getReplyID(), AUTHOR_B));
    }

    @Test
    void unreadCountEqualsReplyCountWhenNoneRead() throws SQLException {
        Post post = createPost();
        createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getUnreadReplyCount(post.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountDecreasesAfterMarkingOneRead() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(reply1.getReplyID(), AUTHOR_A);
        assertEquals(1, db.getUnreadReplyCount(post.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountIsZeroWhenAllRepliesRead() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        Reply reply2 = createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(reply1.getReplyID(), AUTHOR_A);
        db.markReplyAsRead(reply2.getReplyID(), AUTHOR_A);
        assertEquals(0, db.getUnreadReplyCount(post.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountIsPerUser() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(reply1.getReplyID(), AUTHOR_A);
        assertEquals(2, db.getUnreadReplyCount(post.getPostID(), AUTHOR_B));
    }

    @Test
    void unreadRepliesListIsEmptyWhenNoReplies() throws SQLException {
        Post post = createPost();
        assertTrue(db.getUnreadRepliesForPost(post.getPostID(), AUTHOR_A).isEmpty());
    }

    @Test
    void unreadRepliesListContainsAllRepliesInitially() throws SQLException {
        Post post = createPost();
        createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getUnreadRepliesForPost(post.getPostID(), AUTHOR_A).size());
    }

    @Test
    void unreadRepliesListExcludesReadReplies() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(reply1.getReplyID(), AUTHOR_A);
        List<Reply> unread = db.getUnreadRepliesForPost(post.getPostID(), AUTHOR_A);
        assertEquals(1, unread.size());
        assertEquals(REPLY_BODY_2, unread.get(0).getBody());
    }

    @Test
    void unreadRepliesListIsEmptyWhenAllRead() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        Reply reply2 = createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(reply1.getReplyID(), AUTHOR_A);
        db.markReplyAsRead(reply2.getReplyID(), AUTHOR_A);
        assertTrue(db.getUnreadRepliesForPost(post.getPostID(), AUTHOR_A).isEmpty());
    }

    // -------------------------------------------------------------------------
    // updatePost
    // -------------------------------------------------------------------------

    @Test
    void updatePostPersistsTitleAndBody() throws SQLException {
        Post post = createPost();
        db.updatePost(post.getPostID(), "New Title", "New body content");
        Post loaded = db.readPost(post.getPostID());
        assertEquals("New Title", loaded.getTitle());
        assertEquals("New body content", loaded.getBody());
    }

    @Test
    void updatePostThrowsOnEmptyTitle() throws SQLException {
        Post post = createPost();
        assertThrows(IllegalArgumentException.class,
            () -> db.updatePost(post.getPostID(), "", BODY));
    }

    @Test
    void updatePostThrowsOnEmptyBody() throws SQLException {
        Post post = createPost();
        assertThrows(IllegalArgumentException.class,
            () -> db.updatePost(post.getPostID(), TITLE, ""));
    }

    @Test
    void updatePostThrowsWhenTitleExceedsMaxLength() throws SQLException {
        Post post = createPost();
        String longTitle = "A".repeat(101);
        assertThrows(IllegalArgumentException.class,
            () -> db.updatePost(post.getPostID(), longTitle, BODY));
    }

    @Test
    void updatePostThrowsWhenBodyExceedsMaxLength() throws SQLException {
        Post post = createPost();
        String longBody = "A".repeat(1001);
        assertThrows(IllegalArgumentException.class,
            () -> db.updatePost(post.getPostID(), TITLE, longBody));
    }

    // -------------------------------------------------------------------------
    // deletePost
    // -------------------------------------------------------------------------

    @Test
    void deletePostSetsIsDeletedTrue() throws SQLException {
        Post post = createPost();
        db.deletePost(post.getPostID());
        Post loaded = db.readPost(post.getPostID());
        assertTrue(loaded.getIsDeleted());
    }

    @Test
    void deletePostKeepsRowInDatabase() throws SQLException {
        Post post = createPost();
        db.deletePost(post.getPostID());
        assertNotNull(db.readPost(post.getPostID()));
    }

    @Test
    void deletePostDoesNotAffectOtherPosts() throws SQLException {
        Post post1 = createPost();
        Post post2 = new Post(TITLE, BODY, AUTHOR_B, THREAD);
        db.createPost(post2);
        db.deletePost(post1.getPostID());
        assertFalse(db.readPost(post2.getPostID()).getIsDeleted());
    }

    // -------------------------------------------------------------------------
    // updateReply
    // -------------------------------------------------------------------------

    @Test
    void updateReplyPersistsNewBody() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.updateReply(reply.getReplyID(), "Updated reply body");
        List<Reply> replies = db.readRepliesForPost(post.getPostID());
        assertEquals("Updated reply body", replies.get(0).getBody());
    }

    @Test
    void updateReplyThrowsOnEmptyBody() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        assertThrows(IllegalArgumentException.class,
            () -> db.updateReply(reply.getReplyID(), ""));
    }

    @Test
    void updateReplyThrowsWhenBodyExceedsMaxLength() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        String longBody = "A".repeat(1001);
        assertThrows(IllegalArgumentException.class,
            () -> db.updateReply(reply.getReplyID(), longBody));
    }

    // -------------------------------------------------------------------------
    // deleteReply
    // -------------------------------------------------------------------------

    @Test
    void deleteReplyRemovesItFromPost() throws SQLException {
        Post post = createPost();
        Reply reply = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.deleteReply(reply.getReplyID());
        assertTrue(db.readRepliesForPost(post.getPostID()).isEmpty());
    }

    @Test
    void deleteReplyDecreasesReplyCount() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.deleteReply(reply1.getReplyID());
        assertEquals(1, db.getReplyCount(post.getPostID()));
    }

    @Test
    void deleteReplyDoesNotAffectOtherReplies() throws SQLException {
        Post post = createPost();
        Reply reply1 = createReply(post.getPostID(), REPLY_BODY_1, AUTHOR_B);
        Reply reply2 = createReply(post.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.deleteReply(reply1.getReplyID());
        List<Reply> remaining = db.readRepliesForPost(post.getPostID());
        assertEquals(1, remaining.size());
        assertEquals(REPLY_BODY_2, remaining.get(0).getBody());
    }

    private Post createPost() throws SQLException {
        Post post = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(post);
        return post;
    }

    private Reply createReply(int postID, String body, String author) throws SQLException {
        Reply reply = new Reply(postID, body, author);
        db.createReply(reply);
        return reply;
    }

}
