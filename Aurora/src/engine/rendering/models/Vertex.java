package engine.rendering.models;

import org.lwjgl.util.vector.Vector3f;

/**
 * @Description: Holds information about a vertex
 * 
 */

public class Vertex {

	private static final int NO_INDEX = -1;

	private int textureIndex, normalIndex, index;
	private Vertex duplicateVertex;
	private Vector3f position;
	private float length;

	/* Constructor Method */
	public Vertex(int index, Vector3f position) {
		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		this.duplicateVertex = null;

		this.length = position.length();
		this.position = position;
		this.index = index;
	}

	/* Returns if the vertex has the same texture and normal */
	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	/* Returns true if the texture index or normal index is something */
	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	/* Returns the index */
	public int getIndex() {
		return index;
	}

	/* Returns the length of the vertex */
	public float getLength() {
		return length;
	}

	/* Sets the texture index at the vertex */
	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	/* Sets the normal index at the vertex */
	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	/* Returns the position of the vertex */
	public Vector3f getPosition() {
		return position;
	}

	/* Returns the texture index at the vertex */
	public int getTextureIndex() {
		return textureIndex;
	}

	/* Returns the normal index at the vertex */
	public int getNormalIndex() {
		return normalIndex;
	}

	/* Sets the duplicate vertex */
	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	/* Returns the duplicate vertex */
	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}
}
