package engine.world.entities.collisions;

import org.lwjgl.util.vector.Vector3f;

public class TrianglePlane {

	public Vector3f a, b, c;
	public Vector3f planeNormal;
	public double planeConstant;

	public TrianglePlane(Vector3f a, Vector3f b, Vector3f c, Vector3f planeNormal) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.planeNormal = planeNormal;
		planeConstant = -Vector3f.dot(planeNormal, a);
	}

	/**
	 * Returns if the given Vector3f vector is pointing toward the front of the
	 * plane.
	 * 
	 * @param vector
	 *            - the vector to test if it is facing the plane.
	 * @return true if vector is facing the front of the plane or is parallel to it.
	 */
	public boolean isFrontFacingTo(Vector3f vector) {
		return Vector3f.dot(vector, planeNormal) <= 0;
	}

	/**
	 * Gets the shortest distance from the point to the plane. This distance is
	 * perpendicular to the plane.
	 * 
	 * @param point
	 *            - the point to measure the distance to the plane.
	 * @return the distance from the given point to the plane.
	 */
	public double signedDistanceTo(Vector3f point) {
		return Vector3f.dot(planeNormal, point) + planeConstant;
	}
	
	public String toString() {
		return "TrianglePlane=<" + a.toString() + ";"  + b.toString() + ";" + c.toString() + ">";
	}
	
	public boolean isInsideTriangle(Vector3f p) {
		System.out.println("Point: " + p.toString() + " " + this.toString());
		return sameSide(p, a, b, c) && sameSide(p, b, a, c) && sameSide(p, c, a, b);
	}

	private boolean sameSide(Vector3f p1, Vector3f p2, Vector3f a, Vector3f b) {
		Vector3f cross1 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(p1, a, null), null);
		Vector3f cross2 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(p2, a, null), null);
		return Vector3f.dot(cross1, cross2) >= 0;
	}

}
