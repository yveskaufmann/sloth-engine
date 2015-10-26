package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.spi.MouseCursorFactory;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Aaron Mahan &lt;aaron@forerunnergames.com&gt;
 */
public class Lwjgl3MouseCursorFactory implements MouseCursorFactory {

	private long windowId;

	public Lwjgl3MouseCursorFactory(long windowId) {
		this.windowId = windowId;
	}

	@Nullable
	@Override
	public MouseCursor create(
		@Nonnull final String filename,
		final int hotspotX,
		final int hotspotY,
		@Nonnull final NiftyResourceLoader resourceLoader) throws IOException {
		return new Lwjgl3MouseCursor(windowId, filename, hotspotX, hotspotY, resourceLoader);
	}
}
