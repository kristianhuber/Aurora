package aurora.menus;

import org.lwjgl.util.vector.Vector3f;

import engine.guis.component.GuiButton;
import engine.guis.component.GuiComponent;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;
import engine.util.InputManager;

public class MenuButton extends GuiButton {

	private Vector3f glow = new Vector3f(105 / 255f, 205 / 255f, 105 / 255f);
	private static String buttonTexture = "Birch2";
	
	public MenuButton(RenderMode render, float x, float y, float width, float height) {
		super(render, buttonTexture, x, y, width, height);

		this.setTextAlign(GuiComponent.TEXT_ALIGN_LEFT);
		this.setForegroundColor(255, 255, 255);
		this.addHoverAction(new HoverAction() {
			@Override
			public void onHover() {
				setTextEffect(GUIText.MODE_GLOWING);
				setTextEffectColor(glow);
			}
		});
	}

	@Override
	public void update() {
		super.update();
		if (!this.area.contains(InputManager.mousePosition)) {
			setTextEffect(GUIText.MODE_PLAIN);
		}
	}
}
