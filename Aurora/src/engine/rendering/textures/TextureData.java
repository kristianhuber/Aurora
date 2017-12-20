package engine.rendering.textures;

import java.nio.ByteBuffer;

/**
 * @Description: Holds basic information about a texture
 * 
 */

public class TextureData {

	private ByteBuffer buffer;
	private int width, height;

	/* Constructor Method */
	public TextureData(ByteBuffer buffer, int width, int height) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	/* Gets the width of the texture */
	public int getWidth() {
		return width;
	}

	/* Gets the height of the texture */
	public int getHeight() {
		return height;
	}

	/* Gets the byte buffer */
	public ByteBuffer getBuffer() {
		return buffer;
	}

}