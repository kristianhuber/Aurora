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

	// How are you supposed to quickly add GUIs in the world (need a better render mode)?
	
	// Need to make loading chunks
	
	// Make a progress bar, slider, check box
	
	// Make it so that text updates when GUI is resized
	
	public Aurora() {
		this.testWorld = false;
		this.start("Aurora", true, false);
	}
	
	@Override
	protected void loadResources() {
		TextureManager.loadTexture("dudvMap", "extras");
		TextureManager.loadTexture("normalMap", "extras");

		TextureManager.loadTexture("Default", "gui");
		TextureManager.loadTexture("backgroundImage", "gui");
		TextureManager.loadTexture("button", "gui");
		TextureManager.loadTexture("button_hover", "gui");
		TextureManager.loadTexture("Birch2", "gui");
		TextureManager.loadTexture("Birch2_hover", "gui");
		TextureManager.loadTexture("progressbar", "gui");
		TextureManager.loadTexture("textfield", "gui");
		
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
		
		//Load TexturedModels:
		ModelManager.loadModel2("stallRev10");
		TextureManager.loadEntityTexture("stallRev10");

		FontManager.addFont("papyrus");
		FontManager.addFont("tempus");
		FontManager.addFont("cherokee");
	}

	@Override
	protected void preRender() {
		mainMenu = new RenderMainMenu(this);
		this.setRenderMode(mainMenu);
	}

	@Override
	protected void loop() {
		if (this.isWorldCreated()) {
			if (!testWorld)
				Engine.getCamera().setFlying(true);
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
