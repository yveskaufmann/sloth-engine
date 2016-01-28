package shadersloth;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shadersloth.controller.ShaderSlothController;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class ShaderSlothJavaFx extends Application {

	private final CountDownLatch runningLatch = new CountDownLatch(1);

	private final static Logger Log = LoggerFactory.getLogger(ShaderSlothJavaFx.class);
	private static final String EXAMPLE_CODE = "#version 130\n" +
		"\n" +
		"in vec3 fragmentColor;\n" +
		"out vec4 fragColor;\n" +
		"\n" +
		"uniform int isWireframe;\n" +
		"\n" +
		"void main() {\n" +
		"\t// Output color = color specified in the vertex core.shader, \n" +
		"\t// interpolated between all 3 surrounding vertices\n" +
		"\n" +
		"\tvec3 color = vec3(fragmentColor.r * 0.5);\n" +
		"\n" +
		"\tif (isWireframe == 1) {\n" +
		"\t    color = 1.0 - vec3(0.1, 0.1, 0.1);\n" +
		"\t}\n" +
		"\n" +
		"\tfragColor = vec4(color, 1.0f);\n" +
		"\n" +
		"}\n";

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {


		final URL fxmlURL = getClass().getClassLoader().getResource("view/ShaderSlothView.fxml");
		final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
		final Pane content;
		try {
			content = (Pane) fxmlLoader.load();
		} catch (Exception e) {
			Log.error("Failed to parseFile core.shader sloth view", e);
			System.exit(-1);
			return;
		}
		final ShaderSlothController controller = fxmlLoader.getController();

		Scene scene = new Scene(content);
		primaryStage.setTitle("ShaderSloth");
		primaryStage.setMinWidth(640);
		primaryStage.setHeight(480);
		primaryStage.setScene(scene);
		primaryStage.show();

		/**
		 * Informs the render thread that the application
		 * is going to sleep.
		 */
		primaryStage.setOnCloseRequest((e) -> {
			e.consume();
			runningLatch.countDown();
		});

		new Thread(() -> {
			controller.runRenderer(runningLatch);
			Platform.runLater(primaryStage::close);
		}).start();


	}


}
