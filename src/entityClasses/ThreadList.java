package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: ThreadList Class </p>
 *
 * <p> Description: This ThreadList class supports storing all current Thread objects in the
 *  system, as well as retrieving threads by ID, name, or checking thread existence. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 */

public class ThreadList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<Thread> threads;

	/*****
	 * <p> Method: ThreadList() </p>
	 *
	 * <p> Description: This constructor establishes an empty ThreadList, ready to have
	 *  Thread objects added to it. </p>
	 *
	 */
	public ThreadList() {
		this.threads = new ArrayList<Thread>();
	}


	/*****
	 * <p> Method: void addThread(Thread t) </p>
	 *
	 * <p> Description: This method adds a Thread object to the ThreadList. </p>
	 *
	 * @param thread specifies the Thread object to be added to the list
	 *
	 */
	public void addThread(Thread thread) {
		threads.add(thread);
	}


	/*****
	 * <p> Method: List<Thread> getAllThreads() </p>
	 *
	 * <p> Description: This getter returns the complete list of Thread objects. </p>
	 *
	 * @return a List of all Thread objects currently stored
	 *
	 */
	public List<Thread> getAllThreads() {
		return threads;
	}


	/*****
	 * <p> Method: Thread getThreadByID(int threadID) </p>
	 *
	 * <p> Description: This method returns the Thread object matching the specified threadID,
	 *  or null if no such thread exists. </p>
	 *
	 * @param threadID specifies the ID of the thread to find
	 *
	 * @return the Thread object matching the specified threadID, or null if not found
	 *
	 */
	public Thread getThreadByID(int threadID) {
		for (Thread t : threads) {
			if (t.getThreadID() == threadID) {
				return t;
			}
		}
		return null;
	}


	/*****
	 * <p> Method: Thread getThreadByName(String name) </p>
	 *
	 * <p> Description: This method returns the Thread object matching the specified name,
	 *  or null if no such thread exists. </p>
	 *
	 * @param name specifies the name of the thread to find
	 *
	 * @return the Thread object matching the specified name, or null if not found
	 *
	 */
	public Thread getThreadByName(String name) {
		for (Thread t : threads) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}


	/*****
	 * <p> Method: boolean threadExists(String name) </p>
	 *
	 * <p> Description: This method checks whether a thread with the given name exists.
	 *  Used by PostReplyValidator. </p>
	 *
	 * @param name specifies the thread name to check
	 *
	 * @return TRUE if a thread with that name exists, FALSE otherwise
	 *
	 */
	public boolean threadExists(String name) {
		return getThreadByName(name) != null;
	}
}
