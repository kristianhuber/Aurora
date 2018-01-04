package engine.guis.component;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import engine.guis.Gui;
import engine.guis.font.FontManager;
import engine.guis.font.mesh.GUIText;
import engine.rendering.RenderMode;
import engine.util.InputManager;

/**
 * @Author: Kristian
 * @Description: Super Class for all the components of GUIs
 * 
 */

public class GuiComponent extends Gui {

	public static int TEXT_ALIGN_LEFT = -1;
	public static int TEXT_ALIGN_CENTERED = 0;
	public static int TEXT_ALIGN_POSTLEFT = 2;

	protected Vector3f foregroundColor;
	protected Rectangle2D.Float area;
	protected boolean clickedInHere;
	protected String text;

	private Vector3f textSecondary;
	private RenderMode render;
	private ClickAction click;
	private HoverAction hover;
	private float textSize;
	private GUIText gText;
	private int gTextMode;
	private int alignment;

	/**
	 * Main Constructor
	 * 
	 * @param render
	 *            - Used to easily add components to the GUI and Font renderers
	 * @param texture
	 *            - File name for the texture
	 * @param x
	 *            - x Position on the screen based on Java scale
	 * @param y
	 *            - y Position on the screen based on Java scale
	 * @param width
	 *            - width based on Java scale
	 * @param height
	 *            - height based on Java scale
	 */
	public GuiComponent(RenderMode render, String texture, float x, float y, float width, float height) {
		super(texture, x, y, width, height);
		this.area = new Rectangle2D.Float(x, y, width, height);
		this.clickedInHere = false;
		this.render = render;
		this.foregroundColor = new Vector3f(0, 0, 0);
		this.alignment = 0;
		this.gTextMode = 0;
		this.textSecondary = new Vector3f(0, 0, 0);
		this.textSize = -1;
	}

	/**
	 * Checks for conditions to activate actions and updates variables NOTE: Will
	 * not update text alignment
	 */
	@Override
	public void update() {

		// If there is a string and no text being rendered, then make one
		if (text != null && gText == null) {

			// Select bounds based on alignment
			if (alignment == GuiComponent.TEXT_ALIGN_CENTERED) {

				gText = new GUIText(text, textSize, FontManager.font("cherokee"), area.x + area.width / 2,
						area.y + area.height / 4, true);

			} else if (alignment == GuiComponent.TEXT_ALIGN_LEFT) {

				gText = new GUIText(text, textSize, FontManager.font("cherokee"), area.x + area.width * 0.05f,
						area.y + area.height / 4);

			} else if (alignment == GuiComponent.TEXT_ALIGN_POSTLEFT) {

				gText = new GUIText(text, textSize, FontManager.font("cherokee"), area.x + area.width,
						area.y - area.height / 2);
			}

			// Add the attributes and render it
			gText.setColor(foregroundColor);
			gText.setMode(gTextMode);
			gText.setSecondaryColor(textSecondary);
			render.addText(gText);
		}

		// Updates clickedInHere variable
		if (area.contains(InputManager.mousePosition) && InputManager.leftDownEvent) {
			clickedInHere = true;
		}

		// Checks for hover action
		if (hover != null && area.contains(InputManager.mousePosition)) {
			hover.onHover();
		}

		// Checks for click action
		if (click != null && area.contains(InputManager.mousePosition) && InputManager.leftReleasedEvent
				&& clickedInHere) {
			click.onClick();
		}

		// Updates clickedInHere variable
		if (InputManager.leftReleasedEvent) {
			clickedInHere = false;
		}
	}

	/**
	 * Interface to do something on a hover action
	 * 
	 */
	public interface HoverAction {
		public void onHover();
	}

	/**
	 * Adds a hover action
	 * 
	 * @param h
	 *            - A HoverAction interface
	 * 
	 */
	public void addHoverAction(HoverAction h) {
		this.hover = h;
	}

	/**
	 * Interface to do something on a click action
	 * 
	 */
	public interface ClickAction {
		public void onClick();
	}

	/**
	 * Adds a click action
	 * 
	 * @param c
	 *            - A ClickAction interface
	 * 
	 */
	public void addClickAction(ClickAction c) {
		this.click = c;
	}

	/**
	 * Sets the area of the GUI
	 * 
	 * @param x
	 *            - x Position on the screen based on Java scale
	 * @param y
	 *            - y Position on the screen based on Java scale
	 * @param width
	 *            - width based on Java scale
	 * @param height
	 *            - height based on Java scale
	 * 
	 */
	public void setBounds(float x, float y, float width, float height) {
		this.area = new Rectangle2D.Float(x, y, width, height);
	}

	/**
	 * Sets the text color
	 * 
	 * @param color
	 *            - Color represented as a 3D vector with values between 0 and 1
	 */
	public void setForegroundColor(Vector3f color) {
		this.foregroundColor.set(color);
		if (gText != null) {
			gText.setColor(foregroundColor);
		}
	}

	/**
	 * Sets the text color
	 * 
	 * @param r
	 *            - Red component out of 255
	 * @param g
	 *            - Green component out of 255
	 * @param b
	 *            - Blue component out of 255
	 */
	public void setForegroundColor(float r, float g, float b) {
		this.foregroundColor.x = r / 255f;
		this.foregroundColor.y = g / 255f;
		this.foregroundColor.z = b / 255f;
		if (gText != null) {
			gText.setColor(foregroundColor);
		}
	}

	/**
	 * Sets the text effect
	 * 
	 * @param mode
	 *            - Mode is an integer, use constants in this class
	 * 
	 */
	public void setTextEffect(int mode) {
		this.gTextMode = mode;
		if (gText != null) {
			gText.setMode(mode);
		}
	}

	/**
	 * Sets the text effect color
	 * 
	 * @param color
	 *            - Color represented as a 3D vector with values between 0 and 1
	 * 
	 */
	public void setTextEffectColor(Vector3f color) {
		this.textSecondary.set(color);
		if (gText != null) {
			gText.setSecondaryColor(textSecondary);
		}
	}

	/**
	 * Sets the text effect color
	 * 
	 * @param r
	 *            - Red component out of 255
	 * @param g
	 *            - Green component out of 255
	 * @param b
	 *            - Blue component out of 255
	 */
	public void setTextEffectColor(float r, float g, float b) {
		this.textSecondary.x = r / 255f;
		this.textSecondary.y = g / 255f;
		this.textSecondary.z = b / 255f;
		if (gText != null) {
			gText.setSecondaryColor(textSecondary);
		}
	}

	/**
	 * Sets the text alignment relative to the GUI
	 * 
	 * @param alignment
	 *            - Variable for text alignment relative to the GUI
	 */
	public void setTextAlign(int alignment) {
		this.alignment = alignment;
	}

	/**
	 * Gets the text
	 * 
	 * @return - Returns the String version of the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the text length
	 * 
	 * @return - Returns the OpenGL length of the text
	 */
	public float getTextLength() {
		if (gText != null) {
			return gText.getActualLength();
		} else {
			return 0;
		}
	}

	/**
	 * Sets the text
	 * 
	 * @param text
	 *            - A String to represent the text
	 */
	public void setText(String text) {
		this.text = text;
		if (gText != null) {
			gText.setText(text);
		}
		if(this.textSize == -1) {
			this.textSize = this.area.height / 6;
		}
	}

	/**
	 * Sets the text size, this is made for the initial text
	 * 
	 * @param size
	 *            - A float value to represent the size;
	 */
	public void setTextSize(float size) {
		this.textSize = size;
	}
}
