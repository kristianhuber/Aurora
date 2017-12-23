package engine.util;

import java.awt.Point;

import org.lwjgl.input.Mouse;

public class InputManager {
	public static Point mousePosition = new Point(0, 0);
	public static boolean leftDown = false;
	public static boolean leftReleasedEvent = false;
	public static boolean leftDownEvent = false;
	
	private static boolean downWait = false;
	private static boolean upWait = true;
	
	/* Updates the Keyboard and Mouse */
	public static void tick() {
		InputManager.mousePosition = new Point(Mouse.getX(), Engine.HEIGHT - Mouse.getY());

		if (Mouse.isButtonDown(0)) {
			upWait = false;
			
			leftDown = true;
			if(leftDownEvent && downWait) {
				leftDownEvent = false;
			}
			if(!leftDownEvent && !downWait) {
				leftDownEvent = true;
				downWait = true;
			}
		}
		
		if(!Mouse.isButtonDown(0)) {
			downWait = false;
			leftDown = false;
			
			if(leftReleasedEvent && upWait) {
				leftReleasedEvent = false;
			}
			if(!leftReleasedEvent && !upWait) {
				leftReleasedEvent = true;
				upWait = true;
			}
		}
	}
}
