package aurora.mainmenu;

import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.util.RenderMode;

public class RenderMainMenu extends RenderMode{

	public RenderMainMenu() {
		
	}
	
	@Override
	public void render() {
		GuiRenderer.render();
		FontManager.render();
	}
}
