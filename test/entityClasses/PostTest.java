package entityClasses;

import database.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: PostTest </p>
 *
 * <p> Description: Tests for the Post entity class covering the createdAt timestamp
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
class PostTest {

    static final String TITLE    = "Test Title";
    static final String BODY     = "Test body content";
    static final String THREAD   = "General";
    static final String AUTHOR_A = "userA";

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
    void postTimestampIsNullByDefault() {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        assertNull(p.getCreatedAt());
    }

    @Test
    void postTimestampCanBeSetAndRetrieved() {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedAt(now);
        assertEquals(now, p.getCreatedAt());
    }

    @Test
    void postTimestampIsSetAfterCreate() throws SQLException {
        Post p = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(p);
        assertNotNull(p.getCreatedAt());
    }

    @Test
    void postTimestampIsRestoredOnRead() throws SQLException {
        Post created = new Post(TITLE, BODY, AUTHOR_A, THREAD);
        db.createPost(created);
        Post loaded = db.readPost(created.getPostID());
        assertNotNull(loaded.getCreatedAt());
        assertEquals(
            created.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
            loaded.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
    }
}
