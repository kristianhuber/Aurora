package aurora.mainmenu;

import org.lwjgl.util.vector.Vector3f;

import engine.guis.component.GuiButton;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;
import engine.util.InputManager;

public class MenuButton extends GuiButton {

	private Vector3f glow = new Vector3f(105 / 255f, 205 / 255f, 105 / 255f);

	public MenuButton(RenderMode render, String texture, float x, float y, float width, float height) {
		super(render, texture, x, y, width, height);

		this.addHoverAction(new HoverAction() {
			@Override
			public void onHover() {
				setTextMode(GUIText.MODE_GLOWING);
				setTextSecondary(glow);
			}
		});
	}

	@Override
	public void update() {
		super.update();
		if (!this.area.contains(InputManager.mousePosition)) {
			setTextMode(GUIText.MODE_PLAIN);
		}
	}
}
