package guiListUsers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import java.util.List;

public class ViewListUsers {

    private static Stage theStage;
    private static User theUser;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    private static TextArea listArea = new TextArea();
    private static Button button_Return = new Button("Return");

    public static void displayListUsers(Stage stage, User user) {
        theStage = stage;
        theUser = user;
        new ViewListUsers();
    }

    public ViewListUsers() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("All Registered Users");
        title.setFont(javafx.scene.text.Font.font("Arial", 24));

        listArea.setEditable(false);
        listArea.setPrefSize(720, 420);
        listArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

        button_Return.setOnAction(_ -> {
            guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
        });

        root.getChildren().addAll(title, listArea, button_Return);

        Scene scene = new Scene(root, 780, 580);
        theStage.setScene(scene);
        theStage.setTitle("List All Users");
        theStage.show();

        loadAndDisplayUsers();
    }

    private void loadAndDisplayUsers() {
        List<String> userList = theDatabase.getUserList();
        StringBuilder sb = new StringBuilder();

        for (String username : userList) {
            if (username.equals("<Select a User>")) continue;
            
            sb.append(theDatabase.getUserDetailsForList(username));
            sb.append("----------------------------------------\n");
        }

        listArea.setText(sb.toString());
    }
}