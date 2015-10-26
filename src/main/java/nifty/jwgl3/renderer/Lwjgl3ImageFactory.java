package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;
import de.lessvoid.nifty.render.batch.spi.ImageFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;

/**
 * @author Aaron Mahan &lt;aaron@forerunnergames.com&gt;
 */
public class Lwjgl3ImageFactory implements ImageFactory {
	@Nonnull
	@Override
	public BatchRenderBackend.Image create(@Nullable final ByteBuffer buffer, final int imageWidth, final int imageHeight) {
		return new Lwjgl3Image(buffer, imageWidth, imageHeight);
	}

	@Nullable
	@Override
	public ByteBuffer asByteBuffer(@Nullable final BatchRenderBackend.Image image) {
		return image instanceof Lwjgl3Image ? ((Lwjgl3Image)image).getBuffer() : null;
	}
}
