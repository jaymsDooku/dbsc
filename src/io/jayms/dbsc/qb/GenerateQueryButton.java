package io.jayms.dbsc.qb;

import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.ui.comp.QueryTextEditor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

public class GenerateQueryButton extends Button {

	public GenerateQueryButton(QueryBuilderUI qbUI) {
		super("Generate Query");		
		
		this.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		this.onMouseClickedProperty().set((e) -> {
			boolean applyFormatting = qbUI.getQbFormattingCb().isSelected();
			
			String query = qbUI.getQueryBuilderContext().generateQuery(applyFormatting);
			
			Tab selectedTab = qbUI.getMasterUI().getRightPane().getQueriesTab().getSelectionModel().getSelectedItem();
			Node tabContent = selectedTab.getContent();
			if (!(tabContent instanceof QueryTextEditor)) {
				return;
			}
			
			QueryTextEditor tabText = (QueryTextEditor) tabContent;
			tabText.appendText(query);
			qbUI.close();
		});
	}
	
}
