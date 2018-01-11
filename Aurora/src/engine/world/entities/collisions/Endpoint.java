package engine.world.entities.collisions;

public class Endpoint {
	
	private AABB parentBox = null;
	private float value = 0;
	private int index = -1;
	private boolean isMin = false;
	
	public Endpoint(AABB parentBox, float value, boolean isMin) {
		super();
		this.parentBox = parentBox;
		this.value = value;
		this.isMin = isMin;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public AABB getParentBox() {
		return parentBox;
	}

	public boolean isMin() {
		return isMin;
	}
	
	public enum endpointAxis {
		X_AXIS, Y_AXIS, Z_AXIS
	}
	
	public String toString() {
		return "(" + value + ", " + index + ", " + parentBox.getParentEntity().getID() + ")";
	}

}
