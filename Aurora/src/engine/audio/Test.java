package engine.audio;

import org.lwjgl.openal.AL10;

public class Test {

	public static void main(String[] args) throws Exception {
		AudioManager.initialize();
		AudioManager.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioManager.loadSound("audio/birds006.wav");
		int buffer2 = AudioManager.loadSound("audio/bounce.wav");
		
		AudioSource source = new AudioSource();
		source.setLooping(true);
		source.play(buffer);

		AudioSource source2 = new AudioSource();
		source2.setLooping(true);
		source2.play(buffer2);
		
		float xPos = 0;
		source2.setPosition(xPos, 0, 0);

		char c = ' ';
		while (c != 'q') {
			xPos -= 0.03f;
			source2.setPosition(xPos, 0, 0);
			Thread.sleep(10);
		}

		source.delete();
		source2.delete();
		AudioManager.cleanUp();
	}
}
