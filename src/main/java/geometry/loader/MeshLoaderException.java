package geometry.loader;

public class MeshLoaderException extends RuntimeException {
	public MeshLoaderException(String message) {
		super(message);
	}

	public MeshLoaderException(String message, Object ... args) {
		super(String.format(message, args));
	}
}
