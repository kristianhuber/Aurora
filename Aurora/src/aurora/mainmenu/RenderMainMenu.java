package aurora.mainmenu;

import org.lwjgl.opengl.Display;

import engine.guis.Gui;
import engine.guis.GuiRenderer;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode{
	private Gui button, button2, button3, button4, button5;
	private Gui backgroundImage;
	private GUIText title;
	
	public RenderMainMenu() {
		title = new GUIText("Aurora", 45, FontManager.font("tempus"), 665, 10);
		title.setColour(0, 1, 0.45f);
		this.loadText(title);
		
		backgroundImage = new Gui("backgroundImage", 0, 0, Display.getWidth(), Display.getHeight());
		this.guis.add(0, backgroundImage);
		
		button = new Gui("button", 1150, 200, 750, 128);
		this.addGui(1, button);
		
		button2 = new Gui("button_select", 1150, 360, 750, 128);
		this.addGui(1, button2);
		
		button3 = new Gui("button", 1150, 520, 750, 128);
		this.addGui(1, button3);
		
		button4 = new Gui("button_select", 1150, 680, 750, 128);
		this.addGui(1, button4);
		
		button5 = new Gui("button", 1150, 840, 750, 128);
		this.addGui(1, button5);
	}
	
	@Override
	public void initialize() {
		this.showMouse(true);
	}
	
	@Override
	public void render() {
		this.resetScreen();
		GuiRenderer.render(this.guis);
		FontManager.render(this.texts);
	}
}
