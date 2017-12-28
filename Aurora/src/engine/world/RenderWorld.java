package engine.world;

import org.lwjgl.util.vector.Vector3f;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.MasterRenderer;
import engine.rendering.RenderMode;
import engine.util.Engine;

public class RenderWorld extends RenderMode{
	private GUIText positonVec;
	private GUIText time;
	private World world;
	
	public RenderWorld(World world) {
		this.world = world;
		
		positonVec = new GUIText("Position", 16, FontManager.font("papyrus"), 0, 0);
		positonVec.setColour(1, 0.45f, 0);
		this.loadText(positonVec);
		
		time = new GUIText("Time", 16, FontManager.font("papyrus"), 750, 0);
		time.setColour(1, 0.45f, 0);
		this.loadText(time);
	}

	@Override
	public void initialize() {
		this.showMouse(false);
	}
	
	@Override
	public void render() {		
		world.update();
		
		Vector3f pos = Engine.getCamera().getPosition();
		positonVec.setText((int) pos.x + ", " + (int) pos.y + ", " + (int) pos.z);

		float theTime = world.getWorldTime();
		int secondPart = (int) (theTime % 1 * 60);
		if (secondPart < 10) {
			time.setText((int) (theTime) + ":0" + secondPart);
		} else {
			time.setText((int) (theTime) + ":" + secondPart);
		}

		MasterRenderer.renderWorld(world);
		
		GuiRenderer.render(this.guis);
		FontManager.render(this.texts);
	}
}
