package database;

import java.sql.*;
import java.time.LocalDateTime; // Import LocalDateTime for invitation code expiration functionality
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MB)" on the l3ft side
 * of the page under the heading "Reference" for a PDF of 438 pages.)  This class leverages H2
 * and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by Pravalika 
 * 							Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01		2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;
	private boolean currentStudentRole;
	private boolean currentInstructorRole;
	private boolean currentStaffRole;
	private boolean currentOneTimePassword;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
//	statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

/*******
 * <p> Method: connectToTestDatabase </p>
 *
 * <p> Description: Opens a private anonymous in-memory H2 instance used exclusively
 * by unit/integration tests.  Each call gets a fresh, empty schema that is destroyed
 * when the connection is closed, so tests never touch the production file database.</p>
 *
 * @throws SQLException when the driver cannot open the connection
 *
 */
	public void connectToTestDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection("jdbc:h2:mem:", USER, PASS);
			statement = connection.createStatement();
			createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE, "
				+ "studentRole BOOL DEFAULT FALSE, "
				+ "instructorRole BOOL DEFAULT FALSE, "
				+ "staffRole BOOL DEFAULT FALSE, "
				+ "oneTimePassword BOOL DEFAULT FALSE)";
	statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10), "
	            + "deadline TIMESTAMP)"; // Deadline column added for invitation expiration;
	    statement.execute(invitationCodesTable);
	    
		// Create the posts table
	    String postsTable = "CREATE TABLE IF NOT EXISTS PostsDB ("
	    		+ "postID INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "title VARCHAR(100), "
	    		+ "body VARCHAR(1000), "
	    		+ "authorUsername VARCHAR(255), "
	    		+ "thread VARCHAR(255), "
	    		+ "isDeleted BOOL DEFAULT FALSE)";
	    statement.execute(postsTable);

	    // Create the replies table
	    String repliesTable = "CREATE TABLE IF NOT EXISTS RepliesDB ("
	    		+ "replyID INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "postID INT, "
	    		+ "body VARCHAR(1000), "
	    		+ "authorUsername VARCHAR(255))";
	    		//+ "parentReplyID INT";
	    statement.execute(repliesTable);

	    // Add createdAt to existing tables if upgrading from an older schema
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS "
	    		+ "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS "
	    		+ "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

	    // Tracks which replies each user has already read (for unreadCount)
	    String replyReadStatusTable = "CREATE TABLE IF NOT EXISTS ReplyReadStatusDB ("
	    		+ "replyID INT, "
	    		+ "readerUsername VARCHAR(255), "
	    		+ "PRIMARY KEY (replyID, readerUsername))";
	    statement.execute(replyReadStatusTable);
	}
	


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2, "
				+ "studentRole, instructorRole, staffRole) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);

			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);

			currentStudentRole = user.getStudentRole();
			pstmt.setBoolean(11, currentStudentRole);

			currentInstructorRole = user.getInstructorRole();
			pstmt.setBoolean(12, currentInstructorRole);

			currentStaffRole = user.getStaffRole();
			pstmt.setBoolean(13, currentStaffRole);

			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	public boolean loginStudent(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loginInstructor(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND instructorRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loginStaff(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		if (user.getStudentRole()) numberOfRoles++;
		if (user.getInstructorRole()) numberOfRoles++;
		if (user.getStaffRole()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role, LocalDateTime deadline) </p>
	 * 
	 * <p> Description: Given an email address and a roles and a deadline, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table. When the invitation code expires, the record is removed from the table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @param deadline specifies the expiration of the invitation code
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role, LocalDateTime deadline) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role, deadline) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.setTimestamp(4, Timestamp.valueOf(deadline)); // Convert LocalDateTime to SQL Timestamp object
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	/*******
	 * <p> Method: List<String[]> getAllInvitations() </p>
	 * 
	 * <p> Description: Return the InvitationCodes table.</p>
	 *  
	 * @return the list of all codes, email addresses, roles and deadlines.
	 * 
	 */
	public List<String[]> getAllInvitations() {		
		List<String[]> userList = new ArrayList<String[]>();
		String query = "SELECT * FROM InvitationCodes";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String[] invitation = {rs.getString("code"), rs.getString("emailAddress"), rs.getString("role"), rs.getString("deadline")};
				userList.add(invitation);
			}
		} 
		catch (SQLException e) {
        return null;
		}
		//	System.out.println(userList);
		return userList;
	}
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
   /*******
    * <p> Method: boolean isInvitationExpired(String code) </p>
	* 
	* <p> Description: Check for expiration of invitation code.</p>
	* 
	*  @param code is the 6 character String invitation code
	*/
	public boolean isInvitationExpired(String code){
		LocalDateTime now = LocalDateTime.now();

		boolean isExpired = false;
		
		String query = "SELECT deadline FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            LocalDateTime deadline = rs.getTimestamp("deadline").toLocalDateTime();
	            isExpired = deadline.isBefore(now); // if deadline has passed, set isExpired to true
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

		return isExpired;
			
	}
	
	
   /*******
	* <p> Method: void removeInvitationAfterExpiration(String code) </p>
	* 
	* <p> Description: Remove an invitation record once it is expired.</p>
	* 
	*  @param code is the 6 character String invitation code
	*/
	public void removeInvitationAfterExpiration(String code) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the expired code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Method: void updatePassword(String username, String password) </p>
	 * 
	 * <p> Description: Update the password of a user given that user's username and the new
	 *		password.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param password is the new password for the user
	 *  
	 */
	// update the password
	public void updatePassword(String username, String password) {
	    String query = "UPDATE userDB SET password = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = password;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: void updateOneTimePassword(String username) </p>
	 * 
	 * <p> Description: Update the password of a user given that user's username and the new
	 *		password.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param password is the new password for the user
	 *  
	 */
	// update the password
	public void updateOneTimePassword(String username, String value) {
	    boolean status = false;
	    if(value == "true") {
	    	status = true;
	    }
	    if (value == "false") {
	    	status = false;
	    }
		String query = "UPDATE userDB SET oneTimePassword = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, value);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentOneTimePassword = status;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
	    	currentStudentRole = rs.getBoolean(12);
	    	currentInstructorRole = rs.getBoolean(13);
	    	currentStaffRole = rs.getBoolean(14);
	    	currentOneTimePassword = rs.getBoolean(15);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET studentRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentStudentRole = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Instructor") == 0) {
			String query = "UPDATE userDB SET instructorRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentInstructorRole = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET staffRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentStaffRole = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	public boolean getCurrentStudentRole() { return currentStudentRole; }
	public boolean getCurrentInstructorRole() { return currentInstructorRole; }
	public boolean getCurrentStaffRole() { return currentStaffRole; }
	
	/*******
	 * <p> Method: boolean getCurrentOneTimePassword() </p>
	 * 
	 * <p> Description: Get the current user's one-time password status.</p>
	 * 
	 * @return true if this has a one-time password set, or false if not
	 *  
	 */
	public boolean getCurrentOneTimePassword() { return currentOneTimePassword;}


	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}

	/*******
	 * <p> Method: boolean deleteUser(String username) </p>
	 *
	 * <p> Description: Deletes a user from the database. </p>
	 *
	 * @param username the user to delete
	 * @return true if deletion was successful
	 */
	public boolean deleteUser(String username) {
	    if (username == null || username.trim().isEmpty()) return false;
	    
	    String query = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username.trim());
	        int rowsDeleted = pstmt.executeUpdate();
	        return rowsDeleted > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	/*******
	 * <p> Method: String getUserDetailsForList(String username) </p>
	 *
	 * <p> Description: Returns a formatted string with all user details for the list. </p>
	 */
	public String getUserDetailsForList(String username) {
	    StringBuilder sb = new StringBuilder();
	    String query = "SELECT * FROM userDB WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            sb.append("Username: ").append(rs.getString("userName")).append("\n");
	            sb.append("Name: ").append(rs.getString("firstName") != null ? rs.getString("firstName") : "")
	              .append(" ")
	              .append(rs.getString("middleName") != null ? rs.getString("middleName") + " " : "")
	              .append(rs.getString("lastName") != null ? rs.getString("lastName") : "").append("\n");
	            sb.append("Email: ").append(rs.getString("emailAddress") != null ? rs.getString("emailAddress") : "<none>").append("\n");
	            sb.append("Roles: ");
	            
	            boolean hasRole = false;
	            if (rs.getBoolean("adminRole")) { sb.append("Admin "); hasRole = true; }
	            if (rs.getBoolean("studentRole")) { sb.append("Student "); hasRole = true; }
	            if (rs.getBoolean("instructorRole")) { sb.append("Instructor "); hasRole = true; }
	            if (rs.getBoolean("staffRole")) { sb.append("Staff "); hasRole = true; }
	            
	            if (!hasRole) sb.append("None");
	            sb.append("\n");
	        }
	    } catch (SQLException e) {
	        sb.append("Error retrieving user details\n");
	    }
	    return sb.toString();
	}
	
	/*******
	 * <p> Method: createPost </p>
	 * 
	 * <p> Description: Validates the post's title and body, then creates a new row in
	 * the database using the post parameter, and sets the database-generated postID
	 * back onto the Post object. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @throws IllegalArgumentException when the post's title or body fails input validation.
	 * 
	 * @param post specifies a Post object to be added to the database.
	 * 
	 */
		public void createPost(Post post) throws SQLException {
			// Validate the title and body before touching the database
			String errMsg = recognizers.PostReplyValidator.checkForValidPost(
					post.getTitle(), post.getBody());
			if (!errMsg.isEmpty()) {
				throw new IllegalArgumentException(errMsg);
			}
			
			// Validate the thread exists before touching the database
			String threadErrMsg = recognizers.PostReplyValidator.checkForValidThread(post.getThread());
			if (!threadErrMsg.isEmpty()) {
				throw new IllegalArgumentException(threadErrMsg);
			}

			LocalDateTime now = LocalDateTime.now();
			String insertPost = "INSERT INTO PostsDB (title, body, authorUsername, thread, isDeleted, createdAt) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertPost,
					Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, post.getTitle());
				pstmt.setString(2, post.getBody());
				pstmt.setString(3, post.getAuthorUsername());
				pstmt.setString(4, post.getThread());
				pstmt.setBoolean(5, post.getIsDeleted());
				pstmt.setTimestamp(6, Timestamp.valueOf(now));
				pstmt.executeUpdate();

				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						post.setPostID(rs.getInt(1));
					}
				}
				post.setCreatedAt(now);
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while creating post: "
						+ e.getMessage());
				throw e;
			}
		}
	
	/**********
	 * <p> Method: getPostObjects() </p>
	 * 
	 * <p> Description: This creates a list of post objects from the database </p>
	 * 
	 * @returns a list of Post objects created from the database
	 * 
	 */
		public List<Post> getPostObjects() throws SQLException {
			List<Post> postObjects = new ArrayList<>();
			
			String query = "SELECT * FROM PostsDB";
			
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.wasNull()) {
				return postObjects;
			}
			else {
				while (rs.next()) {
					int postID = rs.getInt("postID");
					Post post = new Post(
						rs.getString("title"),
						rs.getString("body"),
						rs.getString("authorUsername"),
						rs.getString("thread")
						);
					post.setPostID(postID);
					post.setIsDeleted(rs.getBoolean("isDeleted"));
					Timestamp ts = rs.getTimestamp("createdAt");
					if (ts != null) post.setCreatedAt(ts.toLocalDateTime());
					postObjects.add(post);
				}
			}
			
			
			return postObjects;
		}
		
	/*******
	* <p> Method: readPost </p>
	* 
	* <p> Description: Retrieves a single Post object from the database matching the
	* specified postID, or null if no such post exists. </p>
	* 
	* @param postID specifies the ID of the post to retrieve.
	* 
	* @return a Post object matching the specified postID, or null if not found.
	* 
	*/
		public Post readPost(int postID) {
			String query = "SELECT * FROM PostsDB WHERE postID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, postID);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						Post post = new Post(
							rs.getString("title"),
							rs.getString("body"),
							rs.getString("authorUsername"),
							rs.getString("thread"));
						post.setPostID(rs.getInt("postID"));
						post.setIsDeleted(rs.getBoolean("isDeleted"));
						Timestamp ts = rs.getTimestamp("createdAt");
						if (ts != null) post.setCreatedAt(ts.toLocalDateTime());
						return post;
					}
				}
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while reading post: " 
						+ e.getMessage());
			}
			return null;
		}
	/*******
	* <p> Method: updatePost </p>
	* 
	* <p> Description: Validates the new title and body, then updates the title and body
    * of an existing post in the database. </p>
	* 
	* @throws IllegalArgumentException when the new title or body fails input validation.
	* 
	*  @param postID specifies the ID of the post to update.
	* 
	* @param newTitle specifies the new title for the post.
	* 
	* @param newBody specifies the new body content for the post.
	* 
	*/
		public void updatePost(int postID, String newTitle, String newBody) {
			// Validate the new title and body before touching the database
			String errMsg = recognizers.PostReplyValidator.checkForValidPost(newTitle, newBody);
			if (!errMsg.isEmpty()) {
				throw new IllegalArgumentException(errMsg);
			}

			String query = "UPDATE PostsDB SET title = ?, body = ? WHERE postID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, newTitle);
				pstmt.setString(2, newBody);
				pstmt.setInt(3, postID);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while updating post: " 
						+ e.getMessage());
			}
		}
	/*******
	* <p> Method: deletePost </p>
	* 
	* <p> Description: Soft-deletes a post by setting its isDeleted flag to true, rather
	* than removing the row, so that any existing replies can still reference the original
	* post and display a message indicating it has been deleted. </p>
	* 
	* @param postID specifies the ID of the post to delete.
    * 
	*/
		public void deletePost(int postID) {
			String query = "UPDATE PostsDB SET isDeleted = TRUE WHERE postID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, postID);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while deleting post: " 
						+ e.getMessage());
			}
		}
	/*******
	* <p> Method: createReply </p>
	* 
	* <p> Description: Validates the reply's body, then creates a new row in the database
	* using the reply parameter, and sets the database-generated replyID back onto the
	* Reply object. </p>
	* 
	* @throws SQLException when there is an issue creating the SQL command or executing it.
	* 
	* @throws IllegalArgumentException when the reply's body fails input validation.
	* 
	* @param reply specifies a Reply object to be added to the database.
	* 
	*/
		public void createReply(Reply reply) throws SQLException {
		// Validate the body before touching the database
			String errMsg = recognizers.PostReplyValidator.checkForValidReply(reply.getBody());
			if (!errMsg.isEmpty()) {
				throw new IllegalArgumentException(errMsg);
			}

			LocalDateTime now = LocalDateTime.now();
			String insertReply = "INSERT INTO RepliesDB (postID, body, authorUsername, createdAt) "
				+ "VALUES (?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertReply,
				Statement.RETURN_GENERATED_KEYS)) {
					pstmt.setInt(1, reply.getPostID());
					pstmt.setString(2, reply.getBody());
					pstmt.setString(3, reply.getAuthorUsername());
					pstmt.setTimestamp(4, Timestamp.valueOf(now));
					pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					reply.setReplyID(rs.getInt(1));
				}
			}
			reply.setCreatedAt(now);
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error while creating reply: "
							+ e.getMessage());
					throw e;
				}
			}
	
	/**********
	 * <p> Method: getReplyObjects() </p>
	 * 
	 * <p> Description: This creates a list of reply objects from the database </p>
	 * 
	 * @returns a list of Reply objects created from the database
	 * 
	 */
			public List<Reply> getReplyObjects() throws SQLException {
				List<Reply> replyObjects = new ArrayList<>();
				
				String query = "SELECT * FROM RepliesDB";
				
				PreparedStatement stmt = connection.prepareStatement(query);
				ResultSet rs = stmt.executeQuery();
				
				if (rs.wasNull()) {
					return replyObjects;
				}
				else {
					while (rs.next()) {
						int replyID = rs.getInt("replyID");
						Reply reply = new Reply(
							rs.getInt("postID"),
							rs.getString("body"),
							rs.getString("authorUsername")
							);
						reply.setReplyID(replyID);
						replyObjects.add(reply);
					}
				}
				
				return replyObjects;
			}	
		
	/*******
	* <p> Method: readRepliesForPost </p>
	* 
	* <p> Description: Retrieves all Reply objects in the database that respond to the
	* specified postID. </p>
	* 
	* @param postID specifies the ID of the post whose replies should be retrieved.
	* 
	* @return a List of Reply objects responding to the specified postID.
	* 
	*/
		public List<Reply> readRepliesForPost(int postID) {
			List<Reply> replies = new ArrayList<Reply>();
			String query = "SELECT * FROM RepliesDB WHERE postID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, postID);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						Reply reply = new Reply(
							rs.getInt("postID"),
							rs.getString("body"),
							rs.getString("authorUsername"));
							reply.setReplyID(rs.getInt("replyID"));
							Timestamp ts = rs.getTimestamp("createdAt");
							if (ts != null) reply.setCreatedAt(ts.toLocalDateTime());
							replies.add(reply);
					}
				}
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while reading replies: " 
					+ e.getMessage());
				}
			return replies;
		}
	/*******
	* <p> Method: updateReply </p>
	* 
	* <p> Description: Validates the new body, then updates the body content of an
	* existing reply in the database. </p>
	* 
	* @throws IllegalArgumentException when the new body fails input validation.
	* 
	* @param replyID specifies the ID of the reply to update.
	* 
	* @param newBody specifies the new body content for the reply.
	* 
	*/
		public void updateReply(int replyID, String newBody) {
		// Validate the new body before touching the database
			String errMsg = recognizers.PostReplyValidator.checkForValidReply(newBody);
				if (!errMsg.isEmpty()) {
					throw new IllegalArgumentException(errMsg);
				}

			String query = "UPDATE RepliesDB SET body = ? WHERE replyID = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, newBody);
					pstmt.setInt(2, replyID);
					pstmt.executeUpdate();
				} catch (SQLException e) {
						System.err.println("*** ERROR *** Database error while updating reply: " 
				+ e.getMessage());
						}
		}
	/*******
	* <p> Method: deleteReply </p>
	* 
	* <p> Description: Permanently removes a reply from the database. Unlike posts, replies
	* do not require soft-delete behavior, since no other entity references a reply after
	* it has been removed. </p>
	* 
	* @param replyID specifies the ID of the reply to delete.
	* 
	*/
		public void deleteReply(int replyID) {
			String query = "DELETE FROM RepliesDB WHERE replyID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, replyID);
				pstmt.executeUpdate();
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error while deleting reply: " 
				+ e.getMessage());
					}
			}
	/*******
	 * <p> Method: int getReplyCount(int postID) </p>
	 *
	 * <p> Description: Returns the total number of replies for the specified post. </p>
	 *
	 * @param postID specifies the post whose reply count should be returned
	 *
	 * @return the number of replies for the post, or 0 on error
	 *
	 */
	public int getReplyCount(int postID) {
		String query = "SELECT COUNT(*) AS cnt FROM RepliesDB WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) return rs.getInt("cnt");
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in getReplyCount: " + e.getMessage());
		}
		return 0;
	}

	/*******
	 * <p> Method: void markReplyAsRead(int replyID, String username) </p>
	 *
	 * <p> Description: Records that the specified user has read the specified reply.
	 * Subsequent calls for the same pair are silently ignored. </p>
	 *
	 * @param replyID specifies the reply that was read
	 *
	 * @param username specifies the user who read the reply
	 *
	 */
	public void markReplyAsRead(int replyID, String username) {
		String query = "MERGE INTO ReplyReadStatusDB (replyID, readerUsername) KEY(replyID, readerUsername) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyID);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in markReplyAsRead: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: boolean isReplyReadByUser(int replyID, String username) </p>
	 *
	 * <p> Description: Returns true if the specified user has already read the specified reply. </p>
	 *
	 * @param replyID specifies the reply to check
	 *
	 * @param username specifies the user to check
	 *
	 * @return true if the reply has been read by the user, false otherwise
	 *
	 */
	public boolean isReplyReadByUser(int replyID, String username) {
		String query = "SELECT COUNT(*) AS cnt FROM ReplyReadStatusDB WHERE replyID = ? AND readerUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyID);
			pstmt.setString(2, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) return rs.getInt("cnt") > 0;
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in isReplyReadByUser: " + e.getMessage());
		}
		return false;
	}

	/*******
	 * <p> Method: int getUnreadReplyCount(int postID, String username) </p>
	 *
	 * <p> Description: Returns the number of replies on the specified post that the specified
	 * user has not yet read. </p>
	 *
	 * @param postID specifies the post whose unread reply count should be returned
	 *
	 * @param username specifies the user whose read status is used
	 *
	 * @return the number of unread replies for this user on this post
	 *
	 */
	public int getUnreadReplyCount(int postID, String username) {
		String query = "SELECT COUNT(*) AS cnt FROM RepliesDB r "
				+ "WHERE r.postID = ? "
				+ "AND r.replyID NOT IN ("
				+ "  SELECT replyID FROM ReplyReadStatusDB WHERE readerUsername = ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			pstmt.setString(2, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) return rs.getInt("cnt");
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in getUnreadReplyCount: " + e.getMessage());
		}
		return 0;
	}

	/*******
	 * <p> Method: List&lt;Reply&gt; getUnreadRepliesForPost(int postID, String username) </p>
	 *
	 * <p> Description: Returns all replies on the specified post that the specified user
	 * has not yet read, ordered by creation time. </p>
	 *
	 * @param postID specifies the post whose unread replies should be returned
	 *
	 * @param username specifies the user whose read status is used
	 *
	 * @return a List of unread Reply objects for this user on this post
	 *
	 */
	public List<Reply> getUnreadRepliesForPost(int postID, String username) {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT * FROM RepliesDB r "
				+ "WHERE r.postID = ? "
				+ "AND r.replyID NOT IN ("
				+ "  SELECT replyID FROM ReplyReadStatusDB WHERE readerUsername = ?) "
				+ "ORDER BY r.createdAt ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			pstmt.setString(2, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Reply reply = new Reply(rs.getInt("postID"), rs.getString("body"), rs.getString("authorUsername"));
					reply.setReplyID(rs.getInt("replyID"));
					Timestamp ts = rs.getTimestamp("createdAt");
					if (ts != null) reply.setCreatedAt(ts.toLocalDateTime());
					replies.add(reply);
				}
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in getUnreadRepliesForPost: " + e.getMessage());
		}
		return replies;
	}

	/*******
	 * <p> Method: void closeConnection()</p>
	 *
	 * <p> Description: Closes the database statement and connection.</p>
	 *
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
