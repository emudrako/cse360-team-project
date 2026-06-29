package entityClasses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*******
 * <p> Title: PostListTest </p>
 *
 * <p> Description: Tests for the PostList entity class covering the filtering and lookup
 * logic in getPostsByAuthor, getPostsByKeyword, and getPostByID. All tests are pure
 * in-memory tests — no database connection required. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 * @version 1.00    2026-06-27 Initial version
 *
 */
class PostListTest {

    static final String AUTHOR_A = "userA";
    static final String AUTHOR_B = "userB";

    private PostList list;
    private Post postA1;
    private Post postA2;
    private Post postB1;

    @BeforeEach
    void setUp() {
        list = new PostList();

        postA1 = new Post("Java Basics", "Learn about variables", AUTHOR_A, "General");
        postA1.setPostID(1);

        postA2 = new Post("Java Loops", "For loops are useful", AUTHOR_A, "General");
        postA2.setPostID(2);

        postB1 = new Post("Python Intro", "Python is easy to learn", AUTHOR_B, "General");
        postB1.setPostID(3);

        list.addPost(postA1);
        list.addPost(postA2);
        list.addPost(postB1);
    }

    // -------------------------------------------------------------------------
    // getPostsByAuthor
    // -------------------------------------------------------------------------

    @Test
    void getPostsByAuthorReturnsOnlyMatchingAuthor() {
        List<Post> result = list.getPostsByAuthor(AUTHOR_B);
        assertEquals(1, result.size());
        assertEquals(postB1, result.get(0));
    }

    @Test
    void getPostsByAuthorReturnsAllPostsForAuthorWithMultiplePosts() {
        List<Post> result = list.getPostsByAuthor(AUTHOR_A);
        assertEquals(2, result.size());
        assertTrue(result.contains(postA1));
        assertTrue(result.contains(postA2));
    }

    @Test
    void getPostsByAuthorReturnsEmptyWhenNoMatch() {
        List<Post> result = list.getPostsByAuthor("unknownUser");
        assertTrue(result.isEmpty());
    }

    @Test
    void getPostsByAuthorReturnsEmptyOnEmptyList() {
        List<Post> result = new PostList().getPostsByAuthor(AUTHOR_A);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getPostsByKeyword
    // -------------------------------------------------------------------------

    @Test
    void getPostsByKeywordMatchesInTitle() {
        List<Post> result = list.getPostsByKeyword("Java");
        assertEquals(2, result.size());
        assertTrue(result.contains(postA1));
        assertTrue(result.contains(postA2));
    }

    @Test
    void getPostsByKeywordMatchesInBody() {
        List<Post> result = list.getPostsByKeyword("variables");
        assertEquals(1, result.size());
        assertEquals(postA1, result.get(0));
    }

    @Test
    void getPostsByKeywordDoesNotDuplicatePostWhenKeywordInBothTitleAndBody() {
        Post p = new Post("Java variables", "Learn about Java variables", AUTHOR_A, "General");
        p.setPostID(4);
        PostList single = new PostList();
        single.addPost(p);

        List<Post> result = single.getPostsByKeyword("Java");
        assertEquals(1, result.size());
    }

    @Test
    void getPostsByKeywordReturnsMultipleMatchingPosts() {
        List<Post> result = list.getPostsByKeyword("learn");
        assertEquals(2, result.size());
    }

    @Test
    void getPostsByKeywordReturnsEmptyWhenNoMatch() {
        List<Post> result = list.getPostsByKeyword("databases");
        assertTrue(result.isEmpty());
    }

    @Test
    void getPostsByKeywordReturnsEmptyOnEmptyList() {
        List<Post> result = new PostList().getPostsByKeyword("Java");
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getPostByID
    // -------------------------------------------------------------------------

    @Test
    void getPostByIDReturnsCorrectPost() {
        Post result = list.getPostByID(2);
        assertEquals(postA2, result);
    }

    @Test
    void getPostByIDReturnsNullWhenNotFound() {
        Post result = list.getPostByID(999);
        assertNull(result);
    }

    @Test
    void getPostByIDReturnsNullOnEmptyList() {
        Post result = new PostList().getPostByID(1);
        assertNull(result);
    }
}
