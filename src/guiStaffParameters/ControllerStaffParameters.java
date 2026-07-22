package guiStaffParameters;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import entityClasses.EvaluationParameter;
import entityClasses.User;
import javafx.scene.control.Label;

// TODO fix the description for this 
/*******
 * <p> Title: ControllerStaffParameters Class. </p>
 *
 * <p> Description: Controller for the Review-Parameter CRUD screen, satisfying
 *  STORY 2: Implement CRUD for Evaluation Parameters. Handles navigation into
 *  this screen, creation and validation of EvaluationParameter objects, reading
 *  all parameters from the database, and building the display card for each
 *  parameter. </p>. 
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
	 * <p> Method: performStaffParameters(Stage ps, User user) </p>
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
	 * <p> Method: performReadAllStaffParameters() </p>
	 *
	 * <p> Description: Retrieves and returns a list of all staff parameters. Satisfies STORY 2: Implement CRUD for Evaluation Parameters
	 * Each time the screen is displayed, displayStaffParameter() calls this method, which calls the database's readAllEvaluationParameters()
	 * to fetch the current list. If that attempt fails, an exeption is thrown and caught here, an error message is shown, and an
	 * empty list is returned instead. </p>
	 * 
	 * @return the list of all evaluation parameters, or an empty list if the database call fails
	 * 
	 */
	public static List<EvaluationParameter> performReadAllStaffParameters() {
		try {
			// grab EvaluationParameter list from the database and return
			return ViewStaffParameters.theDatabase.readAllEvaluationParameters();
		} catch (SQLException e){
			// If the database call throws an exception, alert with error message and return empty list
			ViewStaffParameters.label_ErrorMessage.setText("*** ERROR *** Could not load parameters.");
			return new ArrayList<EvaluationParameter>();
		}

	}

	
	/**********
	 * <p> Method: performReadStaffParameter(int paramID) </p>
	 *
	 * <p> Description: Retrieves a single EvaluationParameter by its ID, satisfying
	 *  STORY 2: Implement CRUD for Evaluation Parameters. Delegates directly to the
	 *  database's readEvaluationParameter(), which already returns null if no
	 *  matching parameter is found or if the database read fails. </p>
	 *
	 * @param paramID specifies the ID of the parameter to retrieve
	 *
	 * @return the EvaluationParameter matching the given ID, or null if not found
	 *
	 */
	public static EvaluationParameter performReadStaffParameter(int paramID) {
	    return ViewStaffParameters.theDatabase.readEvaluationParameter(paramID);
	}
	
	/**********
	 * <p> Method: performUpdateStaffParameter(int paramID, String name, String description,
	 *  double maxScore, double weight) </p>
	 *
	 * <p> Description: Updates an existing EvaluationParameter's fields, satisfying
	 *  STORY 2: Implement CRUD for Evaluation Parameters. Constructs a temporary
	 *  EvaluationParameter to reuse the constructor's validation before persisting
	 *  the change, so an invalid update is rejected the same way an invalid create
	 *  would be. </p>
	 *
	 * @param paramID specifies the ID of the parameter to update
	 * @param name specifies the new name; must not be empty
	 * @param description specifies the new description; must be at least 40 characters
	 * @param maxScore specifies the new max score; must be 1-100
	 * @param weight specifies the new weight; must be 1-10
	 *
	 */
	public static void performUpdateStaffParameter(int paramID, String name, String description,
	        double maxScore, double weight) {
	    try {
	        // Reuse the constructor purely for its validation; the object itself is discarded
	        new EvaluationParameter(name, description, maxScore, weight);

	        boolean wasUpdated = ViewStaffParameters.theDatabase.updateEvaluationParameter(
	            paramID, name, description, maxScore, weight);

	        if (wasUpdated) {
	            ViewStaffParameters.label_ErrorMessage.setText("Parameter updated successfully");
	        } else {
	            ViewStaffParameters.label_ErrorMessage.setText("*** ERROR *** Could not update parameter.");
	        }
	    } catch (IllegalArgumentException e) {
	        ViewStaffParameters.label_ErrorMessage.setText(e.getMessage());
	    }
	}
	
	/**********
	 * <p> Method: createParameterCard(EvaluationParameter param) </p>
	 *
	 * <p> Description: Builds a single styled, clickable card displaying one
	 *  EvaluationParameter's name, satisfying the display portion of STORY 2:
	 *  Implement CRUD for Evaluation Parameters. Called once per parameter by
	 *  ViewStaffParameters.displayParamCards() to populate the horizontal
	 *  parameter list. </p>
	 *
	 * @param param specifies the EvaluationParameter to build a card for
	 *
	 * @return an HBox containing the styled card for this parameter
	 *
	 */
	protected static HBox createParameterCard(EvaluationParameter param) {
		// Remove deleted parameters
	    //if (param.getIsDeleted()) {
	    //   return null;
	    //}
	    HBox paramCard = new HBox(5);
	    paramCard.setPadding(new Insets(5, 10, 5, 10));
	    paramCard.setMaxHeight(40);
	    paramCard.setStyle(
	    	    "-fx-background-color: #0062A3;" +
	    	    "-fx-background-radius: 8;" +
	    	    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    	);

	    
	    // Title in bold
	    Label title = new Label(param.getName());
    	title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
	    	    
	    paramCard.getChildren().addAll(title);
	    // Click on current parameter
	    paramCard.setCursor(Cursor.HAND);
	    paramCard.setOnMouseClicked((_) -> {
	        ViewStaffParameters.showParamDetails(param); 
	    });
	    
	    return paramCard;
	}
	
	/**********
	 * <p> Method: performDeleteStaffParameter(int paramID) </p>
	 *
	 * <p> Description: Deletes an EvaluationParameter by its ID, satisfying STORY 2:
	 *  Implement CRUD for Evaluation Parameters. Delegates to the database's
	 *  deleteEvaluationParameter(), and displays a success or failure message to
	 *  the user based on the result. </p>
	 *
	 * @param paramID specifies the ID of the parameter to delete
	 *
	 */
	public static void performDeleteStaffParameter(int paramID) {
	    boolean wasDeleted = ViewStaffParameters.theDatabase.deleteEvaluationParameter(paramID);

	    if (wasDeleted) {
	        ViewStaffParameters.label_ErrorMessage.setText("Parameter deleted successfully");
	    } else {
	        ViewStaffParameters.label_ErrorMessage.setText("*** ERROR *** Could not delete parameter.");
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