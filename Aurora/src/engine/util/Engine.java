package engine.util;

import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.rendering.MasterRenderer;
import engine.rendering.models.ModelManager;
import engine.rendering.textures.TextureManager;
import engine.world.World;
import engine.world.entities.Camera;

/**
 * @Description: Main Class
 * 
 * */

public abstract class Engine {
	private final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static int WIDTH, HEIGHT;

	protected static Camera camera;
	
	protected boolean testWorld;
	protected World world;
	
	public abstract class RenderMode {
		
		public abstract void render();
	}
	
	/* Constructor Method */
	public Engine() {
		Engine.HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		Engine.WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

		Engine.lastFrameTime = 0;
		Engine.delta = 0;

		this.testWorld = false;
	}
	
	protected void start(String title, boolean fullScreen) {
		this.createDisplay(title, fullScreen);
		this.loadResources();
		this.startRendering();
		this.cleanUp();
	}

	/* Set up the Window */
	private void createDisplay(String title, boolean fullScreen) {
		try {
			if (fullScreen) {
				System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			} else {
				Engine.HEIGHT *= 0.95f;
				Engine.WIDTH *= 0.95f;
			}

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setVSyncEnabled(true);
			Display.sync(this.FPS_CAP);
			Display.setTitle(title);

			Display.create(new PixelFormat().withDepthBits(24), new ContextAttribs(3, 3));
			
			//Enable multi-sampling textures
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	/* Start Rendering */
	private void startRendering() {
		world = new World(testWorld);
		
		camera = new Camera(world);

		MasterRenderer.initialize();
		FontManager.initialize();
		GuiRenderer.initalize();

		Engine.showMouse(false);
		
		this.preRender();
		
		// Main Loop
		while (!Display.isCloseRequested()) {
			this.loop();
			
			camera.move();
			
			world.update();

			MasterRenderer.renderWorld(world);
			
			GuiRenderer.render();
			FontManager.render();
			
			this.loop();
			
			updateDisplay();
		}
	}
	
	/* Keep Game Updated */
	private void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();

		long currentFrameTime = getCurrentTime();
		Engine.delta = (currentFrameTime - Engine.lastFrameTime) / 1000f;
		Engine.lastFrameTime = currentFrameTime;
	}

	/* Clean Up Memory */
	private void cleanUp() {
		MasterRenderer.cleanUp();
		TextureManager.cleanUp();
		ModelManager.cleanUp();
		GuiRenderer.cleanUp();
		FontManager.cleanUp();
		Display.destroy();
	}
		
	/* Do stuff after rendering */
	protected abstract void preRender();
	
	/* Do stuff while rendering */
	protected abstract void loop();
	
	/* Load All Your Resources Here */
	protected abstract void loadResources();
	
	/* Toggles if the mouse is shown */
	public static void showMouse(boolean show) {
		Mouse.setGrabbed(!show);
	}

	/* Returns the difference in time between renders */
	public static float getDelta() {
		return delta;
	}

	/* Gets the system time in seconds */
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	public static Camera getCamera() {
		return Engine.camera;
	}
}
