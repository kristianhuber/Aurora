package engine.guis.component;

import org.lwjgl.util.vector.Vector4f;

import engine.rendering.RenderMode;
import engine.util.InputManager;

public class GuiCheckbox extends GuiComponent {

	private String rootTexture;
	private boolean checked;
	
	public GuiCheckbox(RenderMode render, String texture, float x, float y, float width, float height) {
		super(render, texture, x, y, width, height * 4/3);
		this.setTextAlign(TEXT_ALIGN_POSTLEFT);
		this.rootTexture = texture;
		this.checked = false;
		this.setTextSize(width / 1.25f);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(area.contains(InputManager.mousePosition) && InputManager.leftDownEvent && clickedInHere) {
			this.addFilter(new Vector4f(-0.1f, -0.1f, -0.1f, 0));
			if(checked) {
				this.setTexture(rootTexture);
			}else {
				this.setTexture(rootTexture + "_checked");
			}
			this.checked = !checked;
		}
		if(hasFilter() && !InputManager.leftDown) {
			this.removeFilter();
		}
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isChecked() {
		return this.checked;
	}
}
