package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.spi.BufferFactory;
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class Lwjgl3BufferFactory implements BufferFactory {
	@Nonnull
	@Override
	public ByteBuffer createNativeOrderedByteBuffer(int numBytes) {
		return BufferUtils.createByteBuffer(numBytes);
	}

	@Nonnull
	@Override
	public FloatBuffer createNativeOrderedFloatBuffer(int numFloats) {
		return BufferUtils.createFloatBuffer(numFloats);
	}

	@Nonnull
	@Override
	public IntBuffer createNativeOrderedIntBuffer(int numInts) {
		return BufferUtils.createIntBuffer(numInts);
	}
}
