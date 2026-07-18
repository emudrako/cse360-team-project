package guiStaffParameters;

import javafx.stage.Stage;

import java.sql.SQLException;

import entityClasses.EvaluationParameter;
import entityClasses.User;

// TODO fix the description for this 
/*******
 * <p> Title: ControllerStaffParameters Class. </p>
 *
 * <p> Description: Stub controller for the Staff Parameters screen.
 * Currently just handles navigation into this screen from Staff Home; will be
 * expanded with real event handlers as this feature is built out. </p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ControllerStaffParameters {

	/**********
	 * <p> Method: performControllerStaffParameters(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen
	 * (e.g., from a button on Staff Home). Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	
	public static void performStaffParameters(Stage ps, User user) {
		ViewStaffParameters.displayStaffParameters(ps, user);
	}
	
	/**********
	 * <p> Method: performCreateStaffParameter(String name, String description, double maxScore, double weight) </p>
	 *
	 * <p> Description: Method to create a staff parameter. It creates an EvaluationParameter object for the CREATE portion
	 * of the CRUD for Evaluation Parameters satisfying STORY 2: Implementation of Evaluation Parameters. Validation occurs inside the 
	 * EvaluationParameter constructor; if validation fails or the database insert fails, the resulting error message is displayed to the user 
	 * via label_ErrorMessage rather than being thrown back to the caller. On success, a confirmation message is displayed instead. </p>
	 *
	 * @param name			specifies the name of the parameter to create; must not be empty
	 *
	 * @param description	specifies the description of the parameter to create; must be at least 40 characters
	 * 
	 * @param maxScore		specifies the maxScore of the parameter to create; must be 1-100
	 * 
	 * @param weight		specifies the weight of the parameter to create; must be 1-10
	 * 
	 */
	public static void performCreateStaffParameter(String name, String description, double maxScore, double weight) {
		// create the EvaluationParameter object
		try {
			// Create an EvaluationParameter object, the constructor handles validation
			EvaluationParameter newParam = new EvaluationParameter(name, description, maxScore, weight);
			
			// Persist the new parameter to the database
			ViewStaffParameters.theDatabase.createEvaluationParameter(newParam);
			
			// Parameter was successfully created
			ViewStaffParameters.label_ErrorMessage.setText("Parameter created successfully");
		} catch(IllegalArgumentException e){
			// Input failed a constructor validation check with the specific reason shown 
			ViewStaffParameters.label_ErrorMessage.setText(e.getMessage());
		} catch(SQLException e){
			// Database insert failed after validation passed
			ViewStaffParameters.label_ErrorMessage.setText("*** ERROR *** Could not save parameter to the database.");
		}

	}

	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the staff homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffParameters.theStage, ViewStaffParameters.theUser);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}