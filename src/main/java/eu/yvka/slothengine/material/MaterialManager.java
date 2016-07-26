package eu.yvka.slothengine.material;

import eu.yvka.slothengine.engine.Engine;
import eu.yvka.slothengine.engine.EngineComponent;
import eu.yvka.slothengine.shader.Shader;
import eu.yvka.slothengine.shader.source.FileShaderSource;
import eu.yvka.slothengine.shader.source.ShaderSource;
import eu.yvka.slothengine.utils.NameAlreadyInUseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class MaterialManager implements EngineComponent {

	static private Logger Log = LoggerFactory.getLogger(MaterialManager.class);
	private boolean initialized;
	private ObservableMap<String, Material> materialRepository;

	@Override
	public void initialize() {
		Log.info("Initialize MaterialManager");
		materialRepository = FXCollections.observableHashMap();
		initialized = true;
	}

	@Override
	public void shutdown() {
		Log.info("Shutdown MaterialManager");
		materialRepository.clear();
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Register this material and make is accessible by
	 * <code>getMaterial</code>.
	 *
	 * @param material the Material to register.
	 * @throws NameAlreadyInUseException if the material name is already in use.
     */
	public void registerMaterial(Material material) throws NameAlreadyInUseException {
		String name = material.getMaterialName();
		if (materialRepository.containsKey(name)) {
			throw new NameAlreadyInUseException(name);
		}
		Log.info("Register Material '{}'", material.getMaterialName());
		materialRepository.put(name, material);
	}

	/**
	 * Remove and delete this material.
	 *
	 * @param material the material to removed
	 * @return true if the material was removed.
     */
	public boolean unregisterMaterial(Material material) {
		if (materialRepository.containsKey(material.getMaterialName())) {
			materialRepository.remove(material.getMaterialName());
			return true;
		}
		return false;
	}

	/**
	 * Returns a optional which contains the material with the
	 * specified name if such a material exists.
	 *
	 * @param name the name of the desired material.
	 * @return an optional which contains the material if such a material exists.
     */
	public Optional<Material> getMaterial(String name) {
		Material material = materialRepository.get(name);
		if (material == null) {
			return Optional.empty();
		}
		return Optional.of(material);
	}

	/**
	 * Checks if a material name is already in use.
	 * @return true when the name is already in use.
	 */
	public boolean isMaterialNameInUse(String name) {
		return materialRepository.containsKey(name);
	}

	public void renameMaterial(String oldName, String newMame) throws NameAlreadyInUseException {

		Material material;

		if (newMame.equals(oldName)) return; // Can be Ignored

		material = materialRepository.get(oldName);

		if (material == null) {
			throw new IllegalArgumentException("The specified Material '" + oldName + "' don't exists.");
		}

		if (isMaterialNameInUse(newMame)) {
			throw new NameAlreadyInUseException(newMame);
		}
		material.setMaterialName(newMame);
		materialRepository.remove(oldName);
		materialRepository.put(newMame, material);

		Engine.runWhenReady(() -> {
			Shader shader = material.getShader();
			if (shader != null) {
				for (ShaderSource source : shader.getShaderSources()) {
					if (source instanceof FileShaderSource) {
						FileShaderSource fileShaderSource = (FileShaderSource) source;
						File file = fileShaderSource.getFile();
						File newFileName = new File(file.getParent(), newMame + "." + source.getType().getExtension());
						file.renameTo(newFileName);
						fileShaderSource.setFile(file);

					}
				}
				shader.enableUpdateRequired();
			}
		});

		Log.info("rename material '{}' => '{}'", oldName, newMame);
	}

	public ObservableMap<String, Material> getMaterial() {
		return materialRepository;
	}

}
