package aurora.menus;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.rendering.RenderMode;

public class RenderSettingsScreen extends RenderMode {

	private Gui backgroundImage;
	
	public RenderSettingsScreen(Aurora aurora) {
		backgroundImage = new Gui("backgroundImage2", 0, 0, 1000, 750);
		this.guis.add(0, backgroundImage);
	}

	@Override
	public void render() {
		this.resetScreen();
		super.render();
	}
}
