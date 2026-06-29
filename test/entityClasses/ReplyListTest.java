package entityClasses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: ReplyListTest </p>
 *
 * <p> Description: Tests for the ReplyList entity class covering the filtering and lookup
 * logic in getRepliesByPostID and getReplyByID. All tests are pure in-memory tests —
 * no database connection required. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00    2026-06-27 Initial version
 *
 */
class ReplyListTest {

    private ReplyList list;
    private Reply replyPost1a;
    private Reply replyPost1b;
    private Reply replyPost2a;

    @BeforeEach
    void setUp() {
        list = new ReplyList();

        replyPost1a = new Reply(1, "First reply to post 1", "userA");
        replyPost1a.setReplyID(10);

        replyPost1b = new Reply(1, "Second reply to post 1", "userB");
        replyPost1b.setReplyID(11);

        replyPost2a = new Reply(2, "First reply to post 2", "userA");
        replyPost2a.setReplyID(20);

        list.addReply(replyPost1a);
        list.addReply(replyPost1b);
        list.addReply(replyPost2a);
    }

    // -------------------------------------------------------------------------
    // getRepliesByPostID
    // -------------------------------------------------------------------------

    @Test
    void getRepliesByPostIDReturnsOnlyRepliesForThatPost() {
        List<Reply> result = list.getRepliesByPostID(2);
        assertEquals(1, result.size());
        assertEquals(replyPost2a, result.get(0));
    }

    @Test
    void getRepliesByPostIDReturnsAllRepliesWhenMultipleBelongToSamePost() {
        List<Reply> result = list.getRepliesByPostID(1);
        assertEquals(2, result.size());
        assertTrue(result.contains(replyPost1a));
        assertTrue(result.contains(replyPost1b));
    }

    @Test
    void getRepliesByPostIDReturnsEmptyWhenNoMatch() {
        List<Reply> result = list.getRepliesByPostID(999);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRepliesByPostIDReturnsEmptyOnEmptyList() {
        List<Reply> result = new ReplyList().getRepliesByPostID(1);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getReplyByID
    // -------------------------------------------------------------------------

    @Test
    void getReplyByIDReturnsCorrectReply() {
        Reply result = list.getReplyByID(11);
        assertEquals(replyPost1b, result);
    }

    @Test
    void getReplyByIDReturnsNullWhenNotFound() {
        Reply result = list.getReplyByID(999);
        assertNull(result);
    }

    @Test
    void getReplyByIDReturnsNullOnEmptyList() {
        Reply result = new ReplyList().getReplyByID(10);
        assertNull(result);
    }
}
