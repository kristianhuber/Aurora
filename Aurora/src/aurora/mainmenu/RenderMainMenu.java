package aurora.mainmenu;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode{

	public RenderMainMenu() {
		
	}
	
	@Override
	public void render() {
		GuiRenderer.render();
		FontManager.render();
	}
}
