package renderer;

public class RenderState {
	private boolean wireframe = false;
	private RenderContext.CullFaceMode cullFaceMode = RenderContext.CullFaceMode.Off;
	private RenderContext.TestFunc depthTestMode = RenderContext.TestFunc.Off;
	private RenderContext.BlendFunc blendMode = RenderContext.BlendFunc.Off;
	private float lineWidth = 1.0f;
	private float pointSize = 1.0f;

	public boolean isWireframe() {
		return wireframe;
	}

	public void setWireframe(boolean wireframe) {
		this.wireframe = wireframe;
	}

	public RenderContext.CullFaceMode getCullFaceMode() {
		return cullFaceMode;
	}

	public void setCullFaceMode(RenderContext.CullFaceMode cullFaceMode) {
		this.cullFaceMode = cullFaceMode;
	}

	public RenderContext.TestFunc getDepthTestMode() {
		return depthTestMode;
	}

	public void setDepthTestMode(RenderContext.TestFunc depthTestMode) {
		this.depthTestMode = depthTestMode;
	}

	public RenderContext.BlendFunc getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(RenderContext.BlendFunc blendMode) {
		this.blendMode = blendMode;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public float getPointSize() {
		return pointSize;
	}

	public void setPointSize(float pointSize) {
		this.pointSize = pointSize;
	}
}
