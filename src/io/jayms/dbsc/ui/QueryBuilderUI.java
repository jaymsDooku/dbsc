package io.jayms.dbsc.ui;

import java.util.List;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class QueryBuilderUI extends StandaloneUIModule {

	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private Pane queryBuilderPane;
	
	private final DB db;
	
	public QueryBuilderUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.db = db;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Builder");
		System.out.println("set title");
		
		queryBuilderRootPane = new VBox();
		
		queryBuilderActionBar = new HBox();
		
		qbAddTableCtr = new HBox();
		qbAddTableCmb = new ComboBox<>();
		
		qbAddTableBtn = ComponentFactory.createButton("Add Table", (e) -> {
			String tableName = qbAddTableCmb.getSelectionModel().getSelectedItem();
			List<Table> tables = db.getTables();
			Table table = tables.stream().filter(t -> t.getName().equals(tableName)).findFirst().orElse(null);
			
			VBox tableCtr = new VBox();
			tableCtr.setAlignment(Pos.CENTER);
			tableCtr.setUserData(table);
			enableDrag(tableCtr);
			
			HBox tableHeaderCtr = new HBox();
			tableHeaderCtr.setAlignment(Pos.CENTER);
			tableHeaderCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			Label tableLbl = new Label(tableName);
			tableLbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableHeaderCtr.getChildren().addAll(tableLbl);
			
			HBox tableColCtr = new HBox();
			tableColCtr.setAlignment(Pos.CENTER);
			tableColCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			List<Column> columns = table.getColumns();
			columns.stream().forEach(c -> {
				Label colLbl = new Label(c.getName());
				colLbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
				tableColCtr.getChildren().add(colLbl);
			});
			
			tableCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableCtr.getChildren().addAll(tableHeaderCtr, tableColCtr);
			queryBuilderPane.getChildren().add(tableCtr);
		});
		
		qbAddTableCtr.getChildren().addAll(qbAddTableCmb, qbAddTableBtn);
		System.out.println("set controls");
	
		List<Table> tables = db.getTables();
		for (Table table : tables) {
			qbAddTableCmb.getItems().add(table.getName());
		}
		if (!qbAddTableCmb.getItems().isEmpty()) {
			qbAddTableCmb.getSelectionModel().select(0);
		}
		System.out.println("populated combo with tables");
		
		queryBuilderActionBar.getChildren().add(qbAddTableCtr);
		queryBuilderActionBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		queryBuilderPane = new Pane();
		queryBuilderPane.setPrefSize(800, 600);
		
		queryBuilderRootPane.getChildren().addAll(queryBuilderPane, queryBuilderActionBar);
		
		queryBuilderScene = new Scene(queryBuilderRootPane, 800, 800);
		uiStage.setScene(queryBuilderScene);
	}

	static class Delta { double x, y; }
	// make a node movable by dragging it around with the mouse.
	private void enableDrag(final Node circle) {
	final Delta dragDelta = new Delta();
	circle.setOnMousePressed(new EventHandler<MouseEvent>() {
	  @Override public void handle(MouseEvent mouseEvent) {
	    // record a delta distance for the drag and drop operation.
	    dragDelta.x = circle.getTranslateX() - mouseEvent.getX();
	    dragDelta.y = circle.getTranslateY() - mouseEvent.getY();
	    circle.getScene().setCursor(Cursor.MOVE);
	  }
	});
	circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
	  @Override public void handle(MouseEvent mouseEvent) {
	    circle.getScene().setCursor(Cursor.HAND);
	  }
	});
	circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
	  @Override public void handle(MouseEvent mouseEvent) {
	    circle.setTranslateX(mouseEvent.getX() + dragDelta.x);
	    circle.setTranslateY(mouseEvent.getY() + dragDelta.y);
	  }
	});
	circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
	  @Override public void handle(MouseEvent mouseEvent) {
	    if (!mouseEvent.isPrimaryButtonDown()) {
	      circle.getScene().setCursor(Cursor.HAND);
	    }
	  }
	});
	circle.setOnMouseExited(new EventHandler<MouseEvent>() {
	  @Override public void handle(MouseEvent mouseEvent) {
	    if (!mouseEvent.isPrimaryButtonDown()) {
	      circle.getScene().setCursor(Cursor.DEFAULT);
	    }
	  }
	});
	}
}
