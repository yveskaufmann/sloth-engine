package nifty.jwgl3.renderer;


import de.lessvoid.nifty.render.io.ImageLoader;
import de.lessvoid.nifty.render.io.ImageLoaderFactory;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lwjgl3MouseCursor implements MouseCursor {

	@Nonnull
	private static final Logger log = Logger.getLogger(Lwjgl3MouseCursor.class.getName());
	private long windowId;
	private long cursorId;

	public Lwjgl3MouseCursor(long windowId, String cursorImageFilename, int hotspotX, int hotspotY, NiftyResourceLoader resourceLoader) throws IOException {
		this.windowId = windowId;
		ImageLoader imageLoader = ImageLoaderFactory.createImageLoader(cursorImageFilename);
		InputStream imageStream = resourceLoader.getResourceAsStream(cursorImageFilename);
		if (imageStream == null) {
			throw new IOException("Cannot find / load mouse cursor image file: [" + cursorImageFilename + "].");
		}
		try {
			ByteBuffer imageData = imageLoader.loadAsByteBufferARGB(imageStream, true);
			imageData.rewind();
			int width = imageLoader.getImageWidth();
			int height = imageLoader.getImageHeight();
			cursorId = GLFW.glfwCreateCursor(imageData, hotspotX, hotspotY);
			if (cursorId == 0l) {
				// TODO: receive error messages from GLFWErrorCallback
				throw new IOException("An error occurred while creating the new cursor" );
			}
		} finally {
			try {
				imageStream.close();
			} catch (IOException e) {
				log.log(Level.INFO, "An error occurred while closing the InputStream used to load mouse cursor image: " +
					"[" + cursorImageFilename + "].", e);
			}
		}
	}

	/**
	 * Enable (show) this mouse cursor, hiding / replacing the existing cursor.
	 */
	@Override
	public void enable() {
		GLFW.glfwSetCursor(windowId, cursorId);
	}

	/**
	 * Disable (hide) this mouse cursor, showing / restoring the existing cursor.
	 */
	@Override
	public void disable() {
		GLFW.glfwSetCursor(windowId, 0l);
	}

	/**
	 * Dispose all resources this mouse cursor might require. After this has been called the MouseCursor should not be
	 * used anymore.
	 */
	@Override
	public void dispose() {
		GLFW.glfwDestroyCursor(cursorId);
	}
}
