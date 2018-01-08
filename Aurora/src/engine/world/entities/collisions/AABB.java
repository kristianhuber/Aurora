package engine.world.entities.collisions;

import engine.world.entities.Entity;

public class AABB {

	private float[] endpointsArray = null;
	private Endpoint[] endpointObjects = null;
	private Entity parentEntity = null;

	public AABB(float[] endpointsArray, Entity parentEntity) {
		this.endpointsArray = endpointsArray;
		this.parentEntity = parentEntity;

		endpointObjects = new Endpoint[6];
		for (int i = 0; i < endpointsArray.length; i++)
			endpointObjects[i] = new Endpoint(this, endpointsArray[i], i < 3);
	}

	public float[] getEndpointsArray() {
		return endpointsArray;
	}

	public Endpoint[] getEndpointObjects() {
		return endpointObjects;
	}

	public Entity getParentEntity() {
		return parentEntity;
	}

	public void updateBounds(float[] calculateAABB) {
		for (int i = 0; i < endpointsArray.length; i++)
			endpointObjects[i].setValue(calculateAABB[i]);
	}

}
