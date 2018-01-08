package engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.models.TexturedModel;
import engine.util.Calculator;
import engine.util.Engine;
import engine.world.chunks.Chunk;
import engine.world.chunks.ChunkData;
import engine.world.chunks.ChunkLoader;
import engine.world.entities.Camera;
import engine.world.entities.Entity;
import engine.world.entities.Light;
import engine.world.entities.collisions.EndpointsArray;
import engine.world.terrain.Terrain;
import engine.world.water.WaterTile;

public class World {

	public static final float SUN_DISTANCE = 10000;
	public static final int WORLD_SIZE = 32;

	public static final int RENDER_DISTANCE = 6;

	private ChunkLoader loader;

	private Map<TexturedModel, List<Entity>> entities;
	private EndpointsArray prelimCollisionManager;
	private Map<Integer, Chunk> terrainMap;
	private List<Light> lights;
	private float seaLevel;
	private float time;
	private Light sun;
	private int seed;
	private Vector3f skyColor = new Vector3f(154 / 255F, 160 / 255F, 181 / 255F);
	private boolean testWorld;

	public World(Engine engine, boolean testWorld) {

		entities = new HashMap<TexturedModel, List<Entity>>();
		prelimCollisionManager = new EndpointsArray();
		terrainMap = new HashMap<Integer, Chunk>();
		lights = new ArrayList<Light>();

		loader = new ChunkLoader(engine, this);
		loader.start();

		this.testWorld = testWorld;

		seed = new Random().nextInt(10000000);

		Entity l = new Entity(this, "stall", World.WORLD_SIZE * Terrain.SIZE / 2 + 20,
				World.WORLD_SIZE * Terrain.SIZE / 2 + 10);
		this.addEntity(l);

		Entity a = new Entity(this, "betterpine", World.WORLD_SIZE * Terrain.SIZE / 2,
				World.WORLD_SIZE * Terrain.SIZE / 2 + 10);
		a.setScale(5);
		this.addEntity(a);

		time = 19.5f;

		if (testWorld)
			seaLevel = -999;
		else
			seaLevel = 155;

		sun = new Light(new Vector3f(0, World.SUN_DISTANCE, 0), new Vector3f(1.15F, 1.15F, 1.15F));
		this.addLight(sun);
	}

	public float getSeaLevel() {
		return seaLevel;
	}

	public void update() {
		Engine.getCamera().move();

		// Updating the collision determiner arrays.
		prelimCollisionManager.updateArrays();

		time += Engine.getDelta() / 20;
		if (time >= 24) {
			time = 0;
		}

		float y = (float) (SUN_DISTANCE * Math.sin(Math.PI * (time - 2.25) / 24));
		float z = (float) (SUN_DISTANCE * Math.cos(Math.PI * (time - 2.25) / 24));
		sun.setPosition(0, y, z);
	}

	public float getTerrainHeightAt(float x, float z) {
		Vector2f calc = Calculator.terrainCoords(x, z);
		Chunk c = this.terrainMap.get((int) (calc.x) * 1000 + (int) (calc.y));
		if (c == null) {
			return 0;
		}
		return c.getTerrain().getHeightOfTerrain(x, z);
	}

	public float getTerrainHeightAt(Vector3f position) {
		return this.getTerrainHeightAt(position.x, position.z);
	}

	public List<Terrain> getRenderedTerrains() {
		List<Terrain> toRender = new ArrayList<Terrain>();

		Camera cam = Engine.getCamera();
		Vector2f pos = Calculator.terrainCoords(cam.getPosition().x, cam.getPosition().z);
		float rot = (float) Math.toRadians(180 - (cam.getRotation().y % 360));

		for (int i = 0; i < RENDER_DISTANCE; i++) {
			int x = (int) (pos.x + i * (float) Math.sin(rot));
			int y = (int) (pos.y + i * (float) Math.cos(rot));
			for (int a = -i - 1; a < i + 2; a++) {
				for (int b = -i - 1; b < i + 2; b++) {
					int x2 = x + a;
					int y2 = y + b;
					int dist = a * a + b * b;
					if (x2 >= 0 && y2 >= 0 && x2 < WORLD_SIZE && y2 < WORLD_SIZE) {
						Chunk c = this.terrainMap.get(x2 * 1000 + y2);
						if (c == null) {
							ChunkData data = new ChunkData(dist, seed, x2, y2, seaLevel);
							loader.createChunk(data);
						} else {
							Terrain t = c.getTerrain();
							if (!toRender.contains(t) && t != null) {
								toRender.add(t);
							}
						}
					}
				}
			}
		}

		return toRender;
	}

	public List<WaterTile> getRenderedWaters() {
		List<WaterTile> toRender = new ArrayList<WaterTile>();

		if (testWorld)
			return toRender;

		Camera cam = Engine.getCamera();
		Vector2f pos = Calculator.terrainCoords(cam.getPosition().x, cam.getPosition().z);
		float rot = (float) Math.toRadians(180 - (cam.getRotation().y % 360));

		for (int i = 0; i < RENDER_DISTANCE; i++) {
			int x = (int) (pos.x + i * (float) Math.sin(rot));
			int y = (int) (pos.y + i * (float) Math.cos(rot));
			for (int a = -i - 1; a < i + 2; a++) {
				for (int b = -i - 1; b < i + 2; b++) {
					int x2 = x + a;
					int y2 = y + b;
					int dist = a * a + b * b;
					if (x2 >= 0 && y2 >= 0 && x2 < WORLD_SIZE && y2 < WORLD_SIZE) {
						Chunk c = this.terrainMap.get(x2 * 1000 + y2);
						if (c == null) {
							ChunkData data = new ChunkData(dist, seed, x2, y2, seaLevel);
							loader.createChunk(data);
						} else {
							WaterTile t = c.getWater();
							if (!toRender.contains(t) && t != null) {
								toRender.add(t);
							}
						}
					}
				}
			}
		}

		return toRender;
	}

	public List<Light> getLights() {
		return lights;
	}

	public Map<TexturedModel, List<Entity>> getEntities() {
		return entities;
	}

	public void decorate(int chunkX, int chunkZ) {
		for (int i = 0; i < 10; i++) {
			int x = (int) (Math.random() * Terrain.SIZE + chunkX * Terrain.SIZE);
			int z = (int) (Math.random() * Terrain.SIZE + chunkZ * Terrain.SIZE);
			float y = this.getTerrainHeightAt(x, z);
			if (y > seaLevel + 5) {
				Entity e = new Entity(this, "betterpine", new Vector3f(x, y, z));
				e.setScale(7.5f);
				e.setSelection(false);
				this.addEntity(e);
			}
		}
	}

	public void addChunk(Chunk c) {
		int x = c.getX();
		int z = c.getZ();
		terrainMap.put(x * 1000 + z, c);
	}

	public void addLight(Light light) {
		lights.add(light);
	}

	public void addEntity(Entity entity) {
		synchronized (entities) {
			TexturedModel entityModel = entity.getModel();
			for (TexturedModel tm : entities.keySet())
				if (tm.equals(entityModel)) {
					entities.get(tm).add(entity);
					prelimCollisionManager.addAABB(entity.getBoundingBox());
					return;
				}
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
			prelimCollisionManager.addAABB(entity.getBoundingBox());
		}
	}

	public EndpointsArray getCollisionManager() {
		return prelimCollisionManager;
	}

	public Vector3f getSkyColor() {
		return skyColor;
	}

	public float getWorldTime() {
		return time;
	}

	public int getSeed() {
		return seed;
	}
}
