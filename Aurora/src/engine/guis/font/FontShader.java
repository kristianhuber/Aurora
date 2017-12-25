package engine.guis.font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/engine/guis/font/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/engine/guis/font/fontFragment.txt";
	
	private int location_colour;
	private int location_translation;
	private int location_secondaryColor;
	private int location_mode;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_mode = super.getUniformLocation("mode");
		location_secondaryColor = super.getUniformLocation("secondaryColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadMode(int mode, Vector3f secondary) {
		this.loadVector(location_secondaryColor, secondary);
		this.loadInt(location_mode, mode);
	}
	
	protected void loadColour(Vector3f colour){
		super.loadVector(location_colour, colour);
	}
	
	protected void loadTranslation(Vector2f translation){
		super.load2DVector(location_translation, translation);
	}


}
