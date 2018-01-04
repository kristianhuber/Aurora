package engine.world.chunks;

public class ChunkData {

	private float seaLevel;
	private int seed, x, z;
	
	public ChunkData(int seed, int x, int z, float seaLevel) {
		this.seaLevel = seaLevel;
		this.seed = seed;
		this.x = x;
		this.z = z;
	}

	public float getSeaLevel() {
		return seaLevel;
	}

	public int getSeed() {
		return seed;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return (seed + ", (" + x + ", " + z + "), " + seaLevel);
	}
}
