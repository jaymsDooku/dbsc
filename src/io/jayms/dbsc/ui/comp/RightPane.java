package io.jayms.dbsc.ui.comp;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.ui.AbstractUIModule;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
	
	@Getter private BorderPane workspace;
	@Getter private TabPane queriesTab;
	@Getter private ActionBar actionBar;
	
	public RightPane(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		rightPane = new SplitPane();
		
		topPane = new HBox();
		topPane.setMaxWidth(Double.MAX_VALUE);
		
		chosenFile = new File("test.xlsx");
		
		ssFileChooser = new FileChooser();
		ssFileChooser.setTitle("Choose Spreadsheet Destination");
		ssFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		ssFileChooser.setInitialFileName("test.xlsx");
		ssFileChooser.getExtensionFilters().add(new ExtensionFilter("Spreadsheet Files", ".xlsx"));
		
		pathDisplay = new TextField();
		pathDisplay.textProperty().bind(Bindings.concat(ssFileChooser.initialDirectoryProperty().asString(), "\\", ssFileChooser.initialFileNameProperty().asString()));
		pathDisplay.setEditable(false);
		HBox.setHgrow(pathDisplay, Priority.ALWAYS);
		
		ssFileChooseBtn = new Button("Browse...");
		
		ssFileChooseBtn.setOnMouseClicked((e) -> {
			chosenFile = ssFileChooser.showOpenDialog(masterUI.getStage());
		});
		topPane.getChildren().addAll(pathDisplay, ssFileChooseBtn);
		
		workspace = new BorderPane();
		
		queriesTab = new TabPane();
		
		actionBar = new ActionBar(masterUI);
		actionBar.init();
		
		workspace.setCenter(queriesTab);
		workspace.setBottom(actionBar.getActionBar());
		
		rightPane.setDividerPosition(0, DBSCGraphicalUserInterface.rightTopPaneHeight);
		rightPane.setDividerPosition(1, 0.95);
		rightPane.setOrientation(Orientation.VERTICAL);
		rightPane.getItems().addAll(topPane, workspace);
	}
	
	@Override
	public void show() {
	}
	
	@Override
	public void close() {
	}
}
