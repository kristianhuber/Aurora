package engine.postprocessing;

import org.lwjgl.opengl.GL11;

import aurora.main.Aurora;

public class ImageRenderer {

	private FBO fbo;

	public ImageRenderer(int width, int height) {
		this.fbo = new FBO(width, height, FBO.NONE);
	}
	
	public ImageRenderer() {
		this(Aurora.WIDTH, Aurora.HEIGHT);
	}
	
	public void renderQuad(boolean render) {
		if (!render) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (!render) {
			fbo.unbindFrameBuffer();
		}
		
		/*if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}*/
	}

	public int getOutputTexture() {
		return fbo.getColourTexture();
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}
