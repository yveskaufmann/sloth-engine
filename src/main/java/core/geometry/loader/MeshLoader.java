package core.geometry.loader;

import core.geometry.Mesh;

import java.io.InputStream;

public interface MeshLoader {

	boolean canHandle(String extension);
	Mesh load(InputStream in);
}
