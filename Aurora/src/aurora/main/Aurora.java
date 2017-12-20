package aurora.main;

import org.lwjgl.util.vector.Vector3f;

import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.models.ModelManager;
import engine.rendering.textures.TextureManager;
import engine.util.Engine;
import engine.util.MousePicker;

public class Aurora extends Engine {

	private MousePicker picker;
	private GUIText positonVec;
	private GUIText mouseVec;
	
	public Aurora() {
		super("Aurora", true);
	}

	@Override
	protected void loadResources() {
		TextureManager.loadTexture("dudvMap", "extras");
		TextureManager.loadTexture("normalMap", "extras");

		TextureManager.loadTexture("papyrus", "extras");
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
	}

	@Override
	protected void preRender() {		
		positonVec = new GUIText("Position", 2.5f, FontManager.font("papyrus"), 0, 0);
		positonVec.setColour(1, 0.45f, 0);
		FontManager.loadText(positonVec);
		
		mouseVec = new GUIText("Mouse", 2.5f, FontManager.font("papyrus"), 700, 0);
		mouseVec.setColour(1, 0.45f, 0);
		FontManager.loadText(mouseVec);
		
		picker = new MousePicker(this.world, Engine.getCamera());
	}

	@Override
	protected void loop() {
		Vector3f pos = Engine.camera.getPosition();
		positonVec.setText((int)pos.x + ", " + (int)pos.y + ", " + (int)pos.z);
		
		picker.update();
		
		Vector3f pos2 = picker.getCurrentTerrainPoint();
		pos2.y = world.getTerrainHeightAt(pos2);
		mouseVec.setText((int)pos2.x + ", " + (int)pos2.y + ", " + (int)pos2.z);
	}
	
	public static void main(String[] args) {
		new Aurora();
	}
}
