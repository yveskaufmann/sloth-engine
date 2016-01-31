package shadersloth.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.lwjgl.util.stream.StreamHandler;
import shadersloth.ShaderSloth;
import shadersloth.SlothApplication;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class ShaderSlothController implements Initializable {

	@FXML private VBox renderViewRoot;
	@FXML private ImageView renderView;

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


	/**
	 * The underlying core.renderer implementation
	 */
	private SlothApplication sandbox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		renderView.fitWidthProperty().bind(renderViewRoot.widthProperty());
		renderView.fitHeightProperty().bind(renderViewRoot.heightProperty());


	}

	private StreamHandler getReadHandler() {
		return new StreamHandler() {

			private WritableImage renderImage;

			private long frame;
			private long lastUpload;

			{
				new AnimationTimer() {
					@Override
					public void handle(final long now) {
						frame++;
					}
				}.start();
			}

			public int getWidth() {
				return (int)renderView.getFitWidth();
			}

			public int getHeight() {
				return (int)renderView.getFitHeight();
			}

			public void process(final int width, final int height, final ByteBuffer data, final int stride, final Semaphore signal) {
				// This method runs in the background rendering thread
				Platform.runLater(() -> {
                    try {
                        // If we're quitting, discard update
                        if ( !renderView.isVisible() )
                            return;

                        // Detect resize and recreate the image
                        if ( renderImage == null || (int)renderImage.getWidth() != width || (int)renderImage.getHeight() != height ) {
                            renderImage = new WritableImage(width, height);
                            renderView.setImage(renderImage);
                        }

                        // Throttling, only update the JavaFX view once per frame.
                        // *NOTE*: The +1 is weird here, but apparently setPixels triggers a new pulse within the current frame.
                        // If we ignore that, we'd get a) worse performance from uploading double the frames and b) exceptions
                        // on certain configurations (e.g. Nvidia GPU with the D3D pipeline).
                        if ( frame <= lastUpload + 1 )
                            return;

                        lastUpload = frame;

                        // Upload the image to JavaFX
                        PixelWriter pw = renderImage.getPixelWriter();
                        pw.setPixels(0, 0, width, height, pw.getPixelFormat(), data, stride);
                    } finally {
                        // Notify the render thread that we're done processing
                        signal.release();
                    }
                });
			}
		};
	}


	/**
	 * This method will run the rendering thread.
	 * This method is adapted from: https://github.com/Spasi/LWJGL-FX/blob/master/src/lwjglfx/GUIController.java
	 *
	 * @param runningLatch
     */
	public void runRenderer(final CountDownLatch runningLatch) {
		viewportProperty().addListener(((observable, oldValue, newValue) -> {
			System.out.println(newValue.toString());
		}));

		sandbox = new ShaderSloth();
		sandbox.start(runningLatch, this::getReadHandler, renderView.viewportProperty());
	}

}
