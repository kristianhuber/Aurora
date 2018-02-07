package engine.animation.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.animation.animatedModel.AnimatedModel;
import engine.rendering.MasterRenderer;
import engine.util.Calculator;
import engine.util.Engine;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the joint
	 * transforms are loaded up to the shader to a uniform array. Also 5 attributes
	 * of the VAO are enabled before rendering, to include joint indices and
	 * weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void render(AnimatedModel entity, Vector3f lightDir) {
		prepare(lightDir);
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.loadJointTransforms(entity.getJointTransforms());
		shader.loadTransformationMatrix(
				Calculator.createTransformationMatrix(new Vector3f(2048, 500, 2048), new Vector3f(0, 0, 0), 1));
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
		finish();
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
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
	private void prepare(Vector3f lightDir) {
		shader.start();
		shader.loadProjectionViewMatrix(Calculator.createViewMatrix(Engine.getCamera()));
		shader.loadLightDirection(lightDir);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.stop();
	}

}
