package engine.world.particles;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.util.Engine;

/**
 * @Description: Generates particles in the world
 * 
 */

public class ParticleSystem {

	private float pps, averageSpeed, gravityComplient, averageLifeLength, averageScale;
	private float directionDeviation, speedError, lifeError, scaleError;
	private boolean randomRotation;

	private ParticleTexture texture;
	private Vector3f direction;
	private Random random;

	/* Constructor Method */
	public ParticleSystem(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength,
			float scale) {
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageSpeed = speed;
		this.averageScale = scale;
		this.texture = texture;
		this.pps = pps;

		this.randomRotation = false;
		this.directionDeviation = 0;
		this.scaleError = 0;

		this.random = new Random();
	}

	/* Sets the direction and error (between 0 and 1) */
	public void setDirection(Vector3f direction, float deviation) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = (float) (deviation * Math.PI);
	}

	/* Sets if there should be a random rotation */
	public void randomizeRotation() {
		randomRotation = true;
	}

	/* Sets the speed error (between 0 and 1) */
	public void setSpeedError(float error) {
		this.speedError = error * averageSpeed;
	}

	/* Sets the life error (between 0 and 1) */
	public void setLifeError(float error) {
		this.lifeError = error * averageLifeLength;
	}

	/* Sets the scale error (between 0 and 1) */
	public void setScaleError(float error) {
		this.scaleError = error * averageScale;
	}

	/* Generates the particles at a position */
	public void generateParticles(Vector3f systemCenter) {

		// Figures out how many particles to make
		float delta = Engine.getDelta();
		float particlesToCreate = pps * delta;
		float partialParticle = particlesToCreate % 1;

		// If it decides to render, emit a particle
		if (Math.random() < partialParticle) {
			this.emitParticle(systemCenter);
		}
	}

	/* Creates a particle at a position */
	private void emitParticle(Vector3f center) {

		// Creates a velocity vector
		Vector3f velocity = null;
		if (direction != null) {
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		} else {
			velocity = generateRandomUnitVector();
		}
		velocity.normalise();
		velocity.scale(generateValue(averageSpeed, speedError));

		// Generates the other parameters for a particle
		float scale = generateValue(averageScale, scaleError);
		float lifeLength = generateValue(averageLifeLength, lifeError);

		new Particle(texture, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale);
	}

	/* Generates a random error for the variable */
	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	/* Generates a random rotation */
	private float generateRotation() {
		if (randomRotation) {
			return random.nextFloat() * 360f;
		} else {
			return 0;
		}
	}

	/* ??? */
	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		float cosAngle = (float) Math.cos(angle);
		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		Vector4f direction = new Vector4f(x, y, z, 1);
		if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0, 0, 1), null);
			rotateAxis.normalise();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0, 0, 1)));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirection.z == -1) {
			direction.z *= -1;
		}
		return new Vector3f(direction);
	}

	/* Generates a random velocity vector in a sphere */
	private Vector3f generateRandomUnitVector() {

		// Random angle in radians
		float theta = (float) (random.nextFloat() * 2f * Math.PI);

		// Random z between -1 and 1
		float z = (random.nextFloat() * 2) - 1;

		// Equation for a unit circle
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);

		// Components of the circle
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		return new Vector3f(x, y, z);
	}
}
