package engine.world.particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.Engine;

/**
 * @Description: Single piece of a particle system
 * 
 */

public class Particle {

	private float gravityEffect, elapsedTime, lifeLength, rotation, distance, scale;
	private Vector3f position, velocity, change;

	private Vector2f texOffset1, texOffset2;
	private ParticleTexture texture;
	private float blend;

	/* Constructor Method */
	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale) {

		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.position = position;
		this.velocity = velocity;
		this.texture = texture;
		this.scale = scale;

		this.texOffset1 = new Vector2f();
		this.texOffset2 = new Vector2f();
		this.change = new Vector3f();
		this.elapsedTime = 0;

		ParticleMaster.addParticle(this);
	}

	/* Update the position and texture */
	protected boolean update() {
		// Update the velocity
		this.velocity.y += -50 * gravityEffect * Engine.getDelta();

		// Update the change in position
		this.change.x = velocity.x;
		this.change.y = velocity.y;
		this.change.z = velocity.z;
		this.change.scale(Engine.getDelta());

		Vector3f.add(change, position, position);

		// Change the texture as the particle progresses
		this.updateTextureCoordInfo();

		// Gets the distance from the camera
		this.distance = Vector3f.sub(Engine.getCamera().getPosition(), position, null).lengthSquared();

		// Updates particle life and returns if it should die
		this.elapsedTime += Engine.getDelta();

		return elapsedTime < lifeLength;
	}

	/* Updates the texture based on the particle life */
	private void updateTextureCoordInfo() {

		// Finds the current texture for the particle age
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;

		// Gets the x and y coordinates on the texture
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;

		// Gets the decimal part of the progression as a blend factor
		this.blend = atlasProgression % 1;

		// Gets the two textures that the particle is between
		this.setTextureOffset(texOffset1, index1);
		this.setTextureOffset(texOffset2, index2);

	}

	/* Gets the x and y coordinates on the texture */
	private void setTextureOffset(Vector2f offset, int index) {

		// Gets the x and y coordinates
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();

		// Gets a percentage across the texture
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}

	/* Returns the distance of the particle from the camera */
	protected float getDistance() {
		return distance;
	}

	/* Returns the offset of the first texture */
	protected Vector2f getTexOffset1() {
		return texOffset1;
	}

	/* Returns the offset of the second texture */
	protected Vector2f getTexOffset2() {
		return texOffset2;
	}

	/* Returns the blend factor of the particle */
	protected float getBlend() {
		return blend;
	}

	/* Returns the full particle texture */
	protected ParticleTexture getTexture() {
		return texture;
	}

	/* Returns the particle position */
	protected Vector3f getPosition() {
		return position;
	}

	/* Returns the particle rotation */
	protected float getRotation() {
		return rotation;
	}

	/* Returns the particle scale */
	protected float getScale() {
		return scale;
	}
}
