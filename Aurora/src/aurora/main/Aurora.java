package aurora.main;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import aurora.mainmenu.RenderMainMenu;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.models.ModelManager;
import engine.rendering.textures.TextureManager;
import engine.util.Engine;

public class Aurora extends Engine {
	
	private GUIText positonVec;
	private GUIText message;
	private GUIText time;
	
	public Aurora() {
		this.testWorld = true;
		
		this.start("Aurora", true);
	}

	@Override
	protected void loadResources() {
		TextureManager.loadTexture("dudvMap", "extras");
		TextureManager.loadTexture("normalMap", "extras");

		TextureManager.loadTexture("TestGui", "gui");

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
		positonVec = new GUIText("Position", 2.5f, FontManager.font("papyrus"), 0, 0);
		positonVec.setColour(1, 0.45f, 0);
		FontManager.loadText(positonVec);
		
		time = new GUIText("Time", 2.5f, FontManager.font("papyrus"), 700, 0);
		time.setColour(1, 0.45f, 0);
		FontManager.loadText(time);
		
		message = new GUIText("Press Tab to Load World", 5f, FontManager.font("tempus"), 250, 150);
		message.setColour(1, 0, 0);
		FontManager.loadText(message);
		
		mode = new RenderMainMenu();
	}

	@Override
	protected void loop() {
		if(this.isWorldCreated()) {
			Vector3f pos = Engine.getCamera().getPosition();
			positonVec.setText((int)pos.x + ", " + (int)pos.y + ", " + (int)pos.z);
			
			float theTime = world.getWorldTime();
			time.setText((int)(theTime) + ":" + (int)(theTime % 1 * 100));
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			FontManager.removeText(message);
			this.renderWorld();
		}
	}
	
	public static void main(String[] args) {
		new Aurora();
	}
}
