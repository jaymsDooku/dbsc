package io.jayms.dbsc.ui.comp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

public class QueryTextEditor extends CodeArea {

	private static final String[] KEYWORDS = new String[] {
		"select", "from", "join"
	};
	
	private static final String KEYWORD_PATTERN = "\\b(?i)(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
	
	public QueryTextEditor() {
		super();
		
		this.setParagraphGraphicFactory(LineNumberFactory.get(this));
		this.multiPlainChanges()
			.successionEnds(Duration.ofMillis(50))
			.subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(this.getText())));
	}
	
	public void initCSS(Scene scene) {
		File file = new File("resources/css/keywords.css");
		System.out.println("css file: " + file.getAbsolutePath());
		System.out.println("css file exists: " + file.exists());
		
		ObservableList<String> stylesheets = scene.getStylesheets();
		try {
			stylesheets.add(file.toURL().toExternalForm());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println("stylesheets: " + stylesheets);
	}
	
	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
            		matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
	}
	
}
