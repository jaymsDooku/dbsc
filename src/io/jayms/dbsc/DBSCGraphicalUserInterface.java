package io.jayms.dbsc;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DoubleBandFormatHolder;
import io.jayms.dbsc.model.FontHolder;
import io.jayms.dbsc.model.StyleHolder;
import io.jayms.dbsc.task.QueryTaskMaster;
import io.jayms.dbsc.ui.CreateConnectionUI;
import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.ui.StandaloneUIModule;
import io.jayms.dbsc.ui.UIModule;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.RightPane;
import io.jayms.dbsc.util.DBHelper;
import io.jayms.xlsx.model.StyleTable;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

public class DBSCGraphicalUserInterface extends Application {

	/**
	 * A flag to indicate to certain debugging behaviours.
	 */
	public static final boolean DEBUG = false;
	
	/**
	 * Entry point (main method)
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Some public constant values; path to font for editor, values for the dividers of SplitPanes.
	 */
	public static final double rightTopPaneHeight = 0.05;
	public static final double leftPaneWidth = 0.3;
	public static final String EDITOR_FONT = "file:resources/fonts/Courier Prime.ttf";
	
	/**
	 * Stage of the main application, essentially represents an abstraction of a window in JavaFX.
	 */
	@Getter private Stage stage;
	
	/**
	 * 2 JavaFX Panes responsible for the centre-alignment of all the child elements in the scene.
	 */
	@Getter private BorderPane genesisPane;
	@Getter private VBox rootPane;
	
	/**
	 * Enables the split between the left and right pane.
	 */
	@Getter private SplitPane masterPane;
	
	/**
	 * 2 UI classes responsible for segregating the components on the left and right side of the divider.
	 */
	@Getter private LeftPane leftPane;
	@Getter private RightPane rightPane;
	
	/**
	 * Responsible for and manages activity regarding the database; an essential dependency throughout the program.
	 */
	@Getter private DatabaseManager databaseManager;
	
	/**
	 * Provides helper methods for retrieving certain data from the database.
	 */
	@Getter private DBHelper dbHelper;
	
	/**
	 * Manages query tasks.
	 */
	@Getter private QueryTaskMaster queryTaskMaster;
	
	/**
	 * Holds some UI modules for cleaner disposal towards upon exit of prorgam.
	 */
	@Getter private final Set<UIModule> uiModules = new HashSet<>();
	@Getter private	CreateConnectionUI createConnectionUI;
	@Getter private QueryBuilderUI queryBuilderUI;
	
	/**
	 * Appendage and removal methods for the UI module collection mentioned above.
	 */
	public void openUIModule(UIModule module) {
		uiModules.add(module);
	}
	
	public void closeUIModule(UIModule module) {
		uiModules.remove(module);
	}
	
	/**
	 * Default double band format and title style.
	 */
	@Getter private static DoubleBandFormatHolder defaultDoubleBandFormat;
	@Getter private static StyleHolder defaultTitleStyle;
	@Getter private static StyleHolder defaultSubTotalStyle;
	
	/**
	 * Static initialization of some global defaults. 
	 */
	static {
		FontHolder defaultFont = new FontHolder("Arial", 12, true, StyleTable.COLORS[0]);
		defaultDoubleBandFormat = new DoubleBandFormatHolder(new StyleHolder(defaultFont, StyleTable.COLORS[7]),
				new StyleHolder(defaultFont, StyleTable.COLORS[8]));
		
		java.awt.Color tc = new java.awt.Color(102, 153, 153, 255);
		java.awt.Color stc = new java.awt.Color(244, 66, 66, 255);
		FontHolder tf = new FontHolder("Arial", 12, true, new java.awt.Color(0, 0, 0, 255));
		defaultTitleStyle = new StyleHolder(tf, tc);
		defaultSubTotalStyle = new StyleHolder(tf, stc);
	}
	
	/**
	 * Initialize the JavaFX application.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		File dbFile = new File("localDBs.sqlite"); // Initialize SQLite DB file.
		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile); // Initialize SQLiteDatabase object, using DB file.
		databaseManager = new DatabaseManager(this, sqliteDb); // Initialize DatabaseManager with the SQLiteDatabase.

		//Try to load connection configs from SQLiteDatabase.
		if (databaseManager.loadConnectionConfigs()) {
			System.out.println("Loaded connection configs.");
		} else {
			System.out.println("Did not load any connection configs.");
		}
		
		//Print out the retrieved connection configs to console; mainly for debugging purposes.
		Collection<ConnectionConfig> connConfigs = databaseManager.getConnectionConfigs();
		int i = 0;
		for (ConnectionConfig cc : connConfigs) {
			System.out.println("Connection Config " + i++ + ": " + cc);
		}
		
		//More initialization.
		dbHelper = new DBHelper(databaseManager);
		queryTaskMaster = new QueryTaskMaster(this);
		
		this.stage = stage; // Save reference to stage.
		stage.setTitle("DBSC"); // Set title of window.
		
		//Initialize some UI pane/layout components, with some hard-coded, default settings.
		genesisPane = new BorderPane();
		
		rootPane = new VBox();
		rootPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		rootPane.setPrefHeight(Double.MAX_VALUE);
		
		masterPane = new SplitPane();
		masterPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		masterPane.setOrientation(Orientation.HORIZONTAL);
		masterPane.prefHeightProperty().bind(rootPane.heightProperty());
		
		initUIModules();
		
		masterPane.setDividerPosition(0, leftPaneWidth);
		masterPane.getItems().addAll(leftPane.getLeftPane(), rightPane.getRightPane());
		
		// Listen for when the width of the window changes.
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			onWidthResize(oldVal, newVal); // When the width of window does change, call this method to resize the divider position.
		});
		
		rootPane.getChildren().addAll(masterPane);
		genesisPane.setCenter(rootPane);
		Scene scene = new Scene(genesisPane, 800, 600); // Create scene with parent pane of width 800, height 600.
		stage.setScene(scene);
		
		stage.setOnHiding((e) -> { // Called when the red 'X' of the window is clicked.
			for (UIModule uiModule : uiModules) { // Ensure all UI modules are properly closed before shutting the application.
				uiModule.close();
			}
			stage.close();
		});
		
		stage.show(); // Show window.
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		databaseManager.storeConnectionConfigs(); // Save all cached connection configs to the local SQLite DB.
		databaseManager.close(); // Properly close connections to databases.
	}
	
	/**
	 * Initialize some UI modules in private method; saves cluttering larger method.
	 */
	private void initUIModules() {
		createConnectionUI = new CreateConnectionUI(this);
		uiModules.add(createConnectionUI);
		
		leftPane = new LeftPane(this);
		rightPane = new RightPane(this);
		
		uiModules.add(leftPane);
		uiModules.add(rightPane);
		
		for (UIModule mod : uiModules) {
			if (!(mod instanceof StandaloneUIModule)) { // Initialize the modules that don't do it on their own (in the show method).
				mod.init();
			}
		}
	}
	
	/**
	 * Ensures the proportions of the UI are kept sensible by updating the divider positions.
	 */
	private void onWidthResize(Number oldVal, Number newVal) {
		masterPane.setDividerPosition(0, leftPaneWidth);
		rightPane.getRightPane().setDividerPosition(0, rightTopPaneHeight);
	}

}