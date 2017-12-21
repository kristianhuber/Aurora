package engine.world;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.rendering.MasterRenderer;
import engine.util.Engine;
import engine.util.RenderMode;

public class RenderWorld extends RenderMode{

	private World world;
	
	public RenderWorld(World world) {
		this.world = world;
	}

	@Override
	public void render() {
		Engine.getCamera().move();
		
		world.update();

		MasterRenderer.renderWorld(world);
		
		GuiRenderer.render();
		FontManager.render();
	}
}
