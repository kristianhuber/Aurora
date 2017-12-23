package aurora.mainmenu;

import org.lwjgl.opengl.Display;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.guis.GuiRenderer;
import engine.guis.component.GuiButton;
import engine.guis.component.GuiComponent.ClickAction;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode {
	private GuiButton button, button2, button3, button4, button5;
	private Gui backgroundImage;
	private GUIText title;

	public RenderMainMenu(Aurora aurora) {
		title = new GUIText("Aurora", 42, FontManager.font("cherokee"), 610, 10);
		// title.setColour(121/255f, 76/255f, 19/255f);
		title.setColour(34 / 255f, 139 / 255f, 34 / 255f);
		this.loadText(title);

		backgroundImage = new Gui("backgroundImage", 0, 0, Display.getWidth(), Display.getHeight());
		this.guis.add(0, backgroundImage);

		button = new GuiButton(this, "Birch2", 1100, 200, 750, 128);
		button.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.renderWorld();
			}
		});
		//button.setText("Testing");
		this.addGui(1, button);

		button2 = new GuiButton(this, "Birch2", 1100, 360, 750, 128);
		this.addGui(1, button2);

		button3 = new GuiButton(this, "Birch2", 1100, 520, 750, 128);
		this.addGui(1, button3);

		button4 = new GuiButton(this, "Birch2", 1100, 680, 750, 128);
		this.addGui(1, button4);

		button5 = new GuiButton(this, "Birch2", 1100, 840, 750, 128);
		button5.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.endProgram();
			}
		});
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
