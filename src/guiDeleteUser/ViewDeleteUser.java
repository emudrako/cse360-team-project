package guiDeleteUser;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

public class ViewDeleteUser {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");

    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

    protected static Label label_SelectUser = new Label("Select a user to delete:");
    protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();

    protected static Button button_DeleteUser = new Button("Delete This User");

    protected static Line line_Separator4 = new Line(20, 525, width-20,525);

    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    private static ViewDeleteUser theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    public static Scene theDeleteUserScene = null;
    protected static String theSelectedUser = "";

    public static void displayDeleteUser(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) theView = new ViewDeleteUser();
        ControllerDeleteUser.repaintTheWindow();
    }

    public ViewDeleteUser() {
        theRootPane = new Pane();
        theDeleteUserScene = new Scene(theRootPane, width, height);

        // Area 1
        label_PageTitle.setText("Delete User Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((_) -> 
            {guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

        // Area 2 Select User (with current user removed)
        setupLabelUI(label_SelectUser, "Arial", 20, 400, Pos.BASELINE_LEFT, 20, 130);
        setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);

        List<String> userList = theDatabase.getUserList();
        
        // Remove logged in user
        String currentUsername = theUser.getUserName();
        userList.removeIf(u -> u != null && u.trim().equalsIgnoreCase(currentUsername.trim()));
        
        // Make sure prompt is at the top and only once
        userList.removeIf(u -> "<Select a User>".equals(u));
        userList.add(0, "<Select a User>");

        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
        combobox_SelectUser.getSelectionModel().select(0);
        
        combobox_SelectUser.getSelectionModel().selectedItemProperty()
             .addListener((@SuppressWarnings("unused") ObservableValue<? extends String> obs,
                          @SuppressWarnings("unused") String old,
                          @SuppressWarnings("unused") String newVal) -> 
                          {ControllerDeleteUser.doSelectUser();});

        // Delete Button
        setupButtonUI(button_DeleteUser, "Dialog", 18, 220, Pos.CENTER, 280, 200);
        button_DeleteUser.setOnAction((_) -> ControllerDeleteUser.performDeleteUser());

        // Area 3
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((_) -> ControllerDeleteUser.performReturn());

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((_) -> ControllerDeleteUser.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((_) -> ControllerDeleteUser.performQuit());
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, double y){
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}