package core.renderer;

import core.engine.EngineComponent;

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
			System.out.println(currentFps);
		}

		// TODO: Font renderer should use this renderer it self
		// fontRenderer.drawString("FPS " + currentFps, 0, 0, 1.0f, Color.Red);
	}

	public int getCurrentFPS() {
		return currentFps;
	}
}
