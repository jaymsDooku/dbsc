package io.jayms.dbsc.ui.comp;

import java.util.Collection;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.CreateConnectionUI;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class LeftPane extends AbstractUIModule {

	@Getter private VBox leftPane;
	@Getter private ConnectionTreeView connections;
	
	public LeftPane(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		leftPane = new VBox();
		
		connections = new ConnectionTreeView(masterUI);
		connections.init();
		
		DatabaseManager databaseManager = masterUI.getDatabaseManager();
		Collection<ConnectionConfig> conConfigs = databaseManager.getConnectionConfigs();
		for (ConnectionConfig cc : conConfigs) {
			connections.newConnectionTreeItem(cc);
		}
		
		leftPane.getChildren().addAll(connections.getConnections());
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		
	}
	
}
