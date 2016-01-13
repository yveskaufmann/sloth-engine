package geometry;

import geometry.VertexAttributePointer.Format;
import renderer.Renderer;
import utils.BufferUtils;
import utils.HardwareObject;

import static geometry.VertexBuffer.*;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;

public class Mesh extends HardwareObject {

	/**
	 * Specifies the kinds of primitives which could be used to render
	 * a raw model.
	 */
	public enum Mode {
		/**
		 * <p>
		 * This mode will cause the renderer to interpret each individual<br>
		 * vertex of the model as point.
		 * <br><br>
		 * Points are rasterized as screen-aligned squares of a given window-space size.<br>
		 * The size can be given by two methods:<br>
		 * <br>
		 * <ul>
		 *     <li>glEnable(GL_PROGRAM_POINT_SIZE) and set the size by the shader output variable float gl_PointSize</li>
		 *     <li>glDisable(GL_PROGRAM_POINT_SIZE) and set the size by  using glPointSize(float size)​</li>
		 * </ul>
		 * </p>
		 */
		POINTS(GL_POINTS),

		/**
		 * <p>
		 * This mode will cause the renderer to interpret each group of two successive vertices as a single line.<br>
		 * <br>
		 * Which leads to a set of non connected lines if enough vertices are specified.<br>
		 * For example: The vertices 0 and 1 are considered as line, the vertices 2 and 3 are considered as line
		 * and so on.<br>
		 * <br>
		 * If a non even number of vertices was specified the rest of the vertices will
		 * be ignored.<br>
		 * The width of the line can be specified by using glLineWidth(float width).
		 * </p>
		 */
		LINES(GL_LINES),

		/**
		 * <p>
		 * This mode will cause the renderer to interpret the adjacent vertices as a connected line.<br>
		 * <br>
		 * If N vertices are specified the rendered line consists of N - 1 segments which implies
		 * that one Vertex leads a non rendered line. <br>
		 * <br>
		 * The width of the line can be specified by using glLineWidth(float width).
		 * </p>
		 *
		 */
		LINE_STRIP(GL_LINE_STRIP),

		/**
		 * <p>
		 * This mode acts like LINE_STRIP, except that the first and last vertices are
		 * also considered as a line.<br>
		 * <br>
		 * If N vertices are specified the rendered line consists of N segments.
		 * </p>
		 *
		 */
		LINE_LOOP(GL_LINE_LOOP),


		/**
		 * <p>
		 * This mode will cause the renderer to interpret each group of three successive vertices as a single triangle.<br>
		 * <br>
		 * Which leads to a set of non connected triangles if enough vertices are specified.<br>
		 * For example: The vertices 0, 1 and 2 are considered as triangle, the vertices 3, 4, 5 are considered as triangle
		 * and so on.<br>
		 * <br>
		 * If a number not divisible by three of vertices was specified the rest of the vertices will
		 * be ignored.N Vertices leads to N/3 triangles.
		 * </p>
		 */
		TRIANGLES(GL_TRIANGLES),

		/**
		 * <p>
		 * This mode will cause the renderer to interpret each group of three adjacent vertices as a connected triangle.<br>
		 * <br>
		 * Which leads to a set of connected triangles if enough vertices are specified.<br>
		 * The face direction of strip triangles is specifies by the winding of the  first triangle.<br>
		 * <br>
		 * If a number not divisible by three of vertices was specified the rest of the vertices will
		 * be ignored. N Vertices leads to N - 2 triangles.
		 * </p>
		 */
		TRIANGLE_STRIP(GL_TRIANGLE_STRIP),

		/**
		 * <p>
		 * This mode will cause the renderer to interpret the vertex list as follows:<br>
		 * <br>
		 * The first vertex is always held fixed. From there on, every group of 2 adjacent <br>
		 * vertices form a triangle with the first. So with a vertex list, you get a list of triangles <br>
		 * like so: (0, 1, 2) (0, 2, 3), (0, 3, 4), etc. A vertex stream of n length will generate n-2 triangles.<br>
		 * </p>
		 */
		TRIANGLE_FAN(GL_TRIANGLE_FAN);

		protected int mode;

		Mode(int mode) {
			this.mode = mode;
		}

		public int value() {
			return this.mode;
		}
	}

	private Map<Type, VertexBuffer> buffers = new HashMap<>();
	private Mode mode = Mode.TRIANGLES;
	private float lineSize = 1.0f;
	private float pointSize = 1.0f;
	private int vertexCount = -1;
	private int elementCount = -1;

	public Mesh() {
		super(Mesh.class);
	}

	public VertexBuffer getBuffer(Type type) {
		return buffers.get(type);
	}

	public void setBuffer(Type type, int components, FloatBuffer buffer) {
		setBuffer(type, buffer, components, VertexAttributePointer.Format.Float);
	}

	public void setBuffer(Type type, int components, float[] buffer) {
		setBuffer(type, components, BufferUtils.createBuffer(buffer));
	}

	public void setBuffer(Type type, int components, IntBuffer buffer) {
		setBuffer(type, buffer, components, VertexAttributePointer.Format.Unsigned_Int);
	}

	public void setBuffer(Type type, int components, int[] buffer) {
		setBuffer(type, components, BufferUtils.createBuffer(buffer));
	}

	public void setBuffer(Type type, int components, ShortBuffer buffer) {
		setBuffer(type, buffer, components, VertexAttributePointer.Format.Unsigned_Short);
	}

	public void setBuffer(Type type, int components, short[] buffer) {
		setBuffer(type, components, BufferUtils.createBuffer(buffer));
	}

	public void setBuffer(Type type, int components, ByteBuffer buffer) {
		setBuffer(type, buffer, components, VertexAttributePointer.Format.Unsingned_Byte);
	}

	public void setBuffer(Type type, int components, byte[] buffer) {
		setBuffer(type, components, BufferUtils.createBuffer(buffer));
	}

	public void setBuffer(Type type, Buffer buffer, int components, Format format) {
		VertexBuffer vertexBuffer = buffers.get(type);
		if (vertexBuffer == null) {
			vertexBuffer = new VertexBuffer(type);
			buffers.put(type, vertexBuffer);
		}

		vertexBuffer.setupData(buffer);
		vertexBuffer.getPointer().setComponents(components);
		vertexBuffer.getPointer().setFormat(format);
		calculateCounts();
	}


	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public float getLineWidth() {
		return lineSize;
	}

	public void setLineSize(float lineSize) {
		this.lineSize = lineSize;
	}

	public float getPointSize() {
		return pointSize;
	}

	public void setPointSize(float pointSize) {
		this.pointSize = pointSize;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public Stream<VertexBuffer> bufferStream() {
		return buffers.values().stream();
	}

	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}

	public float getLineSize() {
		return lineSize;
	}


	@Override
	public void deleteObject(Renderer renderer) {
		for (VertexBuffer buffer : buffers.values()) {
			renderer.deleteBuffer(buffer);
			buffer.resetObject();
		}
		resetObject();
	}

	@Override
	public void resetObject() {
		enableUpdateRequired();
	}

	private void calculateCounts() {
		VertexBuffer positionBuffer = getBuffer(Type.Vertex);
		VertexBuffer indexBuffer = getBuffer(Type.Index);

		if (positionBuffer != null) {
			Buffer buffer = positionBuffer.getBuffer();
			vertexCount = buffer.limit() / positionBuffer.getPointer().getComponents();
		}

		if (indexBuffer != null) {
			elementCount = computeNumElements(indexBuffer.getBuffer().limit());
		} else {
			elementCount = computeNumElements(vertexCount);
		}
	}

	private int computeNumElements(int bufferSize) {
		switch (mode) {
			case TRIANGLES:return bufferSize / 3;
			case TRIANGLE_FAN:
			case TRIANGLE_STRIP:return bufferSize - 2;
			case POINTS:return bufferSize;
			case LINES:return bufferSize / 2;
			case LINE_LOOP:return bufferSize;
			case LINE_STRIP:return bufferSize - 1;
			default:
				throw new UnsupportedOperationException();
		}
	}

}
