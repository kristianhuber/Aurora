package engine.world.terrain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.rendering.MasterRenderer;
import engine.rendering.models.RawModel;
import engine.rendering.textures.TerrainTexturePack;
import engine.util.Calculator;
import engine.util.Engine;
import engine.world.World;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer() {
		this.shader = new TerrainShader();
		shader.start();
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(World world, Matrix4f toShadowSpace, Vector4f clipPlane) {
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(world.getSkyColor());
		shader.loadLights(world.getLights());
		shader.loadViewMatrix(Engine.getCamera());
		shader.loadShadowMapSize();
		shader.loadShadowDistance();
		shader.loadFogSettings();
		shader.loadSeaLevel(world.getSeaLevel());

		shader.loadAToShadowSpaceMatrix(toShadowSpace);
		for (Terrain terrain : world.getRenderedTerrains()) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);

			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

			unbindTexturedModel();
		}

		shader.stop();
	}

	private void prepareTerrain(Terrain terrain) {

		RawModel rawModel = terrain.getModel();

		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}

	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture());

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRTexture());

		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGTexture());

		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBTexture());

		//GL13.glActiveTexture(GL13.GL_TEXTURE4);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Calculator
				.createTransformationMatrix(new Vector3f(terrain.getX(), terrain.getY(), terrain.getZ()), new Vector3f(0, 0, 0), 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
