package engine.guis;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import engine.rendering.textures.TextureManager;
import engine.util.Calculator;

public class Gui {
	private Vector2f position, scale;
	private int texture;
	private boolean colored = false;
	private Vector4f color = new Vector4f(0, 0, 0, 0);
	private String ID;
	private boolean hasFilter = false;

	public Gui(String texture, float x, float y, float width, float height) {
		this.scale = Calculator.toOpenGLScale(width, height);
		this.position = Calculator.toOpenGLCoordinates(x, y, scale.x, scale.y);
		this.texture = TextureManager.getTexture(texture).getID();
		this.ID = texture;
	}
	
	public Gui(Vector4f color, float x, float y, float width, float height) {
		this.scale = Calculator.toOpenGLScale(width, height);
		this.position = Calculator.toOpenGLCoordinates(x, y, scale.x, scale.y);
		this.color = color;
		this.colored = true;
		this.ID = color.toString();
	}
	
	public void update() {
		
	}
	
	public void setSize(float width, float height) {
		this.scale = Calculator.toOpenGLScale(width, height);
	}
	
	public void setWidth(float width) {
		this.scale.x = Calculator.toOpenGLScale(width, 0).x;
	}
	
	public void setHeight(float height) {
		this.scale.y = Calculator.toOpenGLScale(0, height).y;
	}

	public int getTexture() {
		return texture;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setTexture(String texture) {
		this.ID = texture;
		this.texture = TextureManager.getTexture(texture).getID();
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y) {
		this.position = Calculator.toOpenGLCoordinates(x, y, scale.x, scale.y);
	}

	public Vector2f getScale() {
		return scale;
	}
	
	public void setColored(boolean colored) {
		this.colored = colored;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public void addFilter(Vector4f color) {
		this.hasFilter = true;
		this.color = color;
	}
	
	public void removeFilter() {
		this.hasFilter = false;
	}
	
	public boolean hasFilter() {
		return hasFilter;
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public boolean isColored() {
		return colored;
	}
	
	@Override
	public String toString() {
		return ID;
	}
}
