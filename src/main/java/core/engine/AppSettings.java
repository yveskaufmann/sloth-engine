package core.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Settings class which is used to configure the EngineApp class.
 */
public class AppSettings  {

	/**
	 * Settings/property for the title of this app.
	 */
	public static final String Title = "app_title";

	/**
	 * Settings/property for the width of the window.
	 */
	public static final String Width = "app_window_width";

	/**
	 * Settings/property for the height of a window
	 */
	public static final String Height = "app_window_height";

	/**
	 * Settings/property flag which indicates if the window
	 * should be showed in fullscreen
	 */
	public static final String Fullscreen = "app_window_fullscreen";

	/**
	 * Flag which indicates if the vsync should be enabled.
	 */
	public static final String VSync = "vsync";

	/**
	 * Property which indicates which open gl major version should be used.
	 */
	public static final String GLMajorVersion = "opengl_major_version";

	/**
	 * Property which indicates which open gl minor version should be used.
	 */
	public static final String GLMinorVersion = "opengl_minor_version";

	/**
	 * Flag which indicates if offscreenRendering should be enabled
	 */
	public static final String OffscreenRendering = "offscreenRendering";

	/**
	 * Flag which specifies if the window should be resizeable
	 */
	public static final String Resizeable = "resizeable";

	/**
	 * Specifies the implementation of a input provider
	 */
	public static final String InputProvider = "input provider type";


	private Map<String, Object> properties;

	/**
	 * Creates a app settings object with default settings
	 */
	public AppSettings() {
		properties = new HashMap<>();
		setDefaultSettings();
	}

	private void setDefaultSettings() {
		put(Title, "ShaderSloth");
		put(Width, 1024);
		put(Height, 768);
		put(Fullscreen, false);
		put(Resizeable, false);
		put(VSync, true);
		put(GLMajorVersion, 3);
		put(GLMinorVersion, 0);
		put(OffscreenRendering, false);
		put(InputProvider, "GLFW");
	}

	/**
	 * Reset this app settings to the default state.
	 */
	public void reset() {
		properties.clear();
		setDefaultSettings();
	}

	/******************************************************************************
 	* Getter Wrappers
 	*****************************************************************************/

	/**
	 * Requests a String value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
     * @return the value of the specified value or fallback.
     */
	public String getString(String prop, String fallback) {
		return getProperty(prop, fallback, String.class);
	}

	/**
	 * Requests a Integer value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
	 * @return the value of the specified value or fallback.
	 */
	public Integer getInteger(String prop, Integer fallback) {
		return getProperty(prop, fallback, Integer.class);
	}

	/**
	 * Requests a Long value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
	 * @return the value of the specified value or fallback.
	 */
	public Long getInteger(String prop, Long fallback) {
		return getProperty(prop, fallback, Long.class);
	}

	/**
	 * Requests a Float value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
	 * @return the value of the specified value or fallback.
	 */
	public Float getInteger(String prop, Float fallback) {
		return getProperty(prop, fallback, Float.class);
	}

	/**
	 * Requests a Double value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
	 * @return the value of the specified value or fallback.
	 */
	public Double getInteger(String prop, Double fallback) {
		return getProperty(prop, fallback, Double.class);
	}

	/**
	 * Requests a Double value from the specified setting.
	 *
	 * @param prop the specified property
	 * @param fallback the fallback which should be returned when the setting is not found.
	 * @return the value of the specified value or fallback.
	 */
	public Boolean getBoolean(String prop, Boolean fallback) {
		return getProperty(prop, fallback, Boolean.class);
	}

	/**
	 * Requests a Double value from the specified setting.
	 *
	 * @param prop the specified property
	 * @return the value of the specified value or fallback.
	 */
	public Boolean getBoolean(String prop) {
		return getBoolean(prop, false);
	}

	/******************************************************************************
	 * Setter Wrappers
	 *****************************************************************************/

	/**
	 * Define a settings value for a specified settings key.
	 * @param settingsKey the key of the setting
	 * @param value the value which should be assign to
	 *              the specified settings key.
     */
	public void put(String settingsKey, Object value) {
		if (value == null) throw new NullPointerException();
		properties.put(settingsKey, value);
	}


	private <T> T getProperty(String prop, T fallback, Class<T> stringClass) {
		Object value = properties.getOrDefault(prop, fallback);

		if (stringClass.isInstance(value)) {
			return stringClass.cast(value);
		}

		return fallback;
	}

}
