package engine.rendering.textures;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

/**
 * @Description: Holds all of the textures in the game
 * 
 */

public class TextureManager {
	private static HashMap<String, TerrainTexturePack> terrainTextures = new HashMap<String, TerrainTexturePack>();
	private static HashMap<String, ModelTexture> textures = new HashMap<String, ModelTexture>();
	private static HashMap<String, Integer> cubeMaps = new HashMap<String, Integer>();

	/* Loads a texture given the file name and directory its in */
	public static void loadTexture(String ID, String path) {

		// Tries to read the file
		try {

			// Loads the texture
			Texture texture = TextureLoader.getTexture("PNG",
					TextureManager.class.getResourceAsStream("/aurora/assets/" + path + "/" + ID + ".png"));

			// Generates different forms of the texture
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

			// Enables anisotropic filtering, helps with oblique angles
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				// Sets the amount of filtering
				float amount = Math.min(4f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			} else {
				// If the computer can't handle it, let the user know
				System.err.println("[Console]: Anisotropic filtering not supported");
			}

			// Loads it into the engine
			TextureManager.textures.put(ID, new ModelTexture(texture.getTextureID()));

		} catch (Exception e) {
			// If it can't find the texture, write what it cannot find
			System.err.println("[Console]: Could not find Texture for '" + ID + "'");
		}
	}

	/* Simple Constructor Method */
	public static void loadTexture(String ID) {
		TextureManager.loadTexture(ID, "textures");
	}

	/* Creates a terrain texture pack */
	public static void createTerrainTexturePack(String ID, String background, String r, String g, String b) {

		// Load each of the textures
		int backID = TextureManager.getTexture(background).getID();
		int rID = TextureManager.getTexture(r).getID();
		int gID = TextureManager.getTexture(g).getID();
		int bID = TextureManager.getTexture(b).getID();

		// Loads it into the engine
		TextureManager.terrainTextures.put(ID, new TerrainTexturePack(backID, rID, gID, bID));
	}

	/* Loads a cube map */
	public static void loadCubeMap(String ID) {

		// Initialize the texture files
		String[] textureFiles = new String[6];
		textureFiles[0] = ID + "_right";
		textureFiles[1] = ID + "_left";
		textureFiles[2] = ID + "_up";
		textureFiles[3] = ID + "_down";
		textureFiles[4] = ID + "_back";
		textureFiles[5] = ID + "_front";

		// Creating a new cube map texture
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		/* Adding each texture to the cube map */
		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
					data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		// Settings for the cube map
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Loads it to the engine
		TextureManager.cubeMaps.put(ID, texID);
	}

	public static void loadEntityTexture(String ID) {
		// Tries to read the file
		try {

			// Loads the texture
			Texture texture = TextureLoader.getTexture("PNG",
					new FileInputStream(new File("textured models\\" + ID + "\\texture.png")));

			// Generates different forms of the texture
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

			// Enables anisotropic filtering, helps with oblique angles
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				// Sets the amount of filtering
				float amount = Math.min(4f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			} else {
				// If the computer can't handle it, let the user know
				System.err.println("[Console]: Anisotropic filtering not supported");
			}

			// Loads it into the engine
			TextureManager.textures.put(ID, new ModelTexture(texture.getTextureID()));

		} catch (Exception e) {
			// If it can't find the texture, write what it cannot find
			System.err.println("[Console]: Could not find Texture for '" + ID + "'");
		}
	}

	/* Loads a texture file and stores it in a texture data class */
	private static TextureData decodeTextureFile(String fileName) {
		ByteBuffer buffer = null;
		int height = 0;
		int width = 0;

		try {
			// Store the data in variables
			PNGDecoder decoder = new PNGDecoder(
					TextureManager.class.getResourceAsStream("/aurora/assets/textures/" + fileName + ".png"));
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();

		} catch (Exception e) {
			// Error handling
			System.err.println("[Console]: Could not load texture '" + fileName + "'");
		}
		return new TextureData(buffer, width, height);
	}

	/* Gets a terrain texture pack */
	public static TerrainTexturePack getTerrainTexturePack(String ID) {
		return TextureManager.terrainTextures.get(ID);
	}

	/* Gets a cube map */
	public static int getCubeMap(String ID) {
		return TextureManager.cubeMaps.get(ID);
	}

	/* Gets a texture */
	public static ModelTexture getTexture(String ID) {
		return TextureManager.textures.get(ID);
	}

	/* Cleans up the memory by removing textures */
	public static void cleanUp() {
		for (ModelTexture m : textures.values()) {
			GL11.glDeleteTextures(m.getID());
		}
		for (Integer i : cubeMaps.values()) {
			GL11.glDeleteTextures(i);
		}
	}
}
