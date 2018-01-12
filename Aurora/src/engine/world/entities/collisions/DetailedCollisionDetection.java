package engine.world.entities.collisions;

import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import com.sun.javafx.geom.Vec3f;

import engine.world.entities.Entity;

public class DetailedCollisionDetection {

	private static Vector3f velocityE3;
	private static Vector3f basePointE3;

	public static Vector3f getCollision(Entity baseEntity, Entity otherEntity, float x_radius, float y_radius,
			float z_radius, Vector3f velocityR3) {
		// Establishing Vector Space Conversion Matrix:
		Matrix3f vectorConversionMatrix = new Matrix3f();
		vectorConversionMatrix.setIdentity();
		vectorConversionMatrix.m00 = 1f / x_radius;
		vectorConversionMatrix.m11 = 1f / y_radius;
		vectorConversionMatrix.m22 = 1f / z_radius;

		// Converting the velocity to e-space:
		velocityE3 = getConvertedVector(velocityR3, vectorConversionMatrix);

		// Converting the entity's center to e-space:
		Vector3f basePointR3 = new Vector3f(baseEntity.getPosition());
		basePointR3.setY(basePointR3.getY() + y_radius);
		basePointE3 = getConvertedVector(basePointR3, vectorConversionMatrix);

		// Getting all the triangle data from the other entity:
		float[] vertices = otherEntity.getModel().getRawModel().getModelData().getVertices();
		float[] normals = otherEntity.getModel().getRawModel().getModelData().getNormals();
		int[] indices = otherEntity.getModel().getRawModel().getModelData().getIndices();

		Vector3f intersectionPoint = null;
		// This is actually storing the square of the intersection distance for speed
		// purposes.
		float intersectionDistance = Float.MAX_VALUE;

		// Looping through each triangle:
		for (int i = 0; i < indices.length / 3; i++) {
			// Getting the vectors that represent the three points of the triangle in
			// question.
			Vector3f a = getConvertedVector(
					new Vector3f(vertices[indices[i * 3]], vertices[indices[i * 3] + 1], vertices[indices[i * 3] + 2]),
					vectorConversionMatrix);
			Vector3f b = getConvertedVector(new Vector3f(vertices[indices[i * 3 + 1]], vertices[indices[i * 3 + 1] + 1],
					vertices[indices[i * 3 + 1] + 2]), vectorConversionMatrix);
			Vector3f c = getConvertedVector(new Vector3f(vertices[indices[i * 3 + 2]], vertices[indices[i * 3 + 2] + 1],
					vertices[indices[i * 3 + 2] + 2]), vectorConversionMatrix);

			// Getting the normal vectors for the points.
			Vector3f aNormal = getConvertedVector(
					new Vector3f(normals[indices[i * 3]], normals[indices[i * 3] + 1], normals[indices[i * 3] + 2]),
					vectorConversionMatrix);
			Vector3f bNormal = getConvertedVector(new Vector3f(normals[indices[i * 3 + 1]],
					normals[indices[i * 3 + 1] + 1], normals[indices[i * 3 + 1] + 2]), vectorConversionMatrix);
			Vector3f cNormal = getConvertedVector(new Vector3f(normals[indices[i * 3 + 2]],
					normals[indices[i * 3 + 2] + 1], normals[indices[i * 3 + 2] + 2]), vectorConversionMatrix);

			// Calculating the plane's normal - MAY WANT TO USE AN AVERAGE OF THE
			// PRECALCULATED VECTORS INSTEAD.
			Vector3f planeNormal = new Vector3f((aNormal.getX() + bNormal.getX() + cNormal.getX()) / 3f,
					(aNormal.getY() + bNormal.getY() + cNormal.getY()) / 3f,
					(aNormal.getZ() + bNormal.getZ() + cNormal.getZ()) / 3f);
			// Vector3f planeNormal = Vector3f.cross(Vector3f.sub(a, b, null),
			// Vector3f.sub(c, b, null), null);

			// Calculating signed distance of the base point to the triangle's plane.
			float planeConstant = -Vector3f.dot(planeNormal, a);
			float signedDistanceAtBasePoint = Vector3f.dot(planeNormal, basePointE3) + planeConstant;

			boolean isInside = false;

			// Calculating t0 and t1:
			float planeNormalDotVelocity = Vector3f.dot(planeNormal, velocityE3);
			if (planeNormalDotVelocity != 0) {
				// Then the triangle's normal is not perpendicular to the velocity vector.
				// If this is the case then the sphere has the ability to collide with the face
				// of the triangle, otherwise, skip this.
				float t0 = (1f - signedDistanceAtBasePoint) / planeNormalDotVelocity;
				float t1 = (-1f - signedDistanceAtBasePoint) / planeNormalDotVelocity;

				// Now we go on to checking for a collision with the inside of the triangle:
				// Calculating the point on the sphere that contacts the plane first:
				Vector3f planeIntersectionPoint = Vector3f.add(Vector3f.sub(basePointE3, planeNormal, null),
						scaleVector(velocityE3, t0), null);
				isInside = isInsideTriangle(planeIntersectionPoint, a, b, c);
				Vector3f distanceVector = scaleVector(velocityE3, t0);
				if (isInside && intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
					intersectionPoint = planeIntersectionPoint;
					intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
				}
			}

			if (!isInside || (planeNormalDotVelocity == 0 && signedDistanceAtBasePoint <= 1)) {
				// Then there could still be a collision with an edge or vertex for this
				// triangle so we have to do the sweep test. For the entire sweep test, we have
				// to test vertices and edges for collision with the sphere.

				float a_val = Vector3f.dot(velocityE3, velocityE3);
				float b_val;
				float c_val;
				float[] timeValues;

				// Testing each Vertex Individually:
				Vector3f p;
				Vector3f aMinusBasePoint;

				// Vertex A:
				p = new Vector3f(a.getX(), a.getY(), a.getZ());
				b_val = 2 * (Vector3f.dot(velocityE3, Vector3f.sub(basePointE3, p, null)));
				aMinusBasePoint = Vector3f.sub(p, basePointE3, null);
				c_val = Vector3f.dot(aMinusBasePoint, aMinusBasePoint) - 1;
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						intersectionPoint = p;
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}
				// Vertex B:
				p = new Vector3f(b.getX(), b.getY(), b.getZ());
				b_val = 2 * (Vector3f.dot(velocityE3, Vector3f.sub(basePointE3, p, null)));
				aMinusBasePoint = Vector3f.sub(p, basePointE3, null);
				c_val = Vector3f.dot(aMinusBasePoint, aMinusBasePoint) - 1;
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						intersectionPoint = p;
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}
				// Vertex C:
				p = new Vector3f(c.getX(), c.getY(), c.getZ());
				b_val = 2 * (Vector3f.dot(velocityE3, Vector3f.sub(basePointE3, p, null)));
				aMinusBasePoint = Vector3f.sub(p, basePointE3, null);
				c_val = Vector3f.dot(aMinusBasePoint, aMinusBasePoint) - 1;
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						intersectionPoint = p;
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}

				// Testing each Edge Individually:
				Vector3f edge;
				Vector3f baseToVertex;

				// Edge AB
				edge = Vector3f.sub(b, a, null);
				baseToVertex = Vector3f.sub(a, basePointE3, null);
				a_val = (float) (Vector3f.dot(edge, edge) * -Vector3f.dot(velocityE3, velocityE3)
						+ Math.pow(Vector3f.dot(edge, velocityE3), 2));
				b_val = (float) (Vector3f.dot(edge, edge) * 2 * Vector3f.dot(velocityE3, baseToVertex)
						- 2 * Vector3f.dot(edge, velocityE3) * Vector3f.dot(edge, baseToVertex));
				c_val = (float) (Vector3f.dot(edge, edge) * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ Math.pow(Vector3f.dot(edge, baseToVertex), 2));
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						float f0 = (Vector3f.dot(edge, velocityE3) * smallerTime - Vector3f.dot(edge, baseToVertex))
								/ Vector3f.dot(edge, edge);
						intersectionPoint = Vector3f.add(a, scaleVector(edge, f0), null);
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}
				// Edge BC
				edge = Vector3f.sub(c, b, null);
				baseToVertex = Vector3f.sub(b, basePointE3, null);
				a_val = (float) (Vector3f.dot(edge, edge) * -Vector3f.dot(velocityE3, velocityE3)
						+ Math.pow(Vector3f.dot(edge, velocityE3), 2));
				b_val = (float) (Vector3f.dot(edge, edge) * 2 * Vector3f.dot(velocityE3, baseToVertex)
						- 2 * Vector3f.dot(edge, velocityE3) * Vector3f.dot(edge, baseToVertex));
				c_val = (float) (Vector3f.dot(edge, edge) * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ Math.pow(Vector3f.dot(edge, baseToVertex), 2));
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						float f0 = (Vector3f.dot(edge, velocityE3) * smallerTime - Vector3f.dot(edge, baseToVertex))
								/ Vector3f.dot(edge, edge);
						intersectionPoint = Vector3f.add(b, scaleVector(edge, f0), null);
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}
				// Edge CA
				edge = Vector3f.sub(a, c, null);
				baseToVertex = Vector3f.sub(c, basePointE3, null);
				a_val = (float) (Vector3f.dot(edge, edge) * -Vector3f.dot(velocityE3, velocityE3)
						+ Math.pow(Vector3f.dot(edge, velocityE3), 2));
				b_val = (float) (Vector3f.dot(edge, edge) * 2 * Vector3f.dot(velocityE3, baseToVertex)
						- 2 * Vector3f.dot(edge, velocityE3) * Vector3f.dot(edge, baseToVertex));
				c_val = (float) (Vector3f.dot(edge, edge) * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ Math.pow(Vector3f.dot(edge, baseToVertex), 2));
				timeValues = solveQuadratic(a_val, b_val, c_val);
				if (timeValues != null) {
					float smallerTime = Math.min(timeValues[0], timeValues[1]);
					Vector3f distanceVector = scaleVector(velocityE3, smallerTime);
					if (intersectionDistance > Vector3f.dot(distanceVector, distanceVector)) {
						float f0 = (Vector3f.dot(edge, velocityE3) * smallerTime - Vector3f.dot(edge, baseToVertex))
								/ Vector3f.dot(edge, edge);
						intersectionPoint = Vector3f.add(c, scaleVector(edge, f0), null);
						intersectionDistance = Vector3f.dot(distanceVector, distanceVector);
					}
				}

			}
		}

		// If there is a closest intersection point then handle collision response.
		if (intersectionPoint != null) {

			// Calculating the normal vector to the sliding plane:
			intersectionDistance = (float) Math.sqrt(intersectionDistance);
			Vector3f slidingPlanePoint = intersectionPoint;
			Vector3f displacementVector = scaleVector(velocityE3.normalise(null), intersectionDistance);
			Vector3f newPosition = Vector3f.add(basePointE3, displacementVector, null);
			Vector3f slidingPlaneNormal = Vector3f.sub(newPosition, intersectionPoint, null);
			slidingPlaneNormal.normalise();

			// Projecting the velocity vector onto the sliding plane:
			Vector3f originalDestination = Vector3f.add(basePointE3, velocityE3, null);
			float planeConstant = -Vector3f.dot(slidingPlaneNormal, slidingPlanePoint);
			float distance = Math.abs(Vector3f.dot(slidingPlaneNormal, originalDestination) + planeConstant);
			Vector3f newDestinationPoint = Vector3f.sub(originalDestination, scaleVector(slidingPlaneNormal, distance),
					null);

			Vector3f newVelocityE3 = Vector3f.sub(newDestinationPoint, intersectionPoint, null);
			Vector3f toReturn =  new Vector3f(newVelocityE3.getX() * x_radius, newVelocityE3.getY() * y_radius,
					newVelocityE3.getZ() * z_radius);
			System.out.println(toReturn.toString());
			return toReturn;
		} else
			return velocityR3;
	}

	public static float[] solveQuadratic(float a, float b, float c) {
		float descriminent = b * b - 4 * a * c;
		if (descriminent < 0)
			return null;
		float sqrtDescriminet = (float) Math.sqrt(descriminent);
		float plusSolution = (-b + sqrtDescriminet) / (2 * a);
		float minusSolution = (-b - sqrtDescriminet) / (2 * a);
		return new float[] { plusSolution, minusSolution };
	}

	public static boolean isInsideTriangle(Vector3f p, Vector3f a, Vector3f b, Vector3f c) {
		return sameSide(p, a, b, c) && sameSide(p, b, a, c) && sameSide(p, c, a, b);
	}

	public static boolean sameSide(Vector3f p1, Vector3f p2, Vector3f a, Vector3f b) {
		Vector3f cross1 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(p1, a, null), null);
		Vector3f cross2 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(p2, a, null), null);
		return Vector3f.dot(cross1, cross2) >= 0;
	}

	public static Vector3f scaleVector(Vector3f toScale, float scaler) {
		return new Vector3f(toScale.getX() * scaler, toScale.getY() * scaler, toScale.getZ() * scaler);
	}

	public static Vector3f getEntityPositionBasedOnLinearFunction(float time) {
		return Vector3f.add(basePointE3,
				new Vector3f(time * velocityE3.getX(), time * velocityE3.getY(), time * velocityE3.getZ()), null);
	}

	public static Vector3f getConvertedVector(Vector3f oldVector, Matrix3f vectorConversionMatrix) {
		return Matrix3f.transform(vectorConversionMatrix, oldVector, null);
	}

}
