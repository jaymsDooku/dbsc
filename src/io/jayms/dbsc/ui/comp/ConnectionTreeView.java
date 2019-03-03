package io.jayms.dbsc.ui.comp;

import java.util.HashMap;
import java.util.Map;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.RegisterDatabaseUI;
import io.jayms.dbsc.ui.RegisterReportUI;
import io.jayms.dbsc.ui.comp.treeitem.ConnectionTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.QueryTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.RootTreeItem;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;

public class ConnectionTreeView extends AbstractUIModule {
	
	@Getter private TreeView<DBSCTreeItem> connections;
	@Getter private TreeItem<DBSCTreeItem> connectionsRoot;
	
	private Map<String, Query> queries = new HashMap<>();
	private Map<String, Long> doubleClick = new HashMap<>();
	
	private Map<DBSCTreeItem, ConnectionConfig> connectionTreeItems = new HashMap<>();
	private Map<DBSCTreeItem, DB> dbTreeItems = new HashMap<>();
	private Map<DBSCTreeItem, Report> reportItems = new HashMap<>();
	
	private ContextMenu newConnectionCM(ConnectionConfig connConfig) {
		ContextMenu connectionCM = new ContextMenu();
		MenuItem newDB = new MenuItem("New DB");
		newDB.setOnAction(e -> {
			new RegisterDatabaseUI(masterUI, connConfig).show();
		});
		MenuItem deleteConn = new MenuItem("Delete Connection");
		deleteConn.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteConnectionConfig(connConfig);
		});
		connectionCM.getItems().addAll(newDB, deleteConn);
		return connectionCM;
	}
	
	private ContextMenu newDatabaseCM(DB db) {
		ContextMenu databaseCM = new ContextMenu();
		MenuItem newDB = new MenuItem("New Report");
		newDB.setOnAction(e -> {
			new RegisterReportUI(masterUI, db).show();
		});
		MenuItem deleteConn = new MenuItem("Delete Database");
		deleteConn.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteDB(db);
		});
		databaseCM.getItems().addAll(newDB, deleteConn);
		return databaseCM;
	}
	
	private void newReportCM(Report report) {
		
	}

	public ConnectionTreeView(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		connectionsRoot = new TreeItem<>(new RootTreeItem(masterUI));
		connections = new TreeView<>(connectionsRoot);
		connections.setMaxHeight(Double.MAX_VALUE);
		connections.prefHeightProperty().bind(masterUI.getRootPane().heightProperty());
		EventHandler<MouseEvent> clickedQueryItem = (MouseEvent e) -> {
			clickedTreeItem(e);
		};
		connections.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedQueryItem);
	}
	
	public TreeItem<DBSCTreeItem> newDBTreeItem(TreeItem<DBSCTreeItem> connItem, DB db) {
		TreeItem<DBSCTreeItem> dbItem = new TreeItem<>(new DBTreeItem(masterUI, db.getDatabaseName()));
		for (Report report : db.getReports()) {
			TreeItem<DBSCTreeItem> reportItem = new TreeItem<>(new ReportTreeItem(masterUI, report.getWorkbookName()));
			for (Query query : report.getQueries()) {
				String wsName = query.getWorksheetName();
				TreeItem<DBSCTreeItem> queryItem = new TreeItem<>(new QueryTreeItem(masterUI, query.getWorksheetName()));
				queries.put(wsName, query);
				reportItem.getChildren().add(queryItem);
			}
			dbItem.getChildren().add(reportItem);
		}
		return dbItem;
	}
	
	public TreeItem<DBSCTreeItem> newConnectionTreeItem(ConnectionConfig connConfig) {
		ConnectionTreeItem connTreeItem = new ConnectionTreeItem(masterUI, connConfig);
		TreeItem<DBSCTreeItem> connItem = new TreeItem<>(connTreeItem);
		for (DB db : connConfig.getDbs()) {
			TreeItem<DBSCTreeItem> dbItem = newDBTreeItem(connItem, db);
			connItem.getChildren().add(dbItem);
		}
		connectionsRoot.getChildren().add(connItem);
		connectionTreeItems.put(connTreeItem, connConfig);
		return connItem;
	}
	
	private void clickedTreeItem(MouseEvent e) {
		Node node = e.getPickResult().getIntersectedNode();
	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
		if (node == null) return;
		if (!(node instanceof DBSCTreeItem) && !(node instanceof Text)) return;
    	
		DBSCTreeItem treeItem;
        String name;
        
        if (node instanceof DBSCTreeItem) {
        	treeItem = (DBSCTreeItem) node;
        	name = treeItem.getTxt().getText();
        } else {
        	Text text = (Text) node;
        	Node parentOfLabel = text.getParent().getParent().getParent();
        	treeItem = (DBSCTreeItem) parentOfLabel;
        	name = text.getText();
        }
        
        if (e.getButton() == MouseButton.SECONDARY) {
        	if (!connectionTreeItems.containsKey(treeItem)) return;
        	
    		ConnectionConfig cc = connectionTreeItems.get(treeItem);
    		if (cc == null) return;
    		
    		if (connectionCM == null) {
    			newConnectionCM(cc);
    		}
    		connectionCM.show(node, Side.RIGHT, 0, 0);
        	return;
        }
        
        if (queries.containsKey(name))  {
	        if (doubleClick.containsKey(name)) {
	        	System.out.println("Double clicking...");
	        	long lastClick = doubleClick.get(name);
	        	long timePassed = System.currentTimeMillis() - lastClick;
	        	System.out.println("Time passed: " + timePassed);
	        	if (timePassed < 500) {
	        		Query query = queries.get(name);
	    	        if (query != null) {
	    	        	if (!isQueryTabOpen(name)) {
	    	        		masterUI.getRightPane().getQueriesTab().getTabs().add(queryTab(name, query));
	    	        	}
	    	        }
	    	        doubleClick.remove(name);
	        	} else {
	        		doubleClick.remove(name);
	        		doubleClick.put(name, System.currentTimeMillis());
	        	}
	        } else {
	        	System.out.println("Single click");
	        	doubleClick.put(name, System.currentTimeMillis());
	        	return;
	        }
        }
	}
	
	private Tab queryTab(String wsName, Query query) {
		Tab queryTab = new Tab();
		queryTab.setUserData(query);
		queryTab.setText(wsName);
		
		TextField queryTextBox = new TextField();
		queryTextBox.setMaxWidth(Double.MAX_VALUE);
		queryTextBox.setMaxHeight(Double.MAX_VALUE);
		queryTextBox.setAlignment(Pos.TOP_LEFT);
		queryTextBox.setText(query.getQuery());
		queryTextBox.setFont(Font.loadFont(DBSCGraphicalUserInterface.EDITOR_FONT, 14));
		queryTextBox.selectPositionCaret(0);
		queryTab.setContent(queryTextBox);
		return queryTab;
	}
	
	private boolean isQueryTabOpen(String wsName) {
		for (Tab tab : masterUI.getRightPane().getQueriesTab().getTabs()) {
			if (tab.getText().equalsIgnoreCase(wsName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void show() {
		
	}

	@Override
	public void close() {
		
	}

	public TreeItem<DBSCTreeItem> getConnectionTreeItem(ConnectionConfig cc) {
		return getTreeItem(connectionsRoot.getChildren(), cc.getHost());
	}
	
	public TreeItem<DBSCTreeItem> getDatabaseTreeItem(DB db) {
		TreeItem<DBSCTreeItem> ccTreeItem = getConnectionTreeItem(db.getConnConfig());
		
		if (ccTreeItem == null) return null;
		
		return getTreeItem(ccTreeItem.getChildren(), db.getDatabaseName());
	}
	
	private TreeItem<DBSCTreeItem> getTreeItem(ObservableList<TreeItem<DBSCTreeItem>> list, String val) {
		for (TreeItem<DBSCTreeItem> item : list) {
			if (item.getValue().equals(val)) {
				return item;
			}
		}
		return null;
	}
}
