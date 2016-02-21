package core.renderer;


import core.engine.Engine;
import core.engine.EngineComponent;
import core.material.Material;
import core.math.Color;
import core.renderer.font.FontRenderer;
import core.scene.camera.Camera;
import core.scene.Geometry;
import core.scene.Node;
import core.scene.Scene;
import core.shader.Shader;
import core.texture.Texture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.util.Map;


public class RendererManager implements EngineComponent {

	private static final Logger Log = LoggerFactory.getLogger(RendererManager.class);

	private RenderState renderState;
	private Renderer renderer;
	private FontRenderer fontRenderer;
	private boolean initialized = false;
	private Scene currentScene;

	@Override
	public void initialize() {
		if (initialized) {
			throw new IllegalArgumentException("The RenderManger is already initialized");
		}

		renderState = new RenderState();
		if (renderer == null) {
			renderer = new Lwjgl3Renderer();
			renderer.applyRenderState(renderState);
			Engine.getPrimaryWindow().updateViewportSize();
		}

		if (fontRenderer == null) {
			fontRenderer = new FontRenderer(new Font("Courier", Font.PLAIN, 32));
		}

		initialized = true;
	}

	@Override
	public void shutdown() {
		if (!initialized) {
			throw new IllegalArgumentException("The RenderManager isn't initialized");
		}

		if (renderer != null) {
			renderer.cleanUp();
		}

		initialized = false;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}



	@Override
	public void onFrameStart() {
		if (renderer != null) {
			renderer.onNewFrame();
		}
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	/**
	 * Get the render state of the active core.renderer.
	 *
	 * @return the state of the current active core.renderer.
	 */
	public RenderState getRenderState() {
		return renderState;
	}

	public void updateViewportSize(int x, int y, int width, int height) {
		if (renderer != null) {
			renderer.setViewport(x, y, width, height);
		}
	}

	public void setScene(Scene scene) {
		if (currentScene != null) {
			currentScene.setActive(false);
		}
		currentScene = scene;
	}

	public void render(float elapsedTime) {
		if (currentScene == null) return;

		renderer.setClearColor(Color.LightGrey);
		renderer.clearBuffers(true, true, true);

		//TODO: Filter Gemoetry Nodes which are not in sight
		currentScene.traverse((node) -> {
			if (node instanceof Geometry) {
				Geometry geometry = (Geometry) node;
				if (geometry.isVisible()) {

					GL11.glEnable(GL11.GL_STENCIL_TEST);
					GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

					renderGeometry((Geometry) node);
					geometry.getMaterial().getRenderState().enableWireframe(false);
					geometry.getMaterial().getRenderState().setBlendMode(RenderState.BlendFunc.Alpha);
					geometry.getMaterial().getRenderState().enableSmoothLines();
					geometry.getMaterial().getShader().getUniform("isWireframe").setValue(1);
					float oldScale = geometry.getScale().x;
					geometry.setScale(oldScale * 1.05f);
					GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
					renderGeometry((Geometry) node);
					geometry.getMaterial().getRenderState().enableWireframe(false);
					geometry.getMaterial().getRenderState().setBlendMode(RenderState.BlendFunc.Alpha);
					geometry.getMaterial().getRenderState().disableSmoothLines();
					geometry.getMaterial().getShader().getUniform("isWireframe").setValue(0);
					geometry.setScale(oldScale);
					GL11.glDisable(GL11.GL_STENCIL_TEST);
				}
			}
		});
	}

	private void renderGeometry(Geometry geometry) {
		Material material = geometry.getMaterial();
		Camera camera = currentScene.getCamera();

		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f modelMatrix = geometry.getTransformMatrix();

		Matrix4f normalMatrix = new Matrix4f();
		viewMatrix.normal(normalMatrix);

		Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);
		Matrix4f modelViewProjectionMatrix = new Matrix4f(projectionMatrix).mul(modelViewMatrix);

		try {
			Shader shader = material.getShader();
			// TODO shader parameters should be received from material
			shader.getUniform("sl_projectionMatrix").setValue(projectionMatrix);
			shader.getUniform("sl_viewMatrix").setValue(viewMatrix);
			shader.getUniform("sl_modelMatrix").setValue(modelMatrix);
			shader.getUniform("sl_normalMatrix").setValue(normalMatrix);
			// shader.getUniform("isWireframe").setValue(material.getRenderState().isWireframe() ? 1 : 0);

			shader.getUniform("sl_modelViewMatrix").setValue(modelViewMatrix);
			shader.getUniform("sl_mvp").setValue(modelViewProjectionMatrix);

			if (currentScene.getLightList() != null) {
				currentScene.getLightList().passToShader(shader);
			}

			renderer.setShader(material.getShader());

		} catch (Exception ex) {
			Log.error("Failed to setup shader", ex);
		}

		for(Map.Entry<Integer, Texture> texture : material.getTextures().entrySet()) {
			renderer.setTexture(texture.getKey(), texture.getValue());
		}

		renderer.applyRenderState(material.getRenderState());
		renderer.drawMesh(geometry.getMesh());
	}

}
