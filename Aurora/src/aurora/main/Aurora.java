package aurora.main;

import org.lwjgl.input.Keyboard;

import aurora.menus.RenderMainMenu;
import aurora.menus.RenderSettingsScreen;
import engine.guis.font.FontManager;
import engine.rendering.RenderMode;
import engine.rendering.models.ModelManager;
import engine.rendering.textures.TextureManager;
import engine.util.Engine;

public class Aurora extends Engine {

	public RenderMode settingsScreen;
	public RenderMode mainMenu;
	
	// How are you supposed to quickly add GUIs in the world (need a better render
	// mode)?

	// Upgrade loading chunks?

	// Make a progress bar, slider

	// Make it so that text updates when GUI is resized

	public Aurora() {
		this.testWorld = false;
		this.start("Aurora", true, false);
	}

	@Override
	protected void loadResources() {

		// Texture Manager is for Textures, Model Manager is for Models, and MetaFile is
		// for Fonts

		// Load Water Resources:
		TextureManager.loadTexture("dudvMap", "textures\\water");
		TextureManager.loadTexture("normalMap", "textures\\water");
		
		// Load the default texture
		TextureManager.loadTexture("Default", "textures");

		// Load GUI Textures:
		TextureManager.loadTexture("backgroundImage", "guis");
		TextureManager.loadTexture("backgroundImage2", "guis");
		TextureManager.loadTexture("Birch2", "guis");
		TextureManager.loadTexture("Birch2_hover", "guis");
		TextureManager.loadTexture("progressbar", "guis");
		TextureManager.loadTexture("textfield", "guis");
		TextureManager.loadTexture("checkbox", "guis");
		TextureManager.loadTexture("checkbox_checked", "guis");

		// Load Terrain Textures:
		TextureManager.loadTexture("GrassTexture", "textures\\terrain");
		TextureManager.loadTexture("Mud", "textures\\terrain");
		TextureManager.loadTexture("Flowers", "textures\\terrain");
		TextureManager.loadTexture("Stone", "textures\\terrain");
		TextureManager.loadTexture("sand", "textures\\terrain");
		TextureManager.createTerrainTexturePack("default", "GrassTexture", "sand", "Flowers", "Stone");

		// Load Particle Textures:
		TextureManager.loadTexture("cosmic", "textures\\particles");
		
		// Load Cube Maps:
		TextureManager.loadCubeMap("day");
		TextureManager.loadCubeMap("night");

		// Load TexturedModels:
		ModelManager.loadTexturedModel("stall");
		ModelManager.loadTexturedModel("stall1");
		ModelManager.loadTexturedModel("betterpine");

		// Load Fonts:
		FontManager.addFont("papyrus");
		FontManager.addFont("tempus");
		FontManager.addFont("cherokee");
	}

	@Override
	protected void preRender() {
		settingsScreen = new RenderSettingsScreen(this);
		mainMenu = new RenderMainMenu(this);
		
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
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			this.setRenderMode(mainMenu);
		}
	}

	public static void main(String[] args) {
		new Aurora();
	}
}
