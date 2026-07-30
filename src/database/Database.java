package database;

import java.sql.*;
import java.time.LocalDateTime; // Import LocalDateTime for invitation code expiration functionality
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Thread;
import entityClasses.EvaluationParameter;
import entityClasses.Feedback;
import entityClasses.Request;
import entityClasses.RequestComment;

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
//		statement.execute("DROP ALL OBJECTS");
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
	    statement.execute(repliesTable);

	    // Add createdAt to existing tables if upgrading from an older schema
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS "
	    		+ "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS "
	    		+ "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
	    
	    // Add parentReplyID to existing tables if upgrading from an older schema
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS "
	    		+ "parentReplyID INT");
	    // Add hasReplies to existing tables if upgrading from an older schema
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS "		
	    		+ "hasReplies BOOL DEFAULT FALSE");
	    // Add numReplies to existing tables if upgrading from an older schema
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS "		
	    		+ "numReplies INT");
	    
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS isFlagged BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS flaggedBy VARCHAR(255)");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS flaggedAt TIMESTAMP");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS staffNote VARCHAR(1000)");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS isResolved BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS resolvedBy VARCHAR(255)");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS resolvedAt TIMESTAMP");

	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS isReviewed BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS reviewedBy VARCHAR(255)");
	    statement.execute("ALTER TABLE PostsDB ADD COLUMN IF NOT EXISTS reviewedAt TIMESTAMP");
 
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS isFlagged BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS flaggedBy VARCHAR(255)");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS flaggedAt TIMESTAMP");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS staffNote VARCHAR(1000)");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS isResolved BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS resolvedBy VARCHAR(255)");
	    statement.execute("ALTER TABLE RepliesDB ADD COLUMN IF NOT EXISTS resolvedAt TIMESTAMP");

	    // Tracks which replies each user has already read (for unreadCount)
	    String replyReadStatusTable = "CREATE TABLE IF NOT EXISTS ReplyReadStatusDB ("
	    		+ "replyID INT, "
	    		+ "readerUsername VARCHAR(255), "
	    		+ "PRIMARY KEY (replyID, readerUsername))";
	    statement.execute(replyReadStatusTable);

	    // Create the threads table
	    String threadsTable = "CREATE TABLE IF NOT EXISTS ThreadsDB ("
	    		+ "threadID    INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "name        VARCHAR(255) UNIQUE, "
	    		+ "description VARCHAR(1000), "
	    		+ "isDefault   BOOL DEFAULT FALSE, "
	    		+ "createdBy   VARCHAR(255), "
	    		+ "createdAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
	    statement.execute(threadsTable);
	    
	    // Create the EvaluationParameters table for STORY 2: Implement CRUD for Evaluation Parameters.
	    // Supports requirement for staff can create, read, update and delete grading parameterss used to evaluate
	    // student discussion performance.
	    String EvaluationParametersTable = "CREATE TABLE IF NOT EXISTS EvaluationParametersDB ("
	    		+ "parameterID		INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "name				VARCHAR(255), "
	    		+ "description		VARCHAR(1000),"
	    		+ "maxScore			DOUBLE,"
	    		+ "weight			DOUBLE)";
	    statement.execute(EvaluationParametersTable);

	    // Create the EvaluationScores table for STORY 3: Evaluate Student Discussion.
	    // One row per (studentUsername, paramID) pair. The UNIQUE constraint is what lets
	    // saveOrUpdateEvaluationScore() use an H2 MERGE to update an existing score in place
	    // instead of creating a duplicate when staff re-score a parameter.
	    String EvaluationScoresTable = "CREATE TABLE IF NOT EXISTS EvaluationScoresDB ("
	    		+ "scoreID          INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "studentUsername  VARCHAR(255), "
	    		+ "paramID          INT, "
	    		+ "staffUsername    VARCHAR(255), "
	    		+ "scoreValue       DOUBLE, "
	    		+ "feedback         VARCHAR(1000), "
	    		+ "scoredAt         TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    		+ "UNIQUE(studentUsername, paramID))";
	    statement.execute(EvaluationScoresTable);

	    // Seed the "General" thread on first run; idempotent on subsequent starts
	    statement.execute("INSERT INTO ThreadsDB (name, description, isDefault, createdBy) "
	    		+ "SELECT 'General', 'Default fallback thread', TRUE, 'system' "
	    		+ "WHERE NOT EXISTS (SELECT 1 FROM ThreadsDB WHERE name = 'General')");

	    // Create the requests table
	    String requestsTable = "CREATE TABLE IF NOT EXISTS RequestsDB ("
	    		+ "requestID         INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "requestorUsername VARCHAR(255), "
	    		+ "subject			 VARCHAR(255), "
	    		+ "description       VARCHAR(1000), "
	    		+ "status			 VARCHAR(255), "
	    		+ "assignedTo		 VARCHAR(255), "
	    		+ "isClosed          BOOL DEFAULT FALSE, "
	    		+ "adminNotes        VARCHAR(1000), "
	    		+ "closedRequestId   INT DEFAULT -1, "
	    		+ "createdAt         TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    		+ "closedAt          TIMESTAMP)";
	    statement.execute(requestsTable);
	    
	    // Create request comment table
	    String requestCommentTable = "CREATE TABLE IF NOT EXISTS RequestCommentDB ("
	    		+ "commentID         INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "requestID		 INT, "
	    		+ "commenterUsername VARCHAR(255), "
	    		+ "description       VARCHAR(1000), "
	    		+ "createdAt         TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
	    statement.execute(requestCommentTable);
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
 *  starting with Select User at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<User>");
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
	 * <p> Method: List String getAllInvitations() </p>
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
	 * <p> Method: void updateOneTimePassword(String username, String value) </p>
	 * 
	 * <p> Description: Update the password of a user given that user's username and the new
	 *		password.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param value is true or false depending on if the password is a one-time password
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
	 * <p> Method: boolean getUserObject(String username) </p>
	 * 
	 * <p> Description: Returns a User object will all the user's information
	 * for a given username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return User a User object that contains all the information for the
	 * specified username
	 *  
	 */
	// get the attributes for a specified user
	public User getUserObject(String username) {
		User userObject;
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
			rs.next();
	    	String theUsername = rs.getString(2);
	    	String password = rs.getString(3);
	    	String firstName = rs.getString(4);
	    	String middleName = rs.getString(5);
	    	String lastName = rs.getString(6);
	    	String preferredFirstName = rs.getString(7);
	    	String emailAddress = rs.getString(8);
	    	boolean adminRole = rs.getBoolean(9);
	    	boolean newRole1 = rs.getBoolean(10);
	    	boolean newRole2 = rs.getBoolean(11);
	    	boolean studentRole = rs.getBoolean(12);
	    	boolean instructorRole = rs.getBoolean(13);
	    	boolean staffRole = rs.getBoolean(14);
	    	userObject = new User(theUsername, password, firstName, middleName, lastName,
	    			preferredFirstName, emailAddress, adminRole, newRole1, newRole2);
	    	userObject.setStudentRole(studentRole);
	    	userObject.setInstructorRole(instructorRole);
	    	userObject.setStaffRole(staffRole);
	    } catch (SQLException e) {
	    	return null;
	    }
		return userObject;
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
			
			// Validate the thread exists in the database (dynamic — not hardcoded names)
			if (!threadExistsInDB(post.getThread()))
				throw new IllegalArgumentException("*** Error *** The specified thread does not exist.");

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
	 * @return a list of Post objects created from the database
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
	* <p> Method: permanentDeletePost </p>
	* 
	* <p> Description: Permanently deletes a post from the database. Used for semi-automated
	* test cases so the database does not become full of test posts </p>
	* 
	* @param postID specifies the ID of the post to delete.
	   * 
	*/
		public void permanentDeletePost(int postID) {
			String query = "DELETE FROM PostsDB WHERE postID = ?";
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
			String insertReply = "INSERT INTO RepliesDB (postID, body, authorUsername, createdAt, parentReplyID, hasReplies, numReplies) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertReply,
				Statement.RETURN_GENERATED_KEYS)) {
					pstmt.setInt(1, reply.getPostID());
					pstmt.setString(2, reply.getBody());
					pstmt.setString(3, reply.getAuthorUsername());
					pstmt.setTimestamp(4, Timestamp.valueOf(now));
					pstmt.setInt(5,  reply.getParentReplyID());
					pstmt.setBoolean(6,  reply.getHasReplies());
					pstmt.setInt(7,  reply.getNumReplies());
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
	 * @return a list of Reply objects created from the database
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
					int parentReplyID = rs.getInt("parentReplyID");
					boolean hasReplies = rs.getBoolean("hasReplies");
					int numReplies = rs.getInt("numReplies");
					Reply reply = new Reply(
						rs.getInt("postID"),
						rs.getString("body"),
						rs.getString("authorUsername")
						);
					reply.setReplyID(replyID);
					reply.setparentReplyID(parentReplyID);
					reply.setHasReplies(hasReplies);
					reply.setNumReplies(numReplies);
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
		 * <p> Method: updateHasReplies </p>
		 * 
		 * <p> Description: Updates the boolean value for hasReplies </p>
		 * 
		 * @param replyID specifies the ID of the reply to update.
		 * 
		 * @param hasReplies specifies the boolean value to set hasReplies to.
		 * 
		 */
		public void updateHasReplies(int replyID, boolean hasReplies) {
			String query = "UPDATE RepliesDB SET hasReplies = ? WHERE replyID = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setBoolean(1, hasReplies);
					pstmt.setInt(2, replyID);
					pstmt.executeUpdate();
				} catch (SQLException e) {
						System.err.println("*** ERROR *** Database error while updating reply: " 
				+ e.getMessage());
					}
		}
		
		/*******
		* <p> Method: updateNumReplies </p>
		* 
		* <p> Description: Updates the int value for numReplies </p>
		* 
		* @param replyID specifies the ID of the reply to update.
		* 
		* @param numReplies specifies the int value to set numReplies to.
		* 
		*/
		public void updateNumReplies(int replyID, int numReplies) {
			String query = "UPDATE RepliesDB SET numReplies = ? WHERE replyID = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setInt(1, numReplies);
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
	 * <p> Method: String getThread() </p>
	 *
	 * <p> Description: Gets the thread of the post specified by the postID. </p>
	 *
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 * @return a String containing the name of the thread the post is assigned to.
	 *
	 */
	public String getThread(int postID) throws SQLException {
		String query = "SELECT thread FROM PostsDB WHERE postID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("thread");
				}
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in getThread: " + e.getMessage());
		}
		return null;
	}
	
	/*******
	 * <p> Method: createThread(Thread thread) </p>
	 *
	 * <p> Description: Creates a new row in ThreadsDB using the thread parameter and sets
	 *  the database-generated threadID back onto the Thread object. </p>
	 *
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 * @param thread specifies the Thread object to be added to the database.
	 *
	 */
	public void createThread(Thread thread) throws SQLException {
		String nameErr = recognizers.PostReplyValidator.checkForValidThreadName(thread.getName());
		if (!nameErr.isEmpty())
			throw new IllegalArgumentException(nameErr);

		String descErr = recognizers.PostReplyValidator.checkForValidThreadDescription(thread.getDescription());
		if (!descErr.isEmpty())
			throw new IllegalArgumentException(descErr);

		if (threadExistsInDB(thread.getName()))
			throw new IllegalArgumentException("Error: A thread with that name already exists.");

		String insert = "INSERT INTO ThreadsDB (name, description, isDefault, createdBy, createdAt) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			LocalDateTime now = LocalDateTime.now();
			pstmt.setString(1, thread.getName());
			pstmt.setString(2, thread.getDescription());
			pstmt.setBoolean(3, thread.getIsDefault());
			pstmt.setString(4, thread.getCreatedBy());
			pstmt.setTimestamp(5, Timestamp.valueOf(now));
			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next())
					thread.setThreadID(rs.getInt(1));
			}
			thread.setCreatedAt(now);
		} catch (SQLException e) {
			System.err.println("Error: Database error while creating thread: " + e.getMessage());
			throw e;
		}
	}

	/*******
	 * <p> Method: readThread(int threadID) </p>
	 *
	 * <p> Description: Retrieves a single Thread object from ThreadsDB matching the
	 *  specified threadID, or null if no such thread exists. </p>
	 *
	 * @param threadID specifies the ID of the thread to retrieve.
	 *
	 * @return a Thread object matching the specified threadID, or null if not found.
	 *
	 */
	public Thread readThread(int threadID) {
		String query = "SELECT * FROM ThreadsDB WHERE threadID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, threadID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return mapRowToThread(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error: Database error in readThread: " + e.getMessage());
		}
		return null;
	}

	/*******
	 * <p> Method: readAllThreads() </p>
	 *
	 * <p> Description: Retrieves all Thread objects from ThreadsDB. </p>
	 *
	 * @return a List of all Thread objects currently stored.
	 *
	 */
	public List<Thread> readAllThreads() {
		List<Thread> list = new ArrayList<>();
		String query = "SELECT * FROM ThreadsDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next())
				list.add(mapRowToThread(rs));
		} catch (SQLException e) {
			System.err.println("Error: Database error in readAllThreads: " + e.getMessage());
		}
		return list;
	}

	/*******
	 * <p> Method: updateThread(int threadID, String newName, String newDescription) </p>
	 *
	 * <p> Description: Updates the name and description of an existing thread. Guarded:
	 *  the "General" thread (isDefault=true) may not be updated. </p>
	 *
	 * @param threadID specifies the ID of the thread to update.
	 *
	 * @param newName specifies the new name for the thread.
	 *
	 * @param newDescription specifies the new description for the thread.
	 *
	 * @throws IllegalArgumentException when the thread is General, the name is invalid,
	 *  or the new name is already taken by another thread.
	 *
	 * @throws SQLException when there is an issue executing the SQL command.
	 *
	 */
	public void updateThread(int threadID, String newName, String newDescription) throws SQLException {
		Thread existing = readThread(threadID);
		if (existing == null)
			throw new IllegalArgumentException("*** Error *** Thread not found.");

		if (existing.getIsDefault())
			throw new IllegalArgumentException("*** Error *** The 'General' thread cannot be updated.");

		String nameErr = recognizers.PostReplyValidator.checkForValidThreadName(newName);
		if (!nameErr.isEmpty())
			throw new IllegalArgumentException(nameErr);

		String descErr = recognizers.PostReplyValidator.checkForValidThreadDescription(newDescription);
		if (!descErr.isEmpty())
			throw new IllegalArgumentException(descErr);

		// Reject if the new name is already taken by a different thread
		String checkQuery = "SELECT COUNT(*) FROM ThreadsDB WHERE name = ? AND threadID <> ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
			pstmt.setString(1, newName);
			pstmt.setInt(2, threadID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0)
					throw new IllegalArgumentException("*** Error *** A thread with that name already exists.");
			}
		}

		String update = "UPDATE ThreadsDB SET name = ?, description = ? WHERE threadID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, newName);
			pstmt.setString(2, newDescription);
			pstmt.setInt(3, threadID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error: Database error while updating thread: " + e.getMessage());
			throw e;
		}
	}

	/*******
	 * <p> Method: deleteThread(int threadID) </p>
	 *
	 * <p> Description: Deletes a thread from ThreadsDB. Guarded: the "General" thread
	 *  (isDefault=true) may not be deleted. All posts belonging to the deleted thread
	 *  are reassigned to "General" before the thread row is removed. </p>
	 *
	 * @param threadID specifies the ID of the thread to delete.
	 *
	 * @throws IllegalArgumentException when the thread is General or not found.
	 *
	 * @throws SQLException when there is an issue executing the SQL command.
	 *
	 */
	public void deleteThread(int threadID) throws SQLException {
		Thread existing = readThread(threadID);
		if (existing == null)
			throw new IllegalArgumentException("Error: Thread not found.");

		if (existing.getIsDefault())
			throw new IllegalArgumentException("Error: The 'General' thread cannot be deleted.");

		// Migrate all posts in this thread to "General" before removing the thread
		String migrate = "UPDATE PostsDB SET thread = 'General' WHERE thread = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(migrate)) {
			pstmt.setString(1, existing.getName());
			pstmt.executeUpdate();
		}

		String delete = "DELETE FROM ThreadsDB WHERE threadID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
			pstmt.setInt(1, threadID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error: Database error while deleting thread: " + e.getMessage());
			throw e;
		}
	}

	/*******
	 * <p> Method: threadExistsInDB(String name) </p>
	 *
	 * <p> Description: Returns true if a thread with the given name exists in ThreadsDB.
	 *  Used by createPost and createThread for validation. </p>
	 *
	 * @param name specifies the thread name to look up
	 *
	 * @return true if the thread exists, false otherwise
	 *
	 */
	private boolean threadExistsInDB(String name) {
		if (name == null) return false;
		String query = "SELECT COUNT(*) FROM ThreadsDB WHERE name = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, name);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * <p> Method: mapRowToThread(ResultSet rs) </p>
	 *
	 * <p> Description: Maps the current row of a ResultSet from ThreadsDB to a Thread
	 *  object. Caller is responsible for advancing the ResultSet cursor. </p>
	 *
	 * @param rs specifies the ResultSet positioned on the row to map
	 *
	 * @return a Thread object populated from the row
	 *
	 * @throws SQLException when a column cannot be read
	 *
	 */
	private Thread mapRowToThread(ResultSet rs) throws SQLException {
		Thread t = new Thread(rs.getString("name"), rs.getString("description"), rs.getString("createdBy"));
		t.setThreadID(rs.getInt("threadID"));
		t.setIsDefault(rs.getBoolean("isDefault"));
		Timestamp ts = rs.getTimestamp("createdAt");
		if (ts != null)
			t.setCreatedAt(ts.toLocalDateTime());
		return t;
	}


	/*******
	 * <p> Method: createEvaluationParameter(EvaluationParameter p) </p>
	 *
	 * <p> Description: Creates a new row in EvaluationParametersDB using the parameter
	 *  object and sets the database-generated paramID back onto the EvaluationParameter
	 *  object. </p>
	 *
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 * @param param specifies the EvaluationParameter object to be added to the database.
	 *
	 * @see EvaluationParameterCrudTest#testCreateValidParameter()
	 * 
	 * @see EvaluationParameterCrudTest#testUniqueIDs()
	 * 
	 */
	
	public void createEvaluationParameter(EvaluationParameter param) throws SQLException {
		String insertEvaluationParameter = "INSERT INTO EvaluationParametersDB (name, description, maxScore, weight) "
			+ "VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertEvaluationParameter,
				Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, param.getName());
			pstmt.setString(2, param.getDescription());
			pstmt.setDouble(3, param.getMaxScore());
			pstmt.setDouble(4, param.getWeight());
			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					param.setParamID(rs.getInt(1));
				}
			}
		} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error while creating evaluation parameter: "
							+ e.getMessage());
					throw e;
				}
	}

	/*******
	 * <p> Method: readEvaluationParameter(int paramID) </p>
	 *
	 * <p> Description: Satisfies Read portion of Story 2: Implement CRUD for Staff
	 *  Parameters. Retrieves a single EvaluationParameter object from
	 *  EvaluationParametersDB matching the specified paramID, or null if no such
	 *  parameter exists. </p>
	 *
	 * @param paramID specifies the ID of the parameter to retrieve.
	 *
	 * @return an EvaluationParameter object matching the specified paramID, or null if
	 *  not found.
	 *
	 * @see EvaluationParameterCrudTest#testReadParameter()
	 * 
	 * @see EvaluationParameterCrudTest#testRaedNonexistentID()
	 * 
	 */
	public EvaluationParameter readEvaluationParameter(int paramID) {
	    String query = "SELECT * FROM EvaluationParametersDB WHERE parameterID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, paramID);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                EvaluationParameter param = new EvaluationParameter(
	                    rs.getString("name"),
	                    rs.getString("description"),
	                    rs.getDouble("maxScore"),
	                    rs.getDouble("weight")
	                );
	                param.setParamID(paramID);
	                return param;
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** Database error while reading parameter: "
	                + e.getMessage());
	    }
	    return null;
	}

	/*******
	 * <p> Method: readAllEvaluationParameters() </p>
	 *
	 * <p> Description: Retrieves all EvaluationParameter objects stored in the EvaluationParametersDB table. This
	 * satisfies the Read portion of STORY 2: Implementation of CRUD for Evaluation Parameters. Each database row
	 * is converted into an EvaluationParameter object via the constructor, with the database parameter ID set afterward </p>
	 *
	 * @return a List of all EvaluationParameter objects currently stored. An empty list returned if no
	 * parameters exist.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 */
	public List<EvaluationParameter> readAllEvaluationParameters() throws SQLException {
		List<EvaluationParameter> allParams = new ArrayList<>();
		
		String query = "SELECT * FROM EvaluationParametersDB";
		
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			int paramID = rs.getInt("parameterID");
			String name = rs.getString("name");
			String description = rs.getString("description");
			double maxScore = rs.getDouble("maxScore");
			double weight = rs.getDouble("weight");

			EvaluationParameter newParam  = new EvaluationParameter(
						name,
						description,
						maxScore,
						weight
					);
			
			newParam.setParamID(paramID);

			allParams.add(newParam);
		}
		
		return allParams;		
	}

	/*******
	 * <p> Method: updateEvaluationParameter(int paramID, String newName,
	 *  String newDescription, double maxScore, double weight) </p>
	 *
	 * <p> Description: Updates the name, description, maxScore, and weight of an existing
	 *  evaluation parameter in EvaluationParametersDB. </p>
	 *
	 * @param paramID specifies the ID of the parameter to update.
	 *
	 * @param newName specifies the new name for the parameter.
	 *
	 * @param newDescription specifies the new description for the parameter.
	 *
	 * @param newMaxScore specifies the new maximum score for the parameter.
	 *
	 * @param newWeight specifies the new weight for the parameter (1-10).
	 * 
	 * @see EvaluationParameterCrudTest#testUpdateParameter()
	 * 
	 * @see EvaluationParameterCrudTest#testUpdateNonexistentID()
	 * 
	 * @see EvaluationParameterCrudTest#testUpdateRejectsInvalidData()
	 *
	 */
	public boolean updateEvaluationParameter(int paramID, String newName, String newDescription,
	        double newMaxScore, double newWeight) {
	    if (paramID <= 0) return false;

	    String query = "UPDATE EvaluationParametersDB SET name = ?, description = ?, maxScore = ?, weight = ? "
	            + "WHERE parameterID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newName);
	        pstmt.setString(2, newDescription);
	        pstmt.setDouble(3, newMaxScore);
	        pstmt.setDouble(4, newWeight);
	        pstmt.setInt(5, paramID);
	        int rowsUpdated = pstmt.executeUpdate();
	        return rowsUpdated > 0;
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** Database error while updating parameter: " + e.getMessage());
	        return false;
	    }
	}

	/*******
	 * <p> Method: deleteEvaluationParameter(int paramID) </p>
	 *
	 * <p> Description: Permanently removes an evaluation parameter from
	 *  EvaluationParametersDB. </p>
	 *
	 * @param paramID specifies the ID of the parameter to delete.
	 *
	 * @see EvaluationParameterCrudTest#testDeleteParameter()
	 * 
	 * @see EvaluationParameterCrudTest#testDeleteNonexistentID()
	 * 
	 * @see EvaluationParameterCrudTest#testDeleteDoesNotAffectOtherRows()
	 *
	 */
	public boolean deleteEvaluationParameter(int paramID) {
	    if (paramID <= 0) return false;

	    String query = "DELETE FROM EvaluationParametersDB WHERE parameterID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, paramID);
	        int rowsDeleted = pstmt.executeUpdate();
	        return rowsDeleted > 0;
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** Database error while deleting parameter: " + e.getMessage());
	        return false;
	    }
	}


	/*******
	 * <p> Method: getStudentUserList() </p>
	 *
	 * <p> Description: Returns the usernames of every user with studentRole=TRUE, for
	 *  populating the student selector on the Evaluate Student Discussion screen
	 *  (STORY 3, criterion 1: "A staff user can select a student from a list").
	 *  Mirrors the "&lt;User&gt;" placeholder pattern already used by getUserList(),
	 *  using "&lt;Student&gt;" instead so the combo box always has a neutral default
	 *  selection. </p>
	 *
	 * @return a list of student usernames prefixed with a "&lt;Student&gt;" placeholder;
	 *  a list containing only the placeholder if the query fails
	 *
	 */
	public List<String> getStudentUserList() {
		List<String> studentList = new ArrayList<String>();
		studentList.add("<Student>");
		String query = "SELECT userName FROM userDB WHERE studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				studentList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in getStudentUserList: " + e.getMessage());
		}
		return studentList;
	}

	/*******
	 * <p> Method: saveOrUpdateEvaluationScore(String studentUsername, int paramID,
	 *  String staffUsername, double scoreValue, double maxScore) </p>
	 *
	 * <p> Description: Saves a staff-assigned score for a student on a parameter,
	 *  satisfying STORY 3 criteria 2 (assign a score) and 3 (update an existing
	 *  score). Reuses the EvaluationScore constructor purely for its range and
	 *  empty-username validation -- the same pattern updateEvaluationParameter()
	 *  uses -- then performs an H2 MERGE keyed on (studentUsername, paramID), so
	 *  re-scoring the same parameter updates the existing row in place instead of
	 *  creating a duplicate. </p>
	 *
	 * @param studentUsername specifies the student being scored
	 *
	 * @param paramID specifies the EvaluationParameter being scored
	 *
	 * @param staffUsername specifies the staff member assigning the score
	 *
	 * @param scoreValue specifies the raw score being assigned
	 *
	 * @param maxScore specifies the maximum score allowed for this parameter, used
	 *  only to validate scoreValue before the write
	 *
	 * @throws IllegalArgumentException if the score or either username fails the
	 *  EvaluationScore constructor's validation
	 *
	 * @throws SQLException when there is an issue creating the SQL command or
	 *  executing it
	 *
	 * @see tests.EvaluateStudentDiscussionTest#testSaveNewScorePersists()
	 * @see tests.EvaluateStudentDiscussionTest#testReScoringUpdatesInPlace()
	 *
	 */
	public void saveOrUpdateEvaluationScore(String studentUsername, int paramID,
			String staffUsername, double scoreValue, double maxScore) throws SQLException {
		// Reuse the constructor purely for its validation; the object itself is discarded
		new entityClasses.EvaluationScore(studentUsername, paramID, staffUsername, scoreValue, maxScore);

		String merge = "MERGE INTO EvaluationScoresDB "
				+ "(studentUsername, paramID, staffUsername, scoreValue, scoredAt) "
				+ "KEY(studentUsername, paramID) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(merge)) {
			pstmt.setString(1, studentUsername);
			pstmt.setInt(2, paramID);
			pstmt.setString(3, staffUsername);
			pstmt.setDouble(4, scoreValue);
			pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error while saving evaluation score: "
					+ e.getMessage());
			throw e;
		}
	}

	/*******
	 * <p> Method: readScoresForStudent(String studentUsername) </p>
	 *
	 * <p> Description: Retrieves every EvaluationScore saved for the specified
	 *  student, satisfying STORY 3 criterion 3 (viewing previously saved scores). </p>
	 *
	 * @param studentUsername specifies the student whose scores should be retrieved
	 *
	 * @return a List of EvaluationScore objects for this student; empty if none
	 *  have been saved yet or the query fails
	 *
	 */
	public List<entityClasses.EvaluationScore> readScoresForStudent(String studentUsername) {
		List<entityClasses.EvaluationScore> result = new ArrayList<entityClasses.EvaluationScore>();
		String query = "SELECT * FROM EvaluationScoresDB WHERE studentUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, studentUsername);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					result.add(mapRowToEvaluationScore(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error while reading scores for student: "
					+ e.getMessage());
		}
		return result;
	}

	/*******
	 * <p> Method: readAllEvaluationScores() </p>
	 *
	 * <p> Description: Retrieves every EvaluationScore in the database. Used to
	 *  reconstruct a complete EvaluationScoreList, for example when auditing all
	 *  scores across every student. </p>
	 *
	 * @return a List of every EvaluationScore currently stored; empty if the query
	 *  fails
	 *
	 */
	public List<entityClasses.EvaluationScore> readAllEvaluationScores() {
		List<entityClasses.EvaluationScore> result = new ArrayList<entityClasses.EvaluationScore>();
		String query = "SELECT * FROM EvaluationScoresDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				result.add(mapRowToEvaluationScore(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error while reading all evaluation scores: "
					+ e.getMessage());
		}
		return result;
	}

	/*******
	 * <p> Method: mapRowToEvaluationScore(ResultSet rs) </p>
	 *
	 * <p> Description: Maps the current row of a ResultSet from EvaluationScoresDB
	 *  to an EvaluationScore object. Caller is responsible for advancing the
	 *  ResultSet cursor. The row's own scoreValue is passed as both the score and
	 *  the constructor's maxScore argument: a value already accepted by the
	 *  database is by definition within whatever range was validated at write
	 *  time, so this satisfies the constructor's guard clause without a second
	 *  lookup of EvaluationParametersDB purely to reconstruct the object. The real
	 *  maxScore used for grading always comes from EvaluationParameter, not from
	 *  this reconstructed value. </p>
	 *
	 * @param rs specifies the ResultSet positioned on the row to map
	 *
	 * @return an EvaluationScore object populated from the row
	 *
	 * @throws SQLException when a column cannot be read
	 *
	 */
	private entityClasses.EvaluationScore mapRowToEvaluationScore(ResultSet rs) throws SQLException {
		double scoreValue = rs.getDouble("scoreValue");
		entityClasses.EvaluationScore score = new entityClasses.EvaluationScore(
				rs.getString("studentUsername"),
				rs.getInt("paramID"),
				rs.getString("staffUsername"),
				scoreValue,
				scoreValue);
		score.setScoreID(rs.getInt("scoreID"));
		Timestamp ts = rs.getTimestamp("scoredAt");
		if (ts != null) score.setScoredAt(ts.toLocalDateTime());
		return score;
	}


	/*******
	 * <p> Method: createRequest(Request request) </p>
	 *
	 * <p> Description: Creates a new row in RequestsDB using the request parameter and
	 *  sets the database-generated requestID back onto the Request object. </p>
	 *
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 * @param request specifies the Request object to be added to the database.
	 *
	 */
	public void createRequest(Request request) throws SQLException {
		LocalDateTime now = LocalDateTime.now();
		String insertRequest = "INSERT INTO RequestsDB (requestorUsername, subject, description, "
			+ "status, assignedTo) "
			+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertRequest,
			Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, request.getRequestorUsername());
				pstmt.setString(2, request.getSubject());
				pstmt.setString(3, request.getDescription());
				pstmt.setString(4, request.getStatus());
				pstmt.setString(5, request.getAssignedTo());
				pstmt.executeUpdate();

		try (ResultSet rs = pstmt.getGeneratedKeys()) {
			if (rs.next()) {
				request.setRequestID(rs.getInt(1));
			}
		}
		request.setCreatedAt(now);
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while creating request: "
						+ e.getMessage());
				throw e;
			}
	}

	/*******
	 * <p> Method: readRequest(int requestID) </p>
	 *
	 * <p> Description: Retrieves a single Request object from RequestsDB matching the
	 *  specified requestID, or null if no such request exists. </p>
	 *
	 * @param requestID specifies the ID of the request to retrieve.
	 *
	 * @return a Request object matching the specified requestID, or null if not found.
	 *
	 */
	public Request readRequest(int requestID) throws SQLException {	
		Request requestObject = null;
		
		String query = "SELECT * FROM RequestsDB";
		
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
			
		if (rs.wasNull()) {
			return null;
		}
		else {
			while (rs.next()) {
				int requestId = rs.getInt("requestID");
				String requestorUsername = rs.getString("requestorUsername");
				String subject = rs.getString("subject");
				String description = rs.getString("description");
				String status = rs.getString("status");
				String assignedTo = rs.getString("assignedTo");
				boolean isClosed = rs.getBoolean("isClosed");
				String adminNotes = rs.getString("adminNotes");
				int closedRequestId = rs.getInt("closedRequestId");
				Timestamp createdAt = rs.getTimestamp("createdAt");
				Timestamp closedAt = rs.getTimestamp("closedAt");
				requestObject = new Request(
					requestorUsername,
					subject,
					description
					);
				requestObject.setRequestID(requestId);
				requestObject.setStatus(status);
				requestObject.setAssignedTo(assignedTo);
				requestObject.setIsClosed(isClosed);
				requestObject.setAdminNotes(adminNotes);
				requestObject.setClosedRequestId(closedRequestId);
				requestObject.setCreatedAt(createdAt.toLocalDateTime());
				if (closedAt != null) {
					requestObject.setClosedAt(closedAt.toLocalDateTime());
				}
			}
		}
		
		return requestObject;
	}

	/*******
	 * <p> Method: readAllRequests() </p>
	 *
	 * <p> Description: Retrieves all Request objects from RequestsDB. </p>
	 *
	 * @return a List of all Request objects currently stored.
	 *
	 */
	public List<Request> readAllRequests() throws SQLException {
		List<Request> requestObjects = new ArrayList<>();
		
		String query = "SELECT * FROM RequestsDB";
			
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
			
		if (rs.wasNull()) {
			return requestObjects;
		}
		else {
			while (rs.next()) {
				int requestID = rs.getInt("requestID");
				String requestorUsername = rs.getString("requestorUsername");
				String subject = rs.getString("subject");
				String description = rs.getString("description");
				String status = rs.getString("status");
				String assignedTo = rs.getString("assignedTo");
				boolean isClosed = rs.getBoolean("isClosed");
				String adminNotes = rs.getString("adminNotes");
				int closedRequestId = rs.getInt("closedRequestId");
				Timestamp createdAt = rs.getTimestamp("createdAt");
				Timestamp closedAt = rs.getTimestamp("closedAt");
				Request request = new Request(
					requestorUsername,
					subject,
					description
					);
				request.setRequestID(requestID);
				request.setStatus(status);
				request.setAssignedTo(assignedTo);
				request.setIsClosed(isClosed);
				request.setAdminNotes(adminNotes);
				request.setClosedRequestId(closedRequestId);
				request.setCreatedAt(createdAt.toLocalDateTime());
				if (closedAt != null) {
					request.setClosedAt(closedAt.toLocalDateTime());
				}
				requestObjects.add(request);
			}
		}
			
		return requestObjects;
		}	

	/*******
	 * <p> Method: updateRequest(int requestID, String newDescription) </p>
	 *
	 * <p> Description: Updates the description of an existing open request in RequestsDB.
	 *  Only open requests may be updated. </p>
	 *
	 * @param requestID specifies the ID of the request to update.
	 *
	 * @param newDescription specifies the new description for the request.
	 *
	 */
	public void updateRequest(int requestID, String newDescription) {
	}
	
	/*******
	 * <p> Method: closeRequest(int requestID, String assignedTo, String adminNotes) </p>
	 *
	 * <p> Description: Closes a requests and updates its status in RequestsDB. </p>
	 *
	 * @param requestID specifies the ID of the request to be closed.
	 *
	 *@param assignedTo specifies the user that the request was closed by.
	 *
	 * @param adminNotes specifies the adminNotes for the request.
	 *
	 */
	public void closeRequest(int requestID, String assignedTo, String adminNotes) {
		updateRequestIsClosed(requestID, true);
		updateRequestStatus(requestID, "Closed");
		updateRequestAssigned(requestID, assignedTo);
		updateAdminNotes(requestID, adminNotes);
		
	}
	
	/*******
	 * <p> Method: updateRequestIsClosed(Request request, boolean value) </p>
	 *
	 * <p> Description: Updates the isClosed value of an existing request in RequestsDB. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param value specifies the new isClosed value for the request.
	 *
	 */
	public void updateRequestIsClosed(int requestID, boolean value) {
		String query = "UPDATE RequestsDB SET isClosed = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, value);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******
	 * <p> Method: updateClosedRequestID(int requestID, int closedRequestID) </p>
	 *
	 * <p> Description: Updates the closedRequest ID of a re-opened request. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param closedRequestID specifies the request ID of the re-opened request
	 *
	 */
	public void updateClosedRequestID(int requestID, int closedRequestID) {
		String query = "UPDATE RequestsDB SET closedRequestId = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, closedRequestID);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******
	 * <p> Method: updateRequestStatus(Request request, String newStatus) </p>
	 *
	 * <p> Description: Updates the status of an existing open request in RequestsDB.
	 *  Only open requests may be updated. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param newStatus specifies the new status for the request.
	 *
	 */
	public void updateRequestStatus(int requestID, String newStatus) {
		String query = "UPDATE RequestsDB SET status = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newStatus);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******
	 * <p> Method: updateRequestAssigned(Request request, String newAssigned) </p>
	 *
	 * <p> Description: Updates the assignedTo value of an existing open request in RequestsDB.
	 *  Only open requests may be updated. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param newAssigned specifies the new assignedTo value for the request.
	 *
	 */
	public void updateRequestAssigned(int requestID, String newAssigned) {
		String query = "UPDATE RequestsDB SET assignedTo = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newAssigned);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******
	 * <p> Method: updateRequestAdminNotes(Request request, String newAdminNotes) </p>
	 *
	 * <p> Description: Updates the adminNotes value of an existing request in RequestsDB. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param newAdminNotes specifies the new adminNotes value for the request.
	 *
	 */
	public void updateAdminNotes(int requestID, String newAdminNotes) {
		String query = "UPDATE RequestsDB SET adminNotes = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newAdminNotes);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******
	 * <p> Method: updateRequestClosedAt(Request request, LocalDateTime closedAt) </p>
	 *
	 * <p> Description: Updates the closedAt value of an existing request in RequestsDB. </p>
	 *
	 * @param requestID specifies the request to update.
	 *
	 * @param closedAt specifies the new closedAt value for the request.
	 *
	 */
	public void updateRequestClosedAt(int requestID, Timestamp closedAt) {
		String query = "UPDATE RequestsDB SET closedAt = ? WHERE requestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setTimestamp(1, closedAt);
			pstmt.setInt(2, requestID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	/*******
	 * <p> Method: deleteRequest(int requestID) </p>
	 *
	 * <p> Description: Permanently removes a request from RequestsDB. </p>
	 *
	 * @param requestID specifies the ID of the request to delete.
	 *
	 */
	public void deleteRequest(int requestID) {
	}

	
	/*******
	 * <p> Method: createRequestComment(RequestComment comment) </p>
	 *
	 * <p> Description: Creates a new row in RequestsDB using the request parameter and
	 *  sets the database-generated requestID back onto the Request object. </p>
	 *
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 *
	 * @param comment specifies the comment to be added to the database
	 *
	 */
	public void createRequestComment(RequestComment comment) throws SQLException {
		LocalDateTime now = LocalDateTime.now();
		String insertRequestComment = "INSERT INTO RequestCommentDB (requestID, commenterUsername, description) "
			+ "VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertRequestComment,
			Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setInt(1, comment.getRequestID());
				pstmt.setString(2, comment.getCommenterUsername());
				pstmt.setString(3, comment.getDescription());
				pstmt.executeUpdate();

		try (ResultSet rs = pstmt.getGeneratedKeys()) {
			if (rs.next()) {
				comment.setRequestID(rs.getInt(1));
			}
		}
		comment.setCreatedAt(now);
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error while creating request: "
						+ e.getMessage());
				throw e;
			}
	}
	
	
	/*******
	 * <p> Method: readAllRequestComments() </p>
	 *
	 * <p> Description: Retrieves all RequestComment objects from RequestCommentDB. </p>
	 *
	 * @return a List of all RequestComment objects currently stored.
	 *
	 */
	public List<RequestComment> readAllRequestComments() throws SQLException {
		List<RequestComment> requestCommentObjects = new ArrayList<>();
		
		String query = "SELECT * FROM RequestCommentDB";
			
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
			
		if (rs.wasNull()) {
			return requestCommentObjects;
		}
		else {
			while (rs.next()) {
				int requestID = rs.getInt("requestID");
				String commenterUsername = rs.getString("commenterUsername");
				String description = rs.getString("description");
				Timestamp createdAt = rs.getTimestamp("createdAt");
				RequestComment comment = new RequestComment(
					requestID,
					commenterUsername,
					description
					);
				comment.setCreatedAt(createdAt.toLocalDateTime());
				requestCommentObjects.add(comment);
			}
		}
			
		return requestCommentObjects;
		}	
	
	/*******
	 * <p> Method: readAllPostsWithStaffFields() </p>
	 *
	 * <p> Description: Retrieves every non deleted Post from PostsDB, including the
	 *  flag/note/resolve/review fields, for the Staff Review screen. </p>
	 *
	 * @return a List of all non deleted Post objects, including staff fields.
	 *
	 */
	public List<Post> readAllPostsWithStaffFields() {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT * FROM PostsDB WHERE isDeleted = FALSE ORDER BY createdAt DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				posts.add(mapRowToPostWithStaffFields(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readAllPostsWithStaffFields: " + e.getMessage());
		}
		return posts;
	}
	
	/*******
	 * <p> Method: readRepliesForPostWithStaffFields(int postID) </p>
	 *
	 * <p> Description: Retrieves all Reply objects for a given post, including the
	 *  staff flag/note/resolve fields. </p>
	 *
	 * @param postID specifies the post whose replies should be retrieved.
	 *
	 * @return a List of Reply objects for the specified post, including staff fields.
	 *
	 */
	public List<Reply> readRepliesForPostWithStaffFields(int postID) {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT * FROM RepliesDB WHERE postID = ? ORDER BY createdAt ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					replies.add(mapRowToReplyWithStaffFields(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readRepliesForPostWithStaffFields: " + e.getMessage());
		}
		return replies;
	}

	/*******
	 * <p> Method: flagPost(int postID, String staffUsername) </p>
	 *
	 * <p> Description: Marks the specified post as flagged and records which staff member
	 *  flagged it by setting isFlagged=true and flaggedBy in PostsDB. </p>
	 *
	 * @param postID specifies the ID of the post to flag.
	 *
	 * @param staffUsername specifies the username of the staff member flagging the post.
	 *
	 */
	public void flagPost(int postID, String staffUsername) {
		String query = "UPDATE PostsDB SET isFlagged = TRUE, flaggedBy = ?, flaggedAt = ? WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, staffUsername);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(3, postID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in flagPost: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: setPostStaffNote(int postID, String note) </p>
	 *
	 * <p> Description: Sets a private staff annotation on the specified post in PostsDB.
	 *  The note is not visible to students. </p>
	 *
	 * @param postID specifies the ID of the post to annotate.
	 *
	 * @param note specifies the private staff note to attach to the post.
	 *
	 */
	public void setPostStaffNote(int postID, String note) {
		String query = "UPDATE PostsDB SET staffNote = ? WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, note);
			pstmt.setInt(2, postID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in setPostStaffNote: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: resolvePost(int postID, String staffUsername) </p>
	 *
	 * <p> Description: Marks the specified post as resolved and records which staff member
	 *  resolved it by setting isResolved=true and resolvedBy in PostsDB. </p>
	 *
	 * @param postID specifies the ID of the post to resolve.
	 *
	 * @param staffUsername specifies the username of the staff member resolving the post.
	 *
	 */
	public void resolvePost(int postID, String staffUsername) {
		String query = "UPDATE PostsDB SET isResolved = TRUE, resolvedBy = ?, resolvedAt = ? WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, staffUsername);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(3, postID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in resolvePost: " + e.getMessage());
		}
	}
	
	/*******
	 * <p> Method: markPostReviewed(int postID, String staffUsername) </p>
	 *
	 * <p> Description: Marks the specified post as reviewed and records which staff member
	 *  reviewed it and when. </p>
	 *
	 */
	public void markPostReviewed(int postID, String staffUsername) {
		String query = "UPDATE PostsDB SET isReviewed = TRUE, reviewedBy = ?, reviewedAt = ? WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, staffUsername);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(3, postID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in markPostReviewed: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: readFlaggedPosts() </p>
	 *
	 * <p> Description: Retrieves all Post objects from PostsDB where isFlagged is true. </p>
	 *
	 * @return a List of all flagged Post objects.
	 *
	 */
	public List<Post> readFlaggedPosts() {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT * FROM PostsDB WHERE isFlagged = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				posts.add(mapRowToPostWithStaffFields(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readFlaggedPosts: " + e.getMessage());
		}
		return posts;
	}


	/*******
	 * <p> Method: flagReply(int replyID, String staffUsername) </p>
	 *
	 * <p> Description: Marks the specified reply as flagged and records which staff member
	 *  flagged it by setting isFlagged=true and flaggedBy in RepliesDB. </p>
	 *
	 */
	public void flagReply(int replyID, String staffUsername) {
		String query = "UPDATE RepliesDB SET isFlagged = TRUE, flaggedBy = ?, flaggedAt = ? WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, staffUsername);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(3, replyID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in flagReply: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: setReplyStaffNote(int replyID, String note) </p>
	 *
	 * <p> Description: Sets a private staff annotation on the specified reply in RepliesDB.
	 *  The note is not visible to students. </p>
	 *
	 * @param replyID specifies the ID of the reply to annotate.
	 *
	 * @param note specifies the private staff note to attach to the reply.
	 *
	 */
	public void setReplyStaffNote(int replyID, String note) {
		String query = "UPDATE RepliesDB SET staffNote = ? WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, note);
			pstmt.setInt(2, replyID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in setReplyStaffNote: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: resolveReply(int replyID, String staffUsername) </p>
	 *
	 * <p> Description: Marks the specified reply as resolved and records which staff member
	 *  resolved it by setting isResolved=true and resolvedBy in RepliesDB. </p>
	 *
	 * @param replyID specifies the ID of the reply to resolve.
	 *
	 * @param staffUsername specifies the username of the staff member resolving the reply.
	 *
	 */
	public void resolveReply(int replyID, String staffUsername) {
		String query = "UPDATE RepliesDB SET isResolved = TRUE, resolvedBy = ?, resolvedAt = ? WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, staffUsername);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(3, replyID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in resolveReply: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: readFlaggedReplies() </p>
	 *
	 * <p> Description: Retrieves all Reply objects from RepliesDB where isFlagged is true. </p>
	 *
	 * @return a List of all flagged Reply objects.
	 *
	 */
	public List<Reply> readFlaggedReplies() {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT * FROM RepliesDB WHERE isFlagged = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				replies.add(mapRowToReplyWithStaffFields(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readFlaggedReplies: " + e.getMessage());
		}
		return replies;
	}
	
	/*******
	 * <p> Method: mapRowToPostWithStaffFields(ResultSet rs) </p>
	 *
	 * <p> Description: Maps the current row of a ResultSet from PostsDB to a Post object,
	 *  including the staff flag/note/resolve/review fields. </p>
	 *
	 */
	private Post mapRowToPostWithStaffFields(ResultSet rs) throws SQLException {
		Post post = new Post(
			rs.getString("title"),
			rs.getString("body"),
			rs.getString("authorUsername"),
			rs.getString("thread"));
		post.setPostID(rs.getInt("postID"));
		post.setIsDeleted(rs.getBoolean("isDeleted"));
		Timestamp createdTs = rs.getTimestamp("createdAt");
		if (createdTs != null) post.setCreatedAt(createdTs.toLocalDateTime());
 
		// Staff-only fields — requires the matching setters to exist on Post.java
		post.setIsFlagged(rs.getBoolean("isFlagged"));
		post.setFlaggedBy(rs.getString("flaggedBy"));
		Timestamp flaggedTs = rs.getTimestamp("flaggedAt");
		if (flaggedTs != null) post.setFlaggedAt(flaggedTs.toLocalDateTime());
		post.setStaffNote(rs.getString("staffNote"));
		post.setIsResolved(rs.getBoolean("isResolved"));
		post.setResolvedBy(rs.getString("resolvedBy"));
		Timestamp resolvedTs = rs.getTimestamp("resolvedAt");
		if (resolvedTs != null) post.setResolvedAt(resolvedTs.toLocalDateTime());
		post.setIsReviewed(rs.getBoolean("isReviewed"));
		post.setReviewedBy(rs.getString("reviewedBy"));
		Timestamp reviewedTs = rs.getTimestamp("reviewedAt");
		if (reviewedTs != null) post.setReviewedAt(reviewedTs.toLocalDateTime());
 
		return post;
	}
	
	/*******
	 * <p> Method: mapRowToReplyWithStaffFields(ResultSet rs) </p>
	 *
	 * <p> Description: Maps the current row of a ResultSet from RepliesDB to a Reply
	 *  object.  </p>
	 *
	 */
	private Reply mapRowToReplyWithStaffFields(ResultSet rs) throws SQLException {
		Reply reply = new Reply(
			rs.getInt("postID"),
			rs.getString("body"),
			rs.getString("authorUsername"));
		reply.setReplyID(rs.getInt("replyID"));
		reply.setparentReplyID(rs.getInt("parentReplyID"));
		reply.setHasReplies(rs.getBoolean("hasReplies"));
		reply.setNumReplies(rs.getInt("numReplies"));
		Timestamp createdTs = rs.getTimestamp("createdAt");
		if (createdTs != null) reply.setCreatedAt(createdTs.toLocalDateTime());
 
		// Staff-only fields — requires the matching setters to exist on Reply.java
		reply.setIsFlagged(rs.getBoolean("isFlagged"));
		reply.setFlaggedBy(rs.getString("flaggedBy"));
		Timestamp flaggedTs = rs.getTimestamp("flaggedAt");
		if (flaggedTs != null) reply.setFlaggedAt(flaggedTs.toLocalDateTime());
		reply.setStaffNote(rs.getString("staffNote"));
		reply.setIsResolved(rs.getBoolean("isResolved"));
		reply.setResolvedBy(rs.getString("resolvedBy"));
		Timestamp resolvedTs = rs.getTimestamp("resolvedAt");
		if (resolvedTs != null) reply.setResolvedAt(resolvedTs.toLocalDateTime());
 
		return reply;
	}
	
	/*******
	 * <p> Method: createFeedback(Feedback feedback) </p>
	 *
	 * <p> Description: Creates a new row in FeedbackDB using the feedback parameter, and
	 *  sets the generated feedbackID back onto the Feedback object. </p>
	 *
	 */
	public void createFeedback(Feedback feedback) throws SQLException {
		LocalDateTime now = LocalDateTime.now();
		String insert = "INSERT INTO FeedbackDB (postID, staffUsername, targetUsername, body, createdAt) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, feedback.getPostID());
			pstmt.setString(2, feedback.getStaffUsername());
			pstmt.setString(3, feedback.getTargetUsername());
			pstmt.setString(4, feedback.getBody());
			pstmt.setTimestamp(5, Timestamp.valueOf(now));
			pstmt.executeUpdate();
 
			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next())
					feedback.setFeedbackID(rs.getInt(1));
			}
			feedback.setCreatedAt(now);
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error while creating feedback: " + e.getMessage());
			throw e;
		}
	}
	
	/*******
	 * <p> Method: readFeedbackForPost(int postID) </p>
	 *
	 * <p> Description: Retrieves all Feedback objects addressed to a given post, ordered
	 *  oldest first. Intended for staff views. </p>
	 *
	 */
	public List<Feedback> readFeedbackForPost(int postID) {
		List<Feedback> list = new ArrayList<>();
		String query = "SELECT * FROM FeedbackDB WHERE postID = ? ORDER BY createdAt ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next())
					list.add(mapRowToFeedback(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readFeedbackForPost: " + e.getMessage());
		}
		return list;
	}

	/*******
	 * <p> Method: readFeedbackForTargetUser(String targetUsername) </p>
	 *
	 * <p> Description: Retrieves all Feedback objects addressed to a specific user, ordered
	 *  oldest first.  </p>
	 *
	 */
	public List<Feedback> readFeedbackForTargetUser(String targetUsername) {
		List<Feedback> list = new ArrayList<>();
		String query = "SELECT * FROM FeedbackDB WHERE targetUsername = ? ORDER BY createdAt ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, targetUsername);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next())
					list.add(mapRowToFeedback(rs));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in readFeedbackForTargetUser: " + e.getMessage());
		}
		return list;
	}
	
	/*******
	 * <p> Method: deleteFeedback(int feedbackID) </p>
	 *
	 * <p> Description: Permanently removes a feedback entry from FeedbackDB. </p>
	 *
	 */
	public boolean deleteFeedback(int feedbackID) {
		String query = "DELETE FROM FeedbackDB WHERE feedbackID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, feedbackID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error in deleteFeedback: " + e.getMessage());
			return false;
		}
	}
	
	/*******
	 * <p> Method: mapRowToFeedback(ResultSet rs) </p>
	 *
	 * <p> Description: Maps the current row of a ResultSet from FeedbackDB to a Feedback
	 *  object. </p>
	 *
	 */
	private Feedback mapRowToFeedback(ResultSet rs) throws SQLException {
		Feedback fb = new Feedback(
			rs.getInt("postID"),
			rs.getString("staffUsername"),
			rs.getString("targetUsername"),
			rs.getString("body"));
		fb.setFeedbackID(rs.getInt("feedbackID"));
		Timestamp ts = rs.getTimestamp("createdAt");
		if (ts != null) fb.setCreatedAt(ts.toLocalDateTime());
		return fb;
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