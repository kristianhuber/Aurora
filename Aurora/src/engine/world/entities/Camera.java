package engine.world.entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.util.Calculator;
import engine.util.Engine;
import engine.world.World;
import engine.world.entities.collisions.CollisionPacket;
import engine.world.entities.collisions.TrianglePlane;
import engine.world.terrain.Terrain;

/**
 * @Description: How the user moves around in the game
 * 
 */

public class Camera extends Entity {

	private final float MOUSE_TOLERANCE = 3.0F;
	private float Y_CAMERA_OFFSET = 5f;
	private final float SCROLL = 100.0F;
	private float SPEED = 100.0f;
	private static final double SQRT_2 = Math.sqrt(2);

	private Vector3f keyBasedVelocity = new Vector3f(0, 0, 0);
	private Vector3f velocity = new Vector3f(0, 0, 0);
	private final Vector3f GRAVITY = new Vector3f(0, -1f, 0);
	private final Vector3f E_RADIUS_VECTOR = new Vector3f(2, 3, 2);
	protected int collisionRecursionStep = 0;
	private static final float VERY_CLOSE_DISTANCE = 0.005f;
	private CollisionPacket collisionPacket;

	private World world;

	private boolean flying;

	/* Construction Method */
	public Camera(World world) {
		super(world, "betterpine", new Vector3f(0, 0, 0));
		this.scale = 1;
		world.addEntity(this);

		this.world = world;
		this.flying = true;
		if (flying)
			SPEED = 30;

		this.position.x = World.WORLD_SIZE * Terrain.SIZE / 2;
		this.position.y = 50;
		this.position.z = World.WORLD_SIZE * Terrain.SIZE / 2;

		Vector3f middleOfEntity = getMiddleOfEntity();
		collisionPacket = new CollisionPacket(E_RADIUS_VECTOR, velocity, middleOfEntity);

		updateTransformationMatrix();
		updateBoundingBox();
	}

	private Vector3f getMiddleOfEntity() {
		return new Vector3f(position.getX(), position.getY() + E_RADIUS_VECTOR.getY(), position.getZ());
	}

	/* Moves the camera around the world */
	public void move() {

		// Finds out which direction the player wants to move in
		float delta = Engine.getDelta();
		this.checkInputs(delta);

		// Now we have to recalculate the velocity vector so it is going in the
		// direction that the player is looking:
		float yAngle = -rotation.y;
		velocity.y = keyBasedVelocity.y;
		velocity.z = (float) (keyBasedVelocity.z * Math.cos(Math.toRadians(-yAngle))
				- keyBasedVelocity.x * Math.sin(Math.toRadians(-yAngle)));
		velocity.x = (float) (keyBasedVelocity.z * Math.sin(Math.toRadians(yAngle))
				- keyBasedVelocity.x * Math.cos(Math.toRadians(yAngle)));

		// Getting Collisions:
		List<Entity> ents = world.getAllEntities("cube");
		Entity[] playerCollisions = new Entity[ents.size()];
		for (int i = 0; i < playerCollisions.length; i++)
			playerCollisions[i] = ents.get(i);

		// Actually moving the entity with respect to velocity and gravity vectors as
		// well as the potential collisions array.
		this.collideAndSlide(velocity, GRAVITY, playerCollisions);

		updateTransformationMatrix();
		updateBoundingBox();

		// Terrain collision detection
		float height = world.getTerrainHeightAt(position);
		if (position.y < height) {
			position.y = height;
		}

		// Decelerating the key based velocities.
		keyBasedVelocity.x = this.decelerate(keyBasedVelocity.x, 0.9f);
		keyBasedVelocity.y = this.decelerate(keyBasedVelocity.y, 0.9f);
		keyBasedVelocity.z = this.decelerate(keyBasedVelocity.z, 0.9f);
	}

	protected void collideAndSlide(Vector3f velocity, Vector3f gravity, Entity[] toCollideWith) {

		Vector3f finalPosition = CollisionPacket.getESpaceVector(position, E_RADIUS_VECTOR);
		// Building the collision packet with the current velocity and position. This
		// converts the velocity and position into e space automatically.
		collisionPacket.R3Position = getMiddleOfEntity();
		collisionPacket.R3Velocity = velocity;

		Vector3f eSpacePosition = CollisionPacket.getESpaceVector(collisionPacket.R3Position, E_RADIUS_VECTOR);
		Vector3f eSpaceVelocity = CollisionPacket.getESpaceVector(collisionPacket.R3Velocity, E_RADIUS_VECTOR);

		if (velocity.lengthSquared() != 0) {
			// Calculating the final position in E3:
			System.out.println(
					"\n-----------------------------------------------------------------------------------------------\n");
			System.out.println("Start Velocity, Entities to Collide with = " + toCollideWith.length);
			collisionRecursionStep = 0;
			finalPosition = collideWithWorld(eSpaceVelocity, eSpacePosition, toCollideWith);
			System.out.println("End Velocity");
		}

		// Updating the R3 Positions:
		collisionPacket.foundCollision = false;
		collisionPacket.R3Position = CollisionPacket.getRSpaceVector(finalPosition, E_RADIUS_VECTOR); // CollisionPacket.getRSpaceVector(collisionPacket.basePoint,
		// E_RADIUS_VECTOR);
		collisionPacket.R3Velocity = gravity;

		// System.out.println("Start Gravity");
		eSpaceVelocity = CollisionPacket.getESpaceVector(gravity, E_RADIUS_VECTOR);
		eSpacePosition = finalPosition;
		collisionRecursionStep = 0;
		// finalPosition = collideWithWorld(eSpaceVelocity, eSpacePosition,
		// toCollideWith);
		// System.out.println("Velocity Move: " + collisionPacket.basePoint.toString() +
		// " -> " + finalPosition.toString() + " | At "
		// + gravity.toString());
		// System.out.println("End Gravity");

		finalPosition = CollisionPacket.getRSpaceVector(finalPosition, E_RADIUS_VECTOR);
		finalPosition.setY(finalPosition.getY() - E_RADIUS_VECTOR.getY());

		position.set(finalPosition.getX(), finalPosition.getY(), finalPosition.getZ());
	}

	/**
	 * Gets the new position of the entity after all narrow phase collision
	 * calculations.
	 * 
	 * @param collisionPacket
	 *            - the collision packet to use for collisions
	 * @param toCollideWith
	 *            - the array of entities that should be tested for narrow phase
	 *            collision
	 * @return the new position of the entity.
	 */
	private Vector3f collideWithWorld(Vector3f vel, Vector3f pos, Entity[] toCollideWith) {
		if (collisionRecursionStep > 5) {
			System.out.println("Hard Return: " + pos.toString());
			return pos;
		}

		collisionPacket.velocity = new Vector3f(vel);
		collisionPacket.normalizedVelocity = new Vector3f(vel);
		if (collisionPacket.velocity.length() != 0)
			collisionPacket.normalizedVelocity.normalise();
		collisionPacket.basePoint = new Vector3f(pos);
		collisionPacket.foundCollision = false;

		// Actually checking all the triangles for a collision:
		for (int n = 0; n < toCollideWith.length; n++) {
			float[] vertices = toCollideWith[n].getModel().getRawModel().getModelData().getVertices();
			float[] normals = toCollideWith[n].getModel().getRawModel().getModelData().getNormals();
			int[] indices = toCollideWith[n].getModel().getRawModel().getModelData().getIndices();

			Matrix4f transMatrix = toCollideWith[n].getTransformationMatrix();
			Vector3f collisionEntityPos = toCollideWith[n].getPosition();

			System.out.println("Vertex Count = " + vertices.length / 3 + " Normals Count = " + normals.length / 3);
			int count = 1;
			for (int i = 0; i < normals.length; i++) {
				System.out.print(normals[i]);
				if (count % 3 == 0)
					System.out.println();
				else
					System.out.print(",");
				count++;
			}

			for (int i = 0; i < indices.length / 3; i++) {
				// Getting the vectors that represent the three points of the triangle in
				// question.
				Vector3f a = new Vector3f(vertices[indices[i * 3] * 3], vertices[indices[i * 3] * 3 + 1],
						vertices[indices[i * 3] * 3 + 2]);
				Vector3f b = new Vector3f(vertices[indices[i * 3 + 1] * 3], vertices[indices[i * 3 + 1] * 3 + 1],
						vertices[indices[i * 3 + 1] * 3 + 2]);
				Vector3f c = new Vector3f(vertices[indices[i * 3 + 2] * 3], vertices[indices[i * 3 + 2] * 3 + 1],
						vertices[indices[i * 3 + 2] * 3 + 2]);

				Vector4f temp = Matrix4f.transform(transMatrix, new Vector4f(a.getX(), a.getY(), a.getZ(), 1f), null);
				a = new Vector3f(temp.getX(), temp.getY(), temp.getZ());
				temp = Matrix4f.transform(transMatrix, new Vector4f(b.getX(), b.getY(), b.getZ(), 1f), null);
				b = new Vector3f(temp.getX(), temp.getY(), temp.getZ());
				temp = Matrix4f.transform(transMatrix, new Vector4f(c.getX(), c.getY(), c.getZ(), 1f), null);
				c = new Vector3f(temp.getX(), temp.getY(), temp.getZ());
				System.out.println(i + ": " + a.toString() + "; " + b.toString() + "; " + c.toString());

				// Getting the normal vectors for the points.
				Matrix4f normalTransMatrix = Calculator.createTransformationMatrix(new Vector3f(0, 0, 0),
						toCollideWith[n].getRotation(), 1);
				Vector3f aNormal = new Vector3f(
						normals[indices[i * 3] * 3], 
						normals[indices[i * 3] * 3 + 1],
						normals[indices[i * 3] * 3 + 2]);
				temp = Matrix4f.transform(normalTransMatrix,
						new Vector4f(aNormal.getX(), aNormal.getY(), aNormal.getZ(), 1f), null);
				aNormal = new Vector3f(temp.getX(), temp.getY(), temp.getZ());
				Vector3f bNormal = new Vector3f(
						normals[indices[i * 3 + 1] * 3], 
						normals[indices[i * 3 + 1] * 3 + 1],
						normals[indices[i * 3 + 1] * 3 + 2]);
				temp = Matrix4f.transform(normalTransMatrix,
						new Vector4f(bNormal.getX(), bNormal.getY(), bNormal.getZ(), 1f), null);
				bNormal = new Vector3f(temp.getX(), temp.getY(), temp.getZ());
				Vector3f cNormal = new Vector3f(
						normals[indices[i * 3 + 2] * 3], 
						normals[indices[i * 3 + 2] * 3 + 1],
						normals[indices[i * 3 + 2] * 3 + 2]);
				temp = Matrix4f.transform(normalTransMatrix,
						new Vector4f(cNormal.getX(), cNormal.getY(), cNormal.getZ(), 1f), null);
				cNormal = new Vector3f(temp.getX(), temp.getY(), temp.getZ());

				Vector3f planeNormalSide = new Vector3f((aNormal.getX() + bNormal.getX() + cNormal.getX()) / 3f,
						(aNormal.getY() + bNormal.getY() + cNormal.getY()) / 3f,
						(aNormal.getZ() + bNormal.getZ() + cNormal.getZ()) / 3f);
				Vector3f planeNormal = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(c, a, null), null);

				System.out.println(aNormal.toString());
				System.out.println(bNormal.toString());
				System.out.println(cNormal.toString());
				
				if (Vector3f.dot(aNormal, planeNormal) < 0)
					planeNormal.negate();

				if (planeNormal.lengthSquared() != 0) {
					planeNormal.normalise();
					CollisionPacket.checkTriangle(collisionPacket, a, b, c, planeNormal);
				}
				System.out.println();
			}
		}

		// If there was no collision:
		if (!collisionPacket.foundCollision) {
			return Vector3f.add(pos, vel, null);
		}

		System.out.println("COLLISION Found! Step: " + collisionRecursionStep);

		Vector3f destinationPoint = Vector3f.add(pos, vel, null);
		Vector3f newBasePoint = new Vector3f(pos);

		if (collisionPacket.nearestDistance >= VERY_CLOSE_DISTANCE) {
			Vector3f v = new Vector3f(vel);
			v = CollisionPacket.scaleVector(v, (float) (collisionPacket.nearestDistance - VERY_CLOSE_DISTANCE));
			newBasePoint = Vector3f.add(v, collisionPacket.basePoint, null);

			// Adjusting the intersection point to be a very small distance away from the
			// actual intersection distance:
			v.normalise();
			collisionPacket.intersectionPoint = Vector3f.sub(collisionPacket.intersectionPoint,
					CollisionPacket.scaleVector(v, VERY_CLOSE_DISTANCE), null);
		}

		// Determining the sliding plane:
		System.out.println("Collision Point: " + collisionPacket.intersectionPoint.toString());
		System.out.println("New Base Point: " + newBasePoint.toString());
		Vector3f slidePlaneOrigin = new Vector3f(collisionPacket.intersectionPoint);
		Vector3f slidePlaneNormal = Vector3f.sub(newBasePoint, collisionPacket.intersectionPoint, null);
		System.out.println("Sliding plane Normal non normalized: " + slidePlaneNormal.toString());
		slidePlaneNormal.normalise();
		System.out.println("Sliding plane Normal: " + slidePlaneNormal.toString());

		float slidePlaneConstant = -Vector3f.dot(slidePlaneOrigin, slidePlaneNormal);
		float distanceToDestinationPoint = Vector3f.dot(destinationPoint, slidePlaneNormal) + slidePlaneConstant;
		Vector3f newDestinationPoint = Vector3f.sub(destinationPoint,
				CollisionPacket.scaleVector(slidePlaneNormal, distanceToDestinationPoint), null);

		Vector3f newVelocityVector = Vector3f.sub(newDestinationPoint, collisionPacket.intersectionPoint, null);

		if (newVelocityVector.length() < VERY_CLOSE_DISTANCE)
			return newBasePoint;

		collisionRecursionStep++;
		Vector3f solution = collideWithWorld(newBasePoint, newVelocityVector, toCollideWith);
		System.out.println("Solution: " + solution.toString());
		return solution;
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
			this.keyBasedVelocity.x = -SPEED * delta;
			xDir = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.keyBasedVelocity.x = SPEED * delta;
			xDir = true;
		}

		// Forward and Back Movement
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.keyBasedVelocity.z = -SPEED * delta;
			zDir = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.keyBasedVelocity.z = SPEED * delta;
			zDir = true;
		}

		// Making sure the velocity has the same magnitude in all directions.
		if (zDir && xDir) {
			keyBasedVelocity.normalise();
			keyBasedVelocity = CollisionPacket.scaleVector(keyBasedVelocity, SPEED * delta);
		}

		// Up and Down Movement
		if (flying) {
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				// this.velocity.y = SPEED * delta;
				this.keyBasedVelocity.y = 0;
				this.rotation.x = 0;
				this.Y_CAMERA_OFFSET = 5f;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				// this.velocity.y = SPEED * delta;
				this.keyBasedVelocity.y = 0;
				this.rotation.x = 90;
				this.Y_CAMERA_OFFSET = 20f;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				this.keyBasedVelocity.y = -SPEED * delta;
			}
		}

		// Sprinting
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (xDir)
				this.keyBasedVelocity.x *= 1.5f;
			if (zDir)
				this.keyBasedVelocity.z *= 1.5f;
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
		if (rotation.x < -90)
			rotation.x = -90;
		if (rotation.x > 90)
			rotation.x = 90;

		// Resets the cursor position
		Mouse.setCursorPosition(Engine.WIDTH / 2, Engine.HEIGHT / 2);
	}

	public Vector3f getCameraPosition() {
		return new Vector3f(position.getX(), position.getY() + Y_CAMERA_OFFSET, position.getZ());
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
