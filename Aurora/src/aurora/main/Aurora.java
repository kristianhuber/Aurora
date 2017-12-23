package aurora.main;

import org.lwjgl.input.Keyboard;

import aurora.mainmenu.RenderMainMenu;
import engine.guis.font.FontManager;
import engine.rendering.RenderMode;
import engine.rendering.models.ModelManager;
import engine.rendering.textures.TextureManager;
import engine.util.Engine;

public class Aurora extends Engine {

	private RenderMode mainMenu;

	// How are you supposed to quickly add GUIs?
	// Need to make the GUI converter on a fixed scale
	public Aurora() {
		this.testWorld = true;
		this.start("Aurora", true);
	}
	
	@Override
	protected void loadResources() {
		TextureManager.loadTexture("dudvMap", "extras");
		TextureManager.loadTexture("normalMap", "extras");

		TextureManager.loadTexture("Default", "gui");
		TextureManager.loadTexture("backgroundImage", "gui");
		TextureManager.loadTexture("button", "gui");
		TextureManager.loadTexture("button_select", "gui");

		// Terrain Textures
		TextureManager.loadTexture("GrassTexture");
		TextureManager.loadTexture("Mud");
		TextureManager.loadTexture("Flowers");
		TextureManager.loadTexture("Stone");
		TextureManager.loadTexture("sand");
		TextureManager.createTerrainTexturePack("default", "GrassTexture", "sand", "Flowers", "Stone");

		// Entity and Particle Textures
		TextureManager.loadTexture("lamp");
		TextureManager.loadTexture("pine");
		TextureManager.loadTexture("Rock");
		TextureManager.loadTexture("cosmic");
		TextureManager.loadTexture("betterpine");

		// Load Cube Maps the Skybox
		TextureManager.loadCubeMap("day");
		TextureManager.loadCubeMap("night");

		// Set the Attributes to the Textures
		TextureManager.getTexture("lamp").setUseFakeLighting(true);
		TextureManager.getTexture("pine").setHasTransparency(true);
		TextureManager.getTexture("betterpine").setHasTransparency(true);

		// Loads the Models
		ModelManager.loadModel("betterpine");
		ModelManager.loadModel("Rock");
		ModelManager.loadModel("lamp");
		ModelManager.loadModel("pine");

		FontManager.addFont("papyrus");
		FontManager.addFont("tempus");
	}

	@Override
	protected void preRender() {
		mainMenu = new RenderMainMenu();
		this.setRenderMode(mainMenu);
	}

	@Override
	protected void loop() {
		if (this.isWorldCreated()) {
			if (!testWorld)
				Engine.getCamera().setFlying(false);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			this.renderWorld();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			this.setRenderMode(mainMenu);
		}
	}

	public static void main(String[] args) {
		new Aurora();
	}
}
