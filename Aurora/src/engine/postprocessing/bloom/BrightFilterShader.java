package engine.postprocessing.bloom;

import engine.rendering.ShaderProgram;

public class BrightFilterShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "/engine/postprocessing/BasicVertexShader.txt";
	private static final String FRAGMENT_FILE = "/engine/postprocessing/bloom/brightFilterFragment.txt";
	
	public BrightFilterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
