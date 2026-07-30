package guiStaffRequests;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.Database;
import entityClasses.Request;
import entityClasses.RequestComment;
import entityClasses.RequestList;
import entityClasses.User;
import guiAdminHome.ViewAdminHome;
import guiStaffHome.ViewStaffHome;


/*******
 * <p> Title: ControllerStaffRequests Class. </p>
 *
 * <p> Description: Contains all methods to display the necessary elements
 * on the Staff Request page. Also contains all methods to perform all
 * necessary functions for creating requests, request cards, comments.
 * Populates different elements depending on whether the user is a Staff or
 * an Admin. </p>
 *
 * <p> Copyright: Pete Echavarria © 2026 </p>
 *
 * @author Pete Echavarria
 *
 * @version 1.00		2026-07-15 Initial version
 *
 */
public class ControllerStaffRequests {

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	// A RequestList object that will be populated with Request objects from the database
	public static RequestList allRequests;
	// Keeps track of the currently selected requests for use in several methods
	static Request currentRequest;
	// When true, will display the newCommentForm
	private static boolean onCommentForm = false;
	// A list of all RequestComment objects that will be populated from the database
	private static List<RequestComment> allComments = new ArrayList<>();
	// A combo box that will allow selected an Admin to assign a request to
	private static ComboBox<String> comboBox_adminSelect = new ComboBox<String>();
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: Clears and rebuilds the Staff Requests page, reloading
	 * all requests and comments from the database to ensures newly created content
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
		// Adds button_NewRequest only if the user is a Staff
		if (ViewStaffRequests.theUser.getCurrentRole().equals("Staff")) {
			ViewStaffRequests.theRootPane.getChildren().add(
				ViewStaffRequests.button_NewRequest);
		}
		// Adds button_AssignedToMe only if the user is an Admin
		if (ViewStaffRequests.theUser.getCurrentRole().equals("Admin")) {
			ViewStaffRequests.theRootPane.getChildren().add(
				ViewStaffRequests.button_AssignedToMe);
		}
		
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
		// Always reload request comments from the database to ensure newly created comments
		// appear immediately
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
	 * <p> Method: performStaffRequests(Stage ps, User user) </p>
	 *
	 * <p> Description: Called when the Staff or Admin user navigates to this screen
	 * (e.g., from a button on Staff or Admin Home). Displays the corresponding View. </p>
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
	 */
	public static void performSearch() {
		// Allows the user to enter search words without regard to capitalization or spaces
		String searchWords = ViewStaffRequests.textfield_Search.getText().trim().toLowerCase();
		if (searchWords.isEmpty()) {
			repaintTheWindow();
			return;
		}
		
		ViewStaffRequests.requestCardList.getChildren().clear();
		ViewStaffRequests.scrollPane_RequestDetails.setContent(null);
		
		List<Request> requestList = allRequests.getAllRequests();
		
		// found will turn true as soon as at least one request is found matching search word(s)
		boolean found = false;
		for (Request request : requestList) {		
			String subject = request.getSubject().trim().toLowerCase();
			String description = request.getDescription().trim().toLowerCase();
			if (subject.contains(searchWords) || description.contains(searchWords)) {
			    found = true;
			    VBox card = createRequestCard(request);
			    if (card != null) {
			    	ViewStaffRequests.requestCardList.getChildren().add(card);
			    }
			}	
		}
		// If no requests are found matching the search word(s), display an error message
		if (!found) {
			ViewStaffRequests.requestCardList.getChildren().add(
				new Label("No requests matching: " + searchWords));
		}
	}
	
	
	/**********
	 * <p> Method: VBox createRequestCard(Request request) </p>
	 * 
	 * <p> Description: This method creates request cards to populate the request card
	 * scroll pane. The card contains information about a request, including the subject
	 * and author so a user can decide which request to view. They can then click on the
	 * card to view the full request. </p>
	 * 
	 * @return a VBox containing the newly created request card
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
	    
	    Label requestID = new Label("ID: " + request.getRequestID());
	    requestID.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
	    
	    // Format timestamp nicely
	    String formattedTime = "";
	    if (request.getCreatedAt() != null) {
	        formattedTime = request.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    
	    // Author and timestamp on same line
	    Label authorAndTime = new Label("by " + request.getRequestorUsername() + "  •  " + formattedTime);
	    authorAndTime.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
	    
	    // Status in lower right using HBox
	    Label status = new Label(request.getStatus());
	    status.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");
	    
	    javafx.scene.layout.HBox topRow = new javafx.scene.layout.HBox();
	    javafx.scene.layout.Region topSpacer = new javafx.scene.layout.Region();
	    javafx.scene.layout.HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);
	    topRow.getChildren().addAll(subject, topSpacer, requestID);
	    
	    javafx.scene.layout.HBox bottomRow = new javafx.scene.layout.HBox();
	    javafx.scene.layout.Region bottomSpacer = new javafx.scene.layout.Region();
	    javafx.scene.layout.HBox.setHgrow(bottomSpacer, javafx.scene.layout.Priority.ALWAYS);
	    bottomRow.getChildren().addAll(authorAndTime, bottomSpacer, status);
	    
	    requestCard.getChildren().addAll(topRow, bottomRow);
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
	 */
	protected static void displayRequestCards() {
	    // A list containing all Request objects that exist
		List<Request> requestList = allRequests.getAllRequests();
		// A list that will be populated with the request objects matching the
		// specified status
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
	    else if (ViewStaffRequests.selectedStatus.equals("Assigned To Me")) {
	    	for (Request request : requestList) {
	    		if (request.getAssignedTo().equals(ViewStaffRequests.theUser.getUserName())
	    				&& !request.getStatus().equals("Closed")) {
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
	            if (!request.getStatus().equals("Closed")) {
	            	newRequestList.add(request);
	            }
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
	* <p> Method: displayRequest(Request request) </p>
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
		
	    String formattedTime1 = "";
	    if (request.getCreatedAt() != null) {
	        formattedTime1 = request.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    Label createdAt = new Label("Created At: " + formattedTime1);
	    Label requestID = new Label("Request ID: " + request.getRequestID());
	    Label requestor = new Label("Requestor: " + request.getRequestorUsername());
	    Label status = new Label("Status: " + request.getStatus());
	    Label assignedTo = new Label("Assigned To: " + request.getAssignedTo());
	    
	    Button button_ReOpen = new Button("Re-Open");
    	button_ReOpen.setOnAction((_) -> {
    		// A pop up window that allows for entering a new description for the
    		// re-opened request
    		TextInputDialog reopenRequest = new TextInputDialog();
    		reopenRequest.setHeight(300);
    		reopenRequest.setWidth(600);
    		reopenRequest.setTitle("Re-Open Request");
    		reopenRequest.setHeaderText("Enter a description for the new request");
    		reopenRequest.setContentText("Description: ");
			Optional<String> result = reopenRequest.showAndWait();
			if (result.isPresent()) {
				String newDescription = result.get();
				Request newRequest = allRequests.reopenRequest(currentRequest.getRequestID(),
						newDescription);
				allRequests.addRequest(newRequest);;
				try {
					theDatabase.createRequest(newRequest);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				theDatabase.updateClosedRequestID(newRequest.getRequestID(), currentRequest.getRequestID());
				currentRequest = newRequest;
				repaintTheWindow();
	    		displayRequest(currentRequest);
			}
			else {
				displayRequest(currentRequest);
			}
    		});

	    // Adds admin specific functions to the request details pane
	    HBox adminFunctions = new HBox(5);
	    if (ViewStaffRequests.theUser.getCurrentRole().equals("Admin")) {
	    	// A combo box that will contain a list of all Admins
	    	setupComboBoxUI(comboBox_adminSelect, "Dialog", 14, 100);
	    	List<String> userList = theDatabase.getUserList();
	    	// Removes <User> from userList so only usernames are searched
	    	userList.remove(0);
	    	List<String> adminList = new ArrayList<>();
	    	for (String username : userList) {
	    		User tempUser = theDatabase.getUserObject(username);
	    		// Only adds a username to the adminList if they have an Admin role
	    		if (tempUser.getAdminRole()) {
	    			adminList.add(tempUser.getUserName());
	    		}
	    	}
	    	// Sorts usernames in the adminList alphabetically
	    	adminList.sort(String.CASE_INSENSITIVE_ORDER);
	    	// Adds default combobox selection as the first element of adminList
	    	adminList.add(0, "<Assign Admin>");
	    	// Adds Unassigned option as the second element of adminList
	    	adminList.add(1, "Unassigned");
	    	comboBox_adminSelect.setItems(FXCollections.observableArrayList(adminList));
	    	comboBox_adminSelect.getSelectionModel().select(0);
	    	
	    	Button button_Assign = new Button("Assign");
	    	button_Assign.setOnAction((_) -> {
				// Does nothing if no selection is made from the adminSelect list
	    		if (comboBox_adminSelect.getValue().equals("<Assign Admin>")) {
					return;
				}
				else if (comboBox_adminSelect.getValue().equals("Unassigned")) {
					// Unassigning a request will set its status to Open
					request.setStatus("Open");
					request.setAssignedTo(comboBox_adminSelect.getValue());
					theDatabase.updateRequestStatus(request.getRequestID(), "Open");
					theDatabase.updateRequestAssigned(request.getRequestID(), comboBox_adminSelect.getValue());
					Alert assignConfirmation = new Alert(Alert.AlertType.INFORMATION);
					assignConfirmation.setTitle("Success!");
					assignConfirmation.setHeaderText("Request has been successfully unassigned.");
					assignConfirmation.showAndWait();
				}
				else {
					request.setStatus("Assigned");
					request.setAssignedTo(comboBox_adminSelect.getValue());
					theDatabase.updateRequestStatus(request.getRequestID(), "Assigned");
					theDatabase.updateRequestAssigned(request.getRequestID(), comboBox_adminSelect.getValue());
					Alert assignConfirmation = new Alert(Alert.AlertType.INFORMATION);
					assignConfirmation.setTitle("Success!");
					assignConfirmation.setHeaderText("Request has been successfully assigned to: "
							+ comboBox_adminSelect.getValue());
					assignConfirmation.showAndWait();
				}
				repaintTheWindow();
	    		displayRequest(currentRequest);
				});
	    		
	    	Button button_Close = new Button("Close");
	    	button_Close.setOnAction((_) -> {
	    		// A pop-up window that allows for entering Admin notes that will be displayed in
	    		// the closed request
	    		TextInputDialog adminNotes = new TextInputDialog();
	    		adminNotes.setHeight(300);
	    		adminNotes.setWidth(600);
	    		adminNotes.setTitle("Close Request");
				adminNotes.setHeaderText("Admin Notes");
				adminNotes.setContentText("Notes: ");
				Optional<String> result = adminNotes.showAndWait();
				if (result.isPresent()) {
					String notes = result.get();
					allRequests.closeRequest(currentRequest.getRequestID(),
						ViewStaffRequests.theUser.getUserName(), notes);
					theDatabase.updateRequestAssigned(request.getRequestID(), ViewStaffRequests.theUser.getUserName());
					theDatabase.updateAdminNotes(request.getRequestID(), notes);
					theDatabase.updateRequestStatus(request.getRequestID(), "Closed");
					theDatabase.updateRequestIsClosed(request.getRequestID(), true);
					theDatabase.updateRequestClosedAt(request.getRequestID(), Timestamp.valueOf(LocalDateTime.now()));
					repaintTheWindow();
		    		displayRequest(currentRequest);
				}
				else {
					repaintTheWindow();
					displayRequest(currentRequest);
				}
				});
	    	
	    	// Only adds the Re-Open button to Closed requests
	    	if (currentRequest.getStatus().equals("Closed")) {
	    		adminFunctions.getChildren().add(button_ReOpen);
	    	}
	    	// If the request is not Closed, populate the following Admin functions
	    	else {
	    		adminFunctions.getChildren().addAll(
	    		comboBox_adminSelect,
	    		button_Assign,
	    		button_Close
	    			);
	    	}
	    }
	    
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
				requestID,
				createdAt,
				requestor,
				status,
				assignedTo
				);
				
		if (currentRequest.getStatus().equals("Closed")) {
			fullRequest.getChildren().add(button_ReOpen);
		}
		
		fullRequest.getChildren().addAll(
				adminFunctions,
				description,
				button_Comment
				);

		// If the Comment button was clicked, populate a newCommentForm
		// in the Request Details scroll pane
		if (onCommentForm == true) {
			VBox commentForm = newCommentForm();
			fullRequest.getChildren().add(commentForm);
			onCommentForm = false;
		}
		
		// If the request has Admin notes, display them in the Request Details
		// scroll pane
		if (currentRequest.getAdminNotes() != null) {
			VBox displayAdminNotes = new VBox(5);
			displayAdminNotes.setPadding(new Insets(15));
			
			HBox topRow = new javafx.scene.layout.HBox();
		    Region topSpacer = new javafx.scene.layout.Region();
		    HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);
		    
			String formattedTime2 = "";
		    if (request.getClosedAt() != null) {
		        formattedTime2 = request.getClosedAt().format(
		            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
		    }
		    
		    Label closedAt = new Label(formattedTime2);
			
		    Label admin = new Label("*Admin Notes*");
			admin.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;");
			
			topRow.getChildren().addAll(admin, topSpacer, closedAt);
			
			TextArea adminNotes = new TextArea(currentRequest.getAdminNotes());
			adminNotes.setPrefHeight(100);
			adminNotes.setWrapText(true);
			adminNotes.setEditable(false);
			
			displayAdminNotes.getChildren().addAll(
					topRow,
					adminNotes);
			fullRequest.getChildren().add(displayAdminNotes);
		}
		
		// Searches through the list of allComments to find those associated with
		// the currently selected request
		for (RequestComment comment : allComments) {
			if (comment.getRequestID() == currentRequest.getRequestID()) {
				fullRequest.getChildren().add(displayRequestComment(comment));
			}
		}
		
		ViewStaffRequests.scrollPane_RequestDetails.setContent(fullRequest);
	} 
	
	
	/**********
	* <p> Method: VBox displayRequestComment(RequestComment comment) </p>
	*
	* <p> Description: This method populates the request details Scroll Pane with the
	* details of the RequestComment object that is passed as an argument. </p>
	*
	* @return a VBox containing the details of the RequestComment object
	* 
	* @param comment the RequestComment object to display in the request details Scroll Pane
	*
	*/
	protected static VBox displayRequestComment(RequestComment comment) {
	    VBox requestComment = new VBox(5);
	    requestComment.setPadding(new Insets(15));

	    HBox topRow = new javafx.scene.layout.HBox();
	    Region topSpacer = new javafx.scene.layout.Region();
	    HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);
	   
	    Label commenter = new Label("Commenter: " + comment.getCommenterUsername());
	    commenter.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;");
	    
	    String formattedTime = "";
	    if (comment.getCreatedAt() != null) {
	        formattedTime = comment.getCreatedAt().format(
	            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
	    }
	    Label createdAt = new Label(formattedTime);
	    
	    topRow.getChildren().addAll(commenter, topSpacer, createdAt);
		
	    TextArea description = new TextArea(comment.getDescription());
	    description.setPrefHeight(100);
	    description.setWrapText(true);
	    description.setEditable(false);
	    
	    requestComment.getChildren().addAll(
				topRow,
				description
				);
	    return requestComment;
	} 
	

	/**********
	 * <p> Method: VBox newCommentForm() </p>
	 * 
	 * <p> Description: This method populates the new comment VBox in GUI Area 4. It
	 * contains all necessary fields for the user to create a comment. </p>
	 * 
	 * @return a VBox containing the comment form with a text area and submit/cancel buttons
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
				repaintTheWindow();
				displayRequest(currentRequest);
			});
		button_Cancel.setOnAction((_) ->
			{displayRequest(currentRequest);
			});
		return vBox_CommentForm;
		}
	
	
	/**********
	 * <p> Method: performCreateComment(int requestID, String commenterUsername, String description) </p>
	 * 
	 * <p> Description: This method validates description input, saves the comment to the database,
	 * clears the form fields, and navigates back to the Staff Requests page. An error message is
	 * displayed if validation fails or a database exception occurs. </p>
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
	 * <p> Method: performHome() </p>
	 * 
	 * <p> Description: This method returns the user to the Staff or Admin home page, depending on their role </p>
	 * 
	 */
	protected static void performHome() {
		// Checks if the user if an Admin or a Staff to determine what home
		// page to take them to
		if (ViewStaffRequests.theUser.getCurrentRole().equals("Admin")) {
			ViewAdminHome.displayAdminHome(ViewStaffRequests.theStage, 
					ViewStaffRequests.theUser);
		}
		if (ViewStaffRequests.theUser.getCurrentRole().equals("Staff")) {
			ViewStaffHome.displayStaffHome(ViewStaffRequests.theStage, 
					ViewStaffRequests.theUser);
		}
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
	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w) {
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
	}
}