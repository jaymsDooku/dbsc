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
	private Button ssFolderChooseBtn;
	private FileChooser ssFolderChooser;
	private TextField ssFileTxt;
	
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
		
		File defFolder = new File(System.getProperty("user.dir"));
		chosenFile = new File(defFolder, "test.xlsx");
		
		ssFileTxt = new TextField(chosenFile.getName());
		
		ssFolderChooser = new FileChooser();
		ssFolderChooser.setTitle("Choose Spreadsheet Destination");
		ssFolderChooser.setInitialDirectory(defFolder);
		ssFolderChooser.getExtensionFilters().add(new ExtensionFilter("Spreadsheet Files", ".xlsx"));
		
		pathDisplay = new TextField();
		pathDisplay.textProperty().bind(ssFolderChooser.initialDirectoryProperty().asString());
		pathDisplay.setEditable(false);
		HBox.setHgrow(pathDisplay, Priority.ALWAYS);
		
		ssFolderChooseBtn = new Button("Browse...");
		
		ssFolderChooseBtn.setOnMouseClicked((e) -> {
			File folder = ssFolderChooser.showOpenDialog(masterUI.getStage());
			String fileName = ssFileTxt.getText();
			String fileNameExt = fileName + ".xlsx";
			chosenFile = new File(folder, fileNameExt);
		});
		topPane.getChildren().addAll(pathDisplay, ssFolderChooseBtn, ssFileTxt);
		
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
