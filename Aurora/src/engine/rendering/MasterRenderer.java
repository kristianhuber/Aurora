package engine.rendering;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.animation.renderer.AnimatedModelRenderer;
import engine.postprocessing.FBO;
import engine.postprocessing.PostProcessing;
import engine.util.Engine;
import engine.world.World;
import engine.world.entities.Camera;
import engine.world.entities.EntityRenderer;
import engine.world.particles.ParticleMaster;
import engine.world.shadows.ShadowMapMasterRenderer;
import engine.world.skybox.SkyboxRenderer;
import engine.world.terrain.TerrainRenderer;
import engine.world.water.WaterRenderer;

/**
 * @Description: Handles all of the in-game rendering
 * 
 */

public class MasterRenderer {

	public static final float FOG_DENSITY = 0.0035F;
	public static final float FOG_GRADIENT = 8F;
	public static final float FOG_DIVIDER = 2.5F;

	public static final float NEAR_PLANE = 0.1F;
	public static final float FAR_PLANE = 1750F;
	public static final float FOV = 65F;

	private static Matrix4f projectionMatrix;

	private static boolean wireframes;

	private static FBO multisampledFBO;
	private static FBO outputFBO;

	private static ShadowMapMasterRenderer shadowMapRenderer;
	private static TerrainRenderer terrainRenderer;
	private static SkyboxRenderer skyboxRenderer;
	private static WaterRenderer waterRenderer;
	private static EntityRenderer renderer;

	private static AnimatedModelRenderer animatedRenderer;
	
	/* Initializes all of the variables */
	public static void initialize(boolean wireframes) {

		// Initialize the FBOs
		MasterRenderer.outputFBO = new FBO(Engine.WIDTH, Engine.HEIGHT, FBO.DEPTH_TEXTURE);
		MasterRenderer.multisampledFBO = new FBO(Engine.WIDTH, Engine.HEIGHT);

		// Create the Projection Matrix
		MasterRenderer.enableCulling();
		MasterRenderer.createProjectionMatrix();

		// Initialize the Renderers
		MasterRenderer.shadowMapRenderer = new ShadowMapMasterRenderer();
		MasterRenderer.terrainRenderer = new TerrainRenderer();
		MasterRenderer.skyboxRenderer = new SkyboxRenderer();
		MasterRenderer.waterRenderer = new WaterRenderer();
		MasterRenderer.renderer = new EntityRenderer();
		
		MasterRenderer.animatedRenderer = new AnimatedModelRenderer();

		ParticleMaster.initialize();
		PostProcessing.initialize();

		MasterRenderer.wireframes = wireframes;
	}

	public static void renderWorld(World world) {
		// Clearing the previous frame
		GL11.glClearColor(0, 0.5f, 1, 1);

		// setting render mode to wire frames if appropriate:
		if (wireframes) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		// Rendering shadows:
		MasterRenderer.shadowMapRenderer.render(world.getEntities(), world.getLights().get(0));
		ParticleMaster.update();
		Camera camera = Engine.getCamera();

		// Rendering Water
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		waterRenderer.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		MasterRenderer.renderScene(world, new Vector4f(0, 1, 0, world.getSeaLevel() + 0.5f));
		camera.invertPitch();
		camera.getPosition().y += distance;
		waterRenderer.bindRefractionFrameBuffer();
		MasterRenderer.renderScene(world, new Vector4f(0, -1, 0, world.getSeaLevel() + 1.0f));
		waterRenderer.unbindCurrentFrameBuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

		// Rendering actual world
		MasterRenderer.multisampledFBO.bindFrameBuffer();
		MasterRenderer.renderScene(world, new Vector4f(0, -1, 0, 100000));
		MasterRenderer.waterRenderer.render(world, world.getLights().get(0), world.getSkyColor());

		// Rendering particles
		ParticleMaster.renderParticles();
		MasterRenderer.multisampledFBO.unbindFrameBuffer();
		MasterRenderer.multisampledFBO.resolveToFbo(MasterRenderer.outputFBO);

		if (wireframes) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}

		// Post Processing
		PostProcessing.doPostProcessing(outputFBO.getColourTexture());
	}

	public static void renderScene(World world, Vector4f clipPlane) {
		MasterRenderer.prepare(world.getSkyColor());

		MasterRenderer.renderer.render(world, world.getEntities(),
				MasterRenderer.shadowMapRenderer.getToShadowMapSpaceMatrix(), clipPlane);

		MasterRenderer.terrainRenderer.render(world, MasterRenderer.shadowMapRenderer.getToShadowMapSpaceMatrix(),
				clipPlane);
		
		
		
		MasterRenderer.animatedRenderer.render(world.entity, new Vector3f(0, 1, 0));
		
		

		if (world.getWorldTime() < 6 || world.getWorldTime() > 20) {
			MasterRenderer.skyboxRenderer.render(world.getSkyColor(), "day", "night", 1);
		} else {
			MasterRenderer.skyboxRenderer.render(world.getSkyColor(), "day", "night", 0);
		}
	}

	public static void prepare(Vector3f skyColor) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	private static void createProjectionMatrix() {
		MasterRenderer.projectionMatrix = new Matrix4f();

		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		MasterRenderer.projectionMatrix.m00 = x_scale;
		MasterRenderer.projectionMatrix.m11 = y_scale;
		MasterRenderer.projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		MasterRenderer.projectionMatrix.m23 = -1;
		MasterRenderer.projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		MasterRenderer.projectionMatrix.m33 = 0;
	}

	public static Matrix4f getProjectionMatrix() {
		return MasterRenderer.projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public static int getShadowMapTexture() {
		return MasterRenderer.shadowMapRenderer.getShadowMap();
	}

	public static void cleanUp() {
		MasterRenderer.shadowMapRenderer.cleanUp();
		MasterRenderer.terrainRenderer.cleanUp();
		MasterRenderer.waterRenderer.cleanUp();
		MasterRenderer.renderer.cleanUp();

		MasterRenderer.animatedRenderer.cleanUp();
		
		MasterRenderer.multisampledFBO.cleanUp();
		MasterRenderer.outputFBO.cleanUp();

		ParticleMaster.cleanUp();
		PostProcessing.cleanUp();
	}
}
