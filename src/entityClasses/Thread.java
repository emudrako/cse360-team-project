package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Thread Class </p>
 *
 * <p> Description: This Thread class represents a discussion thread entity in the system.
 *  Staff can create, read, update, and delete discussion threads. The "General" thread
 *  cannot be updated or deleted (isDefault=true). When a thread is deleted, all posts
 *  in that thread move to "General". </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 */

public class Thread {
	/*
	 * These are the private attributes for this entity object
	 */
	private int           threadID;
	private String        name;
	private String        description;
	private boolean       isDefault;
	private String        createdBy;
	private LocalDateTime createdAt;

	/*****
	 * <p> Method: Thread(String name, String description, String createdBy) </p>
	 *
	 * <p> Description: This constructor is used to establish Thread entity objects. </p>
	 *
	 * @param name specifies the name of this thread
	 *
	 * @param description specifies the description of this thread
	 *
	 * @param createdBy specifies the username of the staff member who created the thread
	 *
	 */
	public Thread(String name, String description, String createdBy) {
		this.name = name;
		this.description = description;
		this.createdBy = createdBy;
		this.isDefault = false;
		this.createdAt = LocalDateTime.now();
	}


	/*****
	 * <p> Method: int getThreadID() </p>
	 *
	 * <p> Description: This getter returns the ThreadID. </p>
	 *
	 * @return an int of the ThreadID
	 *
	 */
	public int getThreadID() { return threadID; }


	/*****
	 * <p> Method: void setThreadID(int id) </p>
	 *
	 * <p> Description: This setter defines the ThreadID attribute. </p>
	 *
	 * @param id specifies the ThreadID assigned to this thread by the database
	 *
	 */
	public void setThreadID(int id) { threadID = id; }


	/*****
	 * <p> Method: String getName() </p>
	 *
	 * <p> Description: This getter returns the Name. </p>
	 *
	 * @return a String of the Name
	 *
	 */
	public String getName() { return name; }


	/*****
	 * <p> Method: void setName(String name) </p>
	 *
	 * <p> Description: This setter defines the Name attribute. </p>
	 *
	 * @param name specifies the new name for this thread
	 *
	 */
	public void setName(String name) { this.name = name; }


	/*****
	 * <p> Method: String getDescription() </p>
	 *
	 * <p> Description: This getter returns the Description. </p>
	 *
	 * @return a String of the Description
	 *
	 */
	public String getDescription() { return description; }


	/*****
	 * <p> Method: void setDescription(String description) </p>
	 *
	 * <p> Description: This setter defines the Description attribute. </p>
	 *
	 * @param description specifies the new description for this thread
	 *
	 */
	public void setDescription(String description) { this.description = description; }


	/*****
	 * <p> Method: boolean getIsDefault() </p>
	 *
	 * <p> Description: This getter returns the IsDefault attribute. True only for
	 *  "General", which blocks update and delete operations. </p>
	 *
	 * @return a boolean TRUE if this thread is the default "General" thread
	 *
	 */
	public boolean getIsDefault() { return isDefault; }


	/*****
	 * <p> Method: void setIsDefault(boolean isDefault) </p>
	 *
	 * <p> Description: This setter defines the IsDefault attribute. </p>
	 *
	 * @param isDefault specifies TRUE if this thread is the protected "General" thread
	 *
	 */
	public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }


	/*****
	 * <p> Method: String getCreatedBy() </p>
	 *
	 * <p> Description: This getter returns the CreatedBy username. </p>
	 *
	 * @return a String of the username who created this thread
	 *
	 */
	public String getCreatedBy() { return createdBy; }


	/*****
	 * <p> Method: LocalDateTime getCreatedAt() </p>
	 *
	 * <p> Description: This getter returns the timestamp when this thread was created. </p>
	 *
	 * @return a LocalDateTime of when this thread was created
	 *
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }


	/*****
	 * <p> Method: void setCreatedAt(LocalDateTime time) </p>
	 *
	 * <p> Description: This setter defines the CreatedAt timestamp. </p>
	 *
	 * @param time specifies the timestamp when this thread was created
	 *
	 */
	public void setCreatedAt(LocalDateTime time) { createdAt = time; }
}
