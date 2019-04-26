package io.jayms.dbsc.ui;

import java.util.function.Consumer;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.FontHolder;
import io.jayms.dbsc.model.StyleHolder;
import io.jayms.dbsc.ui.comp.NumberField;
import io.jayms.dbsc.ui.comp.colorpicker.DBSCColorPicker;
import io.jayms.dbsc.util.GeneralUtils;
import io.jayms.dbsc.util.Validation;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StyleEditorUI extends StandaloneUIModule {
	
	private String editorTitle;
	private boolean dbscClrPkr;
	private StyleHolder selectedStyle;
	private Consumer<StyleHolder> callback;

	public StyleEditorUI(DBSCGraphicalUserInterface masterUI, String editorTitle, boolean dbscClrPkr, StyleHolder style, Consumer<StyleHolder> callback) {
		super(masterUI);
		this.editorTitle = editorTitle;
		this.selectedStyle = style;
		this.dbscClrPkr = dbscClrPkr;
		this.callback = callback;
	}
	
	private Scene styleEditorScene;
	private VBox styleEditorRoot;
	
	private void styleEditorScene() {
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		styleEditorRoot = new VBox();
		styleEditorRoot.setSpacing(10);
		rootCtr.getChildren().add(styleEditorRoot);
		root.getChildren().add(rootCtr);
		styleEditorScene = new Scene(root, 300, 400);
	}
	
	private VBox styleCtr;
	private HBox styleEditorTitleCtr;
	private Label styleEditorTitle;
	
	private void styleEditorTitle() {
		styleEditorTitleCtr = new HBox();
		styleEditorTitleCtr.setAlignment(Pos.CENTER);
		styleEditorTitle = new Label(editorTitle);
		styleEditorTitle.setFont(Font.font("Arial", 20));
		styleEditorTitle.setAlignment(Pos.CENTER);
		styleEditorTitleCtr.getChildren().add(styleEditorTitle);
	}

	private HBox fontFamilyCtr;
	private Label fontFamilyLbl;
	private ComboBox<String> fontFamilyCmb;
	
	private void fontFamily(FontHolder font) {
		fontFamilyCtr = new HBox();
		fontFamilyCtr.setAlignment(Pos.CENTER_RIGHT);
		fontFamilyLbl = new Label("Font Family: ");
		fontFamilyCmb = new ComboBox<>();
		fontFamilyCmb.getItems().add("Arial");
		fontFamilyCmb.getItems().add("Courier");
		fontFamilyCmb.getItems().add("Times New Roman");
		
		String fontFamily = font != null ? font.getFamilyName() : "Arial";
		
		fontFamilyCmb.getSelectionModel().select(fontFamily);
		fontFamilyCtr.getChildren().addAll(fontFamilyLbl, fontFamilyCmb);
	}
	
	private HBox fontSizeCtr;
	private Label fontSizeLbl;
	private NumberField fontSizeTxt;
	
	private void fontSize(FontHolder font) {
		fontSizeCtr = new HBox();
		fontSizeCtr.setAlignment(Pos.CENTER_RIGHT);
		fontSizeLbl = new Label("Font Size: ");
		fontSizeTxt = new NumberField();
		
		if (font != null) fontSizeTxt.setText(Integer.toString(font.getSize()));
		
		fontSizeTxt.setPromptText("Enter font size");
		fontSizeCtr.getChildren().addAll(fontSizeLbl, fontSizeTxt);
	}
	
	private HBox fontClrCtr;
	private Label fontClrLbl;
	private ColorPicker fontClrPkr;
	
	private void fontClr(FontHolder font) {
		fontClrCtr = new HBox();
		fontClrCtr.setAlignment(Pos.CENTER_RIGHT);
		fontClrLbl = new Label("Font Colour: ");
		fontClrPkr = new ColorPicker();
		
		Color fontClr = font == null ? Color.BLACK :
			GeneralUtils.awtToJavaFXColor(font.getColor());
		
		fontClrPkr.setValue(fontClr);
		fontClrCtr.getChildren().addAll(fontClrLbl, fontClrPkr);
	}
	
	private HBox fillCtr;
	private Label fillLbl;
	private ColorPicker fillPkr;
	
	private void fill() {
		fillCtr = new HBox();
		fillCtr.setAlignment(Pos.CENTER_RIGHT);
		fillLbl = new Label("Fill: ");
		fillPkr = dbscClrPkr ? new DBSCColorPicker() : new ColorPicker();
		
		if (selectedStyle != null) fillPkr.setValue(GeneralUtils.awtToJavaFXColor(selectedStyle.getFillColor()));
		
		fillCtr.getChildren().addAll(fillLbl, fillPkr);
	}
	
	private HBox editStyleBtnCtr;
	private Button editStyleBtn;
	
	private void editStyleBtn() {
		editStyleBtnCtr = new HBox();
		editStyleBtnCtr.setAlignment(Pos.CENTER);
		editStyleBtn = new Button("Edit Style");
		EventHandler<MouseEvent> editStylePress = (MouseEvent e) -> {
			onEditStyle();
		};
		editStyleBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, editStylePress);
		editStyleBtnCtr.getChildren().add(editStyleBtn);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage(editorTitle);
		
		styleEditorScene();
		
		styleEditorTitle();
		
		FontHolder font = selectedStyle.getFont();
		styleCtr = new VBox();
		styleCtr.setSpacing(10);
		fontFamily(font);
		fontSize(font);
		fontClr(font);
		fill();
		styleCtr.getChildren().addAll(this.fontFamilyCtr, this.fontSizeCtr, this.fontClrCtr, this.fillCtr);

		editStyleBtn();
		
		styleEditorRoot.getChildren().addAll(styleEditorTitleCtr,
				styleCtr, editStyleBtnCtr);
	
		styleEditorScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onEditStyle();
				}
			}
		});
		
		uiStage.setScene(styleEditorScene);
	}
	
	private void onEditStyle() {
		String fontName = fontFamilyCmb.getSelectionModel().getSelectedItem();
		int fontSize = fontSizeTxt.getIntValue();
		
		if (fontSize < 0) {
			Validation.alert("Font size must be greater than 0.");
			return;
		}
		
		Color fontClr = fontClrPkr.getValue();
		Color fillClr = fillPkr.getValue();
		
		FontHolder fontHolder = selectedStyle.getFont();
		fontHolder.setFamilyName(fontName);
		fontHolder.setSize(fontSize);
		fontHolder.setColor(GeneralUtils.javafxToAwtColor(fontClr));
		selectedStyle.setFillColor(GeneralUtils.javafxToAwtColor(fillClr));
		
		callback.accept(selectedStyle);
		close();
	}
}
