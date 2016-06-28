package eu.yvka.slothengine.geometry.loader;

import eu.yvka.slothengine.geometry.Mesh;

import java.io.InputStream;

public interface MeshLoader {

	boolean canHandle(String extension);
	Mesh load(InputStream in);
}
