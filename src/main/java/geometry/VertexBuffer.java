package geometry;

import renderer.Renderer;
import utils.HardwareObject;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer extends HardwareObject {

	public boolean getNormalized() {
		return normalized;
	}

	public boolean isNormalized() {
		return normalized;
	}

	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}

	public int getStride() {
		return stride;
	}

	public int getOffset() {
		return offset;
	}

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
		Interleaved, CpuOnly, Index
	}

	public static enum Usage {
		/**
		 * The data store contents will be modified once and used at most a few times as source for GL drawing.
		 *
		 */
		STREAM_DRAW,

		/**
		 * The data store contents will be modified once and used at most a few times and
		 * used to return that data when queried by the application.
		 *
		 */
		STREAM_READ,

		/**
		 * The data store contents will be modified once and used at most a few times and
		 * used as the source for GL drawing and image specification commands.
		 *
		 */
		STREAM_COPY,


		/**
		 *  The data store contents will be modified once and used many times as
		 *  source for GL drawing.
		 */
		STATIC_DRAW,

		/**
		 * The data store contents will be modified once and used many times and
		 * used to return that data when queried by the application.
		 */
		STATIC_READ,

		/**
		 * The data store contents will be modified once and used many times and
		 * used as the source for GL drawing and image specification commands.
		 */
		STATIC_COPY,

		/**
		 *  The data store contents will be modified repeatedly and used many times as
		 *  source for GL drawing.
		 */
		DYNAMIC_DRAW,

		/**
		 * The data store contents will be modified repeatedly and used many times and
		 * used to return that data when queried by the application.
		 */
		DYNMAIC_READ,

		/**
		 * The data store contents will be modified repeatedly and used many times and
		 * used as the source for GL drawing and image specification commands.
		 */
		DYNAMIC_COPY;

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
	 * The type of this vertex buffer
	 */
	private Type type;

	/**
	 * The usage of this buffer
	 */
	private Usage usage;

	/**
	 * Format of the buffer
	 */
	private Format format;

	/**
	 * Buffer of twhich holds the data.
	 */
	private Buffer buffer;


	/**
	 * the position of the last element
	 */
	private int vertexCount;

	/**
	 * Count of components must be between 1-4
	 */
	private int components;

	/**
	 * The stride of this buffer
	 */
	private int stride = 0;

	/**
	 * The offset of this buffer
	 */
	private int offset = 0;

	/**
	 * Specifies if the the underlying buffer contains normalized data
	 */
	private boolean normalized = false;


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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public void setFormat(Format format) {
		this.format = format;
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

	public boolean hasChanged() {
		return true;
	}
}
