package engine.guis.component;

import engine.rendering.RenderMode;
import engine.util.InputManager;

public class GuiTextField extends GuiComponent {

	private String theText = "";
	
	public GuiTextField(RenderMode render, String texture, float x, float y, float width, float height) {
		super(render, texture, x, y, width, height);
		this.setTextAlign(TEXT_ALIGN_LEFT);
	}

	@Override
	public void update() {
		super.update();
		theText += InputManager.keysTyped;
		if(InputManager.keyTyped[26] == 1) {
			theText += " ";
		}
		if(InputManager.keyTyped[27] == 1 && theText.length() > 0) {
			theText = theText.substring(0, theText.length() - 1);
		}
		this.setText(theText);
	}
}
