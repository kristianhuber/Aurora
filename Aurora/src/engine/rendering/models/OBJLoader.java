package engine.rendering.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @Description: Loads OBJ files into the game
 * 
 */

public class OBJLoader {

	/* Loads an OBJ model given the file name */
	public static ModelData loadOBJ(String ID) {

		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();

		// Tries to load the file
		try {

			// Loads the file and starts reading
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					OBJLoader.class.getResourceAsStream("/aurora/assets/models/" + ID + ".obj")));

			while (true) {

				line = reader.readLine();

				if (line.startsWith("v ")) {

					// Vertices
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));

					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {

					// Texture Coordinates
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);

				} else if (line.startsWith("vn ")) {

					// Normals
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);

				} else if (line.startsWith("f ")) {

					// Read these separately
					break;
				}
			}

			// Faces
			while (line != null && line.startsWith("f ")) {

				// Read the line
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");

				// Load each vertex
				OBJLoader.processVertex(vertex1, vertices, indices);
				OBJLoader.processVertex(vertex2, vertices, indices);
				OBJLoader.processVertex(vertex3, vertices, indices);

				line = reader.readLine();
			}

			reader.close();

		} catch (IOException e) {
			// If it can't find the file, print out the ID
			System.err.println("[Console]: Could not load model '" + ID + "'");
		}

		// Removes any duplicate vertices
		OBJLoader.removeUnusedVertices(vertices);

		// Creates arrays for the different types of data
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);

		// Create an object with all the data in it and return it
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest);

		return data;
	}

	/* Creates a vertex object to process */
	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {

		// Loads the variables up
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;

		// Figures out if this is a new vertex or not and handles itF
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			OBJLoader.dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	/* Converts from ArrayList to array */
	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	/*  */
	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
			float[] verticesArray, float[] texturesArray, float[] normalsArray) {

		float furthestPoint = 0;

		// Loads all of the data into arrays
		for (int i = 0; i < vertices.size(); i++) {

			Vertex currentVertex = vertices.get(i);

			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}

			// Vectors with info at the current vertex
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());

			// Vertices
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;

			// Textures
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;

			// Normals
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	/* ??? */
	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
			List<Integer> indices, List<Vertex> vertices) {

		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();

			if (anotherVertex != null) {
				OBJLoader.dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices,
						vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());

				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);

				previousVertex.setDuplicateVertex(duplicateVertex);

				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}

	/* Removes vertices that are unused */
	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
}
