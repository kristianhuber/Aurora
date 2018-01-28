package engine.postprocessing.bloom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import engine.postprocessing.Effect;

public class CombineFilter extends Effect{
	
	public CombineFilter(){
		super(new CombineShader());
		
		shader.start();
		((CombineShader) shader).connectTextureUnits();
		shader.stop();
	}
	
	public void render(int colorTexture, int highlightTexture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		renderer.renderQuad();
		shader.stop();
	}
}
