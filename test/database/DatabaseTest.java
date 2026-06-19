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
 * <p> Description: Integration tests for the five read-tracking methods added to the
 * Database class for Stories 6 and 7: getReplyCount, markReplyAsRead, isReplyReadByUser,
 * getUnreadReplyCount, and getUnreadRepliesForPost.  Each test method gets a fresh private
 * in-memory H2 database so tests are fully isolated from one another and from the
 * production file database. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00    2026-06-19 Initial version
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
        Post p = createPost();
        assertEquals(0, db.getReplyCount(p.getPostID()));
    }

    @Test
    void replyCountIsOneAfterOneReply() throws SQLException {
        Post p = createPost();
        createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        assertEquals(1, db.getReplyCount(p.getPostID()));
    }

    @Test
    void replyCountReflectsMultipleReplies() throws SQLException {
        Post p = createPost();
        createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getReplyCount(p.getPostID()));
    }

    @Test
    void replyCountIsIsolatedPerPost() throws SQLException {
        Post p1 = createPost();
        Post p2 = new Post(TITLE, BODY, AUTHOR_B, THREAD);
        db.createPost(p2);
        createReply(p2.getPostID(), REPLY_BODY_1, AUTHOR_A);
        createReply(p2.getPostID(), REPLY_BODY_2, AUTHOR_A);
        assertEquals(0, db.getReplyCount(p1.getPostID()));
        assertEquals(2, db.getReplyCount(p2.getPostID()));
    }

    // Story 11: unread replies count
    @Test
    void replyIsUnreadByDefault() throws SQLException {
        Post p = createPost();
        Reply r = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        assertFalse(db.isReplyReadByUser(r.getReplyID(), AUTHOR_A));
    }

    @Test
    void replyIsReadAfterMarkingAsRead() throws SQLException {
        Post p = createPost();
        Reply r = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(r.getReplyID(), AUTHOR_A);
        assertTrue(db.isReplyReadByUser(r.getReplyID(), AUTHOR_A));
    }

    @Test
    void markingReplyReadIsIdempotent() throws SQLException {
        Post p = createPost();
        Reply r = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(r.getReplyID(), AUTHOR_A);
        assertDoesNotThrow(() -> db.markReplyAsRead(r.getReplyID(), AUTHOR_A));
        assertTrue(db.isReplyReadByUser(r.getReplyID(), AUTHOR_A));
    }

    @Test
    void readStatusIsPerUser() throws SQLException {
        Post p = createPost();
        Reply r = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        db.markReplyAsRead(r.getReplyID(), AUTHOR_A);
        assertFalse(db.isReplyReadByUser(r.getReplyID(), AUTHOR_B));
    }

    @Test
    void unreadCountEqualsReplyCountWhenNoneRead() throws SQLException {
        Post p = createPost();
        createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getUnreadReplyCount(p.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountDecreasesAfterMarkingOneRead() throws SQLException {
        Post p = createPost();
        Reply r1 = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(r1.getReplyID(), AUTHOR_A);
        assertEquals(1, db.getUnreadReplyCount(p.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountIsZeroWhenAllRepliesRead() throws SQLException {
        Post p = createPost();
        Reply r1 = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        Reply r2 = createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(r1.getReplyID(), AUTHOR_A);
        db.markReplyAsRead(r2.getReplyID(), AUTHOR_A);
        assertEquals(0, db.getUnreadReplyCount(p.getPostID(), AUTHOR_A));
    }

    @Test
    void unreadCountIsPerUser() throws SQLException {
        Post p = createPost();
        Reply r1 = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(r1.getReplyID(), AUTHOR_A);
        assertEquals(2, db.getUnreadReplyCount(p.getPostID(), AUTHOR_B));
    }

    @Test
    void unreadRepliesListIsEmptyWhenNoReplies() throws SQLException {
        Post p = createPost();
        assertTrue(db.getUnreadRepliesForPost(p.getPostID(), AUTHOR_A).isEmpty());
    }

    @Test
    void unreadRepliesListContainsAllRepliesInitially() throws SQLException {
        Post p = createPost();
        createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        assertEquals(2, db.getUnreadRepliesForPost(p.getPostID(), AUTHOR_A).size());
    }

    @Test
    void unreadRepliesListExcludesReadReplies() throws SQLException {
        Post p = createPost();
        Reply r1 = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(r1.getReplyID(), AUTHOR_A);
        List<Reply> unread = db.getUnreadRepliesForPost(p.getPostID(), AUTHOR_A);
        assertEquals(1, unread.size());
        assertEquals(REPLY_BODY_2, unread.get(0).getBody());
    }

    @Test
    void unreadRepliesListIsEmptyWhenAllRead() throws SQLException {
        Post p = createPost();
        Reply r1 = createReply(p.getPostID(), REPLY_BODY_1, AUTHOR_B);
        Reply r2 = createReply(p.getPostID(), REPLY_BODY_2, AUTHOR_B);
        db.markReplyAsRead(r1.getReplyID(), AUTHOR_A);
        db.markReplyAsRead(r2.getReplyID(), AUTHOR_A);
        assertTrue(db.getUnreadRepliesForPost(p.getPostID(), AUTHOR_A).isEmpty());
    }

    private Post createPost() throws SQLException {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(p);
        return p;
    }

    private Reply createReply(int postID, String body, String author) throws SQLException {
        Reply r = new Reply(postID, body, author);
        db.createReply(r);
        return r;
    }

}
