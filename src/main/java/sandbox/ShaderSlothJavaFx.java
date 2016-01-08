package sandbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class ShaderSlothJavaFx extends Application {

	private final static Logger Log = LoggerFactory.getLogger(ShaderSlothJavaFx.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
		primaryStage.setTitle("ShaderSloth");
		primaryStage.setMinWidth(640);
		primaryStage.setHeight(480);

		final URL fxmlURL = getClass().getClassLoader().getResource("view/ShaderSlothView.fxml");
		final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);

		try {
			final Pane content = (Pane) fxmlLoader.load();
			final Scene scene = new Scene(content);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			Log.error("Failed to load shader sloth view", e);
			System.exit(-1);
			return;
		}


		new Thread(() -> {
			// Start renderer
			// Platform.runLater(primaryStage::close);
		}, "RendererThread").start();
	}
}
