package engine.rendering.models;

import engine.rendering.textures.ModelTexture;
import engine.rendering.textures.TextureManager;

/**
 * @Description: Holds a texture and model for an entity
 * 
 */

public class TexturedModel {
	
	private ModelTexture texture;
	private RawModel rawModel;
	private String ID;
	private int hash;
	
	/* Constructor Method */
	public TexturedModel(String model, String texture) {
		this.texture = TextureManager.getTexture(texture);
		this.rawModel = ModelManager.getModel(model);
		this.ID = model + "/" + texture;
		this.hash = this.texture.getID() * 1000000 + this.rawModel.getVaoID();
	}

	/* Simple Constructor */
	public TexturedModel(String ID) {
		this(ID, ID);
	}

	/* Returns the texture for the model */
	public ModelTexture getTexture() {
		return texture;
	}

	/* Returns the model */
	public RawModel getRawModel() {
		return rawModel;
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return this.getID();
	}

	@Override
	public boolean equals(Object arg0) {
		TexturedModel tm = (TexturedModel) arg0;
		return rawModel.getVaoID() == tm.getRawModel().getVaoID() && texture.getID() == tm.getTexture().getID();
	}
}
