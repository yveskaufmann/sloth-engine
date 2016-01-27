package sandbox.controls;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import shader.source.ShaderSource;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLSLEditor extends CodeArea {

	private static final Pattern GLSL_KEY_WORD = Pattern.compile("(?<TYPE>int|float|vec[234]|mat[234])");
	private static final int TYPE_GROUP = 1 ;

	{
		getStylesheets().add(GLSLEditor.class.getResource("glsl-highlighting.css").toExternalForm());
	}

	public GLSLEditor() {
		super();
		setParagraphGraphicFactory(LineNumberFactory.get(this));
		textProperty().addListener((obs, oldText, newText) -> {
			setStyleSpans(0, computeHighlighting(newText));
		});
	}

	public GLSLEditor(String text) {
		this();
		replaceText(0, 0, text);
		getUndoManager().forgetHistory();
		getUndoManager().mark();
	}

	private StyleSpans<? extends Collection<String>> computeHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		Matcher typeMatcher = GLSL_KEY_WORD.matcher(text);
		int lastKwEnd = 0;

		while (typeMatcher.find()) {
			spansBuilder.add(Collections.emptyList(), typeMatcher.start() - lastKwEnd);
			if (typeMatcher.group(TYPE_GROUP) != null) {
				spansBuilder.add(Collections.singleton("type"), typeMatcher.end() - typeMatcher.start());
			} else {
				spansBuilder.add(Collections.singleton("text"), typeMatcher.end() - typeMatcher.start());
			}
			lastKwEnd = typeMatcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
