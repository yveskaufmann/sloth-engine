package sandbox;

import org.junit.Test;
import util.NativeLibraryLoader;

import static org.junit.Assert.*;

/**
 * Test class for the native library load
 */
public class NativeLibraryLoaderTest {

	@Test
	public void NativeLibrary_load_JavaLibPathIsSet() throws Exception {
		NativeLibraryLoader.load();

		String javaLibraryPath = System.getProperty(NativeLibraryLoader.LIBRARY_PATH_VARIABLE);
		assertTrue(
			"Expected : " + NativeLibraryLoader.LIBRARY_PATH + " inside " + NativeLibraryLoader.LIBRARY_PATH_VARIABLE +
			"\nActual  : " + javaLibraryPath
			,
			javaLibraryPath.contains(";" + NativeLibraryLoader.LIBRARY_PATH)
		);
	}
}
