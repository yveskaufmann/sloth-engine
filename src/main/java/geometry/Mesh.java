package geometry;

import com.sun.prism.impl.VertexBuffer;
import renderer.Renderer;
import utils.HardwareObject;

import static org.lwjgl.opengl.GL11.*;

public class Mesh extends HardwareObject {

	private Mode mode;
	private VertexBuffer buffer;

	protected Mesh(Object object, Class<?> type) {
		super(object, type);
	}

	@Override
	public void deleteObject(Renderer renderer) {

	}

	@Override
	public void resetObject() {

	}

	public int getVertexCount() {
		return 0;
	}

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

		Mode(int mode) { this.mode = mode; }
		public int value() {return this.mode;}
	}
}
