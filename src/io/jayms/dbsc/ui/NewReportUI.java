package io.jayms.dbsc.ui;

import java.util.Iterator;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DoubleBandFormatHolder;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.model.StyleHolder;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.NumberField;
import io.jayms.dbsc.ui.comp.colorpicker.DBSCColorPicker;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import io.jayms.dbsc.util.GeneralUtils;
import io.jayms.xlsx.model.StyleTable;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
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
	
	private void newReportScene() {
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newReportRoot = new VBox();
		newReportRoot.setSpacing(10);
		rootCtr.getChildren().add(newReportRoot);
		root.getChildren().add(rootCtr);
		newReportScene = new Scene(root, 300, 380);
	}
	
	private HBox newReportTitleCtr;
	private Label newReportTitle;
	
	private void newReportTitle() {
		newReportTitleCtr = new HBox();
		newReportTitleCtr.setAlignment(Pos.CENTER);
		newReportTitle = new Label("New Report");
		newReportTitle.setFont(Font.font("Arial", 20));
		newReportTitle.setAlignment(Pos.CENTER);
		newReportTitleCtr.getChildren().add(newReportTitle);
	}
	
	private HBox reportNameCtr;
	private Label reportNameLbl;
	private TextField reportNameTxt;
	
	private void reportName() {
		reportNameCtr = new HBox();
		reportNameCtr.setAlignment(Pos.CENTER_RIGHT);
		reportNameLbl = new Label("Report Name: ");
		reportNameTxt = new TextField();
		reportNameTxt.setPromptText("Enter report name");
		reportNameCtr.getChildren().addAll(reportNameLbl, reportNameTxt);
	}
	
	private HBox colour1Ctr;
	private Label colour1Lbl;
	private DBSCColorPicker colour1Pkr;
	
	private void colour1Picker() {
		java.awt.Color defAwtClr1 = StyleTable.COLORS[8];
		
		Color defClr1 = GeneralUtils.awtToJavaFXColor(defAwtClr1);
		
		colour1Ctr = new HBox();
		colour1Ctr.setAlignment(Pos.CENTER_RIGHT);
		colour1Lbl = new Label("Pick colour 1: ");
		colour1Pkr = new DBSCColorPicker();
		colour1Pkr.setValue(defClr1);
		colour1Ctr.getChildren().addAll(colour1Lbl, colour1Pkr);
	}
	
	private HBox colour2Ctr;
	private Label colour2Lbl;
	private DBSCColorPicker colour2Pkr;
	
	private void colour2Picker() {
		java.awt.Color defAwtClr2 = StyleTable.COLORS[9];
		Color defClr2 = GeneralUtils.awtToJavaFXColor(defAwtClr2);
		
		colour2Ctr = new HBox();
		colour2Ctr.setAlignment(Pos.CENTER_RIGHT);
		colour2Lbl = new Label("Pick colour 2: ");
		colour2Pkr = new DBSCColorPicker();
		colour2Pkr.setValue(defClr2);
		colour2Ctr.getChildren().addAll(colour2Lbl, colour2Pkr);
	}
	
	private VBox titleStyleCtr;
	private HBox tsTitleCtr;
	private Label tsTitle;
	
	private void tsTitle() {
		tsTitleCtr = new HBox();
		tsTitleCtr.setAlignment(Pos.CENTER);
		tsTitle = new Label("Title Style");
		tsTitle.setFont(Font.font("Arial", 16));
		tsTitle.setAlignment(Pos.CENTER);
		tsTitleCtr.getChildren().add(tsTitle);
	}
	
	private HBox tsFontFamilyCtr;
	private Label tsFontFamilyLbl;
	private ComboBox<String> tsFontFamilyCmb;
	
	private void tsFontFamily() {
		tsFontFamilyCtr = new HBox();
		tsFontFamilyCtr.setAlignment(Pos.CENTER_RIGHT);
		tsFontFamilyLbl = new Label("Font Family: ");
		tsFontFamilyCmb = new ComboBox<>();
		tsFontFamilyCmb.getItems().add("Arial");
		tsFontFamilyCmb.getItems().add("Courier");
		tsFontFamilyCmb.getItems().add("Times New Roman");
		tsFontFamilyCmb.getSelectionModel().select(0);
		tsFontFamilyCtr.getChildren().addAll(tsFontFamilyLbl, tsFontFamilyCmb);
	}
	
	private HBox tsFontSizeCtr;
	private Label tsFontSizeLbl;
	private NumberField tsFontSizeTxt;
	
	private void tsFontSize() {
		tsFontSizeCtr = new HBox();
		tsFontSizeCtr.setAlignment(Pos.CENTER_RIGHT);
		tsFontSizeLbl = new Label("Font Size: ");
		tsFontSizeTxt = new NumberField();
		tsFontSizeTxt.setPromptText("Enter font size");
		tsFontSizeCtr.getChildren().addAll(tsFontSizeLbl, tsFontSizeTxt);
	}
	
	private HBox tsFontClrCtr;
	private Label tsFontClrLbl;
	private ColorPicker tsFontClrPkr;
	
	private void tsFontClr() {
		tsFontClrCtr = new HBox();
		tsFontClrCtr.setAlignment(Pos.CENTER_RIGHT);
		tsFontClrLbl = new Label("Font Colour: ");
		tsFontClrPkr = new ColorPicker();
		tsFontClrPkr.setValue(Color.BLACK);
		tsFontClrCtr.getChildren().addAll(tsFontClrLbl, tsFontClrPkr);
	}
	
	private HBox tsFillCtr;
	private Label tsFillLbl;
	private ColorPicker tsFillPkr;
	
	private void tsFill() {
		tsFillCtr = new HBox();
		tsFillCtr.setAlignment(Pos.CENTER_RIGHT);
		tsFillLbl = new Label("Fill: ");
		tsFillPkr = new ColorPicker();
		tsFillCtr.getChildren().addAll(tsFillLbl, tsFillPkr);
	}
	
	private HBox newReportBtnCtr;
	private Button newReportBtn;
	
	private void newReportBtn() {
		newReportBtnCtr = new HBox();
		newReportBtnCtr.setAlignment(Pos.CENTER);
		newReportBtn = new Button("New Report");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onNewReport();
		};
		newReportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		newReportBtnCtr.getChildren().add(newReportBtn);
	}
	
	public NewReportUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.selectedDB = db;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Report");
		
		newReportScene();
		
		newReportTitle();
	
		reportName();
		
		colour1Picker();
		colour2Picker();
		
		titleStyleCtr = new VBox();
		titleStyleCtr.setSpacing(10);
		tsTitle();
		tsFontFamily();
		tsFontSize();
		tsFontClr();
		tsFill();
		titleStyleCtr.getChildren().addAll(tsTitleCtr, tsFontFamilyCtr, tsFontSizeCtr, tsFontClrCtr, tsFillCtr);

		newReportBtn();
		
		newReportRoot.getChildren().addAll(newReportTitleCtr,
				reportNameCtr, colour1Ctr, colour2Ctr, titleStyleCtr, newReportBtnCtr);
	
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
		
		if (reportName == null || reportName.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR, "You need to specify a report name!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		
		Color color1 = colour1Pkr.getValue();
		Color color2 = colour2Pkr.getValue();
		
		DoubleBandFormatHolder doubleBandFormat = new DoubleBandFormatHolder(GeneralUtils.javafxToAwtColor(color1), GeneralUtils.javafxToAwtColor(color2));
		
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		
		String fontFamilyName = tsFontFamilyCmb.getSelectionModel().getSelectedItem();
		int fontSize = tsFontSizeTxt.getValue();
		java.awt.Color fontColor = GeneralUtils.javafxToAwtColor(tsFontClrPkr.getValue());
		io.jayms.dbsc.model.FontHolder font = new io.jayms.dbsc.model.FontHolder(fontFamilyName, fontSize, false, fontColor);
		
		Color fillClr = tsFillPkr.getValue();
		java.awt.Color awtFill = GeneralUtils.javafxToAwtColor(fillClr);
		
		StyleHolder titleStyle = new StyleHolder(font, awtFill);
		
		Report report = new Report(selectedDB, reportName, doubleBandFormat, titleStyle);
		
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
