package engine.world.chunks;

import engine.util.Engine;
import engine.world.World;

/**
 * @author Kristian
 * 
 *         New thread to load chunks so that the renderer doesn't have to wait
 *         to generate stuff
 */

public class ChunkLoader extends Thread {

	private boolean[][] alreadyCreated;
	private ChunkQueue toCreate;
	private Engine engine;
	private World world;

	/**
	 * Constructor Method
	 * 
	 * @param worldSize
	 *            - an integer representing length and width of world in chunks
	 */
	public ChunkLoader(Engine engine, World world) {

		// This will keep track of the chunks it already created
		alreadyCreated = new boolean[World.WORLD_SIZE][World.WORLD_SIZE];

		// This will keep track of the chunks it needs to create
		toCreate = new ChunkQueue();

		// This is to add the chunks back in
		this.world = world;
		
		//The purpose of this is to handle multithreading
		this.engine = engine;
	}

	/**
	 * Adds a chunk to be rendered
	 * 
	 */
	public void createChunk(ChunkData data) {
		toCreate.addChunk(data);
	}

	@Override
	public void run() {

		// Runs until the window is closed (keep in mind even running in the background)
		while (engine.isRunning()) {
			try {
				Thread.sleep(10);
			}catch(Exception e) {
				System.err.println("[Console]: Thread Sleep Error");
			}
			
			// Avoids errors by making sure a chunk is there
			if (!toCreate.isEmpty()) {
				ChunkData data = toCreate.next();
				int x = data.getX();
				int z = data.getZ();
				if (!alreadyCreated[x][z]) {
					if (data.getSeaLevel() == -999) {
						world.addChunk(new Chunk(x, z));
					} else {
						world.addChunk(new Chunk(world, data.getSeed(), x, z, data.getSeaLevel()));
					}
					alreadyCreated[x][z] = true;
				}
			}
		}
	}
}
