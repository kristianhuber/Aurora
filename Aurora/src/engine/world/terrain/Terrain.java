package engine.world.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.models.ModelData;
import engine.rendering.models.RawModel;
import engine.rendering.textures.TerrainTexturePack;
import engine.rendering.textures.TextureManager;
import engine.util.Calculator;

public class Terrain {

	public static final float SIZE = 128;
	public static final int AMPLITUDE = 255;

	private TerrainTexturePack texturePack;
	private RawModel model;
	private float x;
	private float y;
	private float z;

	private int VERTEX_COUNT = 32;
	private ModelData[] data;

	private float seaLevel;

	private float[][] heights;

	private float lowestHeight;

	public Terrain(int gridX, float y, int gridZ, String texturePack, int seed, float seaLevel) {
		data = new ModelData[(int) (Math.log(VERTEX_COUNT) / Math.log(2)) - 1];
		this.texturePack = TextureManager.getTerrainTexturePack(texturePack);
		this.x = gridX * SIZE;
		this.y = y;
		this.z = gridZ * SIZE;
		this.seaLevel = seaLevel;
		generateTerrain(seed);
	}

	public Terrain(int gridX, float y, int gridZ, String texturePack) {
		data = new ModelData[1];
		this.texturePack = TextureManager.getTerrainTexturePack(texturePack);
		this.x = gridX * SIZE;
		this.y = y;
		this.z = gridZ * SIZE;
		this.seaLevel = -1;
		generateTerrain();
	}

	private void generateTerrain() {
		int VERTEX_COUNT = 4;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = 0;
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = new Vector3f(0, 1, 0);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		this.data[0] = new ModelData(vertices, textureCoords, normals, indices, 0);
	}

	private void generateTerrain(int seed) {

		HeightsGenerator generator = new HeightsGenerator((int) (x / SIZE), (int) (z / SIZE), VERTEX_COUNT, seed,
				AMPLITUDE);
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];

		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

		float[] v16 = new float[count * 3 / 2];
		float[] n16 = new float[count * 3 / 2];
		float[] t16 = new float[count * 2 / 2];
		int[] i16 = new int[3 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

		int vertexPointer = 0;
		int pointer16 = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {

				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, generator);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;

				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;

				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);

				if (vertexPointer % 2 == 0) {
					v16[pointer16 * 3] = vertices[vertexPointer * 3];
					v16[pointer16 * 3 + 1] = vertices[vertexPointer * 3 + 1];
					v16[pointer16 * 3 + 2] = vertices[vertexPointer * 3 + 2];

					n16[pointer16 * 3] = normals[vertexPointer * 3];
					n16[pointer16 * 3 + 1] = normals[vertexPointer * 3 + 1];
					n16[pointer16 * 3 + 2] = normals[vertexPointer * 3 + 2];

					t16[pointer16 * 2] = textureCoords[vertexPointer * 2];
					t16[pointer16 * 2 + 1] = textureCoords[vertexPointer * 2 + 1];

					pointer16++;
				}

				vertexPointer++;
			}
		}

		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz += 2) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx += 2) {
				
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 2;
				int bottomLeft = ((gz + 2) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 2;

				i16[pointer++] = topLeft;
				i16[pointer++] = bottomLeft;
				i16[pointer++] = topRight;
				i16[pointer++] = topRight;
				i16[pointer++] = bottomLeft;
				i16[pointer++] = bottomRight;
			}
		}

		this.data[0] = new ModelData(vertices, textureCoords, normals, indices, 0);
		this.data[1] = new ModelData(v16, t16, n16, i16, 0);
	}

	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
		float heightL = getHeight(x - 1, z, generator);
		float heightR = getHeight(x + 1, z, generator);
		float heightD = getHeight(x, z - 1, generator);
		float heightU = getHeight(x, z + 1, generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();

		return normal;
	}

	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z, seaLevel);
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {

		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);

		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		float answer;
		if (xCoord <= (1 - zCoord)) {
			answer = Calculator.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Calculator.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return answer + y;
	}

	public ModelData getData() {
		return data[0];
	}

	public void setModel(RawModel model) {
		this.model = model;
	}

	public float getLowestHeight() {
		return lowestHeight;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public RawModel getModel() {
		return model;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
}
