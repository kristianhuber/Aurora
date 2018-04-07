package engine.animation.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;

public class AnimationImporter {

	public static AnimatedModelData loadModelData(String name) {

		SkeletonData skeletonData = null;
		MeshData data = null;

		try {

			float[] vert = null;
			float[] texc = null;
			float[] norm = null;
			int[] indc = null;
			int[] jids = null;
			float[] vtxw = null;

			int jointCount = -1;
			JointData headJoint = null;

			File configFile = new File("res\\animation\\" + name + ".txt");
			BufferedReader reader = new BufferedReader(new FileReader(
					configFile));
			
			String variable = "";
			String line;
			while(!(line = reader.readLine()).equals("$headjoint")){
				if(line.startsWith("$")){
					variable = line;
				}else{
					switch (variable) {
					case "$vert":
						vert = convert(line.split(" "));
						break;
					case "$texc":
						texc = convert(line.split(" "));
						break;
					case "$norm":
						norm = convert(line.split(" "));
						break;
					case "$indc":
						indc = convertToInt(line.split(" "));
						break;
					case "$jids":
						jids = convertToInt(line.split(" "));
						break;
					case "$vtxw":
						vtxw = convert(line.split(" "));
						break;
					case "$jointcount":
						jointCount = Integer.parseInt(line);
						break;
					}
				}
			}
			
			headJoint = readJoint(reader);
			
			data = new MeshData(vert, texc, norm, indc, jids, vtxw);
			skeletonData = new SkeletonData(jointCount, headJoint);
			
			reader.close();
			
			return new AnimatedModelData(data, skeletonData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new AnimatedModelData(data, skeletonData);
	}

	private static JointData readJoint(BufferedReader reader) throws IOException{

		// Should be $index
		String line = reader.readLine();
		int index = Integer.parseInt(reader.readLine());

		// Should be $nameID
		line = reader.readLine();
		String name = reader.readLine();

		// Should be $bindLocalTransform
		line = reader.readLine();
		String[] raw = reader.readLine().split(" ");
		Matrix4f data = new Matrix4f();
		data.m00 = Float.parseFloat(raw[0]);
		data.m01 = Float.parseFloat(raw[1]);
		data.m02 = Float.parseFloat(raw[2]);
		data.m03 = Float.parseFloat(raw[3]);
		data.m10 = Float.parseFloat(raw[4]);
		data.m11 = Float.parseFloat(raw[5]);
		data.m12 = Float.parseFloat(raw[6]);
		data.m13 = Float.parseFloat(raw[7]);
		data.m20 = Float.parseFloat(raw[8]);
		data.m21 = Float.parseFloat(raw[9]);
		data.m22 = Float.parseFloat(raw[10]);
		data.m23 = Float.parseFloat(raw[11]);
		data.m30 = Float.parseFloat(raw[12]);
		data.m31 = Float.parseFloat(raw[13]);
		data.m32 = Float.parseFloat(raw[14]);
		data.m33 = Float.parseFloat(raw[15]);

		JointData jdata = new JointData(index, name, data);

		line = reader.readLine();
		while (!line.equals("$endchildren")) {
			if (line.equals("$child")) {
				JointData newJData = readJoint(reader);
				jdata.addChild(newJData);
			} else if (line.equals("$nochildren")) {
				break;
			}
			line = reader.readLine();
		}

		return jdata;
	}

	private static float[] convert(String[] vars) {
		float[] var = new float[vars.length];
		for (int i = 0; i < vars.length; i++) {
			var[i] = Float.parseFloat(vars[i]);
		}
		return var;
	}

	private static int[] convertToInt(String[] vars) {
		int[] var = new int[vars.length];
		for (int i = 0; i < vars.length; i++) {
			var[i] = Integer.parseInt(vars[i]);
		}
		return var;
	}

	public static AnimationData loadAnimation(String name) {
		
		try {
			
			AnimationData data = null;

			File configFile = new File("res\\animation\\" + name + ".txt");
			BufferedReader reader = new BufferedReader(new FileReader(
					configFile));
			
			reader.readLine();
			float animationLength = Float.parseFloat(reader.readLine());
			
			ArrayList<KeyFrameData> frames = new ArrayList<KeyFrameData>();
			
			reader.readLine();
			
			while(reader.ready()){
				
				//$time
				reader.readLine();
				float time = Float.parseFloat(reader.readLine());
				
				KeyFrameData data2 = new KeyFrameData(time);
				
				reader.readLine();
				
				String[] tempData1 = reader.readLine().split("/");
				
				for(String temp : tempData1){
					
					String[] parts = temp.split("&");
					
					Matrix4f matrixData = new Matrix4f();
					String[] inside = parts[1].split(" ");
					matrixData.m00 = Float.parseFloat(inside[0]);
					matrixData.m01 = Float.parseFloat(inside[1]);
					matrixData.m02 = Float.parseFloat(inside[2]);
					matrixData.m03 = Float.parseFloat(inside[3]);
					matrixData.m10 = Float.parseFloat(inside[4]);
					matrixData.m11 = Float.parseFloat(inside[5]);
					matrixData.m12 = Float.parseFloat(inside[6]);
					matrixData.m13 = Float.parseFloat(inside[7]);
					matrixData.m20 = Float.parseFloat(inside[8]);
					matrixData.m21 = Float.parseFloat(inside[9]);
					matrixData.m22 = Float.parseFloat(inside[10]);
					matrixData.m23 = Float.parseFloat(inside[11]);
					matrixData.m30 = Float.parseFloat(inside[12]);
					matrixData.m31 = Float.parseFloat(inside[13]);
					matrixData.m32 = Float.parseFloat(inside[14]);
					matrixData.m33 = Float.parseFloat(inside[15]);
					
					data2.addJointTransform(new JointTransformData(parts[0], matrixData));
				}
				
				frames.add(data2);
			}
			
			KeyFrameData[] array = new KeyFrameData[frames.size()];
			for(int i = 0; i < array.length; i++){
				array[i] = frames.get(i);
			}
			
			data = new AnimationData(animationLength, array);
			
			return data;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
