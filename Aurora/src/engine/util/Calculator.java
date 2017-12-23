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
	
	/* Finds the height of the terrain in between vertices */
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	/* Gets the coordinates for the terrain that the object is on */
	public static Vector2f terrainCoords(float x, float z){
		int x2 = (int) (x / Terrain.SIZE);
		int z2 = (int) (z / Terrain.SIZE);
		
		return new Vector2f(x2, z2);
	}
	
	/* General conversion from Java to OpenGL coordinates */
	public static Vector2f toCenteredOpenGLCoordinates(float x, float y) {
		float x2 = (2.0f * (x + 3)) / Display.getWidth();
		float y2 = (2.0f * y) / Display.getHeight();
		
		return new Vector2f(x2, y2);
	}
	
	/* Converts a width and height to OpenGL width and height*/
	public static Vector2f toOpenGLScale(float width, float height){
		float w2 = width / Display.getWidth();
		float h2 = height / Display.getHeight();
		
		return new Vector2f(w2, h2);
	}
	
	/* Converts Java coordinates to OpenGL*/
	public static Vector2f toOpenGLCoordinates(float x, float y, float scaleX, float scaleY) {
		float x2 = 2 * x / Display.getWidth() - 1 + scaleX;
		float y2 = 2 * y / Display.getHeight() - 1 + scaleY;
		
		return new Vector2f(x2, -y2);
	}
	
	/* Converts Java coordinates to OpenGL*/
	public static Vector2f toOpenGLCoordinates(float x, float y) {
		float x2 = 2 * x / Display.getWidth() - 1;
		float y2 = 2 * y / Display.getHeight() - 1;
		
		return new Vector2f(x2, -y2);
	}
	
	/* Creates a matrix used to change a 2D object's position and scale */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		
		return matrix;
	}
	
	/* Creates a matrix used to change a 3D object's position and scale */
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.translate(translation, matrix, matrix);
		
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
		
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		
		return matrix;
	}
	
	/* Creates a matrix that represents the camera's point of view */
	public static Matrix4f createViewMatrix(Camera c) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(c.getRotation().x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(c.getRotation().y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(c.getRotation().z), new Vector3f(0, 0, 1), matrix, matrix);

		Vector3f negativeplayerPos = new Vector3f(-c.getPosition().x, -c.getPosition().y, -c.getPosition().z);
		Matrix4f.translate(negativeplayerPos, matrix, matrix);

		return matrix;
	}
}
