package engine.util;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.world.entities.Camera;
import engine.world.terrain.Terrain;

/**
 * @Description: Contains random calculations for the game
 */

public class Calculator {

	public static final int SCREEN_WIDTH = 1000;
	public static final int SCREEN_HEIGHT = 750;

	/* Finds the height of the terrain in between vertices */
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3,
			Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x)
				* (p1.z - p3.z);

		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x)
				* (pos.y - p3.z))
				/ det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x)
				* (pos.y - p3.z))
				/ det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	/**
	 * Gets the current chunk the coordinate is on
	 * 
	 * @param x
	 *            - x coordinate in the world
	 * @param z
	 *            - z coordinate in the world
	 * 
	 * @return A 2D vector which tells which chunk the point is on
	 * */
	public static Vector2f terrainCoords(float x, float z) {
		int x2 = (int) (x / Terrain.SIZE);
		int z2 = (int) (z / Terrain.SIZE);

		return new Vector2f(x2, z2);
	}

	/**
	 * Converts from a 1000/750 scale to OpenGL scale
	 * 
	 * @param width
	 *            - A number between 0 and 1000
	 * @param height
	 *            - A number between 0 and 750
	 * 
	 * @return A 2D vector with OpenGL scale
	 * */
	public static Vector2f toOpenGLScale(float width, float height) {
		return new Vector2f(width / Calculator.SCREEN_WIDTH, height
				/ Calculator.SCREEN_HEIGHT);
	}

	/**
	 * Converts from a 1000/750 scale to OpenGL coordinates
	 * 
	 * @param x
	 *            - x coordinate from 0 to 1000
	 * @param y
	 *            - y coordinate from 0 to 750
	 * 
	 * @return A 2D vector with OpenGL coordinates
	 * */
	public static Vector2f toOpenGL(float x, float y) {
		return Calculator.toOpenGL(x, y, 0, 0);
	}

	/**
	 * Converts from a 1000/750 scale to OpenGL coordinates
	 * 
	 * @param x
	 *            - x coordinate from 0 to 1000
	 * @param y
	 *            - y coordinate from 0 to 750
	 * @param width
	 *            - width in OpenGL scale
	 * @param height
	 *            - height in OpenGL scale
	 * 
	 * @return A 2D vector with OpenGL coordinates
	 * */
	public static Vector2f toOpenGL(float x, float y, float width, float height) {
		float x2 = 2 * x / Calculator.SCREEN_WIDTH - 1 + width;
		float y2 = 2 * y / Calculator.SCREEN_HEIGHT - 1 + height;

		return new Vector2f(x2, -y2);
	}

	/* Creates a matrix used to change a 2D object's position and scale */
	public static Matrix4f createTransformationMatrix(Vector2f translation,
			Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);

		return matrix;
	}

	/* Creates a matrix used to change a 3D object's position and scale */
	public static Matrix4f createTransformationMatrix(Vector3f translation,
			Vector3f rotation, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);

		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0,
				0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1,
				0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0,
				1), matrix, matrix);

		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

		return matrix;
	}

	/* Creates a matrix that represents the camera's point of view */
	public static Matrix4f createViewMatrix(Camera c) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(c.getRotation().x),
				new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(c.getRotation().y),
				new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(c.getRotation().z),
				new Vector3f(0, 0, 1), matrix, matrix);

		Vector3f negativeplayerPos = new Vector3f(-c.getPosition().x,
				-c.getPosition().y, -c.getPosition().z);
		Matrix4f.translate(negativeplayerPos, matrix, matrix);

		return matrix;
	}
	/**
	 * Converts from a Java scale to 1000/750 scale
	 * 
	 * @param x
	 *            - x coordinate in Java
	 * @param y
	 *            - y coordinate in Java
	 * 
	 * @return A 2D vector with 1000/750 scale coordinates
	 * */
	public static Vector2f toOpenGLFromJava(float x, float y) {
		float x2 = x / Display.getWidth() * Calculator.SCREEN_WIDTH;
		float y2 = y / Display.getHeight() * Calculator.SCREEN_HEIGHT;
		
		return new Vector2f(x2, y2);
	}
}
