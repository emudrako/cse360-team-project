module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	requires org.junit.jupiter.api;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
