package engine.world;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.rendering.MasterRenderer;
import engine.rendering.RenderMode;

public class RenderWorld extends RenderMode{

	private World world;
	
	public RenderWorld(World world) {
		this.world = world;
	}

	@Override
	public void render() {
		world.update();

		MasterRenderer.renderWorld(world);
		
		GuiRenderer.render();
		FontManager.render();
	}
}
