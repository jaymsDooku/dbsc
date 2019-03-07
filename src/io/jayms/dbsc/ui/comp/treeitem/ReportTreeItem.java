package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.NewQueryUI;
import io.jayms.dbsc.ui.NewReportUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

public class ReportTreeItem extends DBSCTreeItem {

	private Report report;
	
	public ReportTreeItem(DBSCGraphicalUserInterface masterUI, Report report) {
		super(masterUI, new Label(report.getWorkbookName()), ComponentFactory.createButton("+", e -> {
			new NewQueryUI(masterUI, report).show();
		}));
		this.report = report;
	}
	
	@Override
	public void click() {
	}
	
	@Override
	public ContextMenu getContextMenu() {
		return newReportCM();
	}
	
	private ContextMenu newReportCM() {
		ContextMenu reportCM = new ContextMenu();
		MenuItem newReport = new MenuItem("New Query");
		newReport.setOnAction(e -> {
			new NewQueryUI(masterUI, report).show();
		});
		MenuItem settings = new MenuItem("Settings");
		settings.setOnAction(e -> {
		});
		MenuItem deleteConn = new MenuItem("Delete Report");
		deleteConn.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteReport(report);
		});
		reportCM.getItems().addAll(newReport, settings, deleteConn);
		return reportCM;
	}

}
