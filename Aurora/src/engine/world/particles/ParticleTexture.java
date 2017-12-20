package engine.world.particles;

import engine.rendering.textures.TextureManager;

/**
 * @Description: Holds data for the texture of a particle
 * 
 * */

public class ParticleTexture {
	
	private int numberOfRows, textureID;
	private boolean additive;
	
	/* Constructor Method */
	public ParticleTexture(String textureID, int numberOfRows, boolean additive){
		this.textureID = TextureManager.getTexture(textureID).getID();
		this.numberOfRows = numberOfRows;
		this.additive = additive;
	}

	/* Returns if the particle texture is additive (brighter) */
	public boolean isAdditive(){
		return additive;
	}
	
	/* Returns the texture ID */
	public int getTextureID() {
		return textureID;
	}

	/* Returns the number of rows */
	public int getNumberOfRows() {
		return numberOfRows;
	}
}
