package geometry;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

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
		Index,
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

	public enum Format {
		Float,
		Double,
		Byte,
		Unsingned_Byte,
		Short,
		Unsined_Short,
		Int,
		Unsined_Int
	}

	private Usage usage;
	private int id;
	private int vertexCount;
	private Buffer buffer;

	private  FloatBuffer createFloatBufferFrom(float[] vertices) {
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.flip();
		return vertexBuffer;
	}

	public void setData(Usage usage, Format format, int components, Buffer buffer) {

	}

	/**
	 * @return the id of the underlying vao object.
	 */
	public int getId() {
		return this.id;
	}

	public int getVertexCount() {
		return vertexCount;
	}


}
