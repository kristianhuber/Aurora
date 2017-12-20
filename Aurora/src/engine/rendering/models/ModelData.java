package engine.rendering.models;

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
