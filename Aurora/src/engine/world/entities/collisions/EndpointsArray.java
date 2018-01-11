package engine.world.entities.collisions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.world.entities.Entity;
import engine.world.entities.collisions.Endpoint.endpointAxis;

public class EndpointsArray {

	private Endpoint[] x_endpoints, y_endpoints, z_endpoints;
	private PairManager pairManager;

	public EndpointsArray() {
		x_endpoints = new Endpoint[0];
		y_endpoints = new Endpoint[0];
		z_endpoints = new Endpoint[0];
		pairManager = new PairManager();
	}

	private void updateEndpoint(endpointAxis axis, Endpoint toUpdate) {
		boolean isMin = toUpdate.isMin();
		if (axis == endpointAxis.X_AXIS) {
			int startingIndex = toUpdate.getIndex();
			if (startingIndex == x_endpoints.length - 1) {
				walkDownArray(x_endpoints, toUpdate, axis);
			} else if (startingIndex == 0) {
				walkUpArray(x_endpoints, toUpdate, axis);
			} else {
				if (toUpdate.getValue() > x_endpoints[toUpdate.getIndex() + 1].getValue())
					walkUpArray(x_endpoints, toUpdate, axis);
				else if (toUpdate.getValue() < x_endpoints[toUpdate.getIndex() - 1].getValue())
					walkDownArray(x_endpoints, toUpdate, axis);
			}
		} else if (axis == endpointAxis.Y_AXIS) {
			int startingIndex = toUpdate.getIndex();
			if (startingIndex == y_endpoints.length - 1) {
				walkDownArray(y_endpoints, toUpdate, axis);
			} else if (startingIndex == 0) {
				walkUpArray(y_endpoints, toUpdate, axis);
			} else {
				if (toUpdate.getValue() > y_endpoints[toUpdate.getIndex() + 1].getValue())
					walkUpArray(y_endpoints, toUpdate, axis);
				else if (toUpdate.getValue() < y_endpoints[toUpdate.getIndex() - 1].getValue())
					walkDownArray(y_endpoints, toUpdate, axis);
			}
		} else {
			int startingIndex = toUpdate.getIndex();
			if (startingIndex == z_endpoints.length - 1) {
				walkDownArray(z_endpoints, toUpdate, axis);
			} else if (startingIndex == 0) {
				walkUpArray(z_endpoints, toUpdate, axis);
			} else {
				if (toUpdate.getValue() > z_endpoints[toUpdate.getIndex() + 1].getValue())
					walkUpArray(z_endpoints, toUpdate, axis);
				else if (toUpdate.getValue() < z_endpoints[toUpdate.getIndex() - 1].getValue())
					walkDownArray(z_endpoints, toUpdate, axis);
			}
		}
	}

	private void walkUpArray(Endpoint[] axisArray, Endpoint toWalk, endpointAxis axis) {
		boolean isMin = toWalk.isMin();
		while (toWalk.getValue() > axisArray[toWalk.getIndex() + 1].getValue()) {
			int index = toWalk.getIndex();
			boolean isOtherMin = axisArray[index + 1].isMin();
			Endpoint placeHolder = toWalk;
			axisArray[index] = axisArray[index + 1];
			axisArray[index + 1] = placeHolder;
			axisArray[index].setIndex(index);
			axisArray[index + 1].setIndex(index + 1);

			// updating the pair manager.
			if (axisArray[index].getParentBox().getParentEntity() != axisArray[index + 1].getParentBox()
					.getParentEntity())
				if (axis == endpointAxis.X_AXIS) {
					if (isMin && !isOtherMin)
						pairManager.xAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
					else if (!isMin && isOtherMin)
						pairManager.xDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
				} else if (axis == endpointAxis.Y_AXIS) {
					if (isMin && !isOtherMin)
						pairManager.yAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
					else if (!isMin && isOtherMin)
						pairManager.yDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
				} else {
					if (isMin && !isOtherMin)
						pairManager.zAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
					else if (!isMin && isOtherMin)
						pairManager.zDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index + 1].getParentBox().getParentEntity());
				}

			if (index + 2 == axisArray.length)
				return;
		}
	}

	private void walkDownArray(Endpoint[] axisArray, Endpoint toWalk, endpointAxis axis) {
		boolean isMin = toWalk.isMin();
		while (toWalk.getValue() < axisArray[toWalk.getIndex() - 1].getValue()) {
			int index = toWalk.getIndex();
			boolean isOtherMin = axisArray[index - 1].isMin();
			Endpoint placeHolder = toWalk;
			axisArray[index] = axisArray[index - 1];
			axisArray[index - 1] = placeHolder;
			axisArray[index].setIndex(index);
			axisArray[index - 1].setIndex(index - 1);

			// updating the pair manager.
			if (axisArray[index].getParentBox().getParentEntity() != axisArray[index - 1].getParentBox()
					.getParentEntity())
				if (axis == endpointAxis.X_AXIS) {
					if (!isMin && isOtherMin)
						pairManager.xAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
					else if (isMin && !isOtherMin)
						pairManager.xDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
				} else if (axis == endpointAxis.Y_AXIS) {
					if (!isMin && isOtherMin)
						pairManager.yAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
					else if (isMin && !isOtherMin)
						pairManager.yDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
				} else {
					if (!isMin && isOtherMin)
						pairManager.zAddCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
					else if (isMin && !isOtherMin)
						pairManager.zDeleteCollision(axisArray[index].getParentBox().getParentEntity(),
								axisArray[index - 1].getParentBox().getParentEntity());
				}

			// Checking to make sure it doesn't go out of bounds.
			if (index - 1 == 0)
				return;
		}
	}

	private void binaryEndpointInsertion(endpointAxis axis, Endpoint toInsert) {
		if (axis == endpointAxis.X_AXIS) {
			int index = getIndexToAdd(x_endpoints, toInsert);
			toInsert.setIndex(index);
			x_endpoints = insertInArray(x_endpoints, toInsert, index);
		} else if (axis == endpointAxis.Y_AXIS) {
			int index = getIndexToAdd(y_endpoints, toInsert);
			toInsert.setIndex(index);
			y_endpoints = insertInArray(y_endpoints, toInsert, index);
		} else {
			int index = getIndexToAdd(z_endpoints, toInsert);
			toInsert.setIndex(index);
			z_endpoints = insertInArray(z_endpoints, toInsert, index);
		}
	}

	private void addToArray(endpointAxis axis, Endpoint toInsert, boolean isMin) {
		if (axis == endpointAxis.X_AXIS) {
			x_endpoints = insertInArray(x_endpoints, toInsert, 0);
			if (x_endpoints.length > 1)
				updateEndpoint(endpointAxis.X_AXIS, toInsert);
		} else if (axis == endpointAxis.Y_AXIS) {
			y_endpoints = insertInArray(y_endpoints, toInsert, 0);
			if (y_endpoints.length > 1)
				updateEndpoint(endpointAxis.Y_AXIS, toInsert);
		} else {
			z_endpoints = insertInArray(z_endpoints, toInsert, 0);
			if (z_endpoints.length > 1)
				updateEndpoint(endpointAxis.Z_AXIS, toInsert);
		}
	}

	private void deleteEndpoint(endpointAxis axis, Endpoint toDelete) {
		if (axis == endpointAxis.X_AXIS)
			x_endpoints = deleteFromArray(x_endpoints, getIndexToDelete(x_endpoints, toDelete));
		else if (axis == endpointAxis.Y_AXIS)
			y_endpoints = deleteFromArray(y_endpoints, getIndexToDelete(y_endpoints, toDelete));
		else
			z_endpoints = deleteFromArray(z_endpoints, getIndexToDelete(z_endpoints, toDelete));
	}

	public void addAABB(AABB box) {
		Endpoint[] boxEndpoints = box.getEndpointObjects();
		this.addToArray(endpointAxis.X_AXIS, boxEndpoints[0], true);
		this.addToArray(endpointAxis.Y_AXIS, boxEndpoints[1], true);
		this.addToArray(endpointAxis.Z_AXIS, boxEndpoints[2], true);
		this.addToArray(endpointAxis.X_AXIS, boxEndpoints[3], false);
		this.addToArray(endpointAxis.Y_AXIS, boxEndpoints[4], false);
		this.addToArray(endpointAxis.Z_AXIS, boxEndpoints[5], false);
	}

	public void deleteAABB(AABB box) {
		Endpoint[] boxEndpoints = box.getEndpointObjects();
		this.deleteEndpoint(endpointAxis.X_AXIS, boxEndpoints[0]);
		this.deleteEndpoint(endpointAxis.Y_AXIS, boxEndpoints[1]);
		this.deleteEndpoint(endpointAxis.Z_AXIS, boxEndpoints[2]);
		this.deleteEndpoint(endpointAxis.X_AXIS, boxEndpoints[3]);
		this.deleteEndpoint(endpointAxis.Y_AXIS, boxEndpoints[4]);
		this.deleteEndpoint(endpointAxis.Z_AXIS, boxEndpoints[5]);
		pairManager.deleteAllWith(box.getParentEntity());
	}

	public void updateAABB(AABB box) {
		Endpoint[] boxEndpoints = box.getEndpointObjects();
		this.updateEndpoint(endpointAxis.X_AXIS, boxEndpoints[0]);
		this.updateEndpoint(endpointAxis.Y_AXIS, boxEndpoints[1]);
		this.updateEndpoint(endpointAxis.Z_AXIS, boxEndpoints[2]);
		this.updateEndpoint(endpointAxis.X_AXIS, boxEndpoints[3]);
		this.updateEndpoint(endpointAxis.Y_AXIS, boxEndpoints[4]);
		this.updateEndpoint(endpointAxis.Z_AXIS, boxEndpoints[5]);
	}

	/**
	 * This makes sure the arrays of end points are sorted properly for accurate
	 * preliminary collision detection. It should be called once every frame.
	 */
	public void updateArrays() {
		sort(x_endpoints);
		sort(y_endpoints);
		sort(z_endpoints);
	}

	/**
	 * This method will only be called when there is a chance of the entity e
	 * colliding with something. For example, if an entity is moving then it should
	 * update its end points with the updateAABB() method then it should call this
	 * one to determine if it has any possible collisions with other entities in the
	 * world. If there are collisions with other entities, then return the array of
	 * colliding entities in array format.
	 * 
	 * @param e
	 *            - the moving entity that is being tested for collisions.
	 * @return an array of all the entities that the given entity e is now possibly
	 *         overlapping.
	 */
	public Entity[] getBoxCollisions(Entity e) {
		updateAABB(e.getBoundingBox());
//		System.out.println(Arrays.toString(x_endpoints));
//		System.out.println(Arrays.toString(y_endpoints));
//		System.out.println(Arrays.toString(z_endpoints));
//		System.out.println(pairManager.toString());
		return pairManager.getCollidingEntities(e);
	}

	private Entity[] getOverlappingEntities(endpointAxis axis, Endpoint e1, Endpoint e2) {
		List<Entity> overlappingEntityList = new ArrayList<Entity>();
		if (axis == endpointAxis.X_AXIS) {
			for (int i = e1.getIndex() + 1; i < e2.getIndex(); i++)
				overlappingEntityList.add(x_endpoints[i].getParentBox().getParentEntity());

		} else if (axis == endpointAxis.Y_AXIS) {
			for (int i = e1.getIndex() + 1; i < e2.getIndex(); i++)
				overlappingEntityList.add(y_endpoints[i].getParentBox().getParentEntity());
		} else {
			for (int i = e1.getIndex() + 1; i < e2.getIndex(); i++)
				overlappingEntityList.add(z_endpoints[i].getParentBox().getParentEntity());
		}
		Entity[] entityArr = new Entity[overlappingEntityList.size()];
		for (int i = 0; i < entityArr.length; i++)
			entityArr[i] = overlappingEntityList.get(i);
		return entityArr;
	}

	private int getIndexToAdd(Endpoint[] arr, Endpoint val) {
		int start = 0;
		int end = arr.length;
		int middleIndex = 0;
		while (start < end) {
			middleIndex = (start + end) / 2;
			if (arr[middleIndex].getValue() > val.getValue() && end != middleIndex)
				end = middleIndex;
			else if (arr[middleIndex].getValue() < val.getValue() && start != middleIndex)
				start = middleIndex;
			else
				return middleIndex + 1;
		}
		return middleIndex;
	}

	private int getIndexToDelete(Endpoint[] arr, Endpoint val) {
		int start = 0;
		int end = arr.length;
		int middleIndex = 0;
		while (start < end) {
			middleIndex = (start + end) / 2;
			if (arr[middleIndex].getValue() > val.getValue() && end != middleIndex)
				end = middleIndex;
			else if (arr[middleIndex].getValue() < val.getValue() && start != middleIndex)
				start = middleIndex;
			else if (arr[middleIndex].getValue() == val.getValue())
				return middleIndex;
		}
		return middleIndex;
	}

	private Endpoint[] insertInArray(Endpoint[] arr, Endpoint val, int index) {
		Endpoint[] newArray = new Endpoint[arr.length + 1];
		for (int i = 0; i < index; i++)
			newArray[i] = arr[i];
		newArray[index] = val;
		newArray[index].setIndex(index);
		for (int i = index + 1; i < newArray.length; i++) {
			newArray[i] = arr[i - 1];
			newArray[i].setIndex(i);
		}
		return newArray;
	}

	private Endpoint[] deleteFromArray(Endpoint[] arr, int index) {
		if (arr.length > 0) {
			Endpoint[] newArray = new Endpoint[arr.length - 1];
			for (int i = 0; i < index; i++)
				newArray[i] = arr[i];
			for (int i = index; i < newArray.length; i++) {
				newArray[i] = arr[i + 1];
				newArray[i].setIndex(i);
			}
			return newArray;
		} else
			return new Endpoint[0];
	}

	// Using insertion sort because it is most efficient when the array is mostly
	// sorted which these ones are.
	private void sort(Endpoint[] arr) {
		int n = arr.length;
		for (int i = 1; i < n; ++i) {
			Endpoint key = arr[i];
			int j = i - 1;
			while (j >= 0 && arr[j].getValue() > key.getValue()) {
				arr[j + 1] = arr[j];
				arr[j + 1].setIndex(j + 1);
				j = j - 1;
			}
			arr[j + 1] = key;
			arr[j + 1].setIndex(j + 1);
		}
	}
}