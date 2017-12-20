package engine.guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;
import engine.util.Calculator;

public class GuiRenderer {
	private static List<Gui> guis  = new ArrayList<Gui>();
	private static GuiShader shader;
	private static RawModel quad;

	/* Constructor: Initializes the variables */
	public static void initalize() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		ModelManager.loadModel("quad", positions);

		quad = ModelManager.getModel("quad");
		shader = new GuiShader();
	}

	/* Renders all of the GUIs */
	public static void render() {

		shader.start();

		// Enables the settings required to render GUIs
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL30.glBindVertexArray(quad.getVaoID());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);

		// Renders each individual GUI
		for (Gui gui : guis) {
			// Loads the texture to OpenGL texture 0
			if (!gui.isColored()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			}

			// Applies transformations to get it in the correct position
			Matrix4f matrix = Calculator.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			shader.loadColorData(gui.isColored(), gui.getColor());

			// Finally renders it to the screen
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}

		// Disables all of the settings
		GL30.glBindVertexArray(0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);

		shader.stop();
	}

	/* Add a texture to the render list */
	public static void addGui(Gui g) {
		guis.add(g);
	}

	/* Remove a texture from the render list */
	public static void removeGui(Gui g) {
		guis.remove(g);
	}

	/* Destroy the shader */
	public static void cleanUp() {
		shader.cleanUp();
	}
}
