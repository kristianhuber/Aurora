package aurora.mainmenu;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.guis.GuiRenderer;
import engine.guis.component.GuiComponent.ClickAction;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode {
	
	private MenuButton button, button2, button3, button4, button5;
	private Gui backgroundImage;
	private GUIText title;

	private Vector3f foregroundColor = new Vector3f(255 / 255f, 255 / 255f, 255 / 255f);
	
	public RenderMainMenu(Aurora aurora) {
		
		title = new GUIText("Aurora", 42, FontManager.font("cherokee"), 1225, 0);
		title.setColour(34 / 255f, 139 / 255f, 34 / 255f);
		title.setMode(GUIText.MODE_GLOWING);
		title.setSecondaryColor(new Vector3f(155/255f, 1, 155/255f));
		this.loadText(title);

		backgroundImage = new Gui("backgroundImage", 0, 0, Display.getWidth(), Display.getHeight());
		this.guis.add(0, backgroundImage);

		button = new MenuButton(this, "Birch2", 1100, 200, 750, 128);
		button.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.renderWorld();
			}
		});
		button.setText("Single Player");
		button.setForegroundColor(foregroundColor);
		this.addGui(1, button);

		button2 = new MenuButton(this, "Birch2", 1100, 360, 750, 128);
		button2.setText("Scenarios");
		button2.setForegroundColor(foregroundColor);
		this.addGui(1, button2);

		button3 = new MenuButton(this, "Birch2", 1100, 520, 750, 128);
		button3.setText("Multiplayer");
		button3.setForegroundColor(foregroundColor);
		this.addGui(1, button3);

		button4 = new MenuButton(this, "Birch2", 1100, 680, 750, 128);
		button4.setText("Options");
		button4.setForegroundColor(foregroundColor);
		this.addGui(1, button4);

		button5 = new MenuButton(this, "Birch2", 1100, 840, 750, 128);
		button5.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.endProgram();
			}
		});
		button5.setText("Exit");
		button5.setForegroundColor(foregroundColor);
		this.addGui(1, button5);
	}

	@Override
	public void initialize() {
		this.showMouse(true);
	}

	@Override
	public void render() {
		this.resetScreen();
		this.guis.update();
		GuiRenderer.render(this.guis);
		FontManager.render(this.texts);
	}
}
