package engine.animation.loaders;

public class SkeletonData {
	public final JointData headJoint;
	public final int jointCount;

	public SkeletonData(int jointCount, JointData headJoint) {
		this.jointCount = jointCount;
		this.headJoint = headJoint;
	}
}