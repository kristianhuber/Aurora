package engine.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.WaveData;

public class AudioManager {
	private static HashMap<String, Integer> buffers = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sources = new HashMap<String, Integer>();
	
	public static int getSound(String id) {
		return buffers.get(id);
	}
	
	public static void registerSource(AudioSource source, int id) {
		sources.put(source.toString(), new Integer(id));
	}
	
	public static void deleteSource(AudioSource source, int id) {
		source.stop();
		AL10.alDeleteSources(id);
		sources.remove(source.toString());
	}
	
	public static void initialize() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		AudioManager.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
	}
	
	public static void setListenerData(Vector3f pos) {
		AudioManager.setListenerData(pos.x, pos.y, pos.z);
	}
	
	public static void setListenerData(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static void loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		
		try {
			InputStream in = new FileInputStream("res\\audio\\" + file + ".wav");
			BufferedInputStream str = new BufferedInputStream(in);
			WaveData waveFile = WaveData.create(str);
			AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
			buffers.put(file, buffer);
			System.out.println("[Console]: Loaded sound '" + file + "'");
		} catch (FileNotFoundException e) {
			System.err.println("[Console]: Could not load sound '" + file + "'");
			e.printStackTrace();
		}
	}
	
	public static void cleanUp() {
		for(int buffer : buffers.values()) {
			AL10.alDeleteBuffers(buffer);
		}
		for(int source : sources.values()) {
			AL10.alDeleteSources(source);
		}
		AL.destroy();
	}
}
