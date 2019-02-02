package io.jayms.dbsc.ui.comp;

import java.util.Collection;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.CreateConnectionUI;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class LeftPane extends AbstractUIModule {

	@Getter private VBox leftPane;
	private Button newConnectionsBtn;
	@Getter private ConnectionTreeView connections;
	
	public LeftPane(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		leftPane = new VBox();
		
		//Initialize New Connection Button.
		newConnectionsBtn = new Button("New Connection");
		newConnectionsBtn.setMaxWidth(Double.MAX_VALUE);
		
		newConnectionsBtn.setOnMouseClicked((e) -> {
			CreateConnectionUI createConnectionUI = masterUI.getCreateConnectionUI();
			createConnectionUI.show();
		});
		
		connections = new ConnectionTreeView(masterUI);
		connections.init();
		
		DatabaseManager databaseManager = masterUI.getDatabaseManager();
		Collection<ConnectionConfig> conConfigs = databaseManager.getConnectionConfigs();
		for (ConnectionConfig cc : conConfigs) {
			connections.newConnectionTreeItem(cc);
		}
		
		leftPane.getChildren().addAll(newConnectionsBtn, connections.getConnections());
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		
	}
	
}
