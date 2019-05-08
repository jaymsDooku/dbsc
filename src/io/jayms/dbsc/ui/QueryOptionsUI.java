package io.jayms.dbsc.ui;

import java.util.Arrays;
import java.util.Map;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.ui.comp.NumberField;
import io.jayms.xlsx.db.DatabaseColumn;
import io.jayms.xlsx.model.FieldConfiguration;
import io.jayms.xlsx.model.cells.SubTotalFunction;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
	
	@Getter private Query query;
	
	public QueryOptionsUI(DBSCGraphicalUserInterface masterUI, Query query, DatabaseColumn[] fields) {
		super(masterUI);
		this.query = query;
		this.fields = fields;
	}
	
	private void saveFieldConfig(String selected, FieldConfiguration fieldConfig) {
		fieldConfigs.put(selected, fieldConfig);
		query.setFieldConfigs(fieldConfigs);
	}
	
	private void displayOptionsOfField(String selected) {
		if (!fieldConfigs.containsKey(selected)) return;
		
		FieldConfiguration fieldConfig = fieldConfigs.get(selected);
		
		Label fieldName = new Label(selected);
		
		CheckBox inlineCb = new CheckBox("Inline");
		System.out.println("fieldConfig: " + fieldConfig);
		inlineCb.setSelected(fieldConfig.isInline());
		CheckBox subTotalOnChangeCb = new CheckBox("Sub Total on value change");
		subTotalOnChangeCb.setSelected(fieldConfig.isSubTotalOnChange());
		
		HBox stfContainer = new HBox();
		Label stfLabel = new Label("Sub Total Function: ");
		ComboBox<String> subTotalFunctionCmb = new ComboBox<>();
		subTotalFunctionCmb.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
			SubTotalFunction subTotalFunction = SubTotalFunction.valueOf(n);
			fieldConfig.setSubTotalFunction(subTotalFunction);
			saveFieldConfig(selected, fieldConfig);
		});
		Arrays.stream(SubTotalFunction.values()).forEach(v -> {
			subTotalFunctionCmb.getItems().add(v.toString());
		});
		subTotalFunctionCmb.getSelectionModel().select(fieldConfig.getSubTotalFunction().toString());
		stfContainer.getChildren().addAll(stfLabel, subTotalFunctionCmb);
		
		CheckBox swapBandOnChangeCb = new CheckBox("Swap Colour Band on value change");
		swapBandOnChangeCb.setSelected(fieldConfig.isSwapBandOnChange());
		inlineCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setInline(n);
			saveFieldConfig(selected, fieldConfig);
		});
		subTotalOnChangeCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setSubTotalOnChange(n);
			saveFieldConfig(selected, fieldConfig);
		});
		swapBandOnChangeCb.selectedProperty().addListener((ov, o, n) -> {
			fieldConfig.setSwapBandOnChange(n);
			saveFieldConfig(selected, fieldConfig);
		});
		
		HBox colWidthCtr = new HBox();
		Label colWidthLbl = new Label("Column Width: ");
		NumberField colWidthTxt = new NumberField();
		colWidthTxt.setValue(fieldConfig.getColumnWidth());
		colWidthTxt.onKeyReleasedProperty().set((e) -> { // everytime we press a key
			float columnWidth = Float.parseFloat(colWidthTxt.getText()); // parse it as a float
			fieldConfig.setColumnWidth(columnWidth); // update column width
			saveFieldConfig(selected, fieldConfig); // update 
			System.out.println("hello? " + columnWidth);
		});
		colWidthCtr.getChildren().addAll(colWidthLbl, colWidthTxt);
		queryOptionsDisplay.getChildren().clear();
		queryOptionsDisplay.getChildren().addAll(fieldName, inlineCb, subTotalOnChangeCb, stfContainer, swapBandOnChangeCb, colWidthCtr);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Options");
		
		queryOptionsContainer = new SplitPane();
		queryOptionsContainer.setDividerPosition(0, 0.3);
		queryOptionsContainer.setOrientation(Orientation.HORIZONTAL);
		
		fieldConfigs = FieldConfiguration.getDefaultFieldConfigs(fields, query.getFieldConfigs());
		
		fieldsView = new ListView<>();
		
		// iterate over all the fields, and them to the list view.
		Arrays.stream(fields).forEach(dc -> {
			if (!fieldsView.getItems().contains(dc.getName())) {
				fieldsView.getItems().add(dc.getName());
			}
		});
		
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
		
		queryOptionsRootPane = new BorderPane();
		queryOptionsRootPane.setCenter(queryOptionsContainer);
		queryOptionsScene = new Scene(queryOptionsRootPane, 600, 400);
		uiStage.setScene(queryOptionsScene);
	}

	
}
