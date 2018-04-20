package engine.world.chunks;

import org.lwjgl.util.vector.Vector3f;

import engine.world.World;
import engine.world.entities.Entity;
import engine.world.terrain.Terrain;
import engine.world.water.WaterTile;

public class Chunk {

	private World world;
	private WaterTile water;
	private Terrain terrain;
	private float seaLevel;
	private int x, z;
	
	public Chunk(World world, int seed, int x, int z, float seaLevel) {
		
		terrain = new Terrain(x, 0, z, "default", seed, seaLevel);
		if(terrain.getLowestHeight() <= seaLevel) {
			water = new WaterTile(x, seaLevel, z);	
		}
		
		this.seaLevel = seaLevel;
		this.world = world;
		this.x = x;
		this.z = z;
		
		this.decorate();
	}
	
	public void decorate() {
		for (int i = 0; i < 10; i++) {
			int x2 = (int) (Math.random() * Terrain.SIZE + x * Terrain.SIZE);
			int z2 = (int) (Math.random() * Terrain.SIZE + z * Terrain.SIZE);
			float y = terrain.getHeightOfTerrain(x2, z2);
			if (y > seaLevel + 5) {
				Entity e = new Entity(world, "betterpine", new Vector3f(x2, y, z2));
				e.setScale(7.5f);
				e.setSelection(false);
				world.addEntity(e);
			}
		}
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
