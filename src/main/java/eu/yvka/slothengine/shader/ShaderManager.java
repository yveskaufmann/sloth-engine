package eu.yvka.slothengine.shader;

import eu.yvka.slothengine.engine.EngineComponent;
import eu.yvka.slothengine.shader.source.FileShaderSource;
import eu.yvka.slothengine.shader.source.ShaderSource;
import eu.yvka.slothengine.shader.source.StringShaderSource;
import eu.yvka.slothengine.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShaderManager implements EngineComponent {

	/******************************************************************************
	 *
	 * Fields
	 *
	 ******************************************************************************/

	private static final Logger Log = LoggerFactory.getLogger(ShaderManager.class);
	public final String ASSET_PATH = "assets/shaders/";
	private Map<String, Shader> shaderMap;
	private boolean initialized;

	/******************************************************************************
	 *
	 * Methods
	 *
	 ******************************************************************************/

	public Shader getShader(String shader) {
		return getShader(shader, shader, null);
	}

	public Shader getShader(String vertexShader, String fragmentShader) {
		return getShader(vertexShader, fragmentShader, null);
	}

	public Shader getShader(String vertexShader, String fragmentShader, String geometryShader) {
		Objects.requireNonNull(vertexShader, "A vertex shader is required");
		Objects.requireNonNull(fragmentShader, "A fragment shader is required");

		String id = vertexShader;
		// build id for different called shader sources
		boolean shaderSourceNamesAreNotEqual = !vertexShader.equals(fragmentShader) || (geometryShader != null && !geometryShader.equals(fragmentShader));
		if (shaderSourceNamesAreNotEqual) {
			id = vertexShader + "_" + fragmentShader;
			if (geometryShader != null) {
				id += "_" + geometryShader;
			}
		}

		Shader shader = shaderMap.get(id);
		if (shader == null) {

			File vertexSrc = new File(ASSET_PATH + toShaderFileName(vertexShader, ShaderType.VERTEX));
			File fragmentSrc = new File(ASSET_PATH + toShaderFileName(fragmentShader, ShaderType.FRAGMENT));
			File geometrySrc = null;
			if (geometryShader != null ) {
				geometrySrc = new File(ASSET_PATH + toShaderFileName(geometryShader, ShaderType.GEOMETRY));
			}

			shader = registerFileShader(vertexSrc, fragmentSrc, geometrySrc, id);
		}


		return shader;
	}

	public Shader registerFileShader(File vertexShader, File fragmentShader, File geometryShader, String id) {

		if (shaderMap.containsKey(id)) {
			throw new IllegalArgumentException("Shader with the id " + id + " is already registered.");
		}

		Shader shader = new Shader(id);
		shader.addSource(new FileShaderSource(ShaderType.VERTEX, vertexShader));
		shader.addSource(new FileShaderSource(ShaderType.FRAGMENT, fragmentShader));

		if (geometryShader != null) {
			shader.addSource(new FileShaderSource(ShaderType.GEOMETRY, geometryShader));
		}

		shaderMap.put(id, shader);

		return shader;
	}


	public void registerShader(String id, InputStream vertexIn, InputStream fragmentIn) {
		registerShader(id, vertexIn, fragmentIn, null);
	}


	public void registerShader(String id, InputStream vertexIn, InputStream fragmentIn, InputStream geometryIn) {
		if (shaderMap.containsKey(id)) {
			throw new IllegalArgumentException("Shader with the id " + id + " is already registered.");
		}

		try {
			final Shader shader = new Shader(id);
			String vertexSource = IOUtils.toString(new InputStreamReader(vertexIn));
			shader.addSource(new StringShaderSource(toShaderFileName(id, ShaderType.VERTEX),ShaderType.VERTEX, vertexSource));

			String fragmentSource = IOUtils.toString(new InputStreamReader(fragmentIn));
			shader.addSource(new StringShaderSource(toShaderFileName(id, ShaderType.FRAGMENT),ShaderType.FRAGMENT, fragmentSource));

			if (geometryIn != null) {
				String geometrySource = IOUtils.toString(new InputStreamReader(geometryIn));
				shader.addSource(new StringShaderSource(toShaderFileName(id, ShaderType.GEOMETRY), ShaderType.GEOMETRY, geometrySource));
			}

			shaderMap.put(id, shader);

		} catch (IOException e) {
			throw new RuntimeException("Failed to register sources", e);
		} finally {
			IOUtils.closeQuietly(vertexIn);
			IOUtils.closeQuietly(fragmentIn);
			IOUtils.closeQuietly(geometryIn);
		}
	}

	public ShaderSource getSource(ShaderType type, String name) {
		FileShaderSource fileShaderSource = new FileShaderSource(type,
			new File(ASSET_PATH + name + "." + type.getExtension()));

		return fileShaderSource;
	}


	/**
	 * Creates {@line File} instance which points to a shader file
	 * of the specified shader type and with the specified name.
	 * <p>
	 * The resulting file instance points to a file with
	 * the following path:
	 * <p>
 *
 *     <pre>
	 *	ShaderFile = [baseFolder]/[name].[typeExtension]
	 * </pre>
	 *
	 * @param baseFolder the base folder of the shader file
	 * @param name the name of the shader file
	 * @param type the type of the shader file
     * @return the file instance which points to a shader file
     */
	public File toShaderFile(File baseFolder, String name, ShaderType type) {
		return new File(baseFolder, toShaderFileName(name, type));
	}

	/**
	 * Build a shader file name by a specified  shader name and type.
	 *
	 * @param name the name of the shader
	 * @param type the type of the shader
     * @return the shader file name of the specified shader file
     */
	public String toShaderFileName(String name, ShaderType type) {
		return String.join(".", name, type.getExtension());
	}

	/******************************************************************************
	 *
	 * Engine Components Implementation
	 *
	 ******************************************************************************/

	@Override
	public void initialize() {
		if (shaderMap != null) {
			shutdown();
		}
		shaderMap = new HashMap<>();
		try {
			registerShader("Fallback", Shader.class.getResource("Fallback.vert").openStream(), Shader.class.getResource("Fallback.frag").openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		initialized = true;
	}

	@Override
	public void shutdown() {
		// Already performed from the render clean up
		initialized = false;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}
}
