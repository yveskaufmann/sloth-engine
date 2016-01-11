package renderer;

import shader.Shader;

public class RenderContext {




	/**
	 * Specifies how the red, green, blue, and alpha destination blending factors are computed
	 * if <code>glEnable(GL_BLEND)</code> is enabled. You can choose
	 * on of these blend function by using the function <code>glBlendFunci</code>.*
	 */
	public enum BlendFunc {
		Off,
		Zero,
 		one,
		SourceColor,
		OneMinusSourceColor,
		DestinationColor,
	 	OneMinusDestinationColor,
	 	SourceAlpha,
	 	OneMinusSourceAlpha,
	 	DestinationAlpha,
	 	OneMinusDestinationAlpha,
		ConstantColor,
 		OneMinusConstantColor,
 		Constant_Alpha,
  		OneMinusConstantAlpha
	}

	/**
	 * Logical pixel operations <a href="https://www.opengl.org/sdk/docs/man/html/glLogicOp.xhtml">See glLogicOp</a>
	 */
	public enum LogicalPixelOperation {
		Off,
		Clear,
		Set,
		Copy,
		CopyInverted,
		Noop,
		Invert,
		And,
		Nand,
		Or,
		Nor,
		Xor,
		Equiv,
		AndReverse,
		AndInverted,
		OrReverse,
  		OrInverted
	}

	/**
	 * Cull face modes which can be used when
	 * <code>cullFace</code> is enabled. The initial
	 * value is <code>Back</code>.
	 */
	public enum CullFaceMode {
		Off,
		Front,
		Back,
		FrontAndBack
	}

	/**
	 *  The test functions which could used for <code>depthTesting</code>, <code>stencilTesting</code> and so.
	 */
	public enum TestFunc {

		/**
		 * Testing is not enabled
		 */
		Off,

		/**
		 * Never passes.
		 */
		Never,

		/**
		 * Passes if the incoming depth value is less than the stored depth value.
		 */
		Less,

		/**
		 * Passes if the incoming depth value is equal to the stored depth value.
		 */
		Equal,

		/**
		 * Passes if the incoming depth value is less than or equal to the stored depth value.*/
		LessOrEqual,

		/**
		 * Passes if the incoming depth value is greater than the stored depth value.
		 */
		Greater,

		/**
		 * Passes if the incoming depth value is not equal to the stored depth value.
		 */
		NotEqual,

		/**
		 * Passes if the incoming depth value is greater than or equal to the stored depth value.
		 */
		GreaterOrEqual,

		/**
		 * Always passes.
		 */
		Always
	}

	public enum StencilOperation {
		Off,

		/**
		 * Keeps the current value.
		 */
		Keep,

		/**
		 * Sets the stencil buffer value to 0.
		 */
		Zero,

		/**
 		* Sets the stencil buffer value to ref, as specified by glStencilFunc.
 		*/
		Replace,

		/**
		 * Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.
		 */
		Increments,

		/**
		 * Increments the current stencil buffer value.
		 * Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.
		 */
		Increments_Wrap,

		/**
		 * Decrements the current stencil buffer value. Clamps to 0.
		 */
		Decrement,

		/**
		* Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.
		*/
		Decrement_Wrap,

		/**
 		* Bitwise inverts the current stencil buffer value.
 		*/
		Invert
	}

	RenderContext() {
		reset();
	}

	/**
	 * If enabled, do alpha testing. See glAlphaFunc.
	 */
	public boolean alphaTest;

	/**
	 * if enabled, blend the computed fragment color values with the values in the color buffers
	 */
	public boolean blend;

	/**
	 * If enabled, clip geometry against user-defined half space i.
	 */
	public boolean clipDistance;

	/**
	 * Enable the use of a logical operation which is applied between the incoming RGBA color and the RGBA color at the corresponding location in the frame buffer.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glLogicOp.xhtml">See glLogicOp</a>
	 */
	public boolean colorLogicOp;

	/**
	 * If enabled, cull polygons based on their winding in window coordinates.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glCullFace.xhtml">See glCullFace</a>.
	 */
	// TODO remove me: public boolean cullface;

	/**
	 * If enabled, debug messages are produced by a debug context.
	 * When disabled, the debug message log is silenced.
	 * Note that in a non-debug context, very few, if any messages might be produced,
	 * even when GL_DEBUG_OUTPUT is enabled.
	 */
	public boolean debugOutput;

	/**
	 * If enabled, debug messages are produced synchronously by a debug context.
	 * If disabled, debug messages may be produced asynchronously.
	 * In particular, they may be delayed relative to the execution of GL commands,
	 * and the debug callback function may be called from a thread other than
	 * that in which the commands are executed.
	 *
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glDebugMessageCallback.xhtml">See glDebugMessageCallback</a>
	 */
	public boolean debugOutputSynchronously;

	/**
	 * If enabled, the −wc <= zc <= wc plane equation is ignored by view volume clipping (effectively, there is no near or far plane clipping).
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glDepthRange.xhtml">See glDepthRange</a>.
	 */
	public boolean depthClamp;

	/**
	 * If enabled, do depth comparisons and update the depth buffer.
	 * Note that even if the depth buffer exists and the depth mask is non-zero,
	 * the depth buffer is not updated if the depth test is disabled.
	 *
	 * See <a href="https://www.opengl.org/sdk/docs/man/html/glDepthFunc.xhtml">glDepthFunc</a> and <a href="https://www.opengl.org/sdk/docs/man/html/glDepthRange.xhtml">glDepthRange</a>.
	 */
	public boolean depthTesting;

	/**
	 * If enabled, dither color components or indices before they are written to the color buffer.
	 */
	public boolean dither;

	/**
	 * If enabled and the value of GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING
	 * for the framebuffer attachment corresponding to the destination buffer
	 * is GL_SRGB, the R, G, and B destination color values
	 * (after conversion from fixed-point to floating-point) are considered to be encoded
	 * for the sRGB color space and hence are linearized prior to their use in blending.
	 */
	public boolean framebufferSRGB;

	/**
	 * If enabled, draw lines with correct filtering. Otherwise, draw aliased lines.
	 *
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glLineWidth.xhtml">See glLineWidth</a>.
	 */
	public boolean lineSmooth;

	/**
	 * If enabled, use multiple fragment samples in computing the final color of a pixel.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glSampleCoverage.xhtml">See glSampleCoverage</a>.
	 */
	public boolean multisample;

	/**
	 * If enabled, and if the polygon is rendered in GL_FILL mode,
	 * an offset is added to depth values of a polygon's fragments
	 * before the depth comparison is performed.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glPolygonOffset.xhtml">See glPolygonOffset</a>.
	 */
	public boolean polygonOffsetFill;

	/**
	 * If enabled, and if the polygon is rendered in GL_LINE mode,
	 * an offset is added to depth values of a polygon's fragments
	 * before the depth comparison is performed.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glPolygonOffset.xhtml">See glPolygonOffset</a>.
	 */
	public boolean polygonOffsetLine;

	/**
	 * If enabled, an offset is added to depth values of a polygon's fragments
	 * before the depth comparison is performed, if the polygon is rendered in GL_POINT mode.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glPolygonOffset.xhtml">See glPolygonOffset</a>.
	 */
	public boolean polygonOffsetPoint;

	/**
	 * If enabled, draw polygons with proper filtering.
	 * Otherwise, draw aliased polygons.
	 * For correct antialiased polygons, an alpha buffer is needed and the polygons must be sorted
	 * front to back.
	 */
	public boolean polygonSmooth;

	/**
	 * Enables primitive restarting. If enabled,
	 * any one of the draw commands which transfers a set of generic attribute
	 * array elements to the GL will restart the primitive when the index of the vertex
	 * is equal to the primitive restart index. See glPrimitiveRestartIndex.
	 */
	public boolean primativeRestart;

	/**
	 * Enables primitive restarting with a fixed index.
	 * If enabled, any one of the draw commands which transfers
	 * a set of generic attribute array elements to the GL will restart the primitive
	 * when the index of the vertex is equal to the fixed primitive index for the
	 * specified index type.
	 * The fixed index is equal to 2n−12n−1 where n is equal
	 * to 8 for GL_UNSIGNED_BYTE, 16 for GL_UNSIGNED_SHORT and 32 for GL_UNSIGNED_INT.
	 */
	public boolean primativeRestartFixedIndex;

	/**
	 * If enabled, primitives are discarded after the optional transform feedback stage,
	 * but before rasterization.
	 * Furthermore, when enabled, glClear, glClearBufferData,
	 * glClearBufferSubData, glClearTexImage, and glClearTexSubImage are ignored.
	 */
	public boolean rasterizerDiscard;

	/**
	 * If enabled, compute a temporary coverage value where each bit is determined
	 * by the alpha value at the corresponding sample location.
	 * The temporary coverage value is then ANDed with the fragment coverage value.
	 */
	public boolean sampleAlphaToCoverage;

	/**
	 * If enabled, each sample alpha value is replaced by the maximum representable alpha value.
	 */
	public boolean sampleAlphaToOne;

	/**
	 * If enabled, the fragment's coverage is ANDed with the temporary coverage value.
	 * If GL_SAMPLE_COVERAGE_INVERT is set to GL_TRUE, invert the coverage value. See glSampleCoverage.
	 */
	public boolean sampleCoverage;

	/**
	 * If enabled, the active fragment shader is run once for each covered sample,
	 * or at fraction of this rate as determined by the current value of
	 * GL_MIN_SAMPLE_SHADING_VALUE. See glMinSampleShading.
	 */
	public boolean sampleShading;

	/**
	 * If enabled, the sample coverage mask generated for a fragment during rasterization
	 * will be ANDed with the value of GL_SAMPLE_MASK_VALUE before shading occurs.
	 * See glSampleMaski.
	 */
	public boolean sampleMask;

	/**
	 * If enabled, discard fragments that are outside the scissor rectangle.
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glScissor.xhtml">See glScissor</a>.
	 */
	public boolean scissorTest;

	/**
	 * If enabled, do stencil testing and update the stencil buffer.
	 * See <a href="https://www.opengl.org/sdk/docs/man/html/glStencilFunc.xhtml">glStencilFunc</a> and
	 *
	 * <a href="https://www.opengl.org/sdk/docs/man/html/glStencilOp.xhtml">glStencilOp</a>.
	 */
	public boolean stencilTest;

	/**
	 * If enabled, cubemap textures are sampled such that when linearly sampling from the border
	 * between two adjacent faces, texels from both faces are used to generate
	 * the final sample value. When disabled, texels from only a single face are used to
	 * construct the final sample value.
	 */
	public boolean textureCubeMapSeamless;

	/**
	 * If enabled and a vertex or geometry shader is active,
	 * then the derived point size is taken from the (potentially clipped)
	 * shader builtin gl_PointSize and clamped to the implementation-dependent point size range.
	 */
	public boolean programPointSize;

	/**
	 * If enabled the renders only wireframe output.
	 */
	public boolean wireframe;


	/**
	 * The current enabled blend function which should be used
	 * if <code>GL_BLEND</code> is enabled.
	 */
	public BlendFunc blendMode = BlendFunc.Zero;

	/**
	 * Logical Pixel operation which should be used if <code>COLOR_LOGIC_OP</code> is enabled.
	 */
	public LogicalPixelOperation logicOperation = LogicalPixelOperation.Copy;

	/**
	 * Specifies which cull face mode should be use if <code>cullface</code> is enabled.
	 */
	public CullFaceMode cullFaceMode = CullFaceMode.Back;

	/**
	 * Specifies which depth function should be used if <code>depthTesting is enabled.</code>
	 */
	public TestFunc depthTestMode = TestFunc.Less;

	/**
	 * Specifies the width of rasterized lines.
	 */
	public float lineWith = 1.0f;

	/**
	 * glPolygonOffset - factor
	 *
	 * Specifies a scale factor that is used to create a variable depth offset for each polygon.
	 * The initial value is 0.
	 */
	public float polygonOffsetFactor = 0.0f;

	/**
	 * glPolygonOffset — unit
	 *
	 * Is multiplied by an implementation-specific value to create a constant
	 * depth offset. The initial value is 0.
	 */
	public float polygonOffsetUnits = 0.0f;

	/**
	 * Specifies the test function for stencil testing.
	 */
	public TestFunc stencilTestFunc = TestFunc.Always;
	public int stencilRef = 0;
	public int stencilMask = 1;

	/**
	 * Specify the diameter of rasterized points,
	 * if <code>programPointSize</code> is disabled.
	 */
	public float pointSize;

	/**
	 * The current bounded shader
	 */
	public Shader boundShader;

	/**
	 * The current bounded vertex buffer object.
	 */
	public int boundVboBuffer;

	/**
	 * The current bounded element array buffer vbo object.
	 */
	public int boundElementArrayVboBuffer;

	public void reset() {
		blend = false;
		clipDistance = false;
		colorLogicOp = false;
		colorLogicOp = false;
		debugOutput = false;
		debugOutputSynchronously = false;
		depthClamp = false;
		depthTesting = false;
		dither = true;
		framebufferSRGB = false;
		lineSmooth = false;
		multisample = true;
		polygonOffsetFill = false;
		polygonOffsetLine = false;
		polygonOffsetPoint = false;
		polygonSmooth = false;
		primativeRestart = false;
		primativeRestartFixedIndex = false;
		rasterizerDiscard = false;
		sampleAlphaToCoverage = false;
		sampleAlphaToOne = false;
		sampleCoverage = false;
		sampleShading = false;
		sampleMask = false;
		scissorTest = false;
		stencilTest = false;
		textureCubeMapSeamless = false;
		programPointSize = false;
		wireframe = false;

		depthTestMode = TestFunc.Less;
		blendMode = BlendFunc.Zero;

		logicOperation = LogicalPixelOperation.Copy;
		cullFaceMode = CullFaceMode.Back;

		lineWith = 1.0f;
		polygonOffsetFactor = 0.0f;
		polygonOffsetUnits = 0.0f;
		boundShader = null;
		boundVboBuffer = 0;
		boundElementArrayVboBuffer = 0;

	}




}
