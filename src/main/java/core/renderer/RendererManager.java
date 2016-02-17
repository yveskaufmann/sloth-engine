package core.renderer;


import core.engine.Engine;
import core.engine.EngineComponent;
import core.material.Material;
import core.math.Color;
import core.renderer.font.FontRenderer;
import core.scene.Camera;
import core.scene.Geometry;
import core.scene.Node;
import core.scene.Scene;
import core.shader.Shader;
import core.texture.Texture;
import org.joml.Matrix4f;
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

		renderer.applyRenderState(renderState.reset());
		renderer.clearBuffers(true, true, false);
		renderer.setClearColor(Color.LightGrey);

		Node rootNode = currentScene.getRootNode();

		//TODO: Filter Gemoetry Nodes which are not in sight
		rootNode.traversePreOrder((node) -> {
			if (node instanceof Geometry) {
				renderGeometry((Geometry) node);
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

			shader.getUniform("sl_modelViewMatrix").setValue(modelViewMatrix);
			shader.getUniform("sl_mvp").setValue(modelViewProjectionMatrix);

			if (currentScene.getLightList() != null) {
				currentScene.getLightList().passToShader(shader);
			}

			renderer.applyRenderState(material.getRenderState());
			renderer.setShader(material.getShader());
		} catch (Exception ex) {
			Log.error("Failed to setup shader", ex);
		}

		for(Map.Entry<Integer, Texture> texture : material.getTextures().entrySet()) {
			renderer.setTexture(texture.getKey(), texture.getValue());
		}

		renderer.drawMesh(geometry.getMesh());
	}

}
