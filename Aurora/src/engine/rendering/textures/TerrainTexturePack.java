package engine.rendering.textures;

/**
 * @Description: Holds several textures for the terrain shader to use
 * 
 */

public class TerrainTexturePack {

	private int backgroundTexture, rTexture, gTexture, bTexture;

	/* Constructor Method */
	public TerrainTexturePack(int back, int r, int g, int b) {
		this.backgroundTexture = back;
		this.rTexture = r;
		this.gTexture = g;
		this.bTexture = b;
	}

	/* Returns the default texture */
	public int getBackgroundTexture() {
		return backgroundTexture;
	}

	/* Returns the red component texture */
	public int getRTexture() {
		return rTexture;
	}

	/* Returns the green component texture */
	public int getGTexture() {
		return gTexture;
	}

	/* Returns the blue component texture */
	public int getBTexture() {
		return bTexture;
	}
}
