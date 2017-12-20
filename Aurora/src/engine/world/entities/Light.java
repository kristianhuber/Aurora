package engine.world.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * @Description: Holds lighting information
 * 
 * */

public class Light {

	private Vector3f attenuation = new Vector3f(1, 0, 0);
	private Vector3f position, color;
	
	/* Main Constructor Method */
	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	/* Basic Constructor Method */
	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}
	
	/* Returns how far the light should shine */
	public Vector3f getAttenuation(){
		return attenuation;
	}
	
	/* Returns the position */
	public Vector3f getPosition() {
		return position;
	}

	/* Sets the position */
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	/* Returns the light color */
	public Vector3f getColor() {
		return color;
	}

	/* Sets the light color */
	public void setColor(Vector3f color) {
		this.color = color;
	}

}
