package engine.world.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.models.TexturedModel;
import engine.util.Calculator;
import engine.world.World;
import engine.world.entities.collisions.AABB;

/**
 * @Description: Main class for objects in the game
 * 
 */

public class Entity {

	protected Vector3f position, rotation;
	protected World world;

	private TexturedModel model;
	private boolean selected;
	private int textureIndex;
	private float scale;
	
	private AABB boundingBox;
	private Matrix4f transformationMatrix;

	private String ID;

	/* Main Constructor Method */
	public Entity(World w, String ID, String texture, Vector3f position, Vector3f rotation) {
		this.model = new TexturedModel(ID, texture);
		this.position = position;
		this.rotation = rotation;
		this.textureIndex = 0;
		this.world = w;
		this.scale = 1;
		this.ID = ID;
		updateTransformationMatrix();
	}
	
	private void updateTransformationMatrix() {
		transformationMatrix = Calculator.createTransformationMatrix(position, rotation, scale);
		boundingBox = new AABB(model.getRawModel().getModelData().calculateAABB(position, rotation, scale, transformationMatrix));
	}

	public String getID() {
		return ID;
	}

	/* Constructor Method for basic entities with a texture */
	public Entity(World w, String ID, String texture, float x, float z) {
		this(w, ID, texture, new Vector3f(x, w.getTerrainHeightAt(x, z), z), new Vector3f(0, 0, 0));
	}

	/* Constructor Method for basic entities */
	public Entity(World w, String ID, float x, float z) {
		this(w, ID, ID, new Vector3f(x, w.getTerrainHeightAt(x, z), z), new Vector3f(0, 0, 0));
	}

	/* Constructor Method for basic entities with a vector */
	public Entity(World w, String ID, Vector3f position) {
		this(w, ID, ID, position, new Vector3f(0, 0, 0));
	}

	/* Increase the position easily */
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		updateTransformationMatrix();
	}

	/* Increase the rotation easily */
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
		updateTransformationMatrix();
	}

	/* Calculates the x coordinate on texture map */
	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	/* Calculates the y coordinate on the texture map */
	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	/* Set the position with 3 floats */
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		updateTransformationMatrix();
	}

	/* Set the position with 2 floats */
	public void setPosition(float x, float z) {
		this.position.x = x;
		this.position.y = this.world.getTerrainHeightAt(x, z);
		this.position.z = z;
		updateTransformationMatrix();
	}

	/* Return the position vector */
	public Vector3f getPosition() {
		return position;
	}

	/* Set the rotation with 3 floats */
	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
		updateTransformationMatrix();
	}

	/* Return the rotation vector */
	public Vector3f getRotation() {
		return rotation;
	}

	/* Set the scale of the entity */
	public void setScale(float scale) {
		this.scale = scale;
		updateTransformationMatrix();
	}

	/* Get the scale of the entity */
	public float getScale() {
		return scale;
	}

	/* Get the texture on the model */
	public TexturedModel getModel() {
		return model;
	}

	public void setSelection(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}
}