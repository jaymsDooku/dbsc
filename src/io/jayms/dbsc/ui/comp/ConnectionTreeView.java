package io.jayms.dbsc.ui.comp;

import java.util.HashMap;
import java.util.Map;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.comp.treeitem.ConnectionTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeCell;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.QueryTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.RootTreeItem;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import lombok.Getter;

public class ConnectionTreeView extends AbstractUIModule {
	
	@Getter private TreeView<DBSCTreeItem> connections;
	@Getter private TreeItem<DBSCTreeItem> connectionsRoot;
	
	@Getter private Map<String, Query> queries = new HashMap<>();
	
	@Getter private Map<DBSCTreeItem, TreeItem<DBSCTreeItem>> treeItems = new HashMap<>();

	public ConnectionTreeView(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		RootTreeItem rootTreeItem = new RootTreeItem(masterUI);
		connectionsRoot = new TreeItem<>(rootTreeItem);
		connections = new TreeView<>(connectionsRoot);
		connections.setCellFactory(new Callback<TreeView<DBSCTreeItem>, TreeCell<DBSCTreeItem>>() {
			
			@Override
			public TreeCell<DBSCTreeItem> call(TreeView<DBSCTreeItem> param) {
				return new DBSCTreeCell();
			}
			
		});
		connections.setMaxHeight(Double.MAX_VALUE);
		connections.prefHeightProperty().bind(masterUI.getRootPane().heightProperty());
		EventHandler<MouseEvent> clickedQueryItem = (MouseEvent e) -> {
			clickedTreeItem(e);
		};
		connections.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedQueryItem);
	}
	
	public void removeTreeItem(DBSCTreeItem item) {
		TreeItem<DBSCTreeItem> treeItem = this.getTreeItems().get(item);
		treeItem.getParent().getChildren().remove(treeItem);
	}
	
	public TreeItem<DBSCTreeItem> newDBTreeItem(TreeItem<DBSCTreeItem> connItem, DB db) {
		TreeItem<DBSCTreeItem> dbItem = new TreeItem<>(new DBTreeItem(masterUI, db));
		for (Report report : db.getReports()) {
			TreeItem<DBSCTreeItem> reportItem = new TreeItem<>(new ReportTreeItem(masterUI, report));
			for (Query query : report.getQueries()) {
				String wsName = query.getWorksheetName();
				TreeItem<DBSCTreeItem> queryItem = new TreeItem<>(new QueryTreeItem(masterUI, query));
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
			/*dbItem.addEventHandler(TreeItem.branchExpandedEvent(), (e) -> {
				
				e.consume();
			});
			dbItem.addEventHandler(TreeItem.graphicChangedEvent(), (e) -> {
				e.consume();
			});*/
			
			connItem.getChildren().add(dbItem);
		}
		connectionsRoot.getChildren().add(connItem);
		treeItems.put(connTreeItem, connItem);
		return connItem;
	}
	
	private static Class<?> TREE_VIEW_SKIN_CLAZZ = null;
	
	static {
		try {
			TREE_VIEW_SKIN_CLAZZ = Class.forName("com.sun.javafx.scene.control.skin.TreeViewSkin$1");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void clickedTreeItem(MouseEvent e) {
		Node node = e.getPickResult().getIntersectedNode();
	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
		if (node == null) return;
		
		DBSCTreeItem treeItem = null;
		
		if (node instanceof DBSCTreeItem) {
			treeItem = (DBSCTreeItem) node;
		} else if (node instanceof Text) {
			Text text = (Text) node;
        	Node parentOfLabel = text.getParent().getParent();
        	treeItem = (DBSCTreeItem) parentOfLabel;
		} else if (node instanceof Parent) {
			Node parentOfTreeItemNode = node;
			Class<?> nodeClazz = node.getClass();
			if (!TREE_VIEW_SKIN_CLAZZ.isAssignableFrom(nodeClazz)) {
				Node parent = node.getParent().getParent();
				Class<?> parentClazz = parent.getClass();
				if (TREE_VIEW_SKIN_CLAZZ.isAssignableFrom(parentClazz)) {
					parentOfTreeItemNode = parent;
				}
			}
			Parent parentOfTreeItem = (Parent) parentOfTreeItemNode;
			treeItem = parentOfTreeItem.getChildrenUnmodifiable().stream()
					.filter(c -> c instanceof DBSCTreeItem)
					.map(c -> ((DBSCTreeItem) c)).findFirst().orElse(null);
		}
		
		System.out.println("treeItem: " + treeItem);
		
		if (treeItem == null) return;
        
        if (e.getButton() == MouseButton.SECONDARY) {
        	treeItem.getContextMenu().show(node, Side.RIGHT, 0, 0);
        	return;
        }
        
        treeItem.click();
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
		System.out.println("cc: " + db.getConnConfig());
		TreeItem<DBSCTreeItem> ccTreeItem = getConnectionTreeItem(db.getConnConfig());
		
		if (ccTreeItem == null) return null;
		
		return getTreeItem(ccTreeItem.getChildren(), db.getDatabaseName());
	}
	
	public TreeItem<DBSCTreeItem> getReportTreeItem(Report report) {
		TreeItem<DBSCTreeItem> dbTreeItem = getDatabaseTreeItem(report.getDb());
		
		if (dbTreeItem == null) return null;
		
		return getTreeItem(dbTreeItem.getChildren(), report.getWorkbookName());
	}
	
	private TreeItem<DBSCTreeItem> getTreeItem(ObservableList<TreeItem<DBSCTreeItem>> list, String val) {
		for (TreeItem<DBSCTreeItem> item : list) {
			if (item.getValue().getTxt().getText().equals(val)) {
				return item;
			}
		}
		return null;
	}
}
