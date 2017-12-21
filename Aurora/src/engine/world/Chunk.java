package engine.world;

import engine.world.terrain.Terrain;
import engine.world.water.WaterTile;

public class Chunk {

	private WaterTile water;
	private Terrain terrain;
	private int x, z;
	
	public Chunk(int seed, int x, int z, float seaLevel) {
		terrain = new Terrain(x, 0, z, "default", seed, seaLevel);
		water = new WaterTile(x, seaLevel, z);
		
		this.x = x;
		this.z = z;
	}
	
	public Chunk(int x, int z) {
		terrain = new Terrain(x, 0, z, "default");
		
		this.x = x;
		this.z = z;
	}
	
	public WaterTile getWater() {
		return water;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
}
