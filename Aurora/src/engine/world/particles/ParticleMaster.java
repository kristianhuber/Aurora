package engine.world.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description: Keeps track of all the particle systems
 * 
 */

public class ParticleMaster {
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer renderer;

	/* Initialization */
	public static void initialize() {
		ParticleMaster.renderer = new ParticleRenderer();
	}

	/* Updates all of the particles */
	public static void update() {

		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();

		// Iterates through all of the particle types
		while (mapIterator.hasNext()) {

			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();

			// Iterates through all of the particles instances
			while (iterator.hasNext()) {
				Particle p = iterator.next();

				// Updates the particle and or kills it
				boolean stillAlive = p.update();
				if (!stillAlive) {
					iterator.remove();
					if (list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}

			// Rearranges the order of particles
			ParticleMaster.sortHighToLow(list);
		}
	}

	/* Renders all of the particles */
	public static void renderParticles() {
		ParticleMaster.renderer.render(particles);
	}

	/* Adds a particle to the lists */
	public static void addParticle(Particle particle) {

		// Checks to see if the texture already exists
		List<Particle> list = particles.get(particle.getTexture());
		if (list == null) {
			list = new ArrayList<Particle>();
			ParticleMaster.particles.put(particle.getTexture(), list);
		}

		// Adds the particle to the correct list
		list.add(particle);
	}

	/* Sorts the */
	private static void sortHighToLow(List<Particle> list) {

		// Loops through each particle
		for (int i = 1; i < list.size(); i++) {

			Particle item = list.get(i);

			// If out of order, fix it
			if (item.getDistance() > list.get(i - 1).getDistance()) {
				ParticleMaster.sortUpHighToLow(list, i);
			}
		}
	}

	/* Sorts the particle list up to a certain number */
	private static void sortUpHighToLow(List<Particle> list, int i) {

		Particle item = list.get(i);

		// Sees where to put the particle
		int attemptPos = i - 1;
		while (attemptPos != 0 && list.get(attemptPos - 1).getDistance() < item.getDistance()) {
			attemptPos--;
		}

		// Moves the position of the particle
		list.remove(i);
		list.add(attemptPos, item);
	}

	/* Cleans up memory */
	public static void cleanUp() {
		ParticleMaster.renderer.cleanUp();
	}
}
