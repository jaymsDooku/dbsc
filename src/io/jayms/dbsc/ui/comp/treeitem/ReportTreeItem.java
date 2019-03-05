package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.NewQueryUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.Label;
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
	public void click(MouseButton mouseButton) {
		
	}

}
