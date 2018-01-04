package engine.world.chunks;

import org.lwjgl.opengl.Display;

import engine.world.World;

/**
 * @author Kristian
 * 
 *         New thread to load chunks so that the renderer doesn't have to wait
 *         to generate stuff
 */

public class LoadChunks extends Thread {

	private boolean[][] alreadyCreated;
	private ChunkQueue toCreate;
	private World world;

	/**
	 * Constructor Method
	 * 
	 * @param worldSize
	 *            - an integer representing length and width of world in chunks
	 */
	public LoadChunks(World world, int worldSize) {

		// This will keep track of the chunks it already created
		alreadyCreated = new boolean[worldSize][worldSize];

		// This will keep track of the chunks it needs to create
		toCreate = new ChunkQueue();

		// This is to add the chunks back in
		this.world = world;
	}

	/**
	 * Adds a chunk to be rendered
	 * 
	 */
	public void createChunk(ChunkData data) {
		int x = data.getX();
		int z = data.getZ();

		if (!alreadyCreated[x][z]) {
			toCreate.addChunk(data);
			alreadyCreated[x][z] = true;
		}
	}

	@Override
	public void run() {
		
		//Runs until the window is closed (keep in mind even running in the background)
		while (Display.isCloseRequested()) {

			//Avoids errors by making sure a chunk is there
			if (!toCreate.isEmpty()) {
				ChunkData data = toCreate.next();
				if(data.getSeaLevel() == -999) {
					world.addChunk(new Chunk(data.getX(), data.getZ()));
				}else {
					world.addChunk(new Chunk(data.getSeed(), data.getX(), data.getZ(), data.getSeaLevel()));	
				}
			}
		}
	}
}
