package texture.image;

import utils.BufferUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BufferedImageReader implements ImageReader {

	private BufferedImage image;
	private java.awt.image.BufferedImage imageData;
	private int[] pixelBuffer;

	public BufferedImageReader(BufferedImage image) {
		this.image = image;
		this.imageData = image.getData();
	}

	@Override
	public ByteBuffer getBuffer() {
		return getBuffer(0, 0, image.getWidth(), image.getHeight());
	}

	@Override
	public Buffer getBuffer(int x, int y, int xEnd, int yEnd) {

		int width  = xEnd - x;
		int height = yEnd - y;

		if (width < 0 || height <0 ) {
			throw new IllegalArgumentException("XEnd/YEnd must be greater than x/y.");
		}

		int bufferSize = width * height;
		if (pixelBuffer == null || bufferSize != pixelBuffer.length) {
			pixelBuffer = new int[bufferSize];
		}

		imageData.getRGB(x, y, xEnd, yEnd, pixelBuffer, 0, image.getWidth());
		IntBuffer imageBuffer = BufferUtils.createBuffer(pixelBuffer);
		imageBuffer.flip();

		return imageBuffer;
	}
}
