package io.jayms.dbsc.ui;

import java.util.Iterator;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DoubleBandFormatHolder;
import io.jayms.dbsc.model.FontHolder;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.model.StyleHolder;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import io.jayms.dbsc.util.Validation;
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
import javafx.scene.text.Font;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import lombok.Setter;

public class ReportUI extends StandaloneUIModule {

	private final DB selectedDB;
	private final Report selectedReport;
	
	@Setter private StyleHolder band1Style;
	@Setter private StyleHolder band2Style;
	@Setter private StyleHolder titleStyle;
	@Setter private StyleHolder subTotalStyle;
	
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
		newReportTitle = new Label(selectedReport == null ? "New Report" : "Edit Report");
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
		
		if (selectedReport != null) reportNameTxt.setText(selectedReport.getWorkbookName());
		
		reportNameTxt.setPromptText("Enter report name");
		reportNameCtr.getChildren().addAll(reportNameLbl, reportNameTxt);
	}
	
	private HBox band1Ctr;
	private Label band1Lbl;
	private Button band1Btn;
	
	private void band1() {
		band1Ctr = new HBox();
		band1Ctr.setAlignment(Pos.CENTER_RIGHT);
		band1Lbl = new Label("Band 1: ");
		band1Btn = new Button("Edit Band 1");
	
		final StyleHolder finalTitleStyle = band1Style;
		
		band1Btn.setOnMouseClicked((e) -> {
			StyleEditorUI seUI = new StyleEditorUI(masterUI, "Edit Band 1", true, finalTitleStyle, (s) -> { 
				this.band1Style = s;
			});
			seUI.init();
			seUI.show();
		});
		band1Ctr.getChildren().addAll(band1Lbl, band1Btn);
	}
	
	private HBox band2Ctr;
	private Label band2Lbl;
	private Button band2Btn;
	
	private void band2() {
		band2Ctr = new HBox();
		band2Ctr.setAlignment(Pos.CENTER_RIGHT);
		band2Lbl = new Label("Band 2: ");
		band2Btn = new Button("Edit Band 2");

		final StyleHolder finalTitleStyle = band2Style;
		
		band2Btn.setOnMouseClicked((e) -> {
			StyleEditorUI seUI = new StyleEditorUI(masterUI, "Edit Band 2", true, finalTitleStyle, (s) -> { 
				this.band2Style = s;
			});
			seUI.init();
			seUI.show();
		});
		band2Ctr.getChildren().addAll(band2Lbl, band2Btn);
	}
	
	private HBox tsStyleCtr;
	private Label tsStyleLbl;
	private Button tsStyleBtn;
	
	private void tsStyle() {
		tsStyleCtr = new HBox();
		tsStyleCtr.setAlignment(Pos.CENTER_RIGHT);
		tsStyleLbl = new Label("Title Style: ");
		tsStyleBtn = new Button("Edit Title Style");
		
		final StyleHolder finalTitleStyle = titleStyle;
		
		tsStyleBtn.setOnMouseClicked((e) -> {
			StyleEditorUI seUI = new StyleEditorUI(masterUI, "Edit Title Style", false, finalTitleStyle, (s) -> { 
				this.titleStyle = s;
			});
			seUI.init();
			seUI.show();
		});
		tsStyleCtr.getChildren().addAll(tsStyleLbl, tsStyleBtn);
	}
	
	private HBox stStyleCtr;
	private Label stStyleLbl;
	private Button stStyleBtn;
	
	private void stStyle() {
		stStyleCtr = new HBox();
		stStyleCtr.setAlignment(Pos.CENTER_RIGHT);
		stStyleLbl = new Label("Sub Total Style: ");
		stStyleBtn = new Button("Edit Sub Total Style");
		
		final StyleHolder finalSubTotalStyle = subTotalStyle;
		
		stStyleBtn.setOnMouseClicked((e) -> {
			StyleEditorUI seUI = new StyleEditorUI(masterUI, "Edit Sub Total Style", false, finalSubTotalStyle, (s) -> { 
				this.subTotalStyle = s;
			});
			seUI.init();
			seUI.show();
		});
		stStyleCtr.getChildren().addAll(stStyleLbl, stStyleBtn);
	}
	
	private HBox newReportBtnCtr;
	private Button newReportBtn;
	
	private void newReportBtn() {
		newReportBtnCtr = new HBox();
		newReportBtnCtr.setAlignment(Pos.CENTER);
		newReportBtn = new Button(selectedReport == null ? "New Report" : "Edit Report");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onNewReport();
		};
		newReportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		newReportBtnCtr.getChildren().add(newReportBtn);
	}
	
	public ReportUI(DBSCGraphicalUserInterface masterUI, DB db, Report report) {
		super(masterUI);
		this.selectedDB = report == null ? db : report.getDb();
		this.selectedReport = report;
		
		FontHolder font = new FontHolder("Arial", 12, false, StyleTable.COLORS[0]);
		this.band1Style = selectedReport != null ? selectedReport.getDoubleBandFormat().getStyle1() : new StyleHolder(font, StyleTable.COLORS[6]);
		this.band2Style = selectedReport != null ? selectedReport.getDoubleBandFormat().getStyle2() : new StyleHolder(font, StyleTable.COLORS[7]);
		this.titleStyle = selectedReport != null ? selectedReport.getTitleStyle() : new StyleHolder(font, StyleTable.COLORS[8]);
		this.subTotalStyle = selectedReport != null ? selectedReport.getSubTotalStyle() : new StyleHolder(font, StyleTable.COLORS[9]);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage(selectedReport == null ? "New Report" : "Edit Report");
		
		newReportScene();
		
		newReportTitle();
	
		reportName();
		
		band1();
		band2();
		
		tsStyle();
		stStyle();

		newReportBtn();
		
		newReportRoot.getChildren().addAll(newReportTitleCtr,
				reportNameCtr, band1Ctr, band2Ctr, tsStyleCtr, stStyleCtr, newReportBtnCtr);
	
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
			Validation.alert("You need to specify a report name!");
			return;
		}
		
		DoubleBandFormatHolder doubleBandFormat = new DoubleBandFormatHolder(band1Style, band2Style);
		
		System.out.println((selectedReport == null ? "Creating new" : "editing") + " report: " + reportName);
		if (selectedReport == null) {
			Report report = new Report(selectedDB, reportName, doubleBandFormat, titleStyle, subTotalStyle);
			
			TreeItem<DBSCTreeItem> dbTreeItem = masterUI.getLeftPane().getConnections().getDatabaseTreeItem(selectedDB);
			TreeItem<DBSCTreeItem> reportTreeItem = new TreeItem<>(new ReportTreeItem(masterUI, report));
			dbTreeItem.getChildren().add(reportTreeItem);
			
			selectedDB.getReports().add(report);
		} else {
			selectedReport.setWorkbookName(reportName);
			selectedReport.setDoubleBandFormat(doubleBandFormat);
			selectedReport.setTitleStyle(titleStyle);
			selectedReport.setSubTotalStyle(subTotalStyle);
		}
		
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
