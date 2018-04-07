package engine.animation.loaders;

import java.io.PrintWriter;

import org.lwjgl.util.vector.Matrix4f;

/**
 * This contains the data for a transformation of one joint, at a certain time
 * in an animation. It has the name of the joint that it refers to, and the
 * local transform of the joint in the pose position.
 * 
 * @author Karl
 *
 */
public class JointTransformData {

	public final String jointNameId;
	public final Matrix4f jointLocalTransform;

	public JointTransformData(String jointNameId, Matrix4f jointLocalTransform) {
		this.jointNameId = jointNameId;
		this.jointLocalTransform = jointLocalTransform;
	}
	
	public void printMe(PrintWriter w){
		w.println("$jointNameId");
		w.println(jointNameId);
		w.println("$jointLocalTransform");
		w.println(jointLocalTransform.m00 + " "
				+ jointLocalTransform.m01 + " " + jointLocalTransform.m02 + " "
				+ jointLocalTransform.m03 + " " + jointLocalTransform.m10 + " "
				+ jointLocalTransform.m11 + " " + jointLocalTransform.m12 + " "
				+ jointLocalTransform.m13 + " " + jointLocalTransform.m20 + " "
				+ jointLocalTransform.m21 + " " + jointLocalTransform.m22 + " "
				+ jointLocalTransform.m23 + " " + jointLocalTransform.m30 + " "
				+ jointLocalTransform.m31 + " " + jointLocalTransform.m32 + " "
				+ jointLocalTransform.m33);
	}
}
