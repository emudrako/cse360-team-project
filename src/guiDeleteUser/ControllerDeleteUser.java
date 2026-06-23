package guiDeleteUser;

import database.Database;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.List;
import java.util.Optional;

public class ControllerDeleteUser {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    public ControllerDeleteUser() {}

    protected static void doSelectUser() {
        String selected = (String) ViewDeleteUser.combobox_SelectUser.getValue();
        ViewDeleteUser.theSelectedUser = (selected != null && !selected.equals("<Select a User>")) ? selected : "";
        repaintTheWindow();
    }

    protected static void repaintTheWindow() {
        ViewDeleteUser.theRootPane.getChildren().clear();
        
        ViewDeleteUser.theRootPane.getChildren().addAll(
            ViewDeleteUser.label_PageTitle,
            ViewDeleteUser.label_UserDetails,
            ViewDeleteUser.button_UpdateThisUser,
            ViewDeleteUser.line_Separator1,
            ViewDeleteUser.label_SelectUser,
            ViewDeleteUser.combobox_SelectUser,
            ViewDeleteUser.button_DeleteUser,
            ViewDeleteUser.line_Separator4,
            ViewDeleteUser.button_Return,
            ViewDeleteUser.button_Logout, 
            ViewDeleteUser.button_Quit
        );

        ViewDeleteUser.theStage.setTitle("Delete User Page");
        ViewDeleteUser.theStage.setScene(ViewDeleteUser.theDeleteUserScene);
        ViewDeleteUser.theStage.show();
    }

    protected static void performDeleteUser() {
        String userToDelete = ViewDeleteUser.theSelectedUser;
        
        if (userToDelete == null || userToDelete.isEmpty() || userToDelete.equals("<Select a User>")) {
            System.out.println("Please select a user to delete.");
            return;
        }

        // Prevent deleting self
        if (userToDelete.equals(ViewDeleteUser.theUser.getUserName())) {
            showError("You cannot delete your own account!");
            return;
        }

        // Confirmation for deleting
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User Confirmation");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Are you sure you want to delete user:\n" + userToDelete);

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // safety prevent deleting the last admin
            if (isLastAdmin(userToDelete)) {
                showError("Cannot delete this user.\nAt least one Admin account must remain in the system.");
                return;
            }

            if (theDatabase.deleteUser(userToDelete)) {
                System.out.println("User '" + userToDelete + "' deleted successfully.");
                
                // refresh list
                List<String> freshList = theDatabase.getUserList();
                String current = ViewDeleteUser.theUser.getUserName();
                freshList.removeIf(u -> u != null && u.trim().equalsIgnoreCase(current.trim()));
                freshList.removeIf(u -> "<Select a User>".equals(u));
                freshList.add(0, "<Select a User>");
                
                ViewDeleteUser.combobox_SelectUser.setItems(FXCollections.observableArrayList(freshList));
                ViewDeleteUser.combobox_SelectUser.getSelectionModel().select(0);
                ViewDeleteUser.theSelectedUser = "";
                
            } else {
                showError("Failed to delete user: " + userToDelete);
            }
        }
    }

    // Check if deleting this user would remove the last admin
    private static boolean isLastAdmin(String usernameToDelete) {
        return false;
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage, ViewDeleteUser.theUser);
    }

    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
    }

    protected static void performQuit() {
        System.exit(0);
    }
}