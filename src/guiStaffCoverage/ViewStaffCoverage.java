package guiStaffCoverage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/*******
 * <p> Title: ViewStaffCoverage Class. </p>
 *
 * <p> Description: The View for the Staff Answer-Coverage Report screen.
 * Displays a table showing each student's deduplicated peer-reply count
 * and flags students who have replied to fewer than three distinct peers.
 * Staff use this screen to verify that each student has met the three-peer
 * participation requirement without manually scanning every reply thread.
 * This screen is read-only — no modification to reply data is possible.
 * Satisfies Story 5: Answer-Coverage Computation.
 * Styled to match the team's UI standard established in ViewUserUpdate.java:
 * dark navy background with a white rounded card in the center. </p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00  2026-07-19  Initial version — coverage table with
 *                             flagging for students below threshold.
 */
public class ViewStaffCoverage {

    /*-*******************************************************************************************
    Attributes
     */

    // Window dimensions consistent with the team's UI style standards
    private static double width  = 1000;
    private static double height = 900;

    // GUI Area 1 — top bar (outside the card)
    protected static Label  label_UserDetails = new Label();
    protected static Button button_Return     = new Button("Home");
    protected static Button button_Quit       = new Button("X");

    // GUI Area 2 — inside the card: title and subtitle
    protected static Label label_PageTitle = new Label("Answer-Coverage Report");
    protected static Label label_Subtitle  = new Label(
        "Students below threshold have replied to fewer than 3 distinct peers.");

    // GUI Area 3 — column headers (inside the card, positioned absolutely)
    protected static Label label_ColStudent = new Label("Student");
    protected static Label label_ColPeers   = new Label("Distinct Peers");
    protected static Label label_ColStatus  = new Label("Status");

    // GUI Area 4 — scrollable table rows (inside the card)
    protected static VBox       vbox_TableRows   = new VBox(2);
    protected static ScrollPane scrollPane_Table = new ScrollPane(vbox_TableRows);

    // Singleton, database, scene references
    private static ViewStaffCoverage theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    protected static Pane  theRootPane;
    protected static User  theUser;
    private static Scene   theViewStaffCoverageScene;

    /*-*******************************************************************************************
    Constructors
     */

    /**********
     * <p> Method: displayStaffCoverage(Stage ps, User user) </p>
     *
     * <p> Description: Entry point called when staff navigates to this screen
     * from Staff Home. Creates the singleton View if needed, refreshes the
     * coverage table, then shows the scene. The table refreshes on every call
     * so staff always see current participation data.
     * Validated by CoverageReportTests. </p>
     *
     * @param ps   specifies the JavaFX Stage to be used for this GUI
     * @param user specifies the currently logged-in staff or admin user
     */
    public static void displayStaffCoverage(Stage ps, User user) {
        theStage = ps;
        theUser  = user;
        if (theView == null) theView = new ViewStaffCoverage();
        theDatabase.getUserAccountDetails(user.getUserName());
        label_UserDetails.setText("User: " + theUser.getUserName());
        theStage.setTitle("CSE 360 Foundations: Staff Coverage Page");

        // Refresh table every visit so data stays current
        loadCoverageTable();

        theStage.setScene(theViewStaffCoverageScene);
        theStage.show();
    }

    /**********
     * <p> Method: ViewStaffCoverage() </p>
     *
     * <p> Description: Initializes all GUI widgets. Singleton — runs once.
     * Subsequent visits call loadCoverageTable() to refresh data without
     * rebuilding the layout. Uses a Rectangle with rounded corners and a
     * DropShadow as the white card, matching the pattern in ViewUserUpdate.java.
     * All widgets are positioned absolutely on theRootPane on top of the card. </p>
     */
    private ViewStaffCoverage() {
        theRootPane = new Pane();
        theViewStaffCoverageScene = new Scene(theRootPane, width, height);

        // Dark navy background — matches the team's UI style
        theRootPane.setStyle("-fx-background-color: #041E42;");

        // White rounded card — same pattern as ViewUserUpdate.java
        // Uses Rectangle + DropShadow, not a nested Pane
        javafx.scene.shape.Rectangle card = new javafx.scene.shape.Rectangle();
        card.setWidth(900);
        card.setHeight(780);
        card.setX(50);
        card.setY(70);
        card.setArcWidth(20);
        card.setArcHeight(20);
        card.setFill(javafx.scene.paint.Color.WHITE);
        card.setEffect(new javafx.scene.effect.DropShadow(
            20, javafx.scene.paint.Color.rgb(0, 0, 0, 0.3)));

        // ── GUI Area 1: top bar (outside the card) ────────────────────────

        setupLabelUI(label_UserDetails, "Arial", 12, 200, Pos.BASELINE_LEFT, 20, 10);
        label_UserDetails.setStyle("-fx-text-fill: white;");

        setupButtonUI(button_Return, "Dialog", 12, 70, Pos.CENTER, 880, 10);
        button_Return.setOnAction((_) -> { ControllerStaffCoverage.performReturn(); });
        button_Return.setStyle(
            "-fx-background-color: #0062A3; -fx-text-fill: white; -fx-background-radius: 5;");

        setupButtonUI(button_Quit, "Dialog", 12, 30, Pos.CENTER, 960, 10);
        button_Quit.setOnAction((_) -> { ControllerStaffCoverage.performQuit(); });
        button_Quit.setStyle(
            "-fx-background-color: #BF0D3E; -fx-text-fill: white; -fx-background-radius: 5;");

        // ── GUI Area 2: title and subtitle (inside the card) ─────────────

        // Page title — positioned inside the card
        setupLabelUI(label_PageTitle, "Arial", 24, 860, Pos.CENTER, 70, 105);
        label_PageTitle.setStyle("-fx-text-fill: #041E42; -fx-font-weight: bold;");

        // Subtitle
        setupLabelUI(label_Subtitle, "Arial", 12, 860, Pos.CENTER, 70, 140);
        label_Subtitle.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");

        // ── GUI Area 3: column header row (inside the card) ───────────────

        // Column headers positioned as individual labels — absolute layout
        // matching the team's Pane-based approach (no HBox needed)
        setupLabelUI(label_ColStudent, "Arial", 13, 340, Pos.BASELINE_LEFT, 70, 175);
        label_ColStudent.setStyle("-fx-font-weight: bold; -fx-text-fill: #041E42;");

        setupLabelUI(label_ColPeers, "Arial", 13, 240, Pos.BASELINE_LEFT, 410, 175);
        label_ColPeers.setStyle("-fx-font-weight: bold; -fx-text-fill: #041E42;");

        setupLabelUI(label_ColStatus, "Arial", 13, 200, Pos.BASELINE_LEFT, 650, 175);
        label_ColStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #041E42;");

        // Thin separator line under column headers
        javafx.scene.shape.Line headerLine =
            new javafx.scene.shape.Line(70, 197, 940, 197);
        headerLine.setStyle("-fx-stroke: #E0E0E0;");

        // ── GUI Area 4: scrollable table (inside the card) ────────────────

        // The scroll pane sits inside the card boundaries
        vbox_TableRows.setStyle("-fx-padding: 2;");
        scrollPane_Table.setLayoutX(60);
        scrollPane_Table.setLayoutY(200);
        scrollPane_Table.setPrefWidth(878);
        scrollPane_Table.setPrefHeight(620);
        scrollPane_Table.setFitToWidth(true);
        scrollPane_Table.setStyle(
            "-fx-background-color: white; -fx-border-color: transparent;");

        // Add all widgets to the root pane — card first so everything sits on top of it
        theRootPane.getChildren().addAll(
            card,
            label_UserDetails, button_Return, button_Quit,
            label_PageTitle, label_Subtitle,
            label_ColStudent, label_ColPeers, label_ColStatus,
            headerLine, scrollPane_Table);
    }

    /*-*******************************************************************************************
    Coverage table loading
     */

    /**********
     * <p> Method: loadCoverageTable() </p>
     *
     * <p> Description: Queries the database for all users, posts, and replies,
     * runs computeCoverage(), and rebuilds the table rows. Flagged students
     * (below PEER_THRESHOLD) are listed first so staff see who needs attention
     * immediately. Passing students follow below a divider label. Students with
     * zero replies appear in the flagged section with count zero — no student
     * is omitted (satisfies AC5.5). Calls ControllerStaffCoverage.computeCoverage(),
     * the same method validated by CoverageReportTests. </p>
     */
    protected static void loadCoverageTable() {
        vbox_TableRows.getChildren().clear();

        try {
            List<Post>   allPosts   = theDatabase.getPostObjects();
            List<Reply>  allReplies = theDatabase.getReplyObjects();

            // getUserList() prepends "<User>" placeholder — skip it
            List<String> allUsers = theDatabase.getUserList();

            // Run coverage computation — same method tested by CoverageReportTests
            Map<String, Integer> coverageMap =
                ControllerStaffCoverage.computeCoverage(allReplies, allPosts);

            // Flagged students first — staff see who needs attention at the top
            boolean anyFlagged = false;
            for (String username : allUsers) {
                if (username.equals("<User>")) continue;
                int count = coverageMap.getOrDefault(username, 0);
                if (!ControllerStaffCoverage.meetsThreshold(count)) {
                    vbox_TableRows.getChildren().add(buildRow(username, count, true));
                    anyFlagged = true;
                }
            }

            // Divider between flagged and passing sections
            if (anyFlagged) {
                Label divider = new Label(
                    "─────────────── Meeting threshold ───────────────");
                divider.setStyle(
                    "-fx-text-fill: #888888; -fx-font-size: 11; -fx-padding: 4 0;");
                vbox_TableRows.getChildren().add(divider);
            }

            // Passing students below the divider
            for (String username : allUsers) {
                if (username.equals("<User>")) continue;
                int count = coverageMap.getOrDefault(username, 0);
                if (ControllerStaffCoverage.meetsThreshold(count)) {
                    vbox_TableRows.getChildren().add(buildRow(username, count, false));
                }
            }

            // Empty state
            if (allUsers == null || allUsers.size() <= 1) {
                Label empty = new Label("No student accounts found in the system.");
                empty.setStyle("-fx-text-fill: #888888; -fx-font-size: 13;");
                vbox_TableRows.getChildren().add(empty);
            }

        } catch (SQLException e) {
            Label error = new Label("Error loading coverage data: " + e.getMessage());
            error.setStyle("-fx-text-fill: #BF0D3E;");
            vbox_TableRows.getChildren().add(error);
        }
    }

    /**********
     * <p> Method: buildRow(String username, int count, boolean flagged) </p>
     *
     * <p> Description: Builds one table row as an HBox with the student's
     * username, deduplicated peer count, and a colored status label.
     * Flagged rows have a light red background. Passing rows are white. </p>
     *
     * @param username the student's username
     * @param count    the deduplicated peer-reply count
     * @param flagged  true if the student is below PEER_THRESHOLD
     * @return a styled HBox representing one table row
     */
    private static HBox buildRow(String username, int count, boolean flagged) {
        HBox row = new HBox();
        row.setStyle(flagged
            ? "-fx-background-color: #FFF0F0; -fx-padding: 6 10;"
            : "-fx-background-color: #FFFFFF; -fx-padding: 6 10;");

        Label lblName = new Label(username);
        lblName.setMinWidth(340);
        lblName.setFont(Font.font("Arial", 12));

        Label lblCount = new Label(String.valueOf(count));
        lblCount.setMinWidth(240);
        lblCount.setFont(Font.font("Arial", 12));

        Label lblStatus = new Label(
            flagged ? "⚠ BELOW THRESHOLD" : "✓ Meets requirement");
        lblStatus.setMinWidth(200);
        lblStatus.setFont(Font.font("Arial", 12));
        lblStatus.setStyle(flagged
            ? "-fx-text-fill: #BF0D3E; -fx-font-weight: bold;"
            : "-fx-text-fill: #007A33;");

        row.getChildren().addAll(lblName, lblCount, lblStatus);
        return row;
    }

    /*-********************************************************************************************
    Helper methods to reduce code length
     */

    /**********
     * Private local method to initialize the standard fields for a label
     *
     * @param l  The Label object to be initialized
     * @param ff The font to be used
     * @param f  The size of the font to be used
     * @param w  The width of the Label
     * @param p  The alignment (e.g. left, centered, or right)
     * @param x  The location from the left edge (x axis)
     * @param y  The location from the top (y axis)
     */
    private static void setupLabelUI(Label l, String ff, double f,
            double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**********
     * Private local method to initialize the standard fields for a button
     *
     * @param b  The Button object to be initialized
     * @param ff The font to be used
     * @param f  The size of the font to be used
     * @param w  The width of the Button
     * @param p  The alignment (e.g. left, centered, or right)
     * @param x  The location from the left edge (x axis)
     * @param y  The location from the top (y axis)
     */
    private static void setupButtonUI(Button b, String ff, double f,
            double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}