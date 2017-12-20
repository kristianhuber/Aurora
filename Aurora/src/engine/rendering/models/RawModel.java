package engine.rendering.models;

/**
 * @Description: Holds basic information about a model
 * 
 */

public class RawModel {

	private int vertexCount, vaoID;

	/* Constructor Method */
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	/* Returns the VAO ID */
	public int getVaoID() {
		return vaoID;
	}

	/* Returns the vertex count */
	public int getVertexCount() {
		return vertexCount;
	}
}
