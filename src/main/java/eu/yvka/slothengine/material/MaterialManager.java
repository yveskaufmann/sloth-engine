package eu.yvka.slothengine.material;


import eu.yvka.slothengine.utils.NameAlreadyInUseException;
import eu.yvka.slothengine.engine.EngineComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MaterialManager implements EngineComponent {

	static private Logger Log = LoggerFactory.getLogger(MaterialManager.class);
	private boolean initialized;
	private Map<String, Material> materialRepository;

	@Override
	public void initialize() {
		Log.info("Initialize MaterialManager");
		Log.info("Prepare Material Repository");
		materialRepository = new HashMap<>();
		try {
			registerMaterial(new BasicMaterial());
		} catch (NameAlreadyInUseException e) {} // Will never happen
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


}
