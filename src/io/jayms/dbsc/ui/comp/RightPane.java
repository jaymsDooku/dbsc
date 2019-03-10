package io.jayms.dbsc.ui.comp;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.TabEditorData;
import io.jayms.dbsc.ui.AbstractUIModule;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.Getter;

public class RightPane extends AbstractUIModule {

	@Getter private SplitPane rightPane;
	
	private HBox topPane;
	private TextField pathDisplay;
	private Button ssFileChooseBtn;
	private FileChooser ssFileChooser;
	
	@Getter private File chosenFile;
	
	@Getter private TabPane queriesTab;
	
	public RightPane(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		rightPane = new SplitPane();
		
		topPane = new HBox();
		topPane.setMaxWidth(Double.MAX_VALUE);
		
		ssFileChooser = new FileChooser();
		ssFileChooser.setTitle("Choose Spreadsheet Destination");
		ssFileChooser.getExtensionFilters().add(new ExtensionFilter("Spreadsheet Files", ".xlsx"));
		
		pathDisplay = new TextField();
		HBox.setHgrow(pathDisplay, Priority.ALWAYS);
		ssFileChooseBtn = new Button("Browse...");
		
		ssFileChooseBtn.setOnMouseClicked((e) -> {
			chosenFile = ssFileChooser.showOpenDialog(masterUI.getStage());
		});
		topPane.getChildren().addAll(pathDisplay, ssFileChooseBtn);
		
		queriesTab = new TabPane();
		
		rightPane.setDividerPosition(0, DBSCGraphicalUserInterface.rightTopPaneHeight);
		rightPane.setOrientation(Orientation.VERTICAL);
		rightPane.getItems().addAll(topPane, queriesTab);
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
	}
	
	public void runQuery(MouseEvent e) {
		if (chosenFile == null) {
			Alert alert = new Alert(AlertType.ERROR, "You need to select a file destination first!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
		if (curTab == null) {
			System.out.println("No tab open.");
			return;
		}
		String title = curTab.getText();
		Object tabDataObj = curTab.getUserData();
		if (tabDataObj == null || !(tabDataObj instanceof TabEditorData)) {
			System.out.println("No tab editor data held");
			return;
		}
		TabEditorData tabData = (TabEditorData) tabDataObj;
		
	}
}
