package renderer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.lang.reflect.Field;

import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLCaps {

	public static String createCapsString() {
		GLCapabilities caps = GL.createCapabilities();
		Class<?> capsType = caps.getClass();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Graphic Carps Caps:").append("\n");
		buffer.append("-------------------").append("\n\n");

		for(Field field : capsType.getFields()) {
			if (!field.getType().equals(Boolean.TYPE)) continue;

			try {
				if (field.getBoolean(caps)) {
					buffer.append(field.getName());
					buffer.append("\n");
                }
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return buffer.toString();
	}

	public static void main(String[] args) {
		GLFW.glfwInit();
		long windowId = GLFW.glfwCreateWindow(1, 1, "", NULL, NULL);
		GLFW.glfwHideWindow(windowId);
		GLFW.glfwMakeContextCurrent(windowId);

		GL.createCapabilities();
		System.out.println(createCapsString());

		GLFW.glfwDestroyWindow(windowId);
		GLFW.glfwTerminate();

	}
}
