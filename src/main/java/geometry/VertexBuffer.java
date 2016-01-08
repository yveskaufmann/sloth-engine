package geometry;

import renderer.Renderer;
import utils.HardwareObject;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer extends HardwareObject {

	public static enum Type {
		Vertex,
		Normal,
		TextCoords,
		TextCoords02,
		TextCoords03,
		TextCoords04,
		TextCoords05,
		TextCoords06,
		Color,
		Index
	}

	public static enum Usage {
		/**
		 * The data store contents will be modified once and used at most a few times as source for GL drawing.
		 *
		 */
		STREAM_DRAW(GL_STREAM_DRAW),

		/**
		 * The data store contents will be modified once and used at most a few times and
		 * used to return that data when queried by the application.
		 *
		 */
		STREAM_READ(GL_STREAM_READ),

		/**
		 * The data store contents will be modified once and used at most a few times and
		 * used as the source for GL drawing and image specification commands.
		 *
		 */
		STREAM_COPY(GL_STREAM_COPY),


		/**
		 *  The data store contents will be modified once and used many times as
		 *  source for GL drawing.
		 */
		STATIC_DRAW(GL_STATIC_DRAW),

		/**
		 * The data store contents will be modified once and used many times and
		 * used to return that data when queried by the application.
		 */
		STATIC_READ(GL_STATIC_READ),

		/**
		 * The data store contents will be modified once and used many times and
		 * used as the source for GL drawing and image specification commands.
		 */
		STATIC_COPY(GL_STATIC_COPY),

		/**
		 *  The data store contents will be modified repeatedly and used many times as
		 *  source for GL drawing.
		 */
		DYNAMIC_DRAW(GL_DYNAMIC_DRAW),

		/**
		 * The data store contents will be modified repeatedly and used many times and
		 * used to return that data when queried by the application.
		 */
		DYNMAIC_READ(GL_DYNAMIC_READ),

		/**
		 * The data store contents will be modified repeatedly and used many times and
		 * used as the source for GL drawing and image specification commands.
		 */
		DYNAMIC_COPY(GL_DYNAMIC_COPY);

		protected int usage;

		Usage(int usage) {
			this.usage = usage;
		}

		public int value() {
			return this.usage;
		}

	}


	public static enum Format {
		Float,
		Double,
		Byte,
		Unsingned_Byte,
		Short,
		Unsigned_Short,
		Int,
		Unsigned_Int;
	}
	/**
	 * The usage of this buffer
	 */
	private Usage usage;

	/**
	 * Count of vertices
	 */
	private int vertexCount;

	/**
	 * Buffer of twhich holds the data.
	 */
	private Buffer buffer;

	/**
	 * Format of the buffer
	 */
	private Format format;

	/**
	 * Count of components must be between 1-4
	 */
	private int components;

	public VertexBuffer() {
		super(VertexBuffer.class);
	}

	public void setupData(Usage usage, Format format, int components, Buffer buffer) {

		if (components < 1 || components > 4) {
			throw new IllegalArgumentException("The components count must be between 1 and 4");
		}

		if (getId() == UNSET_ID) {
			this.buffer = buffer;
			this.usage = usage;
			this.format = format;
		}

		enableUpdateRequired();
	}

	public Buffer getBuffer() {
		return  buffer;
	}

	public int getVertexCount() {
		return vertexCount;

	}

	public int getCountOfIndices() {
		return  buffer.capacity() / this.components;
	}

	public int getComponents() {
		return components;
	}

	public Format getFormat() {
		return format;
	}

	public Usage getUsage() {
		return usage;
	}

	@Override
	public void deleteObject(Renderer renderer) {
		renderer.deleteBuffer(this);
		resetObject();
	}

	@Override
	public void resetObject() {
		enableUpdateRequired();
		this.setId(UNSET_ID);
	}
}
