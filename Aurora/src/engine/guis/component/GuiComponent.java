package engine.guis.component;

import java.awt.geom.Rectangle2D;

import engine.guis.Gui;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;
import engine.util.InputManager;

public class GuiComponent extends Gui {

	protected Rectangle2D.Float area;

	protected boolean clickedInHere;
	protected String text;

	private RenderMode render;
	private ClickAction click;
	private HoverAction hover;
	private GUIText gText;

	public GuiComponent(RenderMode render, String texture, float x, float y, float width, float height) {
		super(texture, x, y, width, height);
		this.area = new Rectangle2D.Float(x, y, width, height);
		this.clickedInHere = false;
		this.render = render;
	}

	@Override
	public void update() {
		if (area.contains(InputManager.mousePosition) && InputManager.leftDownEvent) {
			clickedInHere = true;
		}
		if (hover != null && area.contains(InputManager.mousePosition)) {
			hover.onHover();
		}
		if (click != null && area.contains(InputManager.mousePosition) && InputManager.leftReleasedEvent
				&& clickedInHere) {
			click.onClick();
		}
		if (InputManager.leftReleasedEvent) {
			clickedInHere = false;
		}
	}

	public interface HoverAction {
		public void onHover();
	}

	public void addHoverAction(HoverAction h) {
		this.hover = h;
	}

	public interface ClickAction {
		public void onClick();
	}

	public void addClickAction(ClickAction c) {
		this.click = c;
	}

	public void setText(String text) {
		this.text = text;
		gText = new GUIText(text, 16, FontManager.font("tempus"), this.getPosition().x + this.getScale().x / 2,
				this.getPosition().y);
		render.loadText(gText);
	}

	public String getText() {
		return text;
	}
}
