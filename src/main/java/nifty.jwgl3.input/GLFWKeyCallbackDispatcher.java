package nifty.jwgl3.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.HashSet;
import java.util.Set;

public class GLFWKeyCallbackDispatcher extends GLFWKeyCallback {

	@FunctionalInterface
	public interface KeyCallback extends GLFWKeyCallback.SAM {};
	private Set<KeyCallback> callbacks;

	public GLFWKeyCallbackDispatcher() {
		super();
		this.callbacks = new HashSet<>();
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		callbacks.forEach((callback) -> callback.invoke(window, key, scancode, action, mods));
	}

	public boolean add(KeyCallback callback) {
		return callbacks.add(callback);
	}

	public boolean remove(KeyCallback callback) {
		return callbacks.remove(callback);
	}
}
