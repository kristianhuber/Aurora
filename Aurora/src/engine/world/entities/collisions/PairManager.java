package engine.world.entities.collisions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.world.entities.Entity;

public class PairManager {

	private List<EntityPair> x_collisions, y_collisions, z_collisions;

	public PairManager() {
		x_collisions = new ArrayList<EntityPair>();
		y_collisions = new ArrayList<EntityPair>();
		z_collisions = new ArrayList<EntityPair>();
	}

	public Entity[] getCollidingEntities(Entity e) {
		List<Entity> xcollisionList = new ArrayList<Entity>();
		for (int i = 0; i < x_collisions.size(); i++) {
			System.out.println(x_collisions.get(i).toString());
			if (x_collisions.get(i).getEntity1() == e && !xcollisionList.contains(x_collisions.get(i).getEntity2()))
				xcollisionList.add(x_collisions.get(i).getEntity2());
			if (x_collisions.get(i).getEntity2() == e && !xcollisionList.contains(x_collisions.get(i).getEntity2()))
				xcollisionList.add(x_collisions.get(i).getEntity1());
		}
		if (xcollisionList.size() > 0) {
			List<Entity> ycollisionList = new ArrayList<Entity>();
			for (int i = 0; i < y_collisions.size(); i++) {
				System.out.println(y_collisions.get(i).toString());
				if (y_collisions.get(i).getEntity1() == e && xcollisionList.contains(y_collisions.get(i).getEntity2())
						&& !ycollisionList.contains(y_collisions.get(i).getEntity2()))
					ycollisionList.add(y_collisions.get(i).getEntity2());
				if (y_collisions.get(i).getEntity2() == e && xcollisionList.contains(y_collisions.get(i).getEntity1())
						&& !ycollisionList.contains(y_collisions.get(i).getEntity1()))
					ycollisionList.add(y_collisions.get(i).getEntity1());
			}
			if (ycollisionList.size() > 0) {
				List<Entity> zcollisionList = new ArrayList<Entity>();
				for (int i = 0; i < z_collisions.size(); i++) {
					System.out.println(z_collisions.get(i).toString());
					if (z_collisions.get(i).getEntity1() == e
							&& ycollisionList.contains(z_collisions.get(i).getEntity2())
							&& !zcollisionList.contains(z_collisions.get(i).getEntity2()))
						zcollisionList.add(z_collisions.get(i).getEntity2());
					if (z_collisions.get(i).getEntity2() == e
							&& ycollisionList.contains(z_collisions.get(i).getEntity1())
							&& !zcollisionList.contains(z_collisions.get(i).getEntity1()))
						zcollisionList.add(z_collisions.get(i).getEntity1());
				}
				Entity[] toReturn = new Entity[zcollisionList.size()];
				for (int i = 0; i < toReturn.length; i++)
					toReturn[i] = zcollisionList.get(i);
				return toReturn;
			}
		}
		return new Entity[0];
	}

	public void deleteAllWith(Entity e) {
		for (int i = 0; i < x_collisions.size(); i++)
			if (x_collisions.get(i).contains(e)) {
				x_collisions.remove(i);
				i--;
			}
		for (int i = 0; i < y_collisions.size(); i++)
			if (y_collisions.get(i).contains(e)) {
				y_collisions.remove(i);
				i--;
			}
		for (int i = 0; i < z_collisions.size(); i++)
			if (z_collisions.get(i).contains(e)) {
				z_collisions.remove(i);
				i--;
			}
	}

	public void xAddCollision(Entity e1, Entity e2) {
		x_collisions.add(new EntityPair(e1, e2));
	}

	public void xDeleteCollision(Entity e1, Entity e2) {
		EntityPair pair = new EntityPair(e1, e2);
		for (int i = 0; i < x_collisions.size(); i++)
			if (pair.equals(x_collisions.get(i))) {
				x_collisions.remove(i);
				return;
			}
	}

	public void yAddCollision(Entity e1, Entity e2) {
		y_collisions.add(new EntityPair(e1, e2));
	}

	public void yDeleteCollision(Entity e1, Entity e2) {
		EntityPair pair = new EntityPair(e1, e2);
		for (int i = 0; i < y_collisions.size(); i++)
			if (pair.equals(y_collisions.get(i))) {
				y_collisions.remove(i);
				return;
			}
	}

	public void zAddCollision(Entity e1, Entity e2) {
		z_collisions.add(new EntityPair(e1, e2));
	}

	public void zDeleteCollision(Entity e1, Entity e2) {
		EntityPair pair = new EntityPair(e1, e2);
		for (int i = 0; i < z_collisions.size(); i++)
			if (pair.equals(z_collisions.get(i))) {
				z_collisions.remove(i);
				return;
			}
	}

	public String toString() {
		String toReturn = "";

		toReturn += "X Collisions: ";
		for (EntityPair ep : x_collisions)
			toReturn += ep.toString() + "; ";
		toReturn += "\nY Collisions: ";
		for (EntityPair ep : y_collisions)
			toReturn += ep.toString() + "; ";
		toReturn += "\nZ Collisions: ";
		for (EntityPair ep : z_collisions)
			toReturn += ep.toString() + "; ";
		return toReturn;
	}

	private class EntityPair {

		private Entity entity1, entity2;

		public EntityPair(Entity e1, Entity e2) {
			this.entity1 = e1;
			this.entity2 = e2;
		}

		public Entity getEntity1() {
			return entity1;
		}

		public void setEntity1(Entity entity1) {
			this.entity1 = entity1;
		}

		public Entity getEntity2() {
			return entity2;
		}

		public void setEntity2(Entity entity2) {
			this.entity2 = entity2;
		}

		public boolean equals(EntityPair e) {
			return (entity1 == e.getEntity1() && entity2 == e.getEntity2())
					|| (entity1 == e.getEntity2() && entity2 == e.getEntity1());
		}

		public boolean contains(Entity e) {
			return e == entity1 || e == entity2;
		}

		public String toString() {
			return "(" + entity1.toString() + "," + entity2.toString() + ")";
		}
	}

}
