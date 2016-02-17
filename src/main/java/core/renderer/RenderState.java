package core.renderer;

import core.engine.Engine;

public class RenderState {



	/**
	 * Specifies how the red, green, blue, and alpha destination blending factors are computed
	 * if <code>glEnable(GL_BLEND)</code> is enabled. You can choose
	 * on of these blend function by using the function <code>glBlendFunci</code>.*
	 */
	public enum BlendFunc {
		/**
		 * Disabled Blending
		 */
		Off,
		/**
		 * Blending with GL_ONE and GL_Zero
		 */
		Default,

		/**
		 *
		 */
		Additive, Color, Alpha,
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


	private CullFaceMode cullFaceMode;
	private TestFunc depthTestMode;
	private BlendFunc blendMode;
	private float lineWidth;
	private float pointSize;
	private boolean enableFPSCounter;
	private boolean wireframe;
	private boolean lineSmooth;
	private boolean smoothLineEnabled;

	public RenderState() {
		reset();
	}

	public CullFaceMode getCullFaceMode() {
		return cullFaceMode;
	}

	public RenderState setCullFaceMode(CullFaceMode cullFaceMode) {
		this.cullFaceMode = cullFaceMode;
		return this;
	}

	public TestFunc getDepthTestMode() {
		return depthTestMode;
	}

	public RenderState setDepthTestMode(TestFunc depthTestMode) {
		this.depthTestMode = depthTestMode;
		return this;
	}

	public BlendFunc getBlendMode() {
		return blendMode;
	}

	public RenderState setBlendMode(BlendFunc blendMode) {
		this.blendMode = blendMode;
		return this;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public RenderState setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public float getPointSize() {
		return pointSize;
	}

	public RenderState setPointSize(float pointSize) {
		this.pointSize = pointSize;
		return this;
	}


	public boolean isWireframe() {
		return wireframe;
	}

	public RenderState enableWireframe(boolean wireframe) {
		this.wireframe = wireframe;
		return this;
	}

	public boolean isFPSCounterEnabled() {
		return enableFPSCounter;
	}

	public RenderState enableFPSCounter() {
		this.enableFPSCounter = true;
		return this;
	}

	public RenderState toggleWireframe() {
		this.wireframe = !this.wireframe;
		return this;
	}

	public RenderState disableFPSCounter() {
		this.enableFPSCounter = false;
		return this;
	}

	public boolean isSmoothLinesEnabled() {
		return this.lineSmooth;
	}

	public RenderState enableSmoothLines() {
		this.lineSmooth = true;
		return this;
	}

	public RenderState disableSmoothLines() {
		this.lineSmooth = false;
		return this;
	}

	public RenderState apply() {
		Engine.getCurrentRenderer().applyRenderState(this);
		return this;
	}



	public RenderState reset() {
		wireframe = false;
		enableFPSCounter = false;
		lineSmooth = false;

		cullFaceMode = CullFaceMode.Back;
		depthTestMode = TestFunc.Less;
		blendMode = BlendFunc.Alpha;
		lineWidth = 1.0f;
		pointSize = 1.0f;
		return this;
	}
}
