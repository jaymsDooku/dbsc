package io.jayms.dbsc;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DBSCGraphicalUserInterface extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private static final double leftPaneWidth = 0.3;
	
	private SplitPane masterPane;
	
	private VBox leftPane;
	private Button newConnectionsBtn;
	private TreeView<String> connections;
	
	private SplitPane rightPane;
	private TextField queryTextBox;
	
	private void leftPane() {
		leftPane = new VBox();
		
		newConnectionsBtn = new Button("New Connections");
		newConnectionsBtn.setMaxWidth(Double.MAX_VALUE);
		
		TreeItem<String> rootItem = new TreeItem<>("Connections");
		TreeItem<String> connItem = new TreeItem<>("127.0.0.1");
		TreeItem<String> reportItem = new TreeItem<>("Finance Report");
		connItem.getChildren().add(reportItem);
		rootItem.getChildren().add(connItem);
		connections = new TreeView<>(rootItem);
		
		leftPane.getChildren().addAll(newConnectionsBtn, connections);
	}
	
	private void rightPane() {
		rightPane = new SplitPane();
		
		queryTextBox = new TextField();
		
		rightPane.setOrientation(Orientation.VERTICAL);
		rightPane.getItems().add(queryTextBox);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("DBSC");
		
		masterPane = new SplitPane();
		masterPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		leftPane();
		rightPane();
		
		masterPane.setDividerPosition(0, leftPaneWidth);
		masterPane.getItems().addAll(leftPane, rightPane);
		
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			onWidthResize(oldVal, newVal);
		});
		
		Scene scene = new Scene(masterPane, 800, 600);
		stage.setScene(scene);
		
		stage.show();
	}
	
	private void onWidthResize(Number oldVal, Number newVal) {
		masterPane.setDividerPosition(0, leftPaneWidth);
	}

}
