package engine.audio;

import org.lwjgl.openal.AL10;

public class AudioSource {

	private int sourceID;

	public AudioSource() {
		sourceID = AL10.alGenSources();
		
		// This is how steep the curve is after the drop off distance
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 1);
		
		// Anything before this distance will have a gain of 1. Set radius to size of
		// town to play town music
		AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, 6);
		
		//Sound isn't played after this distance
		AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, 15);
		
		AudioManager.registerSource(this, sourceID);
	}

	public void play(int buffer) {
		stop();
		AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
		continuePlaying();
	}

	public void delete() {
		AudioManager.deleteSource(this, sourceID);
	}

	public void pause() {
		AL10.alSourcePause(sourceID);
	}

	public void continuePlaying() {
		AL10.alSourcePlay(sourceID);
	}

	public void stop() {
		AL10.alSourceStop(sourceID);
	}

	public void setLooping(boolean loop) {
		AL10.alSourcei(sourceID, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void setVelocity(float x, float y, float z) {
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, x, y, z);
	}

	public void setVolume(float volume) {
		AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch);
	}

	public void setPosition(float x, float y, float z) {
		AL10.alSource3f(sourceID, AL10.AL_POSITION, x, y, z);
	}
}
