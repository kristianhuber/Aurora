package engine.rendering.models;

/**
 * @Description: Holds basic information about a model
 * 
 */

public class RawModel {

	private int vertexCount, vaoID;

	// This is the array that determines where in the index array to render the
	// model based on a given distance.
	int[][] lodInfo = null;
	private boolean hasMultipleLevelsOfDetail = false;
	
	private ModelData modelData = null;

	/* Constructor Method */
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public ModelData getModelData() {
		return modelData;
	}

	public void setModelData(ModelData modelData) {
		this.modelData = modelData;
	}

	public void setLODInfo(int[][] lodInfo) {
		this.lodInfo = lodInfo;
		hasMultipleLevelsOfDetail = lodInfo.length > 1;
	}

	public boolean hasLevelsOfDetail() {
		return hasMultipleLevelsOfDetail;
	}

	/**
	 * Used at render time for each entity to determine the which level of detail to
	 * render.
	 * 
	 * @param distance
	 *            - the distance between the entity to render and the camera
	 * @return an array of integers where the first argument is the start of the
	 *         index array to use and the second is the length of the index array to
	 *         use. If the raw model does not contain a level of detail to render,
	 *         it returns an array of two zeros.
	 */
	public int[] getIndexArrayStartAndLength(float distance) {
		for (int i = 0; i < lodInfo.length; i++) {
			if (lodInfo[i][3] < 0) {
				// If the large bound is negative, the distance only has to be greater than the
				// small bound.
				if (distance >= Math.pow(lodInfo[i][2], 2))
					return new int[] { lodInfo[i][4], lodInfo[i][5] };
			} else {
				// Otherwise, the distance has to be between the smaller and greater bounds.
				if (distance >= Math.pow(lodInfo[i][2], 2) && distance < Math.pow(lodInfo[i][3], 2))
					return new int[] { lodInfo[i][4], lodInfo[i][5] };
			}
		}

		return new int[] { 0, 0 };
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
