package engine.animation.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import engine.animation.animatedModel.AnimatedModel;
import engine.rendering.models.TexturedModel;
import engine.rendering.textures.TextureManager;
import engine.util.Calculator;
import engine.util.Engine;

public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	public void render(AnimatedModel entity, Vector3f lightDir) {
		prepare(entity, lightDir);
		shader.loadJointTransforms(entity.getJointTransforms());
		shader.loadTransformationMatrix(
				Calculator.createTransformationMatrix(entity.getPosition(), entity.getRotation(), 1));
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getVertexCount(), GL11.GL_UNSIGNED_INT,
				0);
		finish();
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as well as
	 * the light direction. Enables and disables a few settings which should be
	 * pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void prepare(AnimatedModel model, Vector3f lightDir) {
		shader.start();
		shader.loadProjectionViewMatrix(Calculator.createViewMatrix(Engine.getCamera()));
		shader.loadLightDirection(lightDir);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTexture(model.getTexture()).getID());
		
		// Entity Stuff
		GL30.glBindVertexArray(model.getModel());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
	}

	private void finish() {
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);

		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}

}
