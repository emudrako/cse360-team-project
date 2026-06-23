package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: PostList Class </p>
 *
 * <p> Description: This PostList class supports storing all current Post objects in the system,
 *  as well as retrieving any subset of those posts, such as the posts written by a specific
 *  author or the posts matching a specified keyword. </p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 *
 */

public class PostList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<Post> posts;

	/*****
	 * <p> Method: PostList() </p>
	 *
	 * <p> Description: This constructor establishes an empty PostList, ready to have
	 * Post objects added to it. </p>
	 *
	 */
	// Constructor to initialize an empty list of Posts.
	public PostList() {
		this.posts = new ArrayList<Post>();
	}


	/*****
	 * <p> Method: void addPost(Post post) </p>
	 *
	 * <p> Description: This method adds a Post object to the PostList. </p>
	 *
	 * @param post specifies the Post object to be added to the list
	 *
	 */
	// Adds a Post to the list.
	public void addPost(Post post) {
		posts.add(post);
	}


	/*****
	 * <p> Method: List<Post> getAllPosts() </p>
	 *
	 * <p> Description: This getter returns the complete list of Post objects. </p>
	 *
	 * @return a List of all Post objects currently stored
	 *
	 */
	// Gets the complete list of Posts.
	public List<Post> getAllPosts() {
		return posts;
	}


	/*****
	 * <p> Method: List<Post> getPostsByAuthor(String authorUsername) </p>
	 *
	 * <p> Description: This method returns the subset of Post objects written by a
	 * specified author. </p>
	 *
	 * @param authorUsername specifies the username whose posts should be returned
	 *
	 * @return a List of Post objects authored by the specified username
	 *
	 */
	// Gets the subset of Posts written by a specific author.
	public List<Post> getPostsByAuthor(String authorUsername) {
		List<Post> result = new ArrayList<Post>();
		for (Post p : posts) {
			if (p.getAuthorUsername().equals(authorUsername)) {
				result.add(p);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: List<Post> getPostsByKeyword(String keyword) </p>
	 *
	 * <p> Description: This method returns the subset of Post objects whose title or
	 * body contains the specified keyword. </p>
	 *
	 * @param keyword specifies the keyword to search for within post titles and bodies
	 *
	 * @return a List of Post objects matching the specified keyword
	 *
	 */
	// Gets the subset of Posts matching a keyword.
	public List<Post> getPostsByKeyword(String keyword) {
		List<Post> result = new ArrayList<Post>();
		for (Post p : posts) {
			if (p.getTitle().contains(keyword) || p.getBody().contains(keyword)) {
				result.add(p);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: Post getPostByID(int postID) </p>
	 *
	 * <p> Description: This method returns the single Post object matching the
	 * specified postID, or null if no such post exists. </p>
	 *
	 * @param postID specifies the ID of the post to find
	 *
	 * @return the Post object matching the specified postID, or null if not found
	 *
	 */
	// Finds a specific Post by its ID.
	public Post getPostByID(int postID) {
		for (Post p : posts) {
			if (p.getPostID() == postID) {
				return p;
			}
		}
		return null;
	}
}
