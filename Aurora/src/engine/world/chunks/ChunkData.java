package engine.world.chunks;

public class ChunkData {

	private int seed, x, z, distance;
	private float seaLevel;
	
	public ChunkData(int distance, int seed, int x, int z, float seaLevel) {
		this.seaLevel = seaLevel;
		this.seed = seed;
		this.x = x;
		this.z = z;
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
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
