package eu.yvka.slothengine.renderer;

import eu.yvka.slothengine.engine.Engine;
import eu.yvka.slothengine.engine.EngineComponent;
import eu.yvka.slothengine.math.Color;
import eu.yvka.slothengine.renderer.font.FontRenderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FPSCounter implements EngineComponent {

	private int renderedFrames;
	private double lastTime;
	private int currentFps;
	private boolean initialized;

	@Override
	public void initialize() {
		lastTime = glfwGetTime();
		currentFps = 0;
		initialized = true;
	}

	@Override
	public void shutdown() {
		initialized = false;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void onFrameEnd() {
		double currentTime = glfwGetTime();
		renderedFrames++;
		if (currentTime - lastTime >= 1.0) {
			currentFps = renderedFrames;
			renderedFrames = 0;
			lastTime += 1.0;
		}
	}

	@Override
	public void onAfterRender(float elapsedTime) {
		FontRenderer fontRenderer = Engine.renderManager().getFontRenderer();
		fontRenderer.drawString("FPS " + currentFps, 0, 0, 1.5f, Color.Red);
	}

	public int getCurrentFPS() {
		return currentFps;
	}
}
