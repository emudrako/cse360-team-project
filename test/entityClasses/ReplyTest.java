package entityClasses;

import database.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: ReplyTest </p>
 *
 * <p> Description: Tests for the Reply entity class covering the createdAt timestamp
 * field added for Story 10.  Pure entity tests verify the getter/setter in isolation;
 * persistence tests use a private in-memory H2 database so they never touch the
 * production file database and each test method starts with a clean schema. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00    2026-06-19 Initial version
 *
 */
class ReplyTest {

    static final String TITLE      = "Test Title";
    static final String BODY       = "Test body content";
    static final String THREAD     = "General";
    static final String AUTHOR_A   = "userA";
    static final String AUTHOR_B   = "userB";
    static final String REPLY_BODY = "Reply one";

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

    // Story 12: timestamp on posts and replies
    @Test
    void replyTimestampIsNullByDefault() {
        Reply r = new Reply(1, REPLY_BODY, AUTHOR_B);
        assertNull(r.getCreatedAt());
    }

    @Test
    void replyTimestampCanBeSetAndRetrieved() {
        Reply r = new Reply(1, REPLY_BODY, AUTHOR_B);
        LocalDateTime now = LocalDateTime.now();
        r.setCreatedAt(now);
        assertEquals(now, r.getCreatedAt());
    }

    @Test
    void replyTimestampIsSetAfterCreate() throws SQLException {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(p);
        Reply r = new Reply(p.getPostID(), REPLY_BODY, AUTHOR_B);
        db.createReply(r);
        assertNotNull(r.getCreatedAt());
    }

    @Test
    void replyTimestampIsRestoredOnRead() throws SQLException {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(p);
        Reply created = new Reply(p.getPostID(), REPLY_BODY, AUTHOR_B);
        db.createReply(created);
        List<Reply> replies = db.readRepliesForPost(p.getPostID());
        assertNotNull(replies.get(0).getCreatedAt());
        assertEquals(
            created.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
            replies.get(0).getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
    }
}
