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

	public Gui(String texture, float x, float y, float width, float height) {
		this.position = Calculator.toOpenGLCoordinates(x, y, width, height);
		this.texture = TextureManager.getTexture(texture).getID();
		this.scale = Calculator.toOpenGLScale(width, height);
	}
	
	public Gui(Vector4f color, float x, float y, float width, float height) {
		this.scale = Calculator.toOpenGLScale(width, height);
		this.position = Calculator.toOpenGLCoordinates(x, y, scale.x, scale.y);
		this.color = color;
		this.colored = true;
	}

	public int getTexture() {
		return texture;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(int x, int y) {
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
	
	public Vector4f getColor() {
		return color;
	}
	
	public boolean isColored() {
		return colored;
	}
}
