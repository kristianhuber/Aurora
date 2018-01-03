package engine.util;

import java.awt.Point;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class InputManager {
	public static Point mousePosition = new Point(0, 0);
	public static boolean leftDown = false;
	public static boolean leftReleasedEvent = false;
	public static boolean leftDownEvent = false;
	public static String keysTyped = "";
	public static int[] keyTyped = new int[29];
	// 0 is no event, -1 is key down, 1 is key up
	// A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z, ,backspace,

	private static boolean[] currentKeyState = new boolean[29];
	private static boolean[] prevKeyState = new boolean[29];
	private static boolean downWait = false;
	private static boolean upWait = true;

	/* Updates the Keyboard and Mouse */
	public static void tick() {
		float x = Mouse.getX();
		float y = Engine.HEIGHT - Mouse.getY();
		Vector2f point = Calculator.toOpenGLFromJava(x, y);
		
		InputManager.mousePosition = new Point((int)point.x, (int)point.y);

		InputManager.updateKeyState();
		InputManager.outputKeys();
		// System.out.print(keysTyped);

		if (Mouse.isButtonDown(0)) {
			upWait = false;

			leftDown = true;
			if (leftDownEvent && downWait) {
				leftDownEvent = false;
			}
			if (!leftDownEvent && !downWait) {
				leftDownEvent = true;
				downWait = true;
			}
		}

		if (!Mouse.isButtonDown(0)) {
			downWait = false;
			leftDown = false;

			if (leftReleasedEvent && upWait) {
				leftReleasedEvent = false;
			}
			if (!leftReleasedEvent && !upWait) {
				leftReleasedEvent = true;
				upWait = true;
			}
		}
	}

	private static void outputKeys() {
		keysTyped = "";
		int addition = 97;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			addition = 65;
		}
		for (int i = 0; i < 26; i++) {
			if (keyTyped[i] == 1) {
				keysTyped += (char) (i + addition);
				// System.out.println((char)(i + 65) + " was released");
			}
			if (keyTyped[i] == -1) {
				// System.out.println((char)(i + 65) + " was pressed");
			}
		}
	}

	private static void updateKeyState() {
		// Update the previous key state
		for (int i = 0; i < currentKeyState.length; i++) {
			prevKeyState[i] = currentKeyState[i];
		}

		// Gets the inputs
		for (int i = 0; i < 26; i++) {
			char key = (char) (i + 65);
			String str = String.valueOf(key);
			currentKeyState[i] = Keyboard.isKeyDown(Keyboard.getKeyIndex(str));
		}
		currentKeyState[26] = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		currentKeyState[27] = Keyboard.isKeyDown(Keyboard.KEY_BACK);
		currentKeyState[28] = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		
		for (int i = 0; i < currentKeyState.length; i++) {
			boolean prev = prevKeyState[i];
			boolean now = currentKeyState[i];
			if ((prev && now) || (!prev && !now)) {
				keyTyped[i] = 0;
			}
			if (prev && !now) {
				keyTyped[i] = 1;
			}
			if (!prev && now) {
				keyTyped[i] = -1;
			}
		}
	}
}
