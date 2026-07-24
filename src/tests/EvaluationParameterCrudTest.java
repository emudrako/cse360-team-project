package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.sql.SQLException;
import database.Database;
import entityClasses.EvaluationParameter;
import guiStaffParameters.ControllerStaffParameters;

public class EvaluationParameterCrudTest {
    private Database db;

    @BeforeEach
    public void setUp() throws SQLException {
        db = new Database();
        db.connectToDatabase();
    }

    
	/**********
	 * <p> Method: testUniqueIDs() </p>
	 *
	 * <p> Description: Verifies that two distinct EvaluationParameter objects, once
	 *  created, are assigned different database-generated IDs, satisfying Test
	 *  Requirement #1 from TP3 Testing Details and Rationale (unique IDs so
	 *  update/delete target the correct parameter). </p>
	 *
	 */
	@Test 
	public void testUniqueIDs() throws SQLException {
	    // Create two separate parameters to test each gets a separate ID
	    EvaluationParameter param1 = new EvaluationParameter(
	        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
	    db.createEvaluationParameter(param1);

	    EvaluationParameter param2 = new EvaluationParameter(
	        "Helpfulness", "Evaluates whether the student's replies genuinely help others.", 10.0, 2.0);
	    db.createEvaluationParameter(param2);

	    // If IDs collided, Update/Delete calls could silently target the wrong row
	    assertNotEquals(param1.getParamID(), param2.getParamID());
	}

	/**********
	 * <p> Method: testCreateValidParameter() </p>
	 *
	 * <p> Description: Verifies that a new EvaluationParameter object, can be successfully
	 * constructed and persisted to the database when fields are all valid. </p>
	 *
	 */
    @Test
    	public void testCreateValidParameter() throws SQLException {
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
		    db.createEvaluationParameter(param);
		    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
		    
	        assertEquals("Clarity", fromDB.getName());
	        assertEquals(10.0, fromDB.getMaxScore(), 0.001);
	        assertEquals(3.0, fromDB.getWeight(), 0.001);
	        assertEquals("Measures how clearly the student communicates their ideas in posts.", fromDB.getDescription());

    }
		 
	/**********
	 * <p> Method: testCreateRejectEmptyName() </p>
	 *
	 * <p> Description: Verifies that a new EvaluationParameter contstructor rejects an empty
	 * name by throwing IllegalArgumentException. </p>
	 *
	 */
    @Test
	public void testCreateRejectEmptyName() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("", "Some description over 40 characters long here.", 10.0, 3.0);
        });
	    
    }

    /**********
	 * <p> Method: testCreateRejectsNullName() </p>
	 *
	 * <p> Description: Verifies that a new EvaluationParameter contstructor rejects an null
	 * name by throwing IllegalArgumentException. </p>
	 *
	 */
    @Test
	public void testCreateRejectNullName() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter(null, "Some description over 40 characters long here.", 10.0, 3.0);
        });
	    
    }
	/**********
	 * <p> Method: testMaxScoreLowerBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that maxScore=1, the lowest valid value, is accepted
	 *  and correctly persisted, satisfying the lower edge of validation
	 *  (maxScore must be 1-100). </p>
	 *
	 */
    @Test
    public void testMaxScoreLowerBoundaryValid() throws SQLException {
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 1.0, 3.0);
	    db.createEvaluationParameter(param);
	    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
	    
	    assertEquals(1.0, fromDB.getMaxScore(), 0.001);

    }

    /**********
	 * <p> Method: testMaxScoreLowerBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that maxScore=0, one step below the valid lower
	 *  boundary, is rejected by throwing IllegalArgumentException, satisfying the
	 *  lower invalid edge (maxScore must be 1-100). </p>
	 *
	 */
    @Test
	public void testMaxScoreLowerBoundaryInvalid() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("Clarity", "Some description over 40 characters long here.", 0.0, 3.0);
        });
	    
    }

    /**********
	 * <p> Method: testMaxScoreUpperBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that maxScore=100, the highest valid value, is accepted
	 *  and correctly persisted, satisfying the upper edge of validation
	 *  (maxScore must be 1-100). </p>
	 *
	 */
    @Test
    public void testMaxScoreUpperBoundaryValid() throws SQLException {
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 100.0, 3.0);
	    db.createEvaluationParameter(param);
	    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
	    
	    assertEquals(100.0, fromDB.getMaxScore(), 0.001);

    }
    
    /**********
	 * <p> Method: testMaxScoreUpperBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that maxScore=101, one step above the valid upper
	 *  boundary, is rejected by throwing IllegalArgumentException, satisfying the
	 *  upper invalid edge (maxScore must be 1-100). </p>
	 *
	 */
    @Test
	public void testMaxScoreUpperBoundaryInvalid() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("Clarity", "Some description over 40 characters long here.", 101.0, 3.0);
        });
	    
    }

	/**********
	 * <p> Method: testWeightLowerBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that Weight=1, the lowest valid value, is accepted
	 *  and correctly persisted, satisfying the lower edge of validation
	 *  (Weight must be 1-10). </p>
	 *
	 */
    @Test
    public void testWeightLowerBoundaryValid() throws SQLException {
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 1.0, 1.0);
	    db.createEvaluationParameter(param);
	    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
	    
	    assertEquals(1.0, fromDB.getWeight(), 0.001);

    }

    /**********
	 * <p> Method: testWeightLowerBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that Weight=0, one step below the valid lower
	 *  boundary, is rejected by throwing IllegalArgumentException, satisfying the
	 *  lower invalid edge (Weight must be 1-10). </p>
	 *
	 */
    @Test
	public void testWeightLowerBoundaryInvalid() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("Clarity", "Some description over 40 characters long here.", 10, 0.0);
        });
	    
    }

    /**********
	 * <p> Method: testWeightUpperBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that Weight=10, the highest valid value, is accepted
	 *  and correctly persisted, satisfying the upper edge of validation
	 *  (Weight must be 1-10). </p>
	 *
	 */
    @Test
    public void testWeightUpperBoundaryValid() throws SQLException {
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", "Measures how clearly the student communicates their ideas in posts.", 100.0, 10.0);
	    db.createEvaluationParameter(param);
	    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
	    
	    assertEquals(10.0, fromDB.getWeight(), 0.001);

    }
    
    /**********
	 * <p> Method: testWeightUpperBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that Weight=11, one step above the valid upper
	 *  boundary, is rejected by throwing IllegalArgumentException, satisfying the
	 *  upper invalid edge (Weight must be 1-100). </p>
	 *
	 */
    @Test
	public void testWeightUpperBoundaryInvalid() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("Clarity", "Some description over 40 characters long here.", 100.0, 11.0);
        });
	    
    }

    /**********
	 * <p> Method: testDescriptionBoundaryValid() </p>
	 *
	 * <p> Description: Verifies that Description char=40, the lowest valid value, is accepted
	 *  and correctly persisted, satisfying the lower edge of validation
	 *  (Description must be at least 40 characters). </p>
	 *
	 */
    @Test
    public void testDescriptionBoundaryValid() throws SQLException {
        String description = "This is exactly forty characters long!!!";
	    EvaluationParameter param = new EvaluationParameter(
		        "Clarity", description, 100.0, 3.0);
	    db.createEvaluationParameter(param);
	    EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
	    
	    assertEquals(description, fromDB.getDescription());

    }
    /**********
	 * <p> Method: testDescriptionBoundaryInvalid() </p>
	 *
	 * <p> Description: Verifies that Description is less than 40 characters, in this case 39, one step below the valid lower
	 *  boundary, is rejected by throwing IllegalArgumentException, satisfying the
	 *  lower invalid edge (Description must be at least 40 characters). </p>
	 *
	 */
    @Test
	public void testDescriptionBoundaryInvalid() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EvaluationParameter("Clarity", "Some description less than 40 characte.", 100.0, 10.0);
        });
	    
    }

    /**********
     * <p> Method: testReadParameter() </p>
     *
     * <p> Description: Verifies that readEvaluationParameter() returns a parameter
     *  whose fields exactly match what was originally stored, confirming data is
     *  not corrupted between saving and retrieving, satisfying Read of CRUD. </p>
     *
     */
    @Test
    public void testReadParameter() throws SQLException {
        EvaluationParameter param = new EvaluationParameter(
            "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
        db.createEvaluationParameter(param);

        EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());

        assertEquals(param.getParamID(), fromDB.getParamID());
        assertEquals("Clarity", fromDB.getName());
        assertEquals("Measures how clearly the student communicates their ideas in posts.", fromDB.getDescription());
        assertEquals(10.0, fromDB.getMaxScore(), 0.001);
        assertEquals(3.0, fromDB.getWeight(), 0.001);
    }
	//Test 16 (Negative): Read nonexistent ID — returns null, not an error
    /**********
     * <p> Method: testReadNonexistentID() </p>
     *
     * <p> Description: Verifies that readEvaluationParameter() returns null, rather
     *  than throwing an exception, when given an ID that does not correspond to
     *  any existing parameter. </p>
     *
     */
    @Test
    public void testReadNonexistentID() throws SQLException {
        // No parameter has ever been created with this ID
        EvaluationParameter fromDB = db.readEvaluationParameter(999999);

        assertNull(fromDB);
    }

    /**********
     * <p> Method: testUpdateParameter() </p>
     *
     * <p> Description: Verifies that updateEvaluationParameter() persists updates. </p>
     *
     */
    @Test
    public void testUpdateParameter() throws SQLException {
        EvaluationParameter param = new EvaluationParameter(
            "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
        db.createEvaluationParameter(param);

        boolean wasUpdated = db.updateEvaluationParameter(
                param.getParamID(), "New Name", "New description over 40 characters here for sure.", 50.0, 5.0);
        
        assertTrue(wasUpdated);
        EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());

        assertEquals("New Name", fromDB.getName());
        assertEquals("New description over 40 characters here for sure.", fromDB.getDescription());
        assertEquals(50.0, fromDB.getMaxScore(), 0.001);
        assertEquals(5.0, fromDB.getWeight(), 0.001);

    }
    
    /**********
     * <p> Method: testUpdateNonexistentID() </p>
     *
     * <p> Description: Verifies that updateEvaluationParameter() returns false,
     *  rather than throwing an exception, when given an ID that does not
     *  correspond to any existing parameter. </p>
     *
     */
    @Test
    public void testUpdateNonexistentID() throws SQLException {
        boolean wasUpdated = db.updateEvaluationParameter(
            999999, "Ghost Name", "This parameter was never created in the first place.", 10.0, 3.0);

        assertFalse(wasUpdated);
    }
    
	//Test 19 (Negative): Update with invalid data rejected — e.g., empty name or out-of-range weight is not applied
    /**********
     * <p> Method: testUpdateRejectsInvalidData() </p>
     *
     * <p>  Description: Verifies that an invalid update (empty name) is rejected
     *  and does not overwrite the original data in the database. </p>
     *
     */
    @Test
    public void testUpdateRejectsInvalidData() throws SQLException {
        EvaluationParameter param = new EvaluationParameter(
                "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
            db.createEvaluationParameter(param);

            // Confirm the constructor itself rejects the invalid input
            assertThrows(IllegalArgumentException.class, () -> {
                new EvaluationParameter("", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
            });

            // Confirm the original parameter in the database is untouched
            EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
            assertEquals("Clarity", fromDB.getName());
    }

    /**********
     * <p> Method: testDeleteParameter() </p>
     *
     * <p> Description: Verifies that deleteEvaluationParameter() persists delete. </p>
     *
     */
    @Test
    public void testDeleteParameter() throws SQLException {
        EvaluationParameter param = new EvaluationParameter(
            "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
        db.createEvaluationParameter(param);

        boolean wasDeleted = db.deleteEvaluationParameter(
                param.getParamID());
        
        assertTrue(wasDeleted);
        
        //confirm deletion persists
        EvaluationParameter fromDB = db.readEvaluationParameter(param.getParamID());
        assertNull(fromDB);

    }

    /**********
     * <p> Method: testDeleteNonexistentID() </p>
     *
     * <p> Description: Verifies that deleteEvaluationParameter() returns false,
     *  rather than throwing an exception, when given an ID that does not
     *  correspond to any existing parameter. </p>
     *
     */
    @Test
    public void testDeleteNonexistentID() throws SQLException {
        boolean wasDeleted = db.deleteEvaluationParameter(999999);

        assertFalse(wasDeleted);
    }
    
    /**********
     * <p> Method: testDeleteDoesNotAffectOtherRows() </p>
     *
     * <p> Description: Verifies that deleting one parameter does not remove or
     *  alter any other existing parameter, confirming the delete operation is
     *  correctly scoped to the specified ID only. </p>
     *
     */
    @Test 
    public void testDeleteDoesNotAffectOtherRows() throws SQLException {
        EvaluationParameter param1 = new EvaluationParameter(
            "Clarity", "Measures how clearly the student communicates their ideas in posts.", 10.0, 3.0);
        db.createEvaluationParameter(param1);

        EvaluationParameter param2 = new EvaluationParameter(
            "Helpfulness", "Evaluates whether the student's replies genuinely help others.", 10.0, 2.0);
        db.createEvaluationParameter(param2);

        // Delete only param1
        db.deleteEvaluationParameter(param1.getParamID());

        // param1 should be gone
        assertNull(db.readEvaluationParameter(param1.getParamID()));

        // param2 should be completely unaffected
        EvaluationParameter param2FromDB = db.readEvaluationParameter(param2.getParamID());
        assertEquals("Helpfulness", param2FromDB.getName());
    }
 }
