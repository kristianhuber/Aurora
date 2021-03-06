package engine.world.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engine.util.Engine;
import engine.world.World;
import engine.world.terrain.Terrain;

/**
 * @Description: How the user moves around in the game
 * 
 */

public class Camera extends Entity {

	private final float MOUSE_TOLERANCE = 3.0F;
	private final float Y_OFFSET = 5F;
	private final float SCROLL = 100.0F;
	private float SPEED = 15.0F;

	private Vector3f velocity = new Vector3f(0, 0, 0);

	private World world;

	private boolean flying;

	/* Construction Method */
	public Camera(World world) {
		super(world, "betterpine", new Vector3f(0, 0, 0));
		this.scale = 5;
		world.addEntity(this);
		
		this.world = world;
		this.flying = true;
		if (flying)
			SPEED = 30;
		
		this.position.x = World.WORLD_SIZE * Terrain.SIZE / 2;
		this.position.y = 200;
		this.position.z = World.WORLD_SIZE * Terrain.SIZE / 2;
		
		updateTransformationMatrix();
	}

	/* Moves the camera around the world */
	public void move() {

		Entity[] playerCollisions = world.getCollisionManager().getBoxCollisions(this);

		// Finds out which direction the player wants to move in
		float delta = Engine.getDelta();
		this.checkInputs(delta);

		if (!flying) {
			velocity.y = -5;
		}

		// Calculates the change in position based on the velocity and direction
		this.position.x += velocity.x * Math.cos(Math.toRadians(rotation.y));
		this.position.x -= velocity.z * Math.sin(Math.toRadians(rotation.y));
		this.position.y += velocity.y;
		this.position.z += velocity.x * Math.sin(Math.toRadians(rotation.y));
		this.position.z += velocity.z * Math.cos(Math.toRadians(rotation.y));

		// Updates the velocity
		velocity.x = this.decelerate(velocity.x, 0.9f);
		velocity.y = this.decelerate(velocity.y, 0.9f);
		velocity.z = this.decelerate(velocity.z, 0.9f);

		// Terrain collision detection
		float height = world.getTerrainHeightAt(position);
		if (position.y < height + Y_OFFSET) {
			position.y = height + Y_OFFSET;
		}
		
		updateTransformationMatrix();
		updateBoundingBox();
	}

	/* Calculates the deceleration rate */
	private float decelerate(float f, float rate) {
		if (Math.abs(f) < 0.1f) {
			f = 0;
		} else {
			f *= rate;
		}
		return f;
	}

	/* Checks to see which buttons are pressed */
	private void checkInputs(float delta) {

		boolean xDir = false;
		boolean zDir = false;

		// Left and Right Movement
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.velocity.x = SPEED * delta;
			xDir = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.velocity.x = -SPEED * delta;
			xDir = true;
		}

		// Forward and Back Movement
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.velocity.z = -SPEED * delta;
			zDir = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.velocity.z = SPEED * delta;
			zDir = true;
		}

		// Up and Down Movement
		if (flying) {
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				this.velocity.y = SPEED * delta;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				this.velocity.y = -SPEED * delta;
			}
		}

		// Sprinting
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (xDir)
				this.velocity.x *= 1.5f;
			if (zDir)
				this.velocity.z *= 1.5f;
		}

		// Mouse Movement Left and Right
		if (Mouse.getX() > Engine.WIDTH / 2 + MOUSE_TOLERANCE) {
			rotation.y += SCROLL * delta;
		} else if (Mouse.getX() < Engine.WIDTH / 2 - MOUSE_TOLERANCE) {
			rotation.y += -SCROLL * delta;
		}

		// Mouse Movement Up and Down
		if (Mouse.getY() > Engine.HEIGHT / 2 + MOUSE_TOLERANCE) {
			rotation.x += -SCROLL * delta;
		} else if (Mouse.getY() < Engine.HEIGHT / 2 - MOUSE_TOLERANCE) {
			rotation.x += SCROLL * delta;
		}

		// Sets the limit on the x rotation so you don't do a flip
		if (rotation.x < -80)
			rotation.x = -80;
		if (rotation.x > 80)
			rotation.x = 80;

		// Resets the cursor position
		Mouse.setCursorPosition(Engine.WIDTH / 2, Engine.HEIGHT / 2);
	}

	/* Inverts the x rotation, used when rendering reflections */
	public void invertPitch() {
		rotation.x = -rotation.x;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
		if (flying) {
			SPEED = 50;
		} else {
			SPEED = 15;
		}
	}
}
