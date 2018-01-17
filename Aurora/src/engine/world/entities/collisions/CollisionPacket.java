package engine.world.entities.collisions;

import javax.sql.rowset.spi.TransactionalWriter;

import org.lwjgl.util.vector.Vector3f;

import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;

public class CollisionPacket {

	public Vector3f eRadius; // ellipsoid radius.

	// Information about the move being requested in R3
	public Vector3f R3Velocity;
	public Vector3f R3Position;

	// Information about the move being requested in eSpace
	public Vector3f velocity;
	public Vector3f normalizedVelocity;
	public Vector3f basePoint;

	// Hit information
	public boolean foundCollision;
	public double nearestDistance;
	public Vector3f intersectionPoint;

	public CollisionPacket(Vector3f eRadius, Vector3f r3Velocity, Vector3f r3Position) {
		this.eRadius = eRadius;
		this.R3Velocity = r3Velocity;
		this.R3Position = r3Position;

		this.velocity = getESpaceVector(R3Velocity, eRadius);
		this.basePoint = getESpaceVector(R3Position, eRadius);
	}

	public static void checkTriangle(CollisionPacket packet, Vector3f R3_a, Vector3f R3_b, Vector3f R3_c, Vector3f R3_planeNormal) {
		
		TrianglePlane triangle = new TrianglePlane(getESpaceVector(R3_a, packet.eRadius), getESpaceVector(R3_b, packet.eRadius), getESpaceVector(R3_c, packet.eRadius),
				getESpaceVector(R3_planeNormal, packet.eRadius));

		if (triangle.isFrontFacingTo(packet.normalizedVelocity)) {

			double t0, t1;
			boolean embeddedInPlane = false;

			// Calculating the signed distance from the sphere position to the triangle
			// point. We are going to use this and the normal dot the velocity several times
			// below.
			double signedDistanceToTrianglePlane = triangle.signedDistanceTo(packet.basePoint);
			double planeNormalDotVelocity = Vector3f.dot(triangle.planeNormal, packet.velocity);

			if (planeNormalDotVelocity == 0) {
				if (Math.abs(signedDistanceToTrianglePlane) >= 1.0) {
					// Then there can be no collision with this plane because the velocity vector is
					// parallel to the plane and it is too far away.
					return;
				} else {
					// Then the plane is embedded in the sphere.
					embeddedInPlane = true;
					t0 = 0.0;
					t1 = 1.0;
				}
			} else {
				// Plane normal dot velocity is not 0 so calculate the interval of collision.
				t0 = (-1 - signedDistanceToTrianglePlane) / planeNormalDotVelocity;
				t1 = (1 - signedDistanceToTrianglePlane) / planeNormalDotVelocity;

				// Make sure t0 < t1:
				if (t0 > t1) {
					double temp = t1;
					t1 = t0;
					t0 = temp;
				}

				// make sure at lease one result is within 0 and 1:
				if (t0 > 1.0 || t1 < 0.0) {
					// No collisions are possible because it won't contact the plane any time soon.
					return;
				}

				// Clamping to bounds in [0,1]
				if (t0 < 0.0)
					t0 = 0.0;
				if (t1 < 0.0)
					t1 = 0.0;
				if (t0 > 1.0)
					t0 = 1.0;
				if (t1 > 1.0)
					t1 = 1.0;
			}

			Vector3f collisionPoint = null;
			boolean foundCollision = false;
			float t = 1.0f;

			// Checking to see if the sphere collides with the face of the triangle:
			if (!embeddedInPlane) {
				Vector3f planeIntersectionPoint = Vector3f.add(Vector3f.sub(packet.basePoint, triangle.planeNormal, null),
						scaleVector(packet.velocity, (float) t0), null);

				if (triangle.isInsideTriangle(planeIntersectionPoint)) {
					// Then there is a collision with the face of the plane because the collision
					// point is inside the triangle:
					foundCollision = true;
					t = (float) t0;
					collisionPoint = planeIntersectionPoint;
				}
			}

			// If there is no collision already, then we have to do the sweep test for the
			// vertices and the edges of the triangle.
			if (!foundCollision) {

				double velocitySquaredLength = Vector3f.dot(packet.velocity, packet.velocity);
				double a, b, c;
				double newT;
				Double quadSolution;
				Vector3f pMinusBasePoint;

				// SWEEPING OVER THE POINTS:
				// For each point we have to get a, b, and c and then get the smallest solution
				// to the quadratic equation of these values.
				a = velocitySquaredLength;

				// P1
				b = 2.0 * (Vector3f.dot(packet.velocity, Vector3f.sub(packet.basePoint, triangle.a, null)));
				pMinusBasePoint = Vector3f.sub(triangle.a, packet.basePoint, null);
				c = Vector3f.dot(pMinusBasePoint, pMinusBasePoint) - 1.0;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					t = quadSolution.floatValue();
					foundCollision = true;
					collisionPoint = triangle.a;
				}

				// P2
				b = 2.0 * (Vector3f.dot(packet.velocity, Vector3f.sub(packet.basePoint, triangle.b, null)));
				pMinusBasePoint = Vector3f.sub(triangle.b, packet.basePoint, null);
				c = Vector3f.dot(pMinusBasePoint, pMinusBasePoint) - 1.0;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					t = quadSolution.floatValue();
					foundCollision = true;
					collisionPoint = triangle.b;
				}

				// P3
				b = 2.0 * (Vector3f.dot(packet.velocity, Vector3f.sub(packet.basePoint, triangle.c, null)));
				pMinusBasePoint = Vector3f.sub(triangle.c, packet.basePoint, null);
				c = Vector3f.dot(pMinusBasePoint, pMinusBasePoint) - 1.0;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					t = quadSolution.floatValue();
					foundCollision = true;
					collisionPoint = triangle.c;
				}

				// SWEEPING OVER THE EDGES:
				Vector3f edge;
				Vector3f baseToVertex;
				float edgeSquaredLength;
				float edgeDotVelocity;
				float edgeDotBaseToVertex;

				// A -> B
				edge = Vector3f.sub(triangle.b, triangle.a, null);
				baseToVertex = Vector3f.sub(triangle.a, packet.basePoint, null);
				edgeSquaredLength = Vector3f.dot(edge, edge);
				edgeDotVelocity = Vector3f.dot(edge, packet.velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2 * Vector3f.dot(packet.velocity, baseToVertex))
						- 2.0 * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ edgeDotBaseToVertex * edgeDotBaseToVertex;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					double f = (edgeDotVelocity * quadSolution.doubleValue() - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0 && f <= 1.0) {
						t = quadSolution.floatValue();
						foundCollision = true;
						collisionPoint = Vector3f.add(triangle.a, scaleVector(edge, (float) f), null);
					}
				}

				// B -> C
				edge = Vector3f.sub(triangle.c, triangle.b, null);
				baseToVertex = Vector3f.sub(triangle.b, packet.basePoint, null);
				edgeSquaredLength = Vector3f.dot(edge, edge);
				edgeDotVelocity = Vector3f.dot(edge, packet.velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2 * Vector3f.dot(packet.velocity, baseToVertex))
						- 2.0 * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ edgeDotBaseToVertex * edgeDotBaseToVertex;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					double f = (edgeDotVelocity * quadSolution.doubleValue() - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0 && f <= 1.0) {
						t = quadSolution.floatValue();
						foundCollision = true;
						collisionPoint = Vector3f.add(triangle.b, scaleVector(edge, (float) f), null);
					}
				}

				// C -> A
				edge = Vector3f.sub(triangle.a, triangle.c, null);
				baseToVertex = Vector3f.sub(triangle.c, packet.basePoint, null);
				edgeSquaredLength = Vector3f.dot(edge, edge);
				edgeDotVelocity = Vector3f.dot(edge, packet.velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2 * Vector3f.dot(packet.velocity, baseToVertex))
						- 2.0 * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1 - Vector3f.dot(baseToVertex, baseToVertex))
						+ edgeDotBaseToVertex * edgeDotBaseToVertex;
				quadSolution = solveGetSmallestQuadraticSolution(a, b, c, t);
				if (quadSolution != null) {
					double f = (edgeDotVelocity * quadSolution.doubleValue() - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0 && f <= 1.0) {
						t = quadSolution.floatValue();
						foundCollision = true;
						collisionPoint = Vector3f.add(triangle.c, scaleVector(edge, (float) f), null);
					}
				}
			}

			// If a collision was found then set the collision response variables to be
			// their proper values.
			if (foundCollision) {
				float distToCollision = t * packet.velocity.length();
				if (!packet.foundCollision || distToCollision < packet.nearestDistance) {
					packet.nearestDistance = distToCollision;
					packet.foundCollision = true;
					packet.intersectionPoint = collisionPoint;
				}
			}
		}
	}

	public static Double solveGetSmallestQuadraticSolution(double a, double b, double c, double maxSolution) {
		double descriminent = b * b - 4 * a * c;
		if (descriminent < 0)
			return null;
		double sqrtDescriminet = Math.sqrt(descriminent);
		double r1 = (-b + sqrtDescriminet) / (2 * a);
		double r2 = (-b - sqrtDescriminet) / (2 * a);

		if (r1 > r2) {
			double temp = r2;
			r2 = r1;
			r1 = temp;
		}

		if (r1 > 0 && r1 < maxSolution)
			return new Double(r1);
		else if (r2 > 0 && r2 < maxSolution)
			return new Double(r2);
		else
			return null;

	}

	public static Vector3f getESpaceVector(Vector3f R3Vector, Vector3f eRadius) {
		return new Vector3f(R3Vector.getX() / eRadius.getX(), R3Vector.getY() / eRadius.getY(),
				R3Vector.getZ() / eRadius.getZ());
	}
	
	public static Vector3f getRSpaceVector(Vector3f E3Vector, Vector3f eRadius) {
		return new Vector3f(E3Vector.getX() * eRadius.getX(), E3Vector.getY() * eRadius.getY(),
				E3Vector.getZ() * eRadius.getZ());
	}

	public static Vector3f scaleVector(Vector3f toScale, float scaler) {
		return new Vector3f(toScale.getX() * scaler, toScale.getY() * scaler, toScale.getZ() * scaler);
	}

	public boolean foundCollision() {
		return foundCollision;
	}

}
