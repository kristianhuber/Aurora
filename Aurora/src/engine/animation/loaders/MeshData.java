package engine.animation.loaders;

public class MeshData {
	private static final int DIMENSIONS = 3;

	public float[] vertices, textureCoords, normals, vertexWeights;
	public int[] indices, jointIds;

	public MeshData(float[] v, float[] t, float[] n, int[] i, int[] j,
			float[] w) {
		this.vertices = v;
		this.textureCoords = t;
		this.normals = n;
		this.indices = i;
		this.jointIds = j;
		this.vertexWeights = w;
	}

	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}
}
