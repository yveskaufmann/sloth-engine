package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;

import java.nio.ByteBuffer;

public class Lwjgl3Image extends BatchRenderBackend.ByteBufferedImage {
	public Lwjgl3Image(ByteBuffer buffer, int imageWidth, int imageHeight) {
		super(buffer, imageWidth, imageHeight);
	}
}
