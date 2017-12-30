package engine.rendering.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(OBJLoader.class.getResourceAsStream("/aurora/assets/models/" + ID + ".obj")));

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

	// Returns the raw model of the given entity ID.
	public static RawModel loadRawModel(String ID) throws FileNotFoundException {
		System.out.print("[INFO] Loading Model: " + ID + " Result: ");
		File configFile = new File("textured models\\" + ID + "\\config.txt");
		Scanner sc = new Scanner(configFile);
		String data = "";
		while (sc.hasNextLine())
			data += sc.nextLine() + "|";

		// getting level of Detail number:
		String[] dataArray = data.split("\\|");
		List<String> levelOfDetailInfo = new ArrayList<String>();
		for (int i = 0; i < dataArray.length; i++)
			if (dataArray[i].length() >= 2 && dataArray[i].substring(0, 2).equals("c:"))
				levelOfDetailInfo.add(dataArray[i].substring(2));
			else if (!levelOfDetailInfo.isEmpty())
				break;

		String verticesString = "";
		String normalsString = "";
		String texturesString = "";
		List<String[]> indicesStrings = new ArrayList<String[]>();

		for (int i = 0; i < dataArray.length; i++)
			if (dataArray[i].length() >= 2)
				if (dataArray[i].substring(0, 2).equals("v:"))
					verticesString = dataArray[i].substring(2);
				else if (dataArray[i].substring(0, 2).equals("n:"))
					normalsString = dataArray[i].substring(2);
				else if (dataArray[i].substring(0, 2).equals("t:"))
					texturesString = dataArray[i].substring(2);
				else if (dataArray[i].substring(0, 2).equals("i:"))
					indicesStrings.add(dataArray[i].substring(2).split("\\,"));

		// checking to make sure the number of level of details is correct.
		if (levelOfDetailInfo.size() != indicesStrings.size()) {
			System.out.println("[ERROR] config file incorrect!");
			return null;
		}

		// Constructing the vertex, normals, and texture arrays.
		String[] verticesStringArray = verticesString.split("\\,");
		String[] normalsStringArray = normalsString.split("\\,");
		String[] texturesStringArray = texturesString.split("\\,");
		float[] vertices = new float[verticesStringArray.length];
		float[] normals = new float[normalsStringArray.length];
		float[] textures = new float[texturesStringArray.length];
		for (int i = 0; i < verticesStringArray.length / 3; i++) {
			vertices[i * 3] = Float.parseFloat(verticesStringArray[i * 3]);
			vertices[i * 3 + 1] = Float.parseFloat(verticesStringArray[i * 3 + 1]);
			vertices[i * 3 + 2] = Float.parseFloat(verticesStringArray[i * 3 + 2]);
			normals[i * 3] = Float.parseFloat(normalsStringArray[i * 3]);
			normals[i * 3 + 1] = Float.parseFloat(normalsStringArray[i * 3 + 1]);
			normals[i * 3 + 2] = Float.parseFloat(normalsStringArray[i * 3 + 2]);
			textures[i * 2] = Float.parseFloat(texturesStringArray[i * 2]);
			textures[i * 2 + 1] = Float.parseFloat(texturesStringArray[i * 2 + 1]);
		}

		// Getting the farthest length:
		float farthestLength = 0;
		for (int i = 0; i < vertices.length / 3; i++) {
			float length = (float) (Math.pow(vertices[i * 3], 2) + Math.pow(vertices[i * 3 + 1], 2)
					+ Math.pow(vertices[i * 3 + 2], 2));
			if (length > farthestLength)
				farthestLength = length;
		}

		// Now the index array has to be constructed which is just going to be each
		// index array one after the other in one giant array. The starting indices and
		// lengths of each set of indices have to be stored in the level of detail data
		// strings.
		int indicesSize = 0;
		for (int i = 0; i < levelOfDetailInfo.size(); i++)
			indicesSize += indicesStrings.get(i).length;

		String[] indicesStringArray = new String[0];
		for (int i = 0; i < levelOfDetailInfo.size(); i++) {
			int startingIndex = indicesStringArray.length;
			indicesStringArray = combineArrays(indicesStringArray, indicesStrings.get(i));
			int currentLODLength = indicesStringArray.length - startingIndex;
			levelOfDetailInfo.set(i, levelOfDetailInfo.get(i) + "," + startingIndex + "," + currentLODLength);
		}

		int[] indices = new int[indicesStringArray.length];
		for (int i = 0; i < indices.length; i++)
			indices[i] = Integer.parseInt(indicesStringArray[i]);

		System.out.println("SUCCESS");
		return ModelManager.loadToVAO(vertices, normals, textures, indices);
	}

	private static String[] combineArrays(String[] a, String[] b) {
		String[] newArr = new String[a.length + b.length];
		for (int i = 0; i < a.length; i++)
			newArr[i] = a[i];
		for (int i = a.length; i < a.length + b.length; i++)
			newArr[i] = b[i - a.length];
		return newArr;
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