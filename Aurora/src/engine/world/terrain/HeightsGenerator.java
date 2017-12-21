package engine.world.terrain;

import java.util.Random;

import engine.world.World;

/**
 * @Description: This is exactly what was on tutorial 37, a.k.a Perlin Noise
 * 
 */

public class HeightsGenerator {
	// Octaves is how many passes to go through
	private int OCTAVES = 8;
	// Roughness is how bumpy the terrain should be. Lower values are smoother
	private float ROUGHNESS = 0.25f;

	// We change this anyway
	private float AMPLITUDE;

	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;

	// MAP_SIZE is depending on the vertex count
	private float MAP_SIZE;
	private float CENTER_OF_MAP;

	public HeightsGenerator() {
		this.seed = random.nextInt(1000000000);
	}

	// only works with POSITIVE gridX and gridZ values!
	public HeightsGenerator(int gridX, int gridZ, int vertexCount, int seed, float amplitude) {
		this.seed = seed;
		this.AMPLITUDE = 500;
		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);
		this.MAP_SIZE = World.WORLD_SIZE * vertexCount;
		this.CENTER_OF_MAP = MAP_SIZE / 2;
	}

	/* ampX is X - 1, ampZ is Z - 1, ampW is X - 1 and Z - 1 */
	public float generateHeight(int x, int z, float seaLevel) {
		float total = 0;
		
		float d = (float) Math.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i) / d);
			float amp;
			amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
		}

		total += AMPLITUDE / 2;

		float xDist = (Math.abs((xOffset + x) - CENTER_OF_MAP) / CENTER_OF_MAP);
		float zDist = (Math.abs((zOffset + z) - CENTER_OF_MAP) / CENTER_OF_MAP);
		float distance = (float) Math.sqrt(xDist * xDist + zDist * zDist);
		float addition = (float) (1 - 0.9f * Math.pow(distance, 2));
		if(distance < 0 && addition > 0) addition *= -1;
		total *= addition;
		
		/*float borderPercent = 0.25f;
		float xPos = xOffset + x;
		if (xPos < MAP_SIZE * borderPercent) {
			total *= (xPos / (MAP_SIZE * borderPercent));
		}
		if (xPos > MAP_SIZE * (1 - borderPercent)) {
			total *= ((MAP_SIZE - xPos) / (MAP_SIZE * borderPercent));
		}*/
		
		// Beach Stuff
		int tolerance = 5;
		if (total < seaLevel + tolerance && total > seaLevel - tolerance) {
			total = (total + seaLevel)/2;
		}

		return total;
	}

	private float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
				+ getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}

	private float getNoise(int x, int z) {
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	}

}
