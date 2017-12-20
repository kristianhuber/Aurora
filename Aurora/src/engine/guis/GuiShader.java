package engine.guis;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import engine.rendering.ShaderProgram;

public class GuiShader extends ShaderProgram {

	private static final String FRAGMENT_FILE = "/engine/guis/GuiFragmentShader.txt";
	private static final String VERTEX_FILE = "/engine/guis/GuiVertexShader.txt";

	private int location_transformationMatrix;
	private int location_colored;
	private int location_color;

	/* Constructor: Passes the file locations of the shaders to the super class */
	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/* Gives the vertex shader in variables */
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	/* Assigns the variable(s) an integer where to upload in memory */
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_colored = super.getUniformLocation("colored");
		location_color = super.getUniformLocation("color");
	}
	
	/* Loads a transformation matrix to the vertex shader */
	public void loadTransformation(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadColorData(boolean colored, Vector4f color) {
		super.loadBoolean(location_colored, colored);
		super.load4DVector(location_color, color);
	}
}