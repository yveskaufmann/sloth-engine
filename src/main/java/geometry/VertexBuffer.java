package geometry;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexBuffer {
	public enum Usage {
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



	private Usage usage;
	private int id;
	private int vertexCount;
	private Buffer buffer;



	public void loadFrom(float[] vertices) {
		this.id = glGenVertexArrays();
		glBindVertexArray(this.id);
		FloatBuffer vertexBuffer = createFloatBufferFrom(vertices);
		storeInFloatAttributeList(0, vertexBuffer);
		glBindVertexArray(0);
	}

	protected void storeInFloatAttributeList(int index, FloatBuffer vertexBuffer) {
		storeInFloatAttributeList(index, vertexBuffer, Usage.STATIC_DRAW);
	}

	protected void storeInFloatAttributeList(int index, FloatBuffer vertexBuffer, Usage usage) {
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, usage.value());
		glVertexAttribPointer(index, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}

	private  FloatBuffer createFloatBufferFrom(float[] vertices) {
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.flip();
		return vertexBuffer;
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
