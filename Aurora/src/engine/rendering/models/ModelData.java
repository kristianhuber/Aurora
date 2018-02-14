package engine.rendering.models;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * @Description: Holds VAO data for a model
 * 
 */

public class ModelData {

	private float[] vertices, textureCoords, normals;
	private int[] indices;

	private float furthestPoint;

	/* Constructor Method */
	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float furthestPoint) {
		this.furthestPoint = furthestPoint;
		this.textureCoords = textureCoords;
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
	}

	/**
	 * @param rotation
	 * @param scale
	 * @param transformationMatrix2 
	 * @return an array of 6 floats representing the boundaries of the AABB in this
	 *         order: xMin, yMin, zMin, xMax, yMax, zMax
	 */
	public float[] calculateAABB(Vector3f position, Matrix4f transformationMatrix) {
		// Creating a transformation matrix based on the rotation and scale, position
		// doesn't matter because it is axis aligned so the bounds can be translated
		// easily.

		float[] newBounds = new float[6];
		newBounds[0] = Float.MAX_VALUE;
		newBounds[1] = Float.MAX_VALUE;
		newBounds[2] = Float.MAX_VALUE;
		newBounds[3] = Float.MIN_VALUE;
		newBounds[4] = Float.MIN_VALUE;
		newBounds[5] = Float.MIN_VALUE;

		for (int i = 0; i < vertices.length / 3; i++) {
			Vector4f oldPoint = new Vector4f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2], 0f);
			Vector4f newPoint = new Vector4f();
			Matrix4f.transform(transformationMatrix, oldPoint, newPoint);

			if (newPoint.getX() < newBounds[0])
				newBounds[0] = newPoint.getX();
			else if (newPoint.getX() > newBounds[3])
				newBounds[3] = newPoint.getX();

			if (newPoint.getY() < newBounds[1])
				newBounds[1] = newPoint.getY();
			else if (newPoint.getY() > newBounds[4])
				newBounds[4] = newPoint.getY();

			if (newPoint.getZ() < newBounds[2])
				newBounds[2] = newPoint.getZ();
			else if (newPoint.getZ() > newBounds[5])
				newBounds[5] = newPoint.getZ();
		}
		
		newBounds[0] -= position.getX();
		newBounds[1] -= position.getY();
		newBounds[2] -= position.getZ();
		newBounds[3] -= position.getX();
		newBounds[4] -= position.getY();
		newBounds[5] -= position.getZ();
		
		return newBounds;
	}

	/* Returns the vertices array */
	public float[] getVertices() {
		return vertices;
	}

	/* Returns the texture coordinates array */
	public float[] getTextureCoords() {
		return textureCoords;
	}

	/* Returns the normals array */
	public float[] getNormals() {
		return normals;
	}

	/* Returns the indices array */
	public int[] getIndices() {
		return indices;
	}

	/* Returns the farthest point from the center of the model */
	public float getFurthestPoint() {
		return furthestPoint;
	}
}
