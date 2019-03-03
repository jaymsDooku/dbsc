package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ReportTreeItem extends DBSCTreeItem {

	public ReportTreeItem(DBSCGraphicalUserInterface masterUI, String reportName) {
		super(masterUI, new Label(reportName), new Button("+"));
	}

}
