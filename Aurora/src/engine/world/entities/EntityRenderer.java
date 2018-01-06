package engine.world.entities;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import aurora.main.Aurora;
import engine.rendering.MasterRenderer;
import engine.rendering.models.RawModel;
import engine.rendering.models.TexturedModel;
import engine.rendering.textures.ModelTexture;
import engine.util.Calculator;
import engine.util.Engine;
import engine.world.World;

/**
 * @Description: Renders entities in the game
 * 
 */

public class EntityRenderer {

	private StaticShader shader;
	private static float renderDistance = 700;

	/* Constructor Method */
	public EntityRenderer() {
		this.shader = new StaticShader();
		shader.start();
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		shader.stop();
	}

	/* Renders the entities in the world */
	public void render(World world, Map<TexturedModel, List<Entity>> entities,
			Matrix4f toShadowSpace, Vector4f clipPlane) {

		// Load the shader settings
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(world.getSkyColor());
		shader.loadLights(world.getLights());
		shader.loadViewMatrix(Engine.getCamera());
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		shader.loadFogSettings();

		Vector3f cameraCoords = Aurora.getCamera().getPosition();
		float renderDistanceSquare = renderDistance * renderDistance;

		synchronized (entities) {
			// Start rendering the entities
			for (TexturedModel model : entities.keySet()) {

				// Prepare this type of entity
				this.prepareTexturedModel(model);

				// Start rendering the instances of the type
				List<Entity> batch = entities.get(model);
				for (Entity entity : batch) {
					Vector3f entityPos = entity.getPosition();
					float dx = cameraCoords.x - entityPos.x;
					float dy = cameraCoords.y - entityPos.y;
					float dz = cameraCoords.z - entityPos.z;
					float distanceSquare = dx * dx + dy * dy + dz * dz;

					// Making sure all entities are within the render distance
					// before rendering
					// them. This overrides the level of detail because the
					// player will not be able
					// to see models at all past the render distance.
					if (distanceSquare < renderDistanceSquare) {

						// Preparing the entity to render.
						this.prepareInstance(entity);

						// If the entity has different levels of detail, then
						// render the correct one,
						// otherwise, render the only one it has.
						if (model.getRawModel().hasLevelsOfDetail()) {
							int[] renderComponents = model
									.getRawModel()
									.getIndexArrayStartAndLength(distanceSquare);
							GL11.glDrawElements(GL11.GL_TRIANGLES,
									renderComponents[1], GL11.GL_UNSIGNED_INT,
									renderComponents[0] * 4);
						} else
							GL11.glDrawElements(GL11.GL_TRIANGLES, model
									.getRawModel().getVertexCount(),
									GL11.GL_UNSIGNED_INT, 0);
					}
				}

				// Unload the model
				this.unbindTexturedModel();
			}
		}
		// Close the shader
		shader.stop();
	}

	/* Loads the settings to the shader for each model */
	private void prepareTexturedModel(TexturedModel model) {

		// Get the model
		RawModel rawModel = model.getRawModel();

		// Load up the model to OpenGL and enable VAOs
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Get the texture
		ModelTexture texture = model.getTexture();

		// Load the texture settings to the shader
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if (texture.hasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariable(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(),
				texture.getReflectivity());

		// Load the texture to OpenGL
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		shader.connectTextureUnits();
	}

	/* Disable the settings per model */
	private void unbindTexturedModel() {

		// Enable in case it was disabled
		MasterRenderer.enableCulling();

		// Disable the VAOs
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	/* Loads settings to the shader per entity instance */
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Calculator.createTransformationMatrix(
				entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(),
				entity.getTextureYOffset());
		shader.loadIsSelected(entity.isSelected());
	}

	/* Cleans up memory */
	public void cleanUp() {
		shader.cleanUp();
	}
}