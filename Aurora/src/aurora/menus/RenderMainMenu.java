package aurora.menus;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.guis.GuiRenderer;
import engine.guis.component.GuiCheckbox;
import engine.guis.component.GuiComponent;
import engine.guis.component.GuiComponent.ClickAction;
import engine.guis.component.GuiProgressBar;
import engine.guis.component.GuiTextField;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode {

	private MenuButton button, button2, button3, button4, button5;
	private Gui backgroundImage;
	private GuiProgressBar bar;
	private GuiTextField field;
	private GuiCheckbox box;
	private GUIText title;

	private Vector3f foregroundColor = new Vector3f(255 / 255f, 255 / 255f, 255 / 255f);

	public RenderMainMenu(Aurora aurora) {
		
		backgroundImage = new Gui("backgroundImage", 0, 0, 1000, 750);
		this.guis.add(0, backgroundImage);
		
		title = new GUIText("Aurora", 40, FontManager.font("cherokee"), 675, 25, false);
		title.setColour(34 / 255f, 139 / 255f, 34 / 255f);
		title.setMode(GUIText.MODE_GLOWING);
		title.setSecondaryColor(new Vector3f(155 / 255f, 1, 155 / 255f));
		this.loadText(title);

		
		//Menu Buttons 
		button = new MenuButton(this, 600, 150, 400, 90);
		button.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.renderWorld();
			}
		});
		button.setText("Single Player");
		button.setForegroundColor(foregroundColor);
		this.addGui(button);
		
		button2 = new MenuButton(this, 600, 250, 400, 90);
		button2.setText("Scenarios");
		button2.setForegroundColor(foregroundColor);
		this.addGui(button2);

		button3 = new MenuButton(this, 600, 350, 400, 90);
		button3.setText("Multiplayer");
		button3.setForegroundColor(foregroundColor);
		button3.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				bar.increaseProgress(-5);
			}
		});
		this.addGui(button3);

		button4 = new MenuButton(this, 600, 450, 400, 90);
		button4.setText("Options");
		button4.setForegroundColor(foregroundColor);
		button4.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				bar.increaseProgress(5);
			}
		});
		this.addGui(button4);

		button5 = new MenuButton(this, 600, 550, 400, 90);
		button5.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.endProgram();
			}
		});
		button5.setText("Exit");
		button5.setForegroundColor(foregroundColor);
		this.addGui(button5);
		
		//Experiments
		bar = new GuiProgressBar(this, "progressbar", 50, 50, 250, 50);
		bar.showProgress(true);
		bar.setForegroundColor(new Vector3f(1, 0, 0));
		this.addGui(bar);
		
		field = new GuiTextField(this, "textfield", 50, 110, 250, 50);
		this.addGui(field);
		
		box = new GuiCheckbox(this, "checkbox", 50, 170, 15, 15);
		box.setText("Enable Anisotropic Filtering");
		box.setForegroundColor(255, 0, 0);
		this.addGui(box);
	}

	@Override
	public void initialize() {
		this.showMouse(true);
	}

	@Override
	public void render() {

		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			bar.increaseProgress(1);
		}
		
		this.resetScreen();
		this.guis.update();
		GuiRenderer.render(this.guis);
		FontManager.render(this.texts);
	}
}
