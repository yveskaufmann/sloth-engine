package geometry;

import geometry.loader.MeshLoader;
import geometry.loader.WaveFrontObjectLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO Document and implement
public class MeshManager {

	private static final String MODEL_DIRECTORY = "assets/models";
	private static final List<MeshLoader> loaders =  new ArrayList<>();
	static {
		loaders.add(new WaveFrontObjectLoader());
	}

	public Mesh loadMesh(String name) {
		MeshLoader loader = getLoaderByExtension(name);
		return loadMesh(name, loader);
	}

	public Mesh loadMesh(String name, MeshLoader loader) {
		String path = MODEL_DIRECTORY + "/" + name;
		try {
			try (FileInputStream in = new FileInputStream(path)) {
				return loader.load(in);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private MeshLoader getLoaderByExtension(String name) {
		String extension = "";

		// Obtain the extension of the model file
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex > 0) {
			extension = name.substring(dotIndex + 1);
		}

		for(MeshLoader loader : loaders) {
			if (loader.canHandle(extension)) {
				return loader;
			}
		}

		throw new UnsupportedOperationException("No loader supports the mesh format:" + extension);
	}

}
