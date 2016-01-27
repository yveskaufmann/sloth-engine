package geometry.loader;

import geometry.Mesh;
import geometry.MeshManager;
import geometry.VertexBuffer;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class WaveFrontObjectLoader implements MeshLoader {

	private final Logger Log = LoggerFactory.getLogger(WaveFrontObjectLoader.class);

	private static final String[] SUPPROTED_EXTENSIONS = {"obj"};
	private static final char COMMENT_TAG = '#';
	private static final char VERTEX_TAG = 'v';
	private static final char NORMAL_TAG = 'n';
	private static final char UV_TAG = 't';
	private static final char FACE_TAG = 'f';
	private int indexSize;
	private int lineNumber;
	private boolean containsVerticies;
	private boolean containsUvs;
	private boolean containsNormals;

	@Override
	public boolean canHandle(String extension) {
		return SUPPROTED_EXTENSIONS[0].equals(extension);
	}

	public Mesh load(InputStream in) {
		List<Float> verticies = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Float> uvs = new ArrayList<>();
		List<Short>  index = new ArrayList<>();
		Mesh mesh = new Mesh();

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			parseFile(reader, verticies, normals, uvs, index);
			FloatBuffer interleaveBuffer = buildInterleavedBuffer(verticies, normals, uvs, index);
			ShortBuffer indexBuffer = buildIndexBuffer(index);

			mesh.setMode(Mesh.Mode.TRIANGLES);
			mesh.setBuffer(VertexBuffer.Type.Interleaved, 3, interleaveBuffer);
			mesh.setBuffer(VertexBuffer.Type.Vertex, 3, interleaveBuffer);
			mesh.setBuffer(VertexBuffer.Type.Normal, 3, interleaveBuffer);
			mesh.setBuffer(VertexBuffer.Type.TextCoords, 2, interleaveBuffer);
			mesh.setBuffer(VertexBuffer.Type.Index, 3, indexBuffer);

			/**
			 * Buffer Layout
			 *
			 * V1 V2 V3 N1 N2 N3 UV1 UV2
			 */
			int stride = 6 * 4 + 2 * 4;
			mesh.getBuffer(VertexBuffer.Type.Vertex).getPointer().setStride(stride).setOffset(0);
			mesh.getBuffer(VertexBuffer.Type.Normal).getPointer().setStride(stride).setOffset(3 * 4);
			mesh.getBuffer(VertexBuffer.Type.TextCoords).getPointer().setStride(stride).setOffset(6 * 4);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return mesh;
	}

	private ShortBuffer buildIndexBuffer(List<Short> index) {
		return  null;
	}

	private FloatBuffer buildInterleavedBuffer(List<Float> verticies, List<Float> normals, List<Float> uvs, List<Short> indexes) {
		int vertexAttributeCount, bytesPerVertex;

		bytesPerVertex = 0;
		vertexAttributeCount = 0;

		if (containsVerticies) {
			vertexAttributeCount++;
			bytesPerVertex += 4;
		}

		if (containsNormals) {
			vertexAttributeCount++;
			bytesPerVertex += 4;}

		if (containsNormals) {
			vertexAttributeCount++;
			bytesPerVertex += 2;
		}

		// Lets figure out what is here the problem.
		// The buffer has the following structure
		// v1 v2 v3 n1 n2 n3 uv1 uv2
		// .........................
		//
		// Vertices, Normals, UVs are obtained indexed
		// from the object file in the following format
		// {v1 : 1} {nv : 1} {uv : 1}
		// {v2 : 2} {nv : 2} {uv : 2}
		// {v3 : 3} {nv : 3} {uv : 3}
		// {v3 : 4} {nv : 5} {uv : 6}
		//    ...      ...      ...
		// The index is obtained as the illustration below:
		//
		// F1 = {
		// 			[V-Index1, N-Index1, UV-Index1]
		// 			[V-Index2, N-Index2, UV-Index2]
		// 			[V-Index3, N-Index3, UV-Index3]
		// 		}
		//      ...
		//
		// At this points are two options which will be described
		// in the following lines. The first option is to build
		// a single buffer which contains all the vertices, normals and
		// texture coordinates but each is stored only once therefore

		// TODO: BUffer größe ist totaler blödsinn :)
		int countOfVertices = indexes.size() / vertexAttributeCount;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(bytesPerVertex * countOfVertices);
		buffer.clear();
		// TODO: Bevor wir die Schleife betreten prüfe ob es über haupt faces gab
		// sonst droht eine Inv Loop
		for (int i = 0; i < indexes.size();) {
				// TODO: Die Iteration ist noch nicht korret, die Schleife
			    // fügt jeweils 3 Vektoren. Erforderlich ist aber das einfügen von 9 Vektoren.
				if (containsVerticies) {
					buffer.put(verticies.get(indexes.get(i)));
					buffer.put(verticies.get(indexes.get(i + 1)));
					buffer.put(verticies.get(indexes.get(i + 2)));
					i+=3;
				}

				if (containsNormals) {
					buffer.put(normals.get(indexes.get(i)));
					buffer.put(normals.get(indexes.get(i + 1)));
					buffer.put(normals.get(indexes.get(i + 2)));
					i+=3;
				}

				if (containsUvs) {
					buffer.put(uvs.get(indexes.get(i)));
					buffer.put(uvs.get(indexes.get(i + 1)));
					i+=2;
				}
		}

		return buffer;
	}

	private void parseFile(BufferedReader reader, List<Float> verticies, List<Float> normals, List<Float> uvs, List<Short> index) throws IOException {
		String line;
		char firstChar;
		char secondChar;
		String[] components;
		indexSize = -1;
		lineNumber = 0;
		containsVerticies = false;
		containsNormals = false;
		containsUvs = false;

		while((line = reader.readLine()) != null) {
			lineNumber++;
			int len = line.length();
			firstChar = len >= 1 ? line.charAt(0): ' ';
			secondChar = len >= 2 ? line.charAt(1):' ';

			switch (firstChar) {
				case ' ':
				case COMMENT_TAG:
					continue;
				case VERTEX_TAG:
					components = line.split("\\s+");
					switch (secondChar) {
						case NORMAL_TAG:
							parseComponents(3, components, normals);
							break;
						case UV_TAG:
							parseComponents(2, components, uvs);
							break;
						default:
							parseComponents(3, components, verticies);
					}
					break;
				case FACE_TAG:
					components = line.split("\\s");
					if (components.length == 4) {
						parseFaceIndex(components, index);
					} else {
						throw new MeshLoaderException("This Loader can parseFile faces only.");
					}
					break;
			}
		}
	}

	private void parseComponents(int count, String[] components, List<Float> writeTo) {
		// Offset +1 is required because components contains the tag too.
		if (components.length != count + 1) throw new MeshLoaderException("In Line %d are not enough components found", lineNumber);
		for (int i = 1; i < count; i++) {
			writeTo.add(Float.valueOf(components[i]));
		}
	}

	private void parseFaceIndex(String[] triangleComponents, List<Short> index) {

		for (int i = 1; i < triangleComponents.length; i++) {
			String triangleVertexIndex = triangleComponents[i];
			String []indexes = triangleVertexIndex.split("/");
			if (indexSize == -1) {
				indexSize = indexes.length;
				containsVerticies = indexSize > 0 && indexes[0].length() > 0;
				containsUvs = indexSize > 1 && indexes[1].length() > 0;
				containsNormals = indexSize > 2 && indexes[2].length() > 0;
			} else if (indexSize != indexes.length) {
				throw new MeshLoaderException("Found in consistent faces definition in line %d.", lineNumber);
			}

			for (int j = 0; j < indexes.length; j++) {
				if (indexes[j].length() == 0) continue;
				index.add((short) (Short.valueOf(indexes[j]) - 1));
			}
		}
	}

	public static void main(String[] args) {
		Mesh mesh = new MeshManager().loadMesh("teapot.obj");
	}

}
