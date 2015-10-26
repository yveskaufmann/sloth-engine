package util;

import org.lwjgl.LWJGLUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * This class is intended to load the
 * native
 */
public class NativeLibraryLoader {
	public static final String LIBRARY_PATH_VARIABLE = "org.lwjgl.librarypath" ;
	public static final String LIBRARY_PATH = new File(System.getProperty("user.dir"), "lib").toString();

	/**
	 * Load the required native libraries which are required
	 * in order to use lwjgl classes.
	 */
	public static void load() {
		URL url = getURLtoLWJGLPlatformJar();

		extractJarToLibFolder(url);

		System.setProperty(LIBRARY_PATH_VARIABLE, LIBRARY_PATH + ";" + System.getProperty(LIBRARY_PATH_VARIABLE));
	}

	private static URL getURLtoLWJGLPlatformJar() {
		final String os = LWJGLUtil.getPlatformName();

		if (os == null) {
			throw new IllegalStateException("Unsupported operating system detected: " + System.getProperty("os.name"));
		}

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls =  ((URLClassLoader) cl).getURLs();
		return Stream.of(urls)
			.filter((url) -> url.toString().endsWith("-natives-" + os + ".jar"))
			.findFirst()
			.orElseThrow(()-> new IllegalStateException("Required lwjgl-platform jars are not provided by the classpath"));
	}

	private static void extractJarToLibFolder(URL url) {
		File libraryFolder = new File(LIBRARY_PATH);
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(new File(url.toURI()));
		} catch (Exception e) {
			e.printStackTrace(); // Will not happen
		}
		Iterator<JarEntry> it = Enumerations.asIterator(jarFile.entries());

		if (libraryFolder.isDirectory()) return;
		libraryFolder.mkdirs();

		while(it.hasNext()) {
			JarEntry entry = it.next();
			File dstFile = new File(libraryFolder, entry.getName());

			if (entry.isDirectory() && !dstFile.isDirectory()) {
				dstFile.mkdirs();
				continue;
			}

			try (BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(entry))) {
				Files.copy(in, dstFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
