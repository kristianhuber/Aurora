package engine.world.water;

import engine.world.terrain.Terrain;

/**
 * @Description: Holds basic information about each instance of a water tile
 * 
 */

public class WaterTile {

	public static final float SIZE = Terrain.SIZE;

	private float height, x, z;

	/* Constructor Method */
	public WaterTile(float x, float height, float z) {
		this.x = x * SIZE;
		this.z = z * SIZE;
		this.height = height;
	}

	/* Returns the height */
	public float getHeight() {
		return height;
	}

	/* Returns the x coordinate */
	public float getX() {
		return x;
	}

	/* Returns the z coordinate */
	public float getZ() {
		return z;
	}
}
