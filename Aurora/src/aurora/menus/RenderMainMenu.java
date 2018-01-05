package aurora.menus;

import org.lwjgl.util.vector.Vector3f;

import aurora.main.Aurora;
import engine.guis.Gui;
import engine.guis.component.GuiComponent.ClickAction;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;

public class RenderMainMenu extends RenderMode {

	private MenuButton singlePlayer, multiplayer, tools, settings, exit;
	private Gui backgroundImage;
	private GUIText title;

	private Vector3f foregroundColor = new Vector3f(255 / 255f, 255 / 255f, 255 / 255f);

	private int buttonWidth = 450;
	private int buttonHeight = 90;
	private int buttonX = 550;
	
	public RenderMainMenu(Aurora aurora) {

		backgroundImage = new Gui("backgroundImage", 0, 0, 1000, 750);
		this.guis.add(0, backgroundImage);

		title = new GUIText("Aurora", 40, FontManager.font("cherokee"), 675, 25, false);
		title.setColor(34, 139, 34);
		title.setMode(GUIText.MODE_GLOWING);
		title.setSecondaryColor(new Vector3f(155 / 255f, 1, 155 / 255f));
		this.addText(title);

		// Menu Buttons
		singlePlayer = new MenuButton(this, buttonX, 150, buttonWidth, buttonHeight);
		singlePlayer.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.renderWorld();
			}
		});
		singlePlayer.setText("Single Player");
		singlePlayer.setForegroundColor(foregroundColor);
		this.addGui(singlePlayer);

		multiplayer = new MenuButton(this, buttonX, 250, buttonWidth, buttonHeight);
		multiplayer.setText("Multiplayer");
		multiplayer.setForegroundColor(foregroundColor);
		multiplayer.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				// Do something
			}
		});
		this.addGui(multiplayer);
		
		tools = new MenuButton(this, buttonX, 350, buttonWidth, buttonHeight);
		tools.setText("Developer Tools");
		tools.setForegroundColor(foregroundColor);
		this.addGui(tools);

		settings = new MenuButton(this, buttonX, 450, buttonWidth, buttonHeight);
		settings.setText("Settings");
		settings.setForegroundColor(foregroundColor);
		settings.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.setRenderMode(aurora.settingsScreen);
			}
		});
		this.addGui(settings);

		exit = new MenuButton(this, buttonX, 550, buttonWidth, buttonHeight);
		exit.addClickAction(new ClickAction() {
			@Override
			public void onClick() {
				aurora.endProgram();
			}
		});
		exit.setText("Exit");
		exit.setForegroundColor(foregroundColor);
		this.addGui(exit);
	}

	@Override
	public void initialize() {
		this.showMouse(true);
	}

	@Override
	public void render() {
		this.resetScreen();

		super.render();
	}
}
