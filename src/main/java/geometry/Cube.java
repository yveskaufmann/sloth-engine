package geometry;

import utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Cube extends Mesh {

	public Cube() {
		setupData();
	}

	private void setupData() {
		/***
		 *  A---D    A---D      D
		 *	|  /|    |  /      /|
		 *	| / | -> | /  +   / |
		 *	|/  |    |/      /  |
		 *	B---C    B      B---C
		 *
		 *    A'--D'
		 *   /|  /|
		 *  A---D |
		 *	| / | C'
		 *	|/  |/
		 *	B---C
		 *
		 */
		FloatBuffer interleavedBuffer = BufferUtils.createBuffer(new float[] {

			-1.0f,  1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // Front Top Left     = A
			-1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // Front Bottom Left	= B
			1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Front Bottom Right = C
			1.0f,  1.0f, 1.0f, 1.0f, 1.0f, 0.0f,  // Front Top Right 	= D

			-1.0f,  1.0f, -1.0f, 1.0f, 0.0f, 0.0f, // Back Top Left     = A'
			-1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // Back Bottom Left	= B'
			1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, // Back Bottom Right  = C'
			1.0f,  1.0f, -1.0f, 1.0f, 1.0f, 0.0f  // Back Top Right 	= D'
		});

		final int topLeft = 0;
		final int bottomLeft = 1;
		final int bottomRight = 2;
		final int topRight = 3;
		final int topLeftBack = 4;
		final int bottomLeftBack = 5;
		final int bottomRightBack = 6;
		final int topRightBack = 7;

		IntBuffer indicesBuffer = BufferUtils.createBuffer(new int[] {
			/**
			 * Front Face
			*/
			topRight, topLeft, bottomLeft,
			bottomLeft, bottomRight, topRight,

			/**
			 * Back Face
			*/
			bottomLeftBack, topLeftBack, topRightBack,
			topRightBack, bottomRightBack, bottomLeftBack,

			/**
			 * Top Face
			*/
			topRightBack, topLeftBack, topLeft,
			topLeft, topRight, topRightBack,

			/**
			 * Bottom Face
			*/

			bottomLeft, bottomLeftBack, bottomRightBack,
			bottomRightBack, bottomRight, bottomLeft,

			/**
			 * Left Face
			*/
			bottomLeft, topLeft, topLeftBack,
			topLeftBack, bottomLeftBack, bottomLeft,

			/**
			 * Right Face
			*/
			topRightBack, topRight, bottomRight,
			bottomRight, bottomRightBack, topRightBack
		});


		setMode(Mesh.Mode.TRIANGLES);
		setBuffer(VertexBuffer.Type.Interleaved, 3, interleavedBuffer);
		setBuffer(VertexBuffer.Type.Index, 3, indicesBuffer);

		setBuffer(VertexBuffer.Type.Vertex, 3, interleavedBuffer);
		setBuffer(VertexBuffer.Type.Color, 3, interleavedBuffer);

		getBuffer(VertexBuffer.Type.Vertex).getPointer().setStride(6 * 4);
		getBuffer(VertexBuffer.Type.Vertex).getPointer().setOffset(0);
		getBuffer(VertexBuffer.Type.Color).getPointer().setStride(6 * 4);
		getBuffer(VertexBuffer.Type.Color).getPointer().setOffset(3 * 4);
	}
}
