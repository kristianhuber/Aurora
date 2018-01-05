package aurora.menus;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.guis.component.GuiCheckbox;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderSettingsScreen extends RenderMode {

	private GuiCheckbox anisotropic;
	private Gui backgroundImage;
	private GUIText title;
	
	public RenderSettingsScreen(Aurora aurora) {
		backgroundImage = new Gui("backgroundImage2", 0, 0, 1000, 750);
		this.guis.add(0, backgroundImage);
		
		title = new GUIText("Settings", 36, FontManager.font("papyrus"), 500, 10, true);
		title.setColor(96, 32, 0);
		this.addText(title);
		
		anisotropic = new GuiCheckbox(this, "checkbox", 10, 100, 16, 16);
		anisotropic.setText("Enable Anisotropic Filtering");
		this.addGui(anisotropic);
	}

	@Override
	public void render() {
		this.resetScreen();
		super.render();
	}
}
