package engine.world.skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.MasterRenderer;
import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;
import engine.rendering.textures.TextureManager;
import engine.world.World;
import engine.world.terrain.Terrain;

/**
 * @Description: Draws the skybox in the world
 * 
 */

public class SkyboxRenderer {

	private static final float SIZE = 900F;
	private static final float[] VERTICES = { -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,
			-SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE,
			-SIZE, SIZE,

			SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE,
			-SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE,
			SIZE,

			-SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE,
			-SIZE,

			-SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE,
			-SIZE, SIZE };

	private SkyboxShader shader;
	private RawModel cube;

	/* Constructor Method */
	public SkyboxRenderer() {

		// Loads a cube model
		cube = ModelManager.loadToVAO(VERTICES, 3);

		// Creates a new shader and initializes it
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		shader.stop();
	}

	/* Renders the skybox in the world */
	public void render(Vector3f fogColor, String textureA, String textureB, float blend) {

		shader.start();

		// Load variables
		shader.loadViewMatrix();
		shader.loadFogColor(fogColor);

		// Bind textures
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);

		this.bindTextures(textureA, textureB, blend);

		// Draw
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());

		// Disable textures and attributes
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);

		shader.stop();
	}

	/* Sets the texture for the skybox */
	private void bindTextures(String textureA, String textureB, float blendFactor) {

		// Get the textures
		int texture1 = TextureManager.getCubeMap(textureA);
		int texture2 = TextureManager.getCubeMap(textureB);

		// Load the two textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);

		// Load the blend factor to the shader
		shader.loadBlendFactor(blendFactor);
	}
}
