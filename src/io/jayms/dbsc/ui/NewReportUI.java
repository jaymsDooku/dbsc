package io.jayms.dbsc.ui;

import java.util.Iterator;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.colorpicker.DBSCColorPicker;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import io.jayms.dbsc.util.GeneralUtils;
import io.jayms.xlsx.model.StyleTable;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class NewReportUI extends StandaloneUIModule {

	private final DB selectedDB;
	
	private Scene newReportScene;
	private VBox newReportRoot;
	
	private HBox newReportTitleCtr;
	private Label newReportTitle;
	
	private HBox reportNameCtr;
	private Label reportNameLbl;
	private TextField reportNameTxt;
	
	private HBox colour1Ctr;
	private Label colour1Lbl;
	private DBSCColorPicker colour1Pkr;
	
	private HBox colour2Ctr;
	private Label colour2Lbl;
	private DBSCColorPicker colour2Pkr;
	
	private HBox newReportBtnCtr;
	private Button newReportBtn;
	
	public NewReportUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.selectedDB = db;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Report");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newReportRoot = new VBox();
		newReportRoot.setSpacing(10);
		rootCtr.getChildren().add(newReportRoot);
		root.getChildren().add(rootCtr);
		newReportScene = new Scene(root, 300, 200);
		
		newReportTitleCtr = new HBox();
		newReportTitleCtr.setAlignment(Pos.CENTER);
		newReportTitle = new Label("New Report");
		newReportTitle.setFont(Font.font("Arial", 20));
		newReportTitle.setAlignment(Pos.CENTER);
		newReportTitleCtr.getChildren().add(newReportTitle);
		
		reportNameCtr = new HBox();
		reportNameCtr.setAlignment(Pos.CENTER_RIGHT);
		
		reportNameLbl = new Label("Report Name: ");
	
		reportNameTxt = new TextField();
		reportNameTxt.setPromptText("Enter report name");
		
		reportNameCtr.getChildren().addAll(reportNameLbl, reportNameTxt);
		
		java.awt.Color defAwtClr1 = StyleTable.STYLE_TABLE.getStyle(8).getFill().getColor();
		java.awt.Color defAwtClr2 = StyleTable.STYLE_TABLE.getStyle(9).getFill().getColor();
		
		Color defClr1 = GeneralUtils.awtToJavaFXColor(defAwtClr1);
		Color defClr2 = GeneralUtils.awtToJavaFXColor(defAwtClr2);
		
		colour1Ctr = new HBox();
		colour1Ctr.setAlignment(Pos.CENTER_RIGHT);
		colour1Lbl = new Label("Pick colour 1: ");
		colour1Pkr = new DBSCColorPicker();
		colour1Pkr.setValue(defClr1);
		
		colour1Ctr.getChildren().addAll(colour1Lbl, colour1Pkr);
		
		colour2Ctr = new HBox();
		colour2Ctr.setAlignment(Pos.CENTER_RIGHT);
		colour2Lbl = new Label("Pick colour 2: ");
		colour2Pkr = new DBSCColorPicker();
		colour2Pkr.setValue(defClr2);
		
		colour2Ctr.getChildren().addAll(colour2Lbl, colour2Pkr);
		
		newReportBtnCtr = new HBox();
		newReportBtnCtr.setAlignment(Pos.CENTER);
		newReportBtn = new Button("New Report");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onNewReport();
		};
		newReportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		newReportBtnCtr.getChildren().add(newReportBtn);
		
		newReportRoot.getChildren().addAll(newReportTitleCtr,
				reportNameCtr, colour1Ctr, colour2Ctr, newReportBtnCtr);
	
		newReportScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onNewReport();
				}
			}
		});
		uiStage.setScene(newReportScene);
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	private void onNewReport() {
		String reportName = reportNameTxt.getText();
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		
		Report report = new Report(selectedDB, reportName, masterUI.getDefaultDoubleBandFormat());
		
		TreeItem<DBSCTreeItem> dbTreeItem = connTreeView.getDatabaseTreeItem(selectedDB);
		TreeItem<DBSCTreeItem> reportTreeItem = new TreeItem<>(new ReportTreeItem(masterUI, report));
		dbTreeItem.getChildren().add(reportTreeItem);
		
		selectedDB.getReports().add(report);
		
		System.out.println("Creating new report: " + reportName);
		close();
	}
	
	private PopupWindow getPopupWindow() {
		final Iterator<Window> windows = Window.impl_getWindows();
		while (windows.hasNext()) {
			final Window window = windows.next();
			if (window instanceof PopupWindow) {
				return (PopupWindow) window;
			}
		}
		return null;
	}
}
