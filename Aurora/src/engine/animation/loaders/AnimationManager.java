package engine.animation.loaders;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.animation.animatedModel.AnimatedModel;
import engine.animation.animatedModel.Joint;
import engine.animation.animation.Animation;
import engine.animation.animation.JointTransform;
import engine.animation.animation.KeyFrame;
import engine.animation.animation.Quaternion;
import engine.rendering.models.ModelManager;

public class AnimationManager extends ModelManager {
	private static HashMap<String, AnimatedModel> animatedModels = new HashMap<String, AnimatedModel>();
	private static HashMap<String, Animation> animations = new HashMap<String, Animation>();
	
	public static AnimatedModel getAnimatedModel(String ID) {
		return animatedModels.get(ID);
	}
	
	public static Animation getAnimation(String ID){
		return animations.get(ID);
	}

	public static void loadAnimatedModel(String ID, String texture) {
		AnimatedModelData eData = AnimationImporter.loadModelData(ID);

		MeshData data = eData.meshData;

		int vaoID = createVAO();
		ModelManager.bindIndicesBuffer(data.indices);

		ModelManager.storeDataInAttributeList(0, 3, data.vertices);
		ModelManager.storeDataInAttributeList(1, 2, data.textureCoords);
		ModelManager.storeDataInAttributeList(2, 3, data.normals);
		AnimationManager.storeDataInAttributeList(3, 3, data.jointIds);
		ModelManager.storeDataInAttributeList(4, 3, data.vertexWeights);

		GL30.glBindVertexArray(0);

		SkeletonData skeletonData = eData.skeletonData;
		Joint headJoint = createJoints(skeletonData.headJoint);

		animatedModels.put(ID, new AnimatedModel(vaoID,
				eData.meshData.indices.length, texture, headJoint,
				skeletonData.jointCount));
	}

	public static void loadAnimation(String ID) {
		AnimationData animationData = AnimationImporter.loadAnimation(ID);

		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}

		animations.put(ID, new Animation(animationData.lengthSeconds, frames));
	}

	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId,
				data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<String, JointTransform>();
		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}

	private static JointTransform createTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
		Quaternion rotation = Quaternion.fromMatrix(mat);
		return new JointTransform(translation, rotation);
	}

	private static void storeDataInAttributeList(int attributeNumber,
			int coordinateSize, int[] data) {

		int vboID = GL15.glGenBuffers();
		VBOs.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attributeNumber, coordinateSize,
				GL11.GL_INT, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
}
