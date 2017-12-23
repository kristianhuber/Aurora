package engine.guis.component;

import org.lwjgl.util.vector.Vector4f;

import engine.rendering.RenderMode;
import engine.util.InputManager;

public class GuiButton extends GuiComponent{
	
	private String rootTexture;
	
	public GuiButton(RenderMode render, String texture, float x, float y, float width, float height) {
		super(render, texture, x, y, width, height);
		this.rootTexture = texture;
	}
	
	@Override
	public void update() {
		super.update();
		if(area.contains(InputManager.mousePosition) && getID().equals(rootTexture)) {
			setTexture(rootTexture + "_hover");
		}
		if(area.contains(InputManager.mousePosition) && InputManager.leftDown && clickedInHere) {
			addFilter(new Vector4f(-0.1f, -0.1f, -0.1f, 0));
		}
		if(!area.contains(InputManager.mousePosition) && !this.getID().equals(rootTexture)) {
			this.setTexture(rootTexture);
		}
		if(hasFilter() && !InputManager.leftDown) {
			this.removeFilter();
		}
	}
}
