package guiStaffRequests;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Request;
import entityClasses.RequestComment;
import entityClasses.RequestList;
import entityClasses.User;


/*******
 * <p> Title: ControllerStaffRequests Class. </p>
 *
 * <p> Description: Stub controller for the Staff requests screen.
 * Currently just handles navigation into this screen from Staff Home; will be
 * expanded with real event handlers as this feature is built out. </p>
 *
 * <p> Copyright: OWNER_NAME_HERE © 2026 </p>
 *
 * @author OWNER_NAME_HERE
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ControllerStaffRequests {

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	private static RequestList allRequests;
	static Request currentRequest;
	private static boolean onCommentForm = false;
	private static List<RequestComment> allComments = new ArrayList<>();
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: Clears and rebuilds the Discussion Board page, reloading
	 * all posts and replies from the database to ensure newly created content
	 * appears immediately. </p>
	 *
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewStaffRequests.theRootPane.getChildren().clear();
		ViewStaffRequests.theRootPane.getChildren().addAll(
				ViewStaffRequests.label_PageTitle, 
				ViewStaffRequests.label_UserDetails,
				ViewStaffRequests.textfield_Search,
				ViewStaffRequests.button_Search,
				ViewStaffRequests.button_NewRequest,
				ViewStaffRequests.button_Open,
				ViewStaffRequests.button_Assigned,
				ViewStaffRequests.button_Closed,
		        ViewStaffRequests.button_ViewAll,
		        ViewStaffRequests.scrollPane_RequestCardList,
		        ViewStaffRequests.scrollPane_RequestDetails,
		        ViewStaffRequests.line_Separator4, 
		        ViewStaffRequests.button_Home,
		        ViewStaffRequests.button_Logout,
		        ViewStaffRequests.button_Quit);
		
		// Always reload posts from the database to ensure newly created posts appear immediately
		List<Request> requests = new ArrayList<>();
		try {
			allRequests = new RequestList();
		    requests = theDatabase.readAllRequests();
		    for (Request request : requests) {
		    	allRequests.addRequest(request);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		displayRequestCards();
		
		try {
		    allComments = theDatabase.readAllRequestComments();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Set the title for the window
		ViewStaffRequests.theStage.setTitle("View / Create Requests");
		ViewStaffRequests.theStage.setScene(ViewStaffRequests.theStaffRequestsScene);
		ViewStaffRequests.theStage.show();
		ViewStaffRequests.theStage.centerOnScreen();
	}
	
	/**********
	 * <p> Method: performControllerStaffRequestsStage ps, User user) </p>
	 *
	 * <p> Description: Called when the staff user navigates to this screen
	 * (e.g., from a button on Staff Home). Displays the corresponding View. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user specifies the User for this GUI
	 *
	 */
	public static void performStaffRequests(Stage ps, User user) {
		ViewStaffRequests.displayStaffRequests(ps, user);
	}
	
	/**********
	 * <p> Method: performSearch() </p>
	 *
	 * <p> Description: Takes the user defined search word(s) to return a list
	 * of request cards that contain the search word(s) in the subject or 
	 * description of the request. </p>
	 *
	 *
	 */
	public static void performSearch() {
		String searchWords = ViewStaffRequests.textfield_Search.getText().trim().toLowerCase();
		List<Request> requestList = allRequests.getAllRequests();
		
		ViewStaffRequests.requestCardList.getChildren().clear();
		
		for (Request request : requestList) {		
			String subject = request.getSubject().trim().toLowerCase();
			String description = request.getDescription().trim().toLowerCase();
			if (subject.contains(searchWords) || description.contains(searchWords)) {
			        VBox card = createRequestCard(request);
			        if (card != null) {
			        	ViewStaffRequests.requestCardList.getChildren().add(card);
			        }
			}
		}
	}
	
	
	/**********
	 * <p> Method: createRequestCard() </p>
	 * 
	 * <p> Description: This method creates request cards to populate the request card
	 * scroll pane. The card contains information about a request, including the subject
	 * and author so a user can decide which request to view. They can then click on the
	 * card to view the full request. </p>
	 * 
	 * @param request the Request object from which to create the request card from
	 * 
	 */
	protected static VBox createRequestCard(Request request) {
	    VBox requestCard = new VBox(5);
	    requestCard.setPadding(new Insets(10));
	    requestCard.setMinWidth(ViewStaffRequests.scrollPane_RequestCardList.getMinWidth()-40);
	    requestCard.setMaxWidth(ViewStaffRequests.scrollPane_RequestCardList.getMinWidth()-40);
	    requestCard.setStyle(
	    	    "-fx-border-color: #E0E0E0;" +
	    	    "-fx-border-radius: 8;" +
	    	    "-fx-background-color: white;" +
	    	    "-fx-background-radius: 8;" +
	    	    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    	);
	    
	    // Title at top in bold
	    Label subject = new Label(request.getSubject());
	    subject.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
	    
	    // Format timestamp nicely
	    String formattedTime = "";
	    if (request.getCreatedAt() != null) {
	        formattedTime = request.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    
	    // Author and timestamp on same line
	    Label authorAndTime = new Label("by " + request.getRequestorUsername() + "  •  " + formattedTime);
	    authorAndTime.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
	    
	    // Thread in upper right using HBox
	    Label status = new Label(request.getStatus());
	    status.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");
	    
	    javafx.scene.layout.HBox topRow = new javafx.scene.layout.HBox();
	    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
	    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
	    topRow.getChildren().addAll(subject, spacer, status);
	    
	    requestCard.getChildren().addAll(topRow, authorAndTime);
	    requestCard.setCursor(Cursor.HAND);
	    requestCard.setOnMouseClicked((_) -> {
	    	currentRequest = request;
	        displayRequest(request);
	    });
	    
	    return requestCard;
	}
	
	
	/**********
	 * <p> Method: displayRequestCards() </p>
	 * 
	 * <p> Description: This method populates the request cards Scroll Pane, which shows
	 * a list of requests by subject. The request cards visible are based on which status
	 * has been selected. If no status has been selected, all request cards are visible. </p>
	 * 
	 * @param requestObjects the list of Request objects to display as request cards
	 *  
	 */
	protected static void displayRequestCards() {
	    List<Request> requestList = allRequests.getAllRequests();
		List<Request> newRequestList = new ArrayList<>();

	    if (ViewStaffRequests.selectedStatus.equals("Open")) {
	        for (Request request : requestList) {
	            if (request.getStatus().equals("Open")) {
	                newRequestList.add(request);
	            }
	        }
	    }
	    else if (ViewStaffRequests.selectedStatus.equals("Assigned")) {
	        for (Request request : requestList) {
	            if (request.getStatus().equals("Assigned")) {
	                newRequestList.add(request);
	            }
	        }
	    }
	    else if (ViewStaffRequests.selectedStatus.equals("Closed")) {
	        for (Request request : requestList) {
	            if (request.getStatus().equals("Closed")) {
	                newRequestList.add(request);
	            }
	        }
	    }
	    else {
	        for (Request request : requestList) {
	            newRequestList.add(request);
	        }
	    }
	    
	    ViewStaffRequests.requestCardList.getChildren().clear();
	    for (Request request : newRequestList) {
	        VBox card = createRequestCard(request);
	        if (card != null) {
	        	ViewStaffRequests.requestCardList.getChildren().add(card);
	        }
	    }
	}
	
	
	/**********
	* <p> Method: displayRequest() </p>
	*
	* <p> Description: This method populates the request details Scroll Pane with the
	* full request details when the user clicks on a request card. </p>
	*
	* @param request the Request object to display in the request details Scroll Pane
	*
	*/
	protected static void displayRequest(Request request) {
		ViewStaffRequests.scrollPane_RequestDetails.setContent(null);
	    VBox fullRequest = new VBox(5);
	    fullRequest.setPadding(new Insets(10));
	    fullRequest.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: #E0E0E0;" +
	        "-fx-border-radius: 8;" +
	        "-fx-background-radius: 8;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    );

	    Label subject = new Label(request.getSubject());
	    subject.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 18px;");
		
	    Label requestor = new Label("Requestor: " + request.getRequestorUsername());
	    Label status = new Label("Status: " + request.getStatus());
		
	    TextArea description = new TextArea(request.getDescription());
	    description.setPrefHeight(100);
	    description.setWrapText(true);
	    description.setEditable(false);
		
		Button button_Comment = new Button("Comment");
		button_Comment.setOnAction((_) ->
			{onCommentForm = true;
			displayRequest(request);
		});
		
		
		fullRequest.getChildren().addAll(
				subject,
				requestor,
				status,
				description,
				button_Comment
				);

		if (onCommentForm == true) {
			VBox commentForm = newCommentForm();
			fullRequest.getChildren().add(commentForm);
			onCommentForm = false;
		}
		
		VBox displayComments = new VBox(5);
		for (RequestComment comment : allComments) {
			if (comment.getRequestID() == currentRequest.getRequestID()) {
				displayComments.getChildren().add(displayRequestComment(comment));
			}
		}
		fullRequest.getChildren().add(displayComments);
		ViewStaffRequests.scrollPane_RequestDetails.setContent(fullRequest);
	} 
	
	
	/**********
	* <p> Method: displayRequestComment() </p>
	*
	* <p> Description: This method populates the request details Scroll Pane with the
	* full request details when the user clicks on a request card. </p>
	*
	* @param request the Request object to display in the request details Scroll Pane
	*
	*/
	protected static VBox displayRequestComment(RequestComment comment) {
	    VBox requestComment = new VBox(5);
	    requestComment.setPadding(new Insets(10));
	    requestComment.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: #E0E0E0;" +
	        "-fx-border-radius: 8;" +
	        "-fx-background-radius: 8;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
	    );

	    Label commenter = new Label("Commenter: " + comment.getCommenterUsername());
		
	    TextArea description = new TextArea(comment.getDescription());
	    description.setPrefHeight(100);
	    description.setWrapText(true);
	    description.setEditable(false);
		
	    requestComment.getChildren().addAll(
				commenter,
				description
				);
	    return requestComment;
	} 
	

	/**********
	 * <p> Method: newCommentForm() </p>
	 * 
	 * <p> Description: This method populates the new reply VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a reply. </p>
	 * 
	 * @return a VBox containing the reply form with a text area and submit/cancel buttons
	 *  
	 */
	protected static VBox newCommentForm() {
		VBox vBox_CommentForm = new VBox(5);
		vBox_CommentForm.setPadding(new Insets(15));
		
		Label label_Title = new Label("New Comment");
		label_Title.setFont(Font.font("Arial", 12));
		label_Title.setStyle("-fx-font-weight: bold;");
		
		TextArea textArea_CommentContent = new TextArea();
		textArea_CommentContent.setWrapText(true);
		textArea_CommentContent.setPrefRowCount(12);
		
		Button button_Submit = new Button("Submit");
		Button button_Cancel = new Button("Cancel");
		HBox hBox_Buttons = new HBox(10, button_Submit, button_Cancel);
		
		vBox_CommentForm.getChildren().addAll(
				label_Title,
				textArea_CommentContent,
				hBox_Buttons);
				
		button_Submit.setOnAction((_) ->
			{performCreateComment(currentRequest.getRequestID(),
				ViewStaffRequests.theUser.getUserName(), textArea_CommentContent.getText());
				displayRequest(currentRequest);
			});
		button_Cancel.setOnAction((_) ->
			{displayRequest(currentRequest);
			});
		return vBox_CommentForm;
		}
	
	
	/**********
	 * <p> Method: performCreateComment() </p>
	 * 
	 * <p> Description: This method validates the title and body input, saves it to the database,
	 * clears the form fields, and navigates back to the Discussion Board,
	 * Displays an error message if validation fails or a database exception occurs. </p>
	 * 
	 */
	protected static void performCreateComment(int requestID, String commenterUsername, String description) {
	    String errMsg;
	    
	 // Validate input using PostReplyValidator
	    errMsg = recognizers.PostReplyValidator.checkForValidRequestDescription(description);
	    if (!errMsg.isEmpty()) {
	       // ViewStaffRequests.label_ErrorMessage.setText(errMsg);
	        return;
	    }
	    
	    // Create the post and save to database
	   RequestComment comment = new RequestComment(requestID, ViewStaffRequests.theUser.getUserName(), description);
	    try {
	        theDatabase.createRequestComment(comment);
	       // ViewStaffRequests.label_ErrorMessage.setText("");
	    } catch (Exception e) {
	    	//ViewStaffRequests.label_ErrorMessage.setText("Error creating post: " + e.getMessage());
	    }
	    allComments.add(comment);
	    displayRequest(currentRequest);
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user to the staff homepage </p>
	 * 
	 */
	protected static void performReturn() {
	    guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffRequests.theStage, ViewStaffRequests.theUser);
	}
	
	
	/**********
	 * <p> Method: performHome() </p>
	 * 
	 * <p> Description: This method returns the user to the student home page </p>
	 * 
	 */
	protected static void performHome() {
		guiStaffHome.ViewStaffHome.displayStaffHome(ViewStaffRequests.theStage,
				ViewStaffRequests.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffRequests.theStage);
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