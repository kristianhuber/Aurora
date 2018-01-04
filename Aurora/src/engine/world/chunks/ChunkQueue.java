package engine.world.chunks;

import java.util.ArrayList;

public class ChunkQueue {

	ArrayList<ChunkData> chunksToProcess;

	public ChunkQueue() {
		chunksToProcess = new ArrayList<ChunkData>();
	}

	public void addChunk(ChunkData data) {
		int dist = data.getDistance();
		if(dist > chunksToProcess.size()) {
			chunksToProcess.add(data);
		}else {
			chunksToProcess.add(dist, data);	
		}
	}

	public boolean isEmpty() {
		return chunksToProcess.isEmpty();
	}
	
	public ChunkData next() {
		if (!chunksToProcess.isEmpty()) {
			ChunkData data = chunksToProcess.get(0);
			chunksToProcess.remove(0);
			return data;
		}
		return null;
	}
}
