package io.jayms.dbsc.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.qb.ColumnLabel;
import io.jayms.dbsc.qb.GenerateQueryButton;
import io.jayms.dbsc.qb.Join;
import io.jayms.dbsc.qb.JoinCircle;
import io.jayms.dbsc.qb.JoinContext;
import io.jayms.dbsc.qb.QueryBuilderContext;
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.DraggableNode;
import io.jayms.dbsc.util.DraggablePane;
import io.jayms.dbsc.util.Vec2;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import lombok.Getter;

public class QueryBuilderUI extends StandaloneUIModule {
	
	@Getter private Scene queryBuilderScene;
	
	@Getter private VBox queryBuilderRootPane;
	
	@Getter private HBox queryBuilderActionBar;
	
	@Getter private HBox qbAddTableCtr;
	@Getter private ComboBox<String> qbAddTableCmb;
	@Getter private Button qbAddTableBtn;
	
	@Getter private Pane qbABSpacer;
	
	@Getter private HBox qbQueryGenCtr;
	@Getter private CheckBox qbFormattingCb;
	@Getter private Button qbGenerateQueryBtn;
	
	@Getter private DraggablePane queryBuilderPane;
	@Getter private QueryBuilderContext queryBuilderContext;
	
	@Getter private final DB db;
	
	@Getter private Set<Table> addedTables = new HashSet<>();
	@Getter private Set<JoinCircle> joinCircles = new HashSet<>();
	
	@Getter private Timer timer;
	@Getter private TimerTask joinLineTask;
	@Getter private JoinContext joinContext;
	
	public QueryBuilderUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.db = db;
		this.queryBuilderContext = new QueryBuilderContext();
		this.timer = new Timer();
	}
	
	public void startJoinLine(JoinCircle joinCircle) {
		Vec2 jcPos = Vec2.getRealPosition(joinCircle);
		
		System.out.println("x: " + jcPos.getX());
		System.out.println("y: " + jcPos.getY());
		
		Line joinLine = new Line(jcPos.getX(), jcPos.getY(), jcPos.getX(), jcPos.getY());
		joinLine.setStroke(Color.RED);
		joinLine.setStrokeWidth(5);
		
		joinContext = new JoinContext(this, joinLine, joinCircle);
		
		queryBuilderPane.getChildren().add(joinContext.getJoinLine());
	}
	
	private JoinCircle getJoinCircle(double x, double y) {
		return joinCircles.stream().filter(c -> {
			Bounds bounds = c.localToScene(c.getBoundsInLocal());
			return bounds.contains(x, y);
		}).findFirst().orElse(null);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Builder");
		
		queryBuilderRootPane = new VBox();
		
		queryBuilderActionBar = new HBox();
		
		qbAddTableCtr = new HBox();
		qbAddTableCmb = new ComboBox<>();
		
		qbAddTableBtn = ComponentFactory.createButton("Add Table", (e) -> {
			String tableName = qbAddTableCmb.getSelectionModel().getSelectedItem();
			List<Table> tables = db.getTables();
			Table table = tables.stream().filter(t -> t.getName().equals(tableName)).findFirst().orElse(null);
			
			if (addedTables.contains(table)) {
				return;
			}
			
			addedTables.add(table);
			
			DraggableNode draggable;
			VBox tableCtr = new VBox();
			tableCtr.setAlignment(Pos.CENTER);
			tableCtr.setUserData(table);
			draggable = new DraggableNode(tableCtr, (n) -> {
				if (!(n instanceof JoinCircle)) return;
				JoinCircle joinCircle = (JoinCircle) n;
				if (!joinCircle.isJoined()) return;
				joinCircle.getJoin().updateLine();
			});
			
			HBox tableHeaderCtr = new HBox();
			tableHeaderCtr.setAlignment(Pos.CENTER);
			tableHeaderCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			Label tableLbl = new Label(tableName);
			tableHeaderCtr.getChildren().addAll(tableLbl);
			
			HBox tableColCtr = new HBox();
			tableColCtr.setAlignment(Pos.CENTER);
			tableColCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			List<Column> columns = table.getColumns();
			columns.stream().forEach(c -> {
				HBox colCtr = new HBox();
				colCtr.setAlignment(Pos.CENTER);
				ColumnLabel colLbl = new ColumnLabel(this, table, c);
				
				StackPane colJoinIndicator = new StackPane();
				JoinCircle colJoinCircle = new JoinCircle(this, table, c);
				joinCircles.add(colJoinCircle);
				
				colJoinIndicator.getChildren().add(colJoinCircle);
				colCtr.getChildren().addAll(colLbl, colJoinIndicator);
				tableColCtr.getChildren().addAll(colCtr);
			});
			
			tableCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableCtr.getChildren().addAll(tableHeaderCtr, tableColCtr);
			queryBuilderPane.addDraggable(draggable);
		});
		
		qbAddTableCtr.getChildren().addAll(qbAddTableCmb, qbAddTableBtn);
	
		List<Table> tables = db.getTables();
		for (Table table : tables) {
			qbAddTableCmb.getItems().add(table.getName());
		}
		if (!qbAddTableCmb.getItems().isEmpty()) {
			qbAddTableCmb.getSelectionModel().select(0);
		}
		
		qbABSpacer = new Pane();
		HBox.setHgrow(qbABSpacer, Priority.ALWAYS);
		qbABSpacer.setMinSize(10, 1);
		
		qbQueryGenCtr = new HBox();
		
		qbFormattingCb = new CheckBox("Apply Formatting");
		
		qbGenerateQueryBtn = new GenerateQueryButton(this);
		
		qbQueryGenCtr.getChildren().addAll();
		
		queryBuilderActionBar.getChildren().addAll(qbAddTableCtr, qbABSpacer, qbGenerateQueryBtn);
		queryBuilderActionBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		queryBuilderPane = new DraggablePane();
		queryBuilderPane.setPrefSize(800, 600);
		queryBuilderPane.onMouseClickedProperty().set((qbe) -> {
			if (joinContext == null) return;
			Line joinLine = joinContext.getJoinLine();
			
			if (joinLine == null) return;
			
			boolean removeLine = true;
			
			JoinCircle jc = getJoinCircle(qbe.getSceneX(), qbe.getSceneY());
			if (jc != null) {
				if (removeLine = joinContext.canJoinWith(jc)) {
					Join newJoin = joinContext.joinWith(jc);
					Map<Table, Join> joins = queryBuilderContext.getJoins();
					Table table1 = newJoin.getJoinCircle1().getTable();
					Table table2 = newJoin.getJoinCircle2().getTable();
					if (joins.containsKey(table1) && joins.containsKey(table2)) {
						newJoin.dismantle();
						return;
					} else if (joins.containsKey(table1)) {
						newJoin.swap();
					}
					joins.put(newJoin.getJoinCircle1().getTable(), newJoin);
				}
			}
			
			if (removeLine) {
				queryBuilderPane.getChildren().remove(joinLine);
				joinLine = null;
			}
		});
		queryBuilderPane.setOnMouseMoved((e) -> {
			if (joinContext == null) return;
			Line joinLine = joinContext.getJoinLine();
			
			if (joinLine == null) return;
			joinLine.setEndX(e.getSceneX());
			joinLine.setEndY(e.getSceneY());
		});
		
		queryBuilderRootPane.getChildren().addAll(queryBuilderPane, queryBuilderActionBar);
		
		queryBuilderScene = new Scene(queryBuilderRootPane, 800, 800);
		uiStage.setScene(queryBuilderScene);
	}
}
