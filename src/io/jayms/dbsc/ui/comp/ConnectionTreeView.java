package io.jayms.dbsc.ui.comp;

import java.util.HashMap;
import java.util.Map;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.AbstractUIModule;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;

public class ConnectionTreeView extends AbstractUIModule {
	
	@Getter private ConnectionConfig selectedConnectionConfig;
	@Getter private TreeView<String> connections;
	@Getter private TreeItem<String> connectionsRoot;
	
	private Map<String, Query> queries = new HashMap<>();
	private Map<String, Long> doubleClick = new HashMap<>();
	private ContextMenu connectionCM;
	private MenuItem newDB;
	
	private Map<TreeItem<String>, ConnectionConfig> connectionTreeItems = new HashMap<>();
	private Map<TreeItem<String>, DB> dbTreeItems = new HashMap<>();
	
	private void newConnectionCM() {
		connectionCM = new ContextMenu();
		newDB = new MenuItem("New DB");
		System.out.println("gang");
		newDB.setOnAction(e -> {
			System.out.println("hello");
			newDB(); 
		});
		connectionCM.getItems().addAll(newDB);
	}
	
	private void newDB() {
		masterUI.getRegisterDatabaseUI().show();
		System.out.println("showing db");
	}

	public ConnectionTreeView(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		connectionsRoot = new TreeItem<>("Connections");
		connections = new TreeView<>(connectionsRoot);
		connections.setMaxHeight(Double.MAX_VALUE);
		connections.prefHeightProperty().bind(masterUI.getRootPane().heightProperty());
		EventHandler<MouseEvent> clickedQueryItem = (MouseEvent e) -> {
			clickedTreeItem(e);
		};
		connections.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedQueryItem);
	}
	
	public TreeItem<String> newDBTreeItem(TreeItem<String> connItem, DB db) {
		TreeItem<String> dbItem = new TreeItem<>(db.getDatabaseName());
		for (Report report : db.getReports()) {
			TreeItem<String> reportItem = new TreeItem<>(report.getWorkbookName());
			for (Query query : report.getQueries()) {
				String wsName = query.getWorksheetName();
				TreeItem<String> queryItem = new TreeItem<>(query.getWorksheetName());
				queries.put(wsName, query);
				reportItem.getChildren().add(queryItem);
			}
			dbItem.getChildren().add(reportItem);
		}
		return dbItem;
	}
	
	public TreeItem<String> newConnectionTreeItem(ConnectionConfig cc) {
		TreeItem<String> connItem = new TreeItem<>(cc.getHost());
		for (DB db : cc.getDbs()) {
			TreeItem<String> dbItem = newDBTreeItem(connItem, db);
			connItem.getChildren().add(dbItem);
		}
		connectionsRoot.getChildren().add(connItem);
		connectionTreeItems.put(connItem, cc);
		return connItem;
	}
	
	private void clickedTreeItem(MouseEvent e) {
		Node node = e.getPickResult().getIntersectedNode();
	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
		if (node == null) return;
		if (!(node instanceof TreeCell) && !(node instanceof Text)) return;
		
		TreeCell<String> treeCell = (TreeCell<String>) node;
    	TreeItem<String> treeItem = treeCell.getTreeItem();
    	System.out.println("casted");
    	
    	if (treeItem == null) return;
    	
        String name = (String) treeItem.getValue();
        System.out.println("Node click: " + name);
        if (e.getButton() == MouseButton.SECONDARY) {
        	System.out.println("Right click");
        	if (!connectionTreeItems.containsKey(treeItem)) return;
        	
        	System.out.println("next");
    		ConnectionConfig cc = connectionTreeItems.get(treeItem);
    		if (cc == null) return;
    		
    		System.out.println("CC exists");
    		if (connectionCM == null) {
    			newConnectionCM();
    		}
    		connectionCM.show(node, Side.RIGHT, 0, 0);
    		selectedConnectionConfig = cc;
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

	public TreeItem<String> getConnectionTreeItem(ConnectionConfig cc) {
		return getTreeItem(connectionsRoot.getChildren(), cc.getHost());
	}
	
	public TreeItem<String> getDatabaseTreeItem(DB db) {
		TreeItem<String> ccTreeItem = getConnectionTreeItem(db.getConnConfig());
		
		if (ccTreeItem == null) return null;
		
		return getTreeItem(ccTreeItem.getChildren(), db.getDatabaseName());
	}
	
	private TreeItem<String> getTreeItem(ObservableList<TreeItem<String>> list, String val) {
		for (TreeItem<String> item : list) {
			if (item.getValue().equals(val)) {
				return item;
			}
		}
		return null;
	}
}
