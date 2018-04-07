package engine.animation.loaders;

public class AnimatedModelData {
	
	public SkeletonData skeletonData;
	public MeshData meshData;
	
	public AnimatedModelData(MeshData data, SkeletonData skeletonData){
		this.meshData = data;
		this.skeletonData = skeletonData;
	}
}
