package engine.rendering.models;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import engine.rendering.textures.TextureManager;

/**
 * @Description: Holds all of the models and their data
 * 
 */

public class ModelManager {
	private static HashMap<String, RawModel> models = new HashMap<String, RawModel>();
	private static List<Integer> VAOs = new ArrayList<Integer>();
	private static List<Integer> VBOs = new ArrayList<Integer>();

	/**
	 * Loads a texture and raw model with the given id from res/texturedModels
	 * 
	 * @param ID
	 *            - the ID of the model and texture to load.
	 */
	public static void loadTexturedModel(String ID) {
		ModelManager.loadEntityModel(ID);
		TextureManager.loadEntityTexture(ID);
	}

	/* Loads a model given the model ID */
	public static void loadModel(String ID) {

		// Loads the data from the OBJ file
		ModelData data = OBJLoader.loadOBJ(ID);
		RawModel model = ModelManager.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(),
				data.getIndices());

		// Stores it in the engine
		ModelManager.models.put(ID, model);
	}

	// Loads a model from a level of Detail file.
	public static void loadEntityModel(String ID) {
		try {
			RawModel model = OBJLoader.loadRawModel(ID);
			ModelManager.models.put(ID, model);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/* Loads a model given a string and vertices */
	public static void loadModel(String ID, float[] verts) {
		ModelManager.models.put(ID, loadToVAO(verts, 2));
	}

	/* Returns a model in the engine */
	public static RawModel getModel(String ID) {
		return ModelManager.models.get(ID);
	}

	/* Creates a VBO */
	public static int createEmptyVBO(int floatCount) {

		// Generate an ID and add it to the VBO list
		int vbo = GL15.glGenBuffers();
		ModelManager.VBOs.add(vbo);

		// Set how long the VBO is going to be
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		return vbo;
	}

	/* Updates data in a VBO */
	public static void updateVBO(int vbo, float[] data, FloatBuffer buffer) {

		// Resets a VBO with the data
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		// Reload the buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/* Loads a VAO and returns the rawmodel with the data in it */
	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {

		// Creates a new VAO and loads it
		int vaoID = createVAO();
		ModelManager.bindIndicesBuffer(indices);

		// Stores these attributes in the VAO
		ModelManager.storeDataInAttributeList(0, 3, positions);
		ModelManager.storeDataInAttributeList(1, 2, textureCoords);
		ModelManager.storeDataInAttributeList(2, 3, normals);

		// Unloads the VAO
		ModelManager.unbindVAO();

		return new RawModel(vaoID, indices.length);
	}

	public static RawModel loadToVAO(float[] vertices, float[] normals, float[] textures, int[] indices,
			List<String> lodData) {
		RawModel model = loadToVAO(vertices, textures, normals, indices);

		int[][] lodDataArray = new int[lodData.size()][6];
		for (int n = 0; n < lodData.size(); n++) {
			String[] data = lodData.get(n).split("\\,");
			int[] intData = new int[data.length];
			for (int i = 0; i < intData.length; i++)
				intData[i] = Integer.parseInt(data[i]);
			lodDataArray[n] = intData;
		}
		model.setLODInfo(lodDataArray);

		return model;
	}

	/* Loads a VAO and returns the ID for it, used in GUIs */
	public static int loadToVAO(float[] positions, float[] textureCoords) {

		// Creates a VAO, stores data in it, and then unbinds
		int vaoID = createVAO();
		ModelManager.storeDataInAttributeList(0, 2, positions);
		ModelManager.storeDataInAttributeList(1, 2, textureCoords);
		ModelManager.unbindVAO();

		return vaoID;
	}

	/* Loads a simple VAO with a certain number of dimensions */
	public static RawModel loadToVAO(float[] positions, int dimensions) {

		// Creates a VAO, loads the data, then unbinds
		int vaoID = createVAO();
		ModelManager.storeDataInAttributeList(0, dimensions, positions);
		ModelManager.unbindVAO();

		return new RawModel(vaoID, positions.length / dimensions);
	}

	/* Creates a VAO ID */
	private static int createVAO() {

		// Creates a new VAO, registers it, then binds it
		int vaoID = GL30.glGenVertexArrays();
		VAOs.add(vaoID);
		GL30.glBindVertexArray(vaoID);

		return vaoID;
	}

	/* Removes a VAO, should only be used for text */
	public static void removeVAO(int id) {
		VAOs.remove(new Integer(id));
		GL30.glDeleteVertexArrays(id);
	}

	/* Stores data in a float array buffer */
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {

		// Creates a VBO and loads it
		int vboID = GL15.glGenBuffers();
		VBOs.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		// Creates the buffer with data in it
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		// Unbinds the buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/* Stores float data in a buffer */
	private static FloatBuffer storeDataInFloatBuffer(float[] data) {

		// Creates a buffer and then sets it
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	/* Stores integer data in a VBO */
	private static void bindIndicesBuffer(int[] indices) {

		// Creates a VBO and binds it
		int vboID = GL15.glGenBuffers();
		VBOs.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);

		// Stores the data in a buffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	/* Stores integer data in a buffer */
	private static IntBuffer storeDataInIntBuffer(int[] data) {

		// Creates a buffer and then sets it
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	/* Add an an attribute to things that are instanced rendered, like particles */
	public static void addInstancedAtrribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {

		// Bind the buffer and VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);

		// Load the data to the buffer
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);

		// Unbind the buffer and VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	/* Unbinds a VAO */
	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	/* Cleans up the computers memory by deleting all of the VAOs and VBOs */
	public static void cleanUp() {
		for (int vao : VAOs) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : VBOs) {
			GL15.glDeleteBuffers(vbo);
		}
	}
}