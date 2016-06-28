package eu.yvka.slothengine.engine;

import eu.yvka.slothengine.window.Window;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.lwjgl.util.stream.RenderStream;
import org.lwjgl.util.stream.StreamHandler;
import org.lwjgl.util.stream.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * JavaFX image view offscreen render support based on
 * org.lwjgl.util.stream.
 *
 * @see <a href="https://github.com/Spasi/LWJGL-FX">Spasi/LWJGL-FX</a>
 *
 */
public class JavaFXOffscreenSupport implements EngineComponent {

	public static final String JAVAFX_OFFSCREEN_SUPPORT = "JAVAFX_OFFSCREEN_SUPPORT";
	private static final Logger Log = LoggerFactory.getLogger(JavaFXOffscreenSupport.class);

	private boolean initialized;
	private StreamUtil.RenderStreamFactory renderStreamFactory;
	private RenderStream renderStream;
	private CountDownLatch runningLatch;
	private ImageView renderView;

	public JavaFXOffscreenSupport(ImageView renderView, CountDownLatch runningLatch) {
		this.runningLatch = runningLatch;
		this.renderView = renderView;
	}

	public Node getNode() {
		return renderView;
	}


	@Override
	public void initialize() {
		Log.info("Initialize JavaFX Offscreen Renderer");

		Window window = Engine.getPrimaryWindow();
		window.setSize((int)renderView.getFitWidth(), (int)renderView.getFitHeight());
		window.hide();

		this.renderStreamFactory = StreamUtil.getRenderStreamImplementation();
		this.renderStream = renderStreamFactory.create(getReadHandler(), 1, 2);
		initialized = true;
	}

	@Override
	public void shutdown() {
		Log.info("Shutdown JavaFX Offscreen Renderer");
		renderStream.destroy();
		initialized = false;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void onBeforeRender(float elapsedTime) {
		renderStream.bind();
	}

	@Override
	public void onFrameEnd() {
		try {
			renderStream.swapBuffers();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (runningLatch.getCount() <= 0) {
			Engine.requestExit();
		}
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
						// If we're quitting, discard onUpdate
						if ( !renderView.isVisible() )
							return;

						// Detect resize and recreate the image
						if ( renderImage == null || (int)renderImage.getWidth() != width || (int)renderImage.getHeight() != height ) {
							renderImage = new WritableImage(width, height);
							renderView.setImage(renderImage);
						}

						// Throttling, only onUpdate the JavaFX shadersloth.view once per frame.
						// *NOTE*: The +1 is weird here, but apparently setPixels triggers a new pulse within the current frame.
						// If we ignore that, we'd get a) worse performance from uploading double the frames and b) exceptions
						// on certain configurations (e.g. Nvidia GPU with the D3D pipeline).
						if ( frame <= lastUpload + 2 )
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


}
