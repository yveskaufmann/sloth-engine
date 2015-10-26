package sandbox;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.sound.openal.OpenALSoundDevice;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.resourceloader.ClasspathLocation;
import nifty.jwgl3.input.GLFWKeyCallbackDispatcher;
import nifty.jwgl3.input.Lwjgl3InputSystem;
import nifty.jwgl3.renderer.Lwjgl3RenderDeviceFactory;
import util.NativeLibraryLoader;
import window.Window;
import window.WindowManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Sandbox {

	public static class MyScreenController extends DefaultScreenController {
		@NiftyEventSubscriber(id="exit")
		public void exit(final String id, Object etc) {
			nifty.exit();
		}
	}
	private static Screen createIntroScreen(final Nifty nifty, final ScreenController controller) {
		return new ScreenBuilder("start") {{
			controller(controller);
			layer(new LayerBuilder("layer") {{
				childLayoutCenter();
				onStartScreenEffect(new EffectBuilder("fade") {{
					length(500);
					effectParameter("start", "#0");
					effectParameter("end", "#f");
				}});
				onEndScreenEffect(new EffectBuilder("fade") {{
					length(500);
					effectParameter("start", "#f");
					effectParameter("end", "#0");
				}});
				onActiveEffect(new EffectBuilder("gradient") {{
					effectValue("offset", "0%", "color", "#333f");
					effectValue("offset", "100%", "color", "#ffff");
				}});
				panel(new PanelBuilder() {{
					childLayoutVertical();
					text(new TextBuilder() {{
						text("Nifty 1.4 Core Hello World");
						style("base-font");
						color(Color.BLACK);
						alignCenter();
						valignCenter();
					}});
					panel(new PanelBuilder(){{
						height(SizeValue.px(10));
					}});
				}});
			}});
		}}.build(nifty);
	}

	static {
		NativeLibraryLoader.load();
	}

	private GLFWKeyCallbackDispatcher keyCallback;

	public void run() throws Exception {


		Window window = WindowManager.get()
			.setTitle("Window the first")
			.setSize(1024, 768)
			.setResizeable(true)
			.setGLContextVersion(3, 0)
			.build()
			.enable();

		Nifty nifty = new Nifty(
			Lwjgl3RenderDeviceFactory.create(window.getWindowId()),
			new OpenALSoundDevice(),
			new Lwjgl3InputSystem(window.getWindowId()),
			new AccurateTimeProvider());


		createIntroScreen(nifty, new MyScreenController());
		nifty.registerMouseCursor("test", "nitfy-cursor.tga", 0, 0);
		nifty.getNiftyMouse().enableMouseCursor("test");
		nifty.gotoScreen("start");


		glfwSetKeyCallback(window.getWindowId(), keyCallback = new GLFWKeyCallbackDispatcher());

		keyCallback.add((_window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE) {
				glfwSetWindowShouldClose(_window, GL_TRUE);
			}
		});


		boolean done = false;
		while ( !window.shouldClose() && !done) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (nifty.update()) {
				done = true;
			}

			nifty.render(true);
			window.update();
		}

		keyCallback.release();
		WindowManager.get().clean();

	}

	public static void main(String[] args) throws Exception {
		Sandbox box = new Sandbox();
		box.run();
	}
}
