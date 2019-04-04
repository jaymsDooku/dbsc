package io.jayms.dbsc.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.ui.comp.NumberField;
import io.jayms.xlsx.db.DatabaseColumn;
import io.jayms.xlsx.model.FieldConfiguration;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

public class QueryOptionsUI extends StandaloneUIModule {

	@Getter private Scene queryOptionsScene;
	
	@Getter private BorderPane queryOptionsRootPane;
	
	@Getter private SplitPane queryOptionsContainer;
	
	@Getter private ListView<String> fieldsView;
	@Getter private VBox queryOptionsDisplay;
	
	@Getter private DatabaseColumn[] fields;
	@Getter private Map<String, FieldConfiguration> fieldConfigs;
	
	@Getter private HBox queryOptionsButtonBar;
	@Getter private Button applyOptionsBtn;
	
	@Getter private Query query;
	
	public QueryOptionsUI(DBSCGraphicalUserInterface masterUI, Query query, DatabaseColumn[] fields) {
		super(masterUI);
		this.query = query;
		this.fields = fields;
	}
	
	private Multimap<String, String> getTableToFields() {
		Multimap<String, String> result = HashMultimap.create();
		
		Arrays.stream(fields).forEach(dc -> {
			result.put(dc.getTableName(), dc.getName());
		});
		
		return result;
	}
	
	private void displayOptionsOfField(String selected) {
		if (!fieldConfigs.containsKey(selected)) return;
		
		FieldConfiguration fieldConfig = fieldConfigs.get(selected);
		
		Label fieldName = new Label(selected);
		
		CheckBox inlineCb = new CheckBox("Inline");
		inlineCb.setSelected(fieldConfig.isInline());
		CheckBox subTotalOnChangeCb = new CheckBox("Sub Total on value change");
		subTotalOnChangeCb.setSelected(fieldConfig.isSubTotalOnChange());
		CheckBox swapBandOnChangeCb = new CheckBox("Swap Colour Band on value change");
		swapBandOnChangeCb.setSelected(fieldConfig.isSwapBandOnChange());
		inlineCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setInline(n);
		});
		subTotalOnChangeCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setSubTotalOnChange(n);
		});
		swapBandOnChangeCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setSwapBandOnChange(n);
		});
		
		HBox colWidthCtr = new HBox();
		Label colWidthLbl = new Label("Column Width: ");
		NumberField colWidthTxt = new NumberField();
		colWidthTxt.onKeyReleasedProperty().set((e) -> {
			float columnWidth = Float.parseFloat(colWidthTxt.getText());
			fieldConfig.setColumnWidth(columnWidth);
		});
		colWidthCtr.getChildren().addAll(colWidthLbl, colWidthTxt);
		queryOptionsDisplay.getChildren().clear();
		queryOptionsDisplay.getChildren().addAll(fieldName, inlineCb, subTotalOnChangeCb, swapBandOnChangeCb);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Options");
		
		queryOptionsContainer = new SplitPane();
		queryOptionsContainer.setDividerPosition(0, 0.3);
		queryOptionsContainer.setOrientation(Orientation.HORIZONTAL);
		
		fieldConfigs = new HashMap<>();
		Arrays.stream(fields).forEach(f -> {
			fieldConfigs.put(f.getName(), FieldConfiguration.getDefaultFieldConfig());
		});
		
		fieldsView = new ListView<>();
		
		Multimap<String, String> tableToFields = getTableToFields();
		for (String table : tableToFields.keySet()) {
			boolean empty = (table == null || table.isEmpty());
			
			fieldsView.getItems().add("|" + (empty ? "No Table Name" : table) + "|");
			Collection<String> fields = tableToFields.get(table);
			fields.stream().forEach(f -> {
				fieldsView.getItems().add(f);
			});
		}
		
		queryOptionsDisplay = new VBox(10);
		queryOptionsDisplay.setPadding(new Insets(10));
		queryOptionsDisplay.setAlignment(Pos.TOP_LEFT);
		
		fieldsView.onMouseClickedProperty().set((e) -> {
			String selected = fieldsView.getSelectionModel().getSelectedItem();
			displayOptionsOfField(selected);
		});
		fieldsView.getSelectionModel().select(1);
		displayOptionsOfField(fieldsView.getSelectionModel().getSelectedItem());
		
		queryOptionsContainer.getItems().addAll(fieldsView, queryOptionsDisplay);
		
		queryOptionsButtonBar = new HBox();
		applyOptionsBtn = new Button("Apply Options");
		applyOptionsBtn.onMouseClickedProperty().set((e) -> {
			
			close();
		});
		
		queryOptionsRootPane = new BorderPane();
		queryOptionsRootPane.setCenter(queryOptionsContainer);
		queryOptionsRootPane.setBottom(queryOptionsButtonBar);
		queryOptionsScene = new Scene(queryOptionsRootPane, 600, 400);
		uiStage.setScene(queryOptionsScene);
	}

	
}