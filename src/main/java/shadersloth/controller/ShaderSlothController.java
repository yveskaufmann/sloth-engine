package shadersloth.controller;

import core.engine.AppSettings;
import core.engine.JavaFXOffscreenSupport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import shadersloth.ShaderSloth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ShaderSlothController implements Initializable {


	@FXML private VBox renderViewRoot;
	@FXML private ImageView renderView;

	/**
	 * The underlying core.renderer implementation
	 */
	private ShaderSloth rendererLoop;
	private ObjectProperty<Rectangle2D> viewport;

	public  final ObjectProperty<Rectangle2D> viewportProperty() {
		if (viewport == null) {
			viewport = new ObjectPropertyBase<Rectangle2D>(new Rectangle2D(0, 0, renderView.getFitWidth(), renderView.getFitHeight())) {
				@Override
				public Object getBean() {
					return ShaderSlothController.this;
				}

				@Override
				public String getName() {
					return "viewport";
				}
			};

			renderView.fitWidthProperty().addListener((observable, oldValue, newWidth) -> {
				viewport.set(new Rectangle2D(0, 0, (Double) newWidth, renderView.getFitHeight()));
			});

			renderView.fitHeightProperty().addListener((observable, oldValue, newWidth) -> {
				viewport.set(new Rectangle2D(0, 0, renderView.getFitWidth(), (Double) newWidth));
			});
		}
		return viewport;
	}




	@Override
	public void initialize(URL location, ResourceBundle resources) {
		renderView.setFitWidth(512);
		renderView.setFitHeight(512);




	}


	/**
	 * This method will run the rendering thread.
	 * This method is adapted from: https://github.com/Spasi/LWJGL-FX/blob/master/src/lwjglfx/GUIController.java
	 *
	 * @param runningLatch
     */
	public void runRenderer(final CountDownLatch runningLatch) {
		AppSettings settings  = new AppSettings();
		settings.set(JavaFXOffscreenSupport.JAVAFX_OFFSCREEN_SUPPORT, new JavaFXOffscreenSupport(renderView, runningLatch));

		rendererLoop = new ShaderSloth();
		rendererLoop.start(settings);
	}

}
