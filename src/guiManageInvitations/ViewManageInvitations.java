package guiManageInvitations;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import javafx.scene.control.ListView;
import java.util.List;

/*******
 * <p> Title: ViewManageInvitations Class. </p>
 *
 * <p> Description: The Java/FX-based page for managing outstanding invitations.
 * Allows the admin to view all pending invitations and manually delete any that
 * are no longer needed.</p>
 *
 * <p> Copyright: Maranda Martinez © 2026 </p>
 *
 * @author Maranda Martinez
 *
 * @version 1.00		2026-06-07 Initial version
 *
 */

public class ViewManageInvitations {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 - ListView
	protected static ListView<String> listview_Invitations = new ListView<String>();
	protected static Button button_Delete = new Button("Delete");
	private static Line line_Separator4 = new Line(20, 525, width-20, 525);

	// GUI Area 3 
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	
	
	private static ViewManageInvitations theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theManageInvitationsScene;
	
	/*-*******************************************************************************************

	Constructors

	 */
	/**********
	 * <p> Method: displayManageInvitations(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Manage Invitations page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the admin currently logged in
	 */
	
	public static void displayManageInvitations(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewManageInvitations();
		
		label_UserDetails.setText("User: " + theUser.getUserName());

		theStage.setTitle("CSE 360 Foundations: Manage Invitations Page");
		
		listview_Invitations.getItems().clear();
		List<String[]> invitations = theDatabase.getAllInvitations();
		for (String[] inv : invitations) {
		    listview_Invitations.getItems().add(inv[0] + " | " + inv[1] + " | " + inv[2] + " | " + inv[3]);
		}
		
		theStage.setScene(theManageInvitationsScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewManageInvitations() </p>
	 *
	 * <p> Description: Initializes all GUI elements. It is a Singleton and runs once. </p>
	 *
	 */
	private ViewManageInvitations() {
	    theRootPane = new Pane();
	    theManageInvitationsScene = new Scene(theRootPane, width, height);
	 
	    // GUI Area 1
	    label_PageTitle.setText("Manage Invitations");
	    setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

	    label_UserDetails.setText("User: " + theUser.getUserName());
	    setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

	    setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
	    button_UpdateThisUser.setOnAction((_) -> { guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
	    
	    // GUI Area 2 
	    listview_Invitations.setLayoutX(20);
	    listview_Invitations.setLayoutY(110);
	    listview_Invitations.setPrefWidth(700);
	    listview_Invitations.setPrefHeight(300);
	    
	    setupButtonUI(button_Delete, "Dialog", 16, 150, Pos.CENTER, 740, 250);
	    button_Delete.setOnAction((_) -> { ControllerManageInvitations.performDelete(); });
	    
	    // GUI Area 3
	    setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	    button_Return.setOnAction((_) -> { ControllerManageInvitations.performReturn(); });

	    setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 270, 540);
	    button_Logout.setOnAction((_) -> { ControllerManageInvitations.performLogout(); });

	    setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 520, 540);
	    button_Quit.setOnAction((_) -> { ControllerManageInvitations.performQuit(); });

	    theRootPane.getChildren().addAll(
	    	label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1, listview_Invitations, button_Delete,
	    	line_Separator4, button_Return, button_Logout, button_Quit);
	    
	}
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
}

