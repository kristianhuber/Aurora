package engine.rendering.models;

import org.lwjgl.util.vector.Vector3f;

import engine.world.entities.Entity;

/**
 * This class will handle most of the heavy weight physics interaction between
 * 3D instances in the world. The major one is collision detection between 3D
 * objects.
 * 
 * @author Greg
 */
public class PhysicsObject {

	// This is the first level of collision detection, it is the only one that
	// cannot exist by itself because it's sole purpose is to determine whether or
	// not to use the other two algorithms.
	private float farthestDistance = -1;

	// This is the second level of collision detection and it can exist with just
	// the first one. It essentially detects for cube intersection of predefined
	// hit boxes for each entity.
	private Vector3f hitbox_min_bounds;
	private Vector3f hitbox_max_bounds;

	// This is the third level of collision detection and it can exist with just
	// the first one or in addition to the second and first one. It test overlap of
	// all triangles in simplified versions of the models that are predefined in
	// their model files.
	private ModelData simplifedModel = null;

	/**
	 * The method that gets whether or not there is a collision. It returns a vector
	 * that represents the direction and length that one entity is overlapping the
	 * other.
	 * 
	 * @param thisEntity
	 *            - the entity that is testing for collision against all other
	 *            entities.
	 * @param otherEntity
	 *            - the entity that is currently being tested to collide with the
	 *            entity that is being tested.
	 * @param other
	 *            - the physics object of the other entity.
	 * @return a vector that represents the direction and length that one entity is
	 *         overlapping the other.
	 */
	public Vector3f getCollisionVector(Entity thisEntity, Entity otherEntity, PhysicsObject other) {
		if (prelimCollisionTest(thisEntity, otherEntity, other)) {
			if (hitbox_min_bounds != null) {
				Vector3f fastCalc = fast_getCollisionVector(thisEntity, otherEntity, other);

				// If the collision was not detected with the first algorithm then it wont be
				// detected with the second algorithm either so return no collision.
				if (fastCalc.equals(new Vector3f(0, 0, 0)))
					return fastCalc;
			} else if (simplifedModel != null)
				return slow_getCollisionVector(thisEntity, otherEntity, other);
		}
		return new Vector3f(0, 0, 0);
	}

	/**
	 * Gets whether or not to test collisions with the other algorithms. This is a
	 * quick little distance test that will make the collision detection a lot more
	 * efficient.
	 * 
	 * @param thisEntity
	 *            - the entity that is testing for collision against all other
	 *            entities.
	 * @param otherEntity
	 *            - the entity that is currently being tested to collide with the
	 *            entity that is being tested.
	 * @param other
	 *            - the physics object of the other entity.
	 * @return true if there could be a collision and false if there can't be a
	 *         collision.
	 */
	private boolean prelimCollisionTest(Entity thisEntity, Entity otherEntity, PhysicsObject other) {
		float lengthToBeGreaterThan = other.getFarthestDistance() + farthestDistance;
		float lengthToBeGreaterThanSquared = lengthToBeGreaterThan * lengthToBeGreaterThan;
		Vector3f thisPos = thisEntity.getPosition();
		Vector3f otherPos = otherEntity.getPosition();
		float farthestDistanceBetween = (thisPos.x - otherPos.x) * (thisPos.x - otherPos.x);
		farthestDistanceBetween += (thisPos.y - otherPos.y) * (thisPos.y - otherPos.y);
		farthestDistanceBetween += (thisPos.z - otherPos.z) * (thisPos.z - otherPos.z);
		return farthestDistanceBetween >= lengthToBeGreaterThanSquared;
	}

	private Vector3f fast_getCollisionVector(Entity thisEntity, Entity otherEntity, PhysicsObject other) {
		return new Vector3f(0, 0, 0);
	}

	private Vector3f slow_getCollisionVector(Entity thisEntity, Entity otherEntity, PhysicsObject other) {
		return new Vector3f(0, 0, 0);
	}

	public PhysicsObject(float farthestDistance, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax,
			ModelData simplifedModel) {
		this.farthestDistance = farthestDistance;
		this.simplifedModel = simplifedModel;

		this.hitbox_min_bounds = new Vector3f(xMin, yMin, zMin);
		this.hitbox_max_bounds = new Vector3f(xMax, yMax, zMax);
	}

	public float getFarthestDistance() {
		return farthestDistance;
	}

	public Vector3f getHitbox_min_bounds() {
		return hitbox_min_bounds;
	}

	public Vector3f getHitbox_max_bounds() {
		return hitbox_max_bounds;
	}

	public ModelData getSimplifedModel() {
		return simplifedModel;
	}

}
