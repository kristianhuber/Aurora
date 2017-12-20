package engine.rendering.textures;

/**
 * @Description: Holds data for a texture
 * 
 */

public class ModelTexture {

	private boolean hasTransparency, useFakeLighting;
	private float reflectivity, shineDamper;
	private int numberOfRows, textureID;

	/* Constructor Method */
	public ModelTexture(int ID) {
		this.hasTransparency = false;
		this.useFakeLighting = false;
		this.numberOfRows = 1;
		this.reflectivity = 0;
		this.shineDamper = 1;
		this.textureID = ID;
	}

	/* Sets the number of rows on the texture */
	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	/* Returns the number of rows on the texture */
	public int getNumberOfRows() {
		return numberOfRows;
	}

	/* Returns the texture ID */
	public int getID() {
		return this.textureID;
	}

	/* Returns the dampness factor */
	public float getShineDamper() {
		return shineDamper;
	}

	/* Sets the damper factor */
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	/* Returns how reflective the texture is */
	public float getReflectivity() {
		return reflectivity;
	}

	/* Sets the reflectivity of the texture */
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	/* Returns if the texture has transparency */
	public boolean hasTransparency() {
		return hasTransparency;
	}

	/* Lets the texture know if there is transparency */
	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	/* Returns if should use fake lighting */
	public boolean useFakeLighting() {
		return useFakeLighting;
	}

	/* Sets whether or not should use fake lighting */
	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
}
