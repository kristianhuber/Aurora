package engine.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import engine.rendering.ShaderProgram;

public class Effect {

	protected ImageRenderer renderer;
	protected ShaderProgram shader;
	
	public Effect(ShaderProgram program, int width, int height) {
		renderer = new ImageRenderer(width, height);
		shader = program;
	}
	
	public Effect(ShaderProgram program) {
		renderer = new ImageRenderer();
		shader = program;
	}
	
	public void render(int texture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
	}
	
	public int getOutputTexture() {
		return renderer.getOutputTexture();
	}
	
	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}
}
