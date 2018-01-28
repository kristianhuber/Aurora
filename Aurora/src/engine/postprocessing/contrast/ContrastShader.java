package engine.postprocessing.contrast;

import engine.rendering.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/engine/postprocessing/BasicVertexShader.txt";
	private static final String FRAGMENT_FILE = "/engine/postprocessing/contrast/contrastFragment.txt";
	
	public ContrastShader() {
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
